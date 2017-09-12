package com.pro.tacoteam.conocerclima;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/*ejemplo con mi API KEY
 * http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=8336bff967aa296a01182ca7f7154453
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG_weather = "weather";
    private static final String TAG_wather_description = "description";
    private static final String TAG_MAIN = "main";
    private static final String TAG_temp = "temp";
    private static final String TAG_pressure = "pressure";
    private static final String TAG_humidity = "humidity";
    private static final String API_KEY = "8336bff967aa296a01182ca7f7154453";
    private static String url = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String TAG = MainActivity.class.getSimpleName();
    private static  String selectedCity = "";
    private static  String estado = "";
    private static  String temperatura = "";
    private static  String presion = "";
    private static  String humedad = "";
    private ProgressDialog pDialog;
    EditText estadoPro;
    EditText temperaturaPro;
    EditText presionPro;
    EditText humedadPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        renderSpinner();
        estadoPro = (EditText) findViewById(R.id.weatherMain);
        temperaturaPro = (EditText) findViewById(R.id.temperatura);
        presionPro = (EditText) findViewById(R.id.presion);
        humedadPro = (EditText) findViewById(R.id.humedad);

        estadoPro.setKeyListener(null);
        temperaturaPro.setKeyListener(null);
        presionPro.setKeyListener(null);
        humedadPro.setKeyListener(null);

    }


    public void renderSpinner() {
// you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Monterrey");
        spinnerArray.add("Culiacan");
        spinnerArray.add("Guadalajara");
        spinnerArray.add("Santiago");
        spinnerArray.add("Saltillo");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCity =  parentView.getItemAtPosition(position).toString();
                new GetInfo().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String urlPro = url + selectedCity + ",mx&lang=es&units=metric&appid=" + API_KEY;
            String jsonStr = sh.makeServiceCall(urlPro);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray weather = jsonObj.getJSONArray(TAG_weather);
                    estado =    weather.getJSONObject(0).getString(TAG_wather_description);


                    JSONObject main = jsonObj.getJSONObject(TAG_MAIN);
                     temperatura = main.getString(TAG_temp);
                     presion = main.getString(TAG_pressure);
                     humedad = main.getString(TAG_humidity);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            estadoPro.setText(estado);
            temperaturaPro.setText(temperatura + "°C");
            humedadPro.setText(humedad +"%");
            presionPro.setText(presion + "hPa");

        }

    }



}
