package com.example.emiproject_androidnoteapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.adapters.ImagesAdapter;
import com.example.emiproject_androidnoteapp.models.AudioClip;
import com.example.emiproject_androidnoteapp.models.Image;
import com.example.emiproject_androidnoteapp.models.Note;
import com.example.emiproject_androidnoteapp.utils.Constants;
import com.example.emiproject_androidnoteapp.utils.FilesUtiles;
import com.example.emiproject_androidnoteapp.utils.IdGenerator;
import com.example.emiproject_androidnoteapp.utils.ImageUtils;
import com.example.emiproject_androidnoteapp.utils.RealmFacade;
import com.example.emiproject_androidnoteapp.utils.SpaceItemDecoration;
import com.example.emiproject_androidnoteapp.utils.Utils;
import com.example.emiproject_androidnoteapp.widgets.NewImageDialog;
import com.example.emiproject_androidnoteapp.widgets.VoiceRecordingDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class NoteActivity extends AudioControllerActivity implements VoiceRecordingDialog.OnRecordListener, ImagesAdapter.OnImageClickedListener {

    public static final int REQUEST_FULL_SCREEN = 1;
    public static final String ACTION_EDIT = "action-edit";
    public static final String ACTION_NEW = "action-new";

    private ImageView btn_share, btn_addImage, btn_record, btn_play, btn_deleteClip;
    private ProgressBar progressBar_clip;
    private EditText title_et, text_et;
    private View media_controller;
    private TextView tv_clipLength, date_tv;
    private RecyclerView imagesRecyclerView;
    private ImagesAdapter imagesAdapter;

    private ImageUtils imageUtils;
    private RealmFacade realm;
    private IdGenerator idGenerator;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageUtils = new ImageUtils(this);
        realm = new RealmFacade();
        title_et = (EditText) findViewById(R.id.title_et);
        text_et = (EditText) findViewById(R.id.text_et);
        date_tv = (TextView) findViewById(R.id.date_tv);
        media_controller = findViewById(R.id.media_controller);
        tv_clipLength = (TextView) findViewById(R.id.tv_clipLength);
        btn_play = (ImageView) findViewById(R.id.btn_play);
        btn_deleteClip = (ImageView) findViewById(R.id.btn_deleteClip);
        progressBar_clip = (ProgressBar) findViewById(R.id.progressBar_clip);
        btn_share = (ImageView) findViewById(R.id.btn_share);
        btn_addImage = (ImageView) findViewById(R.id.btn_addImage);
        btn_record = (ImageView) findViewById(R.id.btn_record);

        if (getIntent().getAction().equals(ACTION_EDIT)) {
            long id = getIntent().getExtras().getLong(Constants.EXTRA_NOTE_ID, -1);
            if (id != -1) {
                Note realmNote = realm.where(Note.class).equalTo("localId", id).findFirst();
                note = realm.getRealm().copyFromRealm(realmNote);
                populateViews();
            } else {
                Toast.makeText(this, "Error has occurred!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            note = new Note();
            note.setImages(new RealmList<Image>());
        }
        idGenerator = new IdGenerator(realm, Image.class);

        initRecyclerView();
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        btn_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewImageDialog.show(NoteActivity.this, imageUtils);
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the application has the permission to use the mic
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    // the application already has permission
                    VoiceRecordingDialog.show(NoteActivity.this, NoteActivity.this);
                } else {
                    // try to get permission
                    ActivityCompat.requestPermissions(NoteActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                    // recheck if permission was granted
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        VoiceRecordingDialog.show(NoteActivity.this, NoteActivity.this);
                    } else {
                        new AlertDialog.Builder(NoteActivity.this)
                                .setMessage("Can not record audio. No permission was given.")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                }
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pause();
                    btn_play.setImageResource(R.drawable.ic_play_circle_outline_gray_24dp);
                } else {
                    start();
                    mHandler.post(mUpdateProgress);
                    btn_play.setImageResource(R.drawable.ic_pause_circle_filled_green_24dp);
                }
            }
        });
        btn_deleteClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NoteActivity.this).setMessage(R.string.delete_confirmation_message_voice)
                        .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAudioClip();
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        progressBar_clip.setMax(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (note.getAudioClip() != null) {
            AudioClip clip = note.getAudioClip();
            initMediaController(clip.getFilePath());
        }
    }

    private void populateViews() {
        title_et.setText(note.getTitle());
        text_et.setText(note.getText());
        date_tv.setText(new SimpleDateFormat("dd.MM.yy").format(note.getCreationDate()));
    }

    private void initRecyclerView() {
        imagesRecyclerView = (RecyclerView) findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.addItemDecoration(new SpaceItemDecoration(5, SpaceItemDecoration.HORIZONTAL_LIST, false));

        imagesAdapter = new ImagesAdapter(this, note.getImages(), this, Utils.getScreenWidth(this));
        imagesAdapter.setHasStableIds(true);
        imagesAdapter.setRecyclerView(imagesRecyclerView);
        imagesRecyclerView.setAdapter(imagesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (note.isEmpty()) {
                    deleteNote();
                    return true;
                }
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this).setMessage(R.string.delete_confirmation_message_note)
                        .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNote();
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ImageUtils.REQUEST_TAKE_PHOTO:
                    String filePath = imageUtils.getCurrentPhotoPath();
                    //String mimeType = MimeTypeUtils.IMAGE_JPEG;
                    note.getImages().add(new Image(idGenerator.generateNewID(), filePath));
                    imagesAdapter.updateDataSet(note.getImages());
                    break;
                case ImageUtils.REQUEST_IMAGE_GET:
                    try {
                        //String mimeType_ = getContentResolver().getType(data.getData());
                        File imageFile = imageUtils.createImageFile(data.getData());
                        note.getImages().add(new Image(idGenerator.generateNewID(), imageFile.getAbsolutePath()));
                        imagesAdapter.updateDataSet(note.getImages());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else if (resultCode == FullScreenImageActivity.RESULT_DELETED) {
            long id = data.getLongExtra(Constants.EXTRA_IMAGE_ID, -1);
            deleteImage(id);
        }

    }

    private void deleteImage(long id) {
        Image imageToDelete = null;
        for (Image image : note.getImages()) {
            if (image.getLocalId() == id) {
                imageToDelete = image;
            }
        }
        if (imageToDelete != null) {
            File imageFile = new File(imageToDelete.getFilePath());
            if (imageFile.exists()) {
                imageFile.delete();
            }
            note.getImages().remove(imageToDelete);
            imagesAdapter.calculateImageWidth();
            imagesAdapter.notifyDataSetChanged();
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    private Handler mHandler = new Handler();

    private final Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            int pos = updateProgress();
            if (isPlaying()) {
                mHandler.postDelayed(mUpdateProgress, 1000 - (pos % 1000));
            }
        }
    };

    private int updateProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        int position = getCurrentPosition();
        int duration = getDuration();
        if (progressBar_clip != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                progressBar_clip.setProgress((int) pos);
            }
        }

        return position;
    }

    private void share() {
        // update note title & text
        note.setTitle(title_et.getText().toString());
        note.setText(text_et.getText().toString());

        // check if the note has content
        if (note.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("There is nothing to share!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        Intent sendIntent = new Intent();
        // multiple because more than one file could be uploaded
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        // set permission to read the files
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // add title, text and subject (same as title for e.g. email targets)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, note.getText());

        // add audio record and images
        ArrayList<Uri> files = new ArrayList<>();
        if (note.getAudioClip() != null && note.getAudioClip().getDuration() > 0) {
            files.add(Uri.parse("file://" + note.getAudioClip().getFilePath()));
        }
        if (note.getImages() != null) {
            for (Image img : note.getImages()) {
                files.add(Uri.parse("file://" + img.getFilePath()));
            }
        }

        // choose best mime type for the intent
        if (note.getAudioClip() == null && note.getImages().isEmpty()) {
            sendIntent.setType("text/plain");
        } else if (note.getAudioClip() == null) {
            sendIntent.setType(note.getImages().first().getMimeType());
        } else if (note.getImages().isEmpty()) {
            sendIntent.setType(note.getAudioClip().getMimeType());
        } else {
            sendIntent.setType("*/*");
        }

        startActivity(sendIntent);
    }

    private void deleteAudioClip() {
        stopAndRelease();
        media_controller.setVisibility(View.GONE);
        mHandler.removeCallbacks(mUpdateProgress);

        AudioClip clip = note.getAudioClip();
        FilesUtiles.deleteFile(clip.getFilePath());
        if (clip.isManaged()) {
            realm.removeFromRealm(clip);
        }
        note.setAudioClip(null);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }


    private void deleteNote() {
        setResult(RESULT_OK);
        if (getIntent().getAction().equals(ACTION_EDIT)) {
            Note realmNote = realm.where(Note.class).equalTo("localId", note.getLocalId()).findFirst();
            realm.removeFromRealm(realmNote);
        }
        finish();
    }

    private void save() {
        if (getIntent().getAction().equals(ACTION_NEW)) {
            note.setCreationDate(new Date());
            IdGenerator generator = new IdGenerator(realm, Note.class);
            note.setLocalId(generator.generateNewID());
        }
        note.setTitle(title_et.getText().toString());
        note.setText(text_et.getText().toString());

        if (note.isEmpty()) {
            deleteNote();
            return;
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();

        setResult(RESULT_OK);
        finish();
    }

    private void setClipLengthText(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;

        String length = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv_clipLength.setText(length);
    }

    @Override
    public void onRecord(String filePath) {
        AudioClip audioClip = note.getAudioClip();
        if (audioClip == null) {
            IdGenerator generator = new IdGenerator(realm, AudioClip.class);
            audioClip = new AudioClip(generator.generateNewID(), filePath);
            note.setAudioClip(audioClip);
        } else {
            audioClip.setFilePath(filePath);
        }
        initMediaController(filePath);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        super.onPrepared(mediaPlayer);
        note.getAudioClip().setDuration(getDuration());
        setClipLengthText(getDuration());
        media_controller.setVisibility(View.VISIBLE);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btn_play.setImageResource(R.drawable.ic_play_circle_outline_gray_24dp);
                mHandler.removeCallbacks(mUpdateProgress);
                progressBar_clip.setProgress(0);
            }
        });
    }

    @Override
    public void onImageClicked(Image image, int position) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra(Constants.EXTRA_IMAGE_ID, image.getLocalId());
        intent.putExtra(Constants.EXTRA_FILE_PATH, image.getFilePath());
        startActivityForResult(intent, REQUEST_FULL_SCREEN);
    }
}
