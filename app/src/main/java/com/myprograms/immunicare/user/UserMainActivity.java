package com.myprograms.immunicare.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.auth.LoginActivity;
import com.myprograms.immunicare.calendar.CalendarActivity;
import com.myprograms.immunicare.calendar.EventAdapter;
import com.myprograms.immunicare.calendar.Reminder;
import com.myprograms.immunicare.user.announcement.AnnouncementAdapter;
import com.myprograms.immunicare.user.announcement.Announcements;
import com.myprograms.immunicare.user.setting.UserMenuActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserMainActivity extends AppCompatActivity {

    private ImageButton btn_menu;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference announcementsRef = db.collection("announcements");

    //Announcements
    private List<Announcements> announcements;
    private AnnouncementAdapter announcementAdapter;
    private RecyclerView announcementRecycler, reminderRecycler;
    private List<Reminder> remindersList = new ArrayList<>();
    private CollectionReference remindersRef = db.collection("reminders");
    private CollectionReference userRef = db.collection("users");
    private DocumentReference userDocRef;

    private TextView dayTxt, userNameTxt;
    private ProgressDialog progressDialog;
    private MaterialButton viewArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_main);
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
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");

        btn_menu = findViewById(R.id.userMenuBtn);
        dayTxt = findViewById(R.id.dayTxt);
        userNameTxt = findViewById(R.id.userNameTxt);

        reminderRecycler = findViewById(R.id.reminderRecycler);

        reminderRecycler.setLayoutManager(new LinearLayoutManager(this));

        fetchEvents();

        //Announcement
        announcementRecycler = findViewById(R.id.announcementList);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this));
        loadAnnouncements();
        //-----------------------//


        btn_menu.setOnClickListener(v -> {
            Intent intent = new Intent(UserMainActivity.this, UserMenuActivity.class);
            startActivity(intent);

        });

        viewArticle = findViewById(R.id.viewArticle);


        viewArticle.setOnClickListener(v -> {
            Intent intent = new Intent(UserMainActivity.this, ArticleActivity.class);
            startActivity(intent);
        });

        userDocRef = userRef.document(mUser.getUid());

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        userNameTxt.setText(userName);
                    }

                    if (Boolean.FALSE.equals(documentSnapshot.getBoolean("terms"))){
                        showTermsAndConditionsDialog();
                    }

                });






        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 18) {
            dayTxt.setText("Good Evening");
        } else if (hour >= 12) {
            dayTxt.setText("Good Afternoon");
        } else { // Before 12 PM
            dayTxt.setText("Good Morning");
        }



    }

    private void showTermsAndConditionsDialog() {
        // Use ScrollView for large text content
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage(getString(R.string.terms_and_conditions)); // Load from strings.xml
        builder.setCancelable(true);
        View termsView = getLayoutInflater().inflate(R.layout.terms_dialog, null);
        //builder.setView(termsView);

        builder.setPositiveButton("Agree", (dialog, which) -> {
            userDocRef.update("terms", true)
                    .addOnSuccessListener(aVoid -> {
                        // Optional: Show a toast or confirmation message
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure (e.g., show a toast or log the error)
                        Toast.makeText(UserMainActivity.this, "Failed to update terms. Please try again.", Toast.LENGTH_SHORT).show();
                    });
            dialog.dismiss();
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> {
            new AlertDialog.Builder(UserMainActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("Disagreeing with the terms will log you out.")
                    .setPositiveButton("Logout", (logoutDialog, logoutWhich) -> {
                        mAuth.signOut();
                        Intent intent = new Intent(UserMainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Ensures the user cannot return to this activity
                    })
                    .setNegativeButton("Back", (logoutDialog, logoutWhich) -> {
                        // Reopen the Terms and Conditions dialog
                        showTermsAndConditionsDialog();
                    })
                    .show();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //OnResume
    @Override
    protected void onResume() {
        super.onResume();

        if (mUser != null){
            String uid = mUser.getUid();
        }else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        loadAnnouncements();
        fetchEvents();

    }

    //Announcement
    private void loadAnnouncements() {
        announcementsRef
                .orderBy("announcementDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                announcements = task.getResult().toObjects(Announcements.class);

                if (announcements == null || announcements.isEmpty()) {
                    announcementRecycler.setVisibility(View.GONE);
                } else {
                    announcementRecycler.setVisibility(View.VISIBLE);
                    announcementAdapter = new AnnouncementAdapter(this, announcements);
                    announcementRecycler.setAdapter(announcementAdapter);
                }
            } else {
                // Handle error

                announcementRecycler.setVisibility(View.GONE);
            }
        });
    }


    //Appointment
    private void fetchEvents() {

        remindersRef.whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    remindersList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Reminder reminder = document.toObject(Reminder.class);
                        remindersList.add(reminder);
                    }
                    updateRecyclerView(remindersList); // Show all events initially
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error fetching events", e);
                });
    }

    private void updateRecyclerView(List<Reminder> filteredReminders) {
        EventAdapter adapter = new EventAdapter(filteredReminders, UserMainActivity.this);
       reminderRecycler.setAdapter(adapter);
    }

}