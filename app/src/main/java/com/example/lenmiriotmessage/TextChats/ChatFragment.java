package com.example.lenmiriotmessage.TextChats;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.Adapters.MessageAdapter;
import com.example.lenmiriotmessage.GeneralActivity;
import com.example.lenmiriotmessage.Models.MessageModel;
import com.example.lenmiriotmessage.Models.UserModel;
import com.example.lenmiriotmessage.R;
import com.example.lenmiriotmessage.Utility.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.Nullable;


public class ChatFragment extends Fragment {
    private final List<MessageModel> mMessagesList = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat
            = new SimpleDateFormat("hh:mm", Locale.US);
    private RecyclerView mChatsRecyclerView;
    private EditText mMessageEditText;
    private DatabaseReference mMessage, mUser;
    private FirebaseAuth auth;
    private String messageTime, PushKey, userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            PushKey = bundle.getString("PushKey", null);
        }

        mMessage = FirebaseDatabase.getInstance()
                .getReference().child("messages").child(PushKey);
        mUser = FirebaseDatabase.getInstance()
                .getReference().child("users");
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") final View v = inflater.inflate(R.layout.fragment_chat, null);

        //Инициализация
        mChatsRecyclerView = v.findViewById(R.id.list_of_message);
        mMessageEditText = v.findViewById(R.id.NewMessage);
        FloatingActionButton mSendButton = v.findViewById(R.id.fabSend);
        mChatsRecyclerView.setHasFixedSize(true);
        // Линейка для вивера
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLayoutManager);


        mSendButton.setOnClickListener(v1 -> {
            String message = mMessageEditText.getText().toString();

            if (message.isEmpty()) {
                Toast.makeText(getActivity()
                                , "Вы не ввели сообщение"
                                , Toast.LENGTH_SHORT)
                        .show();
            } else {
                //message is entered, send
                messageTime = dateFormat.format(calendar.getTime());
                sendMessageToFirebase(message,
                        Objects.requireNonNull(auth.getCurrentUser()).getUid(),
                        userName, Constants.TAG_RID, messageTime);
            }
        });

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.child(Objects.requireNonNull(auth.getCurrentUser())
                        .getUid()).getValue(UserModel.class);
                userName = Objects.requireNonNull(user).getusername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryMessagesForUser();
    }

    private void sendMessageToFirebase(String TextMess,
                                       String Sid, String userName, String Rid,
                                       String TimeMess) {
        mMessagesList.clear();
        MessageModel messageModel = new MessageModel(TextMess, Sid, userName, Rid, TimeMess);
        mMessage.push().setValue(messageModel)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //error
                        Toast.makeText(getActivity(), "Ошибка " + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mMessageEditText.setText(null);
                    }
                });
    }

    private void queryMessagesForUser() {
        mMessage.orderByChild("PushKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessagesList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    MessageModel chatMessage = snap.getValue(MessageModel.class);
                    if (chatMessage != null) mMessagesList.add(chatMessage);
                }
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void populateMessagesRecyclerView() {
        MessageAdapter adapter = new MessageAdapter(mMessagesList, getActivity());
        mChatsRecyclerView.setAdapter(adapter);
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}