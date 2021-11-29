package gallerypro.galleryapp.bestgallery.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.activity.AlbumImageActivity;
import gallerypro.galleryapp.bestgallery.activity.FavouriteVideoActivity;
import gallerypro.galleryapp.bestgallery.activity.VideoAlbumActivity;
import gallerypro.galleryapp.bestgallery.adapter.VideoFolderAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.VideoAlbumClickListener;
import gallerypro.galleryapp.bestgallery.model.VideoFolderModel;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.Date;

public class VideoFragment extends Fragment implements VideoAlbumClickListener {

    private ArrayList<String> videoFolderName = new ArrayList<>();
    private RecyclerView recyclerView;
    private VideoFolderAdapter videoFolderAdapter;
    private PreferenceManager preferenceManager;
    private String videoAlbumPath;
    private String albumName;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        getActivity().setTitle("Video");

        recyclerView = view.findViewById(R.id.videoRecyclerview);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFbInterstitialAds();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        videoFolderAdapter = new VideoFolderAdapter(getContext(), videoFolderName, getAllVideoFolder(), this);
        recyclerView.setAdapter(videoFolderAdapter);


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.video_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_video_fav:
                startActivity(new Intent(getActivity(), FavouriteVideoActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<VideoFolderModel> getAllVideoFolder() {
        videoFolderName.clear();

        ArrayList<VideoFolderModel> tempVideoFolderList = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA
        };


        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {

            while (cursor.moveToNext()) {

                VideoFolderModel videoFolderModel = new VideoFolderModel();

                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));

                // storage/sd_card/video_dir/folder_name"/"video_name.mp4
                int slashFirstIndex = path.lastIndexOf("/");
                // "storage/sd_card/video_dir/folder_name"
                String subString = path.substring(0, slashFirstIndex);
                //storage/sd_card/video_dir"/"folder_name
                int index = subString.lastIndexOf("/");
                // folder_name
                String folderName = subString.substring(index + 1, slashFirstIndex);


                if (!videoFolderName.contains(subString)) {
                    videoFolderName.add(subString);

                    videoFolderModel.setId(id);
                    videoFolderModel.setFolderName(folderName);
                    videoFolderModel.setPath(subString);
                    videoFolderModel.setFirstPic(path);
                    videoFolderModel.addVideo();

                    tempVideoFolderList.add(videoFolderModel);

                } else {
                    for (int i = 0; i < tempVideoFolderList.size(); i++) {
                        if (tempVideoFolderList.get(i).getPath().equals(subString)) {
                            tempVideoFolderList.get(i).setFirstPic(path);
                            tempVideoFolderList.get(i).addVideo();
                        }
                    }
                }


            }
            cursor.close();
        }

        for (VideoFolderModel video : tempVideoFolderList) {
            Log.d("getAllVideoFolder", "id : " + video.id + "\n path : " + video.path);
        }

        for (String folder : videoFolderName) {
            Log.d("getAllVideoFolder", "Folder Name : " + folder);
        }

        return tempVideoFolderList;
    }

    @Override
    public void onVideoAlbumClick(String videoAlbumPath, String albumName) {
        this.videoAlbumPath = videoAlbumPath;
        this.albumName = albumName;

        Date date = new Date();
        if (preferenceManager.getAdsTime() <= date.getTime()) {
            if (mAdManagerInterstitialAd !=  null) {
                mAdManagerInterstitialAd.show(getActivity());
            } else {
                Intent intent = new Intent(getActivity(), VideoAlbumActivity.class);
                intent.putExtra("videoAlbumPath", videoAlbumPath);
                intent.putExtra("albumName", albumName);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getActivity(), VideoAlbumActivity.class);
            intent.putExtra("videoAlbumPath", videoAlbumPath);
            intent.putExtra("albumName", albumName);
            startActivity(intent);
        }


    }


    private void loadFbInterstitialAds() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(getContext(),getString(R.string.admob_interstitial), adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        mAdManagerInterstitialAd = interstitialAd;
                        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                Intent intent = new Intent(getActivity(), VideoAlbumActivity.class);
                                intent.putExtra("videoAlbumPath", videoAlbumPath);
                                intent.putExtra("albumName", albumName);
                                startActivity(intent);
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
                                Date date = new Date();
//                if (preferenceManager.getAdsTime() == 0){
                                preferenceManager.saveAdsTime(date.getTime()+(10 * 60 * 1000));//         1800000 milisecound       }
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mAdManagerInterstitialAd = null;
                        Intent intent = new Intent(getActivity(), VideoAlbumActivity.class);
                        intent.putExtra("videoAlbumPath", videoAlbumPath);
                        intent.putExtra("albumName", albumName);
                        startActivity(intent);
                    }
                });

       /*
        interstitialAd = new InterstitialAd(getContext(), getString(R.string.facebook_interstitial));

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
                Intent intent = new Intent(getActivity(), VideoAlbumActivity.class);
                intent.putExtra("videoAlbumPath", videoAlbumPath);
                intent.putExtra("albumName", albumName);
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

}