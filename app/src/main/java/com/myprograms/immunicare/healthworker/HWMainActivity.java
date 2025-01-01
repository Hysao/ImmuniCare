package com.myprograms.immunicare.healthworker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.main.HwMainAdapter;
import com.myprograms.immunicare.healthworker.menu.HwMenuActivity;

import java.util.Calendar;
import java.util.List;

public class HWMainActivity extends AppCompatActivity {

    private TabLayout hwMainTab;
    private ViewPager2 hwMainViewPager;
    private HwMainAdapter hwMainAdapter;
    private ImageButton menuBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("history");
    private CollectionReference userRef = db.collection("users");
    private CollectionReference childRef = db.collection("children");
    private DocumentReference childDocRef = childRef.document("childId");
    private DocumentReference userDocRef = userRef.document("userId");

    private TextView totalGivenVaccine, todayGivenVaccine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hwmain);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        hwMainTab = findViewById(R.id.hwMainTab);
        hwMainViewPager = findViewById(R.id.hwMainViewPager);
        totalGivenVaccine = findViewById(R.id.totalGivenVaccine);
        todayGivenVaccine = findViewById(R.id.todayGivenVaccine);
        menuBtn = findViewById(R.id.menuBtn);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HWMainActivity.this, HwMenuActivity.class);
                startActivity(i);
            }
        });

        Query query = historyRef.whereEqualTo("hWorkerId", mUser.getUid());

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int total = 0;
                int today = 0;

                // Get current date to compare with document timestamps
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Object changes = document.get("changes");
                    Long timestamp = document.getLong("timestamp");

                    if (changes instanceof List && timestamp != null) {
                        List<String> changeList = (List<String>) changes;

                        // Check if the change occurred today
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
                totalGivenVaccine.setText(String.valueOf(total));
                todayGivenVaccine.setText(String.valueOf(today));
            } else {
                // Handle potential errors
                totalGivenVaccine.setText("00");
                todayGivenVaccine.setText("00");
            }
        });

        hwMainTab.addTab(hwMainTab.newTab().setText("History"));
        hwMainTab.addTab(hwMainTab.newTab().setText("Scan"));
        hwMainTab.addTab(hwMainTab.newTab().setText("Enter Ref"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        hwMainAdapter = new HwMainAdapter(fragmentManager, getLifecycle());
        hwMainViewPager.setAdapter(hwMainAdapter);

        hwMainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hwMainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        hwMainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                hwMainTab.selectTab(hwMainTab.getTabAt(position));
            }

        });
    }
}