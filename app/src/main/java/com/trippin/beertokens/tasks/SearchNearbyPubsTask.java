package com.trippin.beertokens.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Async task that searches for pubs nearby using google play web service.
 */
public class SearchNearbyPubsTask extends AsyncTask<Void, Void, List<Place>> {

    private static final String googlePlacesKey = "AIzaSyA-wc7EimYKZyszOn3L7YRzUJqrGqV_9F4";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_SEARCH = "/search";
    private static final String OUT_JSON = "/json";

    private final String keyword;
    private final double lat;
    private final double lng;
    private final int radius;
    private final GoogleMap map;
    private final Map<String, Place> markerMap;

    public SearchNearbyPubsTask (String keyword, double lat, double lng, int radius, GoogleMap map, Map<String, Place> markerMap) {
        this.keyword = keyword;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.map = map;
        this.markerMap = markerMap;
    }

    @Override
    protected List<Place> doInBackground(Void... voids) {

        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE)
                .append(TYPE_SEARCH)
                .append(OUT_JSON)
                .append("?sensor=false")
                .append("&key=").append(googlePlacesKey)
                .append("&type=bar")
                .append("&keyword=").append(URLEncoder.encode(keyword, "utf8"))
                .append("&location=").append(String.valueOf(lat)).append(",").append(String.valueOf(lng))
                .append("&radius=").append(String.valueOf(radius));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new PlaceImpl(predsJsonArray.getJSONObject(i)));
            }
        } catch (Exception ex) {
            Log.e("BeerTokens", "Couldn't perform search", ex);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return resultList;
    }

    @Override
    protected void onPostExecute(List<Place> pubs) {

        for (Place pub : pubs) {
            MarkerOptions markerOptions = new MarkerOptions()
                .position(pub.getLatLng())
                .title(pub.getName().toString());
            Marker marker = map.addMarker(markerOptions);
            markerMap.put(marker.getId(), pub);
        }
    }

    public class PlaceImpl implements Place, Serializable {

        private String id;
        private String placeId;
        private CharSequence name;
        private LatLng latLng;
        private String address;

        private PlaceImpl(JSONObject json)
            throws JSONException {

            id = json.getString("id");
            placeId = json.getString("place_id");
            name = json.getString("name");
            address = json.getString("vicinity");

            JSONObject location = json.getJSONObject("geometry").getJSONObject("location");
            latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
        }

        @Override
        public String getId() {
            return id;
        }

        public String getPlaceId() {
            return placeId;
        }

        @Override
        public List<Integer> getPlaceTypes() {
            return null;
        }

        @Override
        public CharSequence getAddress() {
            return address;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public CharSequence getName() {
            return name;
        }

        @Override
        public LatLng getLatLng() {
            return latLng;
        }

        @Override
        public LatLngBounds getViewport() {
            return null;
        }

        @Override
        public Uri getWebsiteUri() {
            return null;
        }

        @Override
        public CharSequence getPhoneNumber() {
            return null;
        }

        @Override
        public float getRating() {
            return 0;
        }

        @Override
        public int getPriceLevel() {
            return 0;
        }

        @Override
        public CharSequence getAttributions() {
            return null;
        }

        @Override
        public boolean isDataValid() {
            return true;
        }

        @Override
        public Place freeze() {
            return null;
        }
    }
}



