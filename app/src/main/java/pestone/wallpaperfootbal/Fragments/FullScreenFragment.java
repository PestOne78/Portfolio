package pestone.wallpaperfootbal.Fragments;

import android.Manifest;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import pestone.wallpaperfootbal.R;
import pestone.wallpaperfootbal.Utility.GlideApp;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FullScreenFragment extends Fragment implements View.OnClickListener {

    StorageReference storageRef;
    LinearLayout btn_set, btn_download;
    ImageView fullImage;

    // Данные из предыдущего фрагмента / ссылка на изображение в Firebase
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String path = bundle.getString("URI_KEY");
            if (path != null) {
                storageRef = FirebaseStorage.getInstance().getReference().child(path);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fullscreen, container, false);
    }


    // Графика
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_set = (LinearLayout) view.findViewById(R.id.llSetWallpaper);
        btn_download = (LinearLayout) view.findViewById(R.id.llDownloadWallpaper);

        fullImage = (ImageView) view.findViewById(R.id.imgFullscreen);
        ProgressBar pbImage = (ProgressBar) view.findViewById(R.id.pbLoader);
        btn_set.setOnClickListener(this);
        btn_download.setOnClickListener(this);

        // Загрузка изображения с Firebase / Glide
        GlideApp.with(getActivityNonNull()).load(storageRef).into(fullImage);
        if (fullImage != null) {
            pbImage.setVisibility(View.GONE);
        }
        requestPermissions();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // обработка исключения
    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }

    // Разрешения для риложения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Проверка разрешений для приложения / EasyPermissions
    @AfterPermissionGranted(1)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.SET_WALLPAPER, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getActivityNonNull(), perms)) {
            EasyPermissions.requestPermissions
                    (this, getActivityNonNull().getString(R.string.get_permissions), 1, perms);
        }
    }

    // onClick для ImageButton / сохранить в память устройства / установить на рабочий стол
    @Override
    public void onClick(View v) {
        Bitmap bitmap = ((BitmapDrawable) fullImage.getDrawable()).getBitmap();
        switch (v.getId()) {
            case R.id.llDownloadWallpaper:
                saveImageToSD(bitmap);
                break;
            case R.id.llSetWallpaper:
                setWallpaperBackground(bitmap);
                break;
        }
    }

    // Функция установки изображения на раб. стол
    public void setWallpaperBackground(Bitmap bitmap) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivityNonNull());
            wallpaperManager.setBitmap(bitmap);

            Toast.makeText(getActivityNonNull(),
                    getActivityNonNull().getString(R.string.set_wallpaper_complete), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getActivityNonNull(),
                    getActivityNonNull().getString(R.string.set_wallpaper_failed), Toast.LENGTH_SHORT).show();
        }
    }

    // Сохранение изображения в память устройства
    public void saveImageToSD(Bitmap bitmap) {
        // Проверка доступности
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivityNonNull(),
                    getActivityNonNull().getString(R.string.access_sd), Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание path
        File Directory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + File.separator + "FootballWallpaper");

        // Проверка path до файла
        boolean isCreated = Directory.mkdirs();

        if (!isCreated) Log.i("Directory is Created", "- Created");

        // Создание файла + путь к нему
        File file = new File(Directory, getFileName());

        boolean bool = false;

        if (file.exists())
            bool = file.delete();

        if (!bool) Log.i("Deleting File", "- Access denied");

        // Преобразование в JPEG и сохранение в память
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getActivityNonNull(), getActivityNonNull().getString(R.string.save_file) + " " + Directory, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getActivityNonNull(),
                    getActivityNonNull().getString(R.string.save_file_failed), Toast.LENGTH_SHORT).show();
        }
    }

    // Имя для файла
    @NonNull
    private String getFileName() {
        Random generate = new Random();
        int n = 10000;
        n = generate.nextInt(n);
        return "FootballWallpaper_" + n + ".jpg";
    }

}
