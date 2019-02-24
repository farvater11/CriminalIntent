package com.example.farvater.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String ARG_CRIME_POS = "cur_pos";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private Crime mCrime;
    private int mCurrentPosition;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mIsSeriouslyCheckBox;
    private ImageButton mFirstCrimeButton;
    private ImageButton mLastCrimeButton;
    private ImageButton mDellCrimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoImageButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Point mSizeOfPhotoView = new Point();


    private Intent pickContact;
    private Intent captureImage;



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
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateText();
        }
        else if(requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = getActivity().getContentResolver().
                    query(contactUri, queryFields, null,null, null);


            try{
                if(cursor.getCount() == 0)
                    return;
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                mCallButton.setEnabled(true);

            }
            finally {
                cursor.close();
            }
        }
        else if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.farvater.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }

    }

    private void updateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE '\n' MMM dd, YYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
        mTimeButton.setText(timeFormat.format(mCrime.getDate()));

    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MMM, dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect);

        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCurrentPosition = (int) getArguments().getInt(ARG_CRIME_POS);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmen_crime, container, false);

        pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);


        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        mIsSeriouslyCheckBox = (CheckBox) v.findViewById(R.id.is_seriously_checkbox);
        mFirstCrimeButton = (ImageButton) v.findViewById(R.id.first_crime_button);
        mDellCrimeButton = (ImageButton) v.findViewById(R.id.del_crime_button);
        mLastCrimeButton = (ImageButton) v.findViewById(R.id.last_crime_button);
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mCallButton = (Button) v.findViewById(R.id.call);
        mPhotoImageButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoImageButton.setOnClickListener(this);
        mFirstCrimeButton.setOnClickListener(this);
        mLastCrimeButton.setOnClickListener(this);
        mDellCrimeButton.setOnClickListener(this);
        mReportButton.setOnClickListener(this);
        mSuspectButton.setOnClickListener(this);
        mCallButton.setOnClickListener(this);
        mDateButton.setOnClickListener(this);
        mTimeButton.setOnClickListener(this);
        mPhotoView.setOnClickListener(this);

        updateText();
        updatePhotoView();

        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mIsSeriouslyCheckBox.setChecked(mCrime.isSeriously());
        mIsSeriouslyCheckBox.setEnabled(false);

        if(mCrime.getSuspect() != null){
            mCallButton.setEnabled(true);
            mSuspectButton.setText(mCrime.getSuspect());
        }
        else
            mCallButton.setEnabled(false);



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


        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null)
            mSuspectButton.setEnabled(false);


        captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoImageButton.setEnabled(canTakePhoto);

        ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Получаем размеры
                mSizeOfPhotoView.x = mPhotoView.getWidth();
                mSizeOfPhotoView.y = mPhotoView.getHeight();
                updatePhotoView();
                // Внимание! Код ниже обязателен: нужно не забыть отписать слушателя, так как в данном случае больше он нам не понадобится
                ViewTreeObserver obs = mPhotoView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });

        return v;
    }

    private void showContacts(){
        //requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = ?",  new String[]{mCrime.getSuspect()}, null);
        if (cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                    new String[]{contactId},
                    null,
                    null);

            while (phoneCursor.moveToNext()) {
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                startActivity(intent2);
            }
        }
    }

    private void updatePhotoView(){
        if(mPhotoFile == null|| !mPhotoFile.exists())
            mPhotoView.setImageDrawable(null);
        else{
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mSizeOfPhotoView.x, mSizeOfPhotoView.y);
            mPhotoView.setImageBitmap(bitmap);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_crime_button:
                CrimePagerActivity.resetCurrentItem(0);
                break;
            case R.id.last_crime_button:
                CrimePagerActivity.resetCurrentItem((CrimeLab.get(getActivity()).getCrimes().size()-1));
                break;
            case R.id.crime_date:
                Intent intent = CrimeDatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                startActivityForResult(intent,REQUEST_DATE);
                break;
            case R.id.crime_time:
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());
                timePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                timePickerFragment.show(fragmentManager,DIALOG_DATE);
                break;
            case R.id.del_crime_button:
                CrimeLab.get(getActivity()).remCrime(mCrime);
                getActivity().finish();
                break;
            case R.id.crime_report:
                ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(getCrimeReport())
                .setSubject(getString(R.string.crime_report_subject))
                .setChooserTitle(R.string.send_report)
                .startChooser();
                break;
            case R.id.crime_suspect:
                startActivityForResult(pickContact, REQUEST_CONTACT);
                break;
            case R.id.call:
                showContacts();
                break;
            case R.id.crime_camera:
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.example.farvater.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
                break;
            case R.id.crime_photo:
                FragmentManager fragmentManager1 = getFragmentManager();
                DialogFragment enlargedPhotoFragment = EnlargedPhotoFragment.newInstance(mPhotoFile);
                enlargedPhotoFragment.show(fragmentManager1,DIALOG_PHOTO);
                break;
        }
    }
}
