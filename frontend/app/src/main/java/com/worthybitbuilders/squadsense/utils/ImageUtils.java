package com.worthybitbuilders.squadsense.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {
    public static Bitmap rotateBitmapIfRequired(Context context, Bitmap bitmap, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(input);

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int rotationAngle = 0;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationAngle = 270;
                break;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        input.close();

        return rotatedBitmap;
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String filename) throws IOException {
        File file = new File(context.getCacheDir(), filename);

        OutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        outputStream.flush();
        outputStream.close();

        return file;
    }

    public static Bitmap uriToBitmap(Context context, Uri uri) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        InputStream input = resolver.openInputStream(uri);

        return BitmapFactory.decodeStream(input);
    }
}

