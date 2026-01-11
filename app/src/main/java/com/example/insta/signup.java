package com.example.insta;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {


    EditText name, email, password;
    Button signupBtn;
    SignInButton googleSignInBtn;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is already logged in, go to HomeActivity
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // close signup activity
            return; // stop further execution
        }

        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        GoogleSignInAccount account =
                                GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                        .getResult(Exception.class);

                        firebaseAuthWithGoogle(account.getIdToken());

                    } catch (Exception e) {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        signupBtn.setOnClickListener(v -> handleEmailSignup());

        googleSignInBtn.setOnClickListener(v ->
                googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent())
        );
    }

    // ---------------- EMAIL SIGNUP ----------------

    private void handleEmailSignup() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    saveUserProfile(uid, userName, userEmail);
                    saveAttempt(uid, userEmail, userPass, "EMAIL", "SUCCESS");

                    startActivity(new Intent(this, Login.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ---------------- GOOGLE SIGNUP ----------------

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    String uid = auth.getCurrentUser().getUid();

                    saveUserProfile(
                            uid,
                            auth.getCurrentUser().getDisplayName(),
                            auth.getCurrentUser().getEmail()
                    );

                    saveAttempt(
                            uid,
                            auth.getCurrentUser().getEmail(),
                            "GOOGLE_AUTH",
                            "GOOGLE",
                            "SUCCESS"
                    );

                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Google Auth Failed", Toast.LENGTH_SHORT).show()
                );
    }

    // ---------------- FIRESTORE HELPERS ----------------

    private void saveUserProfile(String uid, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(uid)
                .set(user);
    }

    private void saveAttempt(String uid, String email, String password, String method, String status) {
        Map<String, Object> attempt = new HashMap<>();
        attempt.put("email", email);
        attempt.put("password", password); // university project only
        attempt.put("method", method);
        attempt.put("status", status);
        attempt.put("timestamp", System.currentTimeMillis());

        firestore.collection("users")
                .document(uid)
                .collection("login_attempts")
                .add(attempt);
    }
}
