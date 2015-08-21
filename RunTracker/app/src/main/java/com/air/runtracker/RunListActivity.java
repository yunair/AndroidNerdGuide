package com.air.runtracker;

import android.app.Fragment;

/**
 * Created by Air on 15/8/21.
 */
public class RunListActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }
}
