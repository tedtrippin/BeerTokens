package com.trippin.beertokens.managers;

import android.content.Context;
import android.util.Log;

import com.trippin.beertokens.model.PubVisit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for reading and writing PubVisit data.
 */
public class PubVisitManager {

    private final static String FILE_NAME = "mypubs.json";
    private final static String SEPARATOR = "¦";

    private final File appDir;

    public PubVisitManager (Context context) {
        appDir = context.getFilesDir();
    }

    public List<PubVisit> getMyPubVisits() {

        List<PubVisit> pubVisits = new ArrayList<>();

        File myPubVisitsDataFile = new File(appDir, FILE_NAME);
        if (!myPubVisitsDataFile.exists())
            return pubVisits;

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
                pubVisits.add(pubVisit);
            }

            // Reverse so the latest is first
            Collections.reverse(pubVisits);

        } catch (Exception ex) {
            Log.e("BeerTokens", "Couldnt read myPubs data", ex);
        }

        return pubVisits;
    }

    public void addPubVisist(PubVisit pubVisit) {

        List<PubVisit> pubVisits = new ArrayList<>();

        File myPubVisitsDataFile = new File(appDir, FILE_NAME);
        try (
                Writer out = new FileWriter(myPubVisitsDataFile, true);
        ) {
            String buf = pubVisit.getGooglePlaceId() + SEPARATOR
                + pubVisit.getName() + SEPARATOR
                + pubVisit.getDateAdded() + '\n';
            out.write(buf);
            out.flush();

        } catch (Exception ex) {
            Log.e("BeerTokens", "Unable to add PubVisit to file", ex);
        }
    }
}
