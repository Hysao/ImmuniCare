package com.myprograms.immunicare.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class ChildInputActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button submitButton, dateButton, cancelButton;
    private EditText childName, childPlaceOfBirth, childAddress, childMotherName, childFatherName,
            childHeight, childWeight, childBarangay;
    private RadioButton male, female;

   private ImageView childPhoto;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference childRef = db.collection("children");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_child_input);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        submitButton = findViewById(R.id.submitBtn);
        cancelButton = findViewById(R.id.cancelBtn);
        childName = findViewById(R.id.childName);
        childPlaceOfBirth = findViewById(R.id.childPlaceOfBirth);
        childAddress = findViewById(R.id.childAddress);
        childMotherName = findViewById(R.id.childMotherName);
        childFatherName = findViewById(R.id.childFatherName);
        childHeight = findViewById(R.id.childHeight);
        childWeight = findViewById(R.id.childWeight);
        childBarangay = findViewById(R.id.childBarangay);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        dateButton.setText(getTodaysDate());

        cancelButton.setOnClickListener(v -> finish());

        submitButton.setOnClickListener(v -> addChild(
                childName.getText().toString(),
                male.isChecked() ? "Male" : "Female",
                childPlaceOfBirth.getText().toString(),
                dateButton.getText().toString(),
                childAddress.getText().toString(),
                childMotherName.getText().toString(),
                childFatherName.getText().toString(),
                childHeight.getText().toString(),
                childWeight.getText().toString(),
                childBarangay.getText().toString()
        ));
    }

    private void addChild(
            String childName,
            String childGender,
            String childPlaceOfBirth,
            String childDateOfBirth,
            String childAddress,
            String childMotherName,
            String childFatherName,
            String childHeight,
            String childWeight,
            String childBarangay
    ) {

        if (childName.isEmpty() || childPlaceOfBirth.isEmpty() || childDateOfBirth.isEmpty() || childAddress.isEmpty() ||
                childMotherName.isEmpty() || childFatherName.isEmpty() || childHeight.isEmpty() || childWeight.isEmpty() ||
                childBarangay.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        
        int randomNum1 = 1000 + random.nextInt(9000);
        int randomNum2 = 100 + random.nextInt(900);
        char randomLetter1 = (char) ('A' + random.nextInt(26));
        char randomLetter2 = (char) ('A' + random.nextInt(26));
        String childRefNumber = "IMMUNI" + randomNum1 + randomNum2 + randomLetter1 + randomLetter2;

        HashMap<String, Object> child = new HashMap<>();
        child.put("accountUid", mUser.getUid());
        child.put("childName", childName);
        child.put("childGender", childGender);
        child.put("childPlaceOfBirth", childPlaceOfBirth);
        child.put("childDateOfBirth", childDateOfBirth);
        child.put("childAddress", childAddress);
        child.put("childMotherName", childMotherName);
        child.put("childFatherName", childFatherName);
        child.put("childHeight", childHeight);
        child.put("childWeight", childWeight);
        child.put("childBarangay", childBarangay);

        // Default vaccine status
        String[] vaccines = {"aBBcgVaccine", "aBHepatitisBVaccine", "fVPentavalentVaccine", "fVOpvVaccine",
                "fVpneumococcalVaccine", "sVPentavalentVaccine", "sVOpvVaccine", "sVpneumococcalVaccine",
                "tVPentavalentVaccine", "tVOpvVaccine", "tVinnactivatePolioVaccine", "tVpneumococcalVaccine",
                "foVinactivatedPolio", "foVmeasslesMumpsRubella", "fiVmeasslesMumpsRubella"};

        for (String vaccine : vaccines) {
            child.put(vaccine, false);
        }

        childRef.document(childRefNumber).set(child)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ChildInputActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ChildInputActivity.this, UserMainActivity.class);
                    startActivity(i);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildInputActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        return months[month - 1];
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}
