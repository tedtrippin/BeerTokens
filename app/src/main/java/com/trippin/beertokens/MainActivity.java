package com.trippin.beertokens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startTop100(View v) {
        Intent intent = new Intent(this, Top100Activity.class);
        startActivity(intent);
    }

    public void startMyPubs(View view) {
        Intent intent = new Intent(this, MyPubsActivity.class);
        startActivity(intent);
    }

    public void startPubNameHistory(View view) {

    }

    public void startFindPubs(View view) {
        Intent intent = new Intent(this, NearbyMapsActivity.class);
        intent.putExtra("pubName", "");
        startActivity(intent);
    }
}
