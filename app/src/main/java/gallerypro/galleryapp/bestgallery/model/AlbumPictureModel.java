package gallerypro.galleryapp.bestgallery.model;

import java.io.Serializable;

public class AlbumPictureModel implements Serializable {

    int pictureId;
    String pictureName;
    String picturePath;
    String pictureSize;
    String imageUri;
    String imageHeightWidth;
    String columnsId;
    long dateTaken;
    long dateModified;

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getImageHeightWidth() {
        return imageHeightWidth;
    }

    public void setImageHeightWidth(String imageHeightWidth) {
        this.imageHeightWidth = imageHeightWidth;
    }

    boolean selected = false;

    public AlbumPictureModel() {
    }

    public String getColumnsId() {
        return columnsId;
    }

    public void setColumnsId(String columnsId) {
        this.columnsId = columnsId;
    }

    public AlbumPictureModel(int pictureId, String pictureName, String picturePath, String pictureSize, String imageUri, boolean selected, String columnsId) {
        this.pictureId = pictureId;
        this.pictureName = pictureName;
        this.picturePath = picturePath;
        this.pictureSize = pictureSize;
        this.imageUri = imageUri;
        this.selected = selected;
        this.columnsId = columnsId;
    }

    public AlbumPictureModel(int pictureId, String pictureName, String picturePath, String pictureSize, String imageUri, String imageHeightWidth, String columnsId, long dateTaken, long dateModified) {
        this.pictureId = pictureId;
        this.pictureName = pictureName;
        this.picturePath = picturePath;
        this.pictureSize = pictureSize;
        this.imageUri = imageUri;
        this.imageHeightWidth = imageHeightWidth;
        this.columnsId = columnsId;
        this.dateTaken = dateTaken;
        this.dateModified = dateModified;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPictureSize() {
        return pictureSize;
    }

    public void setPictureSize(String pictureSize) {
        this.pictureSize = pictureSize;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
