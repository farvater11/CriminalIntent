package com.example.farvater.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;


public class EnlargedPhotoFragment extends DialogFragment {

    private static final String ARG_FILE = "photo";
    private ImageView mImageView;

    public static EnlargedPhotoFragment newInstance(File file){
        EnlargedPhotoFragment enlargedPhotoFragment = new EnlargedPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_FILE,file);
        enlargedPhotoFragment.setArguments(bundle);
        return enlargedPhotoFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_enlarged_photo, null);
        mImageView = (ImageView) v.findViewById(R.id.enlarged_photo_image_view);

        final File mPhotoFile = (File) getArguments().getSerializable(ARG_FILE);
        Uri uri = FileProvider.getUriForFile(getActivity(),
                "com.example.farvater.criminalintent.fileprovider",
                mPhotoFile);
        getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Получаем размеры
                int x = mImageView.getWidth();
                int y = mImageView.getHeight();
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), x, y);
                mImageView.setImageBitmap(bitmap);
                // Внимание! Код ниже обязателен: нужно не забыть отписать слушателя, так как в данном случае больше он нам не понадобится
                ViewTreeObserver obs = mImageView.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
