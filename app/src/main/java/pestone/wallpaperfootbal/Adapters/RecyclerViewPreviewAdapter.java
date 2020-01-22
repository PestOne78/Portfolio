package pestone.wallpaperfootbal.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

import pestone.wallpaperfootbal.Utility.GlideApp;
import pestone.wallpaperfootbal.R;
import pestone.wallpaperfootbal.Structure.Wallpaper;

public class RecyclerViewPreviewAdapter extends RecyclerView.Adapter<RecyclerViewPreviewAdapter.ViewHolder> {


    // интерфейс / нажатие на элемент RecyclerView
    public interface OnItemClickListener {
        void OnItemClick(Wallpaper item);
    }

    private List<Wallpaper> wallpaperList;
    private Context mContext;
    private final OnItemClickListener listener;

    // конструктор
    public RecyclerViewPreviewAdapter(List<Wallpaper> wallpaperList, Context mContext, OnItemClickListener listener) {
        this.wallpaperList = wallpaperList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, null);
        return new ViewHolder(v);
    }

    // Привязка данных к графике
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewPreviewAdapter.ViewHolder holder, int position) {
        Wallpaper model = wallpaperList.get(position);
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference().child(model.getUri_key());
        GlideApp.with(Objects.requireNonNull(mContext)).load(storageRef).override(512).into(holder.mImage);

        //нажатие на элемент RecyclerView
        holder.bind(wallpaperList.get(position), listener);
    }

    // размер списка
    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;

        // Графика
        private ViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.image_preview);
        }

        // нажатие на элемент RecyclerView
        private void bind(final Wallpaper item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(item);
                }
            });
        }
    }
}
