package com.myprograms.immunicare.calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private MaterialButton selectDateBtn, addReminderBtn;
    private EditText title, description;

    private ImageButton backBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference remindersRef = db.collection("reminders");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        selectDateBtn = findViewById(R.id.selectDateBtn);
        addReminderBtn = findViewById(R.id.addReminderBtn);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            finish();
        });


        selectDateBtn.setOnClickListener(v -> {
            showCalendarDialog();
        });

        addReminderBtn.setOnClickListener(v -> {
            String reminderTitle = title.getText().toString().trim();
            String reminderDescription = description.getText().toString().trim();
            String selectedDate = selectDateBtn.getText().toString();
            String userId = (mUser != null) ? mUser.getUid() : null;

            if (reminderTitle.isEmpty()) {
                title.setError("Title is required");
                title.requestFocus();
                return;
            }

            if (reminderDescription.isEmpty()) {
                description.setError("Description is required");
                description.requestFocus();
                return;
            }

            if ("Select Date".equals(selectedDate)) {
                selectDateBtn.setError("Date is required");
                return;
            }

            if (userId == null) {
                
                return;
            }

            Reminder reminder = new Reminder(reminderTitle, reminderDescription, selectedDate, userId);

            remindersRef.add(reminder)
                    .addOnSuccessListener(documentReference -> {
                        Intent i = new Intent(AddReminderActivity.this, CalendarActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error Adding Activity", Toast.LENGTH_SHORT).show();
                    });
        });

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