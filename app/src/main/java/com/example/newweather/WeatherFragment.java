package com.example.newweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;



import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class WeatherFragment extends Fragment {

    TextView tempView;
    TextView cityView;
    TextView dateView;
    DateFormat sdf;
    Date date;

    TextView txtLocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        tempView = (TextView) rootView.findViewById(R.id.text_temperature);
        cityView = (TextView) rootView.findViewById(R.id.text_city);
        dateView = (TextView) rootView.findViewById(R.id.text_date);
        sdf = new SimpleDateFormat("dd.MM HH:mm");

        txtLocation = (TextView) rootView.findViewById(R.id.txt_location);


        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ParseAndSet(getCityName());

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        while (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    99);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            txtLocation.setText("");

            Toast.makeText(
                    getActivity().getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            txtLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
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


