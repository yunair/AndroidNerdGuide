package com.air.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link CrimeCameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrimeCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME =
            "com.air.criminalintent.photo_filename";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CrimeCameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CrimeCameraFragment newInstance(String param1, String param2) {
        CrimeCameraFragment fragment = new CrimeCameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CrimeCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);
        Button takePicButton = (Button) view.findViewById(R.id.crime_camera_take_pic_button);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera != null){
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });

        mProgressContainer = view.findViewById(R.id.crime_camera_progress_container);

        initSurfaceView(view);
        return view;
    }

    private void initSurfaceView(View view) {
        mSurfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surface_view);
        SurfaceHolder holder = mSurfaceView.getHolder();
        // setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated,
        // but are required for Camera preview to work on pre-3.0 devices.
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {

                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h) {
                if (mCamera == null)
                    return;

                // The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(size.width, size.height);
                size = getOptimalPreviewSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(size.width, size.height);
                mCamera.setParameters(parameters);

                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            // Create a filename
            long currentTimeMills = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentTimeMills);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int second = calendar.get(Calendar.SECOND);
            String filename = "IMG_" + year + month + day + second + ".jpg";
            //Save the jpeg data to disk
            FileOutputStream fos = null;
            boolean isSuccess = true;

            try {
                fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                if(fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        isSuccess = false;
                    }
                }
            }

            if(isSuccess){
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
    }


    @Override
    public void onPause() {
        super.onPause();
        stopPreviewAndFreeCamera();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
