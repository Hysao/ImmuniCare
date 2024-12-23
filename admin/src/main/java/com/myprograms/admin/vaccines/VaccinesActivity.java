package com.myprograms.admin.vaccines;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.R;


import java.util.ArrayList;
import java.util.List;

public class VaccinesActivity extends AppCompatActivity {

    private ImageButton back;
    private MaterialButton addVaccineBtn;
    private RecyclerView vaccinesRecycler;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vaccinesRef = db.collection("vaccines");

    private VaccineAdapter adapter;
    private List<Vaccines> vaccineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vaccines);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.backBtn);
        vaccinesRecycler = findViewById(R.id.vaccinesRecycler);
        addVaccineBtn = findViewById(R.id.addVaccineBtn);

        back.setOnClickListener(v -> finish());

        addVaccineBtn.setOnClickListener(v -> {
            Intent i = new Intent(VaccinesActivity.this, VaccineAddActivity.class);
            startActivity(i);
        });

        vaccinesRecycler.setLayoutManager(new LinearLayoutManager(this));
        vaccinesRecycler.setHasFixedSize(true);


        fetchVaccines();
    }

    private void fetchVaccines() {
        Query query = vaccinesRef.orderBy("name", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(VaccinesActivity.this, "No vaccines found", Toast.LENGTH_SHORT).show();
                return;
            }

            vaccineList.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Vaccines vaccine = documentSnapshot.toObject(Vaccines.class);
                vaccine.setId(documentSnapshot.getId());
                vaccineList.add(vaccine);
            }

            if (adapter == null) {
                adapter = new VaccineAdapter(VaccinesActivity.this, vaccineList);
                vaccinesRecycler.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(VaccinesActivity.this, "Loading Data Failed", Toast.LENGTH_SHORT).show();
            Log.e("Vaccines Activity", "Error loading data", e);
        });
    }
}
