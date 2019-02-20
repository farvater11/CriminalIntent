package com.example.farvater.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.Inflater;


public class CrimeListFragment extends Fragment{
    private static final int REQUEST_CRIME = 1;
    private static final String KEY_SUBTITLE = "key_visible_subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Button mNewCrimeButton;
    private Button mNewSerioCrimeButton;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SUBTITLE,mSubtitleVisible);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            mSubtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewCrimeButton = (Button) view.findViewById(R.id.new_crime_on_empty_screen);
        mNewSerioCrimeButton = (Button) view.findViewById(R.id.new_serio_crime_on_empty_screen);

        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getID());
                startActivity(intent);
            }
        });
        mNewSerioCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                crime.setSeriously(true);
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getID());
                startActivity(intent);
            }
        });
        updateUI();
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CRIME){

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible)
            subtitleItem.setTitle(R.string.hide_subtitle);
        else
            subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getID());
                startActivity(intent);
                return true;
            case R.id.new_serio_crime:
                Crime crime1 = new Crime();
                crime1.setSeriously(true);
                CrimeLab.get(getActivity()).addCrime(crime1);
                Intent intent1 = CrimePagerActivity.newIntent(getActivity(),crime1.getID());
                startActivity(intent1);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.rem_all_crimes:
                CrimeLab.get(getActivity()).remAllCrimes();
                getActivity().invalidateOptionsMenu();
                updateUI();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount,crimeCount);
        if(!mSubtitleVisible)
            subtitle = null;
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        updateSubtitle();

        if(CrimeLab.get(getActivity()).getCrimes().size() == 0){
            mNewCrimeButton.setVisibility(View.VISIBLE);
            mNewSerioCrimeButton.setVisibility(View.VISIBLE);
        }
        else{
            mNewCrimeButton.setVisibility(View.GONE);
            mNewSerioCrimeButton.setVisibility(View.GONE);
        }


        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setCrimes(crimes);
            mAdapter.mCrimes = crimes;
            mAdapter.notifyDataSetChanged();
        }

    }



    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextVIew;
        private TextView mDateTextView;
        private ImageButton mCallPoliceButton;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        public void bind(Crime crime){
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, YYYY HH:mm:ss");
            mCrime = crime;
            mTitleTextVIew.setText(mCrime.getTitle());
            mDateTextView.setText(dateFormat.format(mCrime.getDate()));
            mCallPoliceButton.setVisibility(crime.isSeriously() ? View.VISIBLE : View.GONE);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            mSolvedImageView.setBackgroundColor(crime.isSeriously() ?
                    getResources().getColor(R.color.colorPrimary):
                    getResources().getColor(R.color.white));
            mDateTextView.setBackgroundColor(crime.isSeriously() ?
                    getResources().getColor(R.color.colorPrimaryDark):
                    getResources().getColor(R.color.white));
            mTitleTextVIew.setBackgroundColor(crime.isSeriously() ?
                    getResources().getColor(R.color.colorPrimary):
                    getResources().getColor(R.color.white));
            mDateTextView.setTextColor(crime.isSeriously() ?
                    getResources().getColor(R.color.alice_blue):
                    getResources().getColor(R.color.gray));

        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){

            super(inflater.inflate(R.layout.list_item_crime, parent,false));

            mTitleTextVIew = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mCallPoliceButton = (ImageButton) itemView.findViewById(R.id.police_button);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
            mCallPoliceButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.police_button)
                Toast.makeText(getActivity(),mCrime.getTitle() + " calling to police!", Toast.LENGTH_SHORT).show();
            else{
                startActivity(CrimePagerActivity.newIntent(getContext(),mCrime.getID()));
            }
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = mCrimes.get(crimeHolder.getLayoutPosition());
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }
}
