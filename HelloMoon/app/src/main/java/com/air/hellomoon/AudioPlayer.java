package com.air.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Air on 15/7/9.
 */
public class AudioPlayer {
    private MediaPlayer mMediaPlayer;

    public void stop(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void pause(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    public boolean isPlaying(){
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void play(Context context) {
        if(mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(context, R.raw.one_small_step);
           /* Uri resourceUri = Uri.parse("android.resource://" +
                    "com.air.hellomoon/raw/apollo_17_stroll");
            mMediaPlayer = MediaPlayer.create(context, resourceUri, mHolder);*/
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
        mMediaPlayer.start();

    }
}
