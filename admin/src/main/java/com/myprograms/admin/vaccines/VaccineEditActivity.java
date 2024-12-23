package com.myprograms.admin.vaccines;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.R;

import java.util.Calendar;

public class VaccineEditActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vaccinesRef = db.collection("vaccines");

    private TextView vaccineNameTxt;
    private EditText vaccineStock;
    private Button mdfDatePicker, expDatePicker;
    private MaterialButton saveBtn, cancelBtn;

    private String vaccineName;
    private String documentId;

    private String selectedManufacturedDate;
    private String selectedExpiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vaccine_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        vaccineNameTxt = findViewById(R.id.vaccineName);
        vaccineStock = findViewById(R.id.vaccineStock);
        mdfDatePicker = findViewById(R.id.mdfDatePicker);
        expDatePicker = findViewById(R.id.expDatePicker);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Get vaccine name from intent
        vaccineName = getIntent().getStringExtra("vaccineName");
        vaccineNameTxt.setText(vaccineName);

        // Fetch vaccine details from Firestore
        Query query = vaccinesRef.whereEqualTo("name", vaccineName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    documentId = document.getId();

                    // Fetch the numeric stock value and convert it to String
                    Integer stockValue = document.get("stock", Integer.class);
                    if (stockValue != null) {
                        vaccineStock.setText(String.valueOf(stockValue));
                    }

                    // Fetch the manufactured date and expiry date
                    selectedManufacturedDate = document.getString("manufacturedDate");
                    if (selectedManufacturedDate != null) {
                        mdfDatePicker.setText(selectedManufacturedDate);
                    }

                    selectedExpiryDate = document.getString("expiryDate");
                    if (selectedExpiryDate != null) {
                        expDatePicker.setText(selectedExpiryDate);
                    }
                }
            } else {
                // Handle task failure
                Log.e("Firestore Query", "Error fetching document: ", task.getException());
            }
        });

        
        mdfDatePicker.setOnClickListener(v -> showDatePickerDialog(mdfDatePicker, true));
        expDatePicker.setOnClickListener(v -> showDatePickerDialog(expDatePicker, false));

    
        cancelBtn.setOnClickListener(v -> finish());

      
        saveBtn.setOnClickListener(v -> saveChanges());
    }

    private void showDatePickerDialog(Button button, boolean isManufacturedDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    button.setText(date);

                    if (isManufacturedDate) {
                        selectedManufacturedDate = date;
                    } else {
                        selectedExpiryDate = date;
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void saveChanges() {
        if (documentId == null) {
            return; // Handle case where documentId is null (e.g., show error message)
        }

        String stock = vaccineStock.getText().toString().trim();

        // Update Firestore document
        vaccinesRef.document(documentId).update(
                "stock", stock,
                "manufacturedDate", selectedManufacturedDate,
                "expiryDate", selectedExpiryDate
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Save Changes", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        });
    }
}
