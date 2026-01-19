package com.softdesk.mehendidesign.models;

public class CategoryModel {
    private String id;
    private String name; // 'title' পরিবর্তন করে 'name' করা হয়েছে
    private String imageUrl; // এটি এখন R2 এর ফুল URL ধারণ করবে

    // কনস্ট্রাক্টর
    public CategoryModel(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // গেটার মেথড (Getters)
    public String getId() {
        return id;
    }

    // R2DataManager-এ সর্টিং এর জন্য এই মেথডটি জরুরি
    public String getName() {
        return name;
    }

    // যদি আপনার Adapter-এ getTitle() ব্যবহার করা থাকে, তবে এটি এরর আটকাতে সাহায্য করবে
    public String getTitle() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}