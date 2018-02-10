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

public class Top100Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top100);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Top 100 pub names");

        // Setup the list of pub name rows
        ListView top100ListView = (ListView)findViewById(R.id.top100ListView);
        String[] top100PubNames = getResources().getStringArray(R.array.top100PubNames);
        PubNameAdapter adapter = new PubNameAdapter(this, top100PubNames);
        top100ListView.setAdapter(adapter);
    }

    private class PubNameAdapter extends BaseAdapter {

        private final String[] pubNames;
        private final LayoutInflater inflater;

        PubNameAdapter(Context context, String[] pubNames) {
            this.pubNames = pubNames;
            inflater = (LayoutInflater.from(context));
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
            name.setText(pubNames[i]);
            name.setOnClickListener(new PubClickListener(pubNames[i]));
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
