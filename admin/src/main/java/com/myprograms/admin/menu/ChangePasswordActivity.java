package com.myprograms.admin.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.admin.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPassword, newPassword, confirmPassword;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference adminRef = db.collection("admin");

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

        // Initialize UI elements
        ImageButton backBtn = findViewById(R.id.backBtn);
        MaterialButton saveBtn = findViewById(R.id.saveBtn);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        backBtn.setOnClickListener(v -> finish());

        saveBtn.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPass = oldPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        // Validate input fields
        if (!validateInputs(oldPass, newPass, confirmPass)) return;

        // Query Firestore to verify the old password
        adminRef.whereEqualTo("userName", "admin").get()
                .addOnSuccessListener(this::verifyOldPassword)
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String oldPass, String newPass, String confirmPass) {
        boolean isValid = true;

        if (oldPass.isEmpty()) {
            oldPassword.setError("Please enter old password");
            isValid = false;
        }
        if (newPass.isEmpty()) {
            newPassword.setError("Please enter new password");
            isValid = false;
        }
        if (confirmPass.isEmpty()) {
            confirmPassword.setError("Please confirm your new password");
            isValid = false;
        }
        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        if (oldPass.equals(newPass)) {
            newPassword.setError("New password cannot be the same as the old password");
            isValid = false;
        }

        return isValid;
    }

    private void verifyOldPassword(QuerySnapshot querySnapshot) {
        if (querySnapshot.isEmpty()) {
            oldPassword.setError("Admin user not found");
            return;
        }

        String storedPassword = Objects.requireNonNull(querySnapshot.getDocuments().get(0).getString("password"));
        String oldPass = oldPassword.getText().toString().trim();

        if (storedPassword.equals(oldPass)) {
            // Update password in Firestore
            adminRef.document(querySnapshot.getDocuments().get(0).getId())
                    .update("password", newPassword.getText().toString().trim())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            oldPassword.setError("Old password is incorrect");
        }
    }
}
