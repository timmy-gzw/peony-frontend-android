package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.presenter.LoginPresenter;

import java.util.Random;


public class LoginByPhoneActivity extends BaseMvpActivity<ILoginView, LoginPresenter> implements View.OnClickListener, ILoginView {

    private EditText mEtPhone;
    private String mPhone;
    private TextView mTvTip;
    private final int mMax = 2000;
    private final int mMin = 1000;

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("手机号登录")
                .build();
        mEtPhone = findViewById(R.id.et_phone);
        mTvTip = findViewById(R.id.tv_tip);
        TextView getCode = findViewById(R.id.tv_get_code);
        getCode.setOnClickListener(this);
        mEtPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getCode.performClick();
                return true;
            }
            return false;
        });
        new AddSpaceTextWatcher(mEtPhone, AddSpaceTextWatcher.SpaceType.mobilePhoneNumberType);
        String spPhone = SPUtils.getInstance().getString(Interfaces.SP_PHONE);
        mEtPhone.setText(spPhone);
        Utils.setFocus(mEtPhone);
        KeyboardUtils.showSoftInput();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login_by_phone;
    }


    @Override
    protected void initData() {
        super.initData();
        Random random = new Random();
        int number = random.nextInt(mMax - mMin) + mMin + 1;
        String text1 = "为您推荐的 ";
        String text2 = number + "";
        String text3 = " 位同城异性朋友\n完成注册后可见";
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(text1).append(text2).append(text3);
        spanString.append(text1).append(text2).append(text3);
        int start = stringBuffer.indexOf(text2);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#F8D423")), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvTip.setText(spanString);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_get_code) {   //获取验证码
            mPhone = mEtPhone.getText().toString().replace(" ","");
//            if (!StringUtils.isPhone(mPhone)) {
//                ToastUtil.showToast(this, "请输入正确的手机号");
//                return;
//            }
            if (Utils.checkNoTell(mPhone)) {
                return;
            }
            LoginReq loginReq = new LoginReq();
            loginReq.phone = mPhone;
            p.sendCode(loginReq, true);

        }
    }

    /**
     * 成功获取验证码
     */
    @Override
    public void getCodeSuccess(String data) {
        ToastUtil.showToast(this, data);
        Intent intent = new Intent(this, GetCodeActivity.class);
        intent.putExtra("phone", mPhone);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginSuccess(LoginDto data) {
    }

    @Override
    public void getConfigSuccess(String msg, int flag) {

    }


    @Override
    public void agreementSuccess() {

    }

    @Override
    public void getOpenLoginAuthStatusSuccess() {

    }

    @Override
    public void getOpenLoginAuthStatusFail() {

    }

    @Override
    public void onFail(int flag) {

    }

    @Override
    public void cancelLoginAuth() {

    }
}
