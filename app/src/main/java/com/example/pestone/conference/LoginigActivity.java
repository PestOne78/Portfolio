package com.example.pestone.conference;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.pestone.conference.Fragments.LoginFragment;

public class LoginigActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginig);


        loginFragment = new LoginFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.signup_layout, loginFragment);
        transaction.commit();
    }

}
