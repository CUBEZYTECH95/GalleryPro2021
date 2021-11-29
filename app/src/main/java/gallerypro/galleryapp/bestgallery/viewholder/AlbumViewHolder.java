package gallerypro.galleryapp.bestgallery.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;

public class AlbumViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView albumName;
    public TextView totalImage;


    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.album_image);
        albumName = itemView.findViewById(R.id.album_name);
        totalImage = itemView.findViewById(R.id.image_size);

    }
}
