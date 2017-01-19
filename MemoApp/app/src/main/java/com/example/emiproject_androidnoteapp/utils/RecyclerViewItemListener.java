package com.example.emiproject_androidnoteapp.utils;

import android.view.View;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public interface RecyclerViewItemListener {

    void onItemClicked(View callerView, int position);

    boolean onItemLongClick(View callerView, int position);
}
