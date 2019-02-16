package com.example.farvater.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

public class CrimeDatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_DATE = "extra date";

    public static Intent newIntent(Context context, Date date){
        Intent intent = new Intent(context, CrimeDatePickerActivity.class);
        intent.putExtra(EXTRA_CRIME_DATE, date);
        return intent;
    }

    public void destroy(){
        finish();
    }

    @Override
    protected Fragment createFragment() {
            return DatePickerFragment.newInstance((Date)getIntent().getSerializableExtra(EXTRA_CRIME_DATE));
    }
}
