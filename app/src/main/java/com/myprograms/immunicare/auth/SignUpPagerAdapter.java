package com.myprograms.immunicare.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SignUpPagerAdapter extends FragmentStateAdapter {
    public SignUpPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new HealthWorkerSignUpFragment();
        }
        return new UserSignUpFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
