package com.myprograms.immunicare.healthworker.update;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.myprograms.immunicare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ImmunizationUpdateActivity extends AppCompatActivity {

    private CheckBox birthBgcCheckbox, birthHepCheckbox,
            firstPentaCheckbox, firstOPVCheckbox, firstPCVCheckbox,
            secondPentaCheckbox, secondOPVCheckbox, secondPCVCheckbox,
            thirdPentaCheckbox, thirdInactivatedCheckbox, thirdOPVCheckbox, thirdPCVCheckbox,
            fourthInactivatedCheckbox, fourthMeaslesCheckbox,
            fifthMeaslesCheckbox;

    private LinearLayout listContainerAtBirth, listContainerFirst,
            listContainerSecond, listContainerThird, listContainerFourth,
            listContainerFifth;

    private CardView cardViewAtBirth,
            cardViewFirst, cardViewSecond,
            cardViewThird, cardViewFourth, cardViewFifth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");
    private DocumentReference childDocRef = childRef.document("childId");

    private CollectionReference historyRef = db.collection("history");
    private CollectionReference userRef = db.collection("users");
    private DocumentReference userDocRef = userRef.document("userId");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private String documentId;

    private MaterialButton saveButton, cancelButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_immunization_update);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();



        initializeCheckboxes();
        saveButton = findViewById(R.id.saveBtn);
        cancelButton = findViewById(R.id.cancelBtn);
        cardViewAtBirth = findViewById(R.id.cardViewAtBirth);
        cardViewFirst = findViewById(R.id.cardViewFirst);
        cardViewSecond = findViewById(R.id.cardViewSecond);
        cardViewThird = findViewById(R.id.cardViewThird);
        cardViewFourth = findViewById(R.id.cardViewFourth);
        cardViewFifth = findViewById(R.id.cardViewFifth);

        listContainerAtBirth = findViewById(R.id.listContainerAtBirth);
        listContainerFirst = findViewById(R.id.listContainerFirst);
        listContainerSecond = findViewById(R.id.listContainerSecond);
        listContainerThird = findViewById(R.id.listContainerThird);
        listContainerFourth = findViewById(R.id.listContainerFourth);
        listContainerFifth = findViewById(R.id.listContainerFifth);

        cardViewAtBirth.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerAtBirth));
        cardViewFirst.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerFirst));
        cardViewSecond.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerSecond));
        cardViewThird.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerThird));
        cardViewFourth.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerFourth));
        cardViewFifth.setOnClickListener(v -> toggleVisibilityWithAnimation(listContainerFifth));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving changes...");


        documentId = getIntent().getStringExtra("documentId");


        fetchChildData();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });


    }

    private void toggleVisibilityWithAnimation(View container) {
        if (container.getVisibility() == View.VISIBLE) {
            container.animate()
                    .alpha(0)
                    .setDuration(300)
                    .withEndAction(() -> container.setVisibility(View.GONE))
                    .start();
        } else {
            container.setVisibility(View.VISIBLE);
            container.setAlpha(0);
            container.animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
        }
    }


    private void initializeCheckboxes() {
        birthBgcCheckbox = findViewById(R.id.birthBgcCheckbox);
        birthHepCheckbox = findViewById(R.id.birthHepCheckbox);
        firstPentaCheckbox = findViewById(R.id.firstPentaCheckbox);
        firstOPVCheckbox = findViewById(R.id.firstOPVCheckbox);
        firstPCVCheckbox = findViewById(R.id.firstPCVCheckbox);
        secondPentaCheckbox = findViewById(R.id.secondPentaCheckbox);
        secondOPVCheckbox = findViewById(R.id.secondOPVCheckbox);
        secondPCVCheckbox = findViewById(R.id.secondPCVCheckbox);
        thirdPentaCheckbox = findViewById(R.id.thirdPentaCheckbox);
        thirdOPVCheckbox = findViewById(R.id.thirdOPVCheckbox);
        thirdPCVCheckbox = findViewById(R.id.thirdPCVCheckbox);
        thirdInactivatedCheckbox = findViewById(R.id.thirdInactivatedCheckbox);
        fourthInactivatedCheckbox = findViewById(R.id.fourthInactivatedCheckbox);
        fourthMeaslesCheckbox = findViewById(R.id.fourthMeaslesCheckbox);
        fifthMeaslesCheckbox = findViewById(R.id.fifthMeaslesCheckbox);
    }

    private void fetchChildData() {
        if (documentId != null) {
            childRef.document(documentId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve data and update UI
                        updateCheckboxes(document);
                    }
                } else {
                    // Handle errors
                }
            });
        }
    }

    private void updateCheckboxes(DocumentSnapshot document) {
        birthBgcCheckbox.setChecked(document.getBoolean("aBBcgVaccine") != null && document.getBoolean("aBBcgVaccine"));
        birthHepCheckbox.setChecked(document.getBoolean("aBHepatitisBVaccine") != null && document.getBoolean("aBHepatitisBVaccine"));
        firstPentaCheckbox.setChecked(document.getBoolean("fVPentavalentVaccine") != null && document.getBoolean("fVPentavalentVaccine"));
        firstOPVCheckbox.setChecked(document.getBoolean("fVOpvVaccine") != null && document.getBoolean("fVOpvVaccine"));
        firstPCVCheckbox.setChecked(document.getBoolean("fVpneumococcalVaccine") != null && document.getBoolean("fVpneumococcalVaccine"));
        secondPentaCheckbox.setChecked(document.getBoolean("sVPentavalentVaccine") != null && document.getBoolean("sVPentavalentVaccine"));
        secondOPVCheckbox.setChecked(document.getBoolean("sVOpvVaccine") != null && document.getBoolean("sVOpvVaccine"));
        secondPCVCheckbox.setChecked(document.getBoolean("sVpneumococcalVaccine") != null && document.getBoolean("sVpneumococcalVaccine"));
        thirdPentaCheckbox.setChecked(document.getBoolean("tVPentavalentVaccine") != null && document.getBoolean("tVPentavalentVaccine"));
        thirdOPVCheckbox.setChecked(document.getBoolean("tVOpvVaccine") != null && document.getBoolean("tVOpvVaccine"));
        thirdPCVCheckbox.setChecked(document.getBoolean("tVpneumococcalVaccine") != null && document.getBoolean("tVpneumococcalVaccine"));
        thirdInactivatedCheckbox.setChecked(document.getBoolean("tVinactivatePolioVaccine") != null && document.getBoolean("tVinactivatePolioVaccine"));
        fourthInactivatedCheckbox.setChecked(document.getBoolean("foVinactivatedPolio") != null && document.getBoolean("foVinactivatedPolio"));
        fourthMeaslesCheckbox.setChecked(document.getBoolean("foVmeasslesMumpsRubella") != null && document.getBoolean("foVmeasslesMumpsRubella"));
        fifthMeaslesCheckbox.setChecked(document.getBoolean("fiVmeasslesMumpsRubella") != null && document.getBoolean("fiVmeasslesMumpsRubella"));

    }

    private void saveChanges() {
        if (documentId != null) {
            // Retrieve updated checkbox states
            boolean birthBgc = birthBgcCheckbox.isChecked();
            boolean birthHep = birthHepCheckbox.isChecked();
            boolean firstPenta = firstPentaCheckbox.isChecked();
            boolean firstOPV = firstOPVCheckbox.isChecked();
            boolean firstPCV = firstPCVCheckbox.isChecked();
            boolean secondPenta = secondPentaCheckbox.isChecked();
            boolean secondOPV = secondOPVCheckbox.isChecked();
            boolean secondPCV = secondPCVCheckbox.isChecked();
            boolean thirdPenta = thirdPentaCheckbox.isChecked();
            boolean thirdOPV = thirdOPVCheckbox.isChecked();
            boolean thirdPCV = thirdPCVCheckbox.isChecked();
            boolean thirdInactivated = thirdInactivatedCheckbox.isChecked();
            boolean fourthInactivated = fourthInactivatedCheckbox.isChecked();
            boolean fourthMeasles = fourthMeaslesCheckbox.isChecked();
            boolean fifthMeasles = fifthMeaslesCheckbox.isChecked();

            progressDialog.show();
            // Fetch the existing data from Firestore to compare changes
            childRef.document(documentId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Compare and identify changes
                        List<String> changes = new ArrayList<>();
                        compareCheckboxChanges(document, changes, "aBBcgVaccine", birthBgc, "birthBgc");
                        compareCheckboxChanges(document, changes, "aBHepatitisBVaccine", birthHep, "birthHep");
                        compareCheckboxChanges(document, changes, "fVPentavalentVaccine", firstPenta, "firstPenta");
                        compareCheckboxChanges(document, changes, "fVOpvVaccine", firstOPV, "firstOPV");
                        compareCheckboxChanges(document, changes, "fVpneumococcalVaccine", firstPCV, "firstPCV");
                        compareCheckboxChanges(document, changes, "sVPentavalentVaccine", secondPenta, "secondPenta");
                        compareCheckboxChanges(document, changes, "sVOpvVaccine", secondOPV, "secondOPV");
                        compareCheckboxChanges(document, changes, "sVpneumococcalVaccine", secondPCV, "secondPCV");
                        compareCheckboxChanges(document, changes, "tVPentavalentVaccine", thirdPenta, "thirdPenta");
                        compareCheckboxChanges(document, changes, "tVOpvVaccine", thirdOPV, "thirdOPV");
                        compareCheckboxChanges(document, changes, "tVpneumococcalVaccine", thirdPCV, "thirdPCV");
                        compareCheckboxChanges(document, changes, "tVinactivatePolioVaccine", thirdInactivated, "thirdInactivated");
                        compareCheckboxChanges(document, changes, "foVinactivatedPolio", fourthInactivated, "fourthInactivated");
                        compareCheckboxChanges(document, changes, "foVmeasslesMumpsRubella", fourthMeasles, "fourthMeasles");
                        compareCheckboxChanges(document, changes, "fiVmeasslesMumpsRubella", fifthMeasles, "fifthMeasles");

                        // Update Firestore with the new data
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("aBBcgVaccine", birthBgc);
                        updatedData.put("aBHepatitisBVaccine", birthHep);
                        updatedData.put("fVPentavalentVaccine", firstPenta);
                        updatedData.put("fVOpvVaccine", firstOPV);
                        updatedData.put("fVpneumococcalVaccine", firstPCV);
                        updatedData.put("sVPentavalentVaccine", secondPenta);
                        updatedData.put("sVOpvVaccine", secondOPV);
                        updatedData.put("sVpneumococcalVaccine", secondPCV);
                        updatedData.put("tVPentavalentVaccine", thirdPenta);
                        updatedData.put("tVOpvVaccine", thirdOPV);
                        updatedData.put("tVpneumococcalVaccine", thirdPCV);
                        updatedData.put("tVinactivatePolioVaccine", thirdInactivated);
                        updatedData.put("foVinactivatedPolio", fourthInactivated);
                        updatedData.put("foVmeasslesMumpsRubella", fourthMeasles);
                        updatedData.put("fiVmeasslesMumpsRubella", fifthMeasles);

                        childRef.document(documentId).update(updatedData).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                // Log changes in history
                                logChangesToHistory(changes);
                            } else {
                                showError("Failed to save changes. Please try again.");
                            }
                        });
                    }
                } else {
                    showError("Failed to fetch current data. Please try again.");
                }
            });
        }
    }

    private void compareCheckboxChanges(DocumentSnapshot document, List<String> changes, String fieldName, boolean newValue, String label) {
        Boolean currentValue = document.getBoolean(fieldName);
        if (currentValue == null || currentValue != newValue) {
            String newState = newValue ? "checked" : "unchecked";
            changes.add(label + " is updated to " + newState);
        }
    }

    private void logChangesToHistory(List<String> changes) {
        if (changes.isEmpty()) {
            showMessage("No changes to save.");
            return;
        }

        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("changes", changes);
        historyEntry.put("timestamp", System.currentTimeMillis());
        historyEntry.put("hWorkerId", mUser.getUid());
        historyEntry.put("childId", documentId);

        AtomicInteger pendingTasks = new AtomicInteger(2);

        // Fetch hWorkerName
        userRef.whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            historyEntry.put("hWorkerName", document.getString("name"));
                            break; // Only fetch the first document
                        }
                    } else {
                        Log.d("Firestore", "No user data found or error occurred.");
                    }
                    checkAndSaveHistory(historyEntry, pendingTasks);
                });

        // Fetch childName
        childRef.document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "Document data: " + document.getData());
                    String childName = document.getString("childName");
                    if (childName != null) {
                        historyEntry.put("childName", childName);
                    } else {
                        Log.d("Firestore", "childName field is null or missing.");
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.e("Firestore", "Error getting document", task.getException());
            }
            checkAndSaveHistory(historyEntry, pendingTasks);
        });

    }

    private void checkAndSaveHistory(Map<String, Object> historyEntry, AtomicInteger pendingTasks) {
        if (pendingTasks.decrementAndGet() == 0) {

            historyRef.add(historyEntry).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showMessage("Changes saved successfully.");
                    finish();
                } else {
                    showError("Failed to log changes. Please try again.");
                }
            });
        }
    }


    private void showMessage(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String error) {

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}