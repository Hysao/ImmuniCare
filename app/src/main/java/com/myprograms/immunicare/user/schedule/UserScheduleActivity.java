package com.myprograms.immunicare.user.schedule;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UserScheduleActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference scheduleRef = db.collection("schedule");
    private CollectionReference userRef = db.collection("users");

    private ImageButton backBtn;
    private Spinner visitNum;

    private MaterialButton selectDateBtn;
    private MaterialButton requestBtn;

    private CheckBox BgcCheckbox, HepBCheckbox, PentavalentCheckbox,
            OPVCheckbox, IPVCheckbox, PneumococcalCheckbox, MMRCheckbox;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        backBtn = findViewById(R.id.backBtn);
        visitNum = findViewById(R.id.visit);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        requestBtn = findViewById(R.id.requestBtn);
        BgcCheckbox = findViewById(R.id.BgcCheckbox);
        HepBCheckbox = findViewById(R.id.HepBCheckbox);
        PentavalentCheckbox = findViewById(R.id.PentavalentCheckbox);
        OPVCheckbox = findViewById(R.id.OPVCheckbox);
        IPVCheckbox = findViewById(R.id.IPVCheckbox);
        PneumococcalCheckbox = findViewById(R.id.PneumococcalCheckbox);
        MMRCheckbox = findViewById(R.id.MMRCheckbox);


        backBtn.setOnClickListener(v -> finish());

        selectDateBtn.setOnClickListener(v -> {
            showCalendarDialog();
        });

        List<String> visit = Arrays.asList(
                "At Birth", "First Visit", "Second Visit",
                "Third Visit", "Fourth Visit", "Fifth Visit"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                visit
        );

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Request Schedule...");

        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        visitNum.setAdapter(adapter);


        visitNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVisit = visit.get(position);
                resetCheckboxStates();
                updateCheckboxVisibility(selectedVisit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSchedule();
            }
        });

    }

    private void requestSchedule() {
        if (selectDateBtn.getText().toString().equals("Select Date")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mUser.getUid();
        String visit = visitNum.getSelectedItem().toString();
        String date = selectDateBtn.getText().toString();

        Query user = userRef.whereEqualTo("userId", userId);
        user.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                String userName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                String userEmail = queryDocumentSnapshots.getDocuments().get(0).getString("email");


                List<String> selectedVaccines = new ArrayList<>();
                if (BgcCheckbox.isChecked()) selectedVaccines.add("BCG");
                if (HepBCheckbox.isChecked()) selectedVaccines.add("Hepatitis B");
                if (PentavalentCheckbox.isChecked()) selectedVaccines.add("Pentavalent");
                if (OPVCheckbox.isChecked()) selectedVaccines.add("OPV");
                if (IPVCheckbox.isChecked()) selectedVaccines.add("IPV");
                if (PneumococcalCheckbox.isChecked()) selectedVaccines.add("Pneumococcal");
                if (MMRCheckbox.isChecked()) selectedVaccines.add("MMR");


                String vaccineList = String.join(", ", selectedVaccines);
                Toast.makeText(this,
                        "Request Details:\nName: " + userName + "\nVisit: " + visit +
                                "\nDate: " + date + "\nVaccines: " + vaccineList,
                        Toast.LENGTH_LONG).show();


                saveScheduleToFirestore(userId, userName,userEmail, visit, date, selectedVaccines);
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveScheduleToFirestore(String userId, String userName,String userEmail, String visit, String date, List<String> vaccines) {

        progressDialog.show();
        HashMap<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("userId", userId);
        scheduleData.put("userName", userName);
        scheduleData.put("visit", visit);
        scheduleData.put("date", date);
        scheduleData.put("vaccines", vaccines);
        scheduleData.put("userEmail", userEmail);
        scheduleData.put("status", "pending");


        scheduleRef.add(scheduleData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        finish();
                        Toast.makeText(UserScheduleActivity.this, "Schedule Requested Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to request schedule", Toast.LENGTH_SHORT).show());
    }
    private void updateCheckboxVisibility(String visit) {
        switch (visit) {
            case "At Birth":
                setCheckboxVisibility(View.VISIBLE, View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE);
                break;
            case "First Visit":
            case "Second Visit":
                setCheckboxVisibility(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE);
                break;
            case "Third Visit":
                setCheckboxVisibility(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE);
                break;
            case "Fourth Visit":
                setCheckboxVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                break;
            case "Fifth Visit":
                setCheckboxVisibility(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE);
                break;
        }
    }

    private void resetCheckboxStates() {
        BgcCheckbox.setChecked(false);
        HepBCheckbox.setChecked(false);
        PentavalentCheckbox.setChecked(false);
        OPVCheckbox.setChecked(false);
        IPVCheckbox.setChecked(false);
        PneumococcalCheckbox.setChecked(false);
        MMRCheckbox.setChecked(false);
    }

    private void setCheckboxVisibility(int bgc, int hepB, int pentavalent, int opv, int ipv, int pneumococcal, int mmr) {
        BgcCheckbox.setVisibility(bgc);
        HepBCheckbox.setVisibility(hepB);
        PentavalentCheckbox.setVisibility(pentavalent);
        OPVCheckbox.setVisibility(opv);
        IPVCheckbox.setVisibility(ipv);
        PneumococcalCheckbox.setVisibility(pneumococcal);
        MMRCheckbox.setVisibility(mmr);
    }

    private void showCalendarDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            selectDateBtn.setText(selectedDate);
        }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}