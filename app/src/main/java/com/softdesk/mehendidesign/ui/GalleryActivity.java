package com.softdesk.mehendidesign.ui;

import android.os.Bundle;
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

        folderPrefix = getIntent().getStringExtra("CAT_ID");
        String categoryTitle = getIntent().getStringExtra("CAT_TITLE");

        initViews(categoryTitle);
        r2Manager = new R2DataManager(this);

        if (folderPrefix != null && !folderPrefix.isEmpty()) {
            loadImagesFromCloud();
        } else {
            Toast.makeText(this, "Error: Category ID Missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : "Gallery");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void loadImagesFromCloud() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);

        r2Manager.fetchImagesByCategory(folderPrefix, designs -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (designs != null && !designs.isEmpty()) {
                ImageAdapter adapter = new ImageAdapter(GalleryActivity.this, designs, false);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(GalleryActivity.this, "No images found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}