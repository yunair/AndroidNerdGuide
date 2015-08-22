package com.air.runtracker;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by Air on 15/8/22.
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {
    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if(isStarted()) {
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        if(mData != null) {
            deliverResult(mData);
        }else {
            forceLoad();
        }
    }
}
