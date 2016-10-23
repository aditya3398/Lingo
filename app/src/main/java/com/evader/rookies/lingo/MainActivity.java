package com.evader.rookies.lingo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
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
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    //UI Stuff
    private EditText autocomplete_address;
    private AutoCompleterAdapter autoCompleterAdapter;
    private RecyclerView recyclerView;
    private ImageView clearEditView;

    //Maps API stuff
    protected GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch(NullPointerException e){
            Log.d("SHIT", e.toString());
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
        if (getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED ||
                getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            currentlocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            currentLocationBounds = new LatLngBounds(new LatLng(mLastLocation.getLatitude()-3, mLastLocation.getLongitude()-3),
                                                    new LatLng(mLastLocation.getLatitude()+3, mLastLocation.getLongitude()+3));
        }
        else {
            currentlocation = new LatLng(42.186133, -71.309194); //defaults to the CVS in Medfield
            currentLocationBounds = new LatLngBounds(new LatLng(42.186133-3, -71.309194-3),
                    new LatLng(42.186133+3, -71.309194+3));
        }
        mGoogleMap = mMapView.getMap();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 15));

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
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                                    autocomplete_address.setText(item.description);
                                    recyclerView.setVisibility(View.GONE);
                                    //TODO should we just move on to the next activit here?
                                    final Handler handler = new Handler();
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
