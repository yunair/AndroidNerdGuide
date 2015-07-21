package com.air.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.air.criminalintent.model.Crime;
import com.air.criminalintent.model.CrimeLab;
import com.air.criminalintent.model.Photo;
import com.air.criminalintent.util.PictureUtils;

import java.util.Date;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME_ID = "com.air.criminalintent.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int RESULT_DATE = 10;
    private static final int RESULT_TIME = 100;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public CrimeFragment() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        initTitleField(v);
        initDateButton(v);
        initSolvedCheckBox(v);
        initPhotoButton(v);
        initPhotoView(v);

        return v;
    }

    private void initPhotoView(View v) {
        mPhotoView = (ImageView) v.findViewById(R.id.crime_image_view);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo p = mCrime.getPhoto();
                if(p == null) return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                ImageFragment dialog = ImageFragment.newInstance(getPhotoPath(p), p.getRotation());
//                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_IMAGE);
            }
        });

        registerForContextMenu(mPhotoView);
    }

    private void initPhotoButton(View v) {
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_image_button);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                && Camera.getNumberOfCameras() > 0;
        if(!hasCamera){
            mPhotoButton.setEnabled(false);
        }
    }

    private void initSolvedCheckBox(View v) {
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });
    }

    private void initTitleField(View v) {
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initDateButton(View v) {
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
    }

    private void updateDate() {
        CharSequence formatDate = DateFormat.format("yyyy/MM/dd:hh:mm:ss", mCrime.getDate());
        mDateButton.setText(formatDate);
    }

    private void showPhotos(){
        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();

        BitmapDrawable bitmapDrawable;
        if(p != null){
            String path = getPhotoPath(p);
            bitmapDrawable = PictureUtils.getScaledDrawable(getActivity(), path);
            //Surface.ROTATION_90为正常横屏
            if(p.getRotation() == Surface.ROTATION_0) {
                mPhotoView.setRotation(90);
            }else if(p.getRotation() == Surface.ROTATION_270){
                mPhotoView.setRotation(180);
            }else if(p.getRotation() == Surface.ROTATION_180){
                mPhotoView.setRotation(270);
            }
            mPhotoView.setImageDrawable(bitmapDrawable);
        }
    }

    @NonNull
    private String getPhotoPath(Photo p) {
        return getActivity().getFileStreamPath(p.getFilename()).getPath();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_delete_crime :
                PictureUtils.cleanImageView(mPhotoView);
                return isSuccessDeletePhotoFile();
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        showPhotos();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID uuid = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(uuid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(resultCode == RESULT_DATE){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(fm, DIALOG_DATE);
        }else if (resultCode == RESULT_TIME){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(fm, DIALOG_DATE);
        }
        if(requestCode == REQUEST_DATE){
            final Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }else if (requestCode == REQUEST_PHOTO) {
            boolean isDeleted = isSuccessDeletePhotoFile();
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            int rotation = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ROTATION, Surface.ROTATION_90);
            Photo photo = new Photo(filename, rotation);
            mCrime.setPhoto(photo);
            showPhotos();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isSuccessDeletePhotoFile() {
        Photo beforePhoto = mCrime.getPhoto();
        return (beforePhoto == null ||
                getActivity().getFileStreamPath(beforePhoto.getFilename()).delete());
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).saveCrimes();
    }
}
