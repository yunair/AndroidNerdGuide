package com.air.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.util.Log;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class PollService extends IntentService {
    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 1000 * 5;
//            * 60; // 5 minute
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String ACTION_SHOW_NOTIFICATION =
            "com.air.android.photogallery.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE =
            "com.air.android.photogallery.PRIVATE";



    public PollService() {
        super("PollService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);
        if (intent != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
            if(!isNetworkAvailable)
                return;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String query = prefs.getString(FlickrFetch.PREF_SEARCH_QUERY, null);
            String lastResultId = prefs.getString(FlickrFetch.PREF_LAST_RESULT_ID, null);

            List<GalleryItem> items;
            if (query != null) {
                items = new FlickrFetch().search(query);
            } else {
                items = new FlickrFetch().fetchItems();
            }
            if (items.size() == 0)
                return;
            String resultId = items.get(0).getId();
            if (!resultId.equals(lastResultId)) {
                Log.i(TAG, "Got a new result: " + resultId);
                sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);
                notifyUserByNotification();
            } else {
                Log.i(TAG, "Got an old result: " + resultId);
            }
            prefs.edit()
                    .putString(FlickrFetch.PREF_LAST_RESULT_ID, resultId)
                    .apply();

        }
    }

    private void notifyUserByNotification() {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, PhotoGalleryActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setTicker(getStringByResources(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(getStringByResources(R.string.new_pictures_title))
                .setContentText(getStringByResources(R.string.new_pictures_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // The integer parameter you pass in is an identifier for your notification.
        // It should be unique across your application.
        // If you post a second notification with this same ID,
        // it will replace the last notification you posted with that ID.
//        nm.notify(1, notification);

        showBackgroundNotification(0, notification);
    }

    private String getStringByResources(@StringRes int str) {
        return getResources().getString(str);
    }


    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if(isOn){
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
        }else {
            am.cancel(pi);
            pi.cancel();
        }

        SharedPreferencesUtil.commitBoolean(context, PREF_IS_ALARM_ON, isOn);
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }

}
