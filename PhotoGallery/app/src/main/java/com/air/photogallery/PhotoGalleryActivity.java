package com.air.photogallery;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }

    /**
     * One important thing to note about onNewIntent(Intent):
     * if you need the new intent value, make sure to save it someplace.
     * The value you get from getIntent() will have the old intent, not the new one.
     * This is because getIntent() is intended to return the intent that started this activity,
     * not the most recent intent it received.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment)
                getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            SharedPreferencesUtil.commitString(this, FlickrFetch.PREF_SEARCH_QUERY, query);
        }
        fragment.updateItems();
    }
}
