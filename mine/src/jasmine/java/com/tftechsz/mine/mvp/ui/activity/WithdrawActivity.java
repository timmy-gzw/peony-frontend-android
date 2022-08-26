package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BasePayTypePopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.ShopInfoDto;
import com.tftechsz.mine.mvp.IView.IWithdrawView;
import com.tftechsz.mine.mvp.presenter.WithdrawPresenter;


public class WithdrawActivity extends BaseMvpActivity<IWithdrawView, WithdrawPresenter> implements View.OnClickListener, IWithdrawView {

    private static final String EXTRA_COIN = "extra_coin";
    private ShopInfoDto mData;
    private TextView mTvCoinNumber;
    private TextView mTvIntegral;   //扣除积分
    private TextView mTvExchange;
    private TextView mTvWithdrawTip;
    private View tvEditPayInfo;
    private View tvAddPayInfo;
    private View clPayInfo;
    private ImageView mIvShop;
    private TextView tvPayName, tvPayNumber;
    @Autowired
    UserProviderService service;
    private ConfigInfo mConfigInfo;

    private String number, name, card, phone;
    private BasePayTypePopWindow popWindow;

    public static void startActivity(Context context, ShopInfoDto coin) {
        Intent intent = new Intent(context, WithdrawActivity.class);
        intent.putExtra(EXTRA_COIN, coin);
        context.startActivity(intent);
    }

    @Override
    public WithdrawPresenter initPresenter() {
        return new WithdrawPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(this).keyboardEnable(true).init();
        new ToolBarBuilder().showBack(true)
                .setTitle("确认兑换")
                .build();
        mTvCoinNumber = findViewById(R.id.tv_coin_num);
        mTvIntegral = findViewById(R.id.tv_integral);
        mIvShop = findViewById(R.id.iv_shop);
        mTvExchange = findViewById(R.id.tv_exchange);
        mTvWithdrawTip = findViewById(R.id.tv_withdraw_tip);
        tvEditPayInfo = findViewById(R.id.tv_pay_info_edit);
        tvAddPayInfo = findViewById(R.id.tv_add_pay_type);
        clPayInfo = findViewById(R.id.cl_pay_ali_add_info);
        tvPayName = findViewById(R.id.tv_pay_name);
        tvPayNumber = findViewById(R.id.tv_pay_number);
        Utils.setFocus(mTvCoinNumber);
        initListener();
        initRxBus();
        p.withdrawWay();
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                finish();
                            }
                        }
                ));
    }

    private void initListener() {
        mTvExchange.setOnClickListener(this);  //兑换
        tvEditPayInfo.setOnClickListener(this);
        tvAddPayInfo.setOnClickListener(this);
    }

    private void showPayInfoPop() {
        if (popWindow == null) {
            popWindow = new BasePayTypePopWindow(this);
            popWindow.addOnConfirmListener(this::setPayWayInfo);
        }
        popWindow.setInfo(number, name, card, phone);
        popWindow.showPopupWindow();
    }

    private void setPayWayInfo(String number, String card, String name, String phone) {
        if (!TextUtils.isEmpty(number)
                && !TextUtils.isEmpty(card)
                && !TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(phone)) {
            WithdrawActivity.this.number = number;
            WithdrawActivity.this.card = card;
            WithdrawActivity.this.name = name;
            WithdrawActivity.this.phone = phone;
            tvPayName.setText(name);
            tvPayNumber.setText(number);
            tvAddPayInfo.setVisibility(View.GONE);
            clPayInfo.setVisibility(View.VISIBLE);
            mTvExchange.setEnabled(true);
        } else {
            mTvExchange.setEnabled(false);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void initData() {
        super.initData();
        mData = (ShopInfoDto) getIntent().getSerializableExtra(EXTRA_COIN);
        if (null != mData) {
            mTvCoinNumber.setText(mData.cost);
            mTvIntegral.setText("-" + mData.integral);
            GlideUtils.loadRouteImage(this, mIvShop, mData.image_big);
        }
        mConfigInfo = service.getConfigInfo();
        if (null != mConfigInfo && service.getConfigInfo().share_config != null) {
            mTvWithdrawTip.setText(service.getConfigInfo().share_config.shopping_convert_msg);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_pay_info_edit || id == R.id.tv_add_pay_type) {
            showPayInfoPop();
        } else if (id == R.id.tv_exchange) {   //兑换
            if (null == mData)
                return;
            if (TextUtils.isEmpty(number)) {
                return;
            }
            if (TextUtils.isEmpty(name)) {
                return;
            }
            if (TextUtils.isEmpty(card)) {
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                return;
            }
            p.withdraw(mData.id, name, number, card, phone);
        }
    }

    @Override
    public void withdrawSuccess(String msg) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
    }

    @Override
    public void withdrawWaySuccess(WithdrawReq.Withdraw msg) {
        setPayWayInfo(msg.account, msg.identity, msg.name, msg.phone);
    }
}
