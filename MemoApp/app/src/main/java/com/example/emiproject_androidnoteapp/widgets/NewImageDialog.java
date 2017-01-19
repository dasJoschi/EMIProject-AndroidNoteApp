package com.example.emiproject_androidnoteapp.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.adapters.AlertDialogListAdapter;
import com.example.emiproject_androidnoteapp.adapters.SimpleListItem;
import com.example.emiproject_androidnoteapp.utils.FilesUtiles;
import com.example.emiproject_androidnoteapp.utils.ImageUtils;
import com.example.emiproject_androidnoteapp.utils.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class NewImageDialog {

    public static void show(Context context, final ImageUtils imageUtils) {

        AlertDialogListAdapter adapter = new AlertDialogListAdapter(context);
        adapter.add(new SimpleListItem.Builder(context)
                .content("Take photo")
                .icon(R.drawable.ic_camera_gray_24dp)
                .build());

        adapter.add(new SimpleListItem.Builder(context)
                .content("Choose image")
                .icon(R.drawable.ic_photo_gray_24dp)
                .build());

        new AlertDialog.Builder(context)
                .setTitle("Add image")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                imageUtils.dispatchTakePictureIntent();
                                break;
                            case 1:
                                imageUtils.selectImage();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }
}
