package com.myprograms.admin.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myprograms.admin.AdminActivity;
import com.myprograms.admin.R;

public class AdminMenuActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference adminRef = db.collection("admin");
    private ImageButton backBtn;
    private TextView changePassword, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        changePassword = findViewById(R.id.changePassword);
        logout = findViewById(R.id.logout);


        backBtn.setOnClickListener(v -> finish());


        changePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(AdminMenuActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Toast.makeText(AdminMenuActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminMenuActivity.this, AdminActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog
                            dialog.dismiss();
                        })
                        .show();
            }
        });


    }
}