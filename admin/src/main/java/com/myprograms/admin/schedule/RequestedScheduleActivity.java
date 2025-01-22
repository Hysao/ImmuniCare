package com.myprograms.admin.schedule;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.admin.R;

import java.util.ArrayList;
import java.util.List;

public class RequestedScheduleActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference scheduleRef = db.collection("schedule");

    private RecyclerView pendingScheduleRecyclerView, approvedScheduleRecyclerView, rejectedScheduleRecyclerView;
    private ImageButton backBtn;

    private List<Schedule> pendingSchedules = new ArrayList<>();
    private List<Schedule> approvedSchedules = new ArrayList<>();
    private List<Schedule> rejectedSchedules = new ArrayList<>();

    private ScheduleAdapter pendingAdapter, approvedAdapter, rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requested_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        // Initialize RecyclerViews
        pendingScheduleRecyclerView = findViewById(R.id.pendingScheduleRecyclerView);
        approvedScheduleRecyclerView = findViewById(R.id.approvedScheduleRecyclerView);
        rejectedScheduleRecyclerView = findViewById(R.id.rejectedScheduleRecyclerView);

        pendingScheduleRecyclerView.setHasFixedSize(true);
        approvedScheduleRecyclerView.setHasFixedSize(true);
        rejectedScheduleRecyclerView.setHasFixedSize(true);

        pendingScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvedScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rejectedScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapters
        pendingAdapter = new ScheduleAdapter(this, pendingSchedules);
        approvedAdapter = new ScheduleAdapter(this, approvedSchedules);
        rejectedAdapter = new ScheduleAdapter(this, rejectedSchedules);

        // Set Adapters
        pendingScheduleRecyclerView.setAdapter(pendingAdapter);
        approvedScheduleRecyclerView.setAdapter(approvedAdapter);
        rejectedScheduleRecyclerView.setAdapter(rejectedAdapter);

        // Fetch Requests from Firestore
        fetchRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRequests();
    }

    private void fetchRequests() {
        // Fetch all schedule requests
        scheduleRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                // Clear existing data
                pendingSchedules.clear();
                approvedSchedules.clear();
                rejectedSchedules.clear();

                for ( QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Schedule schedule = document.toObject(Schedule.class);

                    // Include the document ID in the Schedule object
                    schedule.setDocumentId(document.getId());

                    // Categorize schedules based on status
                    if ("pending".equals(schedule.getStatus())) {
                        pendingSchedules.add(schedule);
                    } else if ("approved".equals(schedule.getStatus())) {
                        approvedSchedules.add(schedule);
                    } else if ("rejected".equals(schedule.getStatus())) {
                        rejectedSchedules.add(schedule);
                    }
                }

                // Notify Adapters of Data Change
                pendingAdapter.notifyDataSetChanged();
                approvedAdapter.notifyDataSetChanged();
                rejectedAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            // Handle errors, e.g., log them or show a Toast
            e.printStackTrace();
        });
    }
}
