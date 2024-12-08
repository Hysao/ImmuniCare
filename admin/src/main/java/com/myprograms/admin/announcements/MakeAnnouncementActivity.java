package com.myprograms.admin.announcements;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

public class MakeAnnouncementActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference announcementsRef = db.collection("announcements");

    private EditText announcementTitle, announcementDescription;
    private Button cancel, add;

    private Announcements announcements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_make_announcement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        announcementTitle = findViewById(R.id.announcement_title);
        announcementDescription = findViewById(R.id.announcement_description);
        cancel = findViewById(R.id.cancel_button);
        add = findViewById(R.id.add_button);

        cancel.setOnClickListener(v -> finish());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = announcementTitle.getText().toString().trim();
                String description = announcementDescription.getText().toString().trim();
                Timestamp timestamp = Timestamp.now();
                String author = "admin";

                if (title.isEmpty() || description.isEmpty()) {
                    if (title.isEmpty()) {
                        announcementTitle.setError("Title cannot be empty");
                    }
                    if (description.isEmpty()) {
                        announcementDescription.setError("Description cannot be empty");
                    }
                } else {
                    Announcements newAnnouncement = new Announcements(title, description, timestamp, author);

                    announcementsRef.add(newAnnouncement)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MakeAnnouncementActivity.this,
                                        "Announcement added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MakeAnnouncementActivity.this,
                                        "Failed to add announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


    }
}