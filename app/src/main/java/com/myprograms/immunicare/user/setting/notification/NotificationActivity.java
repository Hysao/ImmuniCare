package com.myprograms.immunicare.user.setting.notification;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.myprograms.immunicare.R;

public class NotificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch notificationSwitch;
    private ImageButton notificationBackBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        notificationSwitch = findViewById(R.id.notificationSwitch);
        notificationBackBtn = findViewById(R.id.notificationBackBtn);

        if (mUser != null) {
            String userId = mUser.getUid();
            DocumentReference userDoc = userRef.document(userId);

            userDoc.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("notification")) {
                    boolean isNotificationEnabled = documentSnapshot.getBoolean("notification");
                    notificationSwitch.setChecked(isNotificationEnabled);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to fetch notification settings.", Toast.LENGTH_SHORT).show();
            });
        }

        // Handle switch toggle
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mUser != null) {
                String userId = mUser.getUid();
                DocumentReference userDoc = userRef.document(userId);

                userDoc.update("notification", isChecked)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Notification preference updated.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update notification preference.", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        notificationBackBtn.setOnClickListener(v -> finish());
    }

}
