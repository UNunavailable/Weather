

package com.example.newweather;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // TODO: "Переставить вызов функции runTemp в нужное место, оно здесь быть не должно" 31.01.2021
        ParseAndSet();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void ParseAndSet() {
        final TextView tempView = (TextView)findViewById(R.id.text_temperature);
        final TextView cityView = (TextView)findViewById(R.id.text_city);
        final TextView dateView = (TextView)findViewById(R.id.text_date);
        final DateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm");

        String url = "";
        Document page = null;
        Element tableWth = null;
        Element tempWth = null;

        url = getUrlFromCity(getCityName());


        page=getPage(url);
        tableWth=page.selectFirst("div.tab-weather");
        tempWth=page.selectFirst("span[class=js_value tab-weather__value_l]");
        String temperature=tempWth.text();
        tempView.setText(temperature + "°");

        Date date = new Date();
        dateView.setText(sdf.format(date));
    }

    private String getCityName() {
        return "Альметьевск";
    }

    private String getUrlFromCity(String city) {
        String url = "";
        String urlCity = "";
        Document page = null;
        Element link = null;

        try {
            url = "https://www.gismeteo.ru/search/" + URLEncoder.encode(city, "UTF-8") + "/";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        page=getPage(url);
        link = page.selectFirst("div.catalog_item").selectFirst("a[href]");
        urlCity = link.attr("href");
        url = "https://www.gismeteo.ru"+ urlCity + "/";
        return url;
    }
}