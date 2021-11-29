package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.interfaces.AlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.AlbumModel;
import gallerypro.galleryapp.bestgallery.viewholder.AlbumViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private ArrayList<AlbumModel> album;
    private Context context;
    AlbumClickListener clickListener;

    public AlbumAdapter(ArrayList<AlbumModel> album, Context context, AlbumClickListener clickListener) {
        this.album = album;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        final AlbumModel albumModel = album.get(position);

//        Glide.with(context).load(albumModel.getFirstImage()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        Picasso.get().load(new File(albumModel.getFirstImage())).fit().centerCrop().into(holder.imageView);
        final String folderName = albumModel.getFolderName();
        String folderTotalImage = ""+albumModel.getNumberOfImage();
        final String path = albumModel.getPath();

        holder.albumName.setText(folderName);
        holder.albumName.setSelected(true);
        holder.totalImage.setText(folderTotalImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPicClicked(path,folderName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return album.size();
    }
}
