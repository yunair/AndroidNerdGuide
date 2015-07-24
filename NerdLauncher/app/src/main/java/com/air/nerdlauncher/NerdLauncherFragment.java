package com.air.nerdlauncher;

import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class NerdLauncherFragment extends ListFragment {
    private static final String TAG = "NerdLauncherFragment";

    public static NerdLauncherFragment newInstance() {
        NerdLauncherFragment fragment = new NerdLauncherFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NerdLauncherFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.d(TAG, "I found " + activities.size() + " activities");
    }
}
