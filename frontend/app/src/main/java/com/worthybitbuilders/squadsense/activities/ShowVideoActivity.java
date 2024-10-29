package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityShowVideoBinding;

import java.util.Objects;

public class ShowVideoActivity extends AppCompatActivity {
    private ActivityShowVideoBinding binding;
    private Runnable hideTopBarRunnable;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent getIntent = getIntent();
        String videoPath = getIntent.getStringExtra("videoPath");
        String videoName = getIntent.getStringExtra("videoName");

        binding.tvTitle.setText(videoName);
        binding.btnClose.setOnClickListener(view -> finish());
        binding.btnDownload.setOnClickListener(view -> {

        });

        binding.videoView.setVideoPath(videoPath);
        MediaController mediaController = new MediaController(this) {
            @Override
            public void show() {
                super.show();
                showTopBar();
                hideTopBarDelayed(3000);
            }

            @Override
            public void hide() {
                super.hide();
                handler.removeCallbacks(hideTopBarRunnable);
                binding.topbarController.setVisibility(View.GONE);
            }
        };
        mediaController.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setVideoPath(videoPath);
        binding.videoView.start();

        handler = new Handler();
        hideTopBarRunnable = () -> {
            binding.topbarController.setVisibility(View.GONE);
        };

        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            showTopBar();
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            hideTopBarDelayed(3000); // Auto hide after 3 seconds of inactivity
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void showTopBar() {
        binding.topbarController.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideTopBarRunnable); // Cancel any pending top bar hiding callback
        hideTopBarDelayed(3000); // Schedule top bar hiding after 3 seconds of inactivity
    }

    private void hideTopBarDelayed(int delayMillis) {
        handler.postDelayed(hideTopBarRunnable, delayMillis);
    }
}