package com.example.emiproject_androidnoteapp.models;

import android.text.TextUtils;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class Note extends RealmObject {

    @PrimaryKey
    private long localId;

    private Date creationDate;
    private String title;
    private String text;
    //private RealmList<Document> documents;
    private RealmList<Image> images;
    private AudioClip audioClip;

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public void setAudioClip(AudioClip audioClip) {
        this.audioClip = audioClip;
    }

    public boolean isEmpty(){
        return TextUtils.isEmpty(title) && TextUtils.isEmpty(text)
                && audioClip == null && (images == null || images.isEmpty());
    }
}
