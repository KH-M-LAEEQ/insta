package com.example.insta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_account extends AppCompatActivity {

    TextView userName, userEmail;
    ImageView profileImage;
    Button logoutBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize views
        userName = findViewById(R.id.username);
        userEmail = findViewById(R.id.userEmail);
        profileImage = findViewById(R.id.profileImage);
        logoutBtn = findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // ---------------- SHOW USER INFO ----------------
        if (user != null) {
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            userEmail.setText(user.getEmail());
            // Optional: Load profile image if available
            // Glide.with(this).load(user.getPhotoUrl()).into(profileImage);
        }

        // ---------------- LOGOUT ----------------
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            // Go back to login/signup screen
            startActivity(new Intent(this, signup.class));
            finish();
        });
    }
}
