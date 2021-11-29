package gallerypro.galleryapp.bestgallery.model;

public class ImageItem extends ListItem {

    AllImagesModel allImagesModel;

    public AllImagesModel getAllImagesModel() {
        return allImagesModel;
    }

    public void setAllImagesModel(AllImagesModel allImagesModel) {
        this.allImagesModel = allImagesModel;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
}
