package com.myprograms.immunicare.calendar;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;


import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private RecyclerView recyclerView, calendarRecyclerView;
    private ImageButton backBtn;

    private List<Reminder> remindersList = new ArrayList<>();
    private List<String> daysOfMonth = new ArrayList<>();

    private TextView monthName;
    private int currentMonth, currentYear;

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


        recyclerView = findViewById(R.id.upcomingEventRecycler);
        backBtn = findViewById(R.id.backBtn);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthName = findViewById(R.id.monthName);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        for (int i = 1; i <= 31; i++) {
            daysOfMonth.add(String.valueOf(i));
        }

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);



        backBtn.setOnClickListener(v -> {
            finish();
        });

        populateCalendar();

        fetchEvents();

        findViewById(R.id.prevMonthBtn).setOnClickListener(v -> {
            if (currentMonth == 0) {
                currentMonth = 11;
                currentYear--;
            } else {
                currentMonth--;
            }
            populateCalendar();
            Log.d("CalendarActivity", "Month: " + currentMonth + ", Year: " + currentYear);

        });

        findViewById(R.id.nextMonthBtn).setOnClickListener(v -> {
            if (currentMonth == 11) {
                currentMonth = 0;
                currentYear++;
            } else {
                currentMonth++;
            }
            populateCalendar();
            Log.d("CalendarActivity", "Month: " + currentMonth + ", Year: " + currentYear);
        });



    }


    private void populateCalendar() {
        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);


        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, 1);


        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);


        daysOfMonth.clear();


        for (int i = 1; i < firstDayOfMonth; i++) {
            daysOfMonth.add("");
        }


        for (int i = 1; i <= daysInMonth; i++) {
            daysOfMonth.add(String.valueOf(i));
        }


        String monthNameText = new DateFormatSymbols().getMonths()[currentMonth] + " " + currentYear;
        monthName.setText(monthNameText);


        getReminderDates(new OnReminderDatesFetchedListener() {
            @Override
            public void onReminderDatesFetched(List<String> reminderDates) {

                CalendarAdapter adapter = new CalendarAdapter(daysOfMonth, currentDay, reminderDates, currentMonth, currentYear, today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                calendarRecyclerView.setAdapter(adapter);
            }
        });
    }




    private void getReminderDates(final OnReminderDatesFetchedListener listener) {
        List<String> reminderDates = new ArrayList<>();
        remindersRef.whereEqualTo("userId", mUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    remindersList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Reminder reminder = document.toObject(Reminder.class);
                        assert reminder != null;
                        reminderDates.add(reminder.getDate()); // assuming getDate() returns the date in "day/month/year" format
                        remindersList.add(reminder);
                    }
                    // Notify listener once data is fetched
                    listener.onReminderDatesFetched(reminderDates);
                    updateRecyclerView(remindersList); // Show all events initially
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error fetching events", e);
                });
    }

    // Interface for callback when reminder dates are fetched
    public interface OnReminderDatesFetchedListener {
        void onReminderDatesFetched(List<String> reminderDates);
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


                    sortRemindersByDate(remindersList);

                    updateRecyclerView(remindersList);
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error fetching events", e);
                });
    }

    private void updateRecyclerView(List<Reminder> filteredReminders) {
        EventAdapter adapter = new EventAdapter(filteredReminders, CalendarActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void sortRemindersByDate(List<Reminder> remindersList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        remindersList.sort((reminder1, reminder2) -> {
            String date1 = reminder1.getDate();
            String date2 = reminder2.getDate();

            date1 = preprocessDate(date1);
            date2 = preprocessDate(date2);

            LocalDate localDate1 = LocalDate.parse(date1, formatter);
            LocalDate localDate2 = LocalDate.parse(date2, formatter);

            return localDate1.compareTo(localDate2);
        });
    }

    private String preprocessDate(String date) {
        String[] parts = date.split("/");


        if (parts[0].length() == 1) {
            parts[0] = "0" + parts[0];
        }


        if (parts[1].length() == 1) {
            parts[1] = "0" + parts[1];
        }


        return parts[0] + "/" + parts[1] + "/" + parts[2];
    }




}