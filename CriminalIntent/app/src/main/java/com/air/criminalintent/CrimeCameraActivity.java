package com.air.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Air on 15/7/20.
 */
public class CrimeCameraActivity extends SingleFragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //hide the window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //hide the status bar and other os-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }


}
