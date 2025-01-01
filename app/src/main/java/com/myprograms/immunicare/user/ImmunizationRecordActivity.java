package com.myprograms.immunicare.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

public class ImmunizationRecordActivity extends AppCompatActivity {

    private ImageButton back;
    private ImageView aBbcgVaccine, aBHepatitisBVaccine,
            fVpentavalentVaccine, fVOpvVaccine, fVpneumococcalVaccine,
            sVPentavalentVaccine, sVOpvVaccine, sVpneumococcalVaccine,
            tVPentavalentVaccine, tVOpvVaccine, tVinactivatedPolio, tVpneumococcalVaccine,
            foVinactivatedPolio, foVmeasslesMumpsRubella,
            fiVmeasslesMumpsRubella;
    private CardView cardViewBirth, cardViewFirst,
            cardViewSecond, cardViewThird, cardViewFourth,
            cardViewFifth;

    private LinearLayout listContainerAtBirth, listContainerFirst,
            listContainerSecond, listContainerThird, listContainerFourth,
            listContainerFifth;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private DocumentReference childDocRef;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_immunization_record);
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
            return; // Exit activity if no documentId is provided
        }

        back = findViewById(R.id.back);
        initializeWidgets();

        back.setOnClickListener(v -> finish());

        fetchImmunizationRecord();

        cardViewBirth.setOnClickListener(v -> setVisibility(cardViewBirth, listContainerAtBirth));
        cardViewFirst.setOnClickListener(v -> setVisibility(cardViewFirst, listContainerFirst));
        cardViewSecond.setOnClickListener(v -> setVisibility(cardViewSecond, listContainerSecond));
        cardViewThird.setOnClickListener(v -> setVisibility(cardViewThird, listContainerThird));
        cardViewFourth.setOnClickListener(v -> setVisibility(cardViewFourth, listContainerFourth));
        cardViewFifth.setOnClickListener(v -> setVisibility(cardViewFifth, listContainerFifth));


    }

    private void initializeWidgets() {
        // ImageViews
        aBbcgVaccine = findViewById(R.id.aBbcgVaccine);
        aBHepatitisBVaccine = findViewById(R.id.aBHepatitisBVaccine);
        fVpentavalentVaccine = findViewById(R.id.fVpentavalentVaccine);
        fVOpvVaccine = findViewById(R.id.fVOpvVaccine);
        fVpneumococcalVaccine = findViewById(R.id.fVpneumococcalVaccine);
        sVPentavalentVaccine = findViewById(R.id.sVPentavalentVaccine);
        sVOpvVaccine = findViewById(R.id.sVOpvVaccine);
        sVpneumococcalVaccine = findViewById(R.id.sVpneumococcalVaccine);
        tVPentavalentVaccine = findViewById(R.id.tVPentavalentVaccine);
        tVOpvVaccine = findViewById(R.id.tVOpvVaccine);
        tVinactivatedPolio = findViewById(R.id.tVinactivatedPolio);
        tVpneumococcalVaccine = findViewById(R.id.tVpneumococcalVaccine);
        foVinactivatedPolio = findViewById(R.id.foVinactivatedPolio);
        foVmeasslesMumpsRubella = findViewById(R.id.foVmeasslesMumpsRubella);
        fiVmeasslesMumpsRubella = findViewById(R.id.fiVmeasslesMumpsRubella);

        //cardView
        cardViewBirth = findViewById(R.id.cardViewBirth);
        cardViewFirst = findViewById(R.id.cardViewFirst);
        cardViewSecond = findViewById(R.id.cardViewSecond);
        cardViewThird = findViewById(R.id.cardViewThird);
        cardViewFourth = findViewById(R.id.cardViewFourth);
        cardViewFifth = findViewById(R.id.cardViewFifth);

        //LinearLayout
        listContainerAtBirth = findViewById(R.id.listContainerAtBirth);
        listContainerFirst = findViewById(R.id.listContainerFirst);
        listContainerSecond = findViewById(R.id.listContainerSecond);
        listContainerThird = findViewById(R.id.listContainerThird);
        listContainerFourth = findViewById(R.id.listContainerFourth);
        listContainerFifth = findViewById(R.id.listContainerFifth);
    }

    private void fetchImmunizationRecord() {
        childDocRef = childRef.document(documentId);

        childDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve vaccine statuses
                setImage(aBbcgVaccine, documentSnapshot.getBoolean("aBBcgVaccine"));
                setImage(aBHepatitisBVaccine, documentSnapshot.getBoolean("aBHepatitisBVaccine"));
                setImage(fVpentavalentVaccine, documentSnapshot.getBoolean("fVPentavalentVaccine"));
                setImage(fVOpvVaccine, documentSnapshot.getBoolean("fVOpvVaccine"));
                setImage(fVpneumococcalVaccine, documentSnapshot.getBoolean("fVpneumococcalVaccine"));
                setImage(sVPentavalentVaccine, documentSnapshot.getBoolean("sVPentavalentVaccine"));
                setImage(sVOpvVaccine, documentSnapshot.getBoolean("sVOpvVaccine"));
                setImage(sVpneumococcalVaccine, documentSnapshot.getBoolean("sVpneumococcalVaccine"));
                setImage(tVPentavalentVaccine, documentSnapshot.getBoolean("tVPentavalentVaccine"));
                setImage(tVOpvVaccine, documentSnapshot.getBoolean("tVOpvVaccine"));
                setImage(tVinactivatedPolio, documentSnapshot.getBoolean("tVInactivatedVaccine"));
                setImage(tVpneumococcalVaccine, documentSnapshot.getBoolean("tVpneumococcalVaccine"));
                setImage(foVinactivatedPolio, documentSnapshot.getBoolean("foVinactivatedPolio"));
                setImage(foVmeasslesMumpsRubella, documentSnapshot.getBoolean("foVmeasslesMumpsRubella"));
                setImage(fiVmeasslesMumpsRubella, documentSnapshot.getBoolean("fiVmeasslesMumpsRubella"));
            }
        }).addOnFailureListener(e -> {
            // Handle error, e.g., show a message
        });
    }

    private void setImage(ImageView imageView, Boolean vaccineStatus) {
        if (vaccineStatus != null && vaccineStatus) {
            imageView.setBackground(null);
            imageView.setImageResource(R.drawable.vc_check_24_green);
        } else {
            imageView.setBackground(null);
            imageView.setImageResource(R.drawable.vc_none_24_gray);
        }
    }

    private void setVisibility(CardView cardView, LinearLayout listContainer) {
        if (listContainer.getVisibility() == View.VISIBLE) {

            listContainer.setVisibility(View.GONE);
        } else {
            listContainer.setVisibility(View.VISIBLE);
        }
    }
}
