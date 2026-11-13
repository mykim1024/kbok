package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.parse.impl.PitcherStatParser;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PitcherStatCrawler {
    
    private static final String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36";
    private static final String F_SEASON = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSeason$ddlSeason";
    private static final String F_SERIES = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSeries$ddlSeries";
    private static final String F_TEAM = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlTeam$ddlTeam";
    private static final String F_SITUATION = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSituation$ddlSituation";
    private static final String F_SITUTATION_DETEAIL = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSituationDetail$ddlSituationDetail";
    private static final String F_PAGE = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$hfPage";
    private static final String F_PAGE_ORDER_COL = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$hfOrderByCol";
    private static final String F_PAGE_ORDER = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$hfOrderBy";
    private static final String F_SCRIPT_MANAGER = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$smData";
    private static final String PAGER_PREFIX =
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ucPager$btnNo";
    // 페이지 버튼이 부분 새로고침(비동기) 대상이 아닌 페이지.
    // 비동기로 요청하면 서버가 조회는 하지만 표 HTML을 응답에 넣지 않아 0건이 된다.
    // 이 페이지들은 일반 폼 전송으로 요청해 페이지 전체를 받는다.
    private static final Set<String> FULL_POSTBACK_PAGES = Set.of("Basic1.aspx", "Basic2.aspx", "Detail1.aspx");
    // 브라우저처럼 "기본기록 페이지에서 넘어왔다"는 정보를 붙이기 위한 값
    private static final String REFERER =
        "https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx";

    private record PostbackResult(Document doc, String viewState, String viewStateGen, String eventValidation, Map<String, String> cookies){}

    /**
     * 새 세션으로 수집한다. (세션을 이어 쓸 필요가 없는 페이지용)
     */
    public <T> Map<Long, T> crawl(String url, String season, String teamCode, PitcherStatParser<T> parser) throws IOException, InterruptedException{
        return crawl(url, season, teamCode, parser, new HashMap<>());
    }

    /**
     * 넘겨받은 세션(쿠키)을 이어 쓰며 수집한다.
     * KBO 세부기록 페이지는 기본기록 조회(POST)를 거친 세션이어야 열린다.
     * 세션 없이 바로 요청하면 서버가 에러 페이지로 리다이렉트해 0건이 된다.
     * 크롤러는 싱글톤 빈이라 세션을 필드에 두지 않고, 호출하는 쪽에서 만들어 넘긴다.
     */
    public <T> Map<Long, T> crawl(String url, String season, String teamCode, PitcherStatParser<T> parser,
            Map<String, String> session) throws IOException, InterruptedException{
        PostbackResult initResult = crawlInit(url, session);
        
        PostbackResult pageResult = crawlByPage(url, teamCode, season, "1", initResult);
       
        Map<Long, T> parseResult = new HashMap<>();
        parseResult.putAll(parse(pageResult.doc, parser));

        if (parseResult.isEmpty()) {
            // 0건이면 응답 전체를 파일로 남겨 원인을 확인할 수 있게 한다
            String name = url.substring(url.lastIndexOf('/') + 1);
            Path dump = Path.of("logs", "empty-" + teamCode + "-" + name + ".html");
            Files.writeString(dump, pageResult.doc().outerHtml());
            log.warn("[{}] {} 0건 - 응답 전체 저장: {}", teamCode, name, dump);
        }

        int maxPage = extractMaxPage(pageResult.doc());
        log.info("[{}] {} 총 페이지 수 : {}", teamCode, url.substring(url.lastIndexOf('/') + 1), maxPage);
        for(int page = 2; page <= maxPage; page++){
            pageResult = crawlByPage(url, teamCode, season, String.valueOf(page), pageResult);
            parseResult.putAll(parse(pageResult.doc, parser));
        }

        return parseResult;
    }

    private PostbackResult crawlInit(String url, Map<String, String> session) throws IOException{
        // 이 요청에 어떤 세션 쿠키를 들고 가는지 확인용
        log.info("{} 요청 세션 쿠키: {}", url.substring(url.lastIndexOf('/') + 1), session.keySet());

        Connection conn = Jsoup.connect(url)
            .userAgent(UA)
            .cookies(session)
            .method(Method.GET)
            .timeout(50000);

        // 세션을 이어받는 요청(세부기록)만 브라우저처럼 Referer를 붙인다.
        // 새 세션 요청(기본기록1·2)은 원래 잘 되던 요청과 똑같이 둔다.
        if (!session.isEmpty()) {
            conn.header("Referer", REFERER)
                .header("Accept", "text/html")
                .header("Accept-Language", "ko-KR,ko;q=0.9");
        }

        Response initial = conn.execute();

        // 에러 페이지로 넘겨졌으면 여기서 바로 알 수 있게 남긴다
        if (initial.url().getPath().startsWith("/Error/")) {
            log.warn("{} 접근 거부 - 에러 페이지로 이동됨 (세션 필요 여부 확인)",
                url.substring(url.lastIndexOf('/') + 1));
        }

        session.putAll(initial.cookies());

        Document initialDoc = initial.parse();
        String viewState = initialDoc.select("#__VIEWSTATE").val();
        String viewStateGen = initialDoc.select("#__VIEWSTATEGENERATOR").val();
        String eventValidation = initialDoc.select("#__EVENTVALIDATION").val();
        
        return new PostbackResult(initialDoc, viewState, viewStateGen, eventValidation, session);
    }

    private PostbackResult crawlByPage(String url, String teamCode, String season, String page, PostbackResult postbackResult) throws IOException{
        String pageName = url.substring(url.lastIndexOf('/') + 1);
        if (FULL_POSTBACK_PAGES.contains(pageName)) {
            return crawlByPageFull(url, teamCode, season, page, postbackResult);
        }

        String pageEvnet = PAGER_PREFIX + page;
       
        Response response = Jsoup.connect(url)
            .userAgent(UA)
            .cookies(postbackResult.cookies)
            .header("Referer", url)
            .header("Origin", "https://www.koreabaseball.com")
            .header("Accept", "*/*")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("X-MicrosoftAjax", "Delta=true")
            .header("X-Requested-With", "XMLHttpRequest")
            .data(F_SCRIPT_MANAGER, pageEvnet)
            .data("__EVENTTARGET", pageEvnet)
            .data("__EVENTARGUMENT", "")
            .data("__LASTFOCUS", "")
            .data("__VIEWSTATE", postbackResult.viewState)
            .data("__VIEWSTATEGENERATOR", postbackResult.viewStateGen)
            .data("__EVENTVALIDATION", postbackResult.eventValidation)
            .data(F_PAGE, "1")
            .data(F_PAGE_ORDER, "ASC")
            .data(F_PAGE_ORDER_COL, "ERA_RT")
            .data(F_SEASON, season)
            .data(F_SERIES, "0")
            .data(F_TEAM, teamCode)
            .data(F_SITUATION, "")
            .data(F_SITUTATION_DETEAIL, "")
            .data("__ASYNCPOST", "true")
            .method(Method.POST)
            .timeout(5000)
            .execute();

        // POST 응답에서 쿠키가 새로 오면 세션에 반영한다
        postbackResult.cookies.putAll(response.cookies());

        String body = response.body();

        Document doc = Jsoup.parse(body);

        return new PostbackResult(doc, 
                extractDeltaValue(body, "__VIEWSTATE"), 
                extractDeltaValue(body, "__VIEWSTATEGENERATOR"), 
                extractDeltaValue(body, "__EVENTVALIDATION"),
                postbackResult.cookies);
    }

    /**
     * 일반 폼 전송(전체 페이지 새로고침)으로 요청한다.
     * 비동기 요청 표시(X-MicrosoftAjax, __ASYNCPOST, ScriptManager 값)를 빼면
     * 서버가 표가 포함된 페이지 전체 HTML을 돌려준다.
     * 응답이 일반 HTML이라 다음 요청에 쓸 값도 hidden input에서 읽는다.
     */
    private PostbackResult crawlByPageFull(String url, String teamCode, String season, String page, PostbackResult postbackResult) throws IOException{
        String pageEvent = PAGER_PREFIX + page;

        Response response = Jsoup.connect(url)
            .userAgent(UA)
            .cookies(postbackResult.cookies)
            .header("Referer", url)
            .header("Origin", "https://www.koreabaseball.com")
            .header("Accept", "text/html")
            .header("Accept-Language", "ko-KR,ko;q=0.9")
            .data("__EVENTTARGET", pageEvent)
            .data("__EVENTARGUMENT", "")
            .data("__LASTFOCUS", "")
            .data("__VIEWSTATE", postbackResult.viewState)
            .data("__VIEWSTATEGENERATOR", postbackResult.viewStateGen)
            .data("__EVENTVALIDATION", postbackResult.eventValidation)
            .data(F_PAGE, "1")
            .data(F_PAGE_ORDER, "ASC")
            .data(F_PAGE_ORDER_COL, "ERA_RT")
            .data(F_SEASON, season)
            .data(F_SERIES, "0")
            .data(F_TEAM, teamCode)
            .data(F_SITUATION, "")
            .data(F_SITUTATION_DETEAIL, "")
            .method(Method.POST)
            .timeout(10000)
            .execute();

        postbackResult.cookies.putAll(response.cookies());

        Document doc = response.parse();

        return new PostbackResult(doc,
                doc.select("#__VIEWSTATE").val(),
                doc.select("#__VIEWSTATEGENERATOR").val(),
                doc.select("#__EVENTVALIDATION").val(),
                postbackResult.cookies);
    }

    public String extractDeltaValue(String body, String fieldName){
        Matcher m = Pattern.compile(Pattern.quote(fieldName) + "\\|([^|]*)\\|").matcher(body);
        return m.find() ? m.group(1) : null;
    }

    /**
     * 페이저 안의 숫자 버튼 중 가장 큰 값을 페이지 수로 쓴다.
     * 기존 방식(div.paging 개수)은 페이저가 div 하나라 항상 1페이지로 계산돼 30명에서 잘렸다.
     */
    private int extractMaxPage(Document doc){
        int max = 1;
        for (Element el : doc.select("div.paging a, div.paging span")) {
            try {
                max = Math.max(max, Integer.parseInt(el.text().trim()));
            } catch (NumberFormatException e) {
                // "이전", "다음" 같은 버튼은 건너뜀
            }
        }
        return max;
    }
    
    public <T> Map<Long, T> parse(Document doc, PitcherStatParser<T> parser) {
        Map<Long, T> map = new HashMap<>();

        for(Element row : doc.select("table.tData01 tbody tr")){
            Long playerId = KboLinkParser.extractPlayerId(row.select("td").get(1));
            T dto = parser.rowParser(row);
            map.put(playerId, dto);
        }
        return map;
    }
}