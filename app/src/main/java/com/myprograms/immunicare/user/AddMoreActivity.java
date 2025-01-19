package com.myprograms.immunicare.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.auth.LoginActivity;
import com.myprograms.immunicare.user.setting.ChildrenViewAdapter;
import com.myprograms.immunicare.user.setting.reminder.Children;

import java.util.ArrayList;
import java.util.List;

public class AddMoreActivity extends AppCompatActivity {

    private RecyclerView rvChildInfo;
    private TextView addMoreTxt;
    private MaterialButton addMoreBtn;
    private ImageButton backBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private CollectionReference userRef = db.collection("users");

    private List<Children> childrenList;
    private ChildrenViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_more);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

        rvChildInfo = findViewById(R.id.rv_child_info);
        addMoreTxt = findViewById(R.id.addMoreTxt);
        addMoreBtn = findViewById(R.id.addMoreBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        addMoreBtn.setOnClickListener(v -> {
            Intent i = new Intent(AddMoreActivity.this, ChildInputActivity.class);
            startActivity(i);
            finish();
        });

        rvChildInfo.setHasFixedSize(true);
        rvChildInfo.setLayoutManager(new LinearLayoutManager(this));

        loadChildren();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    private void loadChildren() {
        Query query = childRef.whereEqualTo("accountUid", mUser.getUid());
        childrenList = new ArrayList<>();

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        addMoreTxt.setVisibility(View.GONE);
                        rvChildInfo.setVisibility(View.GONE);
                        Toast.makeText(this, "No children found!", Toast.LENGTH_SHORT).show();
                    } else {
                        addMoreTxt.setVisibility(View.VISIBLE);
                        rvChildInfo.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Children child = documentSnapshot.toObject(Children.class);
                            child.setDocumentId(documentSnapshot.getId());
                            childrenList.add(child);
                        }

                        adapter = new ChildrenViewAdapter(childrenList, this);
                        rvChildInfo.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    addMoreTxt.setVisibility(View.VISIBLE);
                    rvChildInfo.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}