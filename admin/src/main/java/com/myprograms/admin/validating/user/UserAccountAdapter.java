package com.myprograms.admin.validating.user;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserAccountAdapter extends FragmentStateAdapter {

    public UserAccountAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return new UserApprovedFragment();
        } else if (position == 2) {
            return new UserRejectedFragment();
            } else {
            return new UserPendingFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
