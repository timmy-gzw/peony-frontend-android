package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IMineIntegralView;
import com.tftechsz.mine.mvp.presenter.MineIntegralPresenter;
import com.tftechsz.mine.mvp.ui.fragment.IntegralListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的积分
 */
@Route(path = ARouterApi.ACTIVITY_MINE_INTEGRAL_NEW)
public class MineIntegralNewActivity extends BaseMvpActivity<IMineIntegralView, MineIntegralPresenter> implements View.OnClickListener, IMineIntegralView {

    private TextView mTvIntegralNum;   //积分
    private TextView mTvWithdrawNum;  //金币
    private IntegralDto mData;

    @Override
    public MineIntegralPresenter initPresenter() {
        return new MineIntegralPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        new ToolBarBuilder().showBack(true)
                .setTitle(getString(R.string.my_benifit))
                .setTitleColor(R.color.white)
                .setRightText(getString(R.string.benifit_record), this)
                .setRightTextColor(R.color.white)
                .setBackTint(R.color.white)
                .build();
        if (baseTitle != null) {
            baseTitle.setBackgroundResource(0);
        }
        mTvIntegralNum = findViewById(R.id.tv_integral_num);   //积分余额
        mTvWithdrawNum = findViewById(R.id.tv_withdraw_num);

        SlidingTabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.vp_integral);
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.integral_exchange));
        titles.add(getString(R.string.exchange_coin));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(IntegralListFragment.newInstance(IntegralListFragment.TYPE_INTEGRAL_TO_RMB));
        fragments.add(IntegralListFragment.newInstance(IntegralListFragment.TYPE_INTEGRAL_TO_COIN));
        mViewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), fragments, titles));
        tabLayout.setViewPager(mViewPager);
        findViewById(R.id.tv_exchange_record).setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_integral_new;
    }

    @Override
    protected void initData() {
        super.initData();

        p.getIntegral();

        initRxBus();
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                p.getIntegral();
                            }
                        }
                ));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_tv_menu) {//顶部右边文本
            ARouterUtils.toIntegralDetailedActivity(0);
        } else if (id == R.id.tv_intefral_shop) { // btn
            startActivity(IntegralShopActivity.class, "integral", integral);
        } else if (id == R.id.tv_exchange_record) {
            ARouterUtils.toIntegralDetailedActivity(2);
        }
    }

    public String integral;

    @Override
    public void getIntegralSuccess(IntegralDto data) {
        integral = data.integral;
        this.mData = data;
        mTvIntegralNum.setText(data.integral);
        mTvWithdrawNum.setText(data.rmb);
    }
}
