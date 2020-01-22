package pestone.wallpaperfootbal.Utility;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


// Украшательсва RecyclerView
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Позиция элемента
        int column = position % spanCount; // Номер колонки

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) { // Верхний край
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // Нижний элемент
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // Верхний элемент
            }
        }
    }
}

