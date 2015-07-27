package com.air.nerdlauncher;


import android.app.Fragment;

public class MainActivity extends SingleFragmentActivity{


    @Override
    protected Fragment createFragment() {
        return new NerdLauncherFragment();
    }
}
