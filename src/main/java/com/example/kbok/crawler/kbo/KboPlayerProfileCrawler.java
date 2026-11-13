package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.PlayerProfileDto;
import com.example.kbok.crawler.kbo.util.CollectVerifier;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KboPlayerProfileCrawler {

        private static final String URL = "https://www.koreabaseball.com/Player/Search.aspx";
        private static final String UA = "Mozilla/5.0 (kbok-project; educational use)";

        // 폼 필드의 정확한 name들 (매번 재입력 실수 방지용 상수화)
        private static final String F_TEAM =
                "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlTeam";
        private static final String F_POSITION =
                "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ddlPosition";
        private static final String F_NAME =
            "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$txtSearchPlayerName";
        private static final String F_PAGE =
            "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$hfPage";

        private static final String PAGER_PREFIX =
            "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ucPager$btnNo";

        private static final String PAGER_NEXT =
                "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ucPager$btnNext";


        private static final int BLOCK_SIZE = 5;
        /** 선수 명단 한 페이지에 나오는 인원 (올림 나눗셈: 104명 ÷ 20 = 5.2 → 6페이지) */
        private static final int PAGE_SIZE = 20;

        private record PostbackResult(Document doc, String viewState, String viewStateGen, String eventValidation){}

        public List<PlayerProfileDto> crawlByTeam(String teamCode) throws IOException, InterruptedException{
                List<PlayerProfileDto> all = new ArrayList<>();

                Response initial = Jsoup.connect(URL)
                                .userAgent(UA)
                                .method(Method.GET)
                                .timeout(5000)
                                .execute();

                Document initialDoc = initial.parse();
                String viewState = initialDoc.select("#__VIEWSTATE").val();
                String viewStateGen = initialDoc.select("#__VIEWSTATEGENERATOR").val();
                String eventValidation = initialDoc.select("#__EVENTVALIDATION").val();
                Map<String, String> cookies = initial.cookies();

                PostbackResult page1 = requestPage(cookies, viewState, viewStateGen, eventValidation, F_TEAM, teamCode, "0");
                all.addAll(parseRows(page1.doc()));

                int totalCount = extractTotalCount(page1.doc());
                int maxPage = totalCount > 0 ? (int) Math.ceil((double) totalCount / PAGE_SIZE) : 1;
                log.info("총 건수={}, 계산된 페이지 수={}", totalCount, maxPage);
                log.info("[{}] 총 페이지 수 : {}", teamCode, maxPage);

                String vs = page1.viewState(), vsg = page1.viewStateGen(), ev = page1.eventValidation();
                
                for(int page = 2; page <= maxPage; page++){
                        boolean crossesBlockBoundary = ((page - 1) % BLOCK_SIZE == 0);

                        String eventTarget;
                        String hfPageValue;
                        
                        if (crossesBlockBoundary) {
                                eventTarget = PAGER_NEXT;
                                hfPageValue = String.valueOf((page - 1) / BLOCK_SIZE);  // 6페이지 → 1, 11페이지 → 2
                        }else if (page <= BLOCK_SIZE) {
                                // 첫 번째 묶음(2~5페이지): 기존 방식 그대로
                                eventTarget = PAGER_PREFIX + page;
                                hfPageValue = String.valueOf(page - 1);
                        } else {
                                // 두 번째 묶음부터는 버튼 번호가 1부터 다시 시작하고,
                                // hfPage에는 몇 번째 묶음인지(0부터)를 보낸다.
                                // 브라우저 확인: 7페이지 = btnNo2 + hfPage 1
                                int block = (page - 1) / BLOCK_SIZE;
                                eventTarget = PAGER_PREFIX + (((page - 1) % BLOCK_SIZE) + 1);
                                hfPageValue = String.valueOf(block);
                        }

                        PostbackResult result = requestPage(cookies, vs, vsg, ev, eventTarget, teamCode, hfPageValue);
                        all.addAll(parseRows(result.doc()));

                        vs = result.viewState();
                        vsg = result.viewStateGen();
                        ev = result.eventValidation();

                        Thread.sleep(1000);
                }

                log.info("[{}] 총 수집 : {}명", teamCode, all.size());
                if (totalCount > 0) {
                        CollectVerifier.verifyTotal("선수명단", teamCode, all.size(), totalCount);
                }
                return all;
        }

        private PostbackResult requestPage(Map<String, String> cookies, String viewState, String viewStateGen, String eventValidation, String eventTarget, String teamCode, String hfPageValue) throws IOException{
                
                String scriptManagerValue = "ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$udpRecord|" + eventTarget;

                 // 2단계: KIA(HT) 필터로 부분 postback 요청
                Response response = Jsoup.connect(URL)
                        .userAgent(UA)
                        .cookies(cookies)
                        .header("X-MicrosoftAjax", "Delta=true")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .data("ctl00$ctl00$ctl00$cphContents$cphContents$cphContents$ScriptManager1", scriptManagerValue)
                        .data("__EVENTTARGET", eventTarget)
                        .data("__EVENTARGUMENT", "")
                        .data("__LASTFOCUS", "")
                        .data("__VIEWSTATE", viewState)
                        .data("__VIEWSTATEGENERATOR", viewStateGen)
                        .data("__EVENTVALIDATION", eventValidation)
                        .data(F_PAGE, hfPageValue)
                        .data(F_TEAM, teamCode)
                        .data(F_POSITION, "")
                        .data(F_NAME, "")
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

        /** 페이지 상단의 "총 N명"을 읽는다. 못 찾으면 0 (이 경우 1페이지로 처리하고 검증은 건너뜀) */
        private int extractTotalCount(Document doc){
                Element countEl = doc.selectFirst("p.title strong span.point");
                if (countEl == null) {
                        log.warn("총 건수 요소를 못 찾음 - 기본값 1페이지로 처리");
                        return 0;
                }
                return Integer.parseInt(countEl.text().replaceAll("[^0-9]", ""));
        }

        private PlayerProfileDto parseRow(Element row){
                Elements cols = row.select("td");
                if(cols.size() < 7) return null;

                PlayerProfileDto dto = new PlayerProfileDto();
                dto.setKboPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
                dto.setBackNumber(cols.get(0).text().trim());
                dto.setName(cols.get(1).text().trim());
                dto.setTeamName(cols.get(2).text().trim());
                dto.setPosition(cols.get(3).text().trim());
                dto.setBirthDate(parseBirthDate(cols.get(4).text().trim()));

                String[] physique = cols.get(5).text().split(",");
                if(physique.length == 2){
                        dto.setHeight(parseMeasurement(physique[0]));
                        dto.setWeight(parseMeasurement(physique[1]));
                }

                dto.setCareer(cols.get(6).text().trim());
                return dto;
        }

        private List<PlayerProfileDto> parseRows(Document doc){
                List<PlayerProfileDto> list = new ArrayList<>();
                for(Element row : doc.select("table tbody tr")){
                        PlayerProfileDto dto = parseRow(row);
                        if(dto != null) list.add(dto);
                }
                return list;
        }

        private LocalDate parseBirthDate(String raw){
                try {
                        return LocalDate.parse(raw);
                } catch (Exception e) {
                        return null;
                }
        }

        private Integer parseMeasurement(String raw){
                String digits = raw.replaceAll("[^0-9]","");
                return digits.isEmpty() ? null : Integer.parseInt(digits);
        }
}