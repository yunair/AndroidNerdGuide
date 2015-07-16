package com.air.criminalintent;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.air.criminalintent.model.Crime;
import com.air.criminalintent.model.CrimeLab;

import java.util.ArrayList;
import java.util.UUID;


public class CrimePageActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    protected Fragment createFragment() {
        UUID uuid = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

        return CrimeFragment.newInstance(uuid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);
        mCrimes = CrimeLab.getInstance(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);

                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


        UUID currentId = (UUID) getIntent().
                getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for(int i = 0; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(currentId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Crime crime = mCrimes.get(position);
        if(crime.getTitle() != null){
            setTitle(crime.getTitle());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
