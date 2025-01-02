package com.myprograms.immunicare.user;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.user.announcement.AnnouncementAdapter;
import com.myprograms.immunicare.user.announcement.Announcements;
import com.myprograms.immunicare.user.setting.UserMenuActivity;

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
    private RecyclerView announcementRecycler;
    private CollectionReference userRef = db.collection("users");

    private TextView dayTxt, userNameTxt;

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
        mAuth.getCurrentUser();

        btn_menu = findViewById(R.id.userMenuBtn);
        dayTxt = findViewById(R.id.dayTxt);
        userNameTxt = findViewById(R.id.userNameTxt);

        //Announcement
        announcementRecycler = findViewById(R.id.announcementList);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this));
        loadAnnouncements();
        //-----------------------//


        btn_menu.setOnClickListener(v -> {
            Intent intent = new Intent(UserMainActivity.this, UserMenuActivity.class);
            startActivity(intent);

        });



//        Query query1 = userRef.whereEqualTo("userId", mUser.getUid());
//
//        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                userNameTxt.setText(queryDocumentSnapshots.getDocuments().get(0).get("name").toString());
//            }
//        });



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


    //OnResume
    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();
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


}