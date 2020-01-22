package pestone.wallpaperfootbal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import pestone.wallpaperfootbal.Fragments.AboutFragment;
import pestone.wallpaperfootbal.Fragments.FullScreenFragment;
import pestone.wallpaperfootbal.Fragments.SlideActivityFragment;

public class SlideActivity extends AppCompatActivity {

    private String TAG;
    Toolbar toolbar;

    // Графика
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TAG = getApplicationContext().getString(R.string.slide_fragment);
        setFragment(SlideActivityFragment.class, TAG);
    }

    // Меню туллбара
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    // Свитч на элементы меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                TAG = getApplication().getString(R.string.about_fragment);
                setFragment(AboutFragment.class, TAG);
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return true;
    }

    // Открытие фрагмента
    private void setFragment(Class fragmentClass, String TAG) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_layout, fragment, TAG).addToBackStack(TAG).commit();
        setTitle(TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Обработка нажатия назад
    @Override
    public void onBackPressed() {
        FullScreenFragment screenFragment = (FullScreenFragment) getSupportFragmentManager()
                .findFragmentByTag(getApplication().getString(R.string.full_screen_fragment));

        AboutFragment aboutFragment = (AboutFragment) getSupportFragmentManager()
                .findFragmentByTag(getApplication().getString(R.string.about_fragment));

        SlideActivityFragment slideActivityFragment = (SlideActivityFragment) getSupportFragmentManager()
                .findFragmentByTag(getApplication().getString(R.string.slide_fragment));

        if (aboutFragment != null && aboutFragment.isVisible()
                || screenFragment != null && screenFragment.isVisible()) {
            setTitle(R.string.slide_fragment);
        } else if (slideActivityFragment != null && slideActivityFragment.isVisible()) {
            finish();
        }
        super.onBackPressed();
    }

}
