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
import com.myprograms.admin.validating.user.UserAccountAdapter;


public class UserAccountFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private UserAccountAdapter userAccountAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_account, container, false);

        viewPager = view.findViewById(R.id.userAccountViewPager);
        tabLayout = view.findViewById(R.id.userAccountTabLayout);


        tabLayout.addTab(tabLayout.newTab().setText("User Accounts"));
//        tabLayout.addTab(tabLayout.newTab().setText("Accepted"));
//        tabLayout.addTab(tabLayout.newTab().setText("Rejected"));

        FragmentManager fragmentManager = getChildFragmentManager();
        userAccountAdapter = new UserAccountAdapter(fragmentManager,getLifecycle());
        viewPager.setAdapter(userAccountAdapter);

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