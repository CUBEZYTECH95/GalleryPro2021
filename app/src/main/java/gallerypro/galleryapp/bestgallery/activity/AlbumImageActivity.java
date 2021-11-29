package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import gallerypro.galleryapp.bestgallery.model.AlbumImageSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;
import gallerypro.galleryapp.bestgallery.adapter.AlbumPictureAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.AlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.viewholder.PictureViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class AlbumImageActivity extends AppCompatActivity implements AlbumClickListener {

    private static final String TAG = "imagealbumlyfecycle";
    Toolbar toolbar;

    private String folderPath;
    private String folderName;
    private ArrayList<AlbumPictureModel> allPictures;
    private RecyclerView recyclerView;
    private AlbumPictureAdapter adapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    private PreferenceManager preferenceManager;


    //delete
    private final List<Integer> deletePosition = new ArrayList<>();

    private List<Integer> getDeletePosition() {
        return this.deletePosition;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.activity_album_image);

        initToolbar();

        recyclerView = findViewById(R.id.albumImageRecyclerView);
        actionModeCallback = new ActionModeCallback();

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        folderPath = getIntent().getStringExtra("folderPath");
        folderName = getIntent().getStringExtra("folderName");

        toolbar.setTitle(folderName);
        toolbar.setTitleTextColor(Color.parseColor("#8B8B8B"));


        setRecyclerView();

    }

    private void setRecyclerView() {
        allPictures = new ArrayList<>();
        allPictures = getAllImageByFolder(folderPath);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AlbumPictureAdapter(allPictures, this, this);
        Log.d(TAG, "deleteImages: " + allPictures.size());
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onRestart: ");
        setRecyclerView();

        if (allPictures.isEmpty()) {
            finish();
        }
        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }


    private void initToolbar() {
        preferenceManager = new PreferenceManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inbox");
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

//        Tools.FullScreencall(this);


    }


    public ArrayList<AlbumPictureModel> getAllImageByFolder(String path) {

        ArrayList<AlbumPictureModel> images = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.SIZE
                , MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_MODIFIED};

        Cursor cursor = this.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns.DATA + " like ?", new String[]{"%" + path + "%"}, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                AlbumPictureModel pic = new AlbumPictureModel();
                pic.setPictureId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))));

                pic.setPictureName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE)));

                pic.setImageHeightWidth(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                        + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)));


                pic.setColumnsId(String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))));

                try {
                    pic.setDateTaken(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pic.setDateModified(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED))));

                images.add(pic);
            }

            cursor.close();
        }


        ArrayList<AlbumPictureModel> reSelection = new ArrayList<>();
        for (int i = images.size() - 1; i > -1; i--) {
            reSelection.add(images.get(i));
        }


        images = reSelection;

        for (int i = 0; i < images.size(); i++) {
            Log.d("******", "getAllImageByFolder: " + images.get(i).getPictureId() + "\n " + images.get(i).getPictureId() + " path : " + images.get(i).getPicturePath());
        }


        return images;

    }

    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPicClicked(PictureViewHolder holder, int position, List<AlbumPictureModel> pics) {

        if (adapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {

            AlbumImageSliderModel albumImageSliderModel = new AlbumImageSliderModel(pics, position);
            Intent intent = new Intent(AlbumImageActivity.this, AlbumImageSliderActivity.class);
            intent.putExtra("key", albumImageSliderModel);
            startActivity(intent);

//            ImageSliderFragment imageSliderFragment = ImageSliderFragment.newInstance(pics, position, AlbumImageActivity.this);
//
//            imageSliderFragment.setEnterTransition(new Slide());
//            imageSliderFragment.setExitTransition(new Slide());
//            // uncomment this to use slide transition and comment the two lines below
////            imageSliderFragment.setEnterTransition(new android.transition.ChangeBounds());
////            imageSliderFragment.setExitTransition(new android.transition.ChangeBounds());
//
//            getSupportFragmentManager()
//                    .beginTransaction()
////                .addSharedElement(holder.imageView, position + "picture")
//                    .add(R.id.displayContainer, imageSliderFragment)
//                    .addToBackStack(null)
//                    .commit();

        }

    }

    @Override
    public void onItemLongClick(View view, AlbumPictureModel obj, int pos) {
        enableActionMode(pos);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        int totalImage = adapter.getItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + " / " + totalImage);
            actionMode.invalidate();
        }

    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(AlbumImageActivity.this, R.color.colorWhite2);
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
                // delete image then  adapter.notifyDataSetChange()

                deleteImages(mode);
//                mode.finish();
                return true;
            } else if (id == R.id.action_share) {
                shareImages();
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
            adapter.clearList();
            actionMode = null;
            Tools.setSystemBarColor(AlbumImageActivity.this, R.color.colorPrimary);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    private void deleteImages(ActionMode mode) {


        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<Integer, String> deletePath = adapter.getDeleteItems();
//                Uri[] uriList = new Uri[]{};
                List<Uri> uriList = new ArrayList<>();
//        Iterator iterator = deletePath.values().iterator();

               /* for (int i = 0; i < deletePath.keySet().size(); i++) {
                    String path = deletePath.get(i);
                    if (path != null) {
                        uriList.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, path));

//                            uriList.add(getContentUri(Uri.fromFile(file)));
                    }
                }*/

                for (Integer i : deletePath.keySet()) {
                    String path = deletePath.get(i);
                    if (path != null) {
                        uriList.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, path));
                        deletePosition.add(i);

                    }
                }

                try {
                    delete(AlbumImageActivity.this, uriList, 105, null, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

               /* for (Integer i : deletePath.keySet()) {
                    String path = deletePath.get(i);
                    Log.d("Hasmap From Data", "deleteImages: position : " + i + " Path : " + path);
                    File file = new File(path);

                    if (file.exists()) {
                        if (file.delete()) {
                            adapter.removeData(i);
                            AlbumImageActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            adapter.notifyDataSetChanged();

                        }
                    } else {
                        Toast.makeText(AlbumImageActivity.this, "file Not exists", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {
            if (resultCode == RESULT_OK) {
                for (Integer i : getDeletePosition()) {
                    adapter.removeData(i);
                }
                adapter.notifyDataSetChanged();
                deletePosition.clear();
            } else {
                deletePosition.clear();
                Toast.makeText(this, "Delete Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void delete(final Activity activity, final List<Uri> uriList, final int requestCode, String imageId, int i)
            throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException {
        final ContentResolver resolver = activity.getContentResolver();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            final List<Uri> list = new ArrayList<>();
//            Collections.addAll(list, uriList);
            final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, uriList);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {

                for (final Uri uri : uriList) {
                    resolver.delete(uri, null, null);
                }
                for (Integer j : getDeletePosition()) {
                    adapter.removeData(j);
                }
                adapter.notifyDataSetChanged();
                this.deletePosition.clear();
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();

            } catch (RecoverableSecurityException ex) {
                final IntentSender intent = ex.getUserAction()
                        .getActionIntent()
                        .getIntentSender();
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);

            }
        } else {
            for (final Uri uri : uriList) {
                resolver.delete(uri, null, null);
            }
            for (Integer k : getDeletePosition()) {
                adapter.removeData(k);
            }
            adapter.notifyDataSetChanged();
            this.deletePosition.clear();
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();

        }
    }

    private Uri getContentUri(Uri imageUri) {
        long id = 0;
        String[] projection = {MediaStore.MediaColumns._ID};
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.MediaColumns.DATA + "=?", new String[]{imageUri.getPath()}, null);
        if (cursor != null) {

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
            cursor.close();
        }
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
    }

    private void shareImages() {
        HashMap<Integer, String> sharePath = adapter.getDeleteItems();
        ArrayList<Uri> files = new ArrayList<>();
        Iterator iterator = sharePath.values().iterator();
        while (iterator.hasNext()) {
            String shareImagePath = iterator.next().toString();
            File file = new File(shareImagePath);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(intent);
    }


    private void callBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

}