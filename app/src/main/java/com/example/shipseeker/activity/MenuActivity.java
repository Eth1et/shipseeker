package com.example.shipseeker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shipseeker.FirebaseManager;
import com.example.shipseeker.R;
import com.example.shipseeker.model.User;

public class MenuActivity extends AppCompatActivity {

    private static final String LOG_TAG = MenuActivity.class.getName();

    private int currentLayoutId = -1;

    EditText emailEditText;
    EditText passwordEditText;

    private void openLayout(int layoutId) {
        currentLayoutId = layoutId;

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
        EditText usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);

        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()){
            Log.e(LOG_TAG, "All fields are required");
            return;
        }

        if(!password.equals(confirmPassword)){
            Log.e(LOG_TAG, "The password and the confirmation are not the same");
            return;
        }

        User newUser = new User(username, email, false);

        FirebaseManager.getInstance().createUser(this, newUser, password).observe(this, isSuccessful -> {
            if(isSuccessful){
                Log.i(LOG_TAG, "Registered user: " + username);
                signIn(email, password);
            }else{
                Log.d(LOG_TAG, "Couldnt create user");
            }
        });
    }

    public void login(View view) {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        Log.e(LOG_TAG, "ASD: " + emailEditText.getText());
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(email.trim().isEmpty() || password.trim().isEmpty()){
            Log.e(LOG_TAG, "All fields are required");
            return;
        }

        signIn(email, password);
    }

    private void signIn(String email, String password){
        FirebaseManager.getInstance().loginUser(this, email, password).observe(this, isSuccessful -> {
            if (isSuccessful){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("SECRET_KEY", 991412911);
                startActivity(intent);
            }else{
                Log.e(LOG_TAG, "Couldn't log in");
                if(currentLayoutId != R.layout.login){
                    openLayout(R.layout.login);
                }
            }
        });
    }

    public void closeRegister(View view) {
        openLayout(R.layout.main_menu);
    }

    public void closeLogin(View view) {
        openLayout(R.layout.main_menu);
    }
}