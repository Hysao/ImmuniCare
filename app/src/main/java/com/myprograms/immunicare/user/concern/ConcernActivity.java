package com.myprograms.immunicare.user.concern;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;

public class ConcernActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private EditText fullNameConcern, emailConcern, titleConcern, descConcern;
    private Button submitBtn;
    private ImageButton backBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference concernRef = db.collection("concern");
    private CollectionReference userRef = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_concern);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        fullNameConcern = findViewById(R.id.fullNameConcern);
        emailConcern = findViewById(R.id.emailConcern);
        titleConcern = findViewById(R.id.titleConcern);
        descConcern = findViewById(R.id.descConcern);
        submitBtn = findViewById(R.id.submitBtn);
        backBtn = findViewById(R.id.concernBackBtn);


        Query userQ = userRef.whereEqualTo("userId", mUser.getUid());

        userQ.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                fullNameConcern.setText(queryDocumentSnapshots.getDocuments().get(0).get("name").toString());
                emailConcern.setText(queryDocumentSnapshots.getDocuments().get(0).get("email").toString());

            }
        });


        backBtn.setOnClickListener(v -> finish());

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitConcern();

            }
        });

    }

    private void submitConcern() {
        // Get input data
        String fullName = fullNameConcern.getText().toString().trim();
        String email = emailConcern.getText().toString().trim();
        String title = titleConcern.getText().toString().trim();
        String desc = descConcern.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Concern object
        Concern concern = new Concern(
                fullName,
                email,
                title,
                desc,
                mUser.getUid(),
                Timestamp.now()
        );

        concernRef.add(concern).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Concern submitted successfully", Toast.LENGTH_SHORT).show();
            titleConcern.setText("");
            descConcern.setText("");
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to submit concern: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}