package com.tftechsz.mine.mvp.ui.activity;


import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.ui.fragment.FriendFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 财富等级/魅力等级
 */
@Route(path = ARouterApi.ACTIVITY_LEVEL_DETAIL)
public class UserLevelActivity extends BaseMvpActivity {

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_user_level;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_FRIEND));
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_WATCH));
        List<String> titles = new ArrayList<>();
        titles.add("财富等级");
        titles.add("魅力等级");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
    }
}