package com.softdesk.mehendidesign.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class R2DataManager {

    // 🔥 FIXED CREDENTIALS FROM YOUR SCREENSHOT
    private static final String ACCOUNT_ID = "c784b8e571db8c9b498b351a77ba63b4";
    private static final String ACCESS_KEY = "17c8973571aadc409abd1af111e43444";
    private static final String SECRET_KEY = "a3e77539ea98e52e25439de2d1ef9409ea0ebae9c787458ad45df425334e7f8d";
    private static final String BUCKET_NAME = "mehendidesign";

    // ⚠️ শেষে স্ল্যাশ (/) থাকা বাধ্যতামূলক
    private static final String PUBLIC_URL  = "https://pub-1b830b43818a419bb4ac06cb809ed435.r2.dev/";

    private AmazonS3Client s3Client;

    public R2DataManager(Context context) {
        try {

            Log.e("MY_DEBUG", "Using Access Key: [" + ACCESS_KEY + "]");
            Log.e("MY_DEBUG", "Using Secret Key: [" + SECRET_KEY + "]");
            BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY.trim(), SECRET_KEY.trim()); // .trim() যোগ করেছি


            s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint("https://" + ACCOUNT_ID + ".r2.cloudflarestorage.com");
            s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("R2Data", "Connection Error: " + e.getMessage());
        }
    }

    // ====================================================
    // 📂 ১. ক্যাটাগরি লোড করা (Auto Cover Detection)
    // ====================================================
    public void fetchCategories(DataCallback<List<CategoryModel>> callback) {
        new AsyncTask<Void, Void, List<CategoryModel>>() {
            @Override
            protected List<CategoryModel> doInBackground(Void... voids) {
                Map<String, String> categoryCoverMap = new HashMap<>();
                List<CategoryModel> categories = new ArrayList<>();

                try {
                    ListObjectsRequest request = new ListObjectsRequest()
                            .withBucketName(BUCKET_NAME);
                    // .withDelimiter("/"); // Delimiter বাদ দেওয়া হয়েছে যাতে সব ফাইল স্ক্যান করে কভার বের করা যায়

                    ObjectListing listing = s3Client.listObjects(request);

                    for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                        String key = summary.getKey(); // ex: Bridal/cover.jpg

                        if (key.contains("/")) {
                            String[] parts = key.split("/");
                            // অন্তত ফোল্ডার এবং ফাইল নেম থাকতে হবে
                            if (parts.length >= 2) {
                                String folderName = parts[0]; // "Bridal"
                                String fileName = parts[1].toLowerCase(); // "cover.jpg"

                                // ১. ডিফল্ট কভার (প্রথম ছবি)
                                if (!categoryCoverMap.containsKey(folderName)) {
                                    categoryCoverMap.put(folderName, PUBLIC_URL + key);
                                }

                                // ২. যদি ফাইলের নাম 'cover' হয়, তাহলে ওটাই ফাইনাল কভার
                                if (fileName.startsWith("cover.")) {
                                    categoryCoverMap.put(folderName, PUBLIC_URL + key);
                                }
                            }
                        }
                    }

                    // ম্যাপ থেকে লিস্ট তৈরি
                    for (Map.Entry<String, String> entry : categoryCoverMap.entrySet()) {
                        String name = entry.getKey();
                        String url = entry.getValue();
                        String id = name + "/"; // ID হিসেবে ফোল্ডার পাথ

                        categories.add(new CategoryModel(id, name, url));
                    }

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
    // 🖼️ ২. ইমেজ লোড করা
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

                    ObjectListing listing = s3Client.listObjects(request);

                    for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                        String key = summary.getKey();
                        String lowerKey = key.toLowerCase();

                        // কভার ফটো ডিটেকশন
                        boolean isCover = lowerKey.contains("/cover.");

                        // লজিক: ফোল্ডার নয় + কভার ফটো নয় + ভ্যালিড ইমেজ ফাইল
                        if (!key.equals(folderPrefix) && !isCover && isImageFile(key)) {
                            String fullUrl = PUBLIC_URL + key;
                            String name = folderPrefix.replace("/", "") + " Design";
                            int views = 1500 + (int)(Math.random() * 5000); // Random Views

                            designs.add(new DesignItem(fullUrl, name, views));
                        }
                    }
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

    // ইমেজ চেকার
    private boolean isImageFile(String key) {
        String lowerKey = key.toLowerCase(Locale.ROOT);
        return lowerKey.endsWith(".jpg") ||
                lowerKey.endsWith(".jpeg") ||
                lowerKey.endsWith(".png") ||
                lowerKey.endsWith(".webp");
    }

    public interface DataCallback<T> {
        void onResult(T data);
    }
}