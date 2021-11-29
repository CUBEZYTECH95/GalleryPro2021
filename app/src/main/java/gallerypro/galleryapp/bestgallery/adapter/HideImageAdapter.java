package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.interfaces.HideImageClickListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class HideImageAdapter extends RecyclerView.Adapter<HideImageAdapter.HideImageViewHolder> {

    Context context;
    List<String> hideImageList ;
    HideImageClickListener hideImageClickListener;

    public HideImageAdapter(Context context, List<String> hideImageList, HideImageClickListener hideImageClickListener) {
        this.context = context;
        this.hideImageList = hideImageList;
        this.hideImageClickListener = hideImageClickListener;
//        Collections.reverse(hideImageList);
    }

    @NonNull
    @Override
    public HideImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HideImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_images,parent,false), hideImageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HideImageViewHolder holder, int position) {
//        Glide.with(context).load(hideImageList.get(position)).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        Picasso.get().load(new File(hideImageList.get(position))).fit().centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return hideImageList.size();
    }

    public class HideImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        HideImageClickListener hideImageClickListener;

        public HideImageViewHolder(@NonNull View itemView, HideImageClickListener hideImageClickListener) {
            super(itemView);
            this.hideImageClickListener = hideImageClickListener;
            imageView = itemView.findViewById(R.id.img_images);

            imageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            hideImageClickListener.onHideImgClick(hideImageList,getAdapterPosition());
        }
    }
}
