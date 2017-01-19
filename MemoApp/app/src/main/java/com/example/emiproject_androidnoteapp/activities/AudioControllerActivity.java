package com.example.emiproject_androidnoteapp.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.MediaController;

import java.io.IOException;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public abstract class AudioControllerActivity extends CameraActivity implements
        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "AudioPlayer";

    protected MediaPlayer mediaPlayer;

    /**
     * Most be called from the child class to start playing
     *
     * @param audioFile the path to the audio file to play.
     */
    protected void initMediaController(String audioFile) {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        try {
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);

            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Could not open audio file for playback.")
                    .setPositiveButton("OK", null)
                    .show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        stopAndRelease();
    }

    //region MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stopAndRelease(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }
    //endregion --------------------------------------------------------------------------------

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared");
    }
}
