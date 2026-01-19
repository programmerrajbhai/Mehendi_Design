package com.softdesk.mehendidesign.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class R2DataManager {

    // ✅ আপনার ক্রেডেনশিয়াল
    private static final String ACCOUNT_ID = "c784b8e571db8c9b498b351a77ba63b4";
    private static final String ACCESS_KEY = "0f68c743da42a2841213ee8dde89f715";
    private static final String SECRET_KEY = "665647c8f26cc669aac4b23e1d22a8483b2559b33f6d6b0ac15dc8d7bbeaa45e";
    private static final String BUCKET_NAME = "mehendidesign";

    private AmazonS3Client s3Client;

    public R2DataManager(Context context) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY.trim(), SECRET_KEY.trim());
            s3Client = new AmazonS3Client(credentials);

            // R2 কনফিগারেশন (Region দেবেন না)
            s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
            s3Client.setEndpoint("https://" + ACCOUNT_ID + ".r2.cloudflarestorage.com");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("R2Data", "Connection Error: " + e.getMessage());
        }
    }

    // ====================================================
    // 🌟 1. POPULAR / ALL DESIGNS (Home Screen এর জন্য)
    // ====================================================
    public void fetchAllDesigns(DataCallback<List<DesignItem>> callback) {
        new AsyncTask<Void, Void, List<DesignItem>>() {
            @Override
            protected List<DesignItem> doInBackground(Void... voids) {
                List<DesignItem> allDesigns = new ArrayList<>();
                try {
                    ListObjectsRequest request = new ListObjectsRequest().withBucketName(BUCKET_NAME);
                    ObjectListing listing;

                    // বাকেটের সব ফাইল লুপ করছি
                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            String lowerKey = key.toLowerCase();
                            boolean isCover = lowerKey.contains("/cover.");

                            // ফোল্ডার এবং কভার বাদে যেকোনো ছবি নিচ্ছি
                            if (key.contains("/") && !isCover && isImageFile(key)) {

                                String fullUrl = getPresignedUrl(key);

                                // নামের জন্য ফোল্ডারের নাম ব্যবহার করছি
                                String[] parts = key.split("/");
                                String name = (parts.length > 0 ? parts[0] : "Mehndi") + " Design";
                                int views = 1500 + (int)(Math.random() * 5000);

                                allDesigns.add(new DesignItem(fullUrl, name, views));
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated());

                    // 🔥 র্যান্ডম মিক্স করা (Popular Feel দেওয়ার জন্য)
                    Collections.shuffle(allDesigns);

                    // অ্যাপ ফাস্ট রাখতে সর্বোচ্চ ১০০টি ছবি রিটার্ন করছি
                    if (allDesigns.size() > 100) {
                        return allDesigns.subList(0, 100);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("R2Data", "All Designs Error: " + e.getMessage());
                }
                return allDesigns;
            }

            @Override
            protected void onPostExecute(List<DesignItem> result) {
                if (callback != null) callback.onResult(result);
            }
        }.execute();
    }

    // ====================================================
    // 📂 2. ক্যাটাগরি লোড করা
    // ====================================================
    public void fetchCategories(DataCallback<List<CategoryModel>> callback) {
        new AsyncTask<Void, Void, List<CategoryModel>>() {
            @Override
            protected List<CategoryModel> doInBackground(Void... voids) {
                Map<String, String> categoryCoverMap = new HashMap<>();
                List<CategoryModel> categories = new ArrayList<>();

                try {
                    ListObjectsRequest request = new ListObjectsRequest().withBucketName(BUCKET_NAME);
                    ObjectListing listing;

                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            if (key.contains("/")) {
                                String[] parts = key.split("/");
                                if (parts.length >= 2) {
                                    String folderName = parts[0];
                                    String fileName = parts[1].toLowerCase();

                                    if (!categoryCoverMap.containsKey(folderName) && isImageFile(key)) {
                                        categoryCoverMap.put(folderName, getPresignedUrl(key));
                                    }
                                    if (fileName.startsWith("cover.") && isImageFile(key)) {
                                        categoryCoverMap.put(folderName, getPresignedUrl(key));
                                    }
                                }
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated());

                    for (Map.Entry<String, String> entry : categoryCoverMap.entrySet()) {
                        String name = entry.getKey();
                        String url = entry.getValue();
                        String id = name + "/";
                        categories.add(new CategoryModel(id, name, url));
                    }

                    Collections.sort(categories, (c1, c2) -> c1.getTitle().compareToIgnoreCase(c2.getTitle()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return categories;
            }

            @Override
            protected void onPostExecute(List<CategoryModel> result) {
                if (callback != null) callback.onResult(result);
            }
        }.execute();
    }

    // ====================================================
    // 🖼️ 3. নির্দিষ্ট ফোল্ডারের ছবি লোড করা
    // ====================================================
    public void fetchImagesByCategory(String folderPrefix, DataCallback<List<DesignItem>> callback) {
        new AsyncTask<Void, Void, List<DesignItem>>() {
            @Override
            protected List<DesignItem> doInBackground(Void... voids) {
                List<DesignItem> designs = new ArrayList<>();
                try {
                    ListObjectsRequest request = new ListObjectsRequest()
                            .withBucketName(BUCKET_NAME)
                            .withPrefix(folderPrefix);
                    ObjectListing listing;

                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            String lowerKey = key.toLowerCase();
                            boolean isCover = lowerKey.contains("/cover.");

                            if (!key.equals(folderPrefix) && !isCover && isImageFile(key)) {
                                String fullUrl = getPresignedUrl(key);
                                String name = folderPrefix.replace("/", "") + " Design";
                                int views = 1500 + (int)(Math.random() * 5000);
                                designs.add(new DesignItem(fullUrl, name, views));
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return designs;
            }

            @Override
            protected void onPostExecute(List<DesignItem> result) {
                if (callback != null) callback.onResult(result);
            }
        }.execute();
    }

    // 🔥 Presigned URL Generator
    private String getPresignedUrl(String key) {
        try {
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60; // 1 Hour
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(BUCKET_NAME, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "https://pub-1b830b43818a419bb4ac06cb809ed435.r2.dev/" + key;
        }
    }

    private boolean isImageFile(String key) {
        String lowerKey = key.toLowerCase(Locale.ROOT);
        return lowerKey.endsWith(".jpg") || lowerKey.endsWith(".jpeg") ||
                lowerKey.endsWith(".png") || lowerKey.endsWith(".webp");
    }

    public interface DataCallback<T> {
        void onResult(T data);
    }
}