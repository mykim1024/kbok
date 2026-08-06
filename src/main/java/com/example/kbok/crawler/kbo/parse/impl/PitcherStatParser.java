package com.example.kbok.crawler.kbo.parse.impl;

import org.jsoup.nodes.Element;

public interface PitcherStatParser <T>{
    T rowParser(Element row);
}
