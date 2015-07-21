package com.air.criminalintent;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.air.criminalintent.util.PictureUtils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends DialogFragment {
    public static final String EXTRA_IMAGE_PATH =
            "com.air.criminalintent.image_path";
    public static final String EXTRA_IMAGE_ROTATION =
            "com.air.criminalintent.image_rotation";
    private String imagePath;
    private int rotation;
    private ImageView mImageView;


    public static ImageFragment newInstance(final String imagePath, final int rotation) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
        args.putSerializable(EXTRA_IMAGE_ROTATION, rotation);

        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString(EXTRA_IMAGE_PATH);
            rotation = getArguments().getInt(EXTRA_IMAGE_ROTATION, Surface.ROTATION_90);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mImageView = new ImageView(getActivity());
        BitmapDrawable drawable = PictureUtils.getScaledDrawable(getActivity(), imagePath);

        //Surface.ROTATION_90为正常横屏
        if(rotation == Surface.ROTATION_0) {
            mImageView.setRotation(90);
        }else if(rotation == Surface.ROTATION_270){
            mImageView.setRotation(180);
        }else if(rotation == Surface.ROTATION_180){
            mImageView.setRotation(270);
        }

        mImageView.setImageDrawable(drawable);
        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}
