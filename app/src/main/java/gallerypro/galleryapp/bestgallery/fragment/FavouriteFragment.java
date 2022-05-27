package gallerypro.galleryapp.bestgallery.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import gallerypro.galleryapp.bestgallery.R;


public class FavouriteFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TemplateView my_template;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabLayout);
        my_template = view.findViewById(R.id.my_template);

        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(getChildFragmentManager());

        pagerAdapter.addFragment(new FavPhotoFragment(),"Photos");
        pagerAdapter.addFragment(new FavVideoFragment(),"Videos");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        refreshAd(view);
        return view;
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        ArrayList<String> stringList = new ArrayList<>();

        public ImagePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            stringList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
    }
    private void refreshAd(View view) {

        AdLoader adLoader = new AdLoader.Builder(requireActivity(), getString(R.string.admob_native))
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
}