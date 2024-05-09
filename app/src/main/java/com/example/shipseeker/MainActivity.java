package com.example.shipseeker;

import android.os.Bundle;
import android.util.Log;

import com.example.shipseeker.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SECRET_KEY = 991412911;
    private static final String LOG_TAG = MainActivity.class.getName();

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if(bundle == null || bundle.getInt("SECRET_KEY", 0) != SECRET_KEY){
            finish();
        }

        setContentView(R.layout.main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager, true);
        try{
            tabLayout.getTabAt(0).setText(getString(R.string.home));
            tabLayout.getTabAt(1).setText(getString(R.string.routes));
            tabLayout.getTabAt(2).setText(getString(R.string.booked));
        }catch (NullPointerException e){
            Log.e(LOG_TAG, "couldnt set tab names");
        }
    }
}