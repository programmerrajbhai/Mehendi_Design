package com.softdesk.mehendidesign.ui;

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

        categoryId = getIntent().getStringExtra("CAT_ID");
        categoryTitle = getIntent().getStringExtra("CAT_TITLE");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.homeRecyclerView);

        // 🔥 UPDATE: Span Count 2 (আপনার রিকোয়েস্ট: এক লাইনে ২টি)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Pass 'false' for Square Layout
        adapter = new ImageAdapter(this, MockDatabase.getDesignsByCategory(categoryId), false);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}