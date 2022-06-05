package com.tftechsz.common.adapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentVpAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragmentList;
    private final List<String> titles;


    public FragmentVpAdapter(FragmentManager fm, List<Fragment> l) {
        this(fm, l, null);
    }

    public FragmentVpAdapter(FragmentManager fm, List<Fragment> l, List<String> titls) {
        super(fm);
        fragmentList = l;
        titles = titls;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (null != titles && titles.size() > 0) {
            return titles.get(position);
        } else
            return super.getPageTitle(position);
    }
}
