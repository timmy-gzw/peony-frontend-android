package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.tftechsz.family.R;
import com.tftechsz.family.mvp.IView.IApplyVerifyView;
import com.tftechsz.family.mvp.presenter.ApplyVerifyPresenter;
import com.tftechsz.family.mvp.ui.fragment.ApplyVerifyFragment;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请审核
 */

@Route(path = ARouterApi.ACTIVITY_FAMILY_APPLY)
public class ApplyVerifyActivity extends BaseMvpActivity<IApplyVerifyView, ApplyVerifyPresenter> implements IApplyVerifyView {

    public int mPosition = 0;

    @Override
    public ApplyVerifyPresenter initPresenter() {
        return new ApplyVerifyPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        int familyId = getIntent().getIntExtra("familyId", 0);
        new ToolBarBuilder().showBack(true)
                .setRightText("忽略全部", v -> {
                    if (mPosition == 0) {
                        p.ignoreApply(familyId, "join");
                    } else {
                        p.ignoreApply(familyId, "leave");
                    }
                })
                .build();
        SlidingScaleTabLayout mTabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ApplyVerifyFragment.newInstance(ApplyVerifyFragment.TYPE_JOIN, familyId));
        fragments.add(ApplyVerifyFragment.newInstance(ApplyVerifyFragment.TYPE_LEAVE, familyId));
        List<String> titles = new ArrayList<>();
        titles.add("加入申请");
        titles.add("退出申请");
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_apply_verify;
    }

    @Override
    public void ignoreApplySuccess(Boolean data) {
        if (data) {
            toastTip("操作成功");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_APPLY_LIST));
        }

    }
}
