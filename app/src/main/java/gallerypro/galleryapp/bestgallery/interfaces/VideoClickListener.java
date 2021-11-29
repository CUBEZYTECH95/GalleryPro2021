package gallerypro.galleryapp.bestgallery.interfaces;

import android.view.View;

import gallerypro.galleryapp.bestgallery.model.AllVideoModel;

import java.util.List;

public interface VideoClickListener {
//    void OnVideoClick(int id,String path, String title,String fileName, String size,String dateAdded, String duration, int position);
    void OnVideoClick(List<AllVideoModel> video, int position);
    void OnVideoLongClick(View v, AllVideoModel allVideoModel, int position);
}
