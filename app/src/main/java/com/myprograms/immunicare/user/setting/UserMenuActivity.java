package com.myprograms.immunicare.user.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myprograms.immunicare.MainActivity;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.auth.LoginActivity;
import com.myprograms.immunicare.calendar.CalendarActivity;
import com.myprograms.immunicare.healthworker.menu.HwMenuActivity;
import com.myprograms.immunicare.user.AddMoreActivity;
import com.myprograms.immunicare.user.ArticleActivity;
import com.myprograms.immunicare.user.ImmunizationRecordActivity;
import com.myprograms.immunicare.user.ImmunizationRecordListActivity;
import com.myprograms.immunicare.user.concern.ConcernActivity;

public class UserMenuActivity extends AppCompatActivity {


    private TextView menuChildProfile,
            menuImmunizationRecord, menuArticles, menuCalendar,
            menuConcern, menuSettings, menuLogout;

    private Intent i;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null){
            String uid = mUser.getUid();
        }else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }


        menuChildProfile = findViewById(R.id.menuChildProfile);
        menuImmunizationRecord = findViewById(R.id.menuImmunizationRecord);
        menuArticles = findViewById(R.id.menuArticles);
        menuCalendar = findViewById(R.id.menuCalendar);
        menuConcern = findViewById(R.id.menuConcern);
        menuSettings = findViewById(R.id.menuSettings);
        menuLogout = findViewById(R.id.menuLogout);

        menuChildProfile.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, AddMoreActivity.class);
            startActivity(i);
            finish();
        });

        menuImmunizationRecord.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, ImmunizationRecordListActivity.class);
            startActivity(i);
            finish();
        });

        menuArticles.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, ArticleActivity.class);
            startActivity(i);
            finish();
        });

        menuCalendar.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, CalendarActivity.class);
            startActivity(i);
            finish();
        });

        menuConcern.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, ConcernActivity.class);
            startActivity(i);
            finish();
        });

        menuSettings.setOnClickListener(v -> {
            i = new Intent(UserMenuActivity.this, SettingsActivity.class);
            startActivity(i);
            finish();
        });

        menuLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

    }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(UserMenuActivity.this, MainActivity.class);
                mAuth.signOut();
                startActivity(i);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}