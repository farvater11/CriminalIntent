package com.example.farvater.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.farvater.criminalintent.database.Crime.CrimeBaseHelper;
import com.example.farvater.criminalintent.database.Crime.CrimeCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.farvater.criminalintent.database.Crime.CrimeDbSchema.*;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public void remCrime(Crime c){
             mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + "=?", new String[]{c.getID().toString()});
    }

    public void remAllCrimes(){
             mDatabase.delete(CrimeTable.NAME,null, null);
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID uuid){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + "=?",
                new String[]{ uuid.toString()});
        try{
            if(cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getID().toString();
        ContentValues value = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, value, CrimeTable.Cols.UUID + " =?",
                new String[] {uuidString});
    }

    public File getPhotoFile(Crime crime){
        File fileDir = mContext.getFilesDir();
        return new File(fileDir, crime.getPhotoFileName());
    }

    private static ContentValues getContentValues (Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getID().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SERIOUSLY, crime.isSeriously() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null ,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
}
