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

import com.google.android.material.tabs.TabLayout;
import com.myprograms.immunicare.R;

public class SignupActivity extends AppCompatActivity {

    private TabLayout signUpTab;
    private ViewPager2 viewPager2;
    private SignUpPagerAdapter signUpPagerAdapter;


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

        signUpTab = findViewById(R.id.tab_signup);
        viewPager2 = findViewById(R.id.signup_pager);

        signUpTab.addTab(signUpTab.newTab().setText("User"));
        signUpTab.addTab(signUpTab.newTab().setText("Health Worker"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        signUpPagerAdapter = new SignUpPagerAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(signUpPagerAdapter);

        signUpTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                signUpTab.selectTab(signUpTab.getTabAt(position));
            }
        });

    }
}