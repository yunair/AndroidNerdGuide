package com.air.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Air on 15/8/22.
 */
public class LastLocationLoader extends DataLoader<Location>{
    private long mRunId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.getInstance(getContext()).getLastLocationForRun(mRunId);
    }
}
