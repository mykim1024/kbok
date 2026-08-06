package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    private record PostbackResult(Document doc, String viewState, String viewStateGen, String eventValidation, Map<String, String> cookies){}

    public <T> Map<Long, T> crawl(String url, String season, String teamCode, PitcherStatParser<T> parser) throws IOException, InterruptedException{
        PostbackResult initResutl = crawlInit(url);
        
        PostbackResult pageResult = crawlByPage(url, teamCode, season, "1", initResutl);
       
        Map<Long, T> parseResult = new HashMap<>();
        parseResult.putAll(parse(pageResult.doc, parser));

        int maxPage = extractMaxPage(pageResult.doc());
        for(int page = 2; page <= maxPage; page++){
            pageResult = crawlByPage(url, teamCode, season, String.valueOf(page), pageResult);
            parseResult.putAll(parse(pageResult.doc, parser));
        }

        return parseResult;
    }

    private PostbackResult crawlInit(String url) throws IOException{
        Response initial = Jsoup.connect(url)
            .userAgent(UA)
            .method(Method.GET)
            .timeout(50000)
            .execute();

        Document initialDoc = initial.parse();
        String viewState = initialDoc.select("#__VIEWSTATE").val();
        String viewStateGen = initialDoc.select("#__VIEWSTATEGENERATOR").val();
        String eventValidation = initialDoc.select("#__EVENTVALIDATION").val();
        Map<String, String> cookies = initial.cookies();
        
        return  new PostbackResult(initialDoc, viewState, viewStateGen, eventValidation, cookies);
    }

    private PostbackResult crawlByPage(String url, String teamCode, String season, String page, PostbackResult postbackResult) throws IOException{
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


        String body = response.body();

        Document doc = Jsoup.parse(body);

        return new PostbackResult(doc, 
                extractDeltaValue(body, "__VIEWSTATE"), 
                extractDeltaValue(body, "__VIEWSTATEGENERATOR"), 
                extractDeltaValue(body, "__EVENTVALIDATION"),
                postbackResult.cookies);
    }

    public String extractDeltaValue(String body, String fieldName){
        Matcher m = Pattern.compile(Pattern.quote(fieldName) + "\\|([^|]*)\\|").matcher(body);
        return m.find() ? m.group(1) : null;
    }

    private int extractMaxPage(Document doc){
        Elements elements = doc.select("div.paging");
        return elements.size() == 1 ? 1 : elements.size() - 2;   
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
