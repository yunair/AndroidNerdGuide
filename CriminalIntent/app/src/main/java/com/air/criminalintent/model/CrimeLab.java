package com.air.criminalintent.model;

import android.content.Context;
import android.util.Log;

import com.air.criminalintent.util.CriminalIntentJSONSerializer;

import java.util.List;
import java.util.UUID;

/**
 * Created by Air on 15/5/13.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private List<Crime> mCrimes;

    private static CrimeLab sCrimeLab;
    private Context mApplicationContext;

    private CriminalIntentJSONSerializer mSerializer;

    private CrimeLab(Context context){
        mApplicationContext = context;
        mSerializer = new CriminalIntentJSONSerializer(mApplicationContext, FILENAME);
        try {
            mCrimes = mSerializer.loadCrimes();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }
    
    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        } }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public static CrimeLab getInstance(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }

        return sCrimeLab;
    }

}
