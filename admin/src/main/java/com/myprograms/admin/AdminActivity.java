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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);


        animationView.playAnimation();


        //animationView.cancelAnimation();

        animationView.setSpeed(1f); // Speed up

        login.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(AdminActivity.this, "Put username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query Firestore for admin with matching username and password
            Query query = usersRef.whereEqualTo("userName", username);
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Assuming you expect only one document with the given username
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    Admin user = document.toObject(Admin.class);

                    // Now you have the user object, you can access its properties
                    assert user != null;
                    String name = user.getUserName();
                    String password1 = user.getPassword();

                    if (!username.equals(name) && !pass.equals(password1)){
                        Toast.makeText(AdminActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent i = new Intent(AdminActivity.this, AdminMainActivity.class);
                        startActivity(i);
                    }

                    // ... access other properties
                } else {
                    // Handle the case where no document is found with the given username
                    Toast.makeText(AdminActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle error
            });



        });

    }
}