package com.example.emiproject_androidnoteapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.emiproject_androidnoteapp.models.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

    private Context context;
    private List<Image> images;
    private OnImageClickedListener clickedListener;
    private int imageWidth;
    private int screenWidth;
    private RecyclerView recyclerView;

    public ImagesAdapter(Context context, List<Image> images, OnImageClickedListener clickedListener, int screenWidth) {
        this.context = context;
        this.images = images;
        this.clickedListener = clickedListener;
        this.screenWidth = screenWidth;

        calculateImageWidth();
    }

    public void calculateImageWidth() {

        int imageCount = images.size();
        int newWidth;

        // 24 = layout leftPadding + rightPadding
        if (imageCount == 0) {
            return;
        }
        if (imageCount < 4) {
            newWidth = (screenWidth / imageCount) - 16 - (24 / imageCount);
        } else {
            newWidth = (screenWidth / 3) - 16 - (24 / imageCount);
        }

        if (imageWidth != newWidth && recyclerView != null) {
            //recyclerView.getLayoutManager().removeAllViews();
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);
                child.getLayoutParams().width = newWidth;
                child.getLayoutParams().height = getImageHeight();
            }
        }

        imageWidth = newWidth;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(getView());
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Image image = images.get(position);
        ImageView imageView = (ImageView) holder.itemView;

        if (TextUtils.isEmpty(image.getFilePath())) {
            imageView.setBackgroundColor(Color.BLACK);
        } else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(context).load(new File(image.getFilePath()))
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .fit()
                    .centerCrop()
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedListener.onImageClicked(image, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(ImageViewHolder holder) {
        holder.itemView.getLayoutParams().width = imageWidth;
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).getLocalId();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    // create a new ImageView for each item referenced by the Adapter
    public ImageView getView() {

        // if it's not recycled, initialize some attributes
        ImageView imageView = new ImageView(context);
        GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(imageWidth, getImageHeight());

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layoutParams.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(layoutParams);
        /*imageView.setPadding(8, 8, 8, 8);
        imageView.setCropToPadding(true);
        imageView.setBackgroundColor(Color.LTGRAY);*/

        return imageView;
    }

    private int getImageHeight() {
        if (getItemCount() == 1) {
            return 500;
        } else  {
            return 350;
        }
    }

    public void updateDataSet(List<Image> images) {
        this.images = images;
        calculateImageWidth();
        notifyDataSetChanged();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public interface OnImageClickedListener {
        void onImageClicked(Image image, int position);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageViewHolder(View itemView) {
            super(itemView);
        }
    }

}
