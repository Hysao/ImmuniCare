package com.myprograms.immunicare.calendar;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private ImageButton backBtn;

    private List<Reminder> remindersList = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference remindersRef = db.collection("reminders");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.upcomingEventRecycler);
        backBtn = findViewById(R.id.backBtn);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        backBtn.setOnClickListener(v -> {
            finish();
        });


        fetchEvents();


    }

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
        EventAdapter adapter = new EventAdapter(filteredReminders, CalendarActivity.this);
        recyclerView.setAdapter(adapter);
    }


    private void filterRemindersByDate(String selectedDate) {
        List<Reminder> filteredReminders = new ArrayList<>();
        for (Reminder reminder : remindersList) {
            if (reminder.getDate().equals(selectedDate)) {
                filteredReminders.add(reminder);
            }
        }
        updateRecyclerView(filteredReminders);
    }


    private void highlightReminderDates() {
        // Create a red circle drawable
        ShapeDrawable circleDrawable = new ShapeDrawable(new OvalShape());
        circleDrawable.getPaint().setColor(Color.RED);


        for (Reminder reminder : remindersList) {
            String[] dateParts = reminder.getDate().split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);

            CalendarDay calendarDay = CalendarDay.from(year, month, day);

            // Add the decorator to highlight this date
            calendarView.addDecorator(new RedCircleDecorator(calendarDay, circleDrawable));
        }
    }

}