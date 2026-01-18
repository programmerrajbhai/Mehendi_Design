package com.softdesk.mehendidesign.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.softdesk.mehendidesign.R;
import java.io.OutputStream;
import java.util.Objects;

public class FullViewActivity extends AppCompatActivity {

    PhotoView photoView;
    String imageUrl;
    Button btnDownload, btnShare, btnFav;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        // Hide Status Bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        photoView = findViewById(R.id.photoView);
        btnDownload = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
        btnFav = findViewById(R.id.btnFav);
        btnBack = findViewById(R.id.btnBack);

        imageUrl = getIntent().getStringExtra("IMAGE_URL");

        // Load Image
        Glide.with(this).load(imageUrl).into(photoView);

        // --- BUTTON ACTIONS ---

        // 1. Download/Save Logic
        btnDownload.setOnClickListener(v -> {
            if (photoView.getDrawable() == null) {
                Toast.makeText(this, "Image not loaded yet!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Get Bitmap from PhotoView
            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
            saveImageToGallery(bitmap);
        });

        // 2. Share Logic
        btnShare.setOnClickListener(v -> {
            if (photoView.getDrawable() == null) return;
            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
            shareImage(bitmap);
        });

        // 3. Favorite Logic (Dummy)
        btnFav.setOnClickListener(v ->
                Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show()
        );

        if(btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());
    }

    // --- SAVE IMAGE FUNCTION (MODERN WAY) ---
    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();

            // Image Name: Mehendi_Time.jpg
            String fileName = "Mehendi_" + System.currentTimeMillis() + ".jpg";

            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

            // Path setup for Android 10+ (Q)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MehendiDesign");
            }

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

            // Save Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos).close();

            Toast.makeText(this, "Image Saved to Gallery! ✅", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // --- SHARE IMAGE FUNCTION ---
    private void shareImage(Bitmap bitmap) {
        try {
            // Save temporarily to share
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Shared Image", null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing image", Toast.LENGTH_SHORT).show();
        }
    }
}