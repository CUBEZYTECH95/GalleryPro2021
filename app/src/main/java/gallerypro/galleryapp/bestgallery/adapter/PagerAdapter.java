package gallerypro.galleryapp.bestgallery.adapter;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import gallerypro.galleryapp.bestgallery.fragment.PrivacyFragment;
import gallerypro.galleryapp.bestgallery.fragment.GalleryFragment;
import gallerypro.galleryapp.bestgallery.fragment.VideoFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    Context context;

    public PagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
                fragment = new GalleryFragment();
        } else if (position == 1) {
            fragment = new VideoFragment();
        } else if (position == 2) {
            fragment = new PrivacyFragment(context);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Gallery";
        } else if (position == 1) {
            title = "Video";
        } else if (position == 2) {
            title = "Privacy";
        }
        return title;
    }

}
