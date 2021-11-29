package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.adapter.FavModel;
import gallerypro.galleryapp.bestgallery.adapter.VideoListAdapter;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.interfaces.VideoClickListener;
import gallerypro.galleryapp.bestgallery.model.AllVideoModel;
import gallerypro.galleryapp.bestgallery.model.VideoSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavouriteVideoActivity extends AppCompatActivity implements VideoClickListener {

    private PreferenceManager preferenceManager;
    private VideoDbHelper videoDbHelper;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<AllVideoModel> allVideoModelList = new ArrayList<>();
    private VideoListAdapter videoListAdapter;
    private LinearLayout noFavVideoContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_video);

        preferenceManager = new PreferenceManager(this);
        videoDbHelper = new VideoDbHelper(this);

        initToolbar();

        recyclerView = findViewById(R.id.favouriteVideoRecyclerView);
        noFavVideoContainer = findViewById(R.id.no_fav_video_container);


        setRecyclerView();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setRecyclerView();
    }

    private void initToolbar() {
        preferenceManager = new PreferenceManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favourite Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Tools.setSystemBarColor(this, R.color.colorPrimary);
        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }
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
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            videoListAdapter = new VideoListAdapter(this, (ArrayList<AllVideoModel>) allVideoModelList, this);
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

        Cursor cursor = FavouriteVideoActivity.this.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns._ID + " = ?", new String[]{id}, null);
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

//    @Override
//    public void OnVideoClick(int id, String path, String title, String fileName, String size, String dateAdded, String duration, int position) {
//
//
//        Intent intent = new Intent(this, VideoPlayerActivity.class);
//        intent.putExtra("id", id);
//        intent.putExtra("path", path);
//        intent.putExtra("title", title);
//        intent.putExtra("fileName", fileName);
//        intent.putExtra("size", size);
//        intent.putExtra("dateAdded", dateAdded);
//        intent.putExtra("duration", duration);
//        startActivity(intent);
//
//    }

    @Override
    public void OnVideoClick(List<AllVideoModel> video, int position) {
        VideoSliderModel videoSliderModel = new VideoSliderModel(video, position);
        Intent intent = new Intent(FavouriteVideoActivity.this, VideoSliderActivity.class);
        intent.putExtra("key", videoSliderModel);
        startActivity(intent);

    }

    @Override
    public void OnVideoLongClick(View v, AllVideoModel allVideoModel, int position) {

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}