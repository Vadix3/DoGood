package com.example.dogood.adapters;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.dogood.R;
import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.GiveItemFragment;
import com.example.dogood.objects.AskItem;
import com.example.dogood.objects.User;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private User mUser;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, User user) {
        super(fragmentManager, lifecycle);
        mUser = user;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                AskItemFragment askItemFragment = new AskItemFragment(mUser.getAskItems(), mUser);
                return askItemFragment;
            case 1:
                GiveItemFragment giveItemFragment = new GiveItemFragment(mUser.getGiveItems(), mUser);
                return giveItemFragment;

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
