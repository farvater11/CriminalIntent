package com.example.farvater.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context contex){
        mCrimes = new ArrayList<>();
        for(int i = 0 ; i < 100 ; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i%2 == 0);
            mCrimes.add(crime);
        }
    }



    private List<Crime> mCrimes;

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID uuid){
        for (Crime crime: mCrimes) {
            if(crime.getID().equals(uuid))
                return crime;
        }
        return null;
    }

}
