package com.example.emiproject_androidnoteapp.models;

import com.example.emiproject_androidnoteapp.utils.MimeTypeUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class AudioClip extends RealmObject implements Document {

    @PrimaryKey
    private long localId;

    private String filePath;
    private long parentId;

    private int duration;
    private boolean deleted;

    public AudioClip() {
    }

    public AudioClip(long localId, String filePath) {
        this.localId = localId;
        this.filePath = filePath;
    }

    @Override
    public String getMimeType() {
        return MimeTypeUtils.THREE_GPP;
    }

    public void setMimeType() {
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
