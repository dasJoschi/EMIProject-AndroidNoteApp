package com.example.emiproject_androidnoteapp.models;

import com.example.emiproject_androidnoteapp.utils.MimeTypeUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class Image extends RealmObject implements Document {

    @PrimaryKey
    private long localId;

    private String filePath;
    private long parentId;

    private boolean deleted;

    public Image() {
    }

    public Image(long localId, String filePath) {
        this.localId = localId;
        this.filePath = filePath;
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

    @Override
    public String getMimeType() {
        return MimeTypeUtils.IMAGE_JPEG;
    }

    public void setMimeType() {}

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
