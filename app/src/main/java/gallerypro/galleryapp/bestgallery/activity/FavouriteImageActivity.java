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
import gallerypro.galleryapp.bestgallery.adapter.AlbumPictureAdapter;
import gallerypro.galleryapp.bestgallery.adapter.FavModel;
import gallerypro.galleryapp.bestgallery.database.DbHelper;
import gallerypro.galleryapp.bestgallery.interfaces.AlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.AlbumImageSliderModel;
import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;
import gallerypro.galleryapp.bestgallery.viewholder.PictureViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavouriteImageActivity extends AppCompatActivity implements AlbumClickListener {

    private PreferenceManager preferenceManager;
    private Toolbar toolbar;
    private DbHelper dbHelper;
    private List<AlbumPictureModel> albumPictureModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlbumPictureAdapter adapter;
    private LinearLayout noFavImgContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_image);

        preferenceManager = new PreferenceManager(this);
        dbHelper = new DbHelper(this);

        initToolbar();
        recyclerView = findViewById(R.id.favouriteImageRecyclerView);
        noFavImgContainer = findViewById(R.id.no_fav_image_container);

        setRecyclerView();

    }

    private void setRecyclerView() {
        albumPictureModelList.clear();
//        List<FavModel> favModelsList = dbHelper.getFavourite("1");
        List<String> favModelsList = dbHelper.getFavList();


        if (favModelsList != null && favModelsList.size() > 0) {

            for (int i = 0; i < favModelsList.size(); i++) {
//            Log.d("**********", "onCreate: "+favModelsList.get(i).getId()+"---"+favModelsList.get(i).getFavStatus());
//            Log.d("**********", "onCreate: "+getFavImage(favModelsList.get(i).getId()).getPictureName());

                AlbumPictureModel albumPictureModel = getFavImage(favModelsList.get(i));

                if (albumPictureModel != null) {
                    albumPictureModelList.add(albumPictureModel);
                }
            }
        }

        if (albumPictureModelList != null && albumPictureModelList.size() > 0) {
            noFavImgContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            adapter = new AlbumPictureAdapter((ArrayList<AlbumPictureModel>) albumPictureModelList, this, this);
            recyclerView.setAdapter(adapter);
        } else {
            noFavImgContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

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
        getSupportActionBar().setTitle("Favourite Image");
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

    private AlbumPictureModel getFavImage(String imageId) {
        AlbumPictureModel albumPictureModel = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.HEIGHT,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_MODIFIED};

        Cursor cursor = this.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns._ID + " = ?", new String[]{imageId}, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)));
                String pictureName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                String picturePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                String pictureSize = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE));
                String pictureHeightWidth = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                        + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH));
                long dateTaken = 0;
                try {
                    dateTaken = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                long dateModified = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED)));
                String columnId = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                /*albumPictureModel.setPictureId(id);
                albumPictureModel.setPictureName(pictureName);
                albumPictureModel.setPicturePath(picturePath);
                albumPictureModel.setPictureSize(pictureSize);
                albumPictureModel.setImageHeightWidth(pictureHeightWidth);
                try {
                    albumPictureModel.setDateTaken(dateTaken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                albumPictureModel.setDateModified(dateModified);*/

                if (picturePath != null && new File(picturePath).exists()) {
                    albumPictureModel = new AlbumPictureModel(id, pictureName, picturePath, pictureSize, picturePath, pictureHeightWidth, columnId, dateTaken, dateModified);
                } else {
                    albumPictureModel = null;
                }


            } while (cursor.moveToNext());
            cursor.close();
        }

        return albumPictureModel;
    }

    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }

    @Override
    public void onPicClicked(PictureViewHolder holder, int position, List<AlbumPictureModel> pics) {

        AlbumImageSliderModel albumImageSliderModel = new AlbumImageSliderModel(pics, position);
        Intent intent = new Intent(FavouriteImageActivity.this, AlbumImageSliderActivity.class);
        intent.putExtra("key", albumImageSliderModel);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, AlbumPictureModel obj, int pos) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}