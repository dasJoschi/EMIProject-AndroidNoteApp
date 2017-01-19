package com.example.emiproject_androidnoteapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.example.emiproject_androidnoteapp.R;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public final class Utils {

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(int dp, Context context) {
        return dpToPx(dp, context.getResources());
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(int px, Context context) {
        return pxToDp(px, context.getResources());
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp        A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param resources Resources to get device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float dpToPx(int dp, Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px        A value in px (pixels) unit. Which we need to convert into db
     * @param resources Resources to get device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pxToDp(int px, Resources resources) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);

        return displaymetrics.widthPixels;
    }

}
