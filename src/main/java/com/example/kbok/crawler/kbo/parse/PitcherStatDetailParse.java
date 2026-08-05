package com.example.kbok.crawler.kbo.parse;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.kbok.crawler.kbo.dto.PitcherStatDetailDto;
import com.example.kbok.crawler.kbo.parse.impl.PitcherStatParser;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

public class PitcherStatDetailParse implements PitcherStatParser<PitcherStatDetailDto>{
    @Override
    public Map<Long, PitcherStatDetailDto> parse(Document doc) {
        Map<Long, PitcherStatDetailDto> map = new HashMap<>();

        for(Element row : doc.select("table.tData01 tbody tr")){
            PitcherStatDetailDto dto = rowParser(row);
            map.put(dto.getPlayerId(), dto);
        }
        return map;
    }

    public PitcherStatDetailDto rowParser(Element row){
        Elements cols = row.select("td");

        return PitcherStatDetailDto.builder()
            .playerId(KboLinkParser.extractPlayerId(cols.get(1)))
            .name(cols.get(1).text().trim())
            .team(cols.get(2).text().trim())
            .gs(KboLinkParser.parseInteger(cols.get(4).text().trim()))
            .svo(KboLinkParser.parseInteger(cols.get(8).text().trim()))
            .gdp(KboLinkParser.parseInteger(cols.get(10).text().trim()))
            .build();
    }
}
