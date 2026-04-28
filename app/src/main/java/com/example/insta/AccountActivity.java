package com.example.insta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private ImageView backBtn;
    private Button btnShowQR, btnLogout;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> userPosts = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userId; // can be current user or another user

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        backBtn = findViewById(R.id.backBtn);
        btnShowQR = findViewById(R.id.btnShowQR);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerView = findViewById(R.id.recyclerViewAccount);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // default to current user
        }

        // Hide QR & Logout buttons if viewing another user
        if (!userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            btnShowQR.setVisibility(Button.GONE);
            btnLogout.setVisibility(Button.GONE);
        }

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Show QR code
        btnShowQR.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, QRActivity.class)));

        // Logout button
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AccountActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Setup RecyclerView for user posts
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(userPosts);
        recyclerView.setAdapter(postAdapter);

        loadUserPosts(userId); // Fetch posts for the given user
    }

    private void loadUserPosts(String userId) {
        db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userPosts.clear();
                    for (var doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        post.setPostId(doc.getId());
                        userPosts.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }
}
