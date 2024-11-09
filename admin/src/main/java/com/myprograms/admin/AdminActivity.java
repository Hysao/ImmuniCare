package com.myprograms.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AdminActivity extends AppCompatActivity {

    private EditText userName, password;
    private Button login;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("admin");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);


        login.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(AdminActivity.this, "Put username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query Firestore for admin with matching username and password
            Query query = usersRef.whereEqualTo("username", username)
                    .whereEqualTo("password", pass); // Assuming you store plain text passwords (not recommended)

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        // Admin found, grant access
                        Toast.makeText(AdminActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminActivity.this, AdminMainActivity.class);
                        startActivity(intent);

                    } else {
                        // Invalid credentials
                        Toast.makeText(AdminActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error fetching data from Firestore
                    Toast.makeText(AdminActivity.this, "Error during login", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}