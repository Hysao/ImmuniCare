package com.myprograms.immunicare.password;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText oldPassword, newPassword, confirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private MaterialButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPasswordText = oldPassword.getText().toString().trim();
                String newPasswordText = newPassword.getText().toString().trim();
                String confirmPasswordText = confirmPassword.getText().toString().trim();

                if (newPasswordText.isEmpty() || confirmPasswordText.isEmpty() || oldPasswordText.isEmpty()) {
                    showToast("Please fill in all fields.");
                    return;
                }

                if (!newPasswordText.equals(confirmPasswordText)) {
                    showToast("New password and confirmation do not match.");
                    return;
                }

                if (newPasswordText.length() < 6) {
                    showToast("Password should be at least 6 characters long.");
                    return;
                }

                if (mUser != null) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String email = mUser.getEmail();
                    if (email == null) {
                        showToast("Failed to get user email.");
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, oldPasswordText)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    mUser.updatePassword(newPasswordText)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    showToast("Password updated successfully.");
                                                    finish();
                                                } else {
                                                    showToast("Failed to update password: " + updateTask.getException().getMessage());
                                                }
                                            });
                                } else {
                                    showToast("Reauthentication failed: " + task.getException().getMessage());
                                }
                            });
                } else {
                    showToast("No authenticated user.");
                }
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}