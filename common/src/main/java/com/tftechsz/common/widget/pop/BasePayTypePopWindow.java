package com.tftechsz.common.widget.pop;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.databinding.PopBasePayTypeBinding;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;
import razerdp.util.KeyboardUtils;

/**
 * 包 名 : com.tftechsz.mine.widget.pop
 * 描 述 : 填写支付宝提现方式
 */
public class BasePayTypePopWindow extends BaseBottomPop {

    private PopBasePayTypeBinding mBind;
    UserProviderService userService;
    private final ConfigInfo mConfigInfo;
    private String number, name, card, phone;
    private int outWarn;//账户类型
    private String typeId;
    private CompositeDisposable mCompositeDisposable;
    private boolean is_show = true;

    public BasePayTypePopWindow(Activity context) {
        this(context,0);
    }
    public BasePayTypePopWindow(Activity context,int warn) {
        super(context);
        this.outWarn = warn;
        userService = ARouter.getInstance().navigation(UserProviderService.class);
        mConfigInfo = userService.getConfigInfo();
        initUI();
    }

    public void setInfo(String number, String name, String card, String phone) {
        this.number = number;
        this.name = name;
        this.card = card;
        this.phone = phone;
        if (mBind != null) {
            mBind.etNumber.setText(number);
            mBind.etName.setText(name);
            mBind.etCard.setText(card);
            mBind.etPhone.setText(phone);
        }
    }

    public void setTypeId(String type_id) {
        this.typeId = type_id;
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_base_pay_type);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        mCompositeDisposable = new CompositeDisposable();
        setInfo("", "", "", "");
        if(outWarn == 1){
            mBind.tvPopTitle.setText("提交新账户");
            mBind.ivCheck.setVisibility(View.VISIBLE);
            mBind.tvCheckTip.setVisibility(View.VISIBLE);
            mBind.ivCheck.setOnClickListener(v->{
                is_show = !is_show;
                mBind.ivCheck.setImageResource(is_show?R.mipmap.ic_check_selector:R.mipmap.ic_check_normal);
            });
        }
        mBind.tvClosePop.setOnClickListener(v -> dismiss());
        mBind.tvConfirm.setOnClickListener((View.OnClickListener) v -> {
            String number = Utils.getText(mBind.etNumber);
            String name = Utils.getText(mBind.etName);
            String card = Utils.getText(mBind.etCard).replace(" ", "");
            String phone = Utils.getText(mBind.etPhone).replace(" ", "");
            if (TextUtils.isEmpty(number)) {
                Utils.setFocus(mBind.etNumber);
                toastTip("请输入你的支付宝账号");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                Utils.setFocus(mBind.etName);
                toastTip("请输入你的真实姓名");
                return;
            }
            if (TextUtils.isEmpty(card)) {
                Utils.setFocus(mBind.etCard);
                toastTip("请输入你的身份证号码");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                Utils.setFocus(mBind.etPhone);
                toastTip("请输入你的手机号码");
                return;
            }
            if (!Utils.regexEmail(mConfigInfo, number) && !Utils.regexTell(mConfigInfo, number)) {
                Utils.setFocus(mBind.etNumber);
                toastTip("支付宝账号格式错误");
                return;
            }
            if (!Utils.regexChineseName(mConfigInfo, name)) {
                Utils.setFocus(mBind.etName);
                toastTip("姓名格式错误");
                return;
            }
            if (!Utils.regexIdCard(mConfigInfo, card)) {
                Utils.setFocus(mBind.etCard);
                toastTip("身份证格式错误");
                return;
            }
            if (!Utils.regexTell(mConfigInfo, phone)) {
                Utils.setFocus(mBind.etPhone);
                toastTip("手机号格式错误");
                return;
            }
            KeyboardUtils.close(mBind.etName);
            KeyboardUtils.close(mBind.etNumber);
            KeyboardUtils.close(mBind.etCard);
            KeyboardUtils.close(mBind.etPhone);
            if(outWarn == 0) {
                listener.onConfirm(number, card, name, phone);
            }else {
                withdraw();
            }
            dismiss();
        });

        mBind.etNumber.addTextChangedListener(new MyInputFilter(mBind.etNumber));
        mBind.etCard.addTextChangedListener(new MyInputFilter(mBind.etCard));
        mBind.etName.addTextChangedListener(new MyInputFilter(mBind.etName));
        mBind.etPhone.addTextChangedListener(new MyInputFilter(mBind.etPhone));
        new AddSpaceTextWatcher(mBind.etPhone, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        new AddSpaceTextWatcher(mBind.etCard, AddSpaceTextWatcher.SpaceType.IDCardNumberType);
    }

    /**
     * 提现操作
     */
    private void withdraw() {
        String number = Utils.getText(mBind.etNumber);
        String name = Utils.getText(mBind.etName);
        String card = Utils.getText(mBind.etCard).replace(" ", "");
        String phone = Utils.getText(mBind.etPhone).replace(" ", "");
        WithdrawReq withdrawReq = new WithdrawReq();
        withdrawReq.type_id = Utils.numberFormat(typeId);
        withdrawReq.out_warn = 1;
        WithdrawReq.Withdraw withdraw = new WithdrawReq.Withdraw();
        withdraw.account = number;
        withdraw.name = name;
        withdraw.identity = card;
        withdraw.phone = phone;
        withdraw.is_show = is_show?1:0;
        withdrawReq.withdraw = withdraw;

        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createExchApi(PublicService.class)
                .withdraw(withdrawReq)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> stringBaseResponse) {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                        dismiss();
                    }
                })
        );
    }

    private void editListener() {
        mBind.tvConfirm.setEnabled(!TextUtils.isEmpty(Utils.getText(mBind.etNumber))
                && !TextUtils.isEmpty(Utils.getText(mBind.etCard))
                && !TextUtils.isEmpty(Utils.getText(mBind.etName))
                && !TextUtils.isEmpty(Utils.getText(mBind.etPhone)));
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

    public interface OnConfirmListener {
        void onConfirm(String number, String card, String name, String phone);
    }

    public OnConfirmListener listener;

    public void addOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public void toastTip(final String msg) {
        ToastUtil.showToast(BaseApplication.getInstance(), msg);
    }
}
