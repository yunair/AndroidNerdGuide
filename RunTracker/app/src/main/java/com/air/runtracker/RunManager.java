package com.air.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Air on 15/8/17.
 */
public class RunManager {
    private static final String TAG = "RunManager";
    public static final String ACTION_LOCATION =
            "com.air.runtracker.ACTION_LOCATION";
    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    // The private constructor forces users to use RunManager.get(Context)
    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public static RunManager getInstance(Context context){
        if(sRunManager == null) {
            synchronized(RunManager.class){
                if(sRunManager == null) {
                    // Use the application context to avoid leaking activities
                    sRunManager = new RunManager(context.getApplicationContext());
                }
            }
        }

        return sRunManager;
    }


    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }


    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        // Get the last known location and broadcast it if you have one
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
        if(lastKnownLocation != null) {
            lastKnownLocation.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnownLocation);
        }
        // Start updates from the location manager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }
    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }
    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    private void broadcastLocation(Location location) {
        Intent intent = new Intent(ACTION_LOCATION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(intent);
    }

}
