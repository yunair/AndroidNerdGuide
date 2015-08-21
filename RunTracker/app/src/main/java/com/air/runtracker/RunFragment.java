package com.air.runtracker;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.air.runtracker.model.Run;

/**
 * Created by Air on 15/8/17.
 */
public class RunFragment extends Fragment {
    private static final String TAG = "RunFragment";
    private static final String ARG_RUN_ID = "RUN_ID";
    
    private Button mStartButton, mStopButton;
    private TextView mStartedTextView, mLatitudeTextView,
            mLongitudeTextView, mAltitudeTextView, mDurationTextView;

    private RunManager mRunManager;
    private Run mRun;
    private Location mLastLocation;

    public static RunFragment newInstance(long runId) {

        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);

        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.getInstance(getActivity());
        // Check for a Run ID as an argument, and find the run
        Bundle args = getArguments();
        if(args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if(runId != -1) {
                mRun = mRunManager.getRun(runId);
                mLastLocation = mRunManager.getLastLocationForRun(runId);
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);
        mStartedTextView = (TextView)view.findViewById(R.id.run_startedTextView);
        mLatitudeTextView = (TextView)view.findViewById(R.id.run_latitudeTextView);
        mLongitudeTextView = (TextView)view.findViewById(R.id.run_longitudeTextView);
        mAltitudeTextView = (TextView)view.findViewById(R.id.run_altitudeTextView);
        mDurationTextView = (TextView)view.findViewById(R.id.run_durationTextView);
        mStartButton = (Button)view.findViewById(R.id.run_startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRunManager.startLocationUpdates();
                if(mRun == null) {
                    mRun = mRunManager.startNewRun();
                }else {
                    mRunManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });
        mStopButton = (Button)view.findViewById(R.id.run_stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRunManager.stopLocationUpdates();
                mRunManager.stopRun();
                updateUI();
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mReceiver,
                new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    private void updateUI() {
        boolean started = mRunManager.isTrackingRun();
        boolean trackingThisRun = mRunManager.isTrackingRun(mRun);

        if (mRun != null)
            mStartedTextView.setText(mRun.getStartDate().toString());
        int durationSeconds = 0;
        if (mRun != null && mLastLocation != null) {
            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
            mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
            mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
        }
        mDurationTextView.setText(Run.formatDuration(durationSeconds));

        mStartButton.setEnabled(!started);
        mStopButton.setEnabled(started & trackingThisRun);
    }

    private BroadcastReceiver mReceiver = new LocationReceiver(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if (!mRunManager.isTrackingRun(mRun))
                return;
            Log.d(TAG, "Last Location");
            mLastLocation = loc;
            if(isVisible()){
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            final int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
//            super.onProviderEnabledChanged(enabled);
        }
    };
}
