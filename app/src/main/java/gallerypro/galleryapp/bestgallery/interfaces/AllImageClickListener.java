package gallerypro.galleryapp.bestgallery.interfaces;

import gallerypro.galleryapp.bestgallery.model.AllImagesModel;

import java.util.List;

public interface AllImageClickListener {

    void onPicClicked(List<AllImagesModel> allImagesModelList, int position);

}
