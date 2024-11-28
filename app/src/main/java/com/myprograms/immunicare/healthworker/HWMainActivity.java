package com.myprograms.immunicare.healthworker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.myprograms.immunicare.R;
import com.myprograms.immunicare.healthworker.main.HwMainAdapter;

public class HWMainActivity extends AppCompatActivity {

    private TabLayout hwMainTab;
    private ViewPager2 hwMainViewPager;
    private HwMainAdapter hwMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hwmain);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hwMainTab = findViewById(R.id.hwMainTab);
        hwMainViewPager = findViewById(R.id.hwMainViewPager);

        hwMainTab.addTab(hwMainTab.newTab().setText("History"));
        hwMainTab.addTab(hwMainTab.newTab().setText("Scan"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        hwMainAdapter = new HwMainAdapter(fragmentManager, getLifecycle());
        hwMainViewPager.setAdapter(hwMainAdapter);

        hwMainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hwMainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        hwMainViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                hwMainTab.selectTab(hwMainTab.getTabAt(position));
            }

        });
    }
}