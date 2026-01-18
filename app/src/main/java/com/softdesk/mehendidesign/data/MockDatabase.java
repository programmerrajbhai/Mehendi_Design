package com.softdesk.mehendidesign.data;

import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockDatabase {

    // Data Store List
    private static final List<CategoryData> DATA_STORE = new ArrayList<>();

    // ==========================================
    // 🔥 DATA ENTRY SECTION (Edit Here) 🔥
    // ==========================================
    static {

        // --- CATEGORY 1: BRIDAL ---
        addCategory(
                "1",                // ID
                "Bridal Mehndi",    // Name
                "https://images.unsplash.com/photo-1598367910900-51b63eb7826d?w=600", // Cover Image

                // 👇 Ekhane (URL, Views) format a data din
                item("https://images.unsplash.com/photo-1598367910900-51b63eb7826d?w=800", 15200),
                item("https://images.unsplash.com/photo-1616091093761-0d33e8b0df9e?w=800", 12500),
                item("https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?w=800", 9800),
                item("https://images.unsplash.com/photo-1560706240-6216091c0e0b?w=800", 8400)
        );

        // --- CATEGORY 2: ARABIC ---
        addCategory(
                "2",
                "Arabic Design",
                "https://images.unsplash.com/photo-1572952453880-99c0d3062331?w=600",

                item("https://images.unsplash.com/photo-1572952453880-99c0d3062331?w=800", 22000),
                item("https://images.unsplash.com/photo-1550604533-339899149726?w=800", 18500),
                item("https://images.pexels.com/photos/15478494/pexels-photo-15478494.jpeg?auto=compress&cs=tinysrgb&w=800", 16200)
        );

        // --- CATEGORY 3: SIMPLE ---
        addCategory(
                "3",
                "Simple Mehndi",
                "https://images.unsplash.com/photo-1522935245844-3d5f0b4d4554?w=600",

                item("https://images.unsplash.com/photo-1522935245844-3d5f0b4d4554?w=800", 5600),
                item("https://images.unsplash.com/photo-1533228876829-65c9479848e3?w=800", 4300),
                item("https://images.pexels.com/photos/5706269/pexels-photo-5706269.jpeg?auto=compress&cs=tinysrgb&w=800", 3200),
                item("https://images.pexels.com/photos/5706270/pexels-photo-5706270.jpeg?auto=compress&cs=tinysrgb&w=800", 2100)
        );

        // --- CATEGORY 4: KIDS ---
        addCategory(
                "4",
                "Kids Special",
                "https://images.unsplash.com/photo-1601306342680-6927d6c813a2?w=600",

                item("https://images.unsplash.com/photo-1601306342680-6927d6c813a2?w=800", 7500),
                item("https://images.pexels.com/photos/13761081/pexels-photo-13761081.jpeg?auto=compress&cs=tinysrgb&w=800", 6200)
        );
    }

    // ==========================================
    // ⚠️ DO NOT TOUCH BELOW CODE (LOGIC) ⚠️
    // ==========================================

    // 1. Get All Categories
    public static List<CategoryModel> getCategories() {
        List<CategoryModel> list = new ArrayList<>();
        for (CategoryData data : DATA_STORE) {
            list.add(new CategoryModel(data.id, data.title, data.coverUrl));
        }
        return list;
    }

    // 2. Get Designs for specific Category
    public static List<DesignItem> getDesignsByCategory(String categoryId) {
        List<DesignItem> designs = new ArrayList<>();

        for (CategoryData data : DATA_STORE) {
            if (data.id.equals(categoryId)) {
                for (DesignEntry entry : data.entries) {
                    // Create Item with fixed views
                    String name = data.title + " Style";
                    designs.add(new DesignItem(entry.url, name, entry.views));
                }
                break;
            }
        }
        return designs;
    }

    // 3. Get Popular Designs (Mix of all, sorted by views)
    public static List<DesignItem> getPopularDesigns() {
        List<DesignItem> allDesigns = new ArrayList<>();
        List<CategoryModel> cats = getCategories();

        for (CategoryModel cat : cats) {
            allDesigns.addAll(getDesignsByCategory(cat.getId()));
        }
        // Highest views first
        Collections.sort(allDesigns, (item1, item2) -> item2.getViewCount() - item1.getViewCount());
        return allDesigns;
    }

    // --- Helper Method to Add Category ---
    private static void addCategory(String id, String title, String coverUrl, DesignEntry... entries) {
        DATA_STORE.add(new CategoryData(id, title, coverUrl, entries));
    }

    // --- Helper Method to Create Item (Clean Syntax) ---
    private static DesignEntry item(String url, int views) {
        return new DesignEntry(url, views);
    }

    // --- Internal Data Classes ---
    private static class CategoryData {
        String id, title, coverUrl;
        DesignEntry[] entries;

        public CategoryData(String id, String title, String coverUrl, DesignEntry[] entries) {
            this.id = id;
            this.title = title;
            this.coverUrl = coverUrl;
            this.entries = entries;
        }
    }

    private static class DesignEntry {
        String url;
        int views;

        public DesignEntry(String url, int views) {
            this.url = url;
            this.views = views;
        }
    }
}