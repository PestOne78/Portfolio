package com.junior.test.jokes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.junior.test.jokes.fragments.JokesFragment;
import com.junior.test.jokes.fragments.WebFragment;
import com.junior.test.jokes.jokes.JokeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String JOKES_TAG = "Jokes";
    private static final String WEB_TAG = "Web";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationSelectedListener
            = item -> {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.btn_jokes:
                fragment = new JokesFragment();
                loadFragment(fragment, JOKES_TAG);
                return true;
            case R.id.btn_web:
                fragment = new WebFragment();
                loadFragment(fragment, WEB_TAG);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPerm();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void loadFragment(Fragment fragment, String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, TAG).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void JokeEventRecived(JokeEvent jokeEvent) {
        Log.i("EventBus", "i update data!");
        JokesFragment jokesFragment = (JokesFragment) getSupportFragmentManager().findFragmentByTag(JOKES_TAG);
        assert jokesFragment != null;
        jokesFragment.updateRecyclerView();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_exit)
                .setMessage(R.string.exit_app)
                .setIcon(R.drawable.chuck_norris)
                .setCancelable(true)
                .setPositiveButton(R.string.yes,
                        (dialog, which) -> finish())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initBNW() {
        BottomNavigationView botNav = findViewById(R.id.bot_nav_view);
        botNav.setOnNavigationItemSelectedListener(mOnNavigationSelectedListener);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) botNav.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    private void requestPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            shouldShowRequestPerm();
        } else {
            initBNW();
            loadFragment(new JokesFragment(), JOKES_TAG);
        }
    }

    private void shouldShowRequestPerm() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET)) {
            View view = findViewById(R.id.main_container);
            final String message = "Internet permission is needed to show jokes";
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", v -> requestInternet())
                    .show();
        } else {
            requestInternet();
        }
    }

    private void requestInternet() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initBNW();
                loadFragment(new JokesFragment(), JOKES_TAG);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.permission_denied)
                        .setMessage(R.string.permission_message)
                        .setIcon(R.drawable.chuck_norris)
                        .setPositiveButton(R.string.grant, ((dialog, which) -> requestInternet()))
                        .setNegativeButton(R.string.ok, (dialog, which) -> finish());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
