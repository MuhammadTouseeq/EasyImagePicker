package com.pakdev.easypicker.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pakdev.easypicker.R;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class EasyImagePicker {


    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    private Activity mContext;
    private String mAPPId;
    private static EasyImagePicker instance = new EasyImagePicker();

    public static EasyImagePicker getInstance() {
        return instance;
    }

    public EasyImagePicker withContext(Activity context,String appID) {
        this.mContext = context;
        this.mAPPId=appID;
        return this;
    }

    public EasyImagePicker openEasyPicker() {
        showDialog();
        return this;
    }
    public EasyImagePicker openCamera() {
        requestPermissions(true);
        return this;
    }
    public EasyImagePicker openGallery() {
        requestPermissions(false);
        return this;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_view);

        ImageView btnCamera = (ImageView) dialog.findViewById(R.id.btncamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(true);
                dialog.dismiss();

            }
        });
        ImageView btnGallery = (ImageView) dialog.findViewById(R.id.btngallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(false);
                dialog.dismiss();

            }
        });
        TextView btnCancel = (TextView) dialog.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    /**
     * Capture image from camera
     */
    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtil.createImageFile(mContext);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        mAPPId + ".provider",
                        photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mContext.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void requestPermissions(final boolean isCamera) {
        Dexter.withActivity(mContext).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            ImageUtil.showSettingsDialog(mContext);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).onSameThread()
                .withErrorListener(error -> Toast.makeText(mContext, "Error occured", Toast.LENGTH_SHORT).show())
                .check();
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    private easyPickerCallback easyPickerCallback;

    public interface easyPickerCallback {
        void onMediaFilePicked(String result);
        void onFailed(String error);
    }


    public void passActivityResult(int requestCode, int resultCode,@Nullable Intent data,easyPickerCallback pickerCallback) {
        switch (requestCode) {

            case (REQUEST_TAKE_PHOTO): {

                if(resultCode == Activity.RESULT_OK){
                    try {
                        mCompressor = new FileCompressor(mContext);
                        mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (pickerCallback != null) {
                        pickerCallback.onMediaFilePicked(mPhotoFile.getAbsolutePath());
                    }
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                    pickerCallback.onFailed("Failed to access file or permission missing");
                }


            }
            break;
            case (REQUEST_GALLERY_PHOTO): {


                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();


                    mCompressor = new FileCompressor(mContext);
                    File mPhotoFile = null;
                    try {
                        mPhotoFile = mCompressor.compressToFile(new File(ImageUtil.getRealPathFromUri(mContext, selectedImage)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (pickerCallback != null) {
                        pickerCallback.onMediaFilePicked(mPhotoFile.getAbsolutePath());
                    }

                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                    pickerCallback.onFailed("Failed to access file or permission missing");
                }

            }


        }
    }

}
