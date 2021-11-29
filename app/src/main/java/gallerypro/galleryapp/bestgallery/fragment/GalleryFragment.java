package gallerypro.galleryapp.bestgallery.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

import gallerypro.galleryapp.bestgallery.R;
import gallerypro.galleryapp.bestgallery.activity.AlbumImageActivity;
import gallerypro.galleryapp.bestgallery.activity.FavouriteImageActivity;
import gallerypro.galleryapp.bestgallery.adapter.AlbumAdapter;
import gallerypro.galleryapp.bestgallery.adapter.AllImagesAdapter;
import gallerypro.galleryapp.bestgallery.interfaces.AlbumClickListener;
import gallerypro.galleryapp.bestgallery.interfaces.AllImageClickListener;
import gallerypro.galleryapp.bestgallery.model.AlbumModel;
import gallerypro.galleryapp.bestgallery.model.AlbumPictureModel;
import gallerypro.galleryapp.bestgallery.model.AllImagesModel;
import gallerypro.galleryapp.bestgallery.model.ListItem;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.viewholder.PictureViewHolder;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment implements AlbumClickListener, AllImageClickListener {

    private RecyclerView recyclerView;
    private TextView textViewNoImage;
    private PreferenceManager preferenceManager;
    AllImagesAdapter allImagesAdapter;
    // all image
    private HashMap<String, List<AllImagesModel>> imageHashMap;
    List<ListItem> consolidatedList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> dateList1 = new ArrayList<>();
    List<AllImagesModel> allImagesList = new ArrayList<>();
    private AdManagerInterstitialAd mAdManagerInterstitialAd;

    private String pictureFolderPath;
    private String folderName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getContext());
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        loadFbInterstitialAds();
        if (!preferenceManager.getGalleryView()) {
            // show all images
            showAllImages();
        } else {
            // show album folder
            showAlbumFolder();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        getActivity().setTitle("Gallery");
        recyclerView = view.findViewById(R.id.recyclerViewAlbum);
        textViewNoImage = view.findViewById(R.id.text_view_no_image);

        return view;
    }

    private void showAllImages() {

//        imageHashMap = groupDataIntoHashMap(getAllImages());
//
//
//        for (String date : imageHashMap.keySet()) {
//            DateItem dateItem = new DateItem();
//            Log.d("+++++", "onCreateView: " + date);
//            dateItem.setDate(date);
//            consolidatedList.add(dateItem);
//
//            for (AllImagesModel allImagesModel : imageHashMap.get(date)) {
//                ImageItem imageItem = new ImageItem();
//                imageItem.setAllImagesModel(allImagesModel);
//                consolidatedList.add(imageItem);
//                Log.d("+++++", "onCreateView img: " + allImagesModel.getPath());
//            }
        allImagesList = getAllImages();
        for (String yeas : dateList) {
            if (dateList1.contains(yeas)) {
                dateList1.add("");
            } else {
                dateList1.add(yeas);
            }
        }


        allImagesAdapter = new AllImagesAdapter(getContext(), allImagesList, dateList1, this);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(allImagesAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAlbumFolder() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(true);

        Log.d("========", "onCreateView: " + getPicturePaths());

        AlbumAdapter albumAdapter = new AlbumAdapter(getPicturePaths(), getContext(), this);
        recyclerView.setAdapter(albumAdapter);

    }

    // album image array list
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<AlbumModel> getPicturePaths() {

        ArrayList<AlbumModel> picFolder = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        try {

            if (cursor != null) {
                cursor.moveToFirst();

            }
            do {
                AlbumModel albumModel = new AlbumModel();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                String folderPath = dataPath.substring(0, dataPath.lastIndexOf(folder + "/"));
                folderPath = folderPath + folder + "/";
                if (!picPaths.contains(folderPath)) {
                    picPaths.add(folderPath);

                    albumModel.setPath(folderPath);
                    albumModel.setFolderName(folder);
                    albumModel.setFirstImage(dataPath);
                    albumModel.addpics();
                    picFolder.add(albumModel);
                } else {
                    for (int i = 0; i < picFolder.size(); i++) {
                        if (picFolder.get(i).getPath().equals(folderPath)) {
                            picFolder.get(i).setFirstImage(dataPath);
                            picFolder.get(i).addpics();
                        }


                    }
                }

            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();

          /*  recyclerView.setVisibility(View.GONE);
            textViewNoImage.setVisibility(View.VISIBLE);*/
        }


        for (int i = 0; i < picFolder.size(); i++) {
            Log.d("GalleryFragment.Java", "getPicturePaths: " + picFolder.get(i).getFolderName() + "\n path : " + picFolder.get(i).getPath() + "\n number of pic : " + picFolder.get(i).getNumberOfImage() + "\n");
        }

        return picFolder;

    }

    //all image array list
    private List<AllImagesModel> getAllImages() {

        List<AllImagesModel> allImages = new ArrayList<>();
//        List<AllImagesModel> allImages1 = new ArrayList<>();


        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_MODIFIED
                , MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.DATE_TAKEN};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED);


        if (cursor != null) {
            cursor.moveToFirst();
        }

        try {


            do {

                AllImagesModel allImagesModel = new AllImagesModel();

                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED));

                long longDate = Long.parseLong(date);
                Date dates = new Date(longDate * 1000L);
                String dateFormatted = new SimpleDateFormat("dd.MM.yyyy").format(dates);

                allImagesModel.setId(Integer.parseInt(id));
                allImagesModel.setPath(path);
                allImagesModel.setName(name);
                allImagesModel.setDate(dateFormatted);
                allImagesModel.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE)));
                allImagesModel.setImageHeightWidth((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                        + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH))));
                try {
                    allImagesModel.setDateTaken(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                allImagesModel.setDateModified(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED))));
                dateList.add(dateFormatted);
                allImages.add(allImagesModel);

            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Collections.sort(allImages, new Comparator<AllImagesModel>() {
//            @Override
//            public int compare(AllImagesModel lhs, AllImagesModel rhs) {
//                long l = Long.parseLong(lhs.getDate());
//                long r = Long.parseLong(rhs.getDate());
//                return l > r ? -1 : (l == r ? 0 : 1);
//            }
//        });

//        Collections.sort(allImages, new Comparator<HashMap<String, String>>() {
//            DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");//do determ
//
//            @Override
//            public int compare(HashMap<String, String> mapping1,
//                               HashMap<String, String> mapping2) {
//                try {
//                    return f.parse(mapping1.get()).compareTo(f.parse(mapping2.get(KEY_NAME)));
//                } catch (ParseException e) {
//                    throw new IllegalArgumentException(e);
//                }
//            }
//        });


        Collections.reverse(dateList);
        Collections.reverse(allImages);

        return allImages;
    }

    //all image hash map
    private HashMap<String, List<AllImagesModel>> groupDataIntoHashMap(List<AllImagesModel> listOfPojosOfJsonArray) {

        HashMap<String, List<AllImagesModel>> groupedHashMap = new HashMap<>();


        for (AllImagesModel allImagesModel : listOfPojosOfJsonArray) {

            String hashMapKey = allImagesModel.getDate();

            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap.get(hashMapKey).add(allImagesModel);
            } else {
                List<AllImagesModel> list = new ArrayList<>();
                list.add(allImagesModel);
                groupedHashMap.put(hashMapKey, list);
            }
        }


        return groupedHashMap;
    }


    // send album image in albumImageActivity
    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

//        sendToActivity.sendActivity(pictureFolderPath,folderName);
        this.pictureFolderPath = pictureFolderPath;
        this.folderName = folderName;

        Date date = new Date();
        if (preferenceManager.getAdsTime() <= date.getTime()) {
            if (mAdManagerInterstitialAd != null) {
                mAdManagerInterstitialAd.show(getActivity());
            } else {
                Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
                intent.putExtra("folderPath", pictureFolderPath);
                intent.putExtra("folderName", folderName);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
            intent.putExtra("folderPath", pictureFolderPath);
            intent.putExtra("folderName", folderName);
            startActivity(intent);
        }


        //  5 <= 6
        // 6+1 <= 8


//        Bundle bundle = new Bundle();
//        bundle.putString("folderPath",pictureFolderPath);
//        bundle.putString("folderName",folderName);

//        AlbumImageFragment albumImageFragment = new AlbumImageFragment();
//        albumImageFragment.setArguments(bundle);
//        getFragmentManager().beginTransaction().replace(((ViewPager)getView().getParent()).getId(),albumImageFragment);

    }

    @Override
    public void onPicClicked(PictureViewHolder holder, int position, List<AlbumPictureModel> pics) {

    }

    @Override
    public void onItemLongClick(View view, AlbumPictureModel obj, int pos) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu, menu);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_mode);
        if (preferenceManager.checkMode()) {
            item.setTitle("Lite Mode");
        } else {
            item.setTitle("Dark Mode");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_album:
                preferenceManager.saveGalleryView(true);
                showAlbumFolder();
                break;
            case R.id.action_all_image:
                preferenceManager.saveGalleryView(false);
                consolidatedList.clear();
                showAllImages();
                break;
            case R.id.action_mode:
                if (preferenceManager.checkMode()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    preferenceManager.isNightMode(false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    preferenceManager.isNightMode(true);
                }
                break;
            case R.id.action_fav:
                startActivity(new Intent(getActivity(), FavouriteImageActivity.class));
                break;

            case R.id.action_privacy_policy:
                //privacy policy

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://gallerypro2021privacypolicy.blogspot.com/2021/03/privacy-policy.html"));
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPicClicked(List<AllImagesModel> allImagesModelList, int position) {

    }


//        ImageSliderFragment imageSliderFragment = ImageSliderFragment.newInstance(consolidatedList,position,getContext());
//
//        imageSliderFragment.setEnterTransition(new Slide());
//        imageSliderFragment.setExitTransition(new Slide());
//        // uncomment this to use slide transition and comment the two lines below
////            imageSliderFragment.setEnterTransition(new android.transition.ChangeBounds());
////            imageSliderFragment.setExitTransition(new android.transition.ChangeBounds());
//
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
////                .addSharedElement(holder.imageView, position + "picture")
//                .add(R.id.mainActivityContainer, imageSliderFragment)
//                .addToBackStack(null)
//                .commit();


    private void loadFbInterstitialAds() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(getContext(), getString(R.string.admob_interstitial), adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        mAdManagerInterstitialAd = interstitialAd;
                        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
                                intent.putExtra("folderPath", pictureFolderPath);
                                intent.putExtra("folderName", folderName);
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
                                preferenceManager.saveAdsTime(date.getTime() + (10 * 60 * 1000)); // 1800000 milisecound
//                }
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mAdManagerInterstitialAd = null;
                        Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
                        intent.putExtra("folderPath", pictureFolderPath);
                        intent.putExtra("folderName", folderName);
                        startActivity(intent);
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

}