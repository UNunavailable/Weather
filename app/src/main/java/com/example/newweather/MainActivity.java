package com.example.newweather;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.URL;
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

        try {
            runTemp();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private static Document getPage() throws IOException {
        String url="https://www.gismeteo.ru/weather-almetevsk-11940/";
        Document page= Jsoup.parse(new URL(url),3000);
        return page;

    }

    private void runTemp() throws IOException {

        final TextView tempView=(TextView)findViewById(R.id.text_temperature);
        final TextView cityView=(TextView)findViewById(R.id.text_city);
        final TextView dateView=(TextView)findViewById(R.id.text_date);
        final DateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm");

        Document page=getPage();
        Element tableWth=page.selectFirst("div.tab-weather");
        Element tempWth=page.selectFirst("span[class=js_value tab-weather__value_l]");
        String temperature=tempWth.text();
        tempView.setText(temperature + " °");

        Date date = new Date();
        dateView.setText(sdf.format(date));
    }
}