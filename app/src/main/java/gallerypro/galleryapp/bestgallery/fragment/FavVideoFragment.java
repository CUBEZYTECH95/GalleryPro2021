package gallerypro.galleryapp.bestgallery.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.activity.FavouriteVideoActivity;
import gallerypro.galleryapp.bestgallery.activity.VideoSliderActivity;
import gallerypro.galleryapp.bestgallery.adapter.VideoListAdapter;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.interfaces.VideoClickListener;
import gallerypro.galleryapp.bestgallery.model.AllVideoModel;
import gallerypro.galleryapp.bestgallery.model.VideoSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;


public class FavVideoFragment extends Fragment implements VideoClickListener {

    private PreferenceManager preferenceManager;
    private VideoDbHelper videoDbHelper;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<AllVideoModel> allVideoModelList = new ArrayList<>();
    private VideoListAdapter videoListAdapter;
    private LinearLayout noFavVideoContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_video, container, false);

        preferenceManager = new PreferenceManager(getContext());
        videoDbHelper = new VideoDbHelper(getContext());
        getActivity().setTitle("Favourite");
        initToolbar();

        recyclerView = view.findViewById(R.id.favouriteVideoRecyclerView);
        noFavVideoContainer = view.findViewById(R.id.no_fav_video_container);


        setRecyclerView();

        return view;
    }

    private void initToolbar() {
        preferenceManager = new PreferenceManager(getContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }

    private void setRecyclerView() {

        allVideoModelList.clear();
//        List<FavModel> favModelsList = videoDbHelper.getFavourite("1");
        List<String> favModelsList = videoDbHelper.getFavList();
        if (favModelsList != null && favModelsList.size() > 0) {

            for (int i = 0; i < favModelsList.size(); i++) {
//            Log.d("**********", "onCreate: "+favModelsList.get(i).getId()+"---"+favModelsList.get(i).getFavStatus());
//            Log.d("**********", "onCreate: "+getFavImage(favModelsList.get(i).getId()).getPictureName());


                AllVideoModel albumPictureModel = getFavouriteVideo(favModelsList.get(i));

                if (albumPictureModel != null) {
                    allVideoModelList.add(albumPictureModel);
                }
            }
        }

        if (allVideoModelList != null && allVideoModelList.size() > 0) {


            noFavVideoContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            videoListAdapter = new VideoListAdapter(getContext(), (ArrayList<AllVideoModel>) allVideoModelList, this);
            recyclerView.setAdapter(videoListAdapter);

        } else {
            noFavVideoContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }



    }

    private AllVideoModel getFavouriteVideo(String id) {

        AllVideoModel allVideoModel = null;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.DURATION
        };

        Cursor cursor = getContext().getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns._ID + " = ?", new String[]{id}, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                int ids = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.TITLE));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                String columnId = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                if (path != null && new File(path).exists()) {

                    /*allVideoModel.setId(ids);
                    allVideoModel.setPath(path);
                    allVideoModel.setTitle(title);
                    allVideoModel.setFileName(fileName);
                    allVideoModel.setSize(size);
                    allVideoModel.setDateAdded(dateAdded);
                    allVideoModel.setDuration(duration);*/
                    allVideoModel = new AllVideoModel(ids, path, title, fileName, size, dateAdded, duration, columnId);

                } else {
                    allVideoModel = null;
                }

            } while (cursor.moveToNext());

            cursor.close();
        }


        return allVideoModel;
    }

    @Override
    public void OnVideoClick(List<AllVideoModel> video, int position) {
        VideoSliderModel videoSliderModel = new VideoSliderModel(video, position);
        Intent intent = new Intent(getActivity(), VideoSliderActivity.class);
        intent.putExtra("key", videoSliderModel);
        startActivity(intent);
    }

    @Override
    public void OnVideoLongClick(View v, AllVideoModel allVideoModel, int position) {

    }
}