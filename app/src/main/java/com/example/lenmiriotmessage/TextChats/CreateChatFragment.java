package com.example.lenmiriotmessage.TextChats;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.lenmiriotmessage.GeneralActivity;
import com.example.lenmiriotmessage.Models.ChatModel;
import com.example.lenmiriotmessage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import io.reactivex.rxjava3.annotations.Nullable;

public class CreateChatFragment extends Fragment {
    private EditText NameChat;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference("chats");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_create_chat, null);

        Button btnCancel = v.findViewById(R.id.btn_cancel);
        Button btnCreateChat = v.findViewById(R.id.btn_create_chat);
        NameChat = v.findViewById(R.id.chat_name);


        btnCreateChat.setOnClickListener(v1 -> {

            String Name = NameChat.getText().toString().trim();
            String Sid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            String PushKey = mDatabase.push().getKey();

            if (TextUtils.isEmpty(Name)) {
                Toast.makeText(getActivity(), "Введите название комнаты!", Toast.LENGTH_SHORT).show();
                return;
            }
            addChat(new ChatModel(Name, "", Sid, false, PushKey, null, null));
        });

        btnCancel.setOnClickListener(v12 -> {
            swapFragment();
            FloatingActionButton mFab = ((GeneralActivity) requireActivity())
                    .getFloatingButton();
            mFab.show();

        });
        return v;
    }

    public void addChat(ChatModel chatModel) {
        try {
            mDatabase.child(chatModel.getPushKey()).setValue(chatModel);
            Toast.makeText(getActivity(), "Чат создан!", Toast.LENGTH_SHORT).show();
            swapFragment();
            FloatingActionButton mFab = ((GeneralActivity)
                    getActivityNonNull()).getFloatingButton();
            mFab.show();

        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Ошибка" + ex, Toast.LENGTH_LONG).show();
        }
    }

    public void swapFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentlayout, new ChatListFragment())
                .commit();
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}
