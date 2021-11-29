package gallerypro.galleryapp.bestgallery.model;

public class VideoFolderModel {

    public String id;
    public String path;
    public String folderName;
    public String firstPic;
    public int numberOfVideo = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }

    public int getNumberOfVideo() {
        return numberOfVideo;
    }

    public void setNumberOfVideo(int numberOfVideo) {
        this.numberOfVideo = numberOfVideo;
    }

    public void  addVideo(){
        this.numberOfVideo++;
    }
}
