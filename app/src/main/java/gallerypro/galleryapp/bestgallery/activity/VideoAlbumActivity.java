package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.model.VideoSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;
import gallerypro.galleryapp.bestgallery.adapter.VideoListAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.VideoClickListener;
import gallerypro.galleryapp.bestgallery.model.AllVideoModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VideoAlbumActivity extends AppCompatActivity implements VideoClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private String videoAlbumPath;
    private String albumName;

    private VideoListAdapter videoListAdapter;

    private ActionMode actionMode;
    private VideoActionModeCallback videoActionModeCallback;

    private ArrayList<AllVideoModel> allAlbumVideo = new ArrayList<>();

    private PreferenceManager preferenceManager;

    private VideoDbHelper videoDbHelper;
    private List<Integer> videoIds = new ArrayList<>();

    //delete
    private final List<Integer> deletePosition = new ArrayList<>();

    private List<Integer> getDeletePosition() {
        return this.deletePosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_album);

        initToolbar();
        recyclerView = findViewById(R.id.video_album_recyclerview);

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        videoAlbumPath = getIntent().getStringExtra("videoAlbumPath");
        albumName = getIntent().getStringExtra("albumName");

        toolbar.setTitle(albumName);
        toolbar.setTitleTextColor(Color.parseColor("#8B8B8B"));


        setRecyclerView();
//        allAlbumVideo = getAllVideo(albumName);
//
        videoDbHelper = new VideoDbHelper(this);
//        for (int i = 0; i<allAlbumVideo.size(); i++){
//            videoIds.add(allAlbumVideo.get(i).id);
//        }
//
//        videoDbHelper.createEmptyTable(videoIds);
//
//
//
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//
//        videoListAdapter = new VideoListAdapter(this, allAlbumVideo, this);
//        recyclerView.setAdapter(videoListAdapter);
//
//        videoActionModeCallback = new VideoActionModeCallback();

    }

    private void setRecyclerView() {
        allAlbumVideo = getAllVideo(albumName);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        videoListAdapter = new VideoListAdapter(this, allAlbumVideo, this);
        recyclerView.setAdapter(videoListAdapter);

        videoActionModeCallback = new VideoActionModeCallback();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setRecyclerView();

    }


    void initToolbar() {
        preferenceManager = new PreferenceManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_video_album);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inbox");

/*        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }*/
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private ArrayList<AllVideoModel> getAllVideo(String path) {
        ArrayList<AllVideoModel> tempVideoList = new ArrayList<>();

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

        String selection = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%" + path + "%"};
        String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN;

        Cursor cursor = VideoAlbumActivity.this.getContentResolver().query(uri, projection, selection, selectionArgs, orderBy + " DESC");

        if (cursor != null) {

            while (cursor.moveToNext()) {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID)));
                String paths = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.TITLE));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATE_ADDED));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                String columnId = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)));

                AllVideoModel allVideoModel = new AllVideoModel(id, paths, title, fileName, size, dateAdded, duration, columnId);
                tempVideoList.add(allVideoModel);

            }
            cursor.close();
        }

        for (AllVideoModel video : tempVideoList) {
            Log.d("AllVideoFolder", "getAllVideo: id :" + video.id + "\n path : " + video.path);
        }

        return tempVideoList;

    }

//    @Override
//    public void OnVideoClick(int id, String path, String title, String fileName, String size, String dateAdded, String duration, int position) {
//
//        if (videoListAdapter.getSelectedItemCount() > 0) {
//            enableActionMode(position);
//        } else {
//
//            Intent intent = new Intent(VideoAlbumActivity.this, VideoPlayerActivity.class);
//            intent.putExtra("id", id);
//            intent.putExtra("path", path);
//            intent.putExtra("title", title);
//            intent.putExtra("fileName", fileName);
//            intent.putExtra("size", size);
//            intent.putExtra("dateAdded", dateAdded);
//            intent.putExtra("duration", duration);
//            startActivity(intent);
//        }
//    }

    @Override
    public void OnVideoClick(List<AllVideoModel> video, int position) {
        if (videoListAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {

//            AlbumImageSliderModel albumImageSliderModel = new AlbumImageSliderModel(pics, position);
//            Intent intent = new Intent(AlbumImageActivity.this, AlbumImageSliderActivity.class);
//            intent.putExtra("key", albumImageSliderModel);
//            startActivity(intent);

            VideoSliderModel videoSliderModel = new VideoSliderModel(video, position);
            Intent intent = new Intent(VideoAlbumActivity.this, VideoSliderActivity.class);
            intent.putExtra("key", videoSliderModel);
            startActivity(intent);

        }

    }

    @Override
    public void OnVideoLongClick(View v, AllVideoModel allVideoModel, int position) {
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(videoActionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        videoListAdapter.toggleSelection(position);
        int count = videoListAdapter.getSelectedItemCount();
        int totalVideo = videoListAdapter.getItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + " / " + totalVideo);
            actionMode.invalidate();
        }
    }


    class VideoActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(VideoAlbumActivity.this, R.color.colorDarkBlue2);
            mode.getMenuInflater().inflate(R.menu.menu_item_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                //delete Video
                deleteVideo(mode);
                return true;
            } else if (id == R.id.action_share) {
                shareVideos();
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            videoListAdapter.clearSelections();
            videoListAdapter.clearList();
            actionMode = null;
            Tools.setSystemBarColor(VideoAlbumActivity.this, R.color.colorPrimary);

        }
    }

    private void shareVideos() {
        HashMap<Integer, String> sharePath = videoListAdapter.getDeleteItems();
        Iterator iterator = sharePath.values().iterator();
        ArrayList<Uri> files = new ArrayList<>();
        while (iterator.hasNext()) {
            String videoPath = iterator.next().toString();
            File file = new File(videoPath);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            files.add(uri);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("video/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
    }

    private String getContentUri(Uri imageUri) {
        long id = 0;
        String[] projection = {MediaStore.MediaColumns._ID};
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.MediaColumns.DATA + "=?", new String[]{imageUri.getPath()}, null);
        if (cursor != null) {

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
            cursor.close();
        }
        return String.valueOf(id);
    }

    private void deleteVideo(ActionMode mode) {

        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<Integer, String> deletePath = videoListAdapter.getDeleteItems();
                List<Uri> uriList = new ArrayList<>();

                for (Integer i : deletePath.keySet()) {
                    String path = deletePath.get(i);
                    String contentUri = getContentUri(Uri.parse(path));
                    if (path != null) {
                        uriList.add(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentUri));
                        deletePosition.add(i);
                    }
                }

                try {
                    deleteMultipleVideo(VideoAlbumActivity.this,uriList,158);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

                /*for (Integer i : deletePath.keySet()) {
                    String path = deletePath.get(i);

                    File file = new File(path);
                    if (file.exists()) {
                        if (file.delete()) {

                            videoListAdapter.removeData(i);
                            VideoAlbumActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            videoListAdapter.notifyDataSetChanged();

                        }
                    } else {
                        Toast.makeText(VideoAlbumActivity.this, "Image Not exists", Toast.LENGTH_SHORT).show();
                    }

                }*/


                dialog.dismiss();
                mode.finish();
            }
        });
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mode.finish();
            }
        });


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();


    }

    private void deleteMultipleVideo(final Activity activity, final List<Uri> uriList, final int requestCode) throws IntentSender.SendIntentException {

        final ContentResolver contentResolver = activity.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                for (Uri uri : uriList) {
                    contentResolver.delete(uri, null, null);
                }

                for (Integer j : getDeletePosition()) {
                    videoListAdapter.removeData(j);
                }
                videoListAdapter.notifyDataSetChanged();
                this.deletePosition.clear();
                Toast.makeText(this, "Videos deleted", Toast.LENGTH_SHORT).show();
            } catch (RecoverableSecurityException e) {
                final IntentSender intent = e.getUserAction().getActionIntent().getIntentSender();
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
            }
        } else {
            for (Uri uri : uriList) {
                contentResolver.delete(uri, null, null);
            }
            for (Integer k : getDeletePosition()) {
                videoListAdapter.removeData(k);
            }
            videoListAdapter.notifyDataSetChanged();
            this.deletePosition.clear();
            Toast.makeText(this, "Videos deleted", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 158) {
            if (resultCode == RESULT_OK) {
                for (Integer i : getDeletePosition()) {
                    videoListAdapter.removeData(i);
                }
                videoListAdapter.notifyDataSetChanged();
            }
            this.deletePosition.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.album_image_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_album_img);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                videoListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}