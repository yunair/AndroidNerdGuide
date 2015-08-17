package com.air.runtracker;

import android.app.Fragment;

/**
 * Created by Air on 15/8/17.
 */
public class RunActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RunFragment();
    }
}
