package com.example.farvater.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.Inflater;

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE="date";
    public static final String EXTRA_DATE = "com.example.farvater.criminalintent.date";

    private DatePicker mDatePicker;
    private Button mOkButton;

    private void sendResult(int resultCode, Date date){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        if(getTargetFragment()==null)
            getActivity().setResult(resultCode, intent);
        else
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode, intent);
    }

    public static DatePickerFragment newInstance(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE,date);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        Date mDate = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        final int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = mCalendar.get(Calendar.MINUTE);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        mOkButton = (Button) v.findViewById(R.id.button_ok);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();

                Date date = new GregorianCalendar(year,month,day,hour,minutes).getTime();
                sendResult(Activity.RESULT_OK, date);
                getActivity().getSupportFragmentManager().beginTransaction().remove(DatePickerFragment.this).commit();


                getActivity().finish();
            }
        });
        return v;
    }
}

