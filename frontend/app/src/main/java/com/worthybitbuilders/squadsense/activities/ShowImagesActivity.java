package com.worthybitbuilders.squadsense.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.worthybitbuilders.squadsense.adapters.ViewPagerImageAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityShowImagesBinding;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.util.List;
import java.util.Objects;

public class ShowImagesActivity extends AppCompatActivity {
    private ActivityShowImagesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityShowImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent sentIntent = getIntent();
        List<String> imagePaths = sentIntent.getStringArrayListExtra("imagePaths");
        List<String> imageNames = sentIntent.getStringArrayListExtra("imageNames");

        ViewPagerImageAdapter adapter = new ViewPagerImageAdapter(imagePaths, binding.imageViewPager);

        binding.btnClose.setOnClickListener(view -> finish());
        binding.btnDownload.setOnClickListener(view -> {
            int position = binding.imageViewPager.getCurrentItem();
            String imagePath = imagePaths.get(position);
            String imageName = imageNames.get(position);

            Uri fileUri = Uri.parse(imagePath);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(fileUri);
            request.setTitle(imageName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, imageName);
            ToastUtils.showToastSuccess(this, "Started download", Toast.LENGTH_LONG);
            downloadManager.enqueue(request);
        });
        binding.imageViewPager.setAdapter(adapter);
        binding.imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tvTitle.setText(imageNames.get(position));
            }
        });
    }
}