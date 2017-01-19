package com.example.emiproject_androidnoteapp.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class LoadLocalImageTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private boolean fixOrientation;
    private int maxNumOfPixels;
    private ImageTaskListener listener;

    public LoadLocalImageTask(ImageView imageView, boolean fixOrientation, int maxNumOfPixels) {
        this(imageView, fixOrientation, maxNumOfPixels, null);
    }

    public LoadLocalImageTask(ImageView imageView, boolean fixOrientation, int maxNumOfPixels, ImageTaskListener listener) {
        this.fixOrientation = fixOrientation;
        this.maxNumOfPixels = maxNumOfPixels;
        this.listener = listener;
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imagePath = params[0];

        return ImageUtils.getBitmapFromFile(imagePath, ImageUtils.UNCONSTRAINED, maxNumOfPixels
                , Bitmap.Config.ARGB_8888, fixOrientation);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

        }
        if (listener != null) {
            listener.onDone(bitmap);
        }

    }

    public interface ImageTaskListener {

        void onDone(Bitmap bitmap);
    }
}

