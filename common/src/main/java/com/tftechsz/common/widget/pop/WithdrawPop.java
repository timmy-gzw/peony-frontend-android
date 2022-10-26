package com.tftechsz.common.widget.pop;

import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.databinding.PopWithdrawBindBinding;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.entity.WithdrawReq;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.MyInputTextBoldFilter;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 提现pop
 */
public class WithdrawPop extends BaseBottomPop implements View.OnClickListener, MyInputTextBoldFilter.TextChangedCallBack {

    UserProviderService service;
    public PublicService userService;
    private PopWithdrawBindBinding mBind;
    private CompositeDisposable mCompositeDisposable;
    private CountBackUtils countBackUtils;
    private String typeId;
    private WithdrawReq.Withdraw withdrawRes;

    public WithdrawPop(String type_id) {
        super(Utils.getApp());
        this.typeId = type_id;
        initUI();
        initListener();
        setOutSideDismiss(false);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                KeyboardUtils.hideSoftInput(mBind.getRoot());
            }
        });
    }


    @Override
    protected View createPopupById() {
        View view = View.inflate(getContext(), R.layout.pop_withdraw_bind, null);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void initUI() {
        countBackUtils = new CountBackUtils();
        userService = RetrofitManager.getInstance().createUserApi(PublicService.class);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mCompositeDisposable = new CompositeDisposable();

        mBind.btnGetCode.setOnClickListener(this);
        mBind.tvNext.setOnClickListener(this);
        mBind.topCancel.setOnClickListener(this);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBind.scrollView.getLayoutParams();
        lp.matchConstraintMaxHeight = ScreenUtils.getScreenHeight() / 3;
        mBind.scrollView.setLayoutParams(lp);
        withdrawWay();
    }

    private void initListener() {
        new AddSpaceTextWatcher(mBind.edtTell, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        new AddSpaceTextWatcher(mBind.etCard, AddSpaceTextWatcher.SpaceType.IDCardNumberType);
        new AddSpaceTextWatcher(mBind.etPhone, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        new MyInputTextBoldFilter(mBind.edtTell, this);
        new MyInputTextBoldFilter(mBind.edtCode, this);
        new MyInputTextBoldFilter(mBind.etNumber, this);
        new MyInputTextBoldFilter(mBind.etName, this);
        new MyInputTextBoldFilter(mBind.etCard, this);
        new MyInputTextBoldFilter(mBind.etPhone, this);
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) return;
        int id = v.getId();
        if (id == R.id.tv_next) { //下一步
            if (!mBind.getIsNext()) {
                checkPhoneCode();
            } else {
                withdraw();
            }
        } else if (id == R.id.top_cancel) { //取消
            dismiss();
        } else if (id == R.id.btn_get_code) { // 获取验证码
            String tell = Utils.getText(mBind.edtTell).replace(" ", "");
            if (!Utils.regexTell(service.getConfigInfo(), tell)) {
                Utils.setFocus(mBind.edtTell);
                Utils.toast("手机号格式错误");
                return;
            }
            perFormCode(tell);
        }
    }

    private void showPop() {
        BasePayTypePopWindow popWindow = new BasePayTypePopWindow(getContext(), 1);
        if (withdrawRes != null && withdrawRes.is_show == 1) {
            popWindow.setInfo(withdrawRes.account, withdrawRes.name, withdrawRes.identity, withdrawRes.phone);
        }
        popWindow.setTypeId(typeId);
        popWindow.showPopupWindow();
    }

    /**
     * 提现方式获取
     */
    public void withdrawWay() {
        mCompositeDisposable.add(userService.withdrawWay(1).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<WithdrawReq.Withdraw>>() {
                    @Override
                    public void onSuccess(BaseResponse<WithdrawReq.Withdraw> response) {
                        if (response.getData() != null ) {
                            withdrawRes = response.getData();
                        }

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 下一步检测短信
     */
    private void checkPhoneCode() {
        String tell = Utils.getText(mBind.edtTell).replace(" ", "");
        String code = Utils.getText(mBind.edtCode);
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createUserApi(PublicService.class)
                .checkCode(tell, code)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> stringBaseResponse) {
                        dismiss();
                        showPop();
                    }
                }));
    }

    /**
     * 提现操作
     */
    private void withdraw() {
        ConfigInfo configInfo = service.getConfigInfo();
        String number = Utils.getText(mBind.etNumber);
        String name = Utils.getText(mBind.etName);
        String card = Utils.getText(mBind.etCard).replace(" ", "");
        String phone = Utils.getText(mBind.etPhone).replace(" ", "");
        if (!Utils.regexEmail(configInfo, number) && !Utils.regexTell(configInfo, number)) {
            Utils.setFocus(mBind.etNumber);
            Utils.toast("支付宝账号格式错误");
            return;
        }
        if (!Utils.regexChineseName(configInfo, name)) {
            Utils.setFocus(mBind.etName);
            Utils.toast("姓名格式错误");
            return;
        }
        if (!Utils.regexIdCard(configInfo, card)) {
            Utils.setFocus(mBind.etCard);
            Utils.toast("身份证格式错误");
            return;
        }
        if (!Utils.regexTell(configInfo, phone)) {
            Utils.setFocus(mBind.etPhone);
            Utils.toast("手机号格式错误");
            return;
        }
        WithdrawReq withdrawReq = new WithdrawReq();
        withdrawReq.type_id = Utils.numberFormat(typeId);
        withdrawReq.out_warn = 1;
        WithdrawReq.Withdraw withdraw = new WithdrawReq.Withdraw();
        withdraw.account = number;
        withdraw.name = name;
        withdraw.identity = card;
        withdraw.phone = phone;
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

    /**
     * 处理发送验证码逻辑
     *
     * @param tell 电话号吗
     */
    private void perFormCode(String tell) {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createUserApi(PublicService.class)
                .checkPhone(tell)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> stringBaseResponse) {
                        sendCode(tell);

                    }
                }));
    }

    /**
     * 发送验证码
     *
     * @param tell 手机号
     */
    private void sendCode(String tell) {
        LoginReq req = new LoginReq();
        req.phone = tell;
        req.type = "withdraw_bind";
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createUserApi(PublicService.class)
                .sendCode(req)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        mBind.btnGetCode.setEnabled(false);
                        Utils.setFocus(mBind.edtCode);
                        countBackUtils.countBack(Interfaces.WAITING_TIME, new CountBackUtils.Callback() {
                            @Override
                            public void countBacking(long time) {
                                performBtn(true, time);
                            }

                            @Override
                            public void finish() {
                                performBtn(false, Interfaces.WAITING_TIME);
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    private void performBtn(boolean b, long waitingTime) {
        if (b) {
            mBind.btnGetCode.setText(String.format("重新获取(%ss)", waitingTime));
            mBind.btnGetCode.setEnabled(false);
            mBind.btnGetCode.setAlpha(0.4f);
        } else {
            mBind.btnGetCode.setText("获取验证码");
            mBind.btnGetCode.setEnabled(true);
            mBind.btnGetCode.setAlpha(1f);
        }
    }

    @Override
    public void textChange() {
        if (!mBind.getIsNext()) { //如果不是最后一步
            String tell = Utils.getText(mBind.edtTell).replace(" ", "");
            String code = Utils.getText(mBind.edtCode);
            boolean regexTell = Utils.regexTell(service.getConfigInfo(), tell);
            if (countBackUtils == null || !countBackUtils.isTiming()) {
                mBind.btnGetCode.setEnabled(regexTell);
            }
            Utils.getText(mBind.edtCode);
            mBind.setIsClick(regexTell && !TextUtils.isEmpty(code) && code.length() == 4);
        } else {
            mBind.setIsClick(!TextUtils.isEmpty(Utils.getText(mBind.etNumber)) && !TextUtils.isEmpty(Utils.getText(mBind.etName))
                    && !TextUtils.isEmpty(Utils.getText(mBind.etCard)) && !TextUtils.isEmpty(Utils.getText(mBind.etPhone)));
        }
    }
}
