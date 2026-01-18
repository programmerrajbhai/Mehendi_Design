package com.softdesk.mehendidesign.data;

import com.softdesk.mehendidesign.models.CategoryModel;
import java.util.ArrayList;
import java.util.List;

public class MockDatabase {

    // --- CATEGORY LIST (Home Page) ---
    public static List<CategoryModel> getCategories() {
        List<CategoryModel> list = new ArrayList<>();

        // সব লিংক Wikimedia Commons এর ডাইরেক্ট ফাইল পাথ (100% Working)
        list.add(new CategoryModel("1", "Bridal Mehndi", "https://commons.wikimedia.org/wiki/Special:FilePath/Bridal_Mehndi_Design.jpg"));
        list.add(new CategoryModel("2", "Finger Mehndi", "https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_designs_attractive_simple.jpg"));
        list.add(new CategoryModel("3", "Foot Mehndi", "https://commons.wikimedia.org/wiki/Special:FilePath/Mehndi.jpg"));
        list.add(new CategoryModel("4", "Simple Design", "https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_design.jpg"));
        list.add(new CategoryModel("5", "Arabic Design", "https://commons.wikimedia.org/wiki/Special:FilePath/Mehendi_Designs.jpg"));

        return list;
    }


    public static List<String> getDesignsByCategory(String categoryId) {
        List<String> images = new ArrayList<>();

        switch (categoryId) {
            case "1": // Bridal Collection
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Bridal_Mehndi_Design.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Full_hand_Mehendi_design.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehendi_Designs.jpg");
                break;

            case "2": // Finger/Simple Collection
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_designs_attractive_simple.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Moderate_Mehndi_Design.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_design.jpg");
                break;

            case "3": // Foot Collection
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehndi.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Bridal_Mehndi_Design.jpg"); // Demo for now
                break;

            case "4": // Simple
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_design.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehandi_designs_attractive_simple.jpg");
                break;

            default: // Others
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Mehendi_Designs.jpg");
                images.add("https://commons.wikimedia.org/wiki/Special:FilePath/Full_hand_Mehendi_design.jpg");
                break;
        }
        return images;
    }
}