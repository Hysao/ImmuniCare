package com.myprograms.admin.vaccines;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

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

        adapter = new VaccineAdapter(this, vaccineList);
        vaccinesRecycler.setAdapter(adapter);

        fetchVaccines();
    }

    private void fetchVaccines() {
        vaccinesRef.orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;

                    vaccineList.clear();
                    for (DocumentSnapshot document : snapshots) {
                        Vaccines vaccine = document.toObject(Vaccines.class);
                        vaccine.setId(document.getId());
                        vaccineList.add(vaccine);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
