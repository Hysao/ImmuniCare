package com.myprograms.admin.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.HashMap;

public class ScheduleDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference schedulesRef = db.collection("schedule");
    private CollectionReference reminderRef = db.collection("reminders");

    private TextView userName, userEmail, visit, vaccines, date, status;
    private MaterialButton approveBtn, rejectBtn;
    private ImageButton backBtn;
    private LinearLayout buttons;

    private String userId;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        visit = findViewById(R.id.visit);
        vaccines = findViewById(R.id.vaccines);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        approveBtn = findViewById(R.id.approveBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        backBtn = findViewById(R.id.backBtn);
        buttons = findViewById(R.id.buttons);

        backBtn.setOnClickListener(v -> finish());

        // Retrieve extras from intent
        userId = getIntent().getStringExtra("userId");
        documentId = getIntent().getStringExtra("documentId");

        // Validate received extras
        if (userId == null || documentId == null) {
            throw new IllegalArgumentException("Missing required extras: userId or documentId");
        }

        fetchScheduleDetails();

        approveBtn.setOnClickListener(v -> {
            approveSchedule();
        });

        rejectBtn.setOnClickListener(v -> {
            rejectSchedule();
        });

    }

    private void approveSchedule() {
        schedulesRef.document(documentId).update("status", "approved")
                .addOnSuccessListener(unused -> {

                    HashMap<String, String> reminder = new HashMap<>();
                    reminder.put("userId", userId);
                    reminder.put("documentId", documentId);
                    reminder.put("date", date.getText().toString());
                    reminder.put("description", vaccines.getText().toString());
                    reminder.put("title", visit.getText().toString());


                    reminderRef.add(reminder)
                            .addOnSuccessListener(reminderDocRef -> {

                                sendApprovalEmail();
                            })
                            .addOnFailureListener(e -> {

                                e.printStackTrace();
                                Toast.makeText(ScheduleDetailsActivity.this, "Failed to add reminder. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure to update the schedule status
                    e.printStackTrace();
                    Toast.makeText(ScheduleDetailsActivity.this, "Failed to approve the schedule. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }


    private void sendApprovalEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail.getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schedule Approved");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your schedule has been approved." +
                "\nPlease check your schedule on "+date.getText().toString()+
                "\nFor the vaccines"+ vaccines.getText().toString()+
                "\nThank you!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients are installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void rejectSchedule() {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rejection Reason");

        // Inflate the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reject_schedule, null);
        EditText reasonInput = dialogView.findViewById(R.id.reasonInput);
        builder.setView(dialogView);

        // Set dialog buttons
        builder.setPositiveButton("Submit", (dialog, which) -> {
            String rejectionReason = reasonInput.getText().toString().trim();

            if (!rejectionReason.isEmpty()) {
                // Update Firestore document with status and reason
                schedulesRef.document(documentId).update("status", "rejected", "rejectionReason", rejectionReason)
                        .addOnSuccessListener(unused -> {



                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail.getText().toString()});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Schedule Rejected");
                            i.putExtra(Intent.EXTRA_TEXT, "Your schedule has been rejected for the following reason:\n\n" + rejectionReason + "\n\nPlease contact us for further information.");
                            startActivity(Intent.createChooser(i, "Send email"));
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to reject the schedule. Try again.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Show a warning if reason is empty
                Toast.makeText(this, "Please enter a reason for rejection.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void fetchScheduleDetails() {

        schedulesRef.document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate fields with data
                        userName.setText(documentSnapshot.getString("userName"));
                        userEmail.setText(documentSnapshot.getString("userEmail"));
                        visit.setText(documentSnapshot.getString("visit"));
                        vaccines.setText(documentSnapshot.get("vaccines").toString());
                        date.setText(documentSnapshot.getString("date"));
                        status.setText(documentSnapshot.getString("status"));
                        if ("pending".equals(status.getText().toString())) {
                            buttons.setVisibility(View.VISIBLE);
                            status.setVisibility(View.GONE);
                        } else {
                            buttons.setVisibility(View.GONE);
                            status.setVisibility(View.VISIBLE);
                        }

                    } else {

                        throw new IllegalStateException("Schedule not found for documentId: " + documentId);
                    }
                })
                .addOnFailureListener(e -> {

                    e.printStackTrace();
                });
    }
}
