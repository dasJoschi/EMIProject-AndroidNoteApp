package com.example.emiproject_androidnoteapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.models.Image;
import com.example.emiproject_androidnoteapp.utils.Constants;
import com.example.emiproject_androidnoteapp.utils.LoadLocalImageTask;
import com.example.emiproject_androidnoteapp.utils.RealmFacade;
import com.example.emiproject_androidnoteapp.widgets.TouchImageView;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class FullScreenImageActivity extends AppCompatActivity {

    public static final int RESULT_DELETED = 1;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private TouchImageView fullImageView;
    private LoadLocalImageTask loadImageTask;

    private String filePath;
    private Image image;
    private RealmFacade realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = new RealmFacade();

        Intent intent = getIntent();
        long imageId = intent.getLongExtra(Constants.EXTRA_IMAGE_ID, -1);

        image = realm.where(Image.class).equalTo("localId", imageId).findFirst();
        filePath = intent.getStringExtra(Constants.EXTRA_FILE_PATH);

        fullImageView = (TouchImageView) findViewById(R.id.fullImage);

        calculateScreenResolution();

        loadImageTask = new LoadLocalImageTask(fullImageView, false,
                SCREEN_HEIGHT * SCREEN_WIDTH);
        loadImageTask.execute(filePath);

        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (realm != null) {
            realm.close();
        }

        fullImageView.setImageBitmap(null);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fullscreenimage_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this).setMessage(R.string.delete_confirmation_message_image)
                        .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteImage();
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;

        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        SCREEN_HEIGHT -= mActionBarSize;
    }

    private void deleteImage() {
        setResult(RESULT_DELETED, getIntent());
        finish();
    }

}
