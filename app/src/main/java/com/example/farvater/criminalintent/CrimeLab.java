package com.example.farvater.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.farvater.criminalintent.database.Crime.CrimeBaseHelper;
import com.example.farvater.criminalintent.database.Crime.CrimeDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.farvater.criminalintent.database.Crime.CrimeDbSchema.*;

public class CrimeLab {
    //private List<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context contex){
        mContext = contex.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes = new ArrayList<>();
        /*for(int i = 0 ; i < 100 ; i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i%3 == 0);
            crime.setSeriously(i%2 == 0);
            mCrimes.add(crime);
        }*/
    }

    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public void remCrime(Crime c){
        //mCrimes.remove(c);
    }
    public void remAllCrimes(){
        //mCrimes.clear();
    }

    public List<Crime> getCrimes(){
        //return mCrimes;
        return new ArrayList<>();
    }

    public Crime getCrime(UUID uuid){
        /*for (Crime crime: mCrimes) {
            if(crime.getID().equals(uuid))
                return crime;
        }*/
        return null;
    }

    private static ContentValues getContentValues (Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getID().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().toString());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SERIOUSLY, crime.isSeriously() ? 1 : 0);
        return values;
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getID().toString();
        ContentValues value = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, value, CrimeTable.Cols.UUID + " =?",
                new String[] {uuidString});
    }

    private Cursor queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null ,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }
}
