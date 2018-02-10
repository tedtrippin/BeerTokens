package com.trippin.beertokens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trippin.beertokens.managers.PubVisitManager;
import com.trippin.beertokens.model.PubVisit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPubsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_pubs);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Visited Pubs");

        try {
            PubVisitManager pubVisitManager = PubVisitManager.instance();
            List<PubVisit> myPubVisits = new ArrayList<>(pubVisitManager.getMyPubVisits());

            // Sort, first in list = last visited
            Collections.sort(myPubVisits, new Comparator<PubVisit>() {
                @Override
                public int compare(PubVisit pv1, PubVisit pv2) {
                    return pv1.getDateAdded() > pv2.getDateAdded() ? 0 : 1;
                }
            });

            ListView myPubsListView = (ListView) findViewById(R.id.myPubsListView);
            PubVisitAdapter adapter = new PubVisitAdapter(this, myPubVisits);
            myPubsListView.setAdapter(adapter);
        } catch (Exception ex) {
            Log.e("BeerTokens", "Couldnt open MyPubsActivity", ex);
            Toast.makeText(this, "Couldnt open MyPubsActivity: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class PubVisitAdapter extends BaseAdapter {

        private final List<PubVisit> pubVisits;
        private final LayoutInflater inflater;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        PubVisitAdapter(Context context, List<PubVisit> pubVisits) {
            this.pubVisits = pubVisits;
            inflater = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return pubVisits.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.pub_list_item, null);
            PubVisit pubVisit = pubVisits.get(i);

            TextView name = (TextView)view.findViewById(R.id.name);
            name.setText(pubVisit.getName());

            TextView date = (TextView)view.findViewById(R.id.date);
            date.setText(dateFormat.format(new Date(pubVisit.getDateAdded())));

            return view;
        }
    }

}
