package com.softdesk.mehendidesign.ui;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.adapters.CategoryAdapter;
import com.softdesk.mehendidesign.adapters.ImageAdapter;
import com.softdesk.mehendidesign.models.DesignItem;
import com.softdesk.mehendidesign.utils.FavoriteManager;
import com.softdesk.mehendidesign.utils.R2DataManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView;
    BottomNavigationView bottomNav;
    ShimmerFrameLayout shimmerFrameLayout;
    FavoriteManager favoriteManager;
    R2DataManager r2Manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r2Manager = new R2DataManager(this);
        favoriteManager = new FavoriteManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Popular Designs");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Shimmer Layout Find
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);

        recyclerView = findViewById(R.id.homeRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadHomeFeed();
                return true;
            } else if (id == R.id.nav_categories) {
                loadCategoriesFromR2();
                return true;
            } else if (id == R.id.nav_fav) {
                loadFavorites();
                return true;
            } else if (id == R.id.nav_download) {
                loadPrivateDownloads();
                return true;
            }
            return false;
        });

        loadHomeFeed();
    }

    // --- 1. POPULAR / HOME FEED ---
    private void loadHomeFeed() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Popular Designs");

        // লোডিং শুরু (Shimmer On)
        startLoading();

        r2Manager.fetchAllDesigns(designs -> {
            // ডাটা আসার পর লোডিং বন্ধ (Shimmer Off)
            stopLoading();

            if (designs != null && !designs.isEmpty()) {
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(new ImageAdapter(MainActivity.this, designs, true));
            } else {
                Toast.makeText(MainActivity.this, "No designs found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- 2. CATEGORIES ---
    private void loadCategoriesFromR2() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Categories");
        startLoading();

        r2Manager.fetchCategories(categories -> {
            stopLoading();
            if (categories != null && !categories.isEmpty()) {
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(new CategoryAdapter(MainActivity.this, categories));
            } else {
                Toast.makeText(MainActivity.this, "No Categories Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavorites() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("My Favorites");
        stopLoading();

        List<String> favUrls = favoriteManager.getAllFavorites();
        List<DesignItem> favItems = new ArrayList<>();
        for (String url : favUrls) { favItems.add(new DesignItem(url, "Favorite", 0)); }
        Collections.reverse(favItems);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this, favItems, false));
    }

    private void loadPrivateDownloads() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Saved");
        stopLoading();

        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SavedDesigns");
        List<DesignItem> downloadedItems = new ArrayList<>();
        if (folder.exists() && folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".jpg")) {
                    downloadedItems.add(new DesignItem(file.getAbsolutePath(), "Saved", 0));
                }
            }
        }
        Collections.reverse(downloadedItems);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this, downloadedItems, false));
    }

    // ✅ সুন্দরভাবে লোডিং হ্যান্ডেল করার মেথড
    private void startLoading() {
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer(); // অ্যানিমেশন শুরু
        }
    }

    private void stopLoading() {
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmer(); // অ্যানিমেশন বন্ধ
            shimmerFrameLayout.setVisibility(View.GONE);
        }
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}