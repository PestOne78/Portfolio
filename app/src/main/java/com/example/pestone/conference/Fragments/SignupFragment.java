package com.example.pestone.conference.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pestone.conference.GeneralActivity;
import com.example.pestone.conference.Models.UserProfileModel;
import com.example.pestone.conference.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends Fragment {

    private final String IS_ONLINE = "true";

    ResetPassFragment passFragment;
    LoginFragment loginFragment;
    FragmentTransaction fTransaction;

    private DatabaseReference mDatabase;
    private EditText inputName, inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_signup, null);

        btnSignIn = (Button) v.findViewById(R.id.sign_in_button);
        btnSignUp = (Button) v.findViewById(R.id.sign_up_button);
        inputName = (EditText) v.findViewById(R.id.nickname);
        inputEmail = (EditText) v.findViewById(R.id.email);
        inputPassword = (EditText) v.findViewById(R.id.password);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        btnResetPassword = (Button) v.findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passFragment = new ResetPassFragment();
                fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fTransaction.replace(R.id.signup_layout, passFragment).addToBackStack("").commit();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFragment = new LoginFragment();
                fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fTransaction.replace(R.id.signup_layout, loginFragment).addToBackStack("").commit();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nameProfile = inputName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Введите email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nameProfile)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Введите имя!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Введите пароль!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.minimum_password), Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    getActivity().finish();
                                    getActivity().startActivity(new Intent(getActivity(), GeneralActivity.class));

                                    Toast.makeText(getActivity(),
                                            "Аккаунт создан!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                    writeUserDB(nameProfile,
                                            auth.getCurrentUser().getEmail(),
                                            IS_ONLINE, "Null", auth.getCurrentUser().getUid());
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Email уже зарегестрирован!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    //write new User in DataBase Firebase
    public void writeUserDB(String Name, String Email, String IsOnline, String image, String UID) {
        UserProfileModel user = new UserProfileModel(Name, Email, IsOnline, image, UID);
        mDatabase.push().setValue(user);
    }
}
