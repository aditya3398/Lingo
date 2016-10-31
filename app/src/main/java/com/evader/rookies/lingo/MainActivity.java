package com.evader.rookies.lingo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.support.v7.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    ImageView logo;
    Button moveOnToAnlysis;
    String largeString = "";
    LatLng latLng1;
    ArrayList<UrbanDefinition> termadefs = new ArrayList<UrbanDefinition>();

    //UI Stuff
    private EditText autocomplete_address;
    private AutoCompleterAdapter autoCompleterAdapter;
    private RecyclerView recyclerView;
    private ImageView clearEditView;

    //Maps API stuff
    protected GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    GoogleMap mGoogleMap;
    public static Map<Character, ArrayList<String>> stopwords = new HashMap<>(26, 0.75f);

    RecyclerView companyCards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageView darkCover = (ImageView) findViewById(R.id.darkCover);
        final TextView darkCoverText = (TextView) findViewById(R.id.darkCoverText);
        darkCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                darkCover.setVisibility(View.GONE);
                darkCoverText.setVisibility(View.GONE);
            }
        });
        darkCoverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                darkCover.setVisibility(View.GONE);
                darkCoverText.setVisibility(View.GONE);
            }
        });
        for (int x = 97; x < 123; x++) {
            stopwords.put((char)x, new ArrayList<String>());
        }
        stopwords.get('a').addAll(new ArrayList<String>(Arrays.asList("a", "and", "am", "are", "as", "at")));
        stopwords.get('b').addAll(new ArrayList<String>(Arrays.asList("because", "be", "but", "by")));
        stopwords.get('c').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('d').addAll(new ArrayList<String>(Arrays.asList("do")));
        stopwords.get('e').addAll(new ArrayList<String>(Arrays.asList("every", "eight")));
        stopwords.get('f').addAll(new ArrayList<String>(Arrays.asList("for", "four", "five")));
        stopwords.get('g').addAll(new ArrayList<String>(Arrays.asList("go")));
        stopwords.get('h').addAll(new ArrayList<String>(Arrays.asList("here", "how", "have", "he")));
        stopwords.get('i').addAll(new ArrayList<String>(Arrays.asList("i", "is", "in", "it")));
        stopwords.get('j').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('k').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('l').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('m').addAll(new ArrayList<String>(Arrays.asList("me", "my")));
        stopwords.get('n').addAll(new ArrayList<String>(Arrays.asList("nine", "not")));
        stopwords.get('o').addAll(new ArrayList<String>(Arrays.asList("of","or", "on", "okay", "ok", "our", "one")));
        stopwords.get('p').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('q').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('r').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('s').addAll(new ArrayList<String>(Arrays.asList("so", "six", "seven")));
        stopwords.get('t').addAll(new ArrayList<String>(Arrays.asList("the","this", "that", "there", "then", "to", "too", "two", "three", "ten", "them")));
        stopwords.get('u').addAll(new ArrayList<String>(Arrays.asList("up")));
        stopwords.get('v').addAll(new ArrayList<String>(Arrays.asList("very")));
        stopwords.get('w').addAll(new ArrayList<String>(Arrays.asList("we", "was", "what","who", "were", "went", "where", "why", "with", "will")));
        stopwords.get('x').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('y').addAll(new ArrayList<String>(Arrays.asList("you")));
        stopwords.get('z').addAll(new ArrayList<String>(Arrays.asList("")));
        logo = (ImageView) findViewById(R.id.toolbar_icon);

        moveOnToAnlysis = (Button) findViewById(R.id.moveNextToAnalysis);
        moveOnToAnlysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadTwitterTask().execute(latLng1);
            }
        });

        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch(NullPointerException e){
            Log.d("LINGOLOG", e.toString());
        }

        //Starting the map
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try { MapsInitializer.initialize(this); } catch (Exception e) { e.printStackTrace(); }

        //initialize the google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        //finds the user's current location
        LatLng currentlocation;
        LatLngBounds currentLocationBounds;
        /*if (getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED ||
                getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            currentlocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            currentLocationBounds = new LatLngBounds(new LatLng(mLastLocation.getLatitude()-3, mLastLocation.getLongitude()-3),
                                                    new LatLng(mLastLocation.getLatitude()+3, mLastLocation.getLongitude()+3));
        }
        else {*/
            currentlocation = new LatLng(39.323409, -99.475708); //defaults to the CVS in Medfield
            currentLocationBounds = new LatLngBounds(new LatLng(31.010571, -123.134766),
                    new LatLng(47.126213, -64.248047));
        //}
        mGoogleMap = mMapView.getMap();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 3));

        autocomplete_address = (EditText) findViewById(R.id.autocomplete_textview);
        autoCompleterAdapter = new AutoCompleterAdapter(this, R.layout.search_row, mGoogleApiClient, currentLocationBounds, null);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(autoCompleterAdapter);
        recyclerView.setVisibility(View.GONE);

        autocomplete_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerView.setVisibility(View.VISIBLE);
                clearEditView.setVisibility(View.VISIBLE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    autoCompleterAdapter.getFilter().filter(s.toString());
                }else if(!mGoogleApiClient.isConnected()){
                    String API_NOT_CONNECTED = getResources().getString(R.string.API_not_connected);
                    String GENERIC_ERROR = getResources().getString(R.string.generic_error);
                    Toast.makeText(MainActivity.this, API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                }
                else{
                    clearEditView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final AutoCompleterAdapter.PlaceAutocomplete item = autoCompleterAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                .getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if(places.getCount()==1){
                                    //Do the things here on Click.....
                                    double lat = places.get(0).getLatLng().latitude;
                                    double lng = places.get(0).getLatLng().longitude;
                                    latLng1 = new LatLng(lat, lng);
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                                    autocomplete_address.setText(item.description);
                                    recyclerView.setVisibility(View.GONE);
                                    logo.setVisibility(View.GONE);
                                    moveOnToAnlysis.setVisibility(View.VISIBLE);
                                    autocomplete_address.clearFocus();
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }else {
                                    String GENERIC_ERROR = getResources().getString(R.string.generic_error);
                                    Toast.makeText(MainActivity.this, GENERIC_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
        );

        clearEditView = (ImageView) findViewById(R.id.clearEditView);
        clearEditView.setVisibility(View.GONE);
        clearEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditView.setVisibility(View.GONE);
                autocomplete_address.setText("");
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private class DownloadTwitterTask extends AsyncTask<LatLng, Void, String> {
        final static String CONSUMER_KEY = "pPEuCKEc4qBjxh6ZznOlGIjIk";
        final static String CONSUMER_SECRET = "8Rh7wkKr3SH6Zy5r8Kf6Hnr8JVqag5cpgk63sQmYpdZuirBQqW";
        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/search/tweets.json?geocode=";

        private String getJSONByLocation(LatLng latLng) {
            String results = null;

            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                Authenticated auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(TwitterStreamURL + latLng.latitude + "%2C" + latLng.longitude + "%2C5mi&count=100");

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                }
            } catch (UnsupportedEncodingException ex) {
                Log.d("LINGOLOG", ex.getMessage());
            } catch (IllegalStateException ex1) {
                Log.d("LINGOLOG", ex1.getMessage());
            }
            return results;
        }

        // convert a JSON authentication object into an Authenticated object
        private Authenticated jsonToAuthenticated(String rawAuthorization) {
            Authenticated auth = null;
            if (rawAuthorization != null && rawAuthorization.length() > 0) {
                try {
                    Gson gson = new Gson();
                    auth = gson.fromJson(rawAuthorization, Authenticated.class);
                } catch (IllegalStateException ex) {
                    Log.d("LINGOLOG", ex.getMessage());
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
                Log.d("LINGOLOG", ex.getMessage());
            } catch (ClientProtocolException ex1) {
                Log.d("LINGOLOG", ex1.getMessage());
            } catch (IOException ex2) {
                Log.d("LINGOLOG", ex2.getMessage());
            }
            return sb.toString();
        }

        // converts a string of JSON data into a Twitter object
        private Statuses jsonToTwitter(String result) {
            Log.d("LINGOLOG","Result text: " + result);
            Statuses statuses = null;

            if (result != null && result.length() > 0) {
                try {
                    Gson gson = new Gson();
                    statuses = gson.fromJson(result, Statuses.class);
                } catch (IllegalStateException ex) {
                    // just eat the exception
                }
            }
            return statuses;
        }

        @Override
        protected String doInBackground(LatLng... latslongs) {
            String result = null;

            if (latslongs.length > 0) {
                result = getJSONByLocation(latslongs[0]);
            }

            if(result != null){
                Statuses statuses = jsonToTwitter(result);

                for (Tweet tweet : statuses.twits){
                    largeString += " " + tweet.getText();
                }
            }

            Log.d("LINGOLOG", largeString);

            if(!largeString.equals("")){
                ArrayList<String> terms = UDParse.tweetsToAnalyze(largeString, stopwords); //this is beautiful
                for(int i = 0; i < terms.size(); i++){
                    try {
                        termadefs.add(new UrbanDefinition(terms.get(i), UDParse.getTheDefinitionYouNeed(terms.get(i))));
                    }
                    catch(NullPointerException e){
                        Log.d("LINGOLOG", e.toString());
                    }
                }
                //holy shit. that was even more beautiful.
                //yooo it's 7:23am bruh...
                //we better win this stupid hackathon. i'm tired af
            }

            return "";
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            setupCards();
            moveOnToAnlysis.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
        }
    }

    public void setupCards(){
        RelativeLayout part1 = (RelativeLayout) findViewById(R.id.mapOne);
        part1.setVisibility(View.GONE);
        RelativeLayout part2 = (RelativeLayout) findViewById(R.id.cardviewOne);
        part2.setVisibility(View.VISIBLE);
        companyCards = (RecyclerView) findViewById(R.id.definition_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        companyCards.setLayoutManager(llm);
        CompanyAdapter ca = new CompanyAdapter(termadefs, getApplicationContext()); //todo order companies by price
        companyCards.setAdapter(ca);

    }

    //for the google maps api
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //for the places api
    @Override
    public void onConnected(@Nullable Bundle bundle) { Log.d("NEODEBUG", "Connection Connected");}

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("NEODEBUG", "Connection Suspended");
        Log.d("NEODEBUG", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("NEODEBUG","Connection Failed");
        Log.d("NEODEBUG", String.valueOf(connectionResult.getErrorCode()));
        String API_NOT_CONNECTED = getResources().getString(R.string.API_not_connected);
        Toast.makeText(this, API_NOT_CONNECTED,Toast.LENGTH_SHORT).show();
    }
}
