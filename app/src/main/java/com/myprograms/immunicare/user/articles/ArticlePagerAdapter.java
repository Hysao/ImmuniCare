package com.myprograms.immunicare.user.articles;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ArticlePagerAdapter extends FragmentStateAdapter {


    public ArticlePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1){
            return  new HepatitisBFragment();
        } else if (position == 2) {
            return new DptFragment();
        } else if (position == 3) {
            return new OpvFragment();
        } else {
            return new BcgFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
