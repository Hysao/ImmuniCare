package com.myprograms.immunicare.user.immunization;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.update.History;
import com.myprograms.immunicare.healthworker.update.HistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserImmunizationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton back;
    private TextView emptyView;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private List<History> historyList = new ArrayList<>();
    private HistoryAdapter historyAdapter;

    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_immunization_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        documentId = getIntent().getStringExtra("documentId");
        if (documentId == null || documentId.isEmpty()) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        emptyView = findViewById(R.id.empty_view);

        back.setOnClickListener(v -> finish());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyAdapter = new HistoryAdapter(historyList, this);
        recyclerView.setAdapter(historyAdapter);

        fetchHistory();
    }

    private void fetchHistory() {
        Query query = historyRef.whereEqualTo("childId", documentId);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showEmptyView(true);
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            History history = document.toObject(History.class);
                            historyList.add(history);
                            Collections.sort(historyList, (h1, h2) -> Long.compare(h2.getTimestamp(), h1.getTimestamp()));
                        }
                        historyAdapter.notifyDataSetChanged();
                        showEmptyView(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Log the error or show a toast message
                    showEmptyView(true);
                });
    }

    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
