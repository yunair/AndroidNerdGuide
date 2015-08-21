package com.air.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.air.runtracker.model.Run;

/**
 * Created by Air on 15/8/17.
 */
public class RunManager {
    private static final String TAG = "RunManager";
    public static final String ACTION_LOCATION =
            "com.air.runtracker.ACTION_LOCATION";

    private static final String PREFS_FILE = "runs";
    private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";


    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;

    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;

    // The private constructor forces users to use RunManager.get(Context)
    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext
                .getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(appContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
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

    public Run startNewRun(){
        // Insert a run into the db
        Run run = insertRun();
        // Start tracking the run
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run) {
        // Keep the ID
        mCurrentRunId = run.getId();
        // Store it in shared preferences
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).apply();
        // Start location updates
        startLocationUpdates();
    }


    public void stopRun() {
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).apply();
    }

    private Run insertRun() {
        Run run = new Run();
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public void insertLocation(Location loc){
        if(mCurrentRunId != -1) {
            mHelper.insertLocation(mCurrentRunId, loc);
        }else {
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }

    public RunDatabaseHelper.RunCursor queryRuns() {
        return mHelper.queryRuns();
    }

    public Run getRun(long id){
        Run run = null;
        RunDatabaseHelper.RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        // If you got a row, get a run
        if(!cursor.isAfterLast()){
            run = cursor.getRun();
        }
        cursor.close();

        return run;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentRunId;
    }

    public Location getLastLocationForRun(long runId){
        Location location = null;
        RunDatabaseHelper.LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            location = cursor.getLocation();
        }
        cursor.close();

        return location;
    }

}
