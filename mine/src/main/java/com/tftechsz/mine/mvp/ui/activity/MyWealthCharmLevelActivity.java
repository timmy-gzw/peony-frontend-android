package com.tftechsz.mine.mvp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GradeLevelDto;
import com.tftechsz.mine.mvp.IView.IGradeIntroduceView;
import com.tftechsz.mine.mvp.presenter.GradeIntroducePresenter;
import com.tftechsz.mine.mvp.ui.fragment.FriendFragment;
import com.tftechsz.mine.mvp.ui.fragment.WealthFragment;

import java.util.ArrayList;
import java.util.List;

public class MyWealthCharmLevelActivity extends BaseMvpActivity<IGradeIntroduceView, GradeIntroducePresenter> implements IGradeIntroduceView {

    private final static String EXTRA_TYPE = "extra_type";
    private final static String EXTRA_USERID = "userId";
    private final static String EXTRA_SEX = "sex";
    private WealthFragment fragment1,fragment2;

    public static void startActivity(Context context, String type, String sex,String userId) {
        Intent intent = new Intent(context, MyWealthCharmLevelActivity.class);
        intent.putExtra(EXTRA_USERID, userId);
        intent.putExtra(EXTRA_SEX, sex);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    public GradeIntroducePresenter initPresenter() {
        return new GradeIntroducePresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_my_wealth_charm_level;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(v->{
            finish();
        });
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        String type = getIntent().getStringExtra(EXTRA_TYPE);
        String sex = getIntent().getStringExtra(EXTRA_SEX);
        String userId = getIntent().getStringExtra(EXTRA_USERID);
        fragment1 = WealthFragment.newInstance("0", userId, sex);
        fragment2 = WealthFragment.newInstance("1",userId,sex);
        fragments.add(fragment1);
        fragments.add(fragment2);
        List<String> titles = new ArrayList<>();
        titles.add("财富等级");
        titles.add("魅力等级");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(type.equals("0")?0:1);
    }

    @Override
    public void getGradeIntroduceSuccess(GradeLevelDto data) {
    }
}