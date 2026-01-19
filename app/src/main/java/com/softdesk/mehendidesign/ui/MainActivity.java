package com.softdesk.mehendidesign.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import com.softdesk.mehendidesign.models.CategoryModel;
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
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r2Manager = new R2DataManager(this);
        favoriteManager = new FavoriteManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        recyclerView = findViewById(R.id.homeRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadHomeFeed();
                return true;
            } else if (id == R.id.nav_categories) { // 🔥 ফিক্সড
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

    private void loadHomeFeed() {
        if (toolbarTitle != null) toolbarTitle.setText("Latest Designs");
        startLoading();

        r2Manager.fetchAllDesigns(new R2DataManager.DataCallback<List<DesignItem>>() {
            @Override
            public void onResult(List<DesignItem> designs) {
                stopLoading();
                if (designs != null && !designs.isEmpty()) {
                    StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setAdapter(new ImageAdapter(MainActivity.this, designs, true));
                } else {
                    Toast.makeText(MainActivity.this, "No designs found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCategoriesFromR2() {
        if (toolbarTitle != null) toolbarTitle.setText("Categories");
        startLoading();

        r2Manager.fetchCategories(new R2DataManager.DataCallback<List<CategoryModel>>() {
            @Override
            public void onResult(List<CategoryModel> categories) {
                stopLoading();
                if (categories != null && !categories.isEmpty()) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(new CategoryAdapter(MainActivity.this, categories));
                } else {
                    Toast.makeText(MainActivity.this, "No Categories Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadFavorites() {
        if (toolbarTitle != null) toolbarTitle.setText("My Favorites");
        recyclerView.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);

        List<String> favUrls = favoriteManager.getAllFavorites();
        List<DesignItem> favItems = new ArrayList<>();

        for (String url : favUrls) {
            favItems.add(new DesignItem(url, "Favorite", 0));
        }

        Collections.reverse(favItems);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this, favItems, false));

        if(favItems.isEmpty()){
            Toast.makeText(this, "No Favorites Added Yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPrivateDownloads() {
        if (toolbarTitle != null) toolbarTitle.setText("Saved");
        recyclerView.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);

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

        if(downloadedItems.isEmpty()){
            Toast.makeText(this, "No Downloads Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoading() {
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
    }

    private void stopLoading() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadHomeFeed();
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else if (id == R.id.nav_categories) { // 🔥 ফিক্সড
            loadCategoriesFromR2();
            bottomNav.setSelectedItemId(R.id.nav_categories);
        } else if (id == R.id.nav_fav) {
            loadFavorites(); // 🔥 এখানে লোড ফেভারিট কল করা হলো
            bottomNav.setSelectedItemId(R.id.nav_fav);
        } else if (id == R.id.nav_download) { // 🔥 নাম ফিক্সড: nav_download
            loadPrivateDownloads();
            bottomNav.setSelectedItemId(R.id.nav_download);
        } else if (id == R.id.nav_rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.nav_privacy) {
            Toast.makeText(this, "Privacy Policy Coming Soon", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}