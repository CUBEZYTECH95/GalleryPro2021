package gallerypro.galleryapp.bestgallery.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import gallerypro.galleryapp.bestgallery.R;

public class PictureViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public RelativeLayout relativeLayout;
    public FrameLayout frameLayout;

    public PictureViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        relativeLayout = itemView.findViewById(R.id.item_selected);
        frameLayout = itemView.findViewById(R.id.item_image);
    }
}
