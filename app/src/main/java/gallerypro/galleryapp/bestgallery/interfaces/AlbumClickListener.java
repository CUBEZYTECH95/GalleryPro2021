package gallerypro.galleryapp.bestgallery.interfaces;

import android.view.View;

import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.viewholder.PictureViewHolder;

import java.util.List;

public interface AlbumClickListener {

    void onPicClicked(String pictureFolderPath, String folderName);
    void onPicClicked(PictureViewHolder holder, int position, List<AlbumPictureModel> pics);

    void onItemLongClick(View view, AlbumPictureModel obj, int pos);
}
