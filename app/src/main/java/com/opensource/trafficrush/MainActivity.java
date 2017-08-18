package com.opensource.trafficrush;

/**
 * Copyright (c) 2017 Hemanth Lam, Nikhitha Durvasula
 * Licensed under the MIT (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static LatLng goodLatLng = new LatLng(37, -120);
    private GoogleMap googleMap;
    private TextView source;
    private AutoCompleteTextView destination;
    LatLng addressPos, finalAddressPos;
    Marker addressMarker;
    public Button trafficData;

    private ArrayList<AutoCompleteBean> resultList;
    private ArrayList<Double> locationResult;

    private static final String LOG_TAG = "AutoComplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyA65-eqSvIefv4lY3vARmN4fwVc1d4lPaE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trafficData = (Button) findViewById(R.id.trafficData);

        trafficData.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String geoUriString = "https://waze.com/ul?q=Chicago";

                Intent wazeMapCall = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString));
                startActivity(wazeMapCall);
            }

        });

        source = (TextView) findViewById(R.id.Source);
        destination = (AutoCompleteTextView) findViewById(R.id.finalAddressEditText);

        //source.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item));
        destination.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_spinner_dropdown_item));

        // Initial Map
        try {

            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Put a dot on my current location
        googleMap.setMyLocationEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setTrafficEnabled(true);
        // 3D building
        googleMap.setBuildingsEnabled(true);
        // Get zoom button
        googleMap.getUiSettings().setZoomControlsEnabled(true);

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

    public void showAddressMarker(View view) {

        String sourceAddress = source.getText().toString();
        String destinationAddress = destination.getText().toString();
        if (destinationAddress != null) {
            new PlaceAMarker().execute(destinationAddress);
        }
    }

    public void getDirections(View view) {

        String startingAddress = source.getText().toString();
        String finalAddress = destination.getText().toString();

        if (finalAddress.equals("")) {
            Toast.makeText(this, "Enter a starting and Ending address", Toast.LENGTH_SHORT).show();
        } else {
            new GetDirections().execute(startingAddress, finalAddress);
        }
    }

    class PlaceAMarker extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String startAddress = params[0];
            String endAddress = params[0];

            startAddress = startAddress.replaceAll(" ", "%20");
            endAddress = endAddress.replaceAll(" ", "%20");
            Log.d("findMe", "startAddress in placeMarker: " + startAddress);
            Log.d("findMe", "endAddress in placeMarker: " + endAddress);
            getLatLng(startAddress, false);
            getLatLng(endAddress, false);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            addressMarker = googleMap.addMarker(new MarkerOptions()
                    .position(addressPos).title("Address"));
        }
    }

    class GetDirections extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            String startAddress =  params[0];
            startAddress = startAddress.replaceAll(" ", "%20");
            getLatLng(startAddress, false);

            String endingAddress = params[1];
            endingAddress = endingAddress.replaceAll(" ", "%20");
            getLatLng(endingAddress, true);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String geoUriString = "http://maps.google.com/maps?addr=" +
                    addressPos.latitude + "," +
                    addressPos.longitude + "&daddr=" +
                    finalAddressPos.latitude + "," +
                    finalAddressPos.longitude;

            Intent mapCall = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString));
            startActivity(mapCall);
        }
    }

    public void getLatLng(String address, boolean setDestination) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address="
                + address + "&sensor=false";
        Log.d("findME", "uri = " + uri);
        HttpGet httpGet = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            InputStream stream = entity.getContent();

            int byteData;
            while ((byteData = stream.read()) != -1) {
                stringBuilder.append((char) byteData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        double lat = 0.0, lng = 0.0;

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            Log.d("findMe", "exception: " + e.getMessage());
            e.printStackTrace();
        }

        Log.d("findMe:",  "lat: " + lat + "long: " + lng);
        if (setDestination) {
            finalAddressPos = new LatLng(lat, lng);
        } else {
            addressPos = new LatLng(lat, lng);
        }
    }

    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> result;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).getDescription();
        }


        @NonNull
        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        assert resultList != null;
                        result = new ArrayList<>(resultList.size());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
        }
    }
    private ArrayList<AutoCompleteBean> autocomplete(String input) {

        ArrayList<AutoCompleteBean> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            String sb = PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?input=" + URLEncoder.encode(input, "utf8") +
                    "&sensor=true&key=" + API_KEY;

            URL url = new URL(sb);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new AutoCompleteBean(predsJsonArray.getJSONObject(i).getString("description"), predsJsonArray.getJSONObject(i).getString("reference")));
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private ArrayList<Double> Details(String description, String reference ) {

        ArrayList<Double> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            String sb = PLACES_API_BASE + TYPE_DETAILS + OUT_JSON + "?reference=" + URLEncoder.encode(reference, "utf8") +
                    "&key=" + API_KEY;

            URL url = new URL(sb);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject jsonObjResult = jsonObj.getJSONObject("result");
            JSONObject jsonObjGemmetry = jsonObjResult.getJSONObject("geometry");
            JSONObject jsonObjLocation = jsonObjGemmetry.getJSONObject("location");

            System.out.println("jsonObj.toString() :::: " + jsonObj.toString());
            System.out.println("jsonObjLocation.toString() :::: " + jsonObjLocation.toString());

            resultList = new ArrayList<>(2);
            resultList.add(jsonObjLocation.getDouble("lat"));
            resultList.add(jsonObjLocation.getDouble("lng"));
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }
}
