package com.myprograms.immunicare.healthworker.update;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

public class ChildInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView infoChildName, infoChildGender, infoChildBirthDate,
            infoChildPlaceBirth, infoChildAddress, infoChildBarangay,
            infoChildMother, infoChildFather, infoChildWeight, infoChildHeight;
    private ImageView childPhoto;

    private Button btnUpdate, editBtn;
    private String documentId;
    private ImageButton backBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);


        mAuth = FirebaseAuth.getInstance();


        documentId = getIntent().getStringExtra("documentId");


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
        editBtn = findViewById(R.id.editInfo);
        backBtn = findViewById(R.id.backBtn);
        childPhoto = findViewById(R.id.childPhoto);

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChildInfoActivity.this, HwEditChildInfoActivity.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
        });


        backBtn.setOnClickListener(v -> {
            finish();
        });

        fetchChildInfo();

//        DocumentReference childDoc = db.collection("children").document(documentId);
//
//        childDoc.get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        infoChildName.setText(documentSnapshot.getString("childName"));
//                        infoChildGender.setText(documentSnapshot.getString("childGender"));
//                        infoChildBirthDate.setText(documentSnapshot.getString("childDateOfBirth"));
//                        infoChildPlaceBirth.setText(documentSnapshot.getString("childPlaceOfBirth"));
//                        infoChildAddress.setText(documentSnapshot.getString("childAddress"));
//                        infoChildBarangay.setText(documentSnapshot.getString("childBarangay"));
//                        infoChildMother.setText(documentSnapshot.getString("childMotherName"));
//                        infoChildFather.setText(documentSnapshot.getString("childFatherName"));
//                        infoChildWeight.setText(documentSnapshot.getString("childWeight") + "kg");
//                        infoChildHeight.setText(documentSnapshot.getString("childHeight") + "cm");
//
//                        String base64Image = documentSnapshot.getString("childPhoto");
//                        if (base64Image != null && !base64Image.isEmpty()) {
//                            try {
//                                byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
//                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//                                childPhoto.setImageBitmap(decodedBitmap);
//                            } catch (IllegalArgumentException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(this, "Child data not found", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Handle update button click
        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ChildInfoActivity.this, ImmunizationUpdateActivity.class);
            intent.putExtra("documentId", documentId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchChildInfo();
    }

    private void fetchChildInfo(){

        DocumentReference childDoc = db.collection("children").document(documentId);

        childDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        infoChildName.setText(documentSnapshot.getString("childName"));
                        infoChildGender.setText(documentSnapshot.getString("childGender"));
                        infoChildBirthDate.setText(documentSnapshot.getString("childDateOfBirth"));
                        infoChildPlaceBirth.setText(documentSnapshot.getString("childPlaceOfBirth"));
                        infoChildAddress.setText(documentSnapshot.getString("childAddress"));
                        infoChildBarangay.setText(documentSnapshot.getString("childBarangay"));
                        infoChildMother.setText(documentSnapshot.getString("childMotherName"));
                        infoChildFather.setText(documentSnapshot.getString("childFatherName"));
                        infoChildWeight.setText(documentSnapshot.getString("childWeight") + "kg");
                        infoChildHeight.setText(documentSnapshot.getString("childHeight") + "cm");

                        String base64Image = documentSnapshot.getString("childPhoto");
                        if (base64Image != null && !base64Image.isEmpty()) {
                            try {
                                byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                childPhoto.setImageBitmap(decodedBitmap);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Child data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}
