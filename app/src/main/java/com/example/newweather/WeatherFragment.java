package com.example.newweather;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
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

    private void ParseAndSet(String city) {
        final Handler h = new Handler();                                                            // Создаём Handler в главном потоке.
                                                                                                    // Он будет выполнять заданный ему код из главного потока когда придёт сообщение.
        Thread pull = new Thread() {                                                                // Создаём поток и прописываем ему функцию.
            @Override
            public void run() {
                Parser parser = new Parser();
                String temperature=parser.getTemperature(city, "gismeteo");

                h.post(new Runnable() {                                                             // Задаём код Handler'у и отправляем сообщение.
                    @Override
                    public void run() {
                        tempView.setText(temperature + "°");
                        cityView.setText("г. " + getCityName());
                        date = new Date();
                        dateView.setText("Последнее обновление: " + sdf.format(date));
                    }
                });
            }
        };                                                                                          // По итогу с сайта информация ищется в нашем потоке, а изменение текстовых ячеек происходит в главном потоке.
        pull.start();                                                                               // Запуск нашего потока.
    }

    private String getCityName() {
        return new CityPreference(getActivity()).getCity();
    }

    public void changeCity(String city){
        ParseAndSet(city);
    }

}


