package com.tftechsz.mine.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.ShopInfoDto;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.mine.mvp.IView.IWithdrawView;
import com.tftechsz.mine.mvp.presenter.WithdrawPresenter;


public class WithdrawActivity extends BaseMvpActivity<IWithdrawView, WithdrawPresenter> implements View.OnClickListener, IWithdrawView {

    private static final String EXTRA_COIN = "extra_coin";
    private ShopInfoDto mData;
    private TextView mTvCoinNumber;
    private EditText mEtNumber, mEtName, mEtCard, mEtPhone;
    private TextView mTvIntegral;   //扣除积分
    private TextView mTvExchange;
    private TextView mTvWithdrawTip;
    @Autowired
    UserProviderService service;
    private ConfigInfo mConfigInfo;

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
        mEtNumber = findViewById(R.id.et_number);
        mEtCard = findViewById(R.id.et_card);
        mEtName = findViewById(R.id.et_name);
        mEtPhone = findViewById(R.id.et_phone);
        mTvIntegral = findViewById(R.id.tv_integral);
        mTvExchange = findViewById(R.id.tv_exchange);
        mTvWithdrawTip = findViewById(R.id.tv_withdraw_tip);
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
        mEtNumber.addTextChangedListener(new MyInputFilter(mEtNumber));
        mEtCard.addTextChangedListener(new MyInputFilter(mEtCard));
        mEtName.addTextChangedListener(new MyInputFilter(mEtName));
        mEtPhone.addTextChangedListener(new MyInputFilter(mEtPhone));
        new AddSpaceTextWatcher(mEtPhone, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        new AddSpaceTextWatcher(mEtCard, AddSpaceTextWatcher.SpaceType.IDCardNumberType);
    }

    private void editListener() {
        if (TextUtils.isEmpty(Utils.getText(mEtNumber))
                || TextUtils.isEmpty(Utils.getText(mEtCard))
                || TextUtils.isEmpty(Utils.getText(mEtName))
                || TextUtils.isEmpty(Utils.getText(mEtPhone))) {
            mTvExchange.setBackgroundResource(R.drawable.bg_gray_ee);
        } else {
            mTvExchange.setBackgroundResource(R.drawable.bg_orange_radius);
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
            mTvIntegral.setText("扣除积分:" + mData.integral);
        }
        mConfigInfo = service.getConfigInfo();
        if (null != mConfigInfo && service.getConfigInfo().share_config != null) {
            mTvWithdrawTip.setText(service.getConfigInfo().share_config.shopping_convert_msg);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (null == mData)
            return;
        if (id == R.id.tv_exchange) {   //兑换
            String number = Utils.getText(mEtNumber);
            String name = Utils.getText(mEtName);
            String card = Utils.getText(mEtCard).replace(" ", "");
            String phone = Utils.getText(mEtPhone).replace(" ", "");
            if (TextUtils.isEmpty(number)) {
                Utils.setFocus(mEtNumber);
                toastTip("请输入你的支付宝账号");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                Utils.setFocus(mEtName);
                toastTip("请输入你的真实姓名");
                return;
            }
            if (TextUtils.isEmpty(card)) {
                Utils.setFocus(mEtCard);
                toastTip("请输入你的身份证号码");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                Utils.setFocus(mEtPhone);
                toastTip("请输入你的手机号码");
                return;
            }
            if (!Utils.regexEmail(mConfigInfo, number) && !Utils.regexTell(mConfigInfo, number)) {
                Utils.setFocus(mEtNumber);
                toastTip("支付宝账号格式错误");
                return;
            }
            if (!Utils.regexChineseName(mConfigInfo, name)) {
                Utils.setFocus(mEtName);
                toastTip("姓名格式错误");
                return;
            }
            if (!Utils.regexIdCard(mConfigInfo, card)) {
                Utils.setFocus(mEtCard);
                toastTip("身份证格式错误");
                return;
            }
            if (!Utils.regexTell(mConfigInfo, phone)) {
                Utils.setFocus(mEtPhone);
                toastTip("手机号格式错误");
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
        mEtName.setText(msg.name);
        mEtCard.setText(msg.identity);
        mEtNumber.setText(msg.account);
        mEtPhone.setText(msg.phone);
    }

    private class MyInputFilter implements TextWatcher {
        private final EditText mEditText;
        Typeface normal = Typeface.defaultFromStyle(Typeface.NORMAL);
        Typeface bold = Typeface.defaultFromStyle(Typeface.BOLD);

        public MyInputFilter(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editListener();
            if (TextUtils.isEmpty(Utils.getText(mEditText))) {
                mEditText.setTypeface(normal);
            } else {
                mEditText.setTypeface(bold);

            }
        }
    }
}
