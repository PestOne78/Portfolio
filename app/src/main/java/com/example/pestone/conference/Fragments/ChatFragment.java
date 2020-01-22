package com.example.pestone.conference.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pestone.conference.Adapters.MessageAdapter;
import com.example.pestone.conference.GeneralActivity;
import com.example.pestone.conference.Models.MessageModel;
import com.example.pestone.conference.Models.RoomModel;
import com.example.pestone.conference.R;
import com.example.pestone.conference.Utility.Constants;
import com.example.pestone.conference.VideoChatFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


public class ChatFragment extends Fragment {

    private RecyclerView mChatsRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private FloatingActionButton mSendButton;
    private DatabaseReference mMessagesDBRef;
    private DatabaseReference mRoomReference;
    private List<MessageModel> mMessagesList = new ArrayList<>();
    private MessageAdapter adapter = null;

    private Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.US);

    private final String pushKey = Constants.pushKey;
    private RoomModel roomModel;

    private String messageTime;
    private final String senderUIDCash = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final String senderEmailCash = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessagesDBRef = FirebaseDatabase.getInstance().getReference().child("MessageUser");
        mRoomReference = FirebaseDatabase.getInstance().getReference().child("Rooms");

    }


    public void queryDB(){
        mRoomReference.orderByChild("pushKey").equalTo(pushKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot room : dataSnapshot.getChildren()) {
                        RoomModel rooms = room.getValue(RoomModel.class);
                        if (rooms != null) {
                            roomModel = rooms;
                        }
                    }
                }else {
                    roomModel = null;
                }

                if (senderUIDCash.equals(roomModel.getReciverUID())) {

                    roomModel.setReciverUID(roomModel.getSenderUID());
                    roomModel.setReciverEmail(roomModel.getSenderEmail());
                    roomModel.setSenderUID(senderUIDCash);
                    roomModel.setSenderEmail(senderEmailCash);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_chat, null);

        //initialize the views
        mChatsRecyclerView = (RecyclerView) v.findViewById(R.id.list_of_message);
        mMessageEditText = (EditText) v.findViewById(R.id.NewMessage);
        mSendButton = (FloatingActionButton) v.findViewById(R.id.fabSend);
        mChatsRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mChatsRecyclerView.setLayoutManager(mLayoutManager);



        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString();

                if (message.isEmpty()) {
                    Toast.makeText(getActivity(), "Вы не ввели сообщение", Toast.LENGTH_SHORT).show();
                } else {
                    //message is entered, send
                    messageTime = dateFormat.format(calendar.getTime());
                    sendMessageToFirebase(message, roomModel.getSenderUID(), roomModel.getReciverUID(),
                            roomModel.getSenderEmail(), roomModel.getReciverEmail(), messageTime, pushKey);
                }
            }
        });


        mChatsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                FloatingActionButton mFab = ((GeneralActivity) getActivityNonNull()).getFloatingButtonStream();

                CoordinatorLayout.LayoutParams layoutParams =
                        (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();

                int fab_bottomMargin = layoutParams.bottomMargin;

                if (dy > 0) {

                    mFab.animate().translationY(mFab.getHeight() + fab_bottomMargin)
                            .setInterpolator(new LinearInterpolator()).start();
                } else if (dy < 0)
                    mFab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryDB();
        queryMessagesForUser();
    }

    private void sendMessageToFirebase(String messageText, String senderID, String receiverID,
                                       String senderName, String reciverName, String messageTime, String pushKey) {
        mMessagesList.clear();

        MessageModel newMsg = new MessageModel(messageText, senderID, receiverID, senderName, reciverName, messageTime, pushKey);
        mMessagesDBRef.push().setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    //error
                    Toast.makeText(getActivity(), "Ошибка " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    mMessageEditText.setText(null);
                }
            }
        });
    }


    private void queryMessagesForUser() {

        mMessagesList.clear();
        mMessagesDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    MessageModel chatMessage = snap.getValue(MessageModel.class);
                    if (chatMessage != null) {
                        if (chatMessage.getSenderId().equals(senderUIDCash)
                                && chatMessage.getReceiverId().equals(roomModel.getReciverUID())
                                && chatMessage.getPushKey().equals(pushKey)
                                || chatMessage.getSenderId().equals(roomModel.getReciverUID())
                                && chatMessage.getReceiverId().equals(senderUIDCash)
                                && chatMessage.getPushKey().equals(pushKey)) {
                            mMessagesList.add(chatMessage);
                        }
                    }
                }
                /**populate messages**/
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateMessagesRecyclerView() {
        adapter = new MessageAdapter(mMessagesList, getActivity());
        mChatsRecyclerView.setAdapter(adapter);
    }


    protected FragmentActivity getActivityNonNull(){
        if(super.getActivity()!=null) {
            return super.getActivity();
        }else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}