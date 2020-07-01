package com.pakdev.easypicker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {
  private ImageUtil() {
  }
  static File compressImage(File imageFile, int reqWidth, int reqHeight,
      Bitmap.CompressFormat compressFormat, int quality, String destinationPath)
      throws IOException {
    FileOutputStream fileOutputStream = null;
    File file = new File(destinationPath).getParentFile();
    if (!file.exists()) {
      file.mkdirs();
    }
    try {
      fileOutputStream = new FileOutputStream(destinationPath);
      // write the compressed bitmap at the destination specified by destinationPath.
      decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality,
          fileOutputStream);
    } finally {
      if (fileOutputStream != null) {
        fileOutputStream.flush();
        fileOutputStream.close();
      }
    }
    return new File(destinationPath);
  }
  static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight)
      throws IOException {
    // First decode with inJustDecodeBounds=true to check dimensions
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
    //check the rotation of the image and display it properly
    ExifInterface exif;
    exif = new ExifInterface(imageFile.getAbsolutePath());
    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
    Matrix matrix = new Matrix();
    if (orientation == 6) {
      matrix.postRotate(90);
    } else if (orientation == 3) {
      matrix.postRotate(180);
    } else if (orientation == 8) {
      matrix.postRotate(270);
    }
    scaledBitmap =
        Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),
            matrix, true);
    return scaledBitmap;
  }
  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;
      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }


  /**
   * Create file with current timestamp name
   *
   * @throws IOException
   */
  public static File createImageFile(Context context) throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String mFileName = "JPEG_" + timeStamp + "_";
    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
    return mFile;
  }
  /**
   * Get real file path from URI
   */
  public static String getRealPathFromUri(Context context,Uri contentUri) {
    Cursor cursor = null;
    try {
      String[] proj = {MediaStore.Images.Media.DATA};
      cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
      assert cursor != null;
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }


  /**
   * Showing Alert Dialog with Settings option
   * Navigates user to app settings
   * NOTE: Keep proper title and message depending on your app
   */
  public static void showSettingsDialog(Activity activity) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle("Need Permissions");
    builder.setMessage(
            "This app needs permission to use this feature. You can grant them in app settings.");
    builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
      dialog.cancel();
      openSettings(activity);
    });
    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    builder.show();
  }
  // navigating user to app settings
  public static void openSettings(Activity activity) {

    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
    intent.setData(uri);
    activity.startActivityForResult(intent, 101);
  }

}