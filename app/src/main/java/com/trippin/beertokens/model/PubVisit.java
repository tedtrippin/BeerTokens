package com.trippin.beertokens.model;

import java.util.Date;

/**
 * Represents a pub and time it was visited.
 */
public class PubVisit {

    private String googlePlaceId;
    private String name; // Actual pub name, eg. 'The Old Royal Oak'
    private String topName; // Name, as in the top 100, eg. 'Royal Oak'
    private long dateAdded;

    public PubVisit(String googlePlaceId, String name, String topName, long dateAdded) {
        this.googlePlaceId = googlePlaceId;
        this.name = name;
        this.topName = topName;
        this.dateAdded = dateAdded;
    }

    public String getGooglePlaceId() {
       return googlePlaceId;
    }

    public String getName() {
        return name;
    }

    public String getTopName() {
        return topName;
    }

    public long getDateAdded() {
        return dateAdded;
    }
}
