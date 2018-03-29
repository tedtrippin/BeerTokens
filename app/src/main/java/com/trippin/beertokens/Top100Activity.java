package com.trippin.beertokens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trippin.beertokens.managers.PubVisitManager;
import com.trippin.beertokens.model.PubVisit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class Top100Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top100);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Top 100 pub names");

        // Get the visited pubs and map them by top100 name
        PubVisitManager pubVisitManager = PubVisitManager.instance();
        Collection<PubVisit> myPubVisits = new ArrayList<>(pubVisitManager.getMyPubVisits());
        Map<String, Long> visitDatesMap = new HashMap<>();
        for (PubVisit pv : myPubVisits) {
            visitDatesMap.put(pv.getTopName(), pv.getDateAdded());
        }

        // Setup the list of pub name rows
        ListView top100ListView = (ListView)findViewById(R.id.top100ListView);
        String[] top100PubNames = getResources().getStringArray(R.array.top100PubNames);
        PubNameAdapter adapter = new PubNameAdapter(this, top100PubNames, visitDatesMap);
        top100ListView.setAdapter(adapter);
    }

    private class PubNameAdapter extends BaseAdapter {

        private final String[] pubNames;
        private final LayoutInflater inflater;
        private final Map<String, Long> visitDatesMap;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        PubNameAdapter(Context context, String[] pubNames, Map<String, Long> visitDatesMap) {
            this.pubNames = pubNames;
            inflater = (LayoutInflater.from(context));
            this. visitDatesMap = visitDatesMap;
        }

        @Override
        public int getCount() {
            return pubNames.length;
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

            view = inflater.inflate(R.layout.top100_list_item, null);
            TextView number = (TextView)view.findViewById(R.id.number);
            String s = (i + 1) + ".";
            number.setText(s);

            TextView name = (TextView)view.findViewById(R.id.name);
            String pubName = pubNames[i];
            name.setText(pubName);
            name.setOnClickListener(new PubClickListener(pubNames[i]));

            Long dateLong = visitDatesMap.get(pubName);
            TextView date = (TextView)view.findViewById(R.id.date);
            date.setText(dateFormat.format(new Date(dateLong)));


            return view;
        }
    }

    private class PubClickListener implements View.OnClickListener {

        private final String pubName;

        PubClickListener(String pubName) {
            this.pubName = pubName;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Top100Activity.this, NearbyMapsActivity.class);
            intent.putExtra("pubName", pubName);
            startActivity(intent);
        }
    }
}
