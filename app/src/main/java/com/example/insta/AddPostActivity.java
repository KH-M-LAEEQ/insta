package com.example.insta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 101;

    private ImageView imgSelected;
    private EditText etCaption;
    private Button btnPickImage, btnUpload;
    private Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imgSelected = findViewById(R.id.imgSelected);
        etCaption = findViewById(R.id.etCaption);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnUpload = findViewById(R.id.btnUpload);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        btnPickImage.setOnClickListener(v -> pickImage());

        btnUpload.setOnClickListener(v -> {
            if (imageUri != null) uploadPost();
            else Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
        });
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imgSelected);
        }
    }

    private void uploadPost() {
        String caption = etCaption.getText().toString().trim();
        if (caption.isEmpty()) caption = "";

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference postRef = storageRef.child("posts/" + System.currentTimeMillis() + ".jpg");

        String finalCaption = caption;
        postRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> postRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("username", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            postMap.put("profileImage", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() : "");
            postMap.put("postImage", uri.toString());
            postMap.put("caption", finalCaption);
            postMap.put("likes", 0);
            postMap.put("timestamp", System.currentTimeMillis());

            db.collection("posts").add(postMap).addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Post uploaded!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        })).addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
