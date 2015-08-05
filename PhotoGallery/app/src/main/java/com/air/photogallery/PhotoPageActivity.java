package com.air.photogallery;

import android.app.Fragment;

/**
 * Created by Air on 15/8/5.
 */
public class PhotoPageActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
