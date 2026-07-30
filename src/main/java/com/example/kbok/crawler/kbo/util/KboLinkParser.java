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
}
