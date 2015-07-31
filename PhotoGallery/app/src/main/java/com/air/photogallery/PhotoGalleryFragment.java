package com.air.photogallery;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Air on 15/7/29.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private GridView mGridView;
    List<GalleryItem> mItems;

    ThumbnailDownloader<ImageView> mThumbnailThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        mThumbnailThread = new ThumbnailDownloader<>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.grid_view);
        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            Activity activity = getActivity();
            if(activity == null)
                return new ArrayList<GalleryItem>();

            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetch.PREF_SEARCH_QUERY, null);

            if (query != null) {
                return new FlickrFetch().search(query);
            } else {
                return new FlickrFetch().fetchItems();
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView)searchItem.getActionView();
        // Get the data from our searchable.xml as a SearchableInfo
        /*SearchManager searchManager = (SearchManager)getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = getActivity().getComponentName();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
        searchView.setSearchableInfo(searchInfo);*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                SharedPreferencesUtil.commitString(getActivity(), FlickrFetch.PREF_SEARCH_QUERY, null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter(List<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.gallery_item, parent, false);
                holder = new ViewHolder();
                holder.imageView = (ImageView)convertView
                        .findViewById(R.id.gallery_item_image_view);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            final GalleryItem item = mItems.get(position);
            mThumbnailThread.queueThumbnail(holder.imageView, item.getUrl());

            holder.imageView.setImageResource(R.drawable.brian_up_close);
            convertView.setTag(holder);
            return convertView;
        }

        class ViewHolder{
            ImageView imageView;
        }
    }

}
