package com.myprograms.immunicare.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.myprograms.immunicare.user.setting.reminder.Children;

import java.util.List;

public class ChildDataActivity extends AppCompatActivity {

    private TextView dataChildName, dataChildBirthDate,
            dataChildGender, dataChildBirthPlace,
            dataChildAddress, dataChildBarangay,
            dataChildMother, dataChildFather,
            dataChildHeight, dataChildWeight;
    private Button seeQrCode;

    private ImageButton backBtn;

    private String documentId;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");


    private List<Children> childrenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        dataChildName = findViewById(R.id.dataChildName);
        dataChildBirthDate = findViewById(R.id.dataChildBirthDate);
        dataChildGender = findViewById(R.id.dataChildGender);
        dataChildBirthPlace = findViewById(R.id.dataChildBirthPlace);
        dataChildAddress = findViewById(R.id.dataChildAddress);
        dataChildBarangay = findViewById(R.id.dataChildBarangay);
        dataChildMother = findViewById(R.id.dataChildMother);
        dataChildFather = findViewById(R.id.dataChildFather);
        dataChildHeight = findViewById(R.id.dataChildHeight);
        dataChildWeight = findViewById(R.id.dataChildWeight);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        seeQrCode = findViewById(R.id.seeQrCode);

        seeQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChildDataActivity.this, ChildQRCodeActivity.class);
                i.putExtra("documentId", documentId);
                startActivity(i);
            }
        });


        documentId = getIntent().getStringExtra("documentId");

        assert documentId != null;
        DocumentReference documentRef = childRef.document(documentId);

        documentRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Children child = documentSnapshot.toObject(Children.class);

                if (child != null) {
                    dataChildName.setText(child.getChildName());
                    dataChildBirthDate.setText(child.getChildDateOfBirth());
                    dataChildGender.setText(child.getChildGender());
                    dataChildBirthPlace.setText(child.getChildPlaceOfBirth());
                    dataChildAddress.setText(child.getChildAddress());
                    dataChildBarangay.setText(child.getChildBarangay());
                    dataChildMother.setText(child.getChildMotherName());
                    dataChildFather.setText(child.getChildFatherName());
                    dataChildHeight.setText(String.format("%s cm", child.getChildHeight()));
                    dataChildWeight.setText(String.format("%s kg", child.getChildWeight()));

                }


            }
        });


    }
}
