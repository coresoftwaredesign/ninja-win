package com.coresoftwaredesign.ninjawin;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdView;

public class MainActivity extends AppCompatActivity {
    private final MediaPlayer[] mediaPlayer = new MediaPlayer[2];
    private int currentPlayer = 0;
    private FrameLayout adContainerView;
    private AdManagerAdView adView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!BuildConfig.ENABLE_TEST_BUILD) {
            configureAds();
        }

        ImageButton imageButton = findViewById(R.id.button_air_horn);

        imageButton.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (mediaPlayer[currentPlayer].isPlaying()) {
                    mediaPlayer[currentPlayer].seekTo(0);
                } else {
                    mediaPlayer[currentPlayer].start();
                }
                currentPlayer = (currentPlayer + 1) % 2;
            }
            return false;
        });
    }

    private void configureAds() {
        MobileAds.initialize(this);
        adContainerView = findViewById(R.id.ad_view_container);

        adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        AdUtil addUtil = new AdUtil();
                        AdSize adSize = addUtil.getAdSize(adContainerView);
                        adView = addUtil.loadAdaptiveBanner(adContainerView, adSize);
                        adContainerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        mediaPlayer[0] = MediaPlayer.create(this, R.raw.ninja_win);
        mediaPlayer[1] = MediaPlayer.create(this, R.raw.ninja_win);
    }

    private void pauseMediaPlayer(int i) {
        if (mediaPlayer[i] != null) {
            if (mediaPlayer[i].isPlaying()) {
                mediaPlayer[i].stop();
            }
            mediaPlayer[i].release();
            mediaPlayer[i] = null;
        }
    }

    @Override
    protected void onPause() {
        pauseMediaPlayer(0);
        pauseMediaPlayer(1);

        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
            adContainerView.removeAllViews();
            adView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // workaround for library memory leak
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && this.isTaskRoot()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

}
