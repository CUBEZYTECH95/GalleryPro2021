package gallerypro.galleryapp.bestgallery.activity;

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
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DrawerTransformer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.database.VideoDbHelper;
import gallerypro.galleryapp.bestgallery.model.AllVideoModel;
import gallerypro.galleryapp.bestgallery.model.VideoSliderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import java.io.File;
import java.text.CharacterIterator;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class VideoSliderActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private List<AllVideoModel> allVideoList = new ArrayList<>();
    private int position;
    private ViewPager viewPager;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;

    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivLike;
    private ImageView ivInfo;

    private ImageView ivShare;
    private ImageView ivDelete;
    private ImageView ivFav;

    private ImageView imageView;
    private VideoPager videoPager;

    private VideoDbHelper videoDbHelper;
    int mAdCount = 0;

    //delete
    private int deletePosition;
    private String vidId;

    int getDeletePosition() {
        return this.deletePosition;
    }

    String getVidId() {
        return this.vidId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_slider);

        preferenceManager = new PreferenceManager(this);

        /*if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        }*/

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        VideoSliderModel videoSliderModel = (VideoSliderModel) getIntent().getSerializableExtra("key");
        allVideoList = videoSliderModel.getAllVideoModelList();
        position = videoSliderModel.getPosition();

        viewPager = findViewById(R.id.albumVideoViewPage);
        ivBack = findViewById(R.id.backImageView);
        tvTitle = findViewById(R.id.video_title);
        ivLike = findViewById(R.id.video_like);
        ivInfo = findViewById(R.id.video_info);
        ivShare = findViewById(R.id.video_share);
        ivDelete = findViewById(R.id.video_delete);
        ivFav = findViewById(R.id.video_fav);

        videoPager = new VideoPager();
        viewPager.setAdapter(videoPager);
        viewPager.setPageTransformer(true, new DrawerTransformer());
        viewPager.setCurrentItem(position);

        videoDbHelper = new VideoDbHelper(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < (videoPager.getCount() - 1) && position < (allVideoList.size() - 1)) {
                    tvTitle.setText(allVideoList.get(position).getTitle());

                    if (videoDbHelper.getStatus(String.valueOf(allVideoList.get(position).getId()))) {
                        ivLike.setImageResource(R.drawable.ic_liked);
                        ivFav.setImageResource(R.drawable.ic_liked);
                    } else {
                        ivLike.setImageResource(R.drawable.ic_like_24);
                        ivFav.setImageResource(R.drawable.ic_like_24);
                    }


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openVideoActivity(position);
                        }
                    });

                    ivLike.setOnClickListener(v -> likeVideo(position));

                    ivFav.setOnClickListener(view -> likeVideo(position));

                    ivInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showInfoDialog(position);
                        }
                    });

                    ivShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareVideo(position);
                        }
                    });

                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allVideoList.get(position).getPath());
                            int i = position;
                            showDeleteDialog(file, allVideoList.get(position).getColumnId(), i, String.valueOf(allVideoList.get(position).getId()));
                        }
                    });


                } else {
                    tvTitle.setText(allVideoList.get(allVideoList.size() - 1).getTitle());

                    if (videoDbHelper.getStatus(String.valueOf(allVideoList.get(allVideoList.size() - 1).getId()))) {
                        ivLike.setImageResource(R.drawable.ic_liked);
                        ivFav.setImageResource(R.drawable.ic_liked);
                    } else {
                        ivLike.setImageResource(R.drawable.ic_like_24);
                        ivFav.setImageResource(R.drawable.ic_like_24);
                    }


                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openVideoActivity(allVideoList.size() - 1);
                        }
                    });

                    ivLike.setOnClickListener(v -> likeVideo(allVideoList.size() - 1));

                    ivFav.setOnClickListener(v -> likeVideo(allVideoList.size() - 1));

                    ivInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showInfoDialog(allVideoList.size() - 1);
                        }
                    });

                    ivShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareVideo(allVideoList.size() - 1);
                        }
                    });

                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(allVideoList.get(allVideoList.size() - 1).getPath());
                            int i = allVideoList.size() - 1;
                            showDeleteDialog(file, allVideoList.get(i).getColumnId(), i, String.valueOf(allVideoList.get(i).getId()));
                        }
                    });


                }
            }

            @Override
            public void onPageSelected(int position) {

                mAdCount++;
                if (mAdCount % 5 == 0) {
                    if (mAdManagerInterstitialAd != null) {
                        mAdManagerInterstitialAd.show(VideoSliderActivity.this);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        AdManagerInterstitialAd.load(VideoSliderActivity.this, getString(R.string.admob_interstitial), adRequest,
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


    private void shareVideo(int i) {
        File file = new File(allVideoList.get(i).getPath());
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(intent);
    }

    private void showInfoDialog(int i) {

        AllVideoModel allVideoModel = allVideoList.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.video_details_dialog, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        Button button = view.findViewById(R.id.btn_details_ok);
        TextView tvImageSize = view.findViewById(R.id.tv_image_size);
        TextView tvImagePath = view.findViewById(R.id.tv_image_path);
        TextView tvImageName = view.findViewById(R.id.tv_image_name);
        TextView tvDateModified = view.findViewById(R.id.tv_date_taken);

        tvImageSize.setText(humanReadableByteCountSI(Long.parseLong(allVideoModel.getSize())));
        tvImagePath.setText(allVideoModel.getPath());
        tvImageName.setText(allVideoModel.getFileName());
        tvDateModified.setText(convertTimeDateModified(Long.parseLong(allVideoModel.getDateAdded())));

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

    public String convertTimeDateModified(long time) {
        Date date = new Date(time * 1000);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MM.yyyy , HH:mm:aa");
        return format.format(date);
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

    private void showDeleteDialog(File file, String columnId, int i, String vidId) {

        Dialog dialog = new Dialog(this, R.style.Dialog_Theme);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_dialog);

        Button delete = dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri[] uri = new Uri[]{Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columnId)};

                try {
                    delete(VideoSliderActivity.this, uri, 125, vidId, i);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

//                deleteVideo(file, i, vidId);
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
    }

    private void deleteVideo(File file, int i, String vidId) {

        if (file.exists()) {
            try {
                boolean delete = file.delete();
                if (delete) {
                    videoDbHelper.removeFav(vidId);
                    Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
                    allVideoList.remove(i);
                    this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    videoPager.notifyDataSetChanged();
                    if (allVideoList.isEmpty()) {
                        finish();
                    }

                } else {
                    Toast.makeText(this, "Video Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void delete(final Activity activity, final Uri[] uriList, final int requestCode, String vidId, int i)
            throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException {
        final ContentResolver resolver = activity.getContentResolver();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.deletePosition = i;
            this.vidId = vidId;
            final List<Uri> list = new ArrayList<>();
            Collections.addAll(list, uriList);
            final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, list);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {

                for (final Uri uri : uriList) {
                    resolver.delete(uri, null, null);
                }
                Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();

            } catch (RecoverableSecurityException ex) {
                this.deletePosition = i;
                this.vidId = vidId;
                final IntentSender intent = ex.getUserAction()
                        .getActionIntent()
                        .getIntentSender();
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
            }
        } else {
            for (final Uri uri : uriList) {
                resolver.delete(uri, null, null);
            }
            videoDbHelper.removeFav(vidId);
            Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
            allVideoList.remove(i);
            videoPager.notifyDataSetChanged();


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 125) {
            if (resultCode == RESULT_OK) {
                videoDbHelper.removeFav(getVidId());
                Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show();
                allVideoList.remove(getDeletePosition());
                videoPager.notifyDataSetChanged();
            }
        }
    }

    private void likeVideo(int i) {
        if (videoDbHelper.getStatus(String.valueOf(allVideoList.get(i).getId()))) {
            ivLike.setImageResource(R.drawable.ic_like_24);
            ivFav.setImageResource(R.drawable.ic_like_24);
            videoDbHelper.removeFav(String.valueOf(allVideoList.get(i).getId()));
        } else {
            ivLike.setImageResource(R.drawable.ic_liked);
            ivFav.setImageResource(R.drawable.ic_liked);
            videoDbHelper.setFav(String.valueOf(allVideoList.get(i).getId()));
        }
    }

    private void openVideoActivity(int i) {
        Intent intent = new Intent(VideoSliderActivity.this, VideoPlayerActivity.class);
        intent.putExtra("id", allVideoList.get(i).getId());
        intent.putExtra("path", allVideoList.get(i).getPath());
        intent.putExtra("title", allVideoList.get(i).getTitle());
        intent.putExtra("fileName", allVideoList.get(i).getFileName());
        intent.putExtra("size", allVideoList.get(i).getSize());
        intent.putExtra("dateAdded", allVideoList.get(i).getDateAdded());
        intent.putExtra("duration", allVideoList.get(i).getDuration());
        startActivity(intent);

    }


    private class VideoPager extends PagerAdapter {

        @Override
        public int getCount() {
            return allVideoList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (View) object;
        }


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.video_slider_item, null);
            imageView = view.findViewById(R.id.iv_video_slider);


            final AllVideoModel allVideoModel = allVideoList.get(position);
//            ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,Integer.parseInt(videoFolderList.get(position).id)
            Glide.with(container.getContext()).load(allVideoModel.getPath()).apply(new RequestOptions().fitCenter()).placeholder(R.drawable.no_image_bg).into(imageView);
//            Picasso.get().load(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,Integer.parseInt(String.valueOf(allVideoModel.id)))).placeholder(R.drawable.no_image_bg).into(imageView);
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
}