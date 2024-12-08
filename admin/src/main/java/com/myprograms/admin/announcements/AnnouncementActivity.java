package com.myprograms.admin.announcements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference announcementsRef = db.collection("announcements");

    private ImageButton backButton;
    private Button addAnnouncement;
    private RecyclerView announcementRecycler;
    private TextView noAnnouncementsTxt;

    private List<Announcements> announcements;
    private AnnouncementAdapter announcementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.backBtn);
        addAnnouncement = findViewById(R.id.addAnnouncementBtn);
        announcementRecycler = findViewById(R.id.announcementList);
        noAnnouncementsTxt = findViewById(R.id.noAnnouncementsTxt);

        backButton.setOnClickListener(v -> finish());
        addAnnouncement.setOnClickListener(v -> startActivity(new Intent(this, MakeAnnouncementActivity.class)));

        announcementRecycler.setLayoutManager(new LinearLayoutManager(this));


        loadAnnouncements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        announcementsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                announcements = task.getResult().toObjects(Announcements.class);

                if (announcements == null || announcements.isEmpty()) {
                    noAnnouncementsTxt.setVisibility(View.VISIBLE);
                    announcementRecycler.setVisibility(View.GONE);
                } else {
                    noAnnouncementsTxt.setVisibility(View.GONE);
                    announcementRecycler.setVisibility(View.VISIBLE);

                    announcementAdapter = new AnnouncementAdapter(this, announcements);
                    announcementRecycler.setAdapter(announcementAdapter);
                }
            } else {
                // Handle error
                noAnnouncementsTxt.setText("Failed to load announcements");
                noAnnouncementsTxt.setVisibility(View.VISIBLE);
                announcementRecycler.setVisibility(View.GONE);
            }
        });
    }
}
