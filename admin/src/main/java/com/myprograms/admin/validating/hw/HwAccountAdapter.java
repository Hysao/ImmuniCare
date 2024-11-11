package com.myprograms.admin.validating.hw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HwAccountAdapter extends FragmentStateAdapter {

    public HwAccountAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new HwApprovedFragment();
        } else if (position == 2) {
            return new HwRejectedFragment();
            } else {
            return new HwPendingFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
