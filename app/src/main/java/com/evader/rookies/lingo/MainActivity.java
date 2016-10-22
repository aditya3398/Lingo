package com.evader.rookies.lingo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //UI Stuff
    private EditText autocomplete_address;
    private AutocompleteAdapter autocompleteAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
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
                .build();

        //finds the user's current location
        LatLng currentlocation;
        LatLngBounds currentLocationBounds;
        if (getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED ||
                getPackageManager().checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            currentlocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            currentLocationBounds = new LatLngBounds(new LatLng(mLastLocation.getLatitude()-5, mLastLocation.getLongitude()-5),
                                                    new LatLng(mLastLocation.getLatitude()+5, mLastLocation.getLongitude()+5));
        }
        else {
            currentlocation = new LatLng(42.186133, -71.309194); //defaults to the CVS in Medfield
        }
        mGoogleMap = mMapView.getMap();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 6));


        autocomplete_address = (EditText) findViewById(R.id.autocomplete_textview);
        autocompleteAdapter = new AutocompleteAdapter(this, R.layout.search_row, mGoogleApiClient, NEWENGLANDBOUNDS, null);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(autocompleteAdapter);
        recyclerView.setVisibility(View.GONE);

        autocomplete_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                recyclerView.setVisibility(View.VISIBLE);
                clearEditView.setVisibility(View.VISIBLE);
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    autocompleteAdapter.getFilter().filter(s.toString());
                }else if(!mGoogleApiClient.isConnected()){
                    String API_NOT_CONNECTED = getResources().getString(R.string.API_not_connected);
                    String GENERIC_ERROR = getResources().getString(R.string.generic_error);
                    Toast.makeText(context, API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.d("NEODEBUG", GENERIC_ERROR);
                }
                else{
                    clearEditView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final AutocompleteAdapter.PlaceAutocomplete item = autocompleteAdapter.getItem(position);
                        final String placeId = String.valueOf(item.placeId);
                        Log.d("NEODEBUG", "Autocomplete item selected: " + item.description);
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
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 13));
                                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                                    autocomplete_address.setText(item.description);
                                    recyclerView.setVisibility(View.GONE);
                                    saveUrl(lat, lng);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 3000ms
                                            ((MainActivity)getActivity()).movePagerAdapter(1);
                                        }
                                    }, 3000);
                                }else {
                                    String GENERIC_ERROR = getResources().getString(R.string.generic_error);
                                    Toast.makeText(MainActivity.this, GENERIC_ERROR, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.d("NEODEBUG", "Clicked: " + item.description);
                        Log.d("NEODEBUG", "Called getPlaceById to get Place details for " + item.placeId);
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
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

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
