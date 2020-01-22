package pestone.wallpaperfootbal.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pestone.wallpaperfootbal.Adapters.RecyclerViewPreviewAdapter;
import pestone.wallpaperfootbal.R;
import pestone.wallpaperfootbal.Structure.Wallpaper;
import pestone.wallpaperfootbal.Utility.GridSpacingItemDecoration;

public class SlideActivityFragment extends Fragment {

    // Глобальные переменные
    private DatabaseReference databaseRef;

    RecyclerViewPreviewAdapter adapter;

    private RecyclerView PreviewImage;
    private List<Wallpaper> Imagelist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ссылка на БД + группировка
        databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.orderByChild("uri_key");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slide, container, false);
    }

    // Графика + украшение RecyclerView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreviewImage = (RecyclerView) view.findViewById(R.id.image_grid_view);

        PreviewImage.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        PreviewImage.setItemAnimator(new DefaultItemAnimator());

        PreviewImage.setHasFixedSize(true);
        PreviewImage.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));
    }

    // Установка адаптера для RecyclerView
    private void setRecyclerView() {
        adapter = new RecyclerViewPreviewAdapter(Imagelist,
                getActivity(), new RecyclerViewPreviewAdapter.OnItemClickListener() {

            @Override
            public void OnItemClick(Wallpaper item) {
                Bundle args = new Bundle(); //Передача аргументов в следующий фрагмент
                args.putString("URI_KEY", item.getUri_key());
                FullScreenFragment fragment = new FullScreenFragment();
                fragment.setArguments(args);

                //Открытие нового фрагмента
                FragmentTransaction transaction = getActivityNonNull().getSupportFragmentManager().beginTransaction();
                String TAG = getActivityNonNull().getString(R.string.full_screen_fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_layout, fragment, TAG).addToBackStack(TAG).commit();
            }
        });
        PreviewImage.setAdapter(adapter);
    }

    // Запрос в DatabaseFirebase
    private void getDataDB() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Wallpaper image = snapshot.getValue(Wallpaper.class);
                    if (image != null) {
                        Imagelist.add(image);   //Заполнение списка элементами
                    }
                }
                setRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ErrorDB", databaseError.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDataDB();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        clearRecyclerView();

    }

    @Override
    public void onStop() {
        super.onStop();
        clearRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // Обработка исключения
    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("GetActivity() вернул Null");
        }
    }

    // Очистка RecyclerView
    private void clearRecyclerView() {
        Imagelist.clear();
        adapter.notifyDataSetChanged();
    }
}
