package com.air.criminalintent.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Air on 15/7/20.
 */
public class Photo {
    private static final String JSON_FILENAME = "filename";
    private static final String JSON_ROTATION = "rotation";
    private String mFilename;
    private int rotation;


    /** Create a Photo representing an existing file on disk */

    public Photo(String filename, int rotation) {
        this.rotation = rotation;
        mFilename = filename;
    }



    public Photo(JSONObject json) throws JSONException {
        mFilename = json.getString(JSON_FILENAME);
        rotation = json.getInt(JSON_ROTATION);
    }
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFilename);
        json.put(JSON_ROTATION, rotation);
        return json;
    }
    public String getFilename() {
        return mFilename;
    }
    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
