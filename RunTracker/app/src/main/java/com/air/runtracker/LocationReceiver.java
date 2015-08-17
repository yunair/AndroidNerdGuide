package com.air.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
    public static final String TAG = "LocationReceiver";

    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // If you got a Location extra, use it
        Location loc = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        if (loc != null) {
            Log.d(TAG, "Receive is not null");
            onLocationReceived(context, loc);
            return;
        }
        // If you get here, something else has happened
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
            boolean enable = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enable);
        }

    }

    protected void onLocationReceived(Context context, Location loc) {
        Log.d(TAG, this + " Got location from " + loc.getProvider() + ": "
                + loc.getLatitude() + ", " + loc.getLongitude());
    }
    protected void onProviderEnabledChanged(boolean enabled) {
        Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
    }
}
