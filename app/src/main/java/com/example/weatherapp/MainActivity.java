package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MainActivity extends AppCompatActivity {

    public void getWeather(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String city = String.valueOf(editText.getText());
        String url;
        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            url = "https://api.openweathermap.org/data/2.5/weather?q=" +city+ "&appid=282568d2c7712acdc7b78de728d580d8";
            result = task.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Result",result);
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url=new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result+= current;
                    data = reader.read();
                }


                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldnt reach weather for this city :(", Toast.LENGTH_SHORT).show();
                return "failed";
            }




        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String text = null;

            TextView textView = (TextView) findViewById(R.id.textView);


            
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content",weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i=0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                    text = jsonPart.getString("description");
                    text = text.substring(0, 1).toUpperCase() + text.substring(1);
                }

                String tempInfo = jsonObject.getString("main");
                Log.i("Temp", tempInfo);
                Pattern p = Pattern.compile("temp\":(.*?),");
                Matcher m = p.matcher(tempInfo);
                while (m.find()) {
                    double temp = Double.parseDouble (m.group(1)) -273;
                    text += "\n" + "Temp: " + String.format("%.1f",temp) + "°C";

                }

                p = Pattern.compile("feels_like\":(.*?),");
                m = p.matcher(tempInfo);
                while (m.find()) {
                    double feelsLike = Double.parseDouble (m.group(1)) -273;
                    text += "\n" + "Feels Like: " + String.format("%.1f",feelsLike) + "°C";

                }



            } catch (Exception e) {
                e.printStackTrace();
            }
            textView.setText(text);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}