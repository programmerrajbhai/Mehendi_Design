package com.softdesk.mehendidesign.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.softdesk.mehendidesign.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_splash);

         ImageView logo = findViewById(R.id.splashLogo);
        ShimmerFrameLayout shimmer = findViewById(R.id.shimmerViewContainer);

         shimmer.startShimmer();

        logo.setAlpha(0f);
        logo.setScaleX(0.5f);
        logo.setScaleY(0.5f);

        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1200)
                .setInterpolator(new OvershootInterpolator()) // বাউন্সি ইফেক্ট
                .start();

        shimmer.setTranslationY(100f);
        shimmer.setAlpha(0f);

        shimmer.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(300)
                .start();

       new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
         overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 3000);
    }
}