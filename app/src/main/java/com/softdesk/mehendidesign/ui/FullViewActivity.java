package com.softdesk.mehendidesign.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.utils.FavoriteManager;
import java.io.File;
import java.io.FileOutputStream;

public class FullViewActivity extends AppCompatActivity {

    PhotoView photoView;
    String imageUrl;
    LinearLayout btnDownload, btnShare, btnFav;
    ImageButton btnBack;
    ImageView iconFav, iconDownload;
    TextView textFav, textDownload;

    FavoriteManager favoriteManager;
    boolean isFav = false;
    boolean isAlreadySaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI(); // Fullscreen
        setContentView(R.layout.activity_full_view);

        favoriteManager = new FavoriteManager(this);

        // Init Views
        photoView = findViewById(R.id.photoView);
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
        btnFav = findViewById(R.id.btnFav);
        btnBack = findViewById(R.id.btnBack);

        // Get Icons & Texts inside buttons
        iconFav = (ImageView) ((LinearLayout) btnFav).getChildAt(0);
        textFav = (TextView) ((LinearLayout) btnFav).getChildAt(1);

        iconDownload = (ImageView) ((LinearLayout) btnDownload).getChildAt(0);
        textDownload = (TextView) ((LinearLayout) btnDownload).getChildAt(1);

        // Load Image
        imageUrl = getIntent().getStringExtra("IMAGE_URL");
        Glide.with(this).load(imageUrl).into(photoView);

        // 🔥 Check if Image is Online or Offline
        checkIfOfflineImage();

        checkFavoriteState();

        // --- CLICK LISTENERS ---

        // 1. SAVE BUTTON
        btnDownload.setOnClickListener(v -> {
            if (isAlreadySaved) {
                Toast.makeText(this, "Already Downloaded!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoView.getDrawable() == null) {
                Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
            saveImagePrivate(bitmap);
        });

        // 2. SHARE BUTTON
        btnShare.setOnClickListener(v -> {
            if (photoView.getDrawable() == null) return;
            shareImage(((BitmapDrawable) photoView.getDrawable()).getBitmap());
        });

        // 3. FAV BUTTON
        btnFav.setOnClickListener(v -> {
            if (isFav) {
                favoriteManager.removeFavorite(imageUrl);
                Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            } else {
                favoriteManager.addFavorite(imageUrl);
                Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show();
            }
            checkFavoriteState();
        });

        // 4. BACK
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void checkIfOfflineImage() {
        if (!imageUrl.startsWith("http")) {
            isAlreadySaved = true;

             iconDownload.setImageResource(android.R.drawable.checkbox_on_background);
            textDownload.setText("Saved");
            btnDownload.setAlpha(0.6f);

        }
    }

    private void checkFavoriteState() {
        isFav = favoriteManager.isFavorite(imageUrl);
        if (isFav) {
            iconFav.setImageResource(android.R.drawable.star_big_on);
            iconFav.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
            textFav.setText("Saved");
        } else {
            iconFav.setImageResource(android.R.drawable.star_big_off);
            iconFav.setColorFilter(getResources().getColor(android.R.color.white));
            textFav.setText("Favorite");
        }
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

            // সাথে সাথে বাটন আপডেট করে দিচ্ছি
            isAlreadySaved = true;
            iconDownload.setImageResource(android.R.drawable.checkbox_on_background);
            textDownload.setText("Saved");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show();
        }
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