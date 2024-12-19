package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.calendar.CalendarActivity;
import com.myprograms.immunicare.healthworker.HWMainActivity;
import com.myprograms.immunicare.user.UserMainActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextInputLayout passwordInputLayout;

    private TextView validateBtn, forgotPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.loginBtn);
        emailEditText = findViewById(R.id.emailLogin);
        passwordEditText = findViewById(R.id.passwordLogin);
        validateBtn = findViewById(R.id.validateBtn);
        forgotPassword = findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                // Handle empty fields
                Toast.makeText(LoginActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();

                            if (currentUser != null) {
                                if (currentUser.isEmailVerified()) {
                                    checkAdminApproval(currentUser.getUid());
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle case where currentUser is null (unexpected)
                                Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle login failure (e.g., incorrect credentials)
                            Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    private void checkAdminApproval(String userId) {
        usersRef.document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("isVerified");
                        Boolean isHw = documentSnapshot.getBoolean("isHw");
                        if (status != null && status.equals("approved")) {

                            Intent intent;
                            if (Boolean.FALSE.equals(isHw)){
                                intent = new Intent(LoginActivity.this, UserMainActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, HWMainActivity.class);
                            }
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Your account is pending admin approval.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle case where user document doesn't exist (unexpected)
                        Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error fetching user data
                    Toast.makeText(LoginActivity.this, "Error checking admin approval.", Toast.LENGTH_SHORT).show();
                });
    }
}