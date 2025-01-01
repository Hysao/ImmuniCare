package com.myprograms.immunicare.healthworker.update;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HwEditChildInfoActivity extends AppCompatActivity {

    private EditText weightInput, heightInput;
    private MaterialButton cancelButton, saveButton;
    private TextView childName, childId, weight, height;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private CollectionReference childRef = db.collection("children");
    private CollectionReference historyRef = db.collection("history");
    private CollectionReference userRef = db.collection("users");

    private String documentId;
    private ProgressDialog progressDialog;
    private String hWorkerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hw_edit_child_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        childName = findViewById(R.id.childName);
        childId = findViewById(R.id.childId);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);

        documentId = getIntent().getStringExtra("documentId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving changes...");

        fetchHealthWorkerName();
        loadChildData();

        cancelButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void fetchHealthWorkerName() {
        userRef.whereEqualTo("userId", mUser.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        hWorkerName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch worker name: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadChildData() {
        DocumentReference childDoc = childRef.document(documentId);

        childDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        childName.setText(documentSnapshot.getString("childName"));
                        childId.setText(documentId);
                        weight.setText(documentSnapshot.getString("childWeight"));
                        height.setText(documentSnapshot.getString("childHeight"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveChanges() {
        String newWeight = weightInput.getText().toString();
        String newHeight = heightInput.getText().toString();

        if (newWeight.isEmpty() || newHeight.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        DocumentReference childDoc = childRef.document(documentId);

        childDoc.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String oldWeight = documentSnapshot.getString("childWeight");
                        String oldHeight = documentSnapshot.getString("childHeight");

                        // Update child data
                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("childWeight", newWeight);
                        updatedData.put("childHeight", newHeight);

                        childDoc.update(updatedData)
                                .addOnSuccessListener(aVoid -> {
                                    // Create history record
                                    List<String> changes = new ArrayList<>();
                                    if (!newWeight.equals(oldWeight)) {
                                        changes.add("Weight updated from " + oldWeight + " to " + newWeight);
                                    }
                                    if (!newHeight.equals(oldHeight)) {
                                        changes.add("Height updated from " + oldHeight + " to " + newHeight);
                                    }

                                    Map<String, Object> historyData = new HashMap<>();
                                    historyData.put("hWorkerId", mUser.getUid());
                                    historyData.put("childId", documentId);
                                    historyData.put("childName", childName.getText().toString());
                                    historyData.put("hWorkerName", hWorkerName);
                                    historyData.put("timestamp", System.currentTimeMillis());
                                    historyData.put("changes", changes);

                                    historyRef.add(historyData)
                                            .addOnSuccessListener(historyDoc -> {
                                                progressDialog.dismiss();
                                                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressDialog.dismiss();
                                                Toast.makeText(this, "Failed to save history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "Failed to update child data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Child record does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error fetching child record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
