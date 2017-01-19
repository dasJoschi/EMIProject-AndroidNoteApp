package com.example.emiproject_androidnoteapp.utils;

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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.emiproject_androidnoteapp.activities.CameraActivity;
import com.example.emiproject_androidnoteapp.models.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class ImageUtils {

    public static final int UNCONSTRAINED = -1;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_IMAGE_GET = 2;

    final CameraActivity activity;
    final Fragment fragment;

    public ImageUtils(CameraActivity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public ImageUtils(CameraActivity activity) {
        this.activity = activity;
        this.fragment = null;
    }

    public File createImageFile(Uri data) throws IOException {
        String mimeType = activity.getContentResolver().getType(data);

        File imageFile = FilesUtiles.createNewFile(activity, mimeType);
        FilesUtiles.copyFile((FileInputStream) activity.getContentResolver().openInputStream(data)
                , imageFile);

        return imageFile;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FilesUtiles.createNewFile(activity, MimeTypeUtils.IMAGE_JPEG);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                setCapturedImageURI(fileUri);
                setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        getCapturedImageURI());
                if (fragment == null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            }
        }

    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            if (fragment == null) {
                activity.startActivityForResult(intent, REQUEST_IMAGE_GET);
            } else {
                fragment.startActivityForResult(intent, REQUEST_IMAGE_GET);
            }

        }
    }

    public String getCurrentPhotoPath() {
        return activity.getCurrentPhotoPath();
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        activity.setCurrentPhotoPath(currentPhotoPath);
    }

    public Uri getCapturedImageURI() {
        return activity.getCapturedImageURI();
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        activity.setCapturedImageURI(capturedImageURI);
    }

    public static File createFileFromBitmap(Context context, Bitmap bitmap, String mimeType, Date creationDate) throws FileNotFoundException {

        if (context == null) {
            return null;
        }

        File file = null;
        try {

            file = FilesUtiles.createNewFile(context, mimeType, creationDate);
            FileOutputStream fOut = new FileOutputStream(file);

            if (mimeType.equals(MimeTypeUtils.IMAGE_PNG)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            }

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }

        return file;
    }

    public static Bitmap getLowResolutionBitmap(String imageFilePath) {
        return getBitmapFromFile(imageFilePath, UNCONSTRAINED, 500 * 500, Bitmap.Config.RGB_565, false);
    }

    public static Bitmap getBitmapFromFile(String imageFilePath, int minSideLength,
                                           int maxNumOfPixels, Bitmap.Config config,
                                           boolean fixOrientation) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, options);
        /*int width = Math.round(options.outWidth / scaleBy);
        int height = Math.round(options.outHeight / scaleBy);*/

        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inSampleSize = computeSampleSize(options, minSideLength, maxNumOfPixels);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        if (fixOrientation) {
            return rotate(bitmap, computeRotation(imageFilePath));
        } else {
            return bitmap;
        }


    }

    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     * */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int computeRotation(String imagePath) {
        int rotate = 0;
        try {
            //File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imagePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees,
                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

}
