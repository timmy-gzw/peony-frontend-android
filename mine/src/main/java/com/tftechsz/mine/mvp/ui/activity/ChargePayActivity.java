package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.ChargePayAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PayTextClick;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IChargePayView;
import com.tftechsz.mine.mvp.presenter.ChargePayPresenter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


@Route(path = ARouterApi.ACTIVITY_CHARGE_PAY)
public class ChargePayActivity extends BaseMvpActivity<IChargePayView, ChargePayPresenter> implements View.OnClickListener, IChargePayView {

    private RechargeDto mData;
    private TextView mTvCoin, mTvCoinNumber;
    private TextView mTvContact;
    private RelativeLayout mRlZFB, mRlWX;
    private RecyclerView mRvPayWay;
    private boolean inPayStatus;
    @Autowired
    UserProviderService service;
    private int form_type;
    private CheckBox checkBox1, checkBox2;
    private ChargePayAdapter adapter;

    @Override
    public ChargePayPresenter initPresenter() {
        return new ChargePayPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .build();
        checkBox1 = findViewById(R.id.checkbox);
        checkBox2 = findViewById(R.id.checkbox2);

        mTvCoin = findViewById(R.id.tv_coin);
        mTvCoinNumber = findViewById(R.id.tv_coin_num);
        mRlZFB = findViewById(R.id.rl_zfb);
        mRlWX = findViewById(R.id.rl_wx);
        mTvContact = findViewById(R.id.tv_contact);
        mRvPayWay = findViewById(R.id.rv_pay_way);
        mRvPayWay.setLayoutManager(new LinearLayoutManager(this));
        initListener();
        initRxBus();
    }

    private void initListener() {
        mRlZFB.setOnClickListener(this);
        mRlWX.setOnClickListener(this);
        findViewById(R.id.tv_pay).setOnClickListener(this);
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

    @Override
    protected int getLayout() {
        return R.layout.activity_charge_pay;
    }


    @Override
    protected void initData() {
        super.initData();
        mData = (RechargeDto) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
        form_type = getIntent().getIntExtra(Interfaces.EXTRA_RECHARGE_TYPE, 0);

        if (null != mData) {
            mTvCoin.setText(mData.coin);
            mTvCoinNumber.setText("¥" + mData.rmb);
        }

        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.api != null && configInfo.api.recharge_bottom != null && configInfo.api.recharge_bottom.size() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (ConfigInfo.MineInfo mineInfo : configInfo.api.recharge_bottom) {
                builder.append(mineInfo.title);
                if (!TextUtils.isEmpty(mineInfo.link)) {
                    int start = builder.toString().indexOf(mineInfo.title);
                    builder.setSpan(new PayTextClick(mContext, mineInfo), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new UnderlineSpan(), start, start + mineInfo.title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mTvContact.setText(builder);
            mTvContact.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (configInfo != null && configInfo.share_config != null && configInfo.share_config.payment_type != null) {
            adapter = new ChargePayAdapter(configInfo.share_config.payment_type);
            mRvPayWay.setAdapter(adapter);
            adapter.setOnItemClickListener((adapter1, view, position) -> {
                adapter.notifyDataPosition(position);
            });
            mRlZFB.setVisibility(View.GONE);
            mRlWX.setVisibility(View.GONE);
        } else {
            mRlZFB.setVisibility(View.VISIBLE);
            mRlWX.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Interfaces.PAY_REQUEST_CODE) {
            inPayStatus = true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (inPayStatus) {
            inPayStatus = false;
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (null == mData)
            return;
        if (id == R.id.rl_zfb) {  //支付宝
            checkBox1.setChecked(true);
            checkBox2.setChecked(false);
        } else if (id == R.id.rl_wx) {   //微信
            checkBox1.setChecked(false);
            checkBox2.setChecked(true);
        } else if (id == R.id.tv_pay) {
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (mRlZFB.getVisibility() == View.VISIBLE) {// 两个选择方式
                if (checkBox1.isChecked()) {
                    p.getOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                } else if (checkBox2.isChecked()) {
                    p.getWxOrderNum(this, mData.id, Utils.getPayTypeFrom(form_type));
                }
            } else {
                if (adapter == null) {
                    return;
                }
                if (Utils.isPayTypeInFamily(form_type)) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("界面_余额不足点击充值", "click", "page_to_recharge",
                                    JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.RechargeExtendDto(adapter.getItem(adapter.getDataPosition()).type, mData.coin))), null);
                }

                if (adapter.isWeChat(adapter.getDataPosition())) {
                    Utils.putPayType(form_type);
                    p.getWxOrderNum(ChargePayActivity.this, mData.id, Utils.getPayTypeFrom(form_type));
                    return;
                }
                if (adapter.isAliPay(adapter.getDataPosition())) {
                    p.getOrderNum(ChargePayActivity.this, mData.id, Utils.getPayTypeFrom(form_type));
                    return;
                }
                if (adapter.isSXYWeChat(adapter.getDataPosition())) {
                    Utils.putPayType(form_type);
                    p.getSXYWeChatPay(mActivity, mData.id, Utils.getPayTypeFrom(form_type));
                }
                if (adapter.isSXYAliPay(adapter.getDataPosition())) {
                    p.getSXYAliOrderNum(mActivity, mData.id, Utils.getPayTypeFrom(form_type));
                }
            }


        }
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
