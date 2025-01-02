package com.myprograms.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.admin.announcements.AnnouncementActivity;
import com.myprograms.admin.concerns.ConcernsActivity;
import com.myprograms.admin.records.ImmunizationRecordActivity;
import com.myprograms.admin.vaccines.VaccinesActivity;
import com.myprograms.admin.validating.ValidatingUserActivity;

import java.util.Calendar;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private CardView immunizationRecords, announcements,
            concerns, scheduleAnnouncement, accountVerification, listOfVaccines;
    private TextView totalVaccination, vaccinationGivenToday;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        immunizationRecords = findViewById(R.id.immunizationRecords);
        announcements = findViewById(R.id.announcements);
        concerns = findViewById(R.id.concerns);
        scheduleAnnouncement = findViewById(R.id.scheduleAnnouncement);
        accountVerification = findViewById(R.id.accountVerification);
        totalVaccination = findViewById(R.id.totalVaccination);
        vaccinationGivenToday = findViewById(R.id.vaccinationGivenToday);
        listOfVaccines = findViewById(R.id.listOfVaccines);

        accountVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ValidatingUserActivity.class);
                startActivity(intent);
            }
        });

        announcements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMainActivity.this, AnnouncementActivity.class);
                startActivity(i);
            }
        });

        listOfVaccines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMainActivity.this, VaccinesActivity.class);
                startActivity(i);
            }
        });

        immunizationRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMainActivity.this, ImmunizationRecordActivity.class);
                startActivity(i);
            }
        });

        concerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMainActivity.this, ConcernsActivity.class);
                startActivity(i);
            }
        });


        Query query = historyRef;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int total = 0;
                int today = 0;

                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Object changes = document.get("changes");
                    Long timestamp = document.getLong("timestamp");

                    if (changes instanceof List && timestamp != null) {
                        List<String> changeList = (List<String>) changes;

                        Calendar changeCalendar = Calendar.getInstance();
                        changeCalendar.setTimeInMillis(timestamp);

                        boolean isToday = (changeCalendar.get(Calendar.YEAR) == currentYear &&
                                changeCalendar.get(Calendar.MONTH) == currentMonth &&
                                changeCalendar.get(Calendar.DAY_OF_MONTH) == currentDay);

                        for (String change : changeList) {
                            if (change.contains("is updated to checked")) {
                                total++;
                                if (isToday) {
                                    today++;
                                }
                            }
                        }
                    }
                }

                // Update UI with counts
                totalVaccination.setText(String.valueOf(total));
                vaccinationGivenToday.setText(String.valueOf(today));
            } else {
                // Handle potential errors
                totalVaccination.setText("00");
                vaccinationGivenToday.setText("00");
            }
        });


    }
}