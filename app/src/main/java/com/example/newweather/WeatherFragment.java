package com.example.newweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {

    TextView tempView;
    TextView cityView;
    TextView dateView;
    DateFormat sdf;
    Date date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        tempView = (TextView)rootView.findViewById(R.id.text_temperature);
        cityView = (TextView)rootView.findViewById(R.id.text_city);
        dateView = (TextView)rootView.findViewById(R.id.text_date);
        sdf = new SimpleDateFormat("dd.MM HH:mm");

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseAndSet(getCityName());
    }

    private static Document getPage(String url) {
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

    private void ParseAndSet(final String city) {
        Document page=getPage(getUrlFromCity(city));
        Element tempWth=page.selectFirst("span[class=js_value tab-weather__value_l]");
        String temperature=tempWth.text();

        tempView.setText(temperature + "°");
        cityView.setText("г. " + getCityName());
        date = new Date();
        dateView.setText("Последнее обновление: " + sdf.format(date));

    }

    private String getCityName() {
        return new CityPreference(getActivity()).getCity();
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
        String text = page.selectFirst("div.flexbox").selectFirst("h2").text();
        if (text.charAt(0)=='А') {                                                                  // Check if there is airport section
            link = page.selectFirst("div.flexbox")
                    .select(".catalog_block").get(1).selectFirst("a[href]");               // If there is, select second section
        } else {
            link = page.selectFirst("div.catalog_item").selectFirst("a[href]");                     // else, select first section
        }
        urlCity = link.attr("href");
        url = "https://www.gismeteo.ru"+ urlCity;
        return url;
    }

    public void changeCity(String city){
        ParseAndSet(city);
    }

}


