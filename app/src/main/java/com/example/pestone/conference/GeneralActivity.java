package com.example.pestone.conference;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.pestone.conference.Fragments.AboutFragment;
import com.example.pestone.conference.Fragments.ChatFragment;
import com.example.pestone.conference.Fragments.CreateRoomFragment;
import com.example.pestone.conference.Fragments.FriendsFragment;
import com.example.pestone.conference.Fragments.RoomsFragment;
import com.example.pestone.conference.Fragments.SettingsFragment;
import com.example.pestone.conference.Models.UserProfileModel;
import com.example.pestone.conference.Utility.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class GeneralActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    RoomsFragment roomsFragment;
    CreateRoomFragment createRoomFragment;

    UserProfileModel userProfileModel;

    public String CURRENT_TAG = Constants.TAG_ROOM;

    public static int navItemIndex = 0;

    NavigationView navigationView;
    TextView userNameDrawer, HeaderUserName;

    private DatabaseReference mReference;
    private FirebaseAuth mAuthorization;
    FloatingActionButton mFab, mStreamingChat;

    FragmentTransaction transaction;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mAuthorization = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userNameDrawer = (TextView) findViewById(R.id.txtUserNameDrawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_exit);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        HeaderUserName = (TextView) headerView.findViewById(R.id.txtUserNameDrawer);
        mStreamingChat = (FloatingActionButton) findViewById(R.id.streaming_chat);
        mStreamingChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GeneralActivity.this, VideoChatFragment.class));
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navItemIndex = 1;
                visiblityFAB();
                createRoom();

            }
        });

        roomsFragment = new RoomsFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentlayout, roomsFragment, Constants.TAG_ROOM).commit();
        setTitle("Комнаты");
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_rooms) {

            fragmentClass = RoomsFragment.class;
            navItemIndex = 0;
            CURRENT_TAG = Constants.TAG_ROOM;

        } else if (id == R.id.nav_friends) {

            fragmentClass = FriendsFragment.class;
            navItemIndex = 1;
            CURRENT_TAG = Constants.TAG_CONTACTS;

        } else if (id == R.id.nav_settings) {

            fragmentClass = SettingsFragment.class;
            navItemIndex = 1;
            CURRENT_TAG = Constants.TAG_SETTINGS;

        } else if (id == R.id.nav_abouting) {

            fragmentClass = AboutFragment.class;
            navItemIndex = 1;
            CURRENT_TAG = Constants.TAG_ABOUT;

        } else if (id == R.id.nav_LogOut) {
            exitDialog("Вы уверены, что хотите выйти из аккаунта?", "Выход из аккаунта");

        } else if (id == R.id.nav_exit) {
            exitDialog("Вы уверены, что хотите выйти?", "Выход");
        }

        try {
            assert fragmentClass != null;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        visiblityFAB();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentlayout, fragment, CURRENT_TAG).commit();

        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);

        setTitle(item.getTitle());

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_items, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createRoom() {
        createRoomFragment = new CreateRoomFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentlayout, createRoomFragment, "CreateRooms").addToBackStack("").commit();
    }

    @Override
    public void onBackPressed() {
        RoomsFragment roomsFragments = (RoomsFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_ROOM);
        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(Constants.TAG_CHAT);
        roomsFragment = new RoomsFragment();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (roomsFragments != null && roomsFragment.isVisible()) {
            mFab.show();
        } else {
            mFab.hide();
        }
        if (chatFragment != null && chatFragment.isVisible()) {
            mStreamingChat.show();
        } else {
            mStreamingChat.hide();
        }
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserName();
        if (userProfileModel != null) {
            HeaderUserName.setText(userProfileModel.getUsername());
        }else setUserName();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public FloatingActionButton getFloatingButton() {
        return mFab;
    }

    public FloatingActionButton getFloatingButtonStream() {
        return mStreamingChat;
    }


    public void signOut() {
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
                                    user.getRef().child("isOnline").setValue("false");
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

    public void exitDialog(String message, final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (title.equals("Выход из аккаунта")) {
                    auth.signOut();
                    signOut();
                } else {
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void visiblityFAB() {
        switch (navItemIndex) {
            case 0:
                mFab.show();
                break;
            case 1:
                mFab.hide();
                break;
        }
    }

    public void setUserName() {
        mReference.orderByChild("email").equalTo(mAuthorization.getCurrentUser().getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot room : dataSnapshot.getChildren()) {
                                UserProfileModel user = room.getValue(UserProfileModel.class);
                                if (user != null) {
                                    userProfileModel = user;
                                }
                            }
                        } else {
                            userProfileModel = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}