package com.example.pestone.conference.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.example.pestone.conference.Adapters.RoomsAdapter;
import com.example.pestone.conference.GeneralActivity;
import com.example.pestone.conference.Models.RoomModel;
import com.example.pestone.conference.R;
import com.example.pestone.conference.Utility.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomsFragment extends Fragment {

    private ChatFragment chat;
    private RecyclerView recyclerView;
    private List<RoomModel> mRoomList = new ArrayList<>();
    private RoomsAdapter adapter = null;
    private FragmentTransaction fTrans;

    private FirebaseDatabase database;
    private DatabaseReference mReference;

    private final static String CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        mReference = database.getReference("Rooms");
        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_rooms, null);

        recyclerView = (RecyclerView) v.findViewById(R.id.rooms_list);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Fab Scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                FloatingActionButton mFab = ((GeneralActivity) getActivityNonNull()).getFloatingButton();

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

        mRoomList.clear();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    RoomModel room = snap.getValue(RoomModel.class);

                    if (room != null) {
                        if (room.getSenderUID().equals(CurrentUserID)
                                || room.getReciverUID().equals(CurrentUserID)) {
                            mRoomList.add(room);
                        }
                    }
                }
                populateMessagesRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateMessagesRecyclerView() {
        adapter = new RoomsAdapter(mRoomList, getActivity(), new RoomsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RoomModel items) {

                Constants.pushKey = items.getPushKey();

                Log.i(Constants.LOG_TAG, "RoomDescription    :   " + Constants.RoomDescription +"\n"
                        +Constants.TOKEN +"\n" + Constants.SESSION_ID);

                chat = new ChatFragment();
                fTrans = getActivityNonNull().getSupportFragmentManager().beginTransaction();
                fTrans.replace(R.id.fragmentlayout, chat, Constants.TAG_CHAT).addToBackStack(Constants.TAG_CHAT).commit();
                GeneralActivity generalActivity = new GeneralActivity();
                FloatingActionButton mFab= ((GeneralActivity)getActivityNonNull()).getFloatingButtonStream();
                mFab.show();
                FloatingActionButton mFabPlus = ((GeneralActivity)getActivityNonNull()).getFloatingButton();
                mFabPlus.hide();

            }
        });
        recyclerView.setAdapter(adapter);
    }

    protected FragmentActivity getActivityNonNull(){
        if(super.getActivity()!=null) {
            return super.getActivity();
        }else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }
}
