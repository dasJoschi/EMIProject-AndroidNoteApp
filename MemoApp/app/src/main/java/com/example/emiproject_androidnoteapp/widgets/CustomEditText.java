package com.example.emiproject_androidnoteapp.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.example.emiproject_androidnoteapp.utils.FontCache;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
        setCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context);
    }

    private void setCustomFont(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            Typeface tf = FontCache.get("ComingSoon.ttf", context);
            if (tf != null) {
                setTypeface(tf);
            }
        }
    }
}
