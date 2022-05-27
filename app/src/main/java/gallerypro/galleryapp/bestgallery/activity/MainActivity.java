package gallerypro.galleryapp.bestgallery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import gallerypro.galleryapp.bestgallery.R;

import gallerypro.galleryapp.bestgallery.appopenads.MyApplication;
import gallerypro.galleryapp.bestgallery.fragment.FavouriteFragment;
import gallerypro.galleryapp.bestgallery.fragment.GalleryFragment;
import gallerypro.galleryapp.bestgallery.fragment.VideoFragment;
import gallerypro.galleryapp.bestgallery.preference.PreferenceManager;
import gallerypro.galleryapp.bestgallery.utils.Tools;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.onesignal.OneSignal;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private ChipNavigationBar chipNavigationBar;

    private Toolbar toolbar;

    private int[] tabIcons = {R.drawable.ic_gallery, R.drawable.ic_video, R.drawable.ic_padlock};

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private PreferenceManager preferenceManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Dialog dialog;
    private AdManagerInterstitialAd mAdManagerInterstitialAd;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        getSupportActionBar().setTitle("Gallery");

        runtimePermission();

    }

    private void startApp() {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        OneSignal.initWithContext(this);
        OneSignal.setAppId("93d205a3-dc41-4349-9d2f-f8721237434d");

        loadExitDialog();

        preferenceManager = new PreferenceManager(this);

       /* if (preferenceManager.checkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }*/

        preferenceManager.saveAdsTime(0);

        AdManagerAdView mAdView = findViewById(R.id.adManagerAdView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite2));
        }*/

       /* if (preferenceManager.checkMode()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }*/

        Application application = getApplication();

//        Tools.FullScreencall(this);

//        tabLayout = findViewById(R.id.tablayout);
//        viewPager = findViewById(R.id.viewPager);
//        tabGallery = findViewById(R.id.tab_gallery);
//        tabVideo = findViewById(R.id.tab_video);
//        tabPrivacy = findViewById(R.id.tab_privacy);
//
//        tvGallery = findViewById(R.id.tv_gallery);
//        tvVideo = findViewById(R.id.tv_video);
//        tvPrivacy = findViewById(R.id.tv_privacy);
//
//        ivGallery = findViewById(R.id.iv_gallery);
//        ivVideo = findViewById(R.id.iv_video);
//        ivPrivacy = findViewById(R.id.iv_privacy);

        chipNavigationBar = findViewById(R.id.chip_navigationbar);

        chipNavigationBar.setItemSelected(R.id.nav_gallery, true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.nav_gallery:
                        fragment = new GalleryFragment();
                        break;
                    case R.id.nav_video:
                        fragment = new VideoFragment();
                        break;
                    case R.id.nav_favourite:
//                        fragment = new PrivacyFragment(MainActivity.this);
                        fragment = new FavouriteFragment();
                        break;
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
        });

//        ivGallery.setImageResource(R.drawable.ic_gallery_selected);
//        ivVideo.setImageResource(R.drawable.ic_video);
//        ivPrivacy.setImageResource(R.drawable.ic_padlock);

//        tvGallery.setTextColor(ContextCompat.getColor(this,R.color.colorText));
//        tvVideo.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//        tvPrivacy.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//
//        tabGallery.setOnClickListener(this);
//        tabVideo.setOnClickListener(this);
//        tabPrivacy.setOnClickListener(this);

//        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);
//        int limit = (pagerAdapter.getCount() > 1 ? pagerAdapter.getCount() - 1 : 1);
//        viewPager.setAdapter(pagerAdapter);m
//        viewPager.setOffscreenPageLimit(limit);
//        tabLayout.setupWithViewPager(viewPager);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        toolbar.setTitle("Gallery");
//                        break;
//                    case 1:
//                        toolbar.setTitle("Video");
//                        break;
//                    case 2:
//                        toolbar.setTitle("Privacy");
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//        setUpTabIcons();

    }

    //    private void setUpTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(SEARCH_SERVICE);
//
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.action_album:
//                preferenceManager.saveGalleryView(true);
//                break;
//            case R.id.action_all_image:
//                preferenceManager.saveGalleryView(false);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void runtimePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
//        else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//        }
        else {
            //permission granted
            startApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            startApp();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onClick(View v) {

//        Fragment selectedFragment = null;
//
//        switch (v.getId()) {
//            case R.id.tab_gallery:
//                ivGallery.setImageResource(R.drawable.ic_gallery_selected);
//                ivVideo.setImageResource(R.drawable.ic_video);
//                ivPrivacy.setImageResource(R.drawable.ic_padlock);
//
//                tvGallery.setTextColor(ContextCompat.getColor(this,R.color.colorText));
//                tvVideo.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//                tvPrivacy.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//
//                selectedFragment = new GalleryFragment();
//                break;
//            case R.id.tab_video:
//                ivGallery.setImageResource(R.drawable.ic_gallery);
//                ivVideo.setImageResource(R.drawable.ic_video_selected);
//                ivPrivacy.setImageResource(R.drawable.ic_padlock);
//
//                tvGallery.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//                tvVideo.setTextColor(ContextCompat.getColor(this,R.color.colorText));
//                tvPrivacy.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//
//                selectedFragment = new VideoFragment();
//                break;
//            case R.id.tab_privacy:
//                ivGallery.setImageResource(R.drawable.ic_gallery);
//                ivVideo.setImageResource(R.drawable.ic_video);
//                ivPrivacy.setImageResource(R.drawable.ic_padlock_selected);
//
//                tvGallery.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//                tvVideo.setTextColor(ContextCompat.getColor(this,R.color.colorDefText));
//                tvPrivacy.setTextColor(ContextCompat.getColor(this,R.color.colorText));
//
//                selectedFragment = new PrivacyFragment(this);
//                break;
//        }
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFbInterstitialAds();
    }

    private void refreshAd(View view) {

        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.admob_native))
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = view.findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdManagerAdRequest.Builder().build());

    }

    private void loadExitDialog() {

        dialog = new Dialog(this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.exit_dialog, null);
        view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAdManagerInterstitialAd != null) {
                    mAdManagerInterstitialAd.show(MainActivity.this);
                }
            }
        });

        refreshAd(view);
        dialog.setContentView(view);

    }

    @Override
    public void onBackPressed() {
        /*finish();
        startActivity(new Intent(MainActivity.this,FullScreenNativeAdsActivity.class));*/

        dialog.show();

    }

    private void loadFbInterstitialAds() {

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(MainActivity.this, getString(R.string.admob_interstitial), adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        mAdManagerInterstitialAd = interstitialAd;
                        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                finish();
//                                Intent intent = new Intent(getActivity(), AlbumImageActivity.class);
//                                intent.putExtra("folderPath", pictureFolderPath);
//                                intent.putExtra("folderName", folderName);
//                                startActivity(intent);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                finish();
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
                        finish();
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

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            Tools.FullScreencall(this);
        }
    }*/

}