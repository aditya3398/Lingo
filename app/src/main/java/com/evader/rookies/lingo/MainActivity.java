package com.evader.rookies.lingo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient gac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleApiClient gac = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(LocationServices.API).addApi(Places.GEO_DATA_API).build();
        Button button = (Button)findViewById(R.id.mainlingobutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UDParse.getTheDefinitionYouNeed("lol");

            }
        });

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Toast.makeText(this, "api does not exist", Toast.LENGTH_SHORT);
        }

    }
}
