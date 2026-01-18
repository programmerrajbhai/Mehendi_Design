package com.softdesk.mehendidesign.ui;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.softdesk.mehendidesign.data.MockDatabase;
import com.softdesk.mehendidesign.models.DesignItem;
import com.softdesk.mehendidesign.utils.FavoriteManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        favoriteManager = new FavoriteManager(this);

        // UI Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Mehndi Feed");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        recyclerView = findViewById(R.id.homeRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);

        // Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // --- BOTTOM NAV LISTENER ---
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadHomeFeed();
                return true;
            } else if (id == R.id.nav_categories) {
                loadCategories();
                return true;
            } else if (id == R.id.nav_fav) {
                loadFavorites();
                return true;
            } else if (id == R.id.nav_download) { // SAVED TAB
                loadPrivateDownloads(); // 🔥 NO PERMISSION NEEDED
                return true;
            }
            return false;
        });

        // Default Load
        loadHomeFeed();
    }

    // --- HOME FEED ---
    private void loadHomeFeed() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Latest Designs");
        showShimmer();

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new ImageAdapter(this, MockDatabase.getPopularDesigns(), true));
    }

    // --- CATEGORIES ---
    private void loadCategories() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Categories");
        showShimmer();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryAdapter(this, MockDatabase.getCategories()));
    }

    // --- FAVORITES ---
    private void loadFavorites() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("My Favorites");
        showShimmer();

        List<String> favUrls = favoriteManager.getAllFavorites();
        List<DesignItem> favItems = new ArrayList<>();

        for (String url : favUrls) {
            favItems.add(new DesignItem(url, "Favorite Design", 0));
        }
        Collections.reverse(favItems);

        if (favItems.isEmpty()) {
            Toast.makeText(this, "No Favorites Added!", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this, favItems, false));
    }

    // --- DOWNLOADS (PRIVATE IN-APP) ---
    private void loadPrivateDownloads() {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("In-App Saved");
        showShimmer();

        // 1. Locate the Private Folder
        File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SavedDesigns");
        List<DesignItem> downloadedItems = new ArrayList<>();

        // 2. Read Files
        if (folder.exists() && folder.listFiles() != null) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".jpg")) {
                    downloadedItems.add(new DesignItem(file.getAbsolutePath(), "Saved Design", 0));
                }
            }
        }

        Collections.reverse(downloadedItems);

        if (downloadedItems.isEmpty()) {
            Toast.makeText(this, "No saved images yet!", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this, downloadedItems, false));
    }

    private void showShimmer() {
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        new Handler().postDelayed(() -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }, 500);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_drawer_home) {
            // হোম পেজ রিলোড বা টোস্ট
            loadHomeFeed();
            Toast.makeText(this, "Welcome Home! 🏠", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_rate) {
            // রেটিং লজিক (ভবিষ্যতে প্লে স্টোর লিংক দিতে পারেন)
            Toast.makeText(this, "Thanks for Rating! ⭐⭐⭐⭐⭐", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_privacy) {
            // প্রাইভেসি পলিসি
            Toast.makeText(this, "Opening Privacy Policy... 🔒", Toast.LENGTH_SHORT).show();
            // Intent to open browser can be added here

        } else if (id == R.id.nav_report) {
            // রিপোর্ট ইস্যু
            Toast.makeText(this, "Report feature coming soon! ⚠️", Toast.LENGTH_SHORT).show();
        }

        // ড্রয়ার বন্ধ করা
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}