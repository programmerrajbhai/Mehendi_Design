package com.softdesk.mehendidesign.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.adapters.ImageAdapter;
import com.softdesk.mehendidesign.utils.R2DataManager;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private R2DataManager r2Manager;
    private String folderPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // ইনটেন্ট থেকে ডাটা নেওয়া
        folderPrefix = getIntent().getStringExtra("CAT_ID");
        String categoryTitle = getIntent().getStringExtra("CAT_TITLE");

        initViews(categoryTitle);
        r2Manager = new R2DataManager(this);

        if (folderPrefix != null && !folderPrefix.isEmpty()) {
            loadImagesFromCloud();
        } else {
            Toast.makeText(this, "Error: Category Not Found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // টাইটেল থেকে স্ল্যাশ (/) সরিয়ে সুন্দর করে দেখানো
            String cleanTitle = (title != null) ? title.replace("/", "") : "Gallery";
            getSupportActionBar().setTitle(cleanTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        recyclerView = findViewById(R.id.homeRecyclerView);

        // ৩ কলামের গ্রিড লেআউট (সুন্দর দেখাবে)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void loadImagesFromCloud() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);

        r2Manager.fetchImagesByCategory(folderPrefix, designs -> {
            // লোডিং বন্ধ
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (designs != null && !designs.isEmpty()) {
                // Adapter সেট করা
                ImageAdapter adapter = new ImageAdapter(GalleryActivity.this, designs, false);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(GalleryActivity.this, "No images found in this category.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // ব্যাক বাটন কাজ করবে
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}