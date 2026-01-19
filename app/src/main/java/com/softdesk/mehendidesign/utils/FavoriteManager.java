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

    public void addFavorite(String imageUrl) {
        Set<String> favs = new HashSet<>(getFavoritesSet());
        favs.add(imageUrl);
        pref.edit().putStringSet(KEY_FAVS, favs).apply();
    }

    public void removeFavorite(String imageUrl) {
        Set<String> favs = new HashSet<>(getFavoritesSet());
        if (favs.contains(imageUrl)) {
            favs.remove(imageUrl);
            pref.edit().putStringSet(KEY_FAVS, favs).apply();
        }
    }

    public boolean isFavorite(String imageUrl) {
        return getFavoritesSet().contains(imageUrl);
    }

    public List<String> getAllFavorites() {
        return new ArrayList<>(getFavoritesSet());
    }

    private Set<String> getFavoritesSet() {
        return pref.getStringSet(KEY_FAVS, new HashSet<>());
    }
}