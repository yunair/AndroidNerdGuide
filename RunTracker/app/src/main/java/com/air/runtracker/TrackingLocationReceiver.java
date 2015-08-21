package com.air.runtracker;

import android.content.Context;
import android.location.Location;

public class TrackingLocationReceiver extends LocationReceiver {
    public TrackingLocationReceiver() {
    }

    @Override
    public void onLocationReceived(Context context, Location loc) {
        RunManager.getInstance(context).insertLocation(loc);
    }
}
