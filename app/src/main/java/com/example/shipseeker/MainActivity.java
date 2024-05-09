package com.example.shipseeker;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private void openLayout(int layoutId) {
        setContentView(layoutId);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openLayout(R.layout.main_menu);
    }

    public void openLogin(View view) {
        openLayout(R.layout.login);
    }

    public void openRegister(View view) {
        openLayout(R.layout.register);
    }

    public void register(View view) {
        System.out.println("register... ");
    }

    public void closeRegister(View view) {
        openLayout(R.layout.main_menu);
    }

    public void login(View view) {
        System.out.println("login... ");
    }

    public void closeLogin(View view) {
        openLayout(R.layout.main_menu);
    }
}