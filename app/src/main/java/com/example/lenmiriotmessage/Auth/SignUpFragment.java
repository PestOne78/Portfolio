package com.example.lenmiriotmessage.Auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.lenmiriotmessage.Models.UserModel;
import com.example.lenmiriotmessage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.Nullable;

public class SignUpFragment extends Fragment {
    private EditText inputName, inputEmail, inputPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View v = inflater
                .inflate(R.layout.fragment_sign_up, null);

        inputName = v.findViewById(R.id.nickname);
        inputEmail = v.findViewById(R.id.email);
        inputPassword = v.findViewById(R.id.password);
        progressBar = v.findViewById(R.id.progressBar);

        Button btnSignIn = v.findViewById(R.id.sign_in_button);
        Button btnSignUp = v.findViewById(R.id.sign_up_button);
        Button btnResetPassword = v.findViewById(R.id.btn_reset_password);

        Spinner spinnerLVL = v.findViewById(R.id.spinner_userlvl);

        btnResetPassword.setOnClickListener(v13 -> swapFragment(true));
        btnSignIn.setOnClickListener(v12 -> swapFragment(false));

        btnSignUp.setOnClickListener(v1 -> {
            progressBar.setVisibility(View.VISIBLE);

            final String userName = inputName.getText().toString().trim();
            final String email = inputEmail.getText().toString().trim();
            final String password = inputPassword.getText().toString().trim();
            final String userLVL = spinnerLVL.getSelectedItem().toString();

            if (checkEmpty(email, password, userName)) {
                createUser(email, password, userName, userLVL);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    //Запись нового пользователя в бд
    public void writeUserDB(String uid, String username, String userLVL) {
        UserModel user = new UserModel(username, userLVL, true);
        mDatabase.child("users").child(uid).setValue(user);
        progressBar.setVisibility(View.GONE);
    }

    private void createUser(String email, String password,
                            String username, String userLVL) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivityNonNull(), task -> {
                    if (task.isSuccessful()) {
                        sendEmailVerification(Objects.requireNonNull(FirebaseAuth
                                .getInstance().getCurrentUser()));
                        writeUserDB(Objects.requireNonNull(auth
                                        .getCurrentUser()).getUid(),
                                username, userLVL);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        toastMake("Email уже зарегестрирован!");
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(getActivityNonNull(), task -> {
            if (task.isSuccessful()) {
                toastMake(getString(R.string.verify_email));
                auth.signOut();
                swapFragment(false);
            }
        });
    }

    private void swapFragment(boolean i) {
        if (i) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.signup_layout, new ResetFragment())
                    .addToBackStack("")
                    .commit();
        } else {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.signup_layout, new SignInFragment())
                    .addToBackStack("")
                    .commit();
        }
    }

    private boolean checkEmpty(String email, String password, String username) {
        if (TextUtils.isEmpty(email)) {
            toastMake("Введите email!");
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            toastMake("Введите имя!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            toastMake("Введите пароль!");
            return false;
        }
        if (password.length() < 6) {
            toastMake(getString(R.string.minimum_password));
            return false;
        }
        return true;
    }

    private void toastMake(String txt) {
        Toast.makeText(
                getActivityNonNull(),
                txt,
                Toast.LENGTH_SHORT
        ).show();
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}
