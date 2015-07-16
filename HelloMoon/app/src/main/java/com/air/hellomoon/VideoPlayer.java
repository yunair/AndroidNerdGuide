package com.air.hellomoon;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;

/**
 * Created by Air on 15/7/9.
 */
public class VideoPlayer {
    private VideoView mVideoView;

    public VideoPlayer(VideoView videoView) {
        mVideoView = videoView;
    }

    public void play(){
        final Context context = mVideoView.getContext();
        final int resId = R.raw.apollo_17_stroll;
        Uri resourceUri = Uri.parse("android.resource://" +
                "com.air.hellomoon/" + R.raw.apollo_17_stroll);
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resId) +
                '/' + context.getResources().getResourceTypeName(resId) +
                '/' + context.getResources().getResourceEntryName(resId));
        Log.d("Video Player", "Uri : " + uri);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
    }

    public void pause(){
        mVideoView.pause();
    }

    public void stop(){
        if(mVideoView != null){
            mVideoView.stopPlayback();
        }
    }
}
