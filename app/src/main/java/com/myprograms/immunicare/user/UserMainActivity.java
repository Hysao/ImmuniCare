package com.myprograms.immunicare.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.user.announcement.AnnouncementAdapter;
import com.myprograms.immunicare.user.announcement.Announcements;
import com.myprograms.immunicare.user.setting.UserMenuActivity;

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

        //Announcement
        announcementRecycler = findViewById(R.id.announcementList);
        announcementRecycler.setLayoutManager(new LinearLayoutManager(this));
        loadAnnouncements();
        //-----------------------//


        btn_menu.setOnClickListener(v -> {
            Intent intent = new Intent(UserMainActivity.this, UserMenuActivity.class);
            startActivity(intent);

        });



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