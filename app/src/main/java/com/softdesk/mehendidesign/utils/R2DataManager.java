package com.softdesk.mehendidesign.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.softdesk.mehendidesign.models.CategoryModel;
import com.softdesk.mehendidesign.models.DesignItem;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class R2DataManager {

    private static final String LOG = "LOG";

    private static String ACCOUNT_ID = "";
    private static String ACCESS_KEY = "";
    private static String SECRET_KEY = "";
    private static String BUCKET_NAME = "";
    private static String PUBLIC_URL = "";

    private AmazonS3Client s3Client;

    public R2DataManager(Context context) {
        // Constructor empty
    }

    private static final String CONFIG_URL = "http://192.168.1.105/config.php";

    private void ensureClientInitialized() {
        if (s3Client != null && !PUBLIC_URL.isEmpty()) return;

        try {
            Log.d("R2Data", "Fetching secure config...");

            URL url = new URL(CONFIG_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.setRequestProperty("X-API-KEY", LOG);

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.e("R2Data", "Security Alert: Server rejected connection. Code: " + responseCode);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // JSON পার্স করা
            JSONObject json = new JSONObject(response.toString());

            // এরর চেক করা
            if (json.has("error")) {
                Log.e("R2Data", "Server Error: " + json.getString("error"));
                return;
            }

            ACCOUNT_ID = json.getString("account_id");
            ACCESS_KEY = json.getString("access_key");
            SECRET_KEY = json.getString("secret_key");
            BUCKET_NAME = json.getString("bucket_name");
            PUBLIC_URL = json.getString("public_url");

            Log.d("R2Data", "Secure Config Loaded!");

            // R2 ক্লায়েন্ট সেটআপ
            BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY.trim(), SECRET_KEY.trim());
            s3Client = new AmazonS3Client(credentials);
            s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
            s3Client.setEndpoint("https://" + ACCOUNT_ID + ".r2.cloudflarestorage.com");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("R2Data", "Secure Fetch Error: " + e.getMessage());
        }
    }

    // ====================================================
    // 🌟 1. POPULAR / ALL DESIGNS
    // ====================================================
    public void fetchAllDesigns(DataCallback<List<DesignItem>> callback) {
        new AsyncTask<Void, Void, List<DesignItem>>() {
            @Override
            protected List<DesignItem> doInBackground(Void... voids) {
                ensureClientInitialized();

                // যদি কনফিগ লোড না হয় বা ক্লায়েন্ট রেডি না থাকে, তবে ফাঁকা লিস্ট রিটার্ন করবে
                if (s3Client == null || PUBLIC_URL.isEmpty()) {
                    Log.e("R2Data", "Client not initialized or URL missing");
                    return new ArrayList<>();
                }

                List<DesignItem> allDesigns = new ArrayList<>();
                try {
                    ListObjectsRequest request = new ListObjectsRequest().withBucketName(BUCKET_NAME);
                    ObjectListing listing;
                    do {
                        listing = s3Client.listObjects(request);
                        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            boolean isCover = key.toLowerCase().contains("/cover.");
                            if (key.contains("/") && !isCover && isImageFile(key)) {

                                String fullUrl = getGeneratedUrl(key);
                                if (!fullUrl.isEmpty()) {
                                    String[] parts = key.split("/");
                                    String name = (parts.length > 0 ? parts[0] : "Mehndi") + " Design";
                                    int views = 1500 + (int) (Math.random() * 5000);
                                    allDesigns.add(new DesignItem(fullUrl, name, views));
                                }
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated());

                    Collections.shuffle(allDesigns);
                    if (allDesigns.size() > 100) return allDesigns.subList(0, 100);

                } catch (Exception e) {
                    e.printStackTrace();
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
    // 📂 2. CATEGORIES
    // ====================================================
    public void fetchCategories(DataCallback<List<CategoryModel>> callback) {
        new AsyncTask<Void, Void, List<CategoryModel>>() {
            @Override
            protected List<CategoryModel> doInBackground(Void... voids) {
                ensureClientInitialized();
                if (s3Client == null || PUBLIC_URL.isEmpty()) return new ArrayList<>();

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

                                    String url = getGeneratedUrl(key);
                                    if (!url.isEmpty()) {
                                        if (!categoryCoverMap.containsKey(folderName) && isImageFile(key)) {
                                            categoryCoverMap.put(folderName, url);
                                        }
                                        if (fileName.startsWith("cover.") && isImageFile(key)) {
                                            categoryCoverMap.put(folderName, url);
                                        }
                                    }
                                }
                            }
                        }
                        request.setMarker(listing.getNextMarker());
                    } while (listing.isTruncated());

                    for (Map.Entry<String, String> entry : categoryCoverMap.entrySet()) {
                        String name = entry.getKey();
                        String id = name + "/";
                        categories.add(new CategoryModel(id, name, entry.getValue()));
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
    // 🖼️ 3. FOLDER IMAGES
    // ====================================================
    public void fetchImagesByCategory(String folderPrefix, DataCallback<List<DesignItem>> callback) {
        new AsyncTask<Void, Void, List<DesignItem>>() {
            @Override
            protected List<DesignItem> doInBackground(Void... voids) {
                ensureClientInitialized();
                if (s3Client == null || PUBLIC_URL.isEmpty()) return new ArrayList<>();

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
                            boolean isCover = key.toLowerCase().contains("/cover.");
                            if (!key.equals(folderPrefix) && !isCover && isImageFile(key)) {

                                String fullUrl = getGeneratedUrl(key);
                                if (!fullUrl.isEmpty()) {
                                    String name = folderPrefix.replace("/", "") + " Design";
                                    int views = 1500 + (int) (Math.random() * 5000);
                                    designs.add(new DesignItem(fullUrl, name, views));
                                }
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

    private String getGeneratedUrl(String key) {
        if (PUBLIC_URL == null || PUBLIC_URL.isEmpty()) {
            return "";
        }
        return PUBLIC_URL + key;
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