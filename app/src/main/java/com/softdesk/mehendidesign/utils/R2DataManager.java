package com.softdesk.mehendidesign.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.HttpMethod;
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

    // ✅ আপনার ক্রেডেনশিয়াল (লগ থেকে ভেরিফাইড)
    private static final String ACCOUNT_ID = "c784b8e571db8c9b498b351a77ba63b4";
    private static final String ACCESS_KEY = "0f68c743da42a2841213ee8dde89f715";
    private static final String SECRET_KEY = "665647c8f26cc669aac4b23e1d22a8483b2559b33f6d6b0ac15dc8d7bbeaa45e";
    private static final String BUCKET_NAME = "mehendidesign";

    private AmazonS3Client s3Client;

    public R2DataManager(Context context) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY.trim(), SECRET_KEY.trim());
            s3Client = new AmazonS3Client(credentials);

            // R2 কনফিগারেশন
            s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
            s3Client.setEndpoint("https://" + ACCOUNT_ID + ".r2.cloudflarestorage.com");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("R2Data", "Connection Error: " + e.getMessage());
        }
    }

    // ====================================================
    // 📂 ১. ক্যাটাগরি লোড করা (With Pagination)
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

                    // 🔥 লুপ: ১০০০ এর বেশি ফাইল থাকলেও সব আনবে
                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();

                            // ফোল্ডার ডিটেকশন
                            if (key.contains("/")) {
                                String[] parts = key.split("/");
                                if (parts.length >= 2) {
                                    String folderName = parts[0];
                                    String fileName = parts[1].toLowerCase();

                                    // কভার ইমেজ লজিক
                                    if (!categoryCoverMap.containsKey(folderName) && isImageFile(key)) {
                                        // ডিফল্ট: ফোল্ডারের প্রথম ছবিটি কভার হবে
                                        categoryCoverMap.put(folderName, getPresignedUrl(key));
                                    }
                                    if (fileName.startsWith("cover.") && isImageFile(key)) {
                                        // স্পেশাল: যদি 'cover.jpg' থাকে, তবে সেটাই ফাইনাল কভার
                                        categoryCoverMap.put(folderName, getPresignedUrl(key));
                                    }
                                }
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated()); // যতক্ষণ আরও ফাইল বাকি আছে, লুপ চলবে

                    // ম্যাপ থেকে লিস্ট তৈরি
                    for (Map.Entry<String, String> entry : categoryCoverMap.entrySet()) {
                        String name = entry.getKey();
                        String url = entry.getValue();
                        String id = name + "/";
                        categories.add(new CategoryModel(id, name, url));
                    }

                    // A-Z সর্টিং
                    Collections.sort(categories, new Comparator<CategoryModel>() {
                        @Override
                        public int compare(CategoryModel c1, CategoryModel c2) {
                            return c1.getTitle().compareToIgnoreCase(c2.getTitle());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("R2Data", "Category Error: " + e.getMessage());
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
    // 🖼️ ২. ইমেজ লোড করা (With Presigned URL)
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

                    // 🔥 লুপ: ফোল্ডারের সব ছবি আনার জন্য
                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            String lowerKey = key.toLowerCase();
                            boolean isCover = lowerKey.contains("/cover.");

                            // শুধু ইমেজ নেবো (ফোল্ডার এবং কভার বাদে)
                            if (!key.equals(folderPrefix) && !isCover && isImageFile(key)) {

                                // 🔥 ফিক্স: Presigned URL জেনারেট করা হচ্ছে যাতে ছবি ১০০% লোড হয়
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
                    Log.e("R2Data", "Image Error: " + e.getMessage());
                }
                return designs;
            }

            @Override
            protected void onPostExecute(List<DesignItem> result) {
                if (callback != null) callback.onResult(result);
            }
        }.execute();
    }

    // 🔥 নতুন মেথড: এটি সিকিউর লিংক তৈরি করে যা ১ ঘণ্টা ভ্যালিড থাকে
    private String getPresignedUrl(String key) {
        try {
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60; // ১ ঘণ্টা মেয়াদ
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(BUCKET_NAME, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "https://pub-1b830b43818a419bb4ac06cb809ed435.r2.dev/" + key; // ফলব্যাক
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