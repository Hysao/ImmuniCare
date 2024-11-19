package com.myprograms.admin.validating;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.myprograms.admin.R;
import com.myprograms.admin.validating.hw.HwAccountAdapter;
import com.myprograms.admin.validating.user.UserAccountAdapter;


public class HwAccountFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private HwAccountAdapter hwAccountAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hw_account, container, false);

        viewPager = view.findViewById(R.id.hwAccountViewPager);
        tabLayout = view.findViewById(R.id.hwAccountTabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Healthcare Worker Accounts"));
//        tabLayout.addTab(tabLayout.newTab().setText("Accepted"));
//        tabLayout.addTab(tabLayout.newTab().setText("Rejected"));

        FragmentManager fragmentManager = getChildFragmentManager();
        hwAccountAdapter = new HwAccountAdapter( fragmentManager, getLifecycle());
        viewPager.setAdapter(hwAccountAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        return view;
    }
}