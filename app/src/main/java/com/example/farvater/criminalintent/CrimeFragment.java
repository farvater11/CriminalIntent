package com.example.farvater.criminalintent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POS = "cur_pos";
    private Crime mCrime;
    private int mCurrentPosition;
    private EditText mTitleField;
    private Button mDateButton;
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
        mIsSeriouslyCheckBox = (CheckBox) v.findViewById(R.id.is_seriously_checkbox);

        mFirstCrimeButton = (ImageButton) v.findViewById(R.id.first_crime_button);
        mLastCrimeButton = (ImageButton) v.findViewById(R.id.last_crime_button);
        mFirstCrimeButton.setOnClickListener(this);
        mLastCrimeButton.setOnClickListener(this);

        mTitleField.setText(mCrime.getTitle());
        mDateButton.setText(mCrime.getDate().toString());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mIsSeriouslyCheckBox.setChecked(mCrime.isSeriously());
        mIsSeriouslyCheckBox.setEnabled(false);
        Toast.makeText(getContext(),String.valueOf(mCrime.isSeriously()),Toast.LENGTH_SHORT).show();

        if(mCurrentPosition == 0){
            mFirstCrimeButton.setEnabled(false);
            mLastCrimeButton.setEnabled(true);
        }
        else if(mCurrentPosition == CrimeLab.get(getActivity()).getCrimes().size()-1) {
            mLastCrimeButton.setEnabled(false);
            mFirstCrimeButton.setEnabled(true);
        }
        else {
            mFirstCrimeButton.setEnabled(true);
            mLastCrimeButton.setEnabled(true);
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
        mDateButton.setEnabled(false);

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
        if (v.getId() == R.id.last_crime_button)
            CrimePagerActivity.resetCurrentItem((CrimeLab.get(getActivity()).getCrimes().size()-1));
    }
}
