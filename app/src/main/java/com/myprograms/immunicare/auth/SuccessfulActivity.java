package com.myprograms.immunicare.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.myprograms.immunicare.R;

public class SuccessfulActivity extends AppCompatActivity {

    private TextView backLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_successful);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backLoginButton = findViewById(R.id.backLoginButton);

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);


        animationView.playAnimation();


        //animationView.cancelAnimation();

        animationView.setSpeed(0.75f); // Speed up

        backLoginButton.setOnClickListener(v -> {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();

        });
    }
}