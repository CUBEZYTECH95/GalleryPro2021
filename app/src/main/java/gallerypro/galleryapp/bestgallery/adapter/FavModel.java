package gallerypro.galleryapp.bestgallery.adapter;

public class FavModel {
    String id;
    String favStatus;

    public FavModel() {

    }

    public FavModel(String id, String favStatus) {
        this.id = id;
        this.favStatus = favStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }
}
