package com.pakdev.practiceapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pakdev.easypicker.utils.EasyImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.BtnTakePicture).setOnClickListener((it) -> {

         EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID).openCamera();

       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImagePicker.getInstance().passActivityResult(requestCode, resultCode, data, new EasyImagePicker.easyPickerCallback() {
            @Override
            public void onMediaFilePicked(String result) {


                Glide.with(MainActivity.this).load(Uri.fromFile(new File(result)))
                        .apply(new RequestOptions().circleCrop())
                        // .placeholder(drawable)
                        .into( ((ImageView)findViewById(R.id.img)));

            }

            @Override
            public void onFailed(String error) {

                Toast.makeText(MainActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}