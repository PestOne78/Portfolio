package com.example.pestone.conference.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pestone.conference.Models.UserProfileModel;
import com.example.pestone.conference.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsFragment extends Fragment {

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        TextView txtEmail,txtUser;
        public FriendsViewHolder (View itemView){
            super(itemView);

            txtEmail = (TextView) itemView.findViewById(R.id.txtUserEmail);
            txtUser = (TextView) itemView.findViewById(R.id.txtUserName);
        }

    }

    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference mReference;

    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        mReference = database.getReference("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friends, null);

        recyclerView = (RecyclerView) v.findViewById(R.id.friends_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        FirebaseRecyclerOptions<UserProfileModel> options =
                new FirebaseRecyclerOptions.Builder<UserProfileModel>()
                .setQuery(mReference,UserProfileModel.class).build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserProfileModel,FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull UserProfileModel model) {
                holder.txtUser.setText(model.getUsername());
                holder.txtEmail.setText(model.getEmail());
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.user_list_item,parent,false);
                return new FriendsViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseRecyclerAdapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecyclerAdapter.stopListening();;
    }
}
