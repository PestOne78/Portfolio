package com.example.pestone.conference.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {

    ResetPassFragment passFragment;
    SignupFragment signupFragment;
    FragmentTransaction fTransaction;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    private DatabaseReference mReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReference = FirebaseDatabase.getInstance().getReference("users");
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, null);

        auth = FirebaseAuth.getInstance();

        //Если входил
        if (auth.getCurrentUser() != null) {
            getActivity().startActivity(new Intent(getActivity(), GeneralActivity.class));
        }

        inputEmail = (EditText) v.findViewById(R.id.email);
        inputPassword = (EditText) v.findViewById(R.id.password);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        btnSignup = (Button) v.findViewById(R.id.btn_signup);
        btnLogin = (Button) v.findViewById(R.id.btn_login);
        btnReset = (Button) v.findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Переход на регистрацию
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupFragment = new SignupFragment();
                fTransaction = getActivityNonNull().getSupportFragmentManager().beginTransaction();
                fTransaction.replace(R.id.signup_layout, signupFragment).addToBackStack("").commit();
            }
        });

        //Смена пароля
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passFragment = new ResetPassFragment();
                fTransaction = getActivityNonNull().getSupportFragmentManager().beginTransaction();
                fTransaction.replace(R.id.signup_layout, passFragment).addToBackStack("").commit();
            }
        });

        //Вход под новым данным
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivityNonNull().getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivityNonNull().getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Авторизация
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
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
                                    getActivityNonNull().startActivity(new Intent(getActivity(), GeneralActivity.class));

                                    mReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {

                                                UserProfileModel model = Snapshot.getValue(UserProfileModel.class);
                                                assert model != null;

                                                if (model.getUID().equals(auth.getCurrentUser().getUid())) {

                                                    Query query = mReference.orderByChild("uid").equalTo(auth.getCurrentUser().getUid());
                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                                user.getRef().child("isOnline").setValue("true");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                }
                            }
                        });
            }
        });
        return v;
    }
}