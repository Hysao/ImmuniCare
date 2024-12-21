package com.myprograms.admin.vaccines;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccineAddActivity extends AppCompatActivity {

    private Button mdfDatePicker;
    private Button expDatePicker;
    private MaterialButton saveBtn, cancelBtn;
    private EditText vaccineStock;
    private Spinner vaccineSpinner;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vaccinesRef = db.collection("vaccines");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_add);

        mdfDatePicker = findViewById(R.id.mdfDatePicker);
        expDatePicker = findViewById(R.id.expDatePicker);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        vaccineStock = findViewById(R.id.vaccineStock);
        vaccineSpinner = findViewById(R.id.vaccineSpinner);

        cancelBtn.setOnClickListener(v -> finish());

        // Set up vaccine list in Spinner
        List<String> vaccineList = new ArrayList<>();
        vaccineList.add("BCG Vaccine");
        vaccineList.add("Hepatitis B Vaccine");
        vaccineList.add("Pentavalent Vaccine");
        vaccineList.add("Oral Polio Vaccine");
        vaccineList.add("Inactivated Polio Vaccine");
        vaccineList.add("Pneumococcal Conjugate Vaccine");
        vaccineList.add("Measles, Mumps, Rubella (MMR) Vaccine");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                vaccineList
        );

        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        vaccineSpinner.setAdapter(adapter);

        mdfDatePicker.setOnClickListener(v -> showDatePickerDialog(mdfDatePicker));
        expDatePicker.setOnClickListener(v -> showDatePickerDialog(expDatePicker));


        saveBtn.setOnClickListener(v -> saveVaccine());
    }

    private void showDatePickerDialog(Button buttonToUpdate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    @SuppressLint("DefaultLocale")
                    String date = String.format("%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                    buttonToUpdate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void saveVaccine() {

        String vaccineName = vaccineSpinner.getSelectedItem().toString();
        String stock = vaccineStock.getText().toString();
        String manufacturedDate = mdfDatePicker.getText().toString();
        String expiryDate = expDatePicker.getText().toString();


        if (stock.isEmpty() || manufacturedDate.equals("JAN 15, 2023") || expiryDate.equals("Add Vaccine")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> vaccineData = new HashMap<>();
        vaccineData.put("name", vaccineName);
        vaccineData.put("stock", Integer.parseInt(stock));
        vaccineData.put("manufacturedDate", manufacturedDate);
        vaccineData.put("expiryDate", expiryDate);


        vaccinesRef.add(vaccineData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Vaccine added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add vaccine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

