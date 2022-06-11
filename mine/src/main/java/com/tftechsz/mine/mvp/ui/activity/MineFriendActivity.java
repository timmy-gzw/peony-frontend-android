package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.ui.fragment.FriendFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * 我的好友，关注，粉丝
 */
public class MineFriendActivity extends BaseMvpActivity implements View.OnClickListener {

    public static final String TYPE_MINE_FRIEND = "friend_type";
    public static final int TYPE_FRIEND = 0;
    public static final int TYPE_WATCH = 1;
    public static final int TYPE_FANS = 2;

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_FRIEND));
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_WATCH));
        fragments.add(FriendFragment.newInstance(FriendFragment.TYPE_FANS));
        List<String> titles = new ArrayList<>();
        titles.add("好友");
        titles.add("关注");
        titles.add("粉丝");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(getIntent().getIntExtra(TYPE_MINE_FRIEND, 0));

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_friend;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all)
            finish();
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }
}