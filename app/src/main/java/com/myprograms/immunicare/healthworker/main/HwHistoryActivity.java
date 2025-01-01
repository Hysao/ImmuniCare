package com.myprograms.immunicare.healthworker.main;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.History;
import com.myprograms.immunicare.healthworker.update.HistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HwHistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");

    private ImageButton backBtn;
    private RecyclerView historyRecyclerView;

    private HistoryAdapter historyAdapter;
    private List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hw_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        backBtn = findViewById(R.id.backBtn);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        backBtn.setOnClickListener(v -> finish());

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList, this);
        historyRecyclerView.setAdapter(historyAdapter);

        fetchHistoryData();
    }

//    private void fetchHistoryData() {
//        if (mUser == null) return;
//
//        historyRef.whereEqualTo("hWorkerId", mUser.getUid())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        historyList = task.getResult().toObjects(History.class);
//                        historyAdapter = new HistoryAdapter(historyList, this);
//                        historyRecyclerView.setAdapter(historyAdapter);
//                    } else {
//                        System.err.println("Error fetching history: " + task.getException());
//                    }
//                });
//    }

    private void fetchHistoryData() {
        if (mUser == null) return;

        historyRef.whereEqualTo("hWorkerId", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        historyList = task.getResult().toObjects(History.class);


                        Collections.sort(historyList, (h1, h2) -> Long.compare(h2.getTimestamp(), h1.getTimestamp()));

                        historyAdapter = new HistoryAdapter(historyList, this);
                        historyRecyclerView.setAdapter(historyAdapter);
                    } else {
                        System.err.println("Error fetching history: " + task.getException());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchHistoryData();
    }
}
