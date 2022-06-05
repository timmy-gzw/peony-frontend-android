package com.tftechsz.common.adapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : viewpager2 适配器
 */
public class FragmentVpAdapter2 extends FragmentStateAdapter {
    private List<Fragment> mFragments;

    public FragmentVpAdapter2(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.mFragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
