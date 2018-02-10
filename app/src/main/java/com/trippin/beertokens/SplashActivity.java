package com.trippin.beertokens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trippin.beertokens.managers.PubVisitManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // Initialise managers
        PubVisitManager.initialise(getApplicationContext());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
