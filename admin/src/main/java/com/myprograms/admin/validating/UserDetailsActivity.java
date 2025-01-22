package com.myprograms.admin.validating;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
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
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();

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

        approveBtn.setOnClickListener(v -> approveUser(documentId, userEmail.getText().toString()));
        rejectBtn.setOnClickListener(v -> showRejectDialog(documentId));


        setupPhotoView();

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

                        if (Boolean.TRUE.equals(documentSnapshot.getBoolean("isHw"))){
                            isHw.setText("Health Care ID");
                        }else {
                            isHw.setText("Barangay ID");
                        }

                        String base64Image = documentSnapshot.getString("photo");
                        if (base64Image != null && !base64Image.isEmpty()) {
                            try {
                                // Decode the Base64 string into a byte array
                                byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);

                                // Convert the byte array into a Bitmap
                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                                // Set the Bitmap to the ImageView
                                barangayIdImage.setImageBitmap(decodedBitmap);
                            } catch (IllegalArgumentException e) {
                                Log.e("UserDetailsActivity", "Error decoding Base64 image", e);
                                Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("UserDetailsActivity", "Base64 image string is null or empty");
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


    private void approveUser(String documentId, String userEmail) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isVerified", "approved");
        updateData.put("notification", false);

        db.collection("users").document(documentId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    sendApprovalEmail(userEmail); // Send the email
                    Toast.makeText(this, "User approved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error approving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendApprovalEmail(String userEmail) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);

        String approvedMessage = "Your account has been approved in ImmuniCare Application!";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Account Approved");
        emailIntent.putExtra(Intent.EXTRA_TEXT, approvedMessage);
        startActivity(Intent.createChooser(emailIntent, "Send approval email"));
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

    private void sendEmail(String recipient, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    private void rejectUser(String documentId, String reason) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("isVerified", "rejected");

        db.collection("users").document(documentId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    String rejectionMessage = "Your account has been rejected. Reason: " + reason;
                    sendEmail(userEmail.getText().toString(), "Account Rejected", rejectionMessage);
                    deleteUserFromFirebaseAuth(documentId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error rejecting user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void deleteUserFromFirebaseAuth(String documentId) {
        db.collection("users").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        auth.fetchSignInMethodsForEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        // Log success but user deletion requires admin privileges in Firebase
                                        Toast.makeText(this, "User marked as rejected in the database.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Unable to find user for deletion in FirebaseAuth", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupPhotoView() {
        barangayIdImage.setOnClickListener(v -> showZoomDialog());
    }

    private void showZoomDialog() {
        // Create a dialog with a custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_zoom_image, null);

        PhotoView photoView = dialogView.findViewById(R.id.photoView);

        // Load the image from the current ImageView into PhotoView
        photoView.setImageDrawable(barangayIdImage.getDrawable());

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Dismiss the dialog when PhotoView is clicked
        photoView.setOnClickListener(v -> dialog.dismiss());
    }
}
