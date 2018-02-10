package com.trippin.beertokens.managers;

import android.content.Context;
import android.util.Log;

import com.trippin.beertokens.model.PubVisit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for reading and writing PubVisit data.
 */
public class PubVisitManager {

    private static PubVisitManager instance;

    private final static String FILE_NAME = "mypubs.json";
    private final static String SEPARATOR = "¦";
    private final File myPubVisitsDataFile;
    private final Map<String, PubVisit> pubVisits = new HashMap<>(); // Key = Google place ID

    public static void initialise(Context context) {
        instance = new PubVisitManager(context);
    }

    public static PubVisitManager instance() {
        return instance;
    }

    private PubVisitManager (Context context) {
        myPubVisitsDataFile = new File(context.getFilesDir(), FILE_NAME);
        load();
    }

    public Collection<PubVisit> getMyPubVisits() {
        return pubVisits.values();
    }

    private void load() {

        if (!myPubVisitsDataFile.exists()) {
            try {
                myPubVisitsDataFile.createNewFile();
            } catch (IOException ex) {
                Log.e("BeerTokens", "Couldn't create visist file", ex);
            }
            return;
        }

        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(myPubVisitsDataFile)));
        ) {
            String buf;
            while ((buf = in.readLine()) != null) {
                String[] ss = buf.split(SEPARATOR);
                String googlePlaceId = ss[0];
                String name = ss[1];
                String topName = ss[2];
                long timeStamp = Long.parseLong(ss[3]);

                PubVisit pubVisit = new PubVisit(googlePlaceId, name, topName, timeStamp);
                pubVisits.put(googlePlaceId, pubVisit);
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            // This here whilst testing so it can recover when adding fields to PubVisit's
            myPubVisitsDataFile.delete();

        } catch (Exception ex) {
            Log.e("BeerTokens", "Couldn't read myPubs data", ex);
        }
    }

    public void addPubVisit(PubVisit pubVisit) {

        // Add/overwrite PubVisit
        pubVisits.put(pubVisit.getGooglePlaceId(), pubVisit);

        try (
                Writer out = new FileWriter(myPubVisitsDataFile, false)
        ) {
            for (PubVisit p : getMyPubVisits()) {
                String buf = p.getGooglePlaceId() + SEPARATOR
                        + p.getName() + SEPARATOR
                        + p.getTopName() + SEPARATOR
                        + p.getDateAdded() + '\n';
                out.write(buf);
            }
            out.flush();

        } catch (Exception ex) {
            Log.e("BeerTokens", "Unable to write PubVisit's to file", ex);
        }
    }
}
