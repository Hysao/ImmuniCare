package com.myprograms.immunicare.healthworker.update;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

public class ChildInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private DocumentReference childDoc = childRef.document();

    private String documentId = getIntent().getStringExtra("documentId");

    private TextView infoChildName, infoChildGender,
            infoChildBirthDate, infoChildPlaceBirth,
            infoChildAddress, infoChildBarangay,
            infoChildMother, infoChildFather,
            infoChildWeight, infoChildHeight;

    private Button btnUpdate;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        infoChildName = findViewById(R.id.infoChildName);
        infoChildGender = findViewById(R.id.infoChildGender);
        infoChildBirthDate = findViewById(R.id.infoChildBirthDate);
        infoChildPlaceBirth = findViewById(R.id.infoChildPlaceBirth);
        infoChildAddress = findViewById(R.id.infoChildAddress);
        infoChildBarangay = findViewById(R.id.infoChildBarangay);
        infoChildMother = findViewById(R.id.infoChildMother);
        infoChildFather = findViewById(R.id.infoChildFather);
        infoChildWeight = findViewById(R.id.infoChildWeight);
        infoChildHeight = findViewById(R.id.infoChildHeight);

        btnUpdate = findViewById(R.id.viewRecordBtn);

        DocumentReference childDoc = childRef.document(documentId);

        childDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                String childName = documentSnapshot.getString("childName");
                String childGender = documentSnapshot.getString("childGender");
                String childBirthDate = documentSnapshot.getString("childDateOfBirth");
                String childPlaceBirth = documentSnapshot.getString("childPlaceOfBirth");
                String childAddress = documentSnapshot.getString("childAddress");
                String childBarangay = documentSnapshot.getString("childBarangay");
                String childMother = documentSnapshot.getString("childMotherName");
                String childFather = documentSnapshot.getString("childFatherName");
                String childWeight = documentSnapshot.getString("childWeight");
                String childHeight = documentSnapshot.getString("childHeight");

                infoChildName.setText(childName);
                infoChildGender.setText(childGender);
                infoChildBirthDate.setText(childBirthDate);
                infoChildPlaceBirth.setText(childPlaceBirth);
                infoChildAddress.setText(childAddress);
                infoChildBarangay.setText(childBarangay);
                infoChildMother.setText(childMother);
                infoChildFather.setText(childFather);
                infoChildWeight.setText(childWeight + "kg");
                infoChildHeight.setText(childHeight + "cm");


            }
        });

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ChildInfoActivity.this, ImmunizationUpdateActivity.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
        });

        }



    }