package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.ui.fragment.GuardFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 守护
 */
public class GuardActivity  extends BaseMvpActivity implements View.OnClickListener {
    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(GuardFragment.newInstance(GuardFragment.TYPE_WATCH));
        fragments.add(GuardFragment.newInstance(GuardFragment.TYPE_FANS));
        List<String> titles = new ArrayList<>();
        titles.add("我守护");
        titles.add("守护我");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_guard;
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
