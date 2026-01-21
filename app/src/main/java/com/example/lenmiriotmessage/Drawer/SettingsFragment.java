package com.example.lenmiriotmessage.Drawer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lenmiriotmessage.Auth.AuthActivity;
import com.example.lenmiriotmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.annotations.Nullable;


public class SettingsFragment extends Fragment {

    // private Button btnSendResetEmail;
    private Button changeEmail;
    private Button changePassword;
    //private Button sendEmail;
    private Button remove;
    // private Button signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_settings, null);

        auth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                requireActivity().finish();
                startActivity(new Intent(getActivity(), AuthActivity.class));
            }
        };

        Button btnChangeEmail = v.findViewById(R.id.change_email_button);
        Button btnChangePassword = v.findViewById(R.id.change_password_button);
        Button btnRemoveUser = v.findViewById(R.id.remove_user_button);
        changeEmail = v.findViewById(R.id.changeEmail);
        changePassword = v.findViewById(R.id.changePass);
        remove = v.findViewById(R.id.remove);

        newEmail = v.findViewById(R.id.new_email);
        password = v.findViewById(R.id.password);
        newPassword = v.findViewById(R.id.newPassword);

        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = v.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(v1 -> {
            newEmail.setVisibility(View.VISIBLE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
            changeEmail.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        });

        changeEmail.setOnClickListener(v12 -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newEmail.getText().toString().trim().isEmpty()) {
                user.updateEmail(newEmail.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Адрес электронной почты обновлен. " +
                                        "Пожалуйста, войдите в систему с новым email!",
                                        Toast.LENGTH_LONG).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Не удалось обновить email!",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else if (newEmail.getText().toString().trim().isEmpty()) {
                newEmail.setError("Введите email");
                progressBar.setVisibility(View.GONE);
            }
        });

        btnChangePassword.setOnClickListener(v13 -> {
            newEmail.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.VISIBLE);
            changeEmail.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
            remove.setVisibility(View.GONE);
        });

        changePassword.setOnClickListener(v14 -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newPassword.getText().toString().trim().isEmpty()) {
                if (newPassword.getText().toString().trim().length() < 6) {
                    newPassword.setError("Пароль слишком короткий, введите минимум 6 символов");
                    progressBar.setVisibility(View.GONE);
                } else {
                    user.updatePassword(newPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),
                                                "Пароль обновлен, войдите с новым паролем!",
                                                Toast.LENGTH_SHORT).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(),
                                                "Не удалось обновить пароль!",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            } else if (newPassword.getText().toString().trim().isEmpty()) {
                newPassword.setError("Введите пароль");
                progressBar.setVisibility(View.GONE);
            }
        });


        btnRemoveUser.setOnClickListener(v15 -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),
                                        "Ваш профиль удален :( Создайте учетную запись!",
                                        Toast.LENGTH_SHORT).show();
                                requireActivity().startActivity
                                        (new Intent(getActivity(), AuthActivity.class));
                                requireActivity().finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Невозможно удалить ваш аккаунт!",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
        return v;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}