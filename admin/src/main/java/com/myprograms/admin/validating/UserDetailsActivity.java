package com.myprograms.admin.validating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userAccountId, userName, userEmail, userPhone, userAddress, isVerified, isHw;
    private ImageButton backBtn;
    private LinearLayout btnContainer;
    private ImageView barangayIdImage;
    private MaterialButton approveBtn, rejectBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        backBtn = findViewById(R.id.backBtn);
        userAccountId = findViewById(R.id.userAccountId);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userAddress = findViewById(R.id.userAddress);
        barangayIdImage = findViewById(R.id.barangayIdImage);
        approveBtn = findViewById(R.id.approveBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        isVerified = findViewById(R.id.isVerified);
        btnContainer = findViewById(R.id.btnContainer);
        isHw = findViewById(R.id.isHw);

        db = FirebaseFirestore.getInstance();

        String documentId = getIntent().getStringExtra("documentId");
        if (documentId != null) {
            loadUserDetails(documentId);
        } else {
            Toast.makeText(this, "No document ID found", Toast.LENGTH_SHORT).show();
            finish();
        }

        backBtn.setOnClickListener(v -> finish());

        approveBtn.setOnClickListener(v -> approveUser(documentId));
        rejectBtn.setOnClickListener(v -> showRejectDialog(documentId));
    }

    private void loadUserDetails(String documentId) {
        db.collection("users").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userAccountId.setText(documentSnapshot.getString("accountId"));
                        userName.setText(documentSnapshot.getString("name"));
                        userEmail.setText(documentSnapshot.getString("email"));
                        userPhone.setText(documentSnapshot.getString("phone"));
                        userAddress.setText(documentSnapshot.getString("address"));

                        if (!Objects.equals(documentSnapshot.getString("isVerified"), "pending")) {
                            isVerified.setVisibility(View.VISIBLE);
                            isVerified.setText(documentSnapshot.getString("isVerified"));
                            btnContainer.setVisibility(View.GONE);
                        }

                        String imageUrl = documentSnapshot.getString("barangayIdImageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this).load(imageUrl).into(barangayIdImage);
                        } else {
                            Toast.makeText(this, "Barangay ID image not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void approveUser(String documentId) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isVerified", "approved");
        updateData.put("notification", "Your account has been approved.");

        db.collection("users").document(documentId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User approved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error approving user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void showRejectDialog(String documentId) {
        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_reject_reason, null);
        EditText reasonInput = dialogView.findViewById(R.id.rejectReasonInput);

        new AlertDialog.Builder(this)
                .setTitle("Reject User")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String reason = reasonInput.getText().toString().trim();
                    if (!reason.isEmpty()) {
                        rejectUser(documentId, reason);
                    } else {
                        Toast.makeText(this, "Reason is required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void rejectUser(String documentId, String reason) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isVerified", "rejected");
        updateData.put("notification", "Your account has been rejected. Reason: " + reason);

        db.collection("users").document(documentId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User rejected", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error rejecting user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
