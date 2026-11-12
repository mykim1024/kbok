package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.BatterStatBasic1Dto;
import com.example.kbok.crawler.kbo.dto.BatterStatBasic2Dto;
import com.example.kbok.crawler.kbo.dto.FieldingStatDto;
import com.example.kbok.crawler.kbo.dto.RunnerStatDto;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BatterStatCrawler {

    private static final String URL1 = "https://www.koreabaseball.com/Record/Player/HitterBasic/Basic1.aspx?sort=HRA_RT";
    private static final String URL2 = "https://www.koreabaseball.com/Record/Player/HitterBasic/Basic2.aspx?sort=HRA_RT";
    private static final String URL_RUNNER = "https://www.koreabaseball.com/Record/Player/Runner/Basic.aspx";
    private static final String URL_DEFENSE = "https://www.koreabaseball.com/Record/Player/Defense/Basic.aspx";
    private static final String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36";

    // 폼 필드의 정확한 name들 (매번 재입력 실수 방지용 상수화)
    private static final String F_SEASON = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSeason$ddlSeason";
    private static final String F_SERIES = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlSeries$ddlSeries";
    private static final String F_TEAM = 
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlTeam$ddlTeam";
    private static final String F_POSITION =
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlPos$ddlPos";
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
    private static final String PAGER_NEXT =
        "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ucPager$btnNext";

    private static final int BLOCK_SIZE = 5;

    private record PostbackResult(Document doc, String viewState, String viewStateGen, String eventValidation){}

    public List<BatterStatBasic1Dto> crawlBasic1(String teamCode, String season) throws IOException, InterruptedException {
        return crawlByTeam(URL1, teamCode, season, this::parseRow, "HRA_RT");        // T = BatterRecordBasic1Dto
    }

    public List<BatterStatBasic2Dto> crawlBasic2(String teamCode, String season) throws IOException, InterruptedException {
        return crawlByTeam(URL2, teamCode, season, this::parseRowDetail, "HRA_RT");  // T = BatterRecordBasic2Dto
    }

    /** 주루 기록 (도루·도루 실패) - 같은 postback 구조라 타자 크롤러를 재사용 */
    public List<RunnerStatDto> crawlRunner(String teamCode, String season) throws IOException, InterruptedException {
        return crawlByTeam(URL_RUNNER, teamCode, season, this::parseRunnerRow, "SB_CN");
    }

    /** 수비 기록 - 한 선수가 여러 포지션을 보면 포지션마다 한 줄씩 나온다 */
    public List<FieldingStatDto> crawlFielding(String teamCode, String season) throws IOException, InterruptedException {
        return crawlByTeam(URL_DEFENSE, teamCode, season, this::parseFieldingRow, "GAME_CN");
    }

    private <T> List<T> crawlByTeam(String url, String teamCode, String season, Function<Element, T> rowParser, String orderCol) throws IOException, InterruptedException {
        List<T> all = new ArrayList<>();

        Response initial = Jsoup.connect(url)
            .userAgent(UA)
            .method(Method.GET)
            .timeout(5000)
            .execute();

        Document initialDoc = initial.parse();
        String viewState = initialDoc.select("#__VIEWSTATE").val();
        String viewStateGen = initialDoc.select("#__VIEWSTATEGENERATOR").val();
        String eventValidation = initialDoc.select("#__EVENTVALIDATION").val();
        Map<String, String> cookies = initial.cookies();

        PostbackResult page1 = requestPage(url, cookies, viewState, viewStateGen, eventValidation, F_TEAM, teamCode, "1", season, orderCol);
        all.addAll(parseRows(page1.doc(), rowParser));

        int maxPage = extractMaxPage(page1.doc());
        log.info("[{}] 총 페이지 수 : {}", teamCode, maxPage);

        String vs = page1.viewState(), vsg = page1.viewStateGen(), ev = page1.eventValidation();
                
        for(int page = 2; page <= maxPage; page++){
            boolean crossesBlockBoundary = ((page - 1) % BLOCK_SIZE == 0);

            String eventTarget;
            String hfPageValue;

            if (crossesBlockBoundary) {
                eventTarget = PAGER_NEXT;
                hfPageValue = "1"; // 캡처로 확인된 고정값
            } else {
                eventTarget = PAGER_PREFIX + page;
                hfPageValue = String.valueOf(page - 1);
            }

            PostbackResult result = requestPage(url, cookies, vs, vsg, ev, eventTarget, teamCode, hfPageValue, season, orderCol);
            all.addAll(parseRows(result.doc(), rowParser));

            vs = result.viewState();
            vsg = result.viewStateGen();
            ev = result.eventValidation();

            Thread.sleep(1000);
        }

        log.info("[{}] 총 수집 : {}명", teamCode, all.size());
        return all;
    }

    private PostbackResult requestPage(String url, Map<String, String> cookies, String viewState, String viewStateGen, String eventValidation, String eventTarget, String teamCode, String hfPageValue, String season, String orderCol) throws IOException{
        String scriptManagerValue = "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$udpContent|" + eventTarget;

        // 2단계: KIA(HT) 필터로 부분 postback 요청
        Response response = Jsoup.connect(url)
            .userAgent(UA)
            .cookies(cookies)
            .header("Referer", url)
            .header("Origin", "https://www.koreabaseball.com")
            .header("Accept", "*/*")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .header("X-MicrosoftAjax", "Delta=true")
            .header("X-Requested-With", "XMLHttpRequest")
            .data(F_SCRIPT_MANAGER, scriptManagerValue)
            .data("__EVENTTARGET", eventTarget)
            .data("__EVENTARGUMENT", "")
            .data("__LASTFOCUS", "")
            .data("__VIEWSTATE", viewState)
            .data("__VIEWSTATEGENERATOR", viewStateGen)
            .data("__EVENTVALIDATION", eventValidation)
            .data(F_PAGE, hfPageValue)
            .data(F_PAGE_ORDER, "DESC")
            .data(F_PAGE_ORDER_COL, orderCol)
            .data(F_SEASON, season)
            .data(F_SERIES, "0")
            .data(F_TEAM, teamCode)
            .data(F_POSITION, "")
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
                extractDeltaValue(body, "__EVENTVALIDATION"));
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

        private <T> List<T> parseRows(Document doc, Function<Element, T> rowParser){
            List<T> list = new ArrayList<>();
            for (Element row : doc.select("table.tData01 tbody tr")) {
                T dto = rowParser.apply(row);
                if (dto != null) list.add(dto);
            }
            return list;
        }

        private BatterStatBasic1Dto parseRow(Element row){
            Elements cols = row.select("td");
            if(cols.size() < 16) return null;

            BatterStatBasic1Dto dto = new BatterStatBasic1Dto();
            dto.setPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
            dto.setName(cols.get(1).text().trim());
            dto.setTeam(cols.get(2).text().trim());
            dto.setAvg(paseDecimal(cols.get(3).text().trim()));   // AVG
            dto.setGame(parseInteger(cols.get(4).text().trim()));
            dto.setPa(parseInteger(cols.get(5).text().trim()));
            dto.setAb(parseInteger(cols.get(6).text().trim()));
            dto.setRun(parseInteger(cols.get(7).text().trim()));
            dto.setHit(parseInteger(cols.get(8).text().trim()));
            dto.setDouble2b(parseInteger(cols.get(9).text().trim()));
            dto.setTriple3b(parseInteger(cols.get(10).text().trim()));
            dto.setHr(parseInteger(cols.get(11).text().trim()));
            dto.setTb(parseInteger(cols.get(12).text().trim()));
            dto.setRbi(parseInteger(cols.get(13).text().trim()));
            dto.setSac(parseInteger(cols.get(14).text().trim()));
            dto.setSf(parseInteger(cols.get(15).text().trim()));

            return dto;
        }


        private BatterStatBasic2Dto parseRowDetail(Element row){
            Elements cols = row.select("td");
            if(cols.size() < 12) return null;

            BatterStatBasic2Dto dto = new BatterStatBasic2Dto();
            dto.setPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
            dto.setName(cols.get(1).text().trim());
            dto.setTeam(cols.get(2).text().trim());
            dto.setBb(parseInteger(cols.get(4).text().trim()));
            dto.setIbb(parseInteger(cols.get(5).text().trim()));
            dto.setHbp(parseInteger(cols.get(6).text().trim()));
            dto.setSo(parseInteger(cols.get(7).text().trim()));
            dto.setGdp(parseInteger(cols.get(8).text().trim()));
            dto.setSlg(paseDecimal(cols.get(9).text().trim()));
            dto.setObp(paseDecimal(cols.get(10).text().trim()));
            dto.setOps(paseDecimal(cols.get(11).text().trim()));

            return dto;
        }

        /** 주루기록 컬럼: 순위, 선수명, 팀명, G, SBA, SB, CS, SB%, OOB, PKO */
        private RunnerStatDto parseRunnerRow(Element row){
            Elements cols = row.select("td");
            if(cols.size() < 10) return null;

            RunnerStatDto dto = new RunnerStatDto();
            dto.setPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
            dto.setName(cols.get(1).text().trim());
            dto.setTeam(cols.get(2).text().trim());
            dto.setSb(parseInteger(cols.get(5).text().trim()));   // SB
            dto.setCs(parseInteger(cols.get(6).text().trim()));   // CS
            return dto;
        }

        /** 수비기록 컬럼: 순위, 선수명, 팀명, POS, G, GS, IP, E, PKO, PO, A, DP, FPCT, PB, SB, CS, CS% */
        private FieldingStatDto parseFieldingRow(Element row){
            Elements cols = row.select("td");
            if(cols.size() < 13) return null;

            FieldingStatDto dto = new FieldingStatDto();
            dto.setPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
            dto.setName(cols.get(1).text().trim());
            dto.setTeam(cols.get(2).text().trim());
            dto.setPosition(cols.get(3).text().trim());              // POS (예: 유격수)
            dto.setGames(parseInteger(cols.get(4).text().trim()));    // G
            dto.setErrors(parseInteger(cols.get(7).text().trim()));   // E
            dto.setFieldingPct(paseDecimal(cols.get(12).text().trim())); // FPCT, 수비 기회가 없으면 "-" → null
            return dto;
        }

        private Integer parseInteger(String raw){
            try {
                return Integer.parseInt(raw);
            } catch (Exception e) {
                return null;
            }
        }

        private BigDecimal paseDecimal(String raw){
            try {
                return new BigDecimal(raw);
            } catch (Exception e) {
                return null;
            }
        }
}
