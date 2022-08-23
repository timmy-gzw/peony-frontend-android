package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.robinhood.ticker.TickerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.PaymentDto;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.EmptyView;
import com.tftechsz.common.widget.pop.BasePayPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.RechargeAdapter;
import com.tftechsz.mine.mvp.IView.IChargePayView;
import com.tftechsz.mine.mvp.presenter.ChargePayPresenter;

/**
 * 金币充值列表
 */
@Route(path = ARouterApi.ACTIVITY_CHARGE_LIST_NEW)
public class ChargeListNewActivity extends BaseMvpActivity<IChargePayView, ChargePayPresenter> implements IChargePayView {

    private TickerView mTvCoin;
    private RechargeAdapter mAdapter;
    private SmartRefreshLayout smartRefreshLayout;
    private int form_type;
    private IntegralDto mCoinBean;
    private RechargeDto mRechargeBean;
    private BasePayPopWindow payPopWindow;

    @Override
    public ChargePayPresenter initPresenter() {
        return new ChargePayPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_charge_list_new;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentBar().navigationBarDarkIcon(false).statusBarDarkFont(false, 0.2f).init();
        new ToolBarBuilder().showBack(true)
                .setTitle(getString(R.string.coin_recharge))
                .setTitleColor(R.color.white)
                .setRightText(getString(R.string.income_and_expenditude_records), v -> ARouterUtils.toIntegralDetailedActivity(1))
                .setRightTextColor(R.color.white)
                .setBackgroundColor(0)
                .setBackTint(R.color.white)
                .build();
        mTvCoin = findViewById(R.id.tv_coin_num);
        smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        smartRefreshLayout.setBackgroundResource(R.color.white);
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> getRechargeInfo());
        RecyclerView rvCoins = findViewById(R.id.recycleview);
        rvCoins.setLayoutManager(new LinearLayoutManager(this));
        rvCoins.addItemDecoration(new SpacingDecoration(0, ConvertUtils.dp2px(10f), true));
        rvCoins.setPadding(ConvertUtils.dp2px(16f), 0, ConvertUtils.dp2px(16f), ConvertUtils.dp2px(16f));
        mAdapter = new RechargeAdapter();
        mAdapter.setEmptyView(new EmptyView(this));
        rvCoins.setAdapter(mAdapter);
        mTvCoin.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN-Bold.ttf"));
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (Utils.isPayTypeInFamily(form_type)) {
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("充值界面列表点击", "click", "charge_list_item",
                                JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto("", mAdapter.getItem(position).coin))), null);
            }
            mRechargeBean = mAdapter.getItem(position);
            if (payPopWindow == null) {
                payPopWindow = new BasePayPopWindow(this);
            }
            if (mRechargeBean == null) return;
            payPopWindow.setTypeId(mRechargeBean.id);
            payPopWindow.setPayInfo(mRechargeBean.rmb, mRechargeBean.coin);
            payPopWindow.showPopupWindow();
        });
        initRxBus();
    }

    @Override
    protected void initData() {
        String coin = getIntent().getStringExtra(Interfaces.EXTRA_COIN_VALUE);
        form_type = getIntent().getIntExtra(Interfaces.EXTRA_RECHARGE_TYPE, 0);
        if (!TextUtils.isEmpty(coin)) {
            mTvCoin.setText(coin);
        }
        getCoin();
    }

    private void getCoin() {
        p.getCoin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRechargeInfo();
    }

    private void getRechargeInfo() {
        p.getRechargeInfo();
    }

    @Override
    public void onGetCoin(IntegralDto bean) {
        mCoinBean = bean;
        mTvCoin.setAnimationDuration(Interfaces.TICKERVIEW_ANIMATION_MONEY);
        mTvCoin.setText(bean.coin);
    }

    @Override
    public void onGetRechargeInfo(PaymentDto bean) {
        smartRefreshLayout.finishRefresh(bean != null);

        if (bean != null) {
            mAdapter.setList(bean.payment);
        } else {
            mAdapter.setList(null);
        }
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                getCoin();
                                if (payPopWindow != null && payPopWindow.isShowing()) payPopWindow.dismiss();
                            }
                        }
                ));
    }

    @Override
    public void paySuccess() {
        if (Utils.isPayTypeInFamily(form_type)) {
            ARouter.getInstance().navigation(MineService.class).trackEvent(Interfaces.POINT_SCENE_PAY_SUCCESS,
                    Interfaces.POINT_EVENT_PAGE, Interfaces.POINT_INDEX_PAY_SUCCESS, JSON.toJSONString(new BuriedPointExtendDto()), null);
        }
        toastTip("支付成功");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
        finish();
    }

    @Override
    public void payFail() {
        toastTip("支付失败");
    }
}
