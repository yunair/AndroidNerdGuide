package com.air.runtracker;

import android.content.Context;

import com.air.runtracker.model.Run;

/**
 * Created by Air on 15/8/22.
 */
public class RunLoader extends DataLoader<Run>{
    private long mRunId;
    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.getInstance(getContext()).getRun(mRunId);
    }
}
