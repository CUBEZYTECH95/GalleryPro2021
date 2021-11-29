package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.interfaces.AllImageClickListener;
import gallerypro.galleryapp.bestgallery.model.AllImagesModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyClassView> {

    List<AllImagesModel> allImagesModelList;
    List<String> imageList;
    Context context;
    AllImageClickListener allImageClickListener;

    public ImageAdapter(List<AllImagesModel> allImagesModelList, List<String> imageList, Context context , AllImageClickListener allImageClickListener) {
        this.allImagesModelList = allImagesModelList;
        this.imageList = imageList;
        this.context = context;
        this.allImageClickListener= allImageClickListener;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_images, parent, false);
        return new MyClassView(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        AllImagesModel imageItem = allImagesModelList.get(position);
        String date = imageList.get(position);

//        Glide.with(context).load(imageItem.getPath()).apply(new RequestOptions().centerCrop()).into(holder.img_images);
        Picasso.get().load(new File(imageItem.getPath())).fit().centerCrop().into(holder.img_images);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
                allImageClickListener.onPicClicked(allImagesModelList, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allImagesModelList.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView img_images;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            img_images = itemView.findViewById(R.id.img_images);
        }
    }
}
