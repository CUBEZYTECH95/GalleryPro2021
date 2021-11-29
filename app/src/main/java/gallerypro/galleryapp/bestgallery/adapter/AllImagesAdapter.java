package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.activity.AllImageSliderActivity;
import gallerypro.galleryapp.bestgallery.interfaces.AllImageClickListener;
import gallerypro.galleryapp.bestgallery.model.AllImagesModel;
import gallerypro.galleryapp.bestgallery.model.ImageSliderModel;

import java.util.ArrayList;
import java.util.List;

public class AllImagesAdapter extends RecyclerView.Adapter<AllImagesAdapter.ImageItemViewHolder> implements AllImageClickListener {

    private Context context;
    private List<AllImagesModel> allImages;
    private List<AllImagesModel> allImages1 = new ArrayList<>();
    private AllImageClickListener allImageClickListener;
    ArrayList<String> dateList = new ArrayList<>();
    List<AllImagesModel> allImagesModels = new ArrayList<>();

    private ImageAdapter imageAdapter;

    public AllImagesAdapter(Context context, List<AllImagesModel> allImages,ArrayList<String> dateList, AllImageClickListener allImageClickListener) {
        this.context = context;
        this.allImages = allImages;
        this.allImageClickListener = allImageClickListener;
        this.dateList = dateList;
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        return allImages.get(position).getType();
//
//    }

    @NonNull
    @Override
    public AllImagesAdapter.ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AllImagesAdapter.ImageItemViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v1 = inflater.inflate(R.layout.image_item, parent, false);
                viewHolder = new ImageItemViewHolder(v1);

//        switch (viewType) {
//            case ListItem.TYPE_GENERAL:
//                View v1 = inflater.inflate(R.layout.image_item, parent, false);
//                viewHolder = new ImageItemViewHolder(v1);
//                break;
//            case ListItem.TYPE_DATE:
//                View v2 = inflater.inflate(R.layout.date_item, parent, false);
//                viewHolder = new DateItemViewHolder(v2);
//                break;
//        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final AllImagesAdapter.ImageItemViewHolder holder, final int position) {

//        final ImageItem imageItem = (ImageItem) allImages.get(position);
        AllImagesModel imageItem = allImages.get(position);
        final ImageItemViewHolder imageItemViewHolder = (ImageItemViewHolder) holder;

        holder.textViewDate.setText(imageItem.getDate());
            if (dateList.get(position).equals("")){
                holder.textViewDate.setVisibility(View.GONE);
            } else {
                holder.textViewDate.setVisibility(View.VISIBLE);
            }
        holder.rv_images.setLayoutManager(new GridLayoutManager(context,4));
        holder.rv_images.setHasFixedSize(true);

        List<AllImagesModel> allImages1 = new ArrayList<>();
        List<String> dateList1 = new ArrayList<>();
        imageAdapter = new ImageAdapter(allImages1, dateList1, context,this);
        holder.rv_images.setAdapter(imageAdapter);

        for (int i = 0; i < allImages.size(); i++) {
            if (!dateList.get(position).equals("")) {
                if (dateList.get(position).equals(allImages.get(i).getDate())) {
                    allImages1.add(allImages.get(i));
                    dateList1.add(dateList.get(position));
                    holder.rv_images.getAdapter().notifyDataSetChanged();
//                    imageAdapter = new ImageAdapter(allImages1, dateList, context);
//                    holder.rv_images.setAdapter(imageAdapter);
                }
            }
        }


//            Log.d("TAG", "getAllImages: name " + allImages.get(i).getName() + "\n" + "path " + allImages.get(i).getPath() + "\n" + "date " + allImages.get(i).getDate());


//        Glide.with(context).load(imageItem.getPath()).placeholder(R.mipmap.ic_launcher).apply(new RequestOptions().centerCrop()).into(imageItemViewHolder.imageView);



//        switch (holder.getItemViewType()) {
//
//            case ListItem.TYPE_GENERAL:
//                final ImageItem imageItem = (ImageItem) allImages.get(position);
//                final ImageItemViewHolder imageItemViewHolder = (ImageItemViewHolder) holder;
//                Glide.with(context).load(imageItem.getAllImagesModel().getPath()).placeholder(R.mipmap.ic_launcher).apply(new RequestOptions().centerCrop()).into(imageItemViewHolder.imageView);
//                imageItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        allImageClickListener.onPicClicked(imageItemViewHolder,position);
//                    }
//                });
//                break;
//
//            case ListItem.TYPE_DATE:
//                DateItem dateItem = (DateItem) allImages.get(position);
//                DateItemViewHolder dateItemViewHolder = (DateItemViewHolder) holder;
//                long longDate = Long.parseLong(dateItem.getDate());
//                Date dates = new Date(longDate * 1000L);
//                String dateFormatted = new SimpleDateFormat("dd.MM.yyyy").format(dates);
//
//                dateItemViewHolder.textViewDate.setText(dateFormatted);
//                break;
//
//        }

    }

    @Override
    public int getItemCount() {
        return allImages != null ? allImages.size() : 0;
    }

    @Override
    public void onPicClicked(List<AllImagesModel> allImagesModelList, int position) {
//        Toast.makeText(context, ""+allImagesModelList.get(position).getPath(), Toast.LENGTH_SHORT).show();
        ImageSliderModel imageSliderModel = new ImageSliderModel(allImagesModelList,position);
        Intent intent = new Intent(context, AllImageSliderActivity.class);
        intent.putExtra("key",imageSliderModel);
        context.startActivity(intent);
    }

    public class ImageItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_images;
        TextView textViewDate;

        public ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_images = itemView.findViewById(R.id.rv_images);
            textViewDate = itemView.findViewById(R.id.txt_date);
        }
    }

//    class DateItemViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textViewDate;
//
//        public DateItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewDate = itemView.findViewById(R.id.txt_date);
//        }
//    }


}
