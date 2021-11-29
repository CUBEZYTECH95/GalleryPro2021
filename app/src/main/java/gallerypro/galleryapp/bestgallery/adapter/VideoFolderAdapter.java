package gallerypro.galleryapp.bestgallery.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.interfaces.VideoAlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.VideoFolderModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.VideoFolderViewHolder> {

    private Context context;
    private ArrayList<String> folderNameList;
    private ArrayList<VideoFolderModel> videoFolderList;
    private RequestOptions options;
    VideoAlbumClickListener videoAlbumClickListener;

    public VideoFolderAdapter(Context context, ArrayList<String> folderNameList, ArrayList<VideoFolderModel> videoFolderList, VideoAlbumClickListener videoAlbumClickListener) {
        this.context = context;
        this.folderNameList = folderNameList;
        this.videoFolderList = videoFolderList;
        this.videoAlbumClickListener = videoAlbumClickListener;
        options = new RequestOptions();

        Log.d("ASDFGHJKOLP)U", "videoFolderList-----id: " + videoFolderList.size());
        Log.d("ASDFGHJKOLP)U", "folderNameList-----firstPic: " + folderNameList.size());

    }

    @NonNull
    @Override
    public VideoFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new VideoFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFolderViewHolder holder, final int position) {
//        Glide.with(context).load(videoFolderList.get(position).getFirstPic()).apply(new RequestOptions().centerCrop()).into(holder.ivFolderImage);
//        Picasso.get().load(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(videoFolderList.get(position).id))).fit().centerCrop().into(holder.ivFolderImage);
        File file = new File(videoFolderList.get(position).getFirstPic());

        Glide.with(context)
                .load(file.getPath())
                .apply(options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(holder.ivFolderImage);


        holder.tvFolderName.setText(videoFolderList.get(position).getFolderName());
        holder.tvFolderItemCount.setText(String.valueOf(videoFolderList.get(position).getNumberOfVideo()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoAlbumClickListener.onVideoAlbumClick(videoFolderList.get(position).getPath(), videoFolderList.get(position).getFolderName());
            }
        });

        Log.d("ASDFGHJKOLP)U", "onBindViewHolder-----id: " + videoFolderList.get(position).id);
        Log.d("ASDFGHJKOLP)U", "onBindViewHolder-----firstPic: " + videoFolderList.get(position).getFirstPic());
    }

    @Override
    public int getItemCount() {
        return folderNameList.size();
    }

    public class VideoFolderViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFolderImage;
        TextView tvFolderName;
        TextView tvFolderItemCount;

        public VideoFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFolderImage = itemView.findViewById(R.id.album_image);
            tvFolderName = itemView.findViewById(R.id.album_name);
            tvFolderItemCount = itemView.findViewById(R.id.image_size);

        }
    }
}
