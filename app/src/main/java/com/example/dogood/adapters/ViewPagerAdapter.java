package com.example.dogood.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dogood.fragments.AskItemFragment;
import com.example.dogood.fragments.GiveItemFragment;
import com.example.dogood.objects.User;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private User mUser;
    public ViewPagerAdapter(@NonNull FragmentManager fm,User user) {
        super(fm);
        this.mUser = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AskItemFragment(mUser.getAskItems(),mUser);
            case 1:
                return new GiveItemFragment(mUser.getGiveItems(),mUser);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
