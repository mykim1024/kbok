package com.example.kbok.crawler.kbo.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.kbok.crawler.kbo.dto.PitcherStatBasic2Dto;
import com.example.kbok.crawler.kbo.parse.impl.PitcherStatParser;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

public class PitcherStatBasic2Parse implements PitcherStatParser<PitcherStatBasic2Dto>{
    @Override
    public PitcherStatBasic2Dto rowParser(Element row){
        Elements cols  = row.select("td");

        return PitcherStatBasic2Dto.builder()
            .playerId(KboLinkParser.extractPlayerId(cols.get(1)))
            .name(cols.get(1).text().trim())
            .team(cols.get(2).text().trim())
            .cg(KboLinkParser.parseInteger(cols.get(4).text().trim()))
            .sho(KboLinkParser.parseInteger(cols.get(5).text().trim()))
            .qs(KboLinkParser.parseInteger(cols.get(6).text().trim()))
            .bsv(KboLinkParser.parseInteger(cols.get(7).text().trim()))
            .tbf(KboLinkParser.parseInteger(cols.get(8).text().trim()))
            .doubles(KboLinkParser.parseInteger(cols.get(11).text().trim()))
            .triples(KboLinkParser.parseInteger(cols.get(12).text().trim()))
            .ibb(KboLinkParser.parseInteger(cols.get(15).text().trim()))
            .wp(KboLinkParser.parseInteger(cols.get(16).text().trim()))
            .bk(KboLinkParser.parseInteger(cols.get(17).text().trim()))
            .build();
    }
}
