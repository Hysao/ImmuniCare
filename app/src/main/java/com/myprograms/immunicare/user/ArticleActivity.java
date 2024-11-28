package com.myprograms.immunicare.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.user.articles.ArticleDetailActivity;

public class ArticleActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private Button readArticlesBtn;
    private ImageButton articleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        readArticlesBtn = findViewById(R.id.readArticlesBtn);
        articleBack = findViewById(R.id.articleBack);

        articleBack.setOnClickListener(v -> {

            finish();
        });

        readArticlesBtn.setOnClickListener(v -> {

            Intent intent = new Intent(this, ArticleDetailActivity.class);
            startActivity(intent);

        });
    }
}