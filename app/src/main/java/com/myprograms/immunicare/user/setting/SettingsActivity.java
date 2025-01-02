package com.myprograms.immunicare.user.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.calendar.AddReminderActivity;
import com.myprograms.immunicare.user.setting.notification.NotificationActivity;


public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView reminderBtn, notificationBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();

        reminderBtn = findViewById(R.id.reminderBtn);
        notificationBtn = findViewById(R.id.notificationBtn);

        reminderBtn.setOnClickListener(v -> {
            Intent i = new Intent(SettingsActivity.this, AddReminderActivity.class);
            startActivity(i);
        });

        notificationBtn.setOnClickListener(v -> {
            Intent i = new Intent(SettingsActivity.this, NotificationActivity.class);
            startActivity(i);
        });

    }
}