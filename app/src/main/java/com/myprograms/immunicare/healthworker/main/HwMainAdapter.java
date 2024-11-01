package com.myprograms.immunicare.healthworker.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.myprograms.immunicare.auth.HealthWorkerSignUpFragment;
import com.myprograms.immunicare.auth.UserSignUpFragment;

public class HwMainAdapter  extends FragmentStateAdapter {

    public HwMainAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new HistoryFragment();
        }
        return new QRFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
