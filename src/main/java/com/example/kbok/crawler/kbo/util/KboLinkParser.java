package com.example.kbok.crawler.kbo.util;

import org.jsoup.nodes.Element;

public class KboLinkParser {
    
    private KboLinkParser(){

    }

    public static Long extractPlayerId(Element nameCell){
        Element link = nameCell.selectFirst("a[href]");
        if(link == null) return null;

        String href = link.attr("href");
        int idx = href.indexOf("playerId=");
        if(idx == -1) return null;

        String idPart = href.substring(idx + "playerId=".length());
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parseInteger(String raw){
            try {
                return Integer.parseInt(raw);
            } catch (Exception e) {
                return null;
            }
    }

    /** "-" 같은 빈 값은 null (예: 이닝이 0인 투수의 ERA) */
    public static java.math.BigDecimal parseDecimal(String raw){
        try {
            return new java.math.BigDecimal(raw);
        } catch (Exception e) {
            return null;
        }
    }
}
