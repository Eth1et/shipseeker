package com.example.shipseeker.activity;

import android.os.Bundle;
import android.util.Log;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.R;
import com.example.shipseeker.SharedViewModel;
import com.example.shipseeker.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SECRET_KEY = 991412911;
    private static final String LOG_TAG = MainActivity.class.getName();

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    FirebaseManager fm;
    FirebaseAuth auth;
    SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if(bundle == null || bundle.getInt("SECRET_KEY", 0) != SECRET_KEY){
            finish();
            return;
        }

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(SharedViewModel.class);

        fm = FirebaseManager.getInstance();
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null){
            Log.e(LOG_TAG, "COULDNT GET login data");
            finish();
            return;
        }

        setContentView(R.layout.main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.i("FASSZ", "nyomjuk uid: " + auth.getUid());
        fm.isUserAdmin(this, auth.getUid()).observe(this, isAdmin -> {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0, isAdmin);
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(viewPagerAdapter);

            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager, true);
            try{
                tabLayout.getTabAt(0).setText(getString(R.string.home));
                tabLayout.getTabAt(1).setText(getString(R.string.routes));
                tabLayout.getTabAt(2).setText(getString(R.string.booked));
                if(isAdmin){
                    tabLayout.getTabAt(3).setText(getString(R.string.admin));
                }
            }catch (NullPointerException e){
                Log.e(LOG_TAG, "couldn't set tab names");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth.signOut();
    }
}