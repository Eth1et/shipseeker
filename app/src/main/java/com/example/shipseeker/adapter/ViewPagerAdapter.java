package com.example.shipseeker.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shipseeker.fragments.BookedFragment;
import com.example.shipseeker.fragments.HomeFragment;
import com.example.shipseeker.fragments.RoutesFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new HomeFragment();
            case 1: return new RoutesFragment();
            default: return new BookedFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
