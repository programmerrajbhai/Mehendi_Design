package com.softdesk.mehendidesign.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.adapters.ImageAdapter;
import com.softdesk.mehendidesign.data.MockDatabase;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageAdapter adapter;
    String categoryId, categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // Get Data from Intent
        categoryId = getIntent().getStringExtra("CAT_ID");
        categoryTitle = getIntent().getStringExtra("CAT_TITLE");

        // Setup Toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Back button
        }

        recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 Column Grid

        // Load Designs with Animation
        adapter = new ImageAdapter(this, MockDatabase.getDesignsByCategory(categoryId));
        recyclerView.setAdapter(adapter);
    }

    // Back Button Click Animation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right); // Back animation
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Hardware Back Button Animation
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}