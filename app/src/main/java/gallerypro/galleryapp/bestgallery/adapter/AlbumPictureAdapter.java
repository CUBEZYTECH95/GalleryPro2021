package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.interfaces.AlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.viewholder.PictureViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;

public class AlbumPictureAdapter extends RecyclerView.Adapter<PictureViewHolder> implements Filterable {

    private List<AlbumPictureModel> pictureList;
    private Context context;
    private AlbumClickListener clickListener;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public HashMap<Integer, String> deleteImageMap = new HashMap<>();

    private List<AlbumPictureModel> albumPictureList;
    private VideoDbHelper videoDbHelper;

    public AlbumPictureAdapter(ArrayList<AlbumPictureModel> pictureList, Context context, AlbumClickListener clickListener) {
        this.pictureList = pictureList;
        this.context = context;
        this.clickListener = clickListener;
        selected_items = new SparseBooleanArray();
        videoDbHelper = new VideoDbHelper(context);

        albumPictureList = new ArrayList<>(pictureList);
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.picture_item, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureViewHolder holder, final int position) {

        final AlbumPictureModel pictureModel = pictureList.get(position);

//        Glide.with(context).load(pictureModel.getPicturePath()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        Picasso.get().load(new File(pictureModel.getPicturePath())).fit().centerCrop().into(holder.imageView);

        setTransitionName(holder.imageView, String.valueOf(position) + "_image");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onPicClicked(holder, position, pictureList);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                clickListener.onItemLongClick(v, pictureModel, position);

                return true;
            }
        });

        toggleCheckedIcon(holder,position);
        displayImage(holder,pictureModel,position);

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    private void toggleCheckedIcon(PictureViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.frameLayout.setVisibility(View.GONE);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.relativeLayout.setVisibility(View.GONE);
            holder.frameLayout.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }


    private void displayImage(PictureViewHolder holder, AlbumPictureModel albumPictureModel, int position) {
        if (albumPictureModel.getImageUri() != null) {
//            Glide.with(context).load(albumPictureModel.getPicturePath()).apply(new RequestOptions().fitCenter()).placeholder(R.mipmap.ic_launcher).into(holder.imageView);
            holder.imageView.setColorFilter(null);
            holder.relativeLayout.setVisibility(View.GONE);
//            holder.image_letter.setVisibility(View.GONE);
        } else {
//            holder.image.setImageResource(R.drawable.shape_circle);
//            holder.image.setColorFilter(inbox.color);
            holder.frameLayout.setVisibility(View.VISIBLE);

        }
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
            deleteImageMap.remove(pos);
        } else {
            selected_items.put(pos, true);
            deleteImageMap.put(pos,pictureList.get(pos).getPicturePath());
            Log.d("add", "toggleCheckedIcon: "+pos);
        }
        notifyItemChanged(pos);
    }


    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public HashMap<Integer, String> getDeleteItems(){
        return deleteImageMap;
    }

    public void removeData(int position){

        pictureList.remove(position);
        resetCurrentIndex();
    }



    public void clearList() {
        deleteImageMap.clear();
    }

    @Override
    public Filter getFilter() {
        return imageFilter;
    }

    private Filter imageFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AlbumPictureModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(albumPictureList);
            }else {
                String filteredPattern = constraint.toString().toLowerCase().trim();
                for (AlbumPictureModel albumPictureModel : albumPictureList){
                    if (albumPictureModel.getPictureName().toLowerCase().contains(filteredPattern)){
                        filteredList.add(albumPictureModel);
                    }
                }
            }
            FilterResults  filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pictureList.clear();
            pictureList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
