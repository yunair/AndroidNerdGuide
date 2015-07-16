package com.air.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Air on 15/5/15.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
