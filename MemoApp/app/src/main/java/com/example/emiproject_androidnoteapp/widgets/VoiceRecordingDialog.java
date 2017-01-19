package com.example.emiproject_androidnoteapp.widgets;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.activities.NoteActivity;
import com.example.emiproject_androidnoteapp.utils.FilesUtiles;
import com.example.emiproject_androidnoteapp.utils.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class VoiceRecordingDialog extends AlertDialog {

    private Context context;
    private MediaRecorder mRecorder;
    private String outputFilePath;
    private OnRecordListener onRecordListener;

    public VoiceRecordingDialog(@NonNull Context context, OnRecordListener onRecordListener) {
        super(context);
        this.context = context;
        this.onRecordListener = onRecordListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice_record);

        final ImageView recordButton = (ImageView) findViewById(R.id.button_record);
        final TextView textView = (TextView) findViewById(R.id.text);
        final TextView subtitleText = (TextView) findViewById(R.id.subtitle);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder == null) {
                    startRecording();
                    recordButton.setBackgroundResource(R.drawable.red_circle);
                    recordButton.setImageResource(R.drawable.ic_mic_white_24dp);
                    textView.setText("Stop recording");
                    subtitleText.setText("Recording");
                } else {
                    stopRecording();
                    dismiss();
                }

            }
        });
    }

    public static void show(@NonNull Context context, OnRecordListener onRecordListener) {
        new VoiceRecordingDialog(context, onRecordListener).show();
    }

    private void startRecording() {

        try {
            outputFilePath = FilesUtiles.createTempFile(context, "temp", MimeTypeUtils.THREE_GPP).getAbsolutePath();

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(outputFilePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
        Toast.makeText(context, "Recording Started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Toast.makeText(context, "Recording Stopped", Toast.LENGTH_SHORT).show();

        try {
            File newFile = FilesUtiles.createNewFile(context, MimeTypeUtils.THREE_GPP, new Date());
            FilesUtiles.copyFile(outputFilePath, newFile);

            if (onRecordListener != null) {
                onRecordListener.onRecord(newFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnRecordListener {

        void onRecord(String filePath);
    }
}
