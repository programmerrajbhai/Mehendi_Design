package com.softdesk.mehendidesign.data;

import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockDatabase {

    // --- 1. CATEGORIES (Cover Images) ---
    public static List<CategoryModel> getCategories() {
        List<CategoryModel> list = new ArrayList<>();

        // Free mehndi design images from Pexels and Pixabay
        list.add(new CategoryModel("1", "Bridal Mehndi", "https://images.pexels.com/photos/1771338/pexels-photo-1771338.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("2", "Finger Mehndi", "https://images.pexels.com/photos/3060467/pexels-photo-3060467.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("3", "Foot Mehndi", "https://images.pexels.com/photos/3060475/pexels-photo-3060475.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("4", "Simple Design", "https://images.pexels.com/photos/3060471/pexels-photo-3060471.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("5", "Arabic Design", "https://images.pexels.com/photos/3060464/pexels-photo-3060464.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("6", "Gol Tikki", "https://images.pexels.com/photos/3060473/pexels-photo-3060473.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("7", "Eid Special", "https://images.pexels.com/photos/3060470/pexels-photo-3060470.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("8", "Kids Mehndi", "https://images.pexels.com/photos/3060466/pexels-photo-3060466.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("9", "Full Hand", "https://images.pexels.com/photos/3060465/pexels-photo-3060465.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("10", "Peacock Style", "https://images.pexels.com/photos/3060469/pexels-photo-3060469.jpeg?auto=compress&cs=tinysrgb&w=600"));

        return list;
    }

    // --- 2. POPULAR DESIGNS (Home Screen Logic - Aggregated & Sorted) ---
    public static List<DesignItem> getPopularDesigns() {
        List<DesignItem> allDesigns = new ArrayList<>();

        // সব ক্যাটাগরি থেকে ডিজাইন নিয়ে এক জায়গায় করা হচ্ছে
        List<CategoryModel> categories = getCategories();
        for (CategoryModel cat : categories) {
            List<DesignItem> items = getDesignsByCategory(cat.getId());
            allDesigns.addAll(items);
        }

        // 🔥 Views Count অনুযায়ী সাজানো (সবচেয়ে বেশি ভিউ সবার উপরে)
        Collections.sort(allDesigns, (item1, item2) -> item2.getViewCount() - item1.getViewCount());

        return allDesigns;
    }

    // --- 3. CATEGORY WISE DESIGNS (Specific Images for Each Category) ---
    public static List<DesignItem> getDesignsByCategory(String categoryId) {
        List<DesignItem> list = new ArrayList<>();

        switch (categoryId) {
            case "1": // Bridal (Heavy, Red, Intricate)
                list.addAll(generateDesigns("Bridal",
                        "https://images.pexels.com/photos/1771338/pexels-photo-1771338.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060465/pexels-photo-3060465.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060474/pexels-photo-3060474.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060468/pexels-photo-3060468.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "2": // Finger (Focus on tips and rings)
                list.addAll(generateDesigns("Finger",
                        "https://images.pexels.com/photos/3060467/pexels-photo-3060467.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060471/pexels-photo-3060471.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060466/pexels-photo-3060466.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060470/pexels-photo-3060470.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "3": // Foot (Legs and Ankles)
                list.addAll(generateDesigns("Foot",
                        "https://images.pexels.com/photos/3060475/pexels-photo-3060475.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060469/pexels-photo-3060469.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060472/pexels-photo-3060472.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "4": // Simple (Minimalist, Easy)
                list.addAll(generateDesigns("Simple",
                        "https://images.pexels.com/photos/3060471/pexels-photo-3060471.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060466/pexels-photo-3060466.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060467/pexels-photo-3060467.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "5": // Arabic (Flowing, Floral, Bold)
                list.addAll(generateDesigns("Arabic",
                        "https://images.pexels.com/photos/3060464/pexels-photo-3060464.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060465/pexels-photo-3060465.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/1771338/pexels-photo-1771338.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "6": // Gol Tikki (Round Mandala)
                list.addAll(generateDesigns("Gol Tikki",
                        "https://images.pexels.com/photos/3060473/pexels-photo-3060473.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060468/pexels-photo-3060468.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060464/pexels-photo-3060464.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "7": // Eid Special (Festive)
                list.addAll(generateDesigns("Eid",
                        "https://images.pexels.com/photos/3060470/pexels-photo-3060470.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060469/pexels-photo-3060469.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060465/pexels-photo-3060465.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "8": // Kids (Small hands, cute)
                list.addAll(generateDesigns("Kids",
                        "https://images.pexels.com/photos/3060466/pexels-photo-3060466.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060467/pexels-photo-3060467.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060471/pexels-photo-3060471.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "9": // Full Hand (Elbow length)
                list.addAll(generateDesigns("Full Hand",
                        "https://images.pexels.com/photos/3060465/pexels-photo-3060465.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060474/pexels-photo-3060474.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/1771338/pexels-photo-1771338.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            case "10": // Peacock (Intricate, Detailed)
                list.addAll(generateDesigns("Peacock",
                        "https://images.pexels.com/photos/3060469/pexels-photo-3060469.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060468/pexels-photo-3060468.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060473/pexels-photo-3060473.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;

            default:
                // Fallback (Rare case)
                list.addAll(generateDesigns("Modern",
                        "https://images.pexels.com/photos/3060464/pexels-photo-3060464.jpeg?auto=compress&cs=tinysrgb&w=600",
                        "https://images.pexels.com/photos/3060471/pexels-photo-3060471.jpeg?auto=compress&cs=tinysrgb&w=600"));
                break;
        }

        return list;
    }

    // --- HELPER: Generate Real Names & Random Views ---
    private static List<DesignItem> generateDesigns(String type, String... urls) {
        List<DesignItem> designs = new ArrayList<>();
        Random r = new Random();

        String[] styleNames = {
                "Pattern Art", "Royal Look", "Geometric", "Classic Vines",
                "Elegant Touch", "Party Special", "Simple Leaf", "Heavy Detail",
                "Floral Magic", "Traditional", "Modern Twist", "Festive Glory",
                "Delicate Lines", "Bold Patterns", "Minimal Beauty", "Intricate Art"
        };

        // প্রতিটি ক্যাটাগরিতে ২০টি করে ডিজাইন তৈরি হবে
        for (int i = 0; i < 20; i++) {
            String url = urls[i % urls.length];
            String name = type + " " + styleNames[r.nextInt(styleNames.length)] + " " + (i + 1);

            // 🔥 ভিউ কাউন্ট জেনারেট হচ্ছে (১০০০ থেকে ৫০,০০০ এর মধ্যে)
            int views = 1000 + r.nextInt(49000);

            designs.add(new DesignItem(url, name, views));
        }
        return designs;
    }

}