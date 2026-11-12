package com.example.kbok.crawler.kbo.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.kbok.crawler.kbo.dto.PitcherStatBasic1Dto;
import com.example.kbok.crawler.kbo.parse.impl.PitcherStatParser;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

public class PitcherStatBasic1Parse implements PitcherStatParser<PitcherStatBasic1Dto>{
    @Override
    public PitcherStatBasic1Dto rowParser(Element row){
        Elements cols = row.select("td");
        
        PitcherStatBasic1Dto dto = new PitcherStatBasic1Dto();
        dto.setPlayerId(KboLinkParser.extractPlayerId(cols.get(1)));
        dto.setName(cols.get(1).text().trim());
        dto.setTeam(cols.get(2).text().trim());
        dto.setEra(KboLinkParser.parseDecimal(cols.get(3).text().trim()));    // ERA
        dto.setGame(KboLinkParser.parseInteger(cols.get(4).text().trim()));
        dto.setW(KboLinkParser.parseInteger(cols.get(5).text().trim()));
        dto.setL(KboLinkParser.parseInteger(cols.get(6).text().trim()));
        dto.setSv(KboLinkParser.parseInteger(cols.get(7).text().trim()));
        dto.setHld(KboLinkParser.parseInteger(cols.get(8).text().trim()));
        dto.setIp(cols.get(10).text().trim());
        dto.setH(KboLinkParser.parseInteger(cols.get(11).text().trim()));
        dto.setHr(KboLinkParser.parseInteger(cols.get(12).text().trim()));
        dto.setBb(KboLinkParser.parseInteger(cols.get(13).text().trim()));
        dto.setHbp(KboLinkParser.parseInteger(cols.get(14).text().trim()));
        dto.setSo(KboLinkParser.parseInteger(cols.get(15).text().trim()));
        dto.setR(KboLinkParser.parseInteger(cols.get(16).text().trim()));
        dto.setEr(KboLinkParser.parseInteger(cols.get(17).text().trim()));
        dto.setWhip(KboLinkParser.parseDecimal(cols.get(18).text().trim()));  // WHIP
        return dto;
    }

}
