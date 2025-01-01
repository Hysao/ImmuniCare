package com.myprograms.admin.concerns;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.R;

import java.util.ArrayList;
import java.util.List;

public class ConcernsActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView concernRecycler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference concernRef = db.collection("concern");

    private ConcernAdapter concernAdapter;
    private List<Concern> concernList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_concerns);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        backBtn = findViewById(R.id.backBtn);
        concernRecycler = findViewById(R.id.concernRecycler);

        // Set up RecyclerView
        concernRecycler.setLayoutManager(new LinearLayoutManager(this));
        concernAdapter = new ConcernAdapter(concernList);
        concernRecycler.setAdapter(concernAdapter);

        // Set back button click listener
        backBtn.setOnClickListener(v -> finish());


        fetchConcerns();
    }

    private void fetchConcerns() {
        concernRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    concernList.clear(); // Clear list to avoid duplicates
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Concern concern = document.toObject(Concern.class);
                        concernList.add(concern);
                    }
                    concernAdapter.notifyDataSetChanged(); // Refresh adapter
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch concerns: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
