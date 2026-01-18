package com.softdesk.mehendidesign.ui;

import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView; // Standard
import com.google.android.material.navigation.NavigationView;
import com.softdesk.mehendidesign.R;
import com.softdesk.mehendidesign.adapters.CategoryAdapter;
import com.softdesk.mehendidesign.data.MockDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView;
    CategoryAdapter adapter;
    BottomNavigationView bottomNav;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Mehndi Design");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        shimmerFrameLayout = findViewById(R.id.shimmerViewContainer);
        recyclerView = findViewById(R.id.homeRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);

        // --- BOTTOM NAVIGATION CLICK LOGIC ---
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Home Logic
                return true;
            } else if (id == R.id.nav_fav) {
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_download) {
                Toast.makeText(this, "Downloads", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // --- DRAWER ---
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // --- DATA LOADING ---
        shimmerFrameLayout.startShimmer();

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(manager);

        adapter = new CategoryAdapter(this, MockDatabase.getCategories());
        recyclerView.setAdapter(adapter);

        // Fake Delay
        new Handler().postDelayed(() -> {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }, 1500);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}