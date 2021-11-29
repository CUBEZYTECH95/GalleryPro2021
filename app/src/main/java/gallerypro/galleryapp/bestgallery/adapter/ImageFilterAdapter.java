package gallerypro.galleryapp.bestgallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.interfaces.FilterListener;

import net.alhazmy13.imagefilter.ImageFilter;

import java.util.List;

public class ImageFilterAdapter extends RecyclerView.Adapter<ImageFilterAdapter.ImageFilterViewHolder> {

    private Context context;
    List<ImageFilter.Filter> allFilter;
    Bitmap bitmap;
    FilterListener filterListener;

    public ImageFilterAdapter(Context context, List<ImageFilter.Filter> allFilter, Bitmap bitmap, FilterListener filterListener) {
        this.context = context;
        this.allFilter = allFilter;
        this.filterListener = filterListener;

        this.bitmap = getResizedBitmap(bitmap,200);


    }

    @NonNull
    @Override
    public ImageFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageFilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_items, parent, false), filterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFilterViewHolder holder, int position) {
        if (position == 0) {
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setImageBitmap(ImageFilter.applyFilter(bitmap, allFilter.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return allFilter.size();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public class ImageFilterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        FilterListener filterListener;

        public ImageFilterViewHolder(@NonNull View itemView, FilterListener filterListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_filter_image);
            this.filterListener = filterListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == 0) {
                filterListener.onFilterClick(null);
            } else {
                filterListener.onFilterClick(allFilter.get(getAdapterPosition()));
            }
        }
    }
}
