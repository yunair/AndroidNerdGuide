package com.air.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Air on 15/7/31.
 */
public class SharedPreferencesUtil {
    public static SharedPreferences.Editor  getSharedPreferencesEditor(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
    }

    public static void commitString(Context context, String key, String str){
        getSharedPreferencesEditor(context).putString(key, str).commit();
    }

    public static void commitBoolean(Context context, String key, boolean is){
        getSharedPreferencesEditor(context).putBoolean(key, is).commit();
    }

}
