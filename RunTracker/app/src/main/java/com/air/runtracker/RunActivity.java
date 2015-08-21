package com.air.runtracker;

import android.app.Fragment;

/**
 * Created by Air on 15/8/17.
 */
public class RunActivity extends SingleFragmentActivity {
    /** A key for passing a run ID as a long */
    public static final String EXTRA_RUN_ID =
            "com.air.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId == -1) {
            return new RunFragment();
        }else {
            return RunFragment.newInstance(runId);
        }
    }
}
