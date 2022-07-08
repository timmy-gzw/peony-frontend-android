package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.ui.fragment.IncomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 收益明细
 */
@Route(path = ARouterApi.ACTIVITY_INCOME_DETAIL)
public class IncomeDetailActivity extends BaseMvpActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_income_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("收益明细").build();
        SlidingTabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp_integral);
        List<String> titles = new ArrayList<>();
        titles.add("收益");
        titles.add("兑换");
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(IncomeFragment.newInstance(0));//收益
        fragments.add(IncomeFragment.newInstance(2));//兑换
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        tabLayout.setViewPager(mViewPager);
        tabLayout.setCurrentTab(getIntent().getIntExtra(Interfaces.EXTRA_TYPE, 0));
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

}
