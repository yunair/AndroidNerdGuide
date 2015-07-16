package com.air.hellomoon;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

/**
 * Created by Air on 15/7/7.
 */
public class HelloMoonFragment extends Fragment{
    private Button mPlayButton;
    private Button mStopButton;
    private AudioPlayer mAudioPlayer;
    private VideoPlayer mVideoPlayer;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_hello_moon, container, false);
        initView(v);
        return v;
    }

    private void initView(final View v) {
        mPlayButton = (Button)v.findViewById(R.id.hellomoon_playButton);
        mStopButton = (Button)v.findViewById(R.id.hellomoon_stopButton);
        mVideoView = (VideoView) v.findViewById(R.id.hellomoon_videoview);
        mVideoPlayer = new VideoPlayer(mVideoView);
        mAudioPlayer = new AudioPlayer();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAudioPlayer.isPlaying()){
                    mAudioPlayer.pause();
//                    mVideoPlayer.pause();
                    mPlayButton.setText("Play");
                } else {
//                    mVideoPlayer.play();
                    mAudioPlayer.play(getActivity());
                    mPlayButton.setText("Pause");

                }
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioPlayer.stop();
//                mVideoPlayer.stop();
                mPlayButton.setText("Play");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.stop();
    }
}
