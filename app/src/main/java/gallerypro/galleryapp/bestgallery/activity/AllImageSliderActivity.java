package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.database.DbHelper;
import gallerypro.galleryapp.bestgallery.model.AllImagesModel;
import gallerypro.galleryapp.bestgallery.model.ImageSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.text.CharacterIterator;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllImageSliderActivity extends AppCompatActivity {


    private List<AllImagesModel> allImage;
    private int position;

    private ViewPager viewPager;
    private ZoomageView zoomageView;
    private TextView tvImageTitle;
    private ImageView ivImageInfo;
    private ImageView ivImageLike;
    private ImageView ivImageShare;
    private ImageView ivImageDelete;
    private ImageView ivBack;
    private ImageView ivEdit;
    private ImageView ivImageOption;
    private ImageView ivRotation;
    private PreferenceManager preferenceManager;
    private ImagesPager imagesPager;

    private DbHelper dbHelper;
    private List<Integer> imageIds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_image_slider);

        preferenceManager = new PreferenceManager(this);

        if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        ImageSliderModel imageSliderMode = (ImageSliderModel) getIntent().getSerializableExtra("key");

        allImage = imageSliderMode.getAllImagesModelList();
        position = imageSliderMode.getPosition();

        dbHelper = new DbHelper(this);



        viewPager = findViewById(R.id.imageviewPager);
        tvImageTitle = findViewById(R.id.image_title);
        ivImageInfo = findViewById(R.id.img_info);
        ivImageLike = findViewById(R.id.img_like);
        ivImageShare = findViewById(R.id.image_share);
        ivImageDelete = findViewById(R.id.image_delete);
        ivBack = findViewById(R.id.backImageView);
        ivEdit = findViewById(R.id.image_edit);
        ivRotation = findViewById(R.id.image_rotate);
        ivImageOption = findViewById(R.id.iv_image_option);

        imagesPager = new ImagesPager();
        viewPager.setAdapter(imagesPager);
        viewPager.setPageTransformer(true, new DrawerTransformer());
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (imagesPager.getCount() - 1) && position < (allImage.size() - 1)) {
                    tvImageTitle.setText(allImage.get(position).getName());

                    if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getId()))) {
                        ivImageLike.setImageResource(R.drawable.ic_liked);
                        ivRotation.setImageResource(R.drawable.ic_liked);
                    } else {
                        ivImageLike.setImageResource(R.drawable.ic_like_24);
                        ivRotation.setImageResource(R.drawable.ic_like_24);
                    }

                    ivImageLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getId()))) {
                                ivImageLike.setImageResource(R.drawable.ic_like_24);
                                ivRotation.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(position).getId()));
                            } else {
                                ivImageLike.setImageResource(R.drawable.ic_liked);
                                ivRotation.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(position).getId()));
                            }
                        }
                    });

                    ivImageInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageInfo(allImage.get(position));
                        }
                    });

                    ivRotation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            hideImage(position);
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(position).getId()))) {
                                ivImageLike.setImageResource(R.drawable.ic_like_24);
                                ivRotation.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(position).getId()));
                            } else {
                                ivImageLike.setImageResource(R.drawable.ic_liked);
                                ivRotation.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(position).getId()));
                            }
                        }
                    });

                    ivImageShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(position);
                        }
                    });

                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AllImageSliderActivity.this, EditActivity.class);
                            intent.putExtra("img", allImage.get(position).getPath());
                            startActivity(intent);
                        }
                    });

                    ivImageOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            imageOption(position);
                        }
                    });

                    ivImageDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allImage.get(position).getPath());
                            Log.d("Image File Path", "onClick: " + file.getPath());
                            showDeleteDialog(file, position, String.valueOf(allImage.get(position).getId()));
                            imagesPager.notifyDataSetChanged();

                        }
                    });

                } else {

                    if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getId()))) {
                        ivImageLike.setImageResource(R.drawable.ic_liked);
                        ivRotation.setImageResource(R.drawable.ic_liked);
                    } else {
                        ivImageLike.setImageResource(R.drawable.ic_like_24);
                        ivRotation.setImageResource(R.drawable.ic_like_24);
                    }
                    ivImageLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getId()))) {
                                ivImageLike.setImageResource(R.drawable.ic_like_24);
                                ivRotation.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(allImage.size() - 1).getId()));
                            } else {
                                ivImageLike.setImageResource(R.drawable.ic_liked);
                                ivRotation.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(allImage.size() - 1).getId()));
                            }
                        }
                    });


                    tvImageTitle.setText(allImage.get(allImage.size() - 1).getName());

                    ivImageInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showImageInfo(allImage.get(allImage.size() - 1));
                        }
                    });

                    ivImageShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareImage(allImage.size() - 1);
                        }
                    });

                    ivEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AllImageSliderActivity.this, EditActivity.class);
                            intent.putExtra("img", allImage.get(allImage.size() - 1).getPath());
                            startActivity(intent);
                        }
                    });

                    ivRotation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            hideImage(allImage.size() - 1);
                            if (dbHelper.getStatuss(String.valueOf(allImage.get(allImage.size() - 1).getId()))) {
                                ivImageLike.setImageResource(R.drawable.ic_like_24);
                                ivRotation.setImageResource(R.drawable.ic_like_24);
                                dbHelper.removeFav(String.valueOf(allImage.get(allImage.size() - 1).getId()));
                            } else {
                                ivImageLike.setImageResource(R.drawable.ic_liked);
                                ivRotation.setImageResource(R.drawable.ic_liked);
                                dbHelper.setFav(String.valueOf(allImage.get(allImage.size() - 1).getId()));
                            }
                        }
                    });

                    ivImageOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageOption(allImage.size() - 1);
                        }
                    });

                    ivImageDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allImage.get(allImage.size() - 1).getPath());
                            Log.d("Image File Path", "onClick: " + file.getPath());
                            showDeleteDialog(file, allImage.size() - 1, String.valueOf(allImage.get(allImage.size() - 1).getId()));
                        }
                    });
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//        ivImageOption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(AllImageSliderActivity.this,ivImageOption);
//                popupMenu.getMenuInflater().inflate(R.menu.image_popup_menu,popupMenu.getMenu());
//
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()){
//                            case R.id.action_hide_image:
//                                File hideFile = new File(Environment.getExternalStorageDirectory()+File.separator+".hideimage");
//                                if (!hideFile.exists()){
//                                    Log.e("HIde File", "onClick: "+hideFile.getPath() );
//                                    hideFile.mkdir();
//                                }
//
//                                Toast.makeText(AllImageSliderActivity.this, "Hide image", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show();
//            }
//        });


//        ivImageDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File file = new File(allImage.get(viewPager.getCurrentItem()).getPath());
//                showDeleteDialog(file);
//
//            }
//        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void shareImage(int position){
        File file = new File(allImage.get(position).getPath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, "share via"));
    }

    public void showDeleteDialog(File file, int j, String imgId) {


        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto(file, j, imgId);
                dialog.dismiss();
            }
        });
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();


//        new AlertDialog.Builder(this)
//                .setTitle("Delete Photo?")
//                .setNegativeButton("No", null)
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        deletePhoto(file, j);
//                    }
//                })
//                .create().show();
    }

    private void deletePhoto(File file, int i, String imgId) {

        if (file.exists()) {
            try {
                boolean delete = file.delete();
                if (delete) {
                    dbHelper.removeFav(imgId);
                    Toast.makeText(AllImageSliderActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                    allImage.remove(i);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    imagesPager.notifyDataSetChanged();
                    if (allImage.isEmpty()) {
                        finish();
                    }

                } else {
                    Toast.makeText(AllImageSliderActivity.this, "Image Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showImageInfo(AllImagesModel allImagesModel) {


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

        tvImageSize.setText(humanReadableByteCountSI(Long.parseLong(allImagesModel.getPictureSize())));
        tvImagePath.setText(allImagesModel.getPath());
        tvImageName.setText(allImagesModel.getName());
        tvImageResolution.setText(allImagesModel.getImageHeightWidth());
        tvDateModified.setText(convertTimeDateModified(allImagesModel.getDateModified()));
        tvDateTaken.setText(convertTimeDateTaken(allImagesModel.getDateTaken()));
//        Log.d("showImageInfo", "showImageInfo: " + albumPictureModel.getDateTaken() + "\n" + convertTimeDateModified(albumPictureModel.getDateModified()) + "\n" + convertTimeDateTaken(albumPictureModel.getDateTaken()));

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

    private class ImagesPager extends PagerAdapter {

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
            zoomageView = view.findViewById(R.id.zoomImageView);


//            final AlbumPictureModel albumPictureModel = allImage.get(position);
            AllImagesModel allImagesModel = allImage.get(position);
            Glide.with(AllImageSliderActivity.this).load(allImagesModel.getPath()).apply(new RequestOptions().fitCenter()).placeholder(R.drawable.no_image_bg).into(zoomageView);
//            Picasso.get().load(new File(allImagesModel.getPath())).placeholder(R.drawable.no_image_bg).into(zoomageView);

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


    private void imageOption(int position) {


        PopupMenu popupMenu = new PopupMenu(AllImageSliderActivity.this, ivImageOption);
        popupMenu.getMenuInflater().inflate(R.menu.image_popup_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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

    private void hideImage(int position){

        if (preferenceManager.isPinSeted()) {
            File hideFile = new File(Environment.getExternalStorageDirectory(), ".HideImage");

            if (!hideFile.exists()) {
                hideFile.mkdir();
//                Toast.makeText(AllImageSliderActivity.this, "File not Existed", Toast.LENGTH_SHORT).show();
//                Toast.makeText(AllImageSliderActivity.this, "" + hideFile.getPath(), Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(AllImageSliderActivity.this, "File Existed", Toast.LENGTH_SHORT).show();
//                Toast.makeText(AllImageSliderActivity.this, "" + hideFile.getPath(), Toast.LENGTH_SHORT).show();
            }


            File file = new File(allImage.get(position).getPath());

            try {
                moveFile(file, hideFile, position);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AllImageSliderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Hide File Exception", "Exception: " + e.getMessage());
            }


            Toast.makeText(AllImageSliderActivity.this, "Hide image", Toast.LENGTH_SHORT).show();

        } else {
            startActivity(new Intent(AllImageSliderActivity.this, SecurityActivity.class));
        }

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


    public static void MoveFile(String path_source, String path_destination) throws IOException {
        File file_Source = new File(path_source);
        File file_Destination = new File(path_destination);

        FileChannel source = null;
        FileChannel destination = null;
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file_Source);
            fileOutputStream = new FileOutputStream(file_Destination);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            fileInputStream.close();
            fileInputStream = null;

            // write the output file
            fileOutputStream.flush();
            fileOutputStream.close();
            fileOutputStream = null;

            // delete the original file


        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    private void moveFile(File file, File dir, int i) throws IOException {
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
//            Collections.reverse(hideList);
            saveArrayList(hideList);


            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();

            boolean isDeleted = file.delete();
            if (isDeleted) {
                allImage.remove(i);
                this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                imagesPager.notifyDataSetChanged();
            }
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }

        imagesPager.notifyDataSetChanged();

        if (allImage.isEmpty()){
            finish();
        }

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

}