package com.myprograms.immunicare.user.articles;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myprograms.immunicare.R;

public class ArticleDetailActivity extends AppCompatActivity {

    private ViewPager2 articleViewPager;
    private TabLayout articleTabLayout;
    private ArticlePagerAdapter articlePagerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        articleTabLayout = findViewById(R.id.articleTab);
        articleViewPager = findViewById(R.id.articleViewPager);

        articleTabLayout.addTab(articleTabLayout.newTab().setText("BCG"));
        articleTabLayout.addTab(articleTabLayout.newTab().setText("HEP B"));
        articleTabLayout.addTab(articleTabLayout.newTab().setText("DPT"));
        articleTabLayout.addTab(articleTabLayout.newTab().setText("OPV"));

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        articlePagerAdapter = new ArticlePagerAdapter(fragmentManager, getLifecycle());
        articleViewPager.setAdapter(articlePagerAdapter);

        articleTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                articleViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        articleViewPager.registerOnPageChangeCallback( new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                articleTabLayout.selectTab(articleTabLayout.getTabAt(position));
            }
        });

    }
}