package gallerypro.galleryapp.bestgallery.model;

import java.io.Serializable;
import java.util.List;

public class VideoSliderModel implements Serializable {
    
    List<AllVideoModel> allVideoModelList;
    int position;

    public VideoSliderModel(List<AllVideoModel> allVideoModelList, int position) {
        this.allVideoModelList = allVideoModelList;
        this.position = position;
    }

    public List<AllVideoModel> getAllVideoModelList() {
        return allVideoModelList;
    }

    public int getPosition() {
        return position;
    }
}
