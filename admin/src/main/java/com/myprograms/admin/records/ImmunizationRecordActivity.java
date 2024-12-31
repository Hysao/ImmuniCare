package com.myprograms.admin.records;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.ArrayList;
import java.util.List;

public class ImmunizationRecordActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");

    private ImageButton backBtn;
    private RecyclerView historyRecycler;

    private List<History> historyList = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_immunization_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        historyRecycler = findViewById(R.id.recordsRecycler);

        backBtn.setOnClickListener(v -> finish());

        // Initialize RecyclerView
        historyRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyList, this);
        historyRecycler.setAdapter(adapter);

        // Listen for Firestore changes
        historyRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                System.err.println("Firestore Listener Error: " + e.getMessage());
                return;
            }

            if (queryDocumentSnapshots == null) {
                System.err.println("No snapshot received from Firestore");
                return;
            }

            // Update list with new data
            historyList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                History history = document.toObject(History.class);
                if (history != null) {
                    historyList.add(history);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }
}
