package ru.geekbrains.webbrowser;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.browse);
        url = findViewById(R.id.url);
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            try {
                final URL uri = new URL(url.getText().toString());
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpsURLConnection urlConnection = null;
                        try {
                            urlConnection = (HttpsURLConnection) uri.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setReadTimeout(10000);
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            final String result = this.getLines(in);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadData(result, "text/html; charset=utf-8", "utf-8");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            if (urlConnection != null){
                                urlConnection.disconnect();
                            }
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    private String getLines(BufferedReader in){
                        return in.lines().collect(Collectors.joining("\n"));
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    };
}
