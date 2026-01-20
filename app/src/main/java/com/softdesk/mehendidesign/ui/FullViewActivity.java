package com.softdesk.mehendidesign.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.adapters.FullViewAdapter;
import com.softdesk.mehendidesign.models.DesignItem;
import com.softdesk.mehendidesign.utils.FavoriteManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FullViewActivity extends AppCompatActivity {

    public static List<DesignItem> sDesignList = new ArrayList<>();

    ViewPager2 viewPager;
    LinearLayout btnDownload, btnShare, btnFav;
    ImageButton btnBack;
    ImageView iconFav, iconDownload;
    TextView textFav, textDownload;

    FavoriteManager favoriteManager;
    DesignItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_full_view);

        favoriteManager = new FavoriteManager(this);

        // View Init
        viewPager = findViewById(R.id.viewPager);
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
        btnFav = findViewById(R.id.btnFav);
        btnBack = findViewById(R.id.btnBack);

        // Icons & Text
        iconFav = (ImageView) ((LinearLayout) btnFav).getChildAt(0);
        textFav = (TextView) ((LinearLayout) btnFav).getChildAt(1);
        iconDownload = (ImageView) ((LinearLayout) btnDownload).getChildAt(0);
        textDownload = (TextView) ((LinearLayout) btnDownload).getChildAt(1);

        int position = getIntent().getIntExtra("POSITION", 0);

        if (sDesignList != null && !sDesignList.isEmpty()) {
            FullViewAdapter adapter = new FullViewAdapter(this, sDesignList);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position, false); // ক্লিক করা ইমেজে জাম্প করা

            onPageChanged(position);
        } else {
            Toast.makeText(this, "Error loading images", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                onPageChanged(position);
            }
        });


        btnDownload.setOnClickListener(v -> {
            if (currentItem == null) return;
            if (isOfflineImage(currentItem.getImageUrl())) {
                Toast.makeText(this, "Already Downloaded!", Toast.LENGTH_SHORT).show();
            } else {
                downloadAndSaveImage(currentItem.getImageUrl());
            }
        });

        btnShare.setOnClickListener(v -> {
            if (currentItem == null) return;
            downloadAndShareImage(currentItem.getImageUrl());
        });

        // Fav Button
        btnFav.setOnClickListener(v -> {
            if (currentItem == null) return;
            String url = currentItem.getImageUrl();

            if (favoriteManager.isFavorite(url)) {
                favoriteManager.removeFavorite(url);
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                favoriteManager.addFavorite(url);
                Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show();
            }
            updateButtonsState();
        });

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void onPageChanged(int position) {
        if (sDesignList != null && position < sDesignList.size()) {
            currentItem = sDesignList.get(position);
            updateButtonsState();
        }
    }

    private void updateButtonsState() {
        if (currentItem == null) return;
        String url = currentItem.getImageUrl();

        if (favoriteManager.isFavorite(url)) {
            iconFav.setImageResource(android.R.drawable.star_big_on);
            iconFav.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
            textFav.setText("Saved");
        } else {
            iconFav.setImageResource(android.R.drawable.star_big_off);
            iconFav.setColorFilter(getResources().getColor(android.R.color.white));
            textFav.setText("Favorite");
        }

        // Check Download
        if (isOfflineImage(url)) {
            iconDownload.setImageResource(android.R.drawable.checkbox_on_background);
            textDownload.setText("Saved");
            btnDownload.setAlpha(0.6f);
        } else {
            iconDownload.setImageResource(android.R.drawable.stat_sys_download);
            textDownload.setText("Save");
            btnDownload.setAlpha(1.0f);
        }
    }

    private boolean isOfflineImage(String url) {
        return !url.startsWith("http");
    }

    private void downloadAndSaveImage(String url) {
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImagePrivate(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    private void saveImagePrivate(Bitmap bitmap) {
        try {
            File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SavedDesigns");
            if (!folder.exists()) folder.mkdirs();

            String fileName = "Mehendi_" + System.currentTimeMillis() + ".jpg";
            File file = new File(folder, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(this, "Saved for Offline! ✅", Toast.LENGTH_SHORT).show();

            iconDownload.setImageResource(android.R.drawable.checkbox_on_background);
            textDownload.setText("Saved");
            btnDownload.setAlpha(0.6f);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
    }
    private void downloadAndShareImage(String url) {
        Toast.makeText(this, "Preparing Share...", Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        shareImage(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    private void shareImage(Bitmap bitmap) {
        try {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Shared Image", null);
            Uri uri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share Image"));
        } catch (Exception e) { }
    }

    private void hideSystemUI() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }
}