//package com.example.photogallery.fragment;
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.ContentResolver;
//import android.content.ContentUris;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.annotation.ColorInt;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import android.provider.CalendarContract;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
//import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
//import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
//import com.ToxicBakery.viewpager.transforms.DrawerTransformer;
//import com.ToxicBakery.viewpager.transforms.StackTransformer;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.example.photogallery.R;
//import com.example.photogallery.activity.AllImageSliderActivity;
//import com.example.photogallery.activity.MainActivity;
//import com.example.photogallery.database.DbHelper;
//import com.example.photogallery.model.AlbumPictureModel;
//import com.example.photogallery.model.AllImagesModel;
//import com.example.photogallery.model.ImageItem;
//import com.example.photogallery.model.ListItem;
//
//import java.io.File;
//import java.text.CharacterIterator;
//import java.text.Format;
//import java.text.SimpleDateFormat;
//import java.text.StringCharacterIterator;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//
//import static androidx.core.view.ViewCompat.setTransitionName;
//
//public class ImageSliderFragment extends Fragment {
//
//    private List<AlbumPictureModel> allImage;
//    private int position;
//    private Context context;
//    private ViewPager viewPager;
//
//    private ImageView imageView;
//    private TextView textViewImageTitle;
//    private ImageView imageViewBack;
//    private ImageView imageViewLike;
//
//    //imagePager
//    private ImagePager imagePager;
//
//    //top bar
//    private ImageView ivInfo;
//
//    // Bottom Bar
//    private ImageView ivRotate;
//    private ImageView ivEdit;
//    private ImageView ivShare;
//    private ImageView ivDelete;
//
//    //db
//    private DbHelper dbHelper;
//    private List<Integer> imageIds = new ArrayList<>();
//
//    public ImageSliderFragment() {
//    }
//
//    public ImageSliderFragment(List<AlbumPictureModel> allImage, int position, Context context) {
//        this.allImage = allImage;
//        this.position = position;
//        this.context = context;
//        dbHelper = new DbHelper(context);
//        for (int i = 0; i<allImage.size(); i++){
//            imageIds.add(allImage.get(i).getPictureId());
//        }
//        dbHelper.createEmptyTable(imageIds);
//    }
//
//
//    public static ImageSliderFragment newInstance(List<AlbumPictureModel> allImage, int position, Context context) {
//        ImageSliderFragment imageSliderFragment = new ImageSliderFragment(allImage, position, context);
//        return imageSliderFragment;
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
//
//        ivInfo = view.findViewById(R.id.img_info);
//        imageViewLike = view.findViewById(R.id.img_like);
//
//        imageViewBack = view.findViewById(R.id.backImageView);
//        ivRotate = view.findViewById(R.id.image_rotate);
//        ivEdit = view.findViewById(R.id.image_edit);
//        ivShare = view.findViewById(R.id.image_share);
//        ivDelete = view.findViewById(R.id.image_delete);
//
//        imageViewBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//
//        ivRotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Rotate", Toast.LENGTH_SHORT).show();
//            }
//        });
//        ivEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
//            }
//        });
//        ivShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        ivInfo.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                showImageInfo(position);
////            }
////        });
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
//
//        // set images in viewPage
//        viewPager = view.findViewById(R.id.albumImageviewPage);
//        textViewImageTitle = view.findViewById(R.id.image_title);
//
//
//
////            textViewImageTitle.setText(allImage.get(position).getPictureName());
//        imagePager = new ImagePager();
//        viewPager.setAdapter(imagePager);
//        viewPager.setPageTransformer(true, new DrawerTransformer());
//        viewPager.setCurrentItem(position);
//        viewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
//                if (position < (imagePager.getCount() - 1) && position < (allImage.size() - 1)) {
//                    textViewImageTitle.setText(allImage.get(position).getPictureName());
//                    if (dbHelper.getStatus(String.valueOf(allImage.get(position).getPictureId())).equals("0")){
//                        imageViewLike.setImageResource(R.drawable.ic_like_24);
//                    }else {
//                        imageViewLike.setImageResource(R.drawable.ic_liked);
//                    }
//                    imageViewLike.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (dbHelper.getStatus(String.valueOf(allImage.get(position).getPictureId())).equals("0")){
//                                imageViewLike.setImageResource(R.drawable.ic_liked);
//                                dbHelper.updateStatus(String.valueOf(allImage.get(position).getPictureId()),"1");
//                            }else {
//                                imageViewLike.setImageResource(R.drawable.ic_like_24);
//                                dbHelper.updateStatus(String.valueOf(allImage.get(position).getPictureId()),"0");
//                            }
//                        }
//                    });
//                    ivInfo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            showImageInfo(allImage.get(position));
//
//                        }
//                    });
//                    ivShare.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setType("image/*");
//                            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(allImage.get(position).getPicturePath()));
//                            startActivity(Intent.createChooser(intent,"share via"));
//                        }
//                    });
//                    ivDelete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            File file = new File(allImage.get(position).getPicturePath());
//                            int i = position;
//                            showDeleteDialog(file,i);
//                        }
//                    });
//                } else {
//
//                    if (dbHelper.getStatus(String.valueOf(allImage.get(allImage.size() - 1).getPictureId())).equals("0")){
//                        imageViewLike.setImageResource(R.drawable.ic_like_24);
//                    }else {
//                        imageViewLike.setImageResource(R.drawable.ic_liked);
//                    }
//
//                    imageViewLike.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (dbHelper.getStatus(String.valueOf(allImage.get(allImage.size() - 1).getPictureId())).equals("0")){
//                                imageViewLike.setImageResource(R.drawable.ic_liked);
//                                dbHelper.updateStatus(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()),"1");
//                            }else {
//                                imageViewLike.setImageResource(R.drawable.ic_like_24);
//                                dbHelper.updateStatus(String.valueOf(allImage.get(allImage.size() - 1).getPictureId()),"0");
//                            }
//                        }
//                    });
//
//                    textViewImageTitle.setText(allImage.get(allImage.size() - 1).getPictureName());
//                    ivInfo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            showImageInfo(allImage.get(allImage.size() - 1));
//                        }
//                    });
//                    ivShare.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setType("image/*");
//                            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(allImage.get(allImage.size() - 1).getPicturePath()));
//                            startActivity(Intent.createChooser(intent,"share via"));
//                        }
//                    });
//                    ivDelete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            File file = new File(allImage.get(allImage.size() - 1).getPicturePath());
//                            int i = allImage.size() - 1;
//                            showDeleteDialog(file,i);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//    }
//
//
//    // ImageView Adapter
//
////    private class ImagePager extends PagerAdapter {
////
////        @Override
////        public int getCount() {
////            return allImage.size();
////        }
////
////        @Override
////        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
////            return view == (View) object;
////        }
////
////
////        @NonNull
////        @Override
////        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
////
////            LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////
////            View view = layoutInflater.inflate(R.layout.display_image_item, null);
////            imageView = view.findViewById(R.id.zoomImageView);
////
////
////            final AlbumPictureModel albumPictureModel = allImage.get(position);
////            Glide.with(context).load(albumPictureModel.getPicturePath()).apply(new RequestOptions().fitCenter()).placeholder(R.mipmap.ic_launcher).into(imageView);
////
////            ((ViewPager) container).addView(view);
////
////            return view;
////        }
////
////        @Override
////        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
////            container.removeView((View) object);
////        }
////
////        public int getItemPosition(Object object) {
////            return POSITION_NONE;
////        }
////
////    }
//
//    // Image Info
//    private void showImageInfo(AlbumPictureModel albumPictureModel) {
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.details_dialog, null);
//        builder.setView(view);
//
//        final AlertDialog alertDialog = builder.create();
//        Button button = view.findViewById(R.id.btn_details_ok);
//        TextView tvImageSize = view.findViewById(R.id.tv_image_size);
//        TextView tvImagePath = view.findViewById(R.id.tv_image_path);
//        TextView tvImageName = view.findViewById(R.id.tv_image_name);
//        TextView tvImageResolution = view.findViewById(R.id.iv_image_resolution);
//        TextView tvDateTaken = view.findViewById(R.id.tv_date_taken);
//        TextView tvDateModified = view.findViewById(R.id.tv_date_modified);
//
//        tvImageSize.setText(humanReadableByteCountSI(Long.parseLong(albumPictureModel.getPictureSize())));
//        tvImagePath.setText(albumPictureModel.getPicturePath());
//        tvImageName.setText(albumPictureModel.getPictureName());
//        tvImageResolution.setText(albumPictureModel.getImageHeightWidth());
//        tvDateModified.setText(convertTimeDateModified(albumPictureModel.getDateModified()));
//        tvDateTaken.setText(convertTimeDateTaken(albumPictureModel.getDateTaken()));
//        Log.d("showImageInfo", "showImageInfo: " + albumPictureModel.getDateTaken() + "\n" + convertTimeDateModified(albumPictureModel.getDateModified()) + "\n" + convertTimeDateTaken(albumPictureModel.getDateTaken()));
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
//        if (alertDialog.getWindow() != null) {
//            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        }
//        alertDialog.show();
//
//    }
//
//    public static String humanReadableByteCountSI(long bytes) {
//        if (-1000 < bytes && bytes < 1000) {
//            return bytes + " B";
//        }
//        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
//        while (bytes <= -999_950 || bytes >= 999_950) {
//            bytes /= 1000;
//            ci.next();
//        }
//        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
//    }
//
//    public String convertTimeDateModified(long time) {
//        Date date = new Date(time * 1000);
//        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
//        return format.format(date);
//    }
//
//    public String convertTimeDateTaken(long time) {
//        Date date = new Date(time);
//        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
//        return format.format(date);
//    }
//
//    public void showDeleteDialog(File file, int j) {
//        new AlertDialog.Builder(context)
//                .setTitle("Delete Photo?")
//                .setNegativeButton("No", null)
//                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        deletePhoto(file,j);
////                        deleteImage(file,i);
//                    }
//                })
//                .create().show();
//    }
//
//    private void deletePhoto(File file,int i) {
//
//        if (file.exists()) {
//            try {
//                boolean delete = file.delete();
//                if (delete) {
//                    Toast.makeText(context, "file deleted", Toast.LENGTH_SHORT).show();
//                    allImage.remove(i);
//                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//                    imagePager.notifyDataSetChanged();
////                    callBroadCast();
//
//                } else {
//                    Toast.makeText(context, "File Not Deleted", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//
//    private void deleteImage(File file, int i) {
//        // Set up the projection (we only need the ID)
//        String[] projection = {MediaStore.Images.Media._ID};
//
//        // Match on the file path
//        String selection = MediaStore.Images.Media.DATA + " = ?";
//        String[] selectionArgs = new String[]{file.getAbsolutePath()};
//
//        // Query for the ID of the media matching the file path
//        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        ContentResolver contentResolver = context.getContentResolver();
//        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
//        if (c.moveToFirst()) {
//            // We found the ID. Deleting the item via the content provider will also remove the file
//            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
//            contentResolver.delete(deleteUri, null, null);
//
//
////            getActivity().runOnUiThread(new Runnable() {
////
////                @Override
////                public void run() {
////                    viewPager.removeViewAt(i);
////                    viewPager.setCurrentItem(i-1);
////                    imagePager.notifyDataSetChanged();
////                }
////            });
//
//
//
//        } else {
//            // File not found in media store DB
//            Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
//        }
//        c.close();
//    }
//
//
//}