package com.example.lenmiriotmessage.Auth;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.lenmiriotmessage.GeneralActivity;
import com.example.lenmiriotmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.Nullable;

public class SignInFragment extends Fragment {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkSignUp(auth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View v = inflater
                .inflate(R.layout.fragment_sign_in, null);

        inputEmail = v.findViewById(R.id.email);
        inputPassword = v.findViewById(R.id.password);
        progressBar = v.findViewById(R.id.progressBar);
        Button btnSignup = v.findViewById(R.id.btn_signup);
        Button btnLogin = v.findViewById(R.id.btn_login);
        Button btnReset = v.findViewById(R.id.btn_reset_password);

        //Переход на регистрацию
        btnSignup.setOnClickListener(v13 -> swapFragment(new SignUpFragment()));
        //Смена пароля
        btnReset.setOnClickListener(v12 -> swapFragment(new ResetFragment()));

        //Вход
        btnLogin.setOnClickListener(v1 -> {
            final String email = inputEmail.getText().toString().trim();
            final String password = inputPassword.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (checkEmpty(email, password)) {
                signIn(email, password);
            }
            progressBar.setVisibility(View.GONE);
        });
        return v;
    }

    //Проверка на пусто
    private boolean checkEmpty(String email, String pass) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivityNonNull()
                            .getApplicationContext(),
                            "Введите почту!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getActivityNonNull()
                            .getApplicationContext(),
                            "Введите пароль!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void swapFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.signup_layout, fragment)
                .addToBackStack("")
                .commit();
    }

    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    progressBar.setVisibility(View.GONE);
                    if (!task.isSuccessful()) {
                        // there was an error
                        if (password.length() < 6) {
                            inputPassword.setError(getString(R.string.minimum_password));
                        } else {
                            Toast.makeText(getActivity(),
                                    getString(R.string.auth_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        checkSignUp(auth);
                    }
                });
    }

    //Проверка существующей авторизации
    private void checkSignUp(FirebaseAuth auth) {
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(Objects
                    .requireNonNull(auth.getCurrentUser())
                    .getUid()).child("isOnline").setValue(true);
            requireActivity().startActivity
                    (new Intent(getActivity(), GeneralActivity.class));
            requireActivity().finish();
        } else if (auth.getCurrentUser() != null && !auth.getCurrentUser().isEmailVerified()) {
            Toast.makeText(getActivity()
                            , "Подтвердите свою почту!"
                            , Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}

