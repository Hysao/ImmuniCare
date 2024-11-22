package com.myprograms.immunicare.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

public class AddMoreActivity extends AppCompatActivity {

    private RecyclerView rvChildInfo;
    private TextView addMoreTxt;
    private Button addMoreBtn;
    private ImageButton backBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private CollectionReference userRef = db.collection("users");


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
        mAuth.getCurrentUser();


        rvChildInfo = findViewById(R.id.rv_child_info);
        addMoreTxt = findViewById(R.id.addMoreTxt);
        addMoreBtn = findViewById(R.id.addMoreBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        addMoreBtn.setOnClickListener(v -> {
            Intent i = new Intent(AddMoreActivity.this, ChildInputActivity.class);
            startActivity(i);
            finish();
        });

    }
}