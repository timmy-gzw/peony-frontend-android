package com.tftechsz.im.widget.pop;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.im.R;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.PriceEditInputFilter;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseBottomPop;

import io.reactivex.disposables.CompositeDisposable;

public
/**
 *  包 名 : com.tftechsz.im.widget.pop

 *  描 述 : 红包弹窗
 */
class SendRedEnvelopePopWindow extends BaseBottomPop implements View.OnClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {
    private final Activity activity;
    private EditText mEdtGoldPrice;
    private EditText mEdtGoldNum;
    private EditText mEdtLeave;
    private TextView mTvBalance;
    private String mCoin;//金币
    private TextView mTvCondition;
    private RadioGroup mRgCondition;
    private SendClickListener listener;
    private boolean isFamilyRecruit;
    private int MIN_RED_ENVELOPE_NUM = Interfaces.MIN_RED_ENVELOPE_NUM;
    private int MIN_RED_ENVELOPE_PRICE = Interfaces.MIN_RED_ENVELOPE_PRICE;
    private int MAX_RED_ENVELOPE_PRICE = Interfaces.MAX_RED_ENVELOPE_PRICE;
    private TextView mTvTitle, mTvTitleHint, mTvAllPrice;
    private final UserProviderService userService;
    private final CompositeDisposable mCompositeDisposable;
    private int receiveUserType = 0;

    public SendRedEnvelopePopWindow(Activity activity) {
        super(activity);
        this.activity = activity;
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
        initRxBus();
        setOutSideDismiss(false);
    }

    public void setIsFamilyRecruit(boolean isFamilyRecruit) {
        this.isFamilyRecruit = isFamilyRecruit;
        initView();
    }

    private void initUI() {
        mRgCondition = findViewById(R.id.rg_condition);
        mRgCondition.setOnCheckedChangeListener(this);
        mTvCondition = findViewById(R.id.tv_condition);
        mTvTitle = findViewById(R.id.iv_title);
        mTvTitleHint = findViewById(R.id.tv_title_hint);
        mTvAllPrice = findViewById(R.id.all_price);
        mEdtGoldPrice = findViewById(R.id.edt_gold_price);
        mEdtGoldNum = findViewById(R.id.edt_gold_num);
        mEdtLeave = findViewById(R.id.edt_leave);
        mTvBalance = findViewById(R.id.tv_balance);
        findViewById(R.id.ic_close).setOnClickListener(v -> dismiss());
        findViewById(R.id.tv_send).setOnClickListener(this);
        findViewById(R.id.tv_recharge).setOnClickListener(this);
        mEdtGoldPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                return;
            }
            String price = Utils.getText(mEdtGoldPrice);
            if (TextUtils.isEmpty(price)) {
                return;
            }
            if (Integer.parseInt(price) < MIN_RED_ENVELOPE_PRICE) {
                mEdtGoldPrice.setText(String.valueOf(MIN_RED_ENVELOPE_PRICE));
                Utils.toast(String.format("红包金额最少%s金币", MIN_RED_ENVELOPE_PRICE));
            }
        });

        mEdtGoldPrice.addTextChangedListener(this);
        mEdtGoldNum.addTextChangedListener(this);

        mEdtGoldNum.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                return;
            }
            String num = Utils.getText(mEdtGoldNum);
            if (TextUtils.isEmpty(num)) {
                return;
            }
            if (Integer.parseInt(num) < MIN_RED_ENVELOPE_NUM) {
                mEdtGoldNum.setText(String.valueOf(MIN_RED_ENVELOPE_NUM));
                Utils.toast(String.format("红包个数不可少于%s个", MIN_RED_ENVELOPE_NUM));
            }
        });
    }

    private void initView() {
        mEdtGoldPrice.setText(null);
        mEdtGoldNum.setText(null);
        if (!isFamilyRecruit) {
            MIN_RED_ENVELOPE_NUM = Interfaces.MIN_RED_ENVELOPE_NUM;
            MIN_RED_ENVELOPE_PRICE = Interfaces.MIN_RED_ENVELOPE_PRICE;
            MAX_RED_ENVELOPE_PRICE = Interfaces.MAX_RED_ENVELOPE_PRICE;
            mTvTitle.setText("发红包");
            mTvTitleHint.setVisibility(View.GONE);
            mEdtGoldPrice.setHint("填写金额");
            mEdtGoldNum.setHint("填写个数");
            mEdtLeave.setHint("恭喜发财, 大吉大利");
            mTvAllPrice.setVisibility(View.GONE);
            mTvCondition.setVisibility(View.VISIBLE);
            mRgCondition.setVisibility(View.VISIBLE);
        } else {
            MIN_RED_ENVELOPE_NUM = 5;
            MIN_RED_ENVELOPE_PRICE = 1;
            MAX_RED_ENVELOPE_PRICE = 500;
            mTvTitle.setText("家族招募红包");
            mTvTitleHint.setVisibility(View.VISIBLE);
            mEdtGoldPrice.setHint(String.format("输入单个红包金额, 最低%s金币", MIN_RED_ENVELOPE_PRICE));
            mEdtGoldNum.setHint("最多100个");
            mEdtLeave.setHint("欢迎大家加入我的家族");
            mTvAllPrice.setVisibility(View.VISIBLE);
            mTvCondition.setVisibility(View.GONE);
            mRgCondition.setVisibility(View.GONE);
        }
    }

    public void setCoin(String coin) {
        mCoin = coin;
        mTvBalance.setText(String.format("金币余额：%s金币", coin));
        InputFilter[] filters = {new PriceEditInputFilter(MAX_RED_ENVELOPE_PRICE, mEdtGoldPrice, 0)};
        mEdtGoldPrice.setFilters(filters);
        InputFilter[] filters1 = {new PriceEditInputFilter(Interfaces.MAX_RED_ENVELOPE_NUM, mEdtGoldNum, 1)};
        mEdtGoldNum.setFilters(filters1);
    }

    public void getCoin() {
        userService.getField("property", "coin", new ResponseObserver<BaseResponse<IntegralDto>>() {
            @Override
            public void onSuccess(BaseResponse<IntegralDto> response) {
                if (response.getData() != null) {
                    UserInfo userInfo = userService.getUserInfo();
                    userInfo.setCoin(response.getData().coin);
                    userService.setUserInfo(userInfo);
                    setCoin(response.getData().coin);
                }
            }
        });
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                getCoin();
                                dismiss();
                            } else if (event.type == Constants.NOTIFY_HIDE_SOFT) {
                                Utils.hideSoftKeyBoard(activity);
                            }
                        }
                ));
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.send_pop_red_envelope);
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_recharge) { //去充值
            ARouterUtils.toRechargeActivity();
        } else if (id == R.id.tv_send) { //发送
            if (Utils.checkNull(mEdtGoldPrice, "请填写红包金币数量")) {
                return;
            }
            if (Utils.checkNull(mEdtGoldNum, "请填写红包个数")) {
                return;
            }
            int goldPrice = Integer.parseInt(Utils.getText(mEdtGoldPrice));
            int goldNum = Integer.parseInt(Utils.getText(mEdtGoldNum));
            if (goldPrice < MIN_RED_ENVELOPE_PRICE) {
                mEdtGoldPrice.setText(String.valueOf(MIN_RED_ENVELOPE_PRICE));
                mEdtGoldPrice.setSelection(Utils.getText(mEdtGoldPrice).length());
                Utils.toast(String.format("红包金额最少%s金币", MIN_RED_ENVELOPE_PRICE));
                return;
            }
            if (goldNum < MIN_RED_ENVELOPE_NUM) {
                mEdtGoldNum.setText(String.valueOf(MIN_RED_ENVELOPE_NUM));
                mEdtGoldNum.setSelection(Utils.getText(mEdtGoldNum).length());
                Utils.toast(String.format("红包个数不可少于%s个", MIN_RED_ENVELOPE_NUM));
                return;
            }
            if (!isFamilyRecruit && goldPrice < goldNum) {
                Utils.toast("金币数量不能少于红包个数");
                return;
            }

            if (TextUtils.isEmpty(mCoin)) {
                return;
            }

            if (isFamilyRecruit) {
               /* if (Double.parseDouble(mCoin) < goldPrice * goldNum) { //持有金币小于要发送的金币数量
                    Utils.toast("余额不足，请充值");
                    ARouterUtils.toRechargeActivity();
                    return;
                }*/
            } else {
               /* if (Double.parseDouble(mCoin) < goldPrice) { //持有金币小于要发送的金币数量
                    ARouterUtils.toRechargeActivity();
                    return;
                }*/
            }
            if (listener != null) {
                listener.sendClick(goldPrice, goldNum,receiveUserType, Utils.getText(mEdtLeave));
            }
        }
    }

    public void clearEdit() {
        mEdtGoldPrice.setText("");
        mEdtGoldNum.setText("");
        mEdtLeave.setText("");
        dismiss();
        Utils.hideSoftKeyBoard(activity);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isFamilyRecruit) { //如果是家族邀请红包
            String price = Utils.getText(mEdtGoldPrice);
            String num = Utils.getText(mEdtGoldNum);
            if (TextUtils.isEmpty(price) || TextUtils.isEmpty(num)) {
                mTvAllPrice.setText("0金币");
            } else {
                mTvAllPrice.setText(String.format("%s金币", Integer.parseInt(price) * Integer.parseInt(num)));
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_condition) {
            if (checkedId == R.id.rb_all) {
                receiveUserType = 0;
            } else if (checkedId == R.id.rb_boy) {
                receiveUserType = 1;
            } else if (checkedId == R.id.rb_girl) {
                receiveUserType = 2;
            }
        }
    }

    public interface SendClickListener {
        void sendClick(int gold_price, int gold_num, int receiveUserType, String gold_content);
    }

    public void addOnclikListener(SendClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        Utils.setFocus(mEdtGoldPrice);
        KeyboardUtils.showSoftInput();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
        }
    }
}
