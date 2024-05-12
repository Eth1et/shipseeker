package com.example.shipseeker.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shipseeker.fragment.AdminFragment;
import com.example.shipseeker.fragment.BookedFragment;
import com.example.shipseeker.fragment.HomeFragment;
import com.example.shipseeker.fragment.RoutesFragment;
import com.google.firebase.auth.FirebaseAuth;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    boolean isAdmin;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, boolean isAdmin) {
        super(fm, behavior);
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return new RoutesFragment();
            case 2: return new BookedFragment();
            case 3:
                if(isAdmin)
                    return new AdminFragment();
                return new HomeFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3 + (isAdmin? 1 : 0);
    }
}
