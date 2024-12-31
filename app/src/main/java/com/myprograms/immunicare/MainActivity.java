package com.myprograms.immunicare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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


//        mapView = findViewById(R.id.mapView);

//        healthCenterLocations = Arrays.asList(
//                new LatLng(14.599512, 120.984222), // Barangay A
//                new LatLng(14.609512, 120.974222)  // Health Center B
//        );
//
//        healthCenterNames = Arrays.asList(
//                "Barangay 389",
//                "San Sebastian Health Center"
//        );
//
//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync((OnMapReadyCallback) MainActivity.this);


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


//    public void onMapReady(GoogleMap googleMap) {
//        // Add markers to the map
//        for (int i = 0; i < healthCenterLocations.size(); i++) {
//            googleMap.addMarker(new MarkerOptions()
//                    .position(healthCenterLocations.get(i))
//                    .title(healthCenterNames.get(i)));
//        }
//
//        // Move the camera to the first location
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(healthCenterLocations.get(0), 12));
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapView.onLowMemory();
//    }



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