package com.example.emiproject_androidnoteapp.utils;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    protected int orientation;
    boolean firstItemOnly;

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public SpaceItemDecoration(int space, int orientation, boolean firstItemOnly) {
        this.space = space;
        this.orientation = orientation;
        this.firstItemOnly = firstItemOnly;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int childPosition = parent.getChildAdapterPosition(view);
        if (firstItemOnly) {
            if (isFirstItem(childPosition) || isLastItem(childPosition, parent)) {
                addOffset(outRect, childPosition);
            }
        } else {
            addOffset(outRect, childPosition);
        }
    }

    protected void addOffset(Rect outRect, int childPosition) {

        if (orientation == HORIZONTAL_LIST) {
            outRect.left = space;
            outRect.right = space;
        } else {
            outRect.bottom += space;
            // Add top margin only for the first item to avoid double space between items
            if (isFirstItem(childPosition))
                outRect.top += space;
        }

    }

    protected boolean isFirstItem(int childPosition) {
        return childPosition == 0;
    }

    protected boolean isLastItem(int childPosition, RecyclerView parent) {
        return childPosition == parent.getAdapter().getItemCount() - 1;
    }
}
