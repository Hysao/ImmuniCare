package com.myprograms.immunicare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myprograms.immunicare.auth.LoginActivity;
import com.myprograms.immunicare.auth.SignupActivity;
import com.myprograms.immunicare.user.announcement.AnnouncementAdapter;
import com.myprograms.immunicare.user.announcement.Announcements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView signUpBtn, loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private RecyclerView announcementRecycler;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference announcementRef = db.collection("announcements");

    private AnnouncementAdapter announcementAdapter;
    private List<Announcements> announcementList;

    private MapView mapView;
    private List<LatLng> healthCenterLocations;
    private List<String> healthCenterNames;
    private MaterialButton visitPage;
    private ImageView facebookPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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

        signUpBtn = findViewById(R.id.signUpButton);
        loginBtn = findViewById(R.id.loginButton);
        announcementRecycler = findViewById(R.id.announcementRecycler);
        visitPage = findViewById(R.id.visitPage);
        facebookPage = findViewById(R.id.facebookPage);

        visitPage.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Open Facebook Page")
                    .setMessage("Do you allow this app to open the Facebook page?")
                    .setPositiveButton("Allow", (dialog, which) -> {

                        String fbPageUrl = "https://www.facebook.com/profile.php?id=100069787952022&mibextid=ZbWKwL";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbPageUrl));
                        startActivity(intent);
                    })
                    .setNegativeButton("Deny", (dialog, which) -> {

                        dialog.dismiss();
                    })
                    .show();
        });



        signUpBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(i);
        });

        loginBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });

        announcementRecycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fetchAnnouncement();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAnnouncement();
    }

    private void fetchAnnouncement() {

        announcementRef
                .orderBy("announcementDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(2)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        announcementList = task.getResult().toObjects(Announcements.class);

                        if (announcementList == null || announcementList.isEmpty()) {
                            announcementRecycler.setVisibility(View.GONE);
                        } else {
                            announcementRecycler.setVisibility(View.VISIBLE);
                            announcementAdapter = new AnnouncementAdapter(this, announcementList);
                            announcementRecycler.setAdapter(announcementAdapter);
                        }
                    } else {
                        // Handle error

                        announcementRecycler.setVisibility(View.GONE);
                    }
                });
    }

}