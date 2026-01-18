package com.softdesk.mehendidesign.models;

public class CategoryModel {
    String id;
    String title;
    String imageUrl; // Server hole URL hobe, ekhon drawable name nibo

    public CategoryModel(String id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }

    public String getId() {
        return id;
    }
}