package gallerypro.galleryapp.bestgallery.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DrawerTransformer;

import gallerypro.galleryapp.bestgallery.R;

import gallerypro.galleryapp.bestgallery.database.DbHelper;
import gallerypro.galleryapp.bestgallery.model.AlbumImageSliderModel;
import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.text.CharacterIterator;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class AlbumImageSliderActivity extends AppCompatActivity {

    private List<AlbumPictureModel> allImage;
    private int position;

    private ViewPager viewPager;

    private ImageView imageView;
    private TextView textViewImageTitle;
    private ImageView imageViewBack;
    private ImageView imageViewLike;
    int mAdCount = 0;

    //imagePager
//    private ImageSliderFragment.ImagePager imagePager;

    private ImagePager imagesPager;

    //top bar
    private ImageView ivInfo;
    private ImageView ivMoreOption;

    // Bottom Bar
    private ImageView ivRotate;
    private ImageView ivEdit;
    private ImageView ivShare;
    private ImageView ivDelete;

    //db
    private DbHelper dbHelper;
    private List<Integer> imageIds = new ArrayList<>();

    // prefrence
    PreferenceManager preferenceManager;

    ActivityResultLauncher<IntentSenderRequest> intentSenderRequest;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;


    //delete
    int deletePosition;
    String imageID;

    int getPos() {
        return this.deletePosition;
    }

    String getImageID() {
        return this.imageID;
    }

    //modified

    private AlbumPictureModel renameModel;

    AlbumPictureModel getRenameModel() {
        return this.renameModel;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_image_slider);

        preferenceManager = new PreferenceManager(this);

        /*if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite2));
        }*/

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AlbumImageSliderModel albumImageSliderModel = (AlbumImageSliderModel) getIntent().getSerializableExtra("key");
        allImage = albumImageSliderModel.getAlbumPictureModelList();
        position = albumImageSliderModel.getPosition();

        dbHelper = new DbHelper(this);

        /*for (int i = 0; i < allImage.size(); i++) {
            imageIds.add(allImage.get(i).getPictureId());
        }
        dbHelper.createEmptyTable(imageIds);*/

        ivInfo = findViewById(R.id.img_info);
        imageViewLike = findViewById(R.id.img_like);
        ivMoreOption = findViewById(R.id.iv_more_option);

        imageViewBack = findViewById(R.id.backImageView);
        ivRotate = findViewById(R.id.image_rotate);
        ivEdit = findViewById(R.id.image_edit);
        ivShare = findViewById(R.id.image_share);
        ivDelete = findViewById(R.id.image_delete);


        viewPager = findViewById(R.id.albumImageviewPage);
        textViewImageTitle = findViewById(R.id.image_title);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //            textViewImageTitle.setText(allImage.get(position).getPictureName());
        imagesPager = new ImagePager();
        viewPager.setAdapter(imagesPager);
        viewPager.setPageTransformer(true, (ViewPager.PageTransformer) new DrawerTransformer());
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                if (position < (imagesPager.getCount() - 1) && position < (allImage.size() - 1)) {
                    textViewImageTitle.setText(allImage.get(position).getPictureName());
                    if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getPictureId()))) {
                        imageViewLike.setImageResource(R.drawable.ic_liked);
                        ivRotate.setImageResource(R.drawable.ic_liked);
                    } else {
                        imageViewLike.setImageResource(R.drawable.ic_like_24);
                        ivRotate.setImageResource(R.drawable.ic_like_24);
                    }
                    imageViewLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getPictureId()))) {
                                imageViewLike.setImageResource(R.drawable.ic_like_24);
                                ivRotate.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(position).getPictureId()));
                            } else {
                                imageViewLike.setImageResource(R.drawable.ic_liked);
                                ivRotate.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(position).getPictureId()));
                            }
                        }
                    });
                    ivInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageInfo(allImage.get(position));

                        }
                    });

                    ivRotate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            hideImage(position);
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getPictureId()))) {
                                imageViewLike.setImageResource(R.drawable.ic_like_24);
                                ivRotate.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(position).getPictureId()));
                            } else {
                                imageViewLike.setImageResource(R.drawable.ic_liked);
                                ivRotate.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(position).getPictureId()));
                            }
                        }
                    });

                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AlbumImageSliderActivity.this, EditActivity.class);
                            intent.putExtra("img", allImage.get(position).getPicturePath());
                            startActivity(intent);
                        }
                    });

                    ivShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(position);
                        }
                    });
                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allImage.get(position).getPicturePath());
                            int i = position;
                            showDeleteDialog(allImage.get(position).getPicturePath(), allImage.get(position).getColumnsId(), i, String.valueOf(allImage.get(position).getPictureId()));
                        }
                    });
                    ivMoreOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageOptionMenu(position);
                        }
                    });
                } else {

                    if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()))) {
                        imageViewLike.setImageResource(R.drawable.ic_liked);
                        ivRotate.setImageResource(R.drawable.ic_liked);
                    } else {
                        imageViewLike.setImageResource(R.drawable.ic_like_24);
                        ivRotate.setImageResource(R.drawable.ic_like_24);
                    }

                    imageViewLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()))) {
                                imageViewLike.setImageResource(R.drawable.ic_like_24);
                                ivRotate.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()));
                            } else {
                                imageViewLike.setImageResource(R.drawable.ic_liked);
                                ivRotate.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()));
                            }
                        }
                    });

                    textViewImageTitle.setText(allImage.get(allImage.size() - 1).getPictureName());
                    ivInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageInfo(allImage.get(allImage.size() - 1));
                        }
                    });
                    ivShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(allImage.size() - 1);
                        }
                    });

                    ivRotate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            hideImage(allImage.size() - 1);
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()))) {
                                imageViewLike.setImageResource(R.drawable.ic_like_24);
                                ivRotate.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()));
                            } else {
                                imageViewLike.setImageResource(R.drawable.ic_liked);
                                ivRotate.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()));
                            }
                        }
                    });

                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AlbumImageSliderActivity.this, EditActivity.class);
                            intent.putExtra("img", allImage.get(allImage.size() - 1).getPicturePath());
                            startActivity(intent);
                        }
                    });

                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allImage.get(allImage.size() - 1).getPicturePath());
                            int i = allImage.size() - 1;
                            showDeleteDialog(allImage.get(allImage.size() - 1).getPicturePath(), allImage.get(allImage.size() - 1).getColumnsId(), i, String.valueOf(allImage.get(allImage.size() - 1).getPictureId()));
                        }
                    });

                    ivMoreOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageOptionMenu(allImage.size() - 1);
                        }
                    });

                }

            }

            @Override
            public void onPageSelected(int position) {

                mAdCount++;
                if (mAdCount % 5 == 0) {
                    if (mAdManagerInterstitialAd != null) {
                        mAdManagerInterstitialAd.show(AlbumImageSliderActivity.this);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFbInterstitialAds();
    }

    private void loadFbInterstitialAds() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(AlbumImageSliderActivity.this, getString(R.string.admob_interstitial), adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        mAdManagerInterstitialAd = interstitialAd;
                        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
//                                Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
//                                intent.putExtra("folderPath", pictureFolderPath);
//                                intent.putExtra("folderName", folderName);
//                                startActivity(intent);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {

                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mAdManagerInterstitialAd = null;
//                                Date date = new Date();
////                if (preferenceManager.getAdsTime() == 0){
//                                preferenceManager.saveAdsTime(date.getTime() + (10 * 60 * 1000)); // 1800000 milisecound
//                }
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        mAdManagerInterstitialAd = null;
//                        Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
//                        intent.putExtra("folderPath", pictureFolderPath);
//                        intent.putExtra("folderName", folderName);
//                        startActivity(intent);
                    }

                });

        /*interstitialAd = new InterstitialAd(getContext(), getString(R.string.facebook_interstitial));

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Date date = new Date();
//                if (preferenceManager.getAdsTime() == 0){
                preferenceManager.saveAdsTime(date.getTime() + 1800000);
//                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
                intent.putExtra("folderPath", pictureFolderPath);
                intent.putExtra("folderName", folderName);
                startActivity(intent);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
*/

    }

    private void imageOptionMenu(int position) {
        PopupMenu popupMenu = new PopupMenu(AlbumImageSliderActivity.this, ivMoreOption);
        popupMenu.getMenuInflater().inflate(R.menu.image_popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_hide_image:

                        hideImage(position);

                        break;
                }
                return true;
            }
        });
        popupMenu.show();


    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void hideImage(int position) {

        if (preferenceManager.isPinSeted()) {

            newRename(allImage.get(position), this);

//            hideImageApi30(allImage.get(position));

           /* File hideFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                hideFile = new File(this.getExternalFilesDir(null), ".HideImage");
            } else {

                hideFile = new File(Environment.getExternalStorageDirectory(), ".HideImage");
            }


            if (!hideFile.exists()) {
                hideFile.mkdir();
//                Toast.makeText(AlbumImageSliderActivity.this, "File not Existed", Toast.LENGTH_SHORT).show();
//                Toast.makeText(AlbumImageSliderActivity.this, "" + hideFile.getPath(), Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(AlbumImageSliderActivity.this, "File Existed", Toast.LENGTH_SHORT).show();
//                Toast.makeText(AlbumImageSliderActivity.this, "" + hideFile.getPath(), Toast.LENGTH_SHORT).show();
            }


            File file = new File(allImage.get(position).getPicturePath());

            try {// TODO:  MOVE FILE
                *//**
             * @prmam file old file
             * @param hilde file path
             * @param position
             *//*
                moveFile(file, hideFile, position);
            } catch (IOException e) {
                e.printStackTrace();
//                Toast.makeText(AlbumImageSliderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Hide File Exception", "Exception: " + e.getMessage());
            }


            Toast.makeText(AlbumImageSliderActivity.this, "Hide image", Toast.LENGTH_SHORT).show();*/

        } else {
            startActivity(new Intent(AlbumImageSliderActivity.this, SecurityActivity.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void hideImageApi30(AlbumPictureModel model) {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ".hideImage");

        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(model.getPicturePath());


        long mediaID = getFilePathToMediaID(new File(file.getAbsolutePath()).getAbsolutePath(), this);
        Uri utiOne =ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),mediaID);

        String path = dir.getAbsolutePath()+File.separator+model.getPictureName();
        Toast.makeText(this, "" +model.getPicturePath(), Toast.LENGTH_SHORT).show();

        ContentValues cv = new ContentValues();
//        cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME,".hideImage");
//        cv.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+".hideImage");
        cv.put(MediaStore.Images.Media.DATA,path);
//        cv.put(MediaStore.Images.Media._ID,model.getPictureId());

        getContentResolver().update(utiOne,cv,null);




       /* File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ".hideImage");
        Toast.makeText(this, "" + dir, Toast.LENGTH_SHORT).show();
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(model.getPicturePath());

        File newFile = new File(dir, file.getName());

        boolean b = file.renameTo(newFile);


        Toast.makeText(this, "" + b + "-----" + file.getName(), Toast.LENGTH_SHORT).show();

//        Toast.makeText(this, ""+file, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, Environment.DIRECTORY_PICTURES + File.separator + ".HideImage" + File.separator + file.getName(), Toast.LENGTH_SHORT).show();

        ContentResolver resolver = getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media._ID, model.getPictureId());
//        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + ".hideImage");
        cv.put(MediaStore.Images.Media.DATA, dir.getAbsolutePath() + File.separator + file.getName());
        resolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv, MediaStore.Images.Media._ID + "=?", new String[]{String.valueOf(model.getPictureId())});

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.parse(Environment.DIRECTORY_PICTURES + File.separator + ".HideImage" + File.separator + file.getName()));
        sendBroadcast(intent);*/
    }

    Long getIdFromDisplayName(String displayName) {
        String[] projection;
        projection = new String[]{MediaStore.Files.FileColumns._ID};

        // TODO This will break if we have no matching item in the MediaStore.
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), projection,
                MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?", new String[]{displayName}, null);
        assert cursor != null;
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            int columnIndex = cursor.getColumnIndex(projection[0]);
            long fileId = cursor.getLong(columnIndex);

            cursor.close();
            return fileId;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void newRename(AlbumPictureModel model, Activity activity) {
        List<Uri> uris = new ArrayList<>();
        long mediaID = getFilePathToMediaID(new File(model.getPicturePath()).getAbsolutePath(), this);

        Uri uriOne = ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri("external"), mediaID);
        uris.add(uriOne);
        this.renameModel = model;
        PendingIntent pi = MediaStore.createWriteRequest(this.getContentResolver(), uris);

        try {
            activity.startIntentSenderForResult(pi.getIntentSender(), 588, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    private void shareImage(int position) {
        File file = new File(allImage.get(position).getPicturePath());
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }



        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "share via"));
    }

    /**
     * @param file old image file path
     * @param dir  hide file path
     * @param i    arraylist position
     * @throws IOException
     */

    private void moveFile(File file, File dir, int i) throws IOException {

      /*  File newFile = new File(dir, file.getName());

//        boolean b = file.renameTo(newFile);

//        Toast.makeText(this, ""+b, Toast.LENGTH_SHORT).show();

       ContentResolver resolver = getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media._ID,allImage.get(position).getPictureId());
        contentValues.put(MediaStore.Images.Media.DATA,newFile.toString());



        resolver.update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues,MediaStore.Images.Media._ID + "=?",new String[]{String.valueOf(allImage.get(position).getPictureId())});
*/
        //

        File newFile = new File(dir, file.getName());
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {

            ArrayList<String> hideList = new ArrayList<>();
            if (getArrayList().size() > 0) {
                hideList.addAll(getArrayList());
            }
            hideList.add(file.getParentFile().getAbsolutePath());
            saveArrayList(new ArrayList<>());
            Collections.reverse(hideList);
            saveArrayList(hideList);


            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();

            boolean isDeleted = file.delete();
            if (isDeleted) {
//                Toast.makeText(this, "file deleted", Toast.LENGTH_SHORT).show();

                allImage.remove(i);
                this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                imagesPager.notifyDataSetChanged();
            }
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }

        imagesPager.notifyDataSetChanged();

    }

    public ArrayList<String> getArrayList() {
        ArrayList<String> hideList = new ArrayList<>();
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("HIDE_LIST", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        if (json.equals("")) {
            return hideList;
        } else {
            hideList = gson.fromJson(json, type);
        }
        return hideList;
    }

    public static long getFilePathToMediaID(String songPath, Context context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Audio.Media._ID};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }

    public void saveArrayList(ArrayList<String> list) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json;
        json = gson.toJson(list);
        editor.putString("HIDE_LIST", json);
        editor.apply();
    }

    private class ImagePager extends PagerAdapter {

        @Override
        public int getCount() {
            return allImage.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (View) object;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.display_image_item, null);
            imageView = view.findViewById(R.id.zoomImageView);


            final AlbumPictureModel albumPictureModel = allImage.get(position);
//            Glide.with(container.getContext()).load(albumPictureModel.getPicturePath()).apply(new RequestOptions().fitCenter()).placeholder(R.drawable.no_image_bg).into(imageView);
            Picasso.get().load(new File(albumPictureModel.getPicturePath())).placeholder(R.drawable.no_image_bg).into(imageView);
            ((ViewPager) container).addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }


    // Image Info
    private void showImageInfo(AlbumPictureModel albumPictureModel) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.details_dialog, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        Button button = view.findViewById(R.id.btn_details_ok);
        TextView tvImageSize = view.findViewById(R.id.tv_image_size);
        TextView tvImagePath = view.findViewById(R.id.tv_image_path);
        TextView tvImageName = view.findViewById(R.id.tv_image_name);
        TextView tvImageResolution = view.findViewById(R.id.iv_image_resolution);
        TextView tvDateTaken = view.findViewById(R.id.tv_date_taken);
        TextView tvDateModified = view.findViewById(R.id.tv_date_modified);

        tvImageSize.setText(humanReadableByteCountSI(Long.parseLong(albumPictureModel.getPictureSize())));
        tvImagePath.setText(albumPictureModel.getPicturePath());
        tvImageName.setText(albumPictureModel.getPictureName());
        tvImageResolution.setText(albumPictureModel.getImageHeightWidth());
        tvDateModified.setText(convertTimeDateModified(albumPictureModel.getDateModified()));
        tvDateTaken.setText(convertTimeDateTaken(albumPictureModel.getDateTaken()));
        Log.d("showImageInfo", "showImageInfo: " + albumPictureModel.getDateTaken() + "\n" + convertTimeDateModified(albumPictureModel.getDateModified()) + "\n" + convertTimeDateTaken(albumPictureModel.getDateTaken()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public String convertTimeDateModified(long time) {
        Date date = new Date(time * 1000);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
        return format.format(date);
    }

    public String convertTimeDateTaken(long time) {
        Date date = new Date(time);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
        return format.format(date);
    }

    public void showDeleteDialog(String file, String columnId, int j, String imageId) {

        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(v -> {
            Uri[] uris = new Uri[]{Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columnId)};
            try {
                delete(this, uris, 105, imageId, j);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
//            deleteImage(file,j);
//            deletePhotos(file,j,imageId);
//            deletePhoto(file, j, imageId);
            dialog.dismiss();
        });
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();

    }

    /*private void deletePhoto(File file, int i, String imageId) {

        if (file.exists()) {
            try {
                boolean delete = file.delete();
                if (delete) {
                    dbHelper.removeFav(imageId);
                    Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                    allImage.remove(i);
                    this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    imagesPager.notifyDataSetChanged();
                    if (allImage.isEmpty()) {
                        Intent intent = new Intent(AlbumImageSliderActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
//                    callBroadCast();

                } else {
                    Toast.makeText(this, "Image Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }*/

    private void deletePhotos(File file, int i, String imageId) {

        ContentResolver resolver = getApplicationContext().getContentResolver();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Images.Media.DATA + "=?";
        String[] stringArgs = new String[]{file.getAbsolutePath()};

        resolver.delete(uri, selection, stringArgs);
        dbHelper.removeFav(imageId);
        allImage.remove(i);
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }

    public void delete(final Activity activity, final Uri[] uriList, final int requestCode, String imageId, int i)
            throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException {
        final ContentResolver resolver = activity.getContentResolver();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.imageID = imageId;
            this.deletePosition = i;
            final List<Uri> list = new ArrayList<>();
            Collections.addAll(list, uriList);
            final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, list);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {

                for (final Uri uri : uriList) {
                    resolver.delete(uri, null, null);
                }
                dbHelper.removeFav(imageId);
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                allImage.remove(i);
                imagesPager.notifyDataSetChanged();

            } catch (RecoverableSecurityException ex) {
                this.imageID = imageId;
                this.deletePosition = i;
                final IntentSender intent = ex.getUserAction()
                        .getActionIntent()
                        .getIntentSender();
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
            }
        } else {
            for (final Uri uri : uriList) {
                resolver.delete(uri, null, null);
            }
            dbHelper.removeFav(imageId);
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            allImage.remove(i);
            imagesPager.notifyDataSetChanged();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {

            if (resultCode == RESULT_OK) {
                dbHelper.removeFav(getImageID());
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
                allImage.remove(getPos());
                imagesPager.notifyDataSetChanged();
            }
        }
        if (requestCode == 588) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show();
                hideImageApi30(getRenameModel());
            } else {
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteImage(File file, int i) {
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = this.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);


//            getActivity().runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    viewPager.removeViewAt(i);
//                    viewPager.setCurrentItem(i-1);
//                    imagePager.notifyDataSetChanged();
//                }
//            });


        } else {
            // File not found in media store DB
            Toast.makeText(this, "image not found", Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

   /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            Tools.FullScreencall(this);
        }
    }*/


}