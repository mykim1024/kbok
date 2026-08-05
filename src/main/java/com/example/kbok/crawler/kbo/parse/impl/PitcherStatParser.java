package com.example.kbok.crawler.kbo.parse.impl;

import java.util.Map;

import org.jsoup.nodes.Document;

public interface PitcherStatParser <T>{
    Map<Long, T> parse(Document doc);
}
