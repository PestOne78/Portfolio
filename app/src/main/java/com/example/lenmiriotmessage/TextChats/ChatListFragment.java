package com.example.lenmiriotmessage.TextChats;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.Adapters.ChatListAdapter;
import com.example.lenmiriotmessage.GeneralActivity;
import com.example.lenmiriotmessage.Models.ChatModel;
import com.example.lenmiriotmessage.R;
import com.example.lenmiriotmessage.Utility.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;

public class ChatListFragment extends Fragment {
    private final List<ChatModel> mChatList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference mReference;
    private String userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("chats");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userName = bundle.getString("userName", null);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.fragment_chat_list, null);

        recyclerView = v.findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Fab Scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                FloatingActionButton mFab = ((GeneralActivity)
                        requireActivity()).getFloatingButton();
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
        queryMessagesForUsers();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void queryMessagesForUsers() {

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    ChatModel chatModel = snap.getValue(ChatModel.class);
                    if (chatModel != null) mChatList.add(chatModel);
                }
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void populateMessagesRecyclerView() {
        ChatListAdapter adapter = new ChatListAdapter(mChatList,
                getActivity(), items -> {
            Constants.pushKey = items.getPushKey();
            Log.i(Constants.LOG_TAG, "RoomDescription    :   " +
                    Constants.RoomDescription + "\n"
                    + Constants.TOKEN + "\n" + Constants.SESSION_ID);

            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("PushKey", items.getPushKey());
            bundle.putString("userName", userName);
            chatFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentlayout, chatFragment,
                            Constants.TAG_CHAT)
                    .addToBackStack(Constants.TAG_CHAT)
                    .commit();
            FloatingActionButton mFabPlus = ((GeneralActivity) requireActivity())
                    .getFloatingButton();
            mFabPlus.hide();
        });
        recyclerView.setAdapter(adapter);
    }
}
