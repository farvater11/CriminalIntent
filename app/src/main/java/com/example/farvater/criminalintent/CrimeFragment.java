package com.example.farvater.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POS = "cur_pos";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private Crime mCrime;
    private int mCurrentPosition;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mIsSeriouslyCheckBox;
    private ImageButton mFirstCrimeButton;
    private ImageButton mLastCrimeButton;


    public static CrimeFragment newInstance(UUID uuid, int position){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);
        args.putInt(ARG_CRIME_POS, position);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == REQUEST_DATE)&&(resultCode == Activity.RESULT_OK)){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE '\n' MMM dd, YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCurrentPosition = (int) getArguments().getInt(ARG_CRIME_POS);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmen_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mIsSeriouslyCheckBox = (CheckBox) v.findViewById(R.id.is_seriously_checkbox);

        mFirstCrimeButton = (ImageButton) v.findViewById(R.id.first_crime_button);
        mLastCrimeButton = (ImageButton) v.findViewById(R.id.last_crime_button);
        mFirstCrimeButton.setOnClickListener(this);
        mLastCrimeButton.setOnClickListener(this);

        mTitleField.setText(mCrime.getTitle());
        updateDate();
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mIsSeriouslyCheckBox.setChecked(mCrime.isSeriously());
        mIsSeriouslyCheckBox.setEnabled(false);
        mDateButton.setOnClickListener(this);
        mTimeButton.setOnClickListener(this);
        Toast.makeText(getContext(),String.valueOf(mCrime.isSeriously()),Toast.LENGTH_SHORT).show();

        if(mCurrentPosition == 0){
            mFirstCrimeButton.setVisibility(INVISIBLE);
            mLastCrimeButton.setVisibility(VISIBLE);
        }
        else if(mCurrentPosition == CrimeLab.get(getActivity()).getCrimes().size()-1) {
            mFirstCrimeButton.setVisibility(VISIBLE);
            mLastCrimeButton.setVisibility(INVISIBLE);
        }
        else {
            mFirstCrimeButton.setVisibility(VISIBLE);
            mLastCrimeButton.setVisibility(VISIBLE);
        }

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mCrime.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //mDateButton.setText(mCrime.getDate().toString());

        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.first_crime_button)
            CrimePagerActivity.resetCurrentItem(0);
        else if (v.getId() == R.id.last_crime_button)
            CrimePagerActivity.resetCurrentItem((CrimeLab.get(getActivity()).getCrimes().size()-1));
        else if (v.getId() == R.id.crime_date){
            Intent intent = CrimeDatePickerActivity.newIntent(getActivity(), mCrime.getDate());
            startActivityForResult(intent,REQUEST_DATE);
            /*
            FragmentManager fragmentManager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(fragmentManager,DIALOG_DATE);*/
        }
        else if(v.getId() == R.id.crime_time){
            FragmentManager fragmentManager = getFragmentManager();
            TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());
            timePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            timePickerFragment.show(fragmentManager,DIALOG_DATE);
        }
    }
}
