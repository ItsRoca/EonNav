package com.example.eonnav;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DetailPagerAdapter extends FragmentStateAdapter {

    private final Bundle data;

    public DetailPagerAdapter(@NonNull DetailActivity fa, Bundle data) {
        super(fa);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DetailDescriptionFragment();
                break;
            case 1:
                fragment = new DetailDataFragment();
                break;
            case 2:
                fragment = new DetailFamilyFragment();
                break;
            case 3:
                fragment = new DetailMovesFragment();
                break;
            case 4:
                fragment = new DetailBattleInfoFragment();
                break;
            default:
                fragment = new DetailDescriptionFragment();
                break;
        }
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
