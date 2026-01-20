package com.softdesk.mehendidesign.models;

public class DesignItem {
    String imageUrl;
    String categoryName;
    int viewCount;

    public DesignItem(String imageUrl, String categoryName, int viewCount) {
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.viewCount = viewCount;
    }

    public String getImageUrl() { return imageUrl; }
    public String getCategoryName() { return categoryName; }
    public int getViewCount() { return viewCount; }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
}