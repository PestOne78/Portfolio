package com.example.lenmiriotmessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lenmiriotmessage.Auth.AuthActivity;
import com.example.lenmiriotmessage.Drawer.AboutFragment;
import com.example.lenmiriotmessage.Drawer.SettingsFragment;
import com.example.lenmiriotmessage.Drawer.UserFragment;
import com.example.lenmiriotmessage.Models.UserModel;
import com.example.lenmiriotmessage.TextChats.ChatListFragment;
import com.example.lenmiriotmessage.TextChats.CreateChatFragment;
import com.example.lenmiriotmessage.Utility.Constants;
import com.example.lenmiriotmessage.WebRTC.P2pActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class GeneralActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String CURRENT_TAG;
    public String userName;
    public FloatingActionButton mFab, mStreamingChat;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private int id;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        setSupportActionBar(findViewById(R.id.toolbar));

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, findViewById(R.id.toolbar), R.string.app_name, R.string.app_exit);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView usernameDrawer = navigationView.getHeaderView(0)
                .findViewById(R.id.textview_username_drawer);
        TextView userlvlDrawer = navigationView.getHeaderView(0)
                .findViewById(R.id.textview_userlvl_drawer);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.child("users")
                        .child(Objects.requireNonNull(auth.getCurrentUser())
                                .getUid()).getValue(UserModel.class);
                usernameDrawer.setText(Objects.requireNonNull(user).getusername());
                userlvlDrawer.setText(Objects.requireNonNull(user).getuserLVL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mStreamingChat = findViewById(R.id.streaming_chat);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            visiblityFAB(false);
            swapFragment(new CreateChatFragment(), Constants.TAG_CREATE_MACHINES);
        });

        chatsFragment();
        navigationView.getMenu().getItem(0).setChecked(true);

        //обработка кнопки назад
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (id != R.id.nav_machines) {
                    chatsFragment();
                } else exitDialog("Вы уверены, что хотите выйти?"
                        , "Выход");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        id = item.getItemId();

        if (id == R.id.nav_machines) {
            CURRENT_TAG = Constants.TAG_MACHINES;
            swapFragment(new ChatListFragment(), CURRENT_TAG);

        } else if (id == R.id.nav_users) {
            CURRENT_TAG = Constants.TAG_CONTACTS;
            swapFragment(new UserFragment(), CURRENT_TAG);

        } else if (id == R.id.nav_settings) {
            CURRENT_TAG = Constants.TAG_SETTINGS;
            swapFragment(new SettingsFragment(), CURRENT_TAG);

        } else if (id == R.id.nav_about) {
            CURRENT_TAG = Constants.TAG_ABOUT;
            swapFragment(new AboutFragment(), CURRENT_TAG);

        } else if (id == R.id.nav_sign_out) {
            exitDialog("Вы уверены, что хотите выйти из аккаунта?"
                    , "Выход из аккаунта");

        } else if (id == R.id.nav_exit) {
            exitDialog("Вы уверены, что хотите выйти?"
                    , "Выход");
        }
        mStreamingChat.hide();
        visiblityFAB(id == R.id.nav_machines);

        item.setChecked(!item.isChecked());
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void swapFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentlayout, fragment
                        , tag)
                .addToBackStack(tag)
                .commit();
    }

    public FloatingActionButton getFloatingButton() {
        return mFab;
    }

    public void exitDialog(String message, final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, id) -> {
            if (title.equals("Выход из аккаунта")) {
                setOffline();
                auth.signOut();
                startActivity(new Intent(GeneralActivity.this,
                        AuthActivity.class));
            } else {
                setOffline();
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, (dialog, id) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setOffline() {
        mDatabase.child("users")
                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .child("isOnline")
                .setValue(false);
    }

    public void visiblityFAB(boolean i) {
        if (i) mFab.show();
        else mFab.hide();
    }

    private void chatsFragment() {
        CURRENT_TAG = Constants.TAG_MACHINES;
        swapFragment(new ChatListFragment(), CURRENT_TAG);

        visiblityFAB(true);
        id = R.id.nav_machines;
        setTitle(Constants.TAG_MACHINES);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void p2pCall(){
        startActivity(new Intent(
                GeneralActivity.this,
                P2pActivity.class));
    }
}