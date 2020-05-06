package ru.geekbrains.weathermoscow;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.weathermoscow.data.WeatherRequest;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=55.75&lon=37.62&appid=";
    private static final String TAG = "WEATHER_MY";

    private EditText city;
    private EditText temperature;
    private EditText pressure;
    private EditText humidity;
    private EditText windSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        city = findViewById(R.id.textCity);
        temperature = findViewById(R.id.textTemprature);
        pressure = findViewById(R.id.textPressure);
        humidity = findViewById(R.id.textHumidity);
        windSpeed = findViewById(R.id.textWindspeed);
        Button refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        HttpsURLConnection urlConnection = null;
                        try{
                            urlConnection = (HttpsURLConnection) uri.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setReadTimeout(10000);
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String result = getLines(in);
                            Gson gson = new Gson();
                            final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    displayWeather(weatherRequest);
                                }
                            });
                        }catch(Exception e){
                            Log.e(TAG, "Fail Connection", e);
                            e.printStackTrace();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    private String getLines(BufferedReader in) {
                        return in.lines().collect(Collectors.joining("\n"));
                    }

                }).start();
            }catch(Exception e){
                Log.e(TAG, "Fail URL", e);
                e.printStackTrace();
            }
        }
    };

    private void displayWeather(WeatherRequest weatherRequest){
        city.setText(weatherRequest.getName());
        temperature.setText(String.format("%f2", weatherRequest.getMain().getTemp()));
        pressure.setText(String.format("%d", weatherRequest.getMain().getPressure()));
        humidity.setText(String.format("%d", weatherRequest.getMain().getHumidity()));
        windSpeed.setText(String.format("%d", weatherRequest.getWind().getSpeed()));
    }
}
