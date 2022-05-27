package gallerypro.galleryapp.bestgallery.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;

import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.interfaces.VideoClickListener;
import gallerypro.galleryapp.bestgallery.model.AllVideoModel;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder> implements Filterable {

    private Context context;
    private ArrayList<AllVideoModel> allVideoList;
    VideoClickListener videoClickListener;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public HashMap<Integer, String> deleteVideoMap = new HashMap<>();

    private ArrayList<AllVideoModel> allVideoSearchList;


    public VideoListAdapter(Context context, ArrayList<AllVideoModel> allVideoList, VideoClickListener videoClickListener) {
        this.context = context;
        this.allVideoList = allVideoList;
        this.videoClickListener = videoClickListener;

        selected_items = new SparseBooleanArray();

        allVideoSearchList = new ArrayList<>(allVideoList);
    }

    @NonNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item,parent,false);
        return new VideoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListViewHolder holder, int position) {
        AllVideoModel allVideoModel = allVideoList.get(position);

//        Glide.with(context).load(allVideoList.get(position).path).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        Picasso.get().load(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,allVideoList.get(position).id)).fit().centerCrop().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoClickListener.OnVideoClick(allVideoList,position);
//                videoClickListener.OnVideoClick(allVideoModel.id,allVideoModel.path,allVideoModel.title,allVideoModel.fileName,allVideoModel.size,allVideoModel.dateAdded,allVideoModel.duration,position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                videoClickListener.OnVideoLongClick(v,allVideoModel,position);
                return true;
            }
        });

        toggleCheckedIcon(holder, position);
        displayImage(holder, allVideoModel);


    }

    @Override
    public int getItemCount() {
        return allVideoList.size();
    }


    private void toggleCheckedIcon(VideoListViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.imageView.setVisibility(View.GONE);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.relativeLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    private void displayImage(VideoListViewHolder holder, AllVideoModel allVideoModel) {
        if (allVideoModel.path != null) {
//            holder.image.setImageResource(allVideoModel.image);
//            holder.imageView.setColorFilter(null);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
//            holder.image.setImageResource(R.drawable.shape_circle);
//            holder.image.setColorFilter(allVideoModel.color);
            holder.imageView.setVisibility(View.VISIBLE);
        }
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
            deleteVideoMap.remove(pos);
        } else {
            selected_items.put(pos, true);
            deleteVideoMap.put(pos,allVideoList.get(pos).getPath());
        }
        notifyItemChanged(pos);
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public HashMap<Integer, String> getDeleteItems(){
        return deleteVideoMap;
    }


    public void removeData(int position){
        allVideoList.remove(position);
    }
    public void clearList() {
        deleteVideoMap.clear();
        resetCurrentIndex();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<AllVideoModel> allVideoFiltered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                allVideoFiltered.addAll(allVideoSearchList);
            }else {
                String filteredPattern = constraint.toString().toLowerCase().trim();
                for(AllVideoModel allVideoModel : allVideoSearchList){
                    if (allVideoModel.title.toLowerCase().contains(filteredPattern)){
                        allVideoFiltered.add(allVideoModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = allVideoFiltered;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allVideoList.clear();
            allVideoList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class VideoListViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout relativeLayout;
        FrameLayout frameLayout;

        public VideoListViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.video_thumbnail);
            relativeLayout = itemView.findViewById(R.id.video_item_selected);
            frameLayout = itemView.findViewById(R.id.video_item_image);

        }
    }
}
