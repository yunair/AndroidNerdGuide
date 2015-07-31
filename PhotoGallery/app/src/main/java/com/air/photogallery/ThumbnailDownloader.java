package com.air.photogallery;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Air on 15/7/29.
 */
// use Token to identify each download
public class ThumbnailDownloader<Token> extends HandlerThread{
    public static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mHandler;
    Map<Token, String> requestMap =
            Collections.synchronizedMap(new HashMap<Token, String>());

    private Handler mResponseHandler;
    Listener<Token> mTokenListener;

    LruCache<Token, Bitmap> mTokenBitmapLruCache;

    public interface Listener<Token>{
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        this.mTokenListener = listener;
    }



    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null)
                return;

            final Bitmap tmpBitmap = getBitmapFromMemCache(token);
            final Bitmap bitmap;
            if(tmpBitmap == null){
                byte[] bitmapBytes = new FlickrFetch().getUrlBytes(url);
                bitmap = BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                addBitmapToMemoryCache(token, bitmap);
                mResponseHandler.post(new Runnable() {
                    public void run() {
                        if (requestMap.get(token) == null)
                            return;
                        requestMap.remove(token);
                        mTokenListener.onThumbnailDownloaded(token, bitmap);
                    }
                });
                Log.i(TAG, "Bitmap created");

            }else {
                bitmap = tmpBitmap;
                mResponseHandler.post(new Runnable() {
                    public void run() {
                        mTokenListener.onThumbnailDownloaded(token, bitmap);
                    }
                });
                Log.i(TAG, "Bitmap get");
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    @SuppressLint("HandlerLeaks")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token)msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                } }
        };
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;


        mTokenBitmapLruCache = new LruCache<>(cacheSize);
        this.mResponseHandler = responseHandler;
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

    public void addBitmapToMemoryCache(Token key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mTokenBitmapLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(Token key) {
        return mTokenBitmapLruCache.get(key);
    }


}
