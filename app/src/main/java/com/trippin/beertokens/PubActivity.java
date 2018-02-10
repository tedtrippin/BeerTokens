package com.trippin.beertokens;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.trippin.beertokens.NearbyMapsActivity;
import com.trippin.beertokens.R;
import com.trippin.beertokens.managers.PubVisitManager;
import com.trippin.beertokens.model.PubVisit;

public class PubActivity extends AppCompatActivity {

    private static final int RADIUS = 20;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 101;

    private String pubName;
    private String pubTopName;
    private String pubId;
    private String pubPlaceId;
    private String pubAddress;
    private String pubPhoneNumber;
    private String pubWebsiteUrl;
    private float pubRating;
    private double currentLattitude;
    private double currentLongitude;
    private double pubLattitude;
    private double pubLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub);

        // Get the pub name from the parameters passed in on the intent
        pubName = getIntent().getExtras().getString("pubName");
        pubTopName = getIntent().getExtras().getString("pubTopName");
        pubId = getIntent().getExtras().getString("pubId");
        pubPlaceId = getIntent().getExtras().getString("pubPlaceId");
        pubAddress = getIntent().getExtras().getString("pubAddress");
        pubPhoneNumber = getIntent().getExtras().getString("pubPhoneNumber");
        pubRating = getIntent().getExtras().getFloat("pubRating");
        pubWebsiteUrl = getIntent().getExtras().getString("pubWebsiteUrl");
        currentLattitude = getIntent().getExtras().getDouble("currentLattitude");
        currentLongitude = getIntent().getExtras().getDouble("currentLongitude");
        pubLattitude = getIntent().getExtras().getDouble("pubLattitude");
        pubLongitude = getIntent().getExtras().getDouble("pubLongitude");

        getPhoto();

        ((TextView) findViewById(R.id.pubAddress)).setText(pubAddress);
        ((TextView) findViewById(R.id.pubWebsiteUrl)).setText(pubWebsiteUrl);
        ((TextView) findViewById(R.id.pubPhoneNumber)).setText(pubPhoneNumber);
        ((TextView) findViewById(R.id.pubRating)).setText(Float.toString(pubRating));

        // Set the toolbar as the "action bar"
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(pubName);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            NavUtils.navigateUpFromSameTask(PubActivity.this);
            }
        });

        setupPubSign();

        setupAddButton();
    }

    private void getPhoto() {

        try {
            final GeoDataClient geoDataClient = Places.getGeoDataClient(this, null);
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = geoDataClient.getPlacePhotos(pubPlaceId);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    PlacePhotoMetadataResponse photos = task.getResult();
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    if (photoMetadataBuffer.getCount() > 0) {
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                ImageView pictureView = (ImageView) findViewById(R.id.pubPicture);
                                pictureView.setImageBitmap(photo.getBitmap());
                            }
                        });
                    } else {
                        ImageView pictureView = (ImageView) findViewById(R.id.pubPicture);
                        Drawable noContent = getResources().getDrawable(R.drawable.ic_no_image);
                        pictureView.setImageDrawable(noContent);
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, "Couldnt get photo", Toast.LENGTH_SHORT);
        }
    }

    public void addToVisited(View v) {

        final PubVisitManager pubVisitManager = new PubVisitManager(this);
        PubVisit pubVisit = new PubVisit(pubId, pubName, pubTopName, System.currentTimeMillis());
        pubVisitManager.addPubVisist(pubVisit);

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

/*
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

                alertBuilder.setMessage("Add " + pub.getName() + "?")
                    .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                    .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PubVisit pubVisit = new PubVisit(pub.getId(), pub.getName().toString(), System.currentTimeMillis());
                                pubVisitManager.addPubVisist(pubVisit);
                                dialog.cancel();
                            }
                        }
                    );

                alertBuilder.create().show();
*/

    }

    private void setupPubSign() {

        ImageView pubSignView = (ImageView) findViewById(R.id.pubSign);
        if (pubTopName.isEmpty()) {
            pubSignView.setVisibility(View.GONE);
        } else {
            Resources resources = getResources();
            String fileName = "ps_" + pubTopName.toLowerCase().replace(' ', '_');
            int id = resources.getIdentifier(fileName, "drawable", getApplicationContext().getPackageName());
            try {
                Drawable pubSign = resources.getDrawable(id, null);
                pubSignView.setImageDrawable(pubSign);
            } catch (Resources.NotFoundException ex) {
                Toast.makeText(this, "Couldn't find pub sign", Toast.LENGTH_SHORT);
            }
        }
    }

    private void setupAddButton() {

        double deltaX = (pubLattitude - currentLattitude);
        double deltaY = (pubLongitude - currentLongitude);
        double radius = Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
        boolean inVicinity = radius < 0.02;

        Button addPubButton = (Button) findViewById(R.id.addPubButton);
        addPubButton.setEnabled(inVicinity);
        addPubButton.setText(inVicinity
            ? "Add " + pubTopName
            : "Too far away to add");
    }
}
