package com.example.lenmiriotmessage.Drawer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lenmiriotmessage.Adapters.UserListAdapter;
import com.example.lenmiriotmessage.Models.UserModel;
import com.example.lenmiriotmessage.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;

public class UserFragment extends Fragment {

    private final List<UserModel> mUserList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference mReference;
    private String userName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("users");

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
        View v = inflater.inflate(R.layout.fragment_users, null);

        recyclerView = v.findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
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
                mUserList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    UserModel userModel = snap.getValue(UserModel.class);
                    if (userModel != null) mUserList.add(userModel);
                }
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void populateMessagesRecyclerView() {
        UserListAdapter adapter = new UserListAdapter(mUserList, getActivity());
        recyclerView.setAdapter(adapter);
    }

}
