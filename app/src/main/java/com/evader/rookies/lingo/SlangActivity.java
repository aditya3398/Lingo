package com.evader.rookies.lingo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

public class SlangActivity extends AppCompatActivity {

    ArrayList<UrbanDefinition> termadefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slang);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        termadefs = getIntent().getParcelableArrayListExtra("urban_defs");

        for (int i = 0; i < termadefs.size(); i++){
            Log.d("LINGOLOG", i + ") " + termadefs.get(i).getTerm() + ": " + termadefs.get(i).getDefinition());
        }

    }
}
