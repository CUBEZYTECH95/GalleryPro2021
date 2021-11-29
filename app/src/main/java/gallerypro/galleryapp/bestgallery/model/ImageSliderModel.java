package gallerypro.galleryapp.bestgallery.model;

import java.io.Serializable;
import java.util.List;

public class ImageSliderModel implements Serializable {

    List<AllImagesModel> allImagesModelList;
    int position;

    public ImageSliderModel(List<AllImagesModel> allImagesModelList, int position) {
        this.allImagesModelList = allImagesModelList;
        this.position = position;
    }

    public List<AllImagesModel> getAllImagesModelList() {
        return allImagesModelList;
    }

    public int getPosition() {
        return position;
    }
}
