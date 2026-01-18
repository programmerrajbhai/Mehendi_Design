package com.softdesk.mehendidesign.data;

import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MockDatabase {

    // --- 1. CATEGORIES (All 10 Categories) ---
    public static List<CategoryModel> getCategories() {
        List<CategoryModel> list = new ArrayList<>();

        list.add(new CategoryModel("1", "Bridal Mehndi", "https://images.unsplash.com/photo-1598367910900-51b63eb7826d?w=600"));
        list.add(new CategoryModel("2", "Finger Mehndi", "https://images.pexels.com/photos/14566127/pexels-photo-14566127.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("3", "Foot Mehndi", "https://images.pexels.com/photos/14956891/pexels-photo-14956891.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("4", "Simple Design", "https://images.pexels.com/photos/5706269/pexels-photo-5706269.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("5", "Arabic Design", "https://images.pexels.com/photos/15478494/pexels-photo-15478494.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("6", "Gol Tikki", "https://images.pexels.com/photos/11385465/pexels-photo-11385465.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("7", "Eid Special", "https://images.unsplash.com/photo-1616091093761-0d33e8b0df9e?w=600"));
        list.add(new CategoryModel("8", "Kids Mehndi", "https://images.pexels.com/photos/13761081/pexels-photo-13761081.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("9", "Full Hand", "https://images.pexels.com/photos/9980838/pexels-photo-9980838.jpeg?auto=compress&cs=tinysrgb&w=600"));
        list.add(new CategoryModel("10", "Peacock Style", "https://images.pexels.com/photos/12496293/pexels-photo-12496293.jpeg?auto=compress&cs=tinysrgb&w=600"));

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

        // 🔥 Views Count অনুযায়ী সাজানো (সবচেয়ে বেশি ভিউ সবার উপরে)
        Collections.sort(allDesigns, (item1, item2) -> item2.getViewCount() - item1.getViewCount());

        return allDesigns;
    }

    // --- 3. CATEGORY WISE DESIGNS (Specific Images for Each Category) ---
    public static List<DesignItem> getDesignsByCategory(String categoryId) {
        List<DesignItem> list = new ArrayList<>();

        switch (categoryId) {
            case "1": // Bridal (Heavy, Red, Intricate)
                list.addAll(generateDesigns("Bridal",
                        "https://images.unsplash.com/photo-1598367910900-51b63eb7826d?w=800", // Red Hands
                        "https://images.unsplash.com/photo-1563200959-19799298718a?w=800", // Full Arm
                        "https://images.pexels.com/photos/9980838/pexels-photo-9980838.jpeg?auto=compress&cs=tinysrgb&w=800", // Intricate
                        "https://images.pexels.com/photos/10153205/pexels-photo-10153205.jpeg?auto=compress&cs=tinysrgb&w=800")); // Wedding
                break;

            case "2": // Finger (Focus on tips and rings)
                list.addAll(generateDesigns("Finger",
                        "https://images.pexels.com/photos/14566127/pexels-photo-14566127.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/14566129/pexels-photo-14566129.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/3094218/pexels-photo-3094218.jpeg?auto=compress&cs=tinysrgb&w=800"));
                break;

            case "3": // Foot (Legs and Ankles)
                list.addAll(generateDesigns("Foot",
                        "https://images.pexels.com/photos/14956891/pexels-photo-14956891.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/14956895/pexels-photo-14956895.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/12496293/pexels-photo-12496293.jpeg?auto=compress&cs=tinysrgb&w=800"));
                break;

            case "4": // Simple (Minimalist, Easy)
                list.addAll(generateDesigns("Simple",
                        "https://images.pexels.com/photos/5706269/pexels-photo-5706269.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/5706270/pexels-photo-5706270.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.unsplash.com/photo-1522935245844-3d5f0b4d4554?w=800"));
                break;

            case "5": // Arabic (Flowing, Floral, Bold)
                list.addAll(generateDesigns("Arabic",
                        "https://images.pexels.com/photos/15478494/pexels-photo-15478494.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/15478497/pexels-photo-15478497.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.unsplash.com/photo-1616091093761-0d33e8b0df9e?w=800"));
                break;

            case "6": // Gol Tikki (Round Mandala)
                list.addAll(generateDesigns("Gol Tikki",
                        "https://images.pexels.com/photos/11385465/pexels-photo-11385465.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/9980843/pexels-photo-9980843.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://upload.wikimedia.org/wikipedia/commons/e/e6/Mehndi_design_on_hand.jpg"));
                break;

            case "7": // Eid Special (Festive)
                list.addAll(generateDesigns("Eid",
                        "https://images.unsplash.com/photo-1616091093761-0d33e8b0df9e?w=800",
                        "https://images.pexels.com/photos/12496296/pexels-photo-12496296.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/15777718/pexels-photo-15777718.jpeg?auto=compress&cs=tinysrgb&w=800"));
                break;

            case "8": // Kids (Small hands)
                list.addAll(generateDesigns("Kids",
                        "https://images.pexels.com/photos/13761081/pexels-photo-13761081.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/13761082/pexels-photo-13761082.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.unsplash.com/photo-1522935245844-3d5f0b4d4554?w=800"));
                break;

            case "9": // Full Hand (Elbow length)
                list.addAll(generateDesigns("Full Hand",
                        "https://images.pexels.com/photos/9980838/pexels-photo-9980838.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.unsplash.com/photo-1563200959-19799298718a?w=800",
                        "https://images.pexels.com/photos/10153205/pexels-photo-10153205.jpeg?auto=compress&cs=tinysrgb&w=800"));
                break;

            case "10": // Peacock (Intricate, Detailed)
                list.addAll(generateDesigns("Peacock",
                        "https://images.pexels.com/photos/12496293/pexels-photo-12496293.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/5997970/pexels-photo-5997970.jpeg?auto=compress&cs=tinysrgb&w=800",
                        "https://images.pexels.com/photos/7249339/pexels-photo-7249339.jpeg?auto=compress&cs=tinysrgb&w=800"));
                break;

            default:
                // Fallback (Rare case)
                list.addAll(generateDesigns("Modern",
                        "https://images.pexels.com/photos/3094218/pexels-photo-3094218.jpeg?auto=compress&cs=tinysrgb&w=800"));
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
                "Elegant Touch", "Party Special", "Simple Leaf", "Heavy Detail"
        };

        // প্রতিটি ক্যাটাগরিতে ২০টি করে ডিজাইন তৈরি হবে
        for (int i = 0; i < 20; i++) {
            String url = urls[i % urls.length];
            String name = type + " " + styleNames[r.nextInt(styleNames.length)];

            // 🔥 ভিউ কাউন্ট জেনারেট হচ্ছে (১০০০ থেকে ৫০,০০০ এর মধ্যে)
            int views = 1000 + r.nextInt(49000);

            designs.add(new DesignItem(url, name, views));
        }
        return designs;
    }
}