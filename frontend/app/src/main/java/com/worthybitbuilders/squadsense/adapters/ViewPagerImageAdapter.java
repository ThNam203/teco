package com.worthybitbuilders.squadsense.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.worthybitbuilders.squadsense.R;

import java.util.List;

public class ViewPagerImageAdapter extends RecyclerView.Adapter<ViewPagerImageAdapter.ImageForViewPagerViewHolder> {
    private List<String> imagePaths;
    private ViewPager2 viewPager;
    public ViewPagerImageAdapter(List<String> imagePaths, ViewPager2 viewPager) {
        this.imagePaths = imagePaths;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public ImageForViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_pager_view, parent, false);
        return new ImageForViewPagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageForViewPagerViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(holder.imageView)
                .load(imagePath)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    protected static class ImageForViewPagerViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView imageView;

        public ImageForViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoView);
        }
    }
}
