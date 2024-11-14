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

import com.myprograms.admin.validating.ValidatingUserActivity;

public class AdminMainActivity extends AppCompatActivity {

    private CardView immunizationRecords, announcements, concerns, scheduleAnnouncement, accountVerification;
    private TextView totalVaccination, vaccinationGivenToday;

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

        accountVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ValidatingUserActivity.class);
                startActivity(intent);
            }
        });


    }
}