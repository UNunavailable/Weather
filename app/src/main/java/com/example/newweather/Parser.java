package com.example.newweather;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Parser {
    private Document getPage(String url) {
        final Document[] page = {null}; // idk why, but it needs to be final one-array document

        // creating new thread for pulling url page
        Thread pull = new Thread() {
            @Override
            public void run() {
                try {
                    page[0] = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            pull.start(); // starting thread
            pull.join(); // waiting for thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return page[0];
    }

    public String getTemperature(String city, String site) {
        Document page=getPage(getUrlFromCity(city));
        Element temperatureElement=page.selectFirst("span[class=unit unit_temperature_c]");
        String temperature=temperatureElement.text();
        return temperature;
    }

    private String getUrlFromCity(String city) {
        String url = "";
        String urlCity;
        Document page;
        Element link;

        try {
            url = "https://www.gismeteo.ru/search/" + URLEncoder.encode(city, "UTF-8") + "/";  // Encode city name into utf-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        page=getPage(url);
        urlCity = page.selectFirst("div.catalog-item-link").selectFirst("a").attr("href");
        url = "https://www.gismeteo.ru"+ urlCity;
        return url;
    }
}
