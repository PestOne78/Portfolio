package com.example.lenmiriotmessage.Auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lenmiriotmessage.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginig);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.signup_layout, new SignInFragment())
                .commit();
    }
}
