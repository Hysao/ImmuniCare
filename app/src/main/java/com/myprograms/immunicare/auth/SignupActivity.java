package com.myprograms.immunicare.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.myprograms.immunicare.R;

public class SignupActivity extends AppCompatActivity {

    private MaterialButton userBtn, hwBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userBtn = findViewById(R.id.userBtn);
        hwBtn = findViewById(R.id.hwBtn);

        userBtn.setOnClickListener(v -> showFragment(new UserSignUpFragment()));
        hwBtn.setOnClickListener(v -> showFragment(new HealthWorkerSignUpFragment()));
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                )
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }
}
