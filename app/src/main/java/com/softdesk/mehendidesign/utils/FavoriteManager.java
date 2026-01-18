package com.softdesk.mehendidesign.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteManager {

    private static final String PREF_NAME = "MehendiFavs";
    private static final String KEY_FAVS = "favorite_urls";

    private SharedPreferences pref;

    public FavoriteManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- ফেভারিট যোগ করা ---
    public void addFavorite(String imageUrl) {
        // 🔥 FIX: পুরনো সেট থেকে নতুন HashSet তৈরি করা হচ্ছে
        // এটি না করলে ডাটা ওভাররাইট হয়ে যায় এবং শুধু ১টা সেভ থাকে
        Set<String> favs = new HashSet<>(getFavoritesSet());

        favs.add(imageUrl);
        pref.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    // --- ফেভারিট রিমুভ করা ---
    public void removeFavorite(String imageUrl) {
        // 🔥 FIX: এখানেও নতুন HashSet তৈরি করে রিমুভ করা হচ্ছে
        Set<String> favs = new HashSet<>(getFavoritesSet());

        if (favs.contains(imageUrl)) {
            favs.remove(imageUrl);
            pref.edit().putStringSet(KEY_FAVS, favs).apply();
        }
    }

    // --- চেক করা ফেভারিট কিনা ---
    public boolean isFavorite(String imageUrl) {
        return getFavoritesSet().contains(imageUrl);
    }

    // --- সব ফেভারিট লিস্ট পাওয়া ---
    public List<String> getAllFavorites() {
        return new ArrayList<>(getFavoritesSet());
    }

    // --- ইন্টারনাল হেল্পার ---
    private Set<String> getFavoritesSet() {
        // ডিফল্ট ভ্যালু হিসেবে একটি খালি সেট রিটার্ন করা হচ্ছে
        return pref.getStringSet(KEY_FAVS, new HashSet<>());
    }
}