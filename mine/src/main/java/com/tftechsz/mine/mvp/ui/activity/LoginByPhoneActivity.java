package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SPUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.utils.AddSpaceTextWatcher;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.presenter.LoginPresenter;
import com.tftechsz.mine.utils.UserManager;

import java.util.Random;


public class LoginByPhoneActivity extends BaseMvpActivity<ILoginView, LoginPresenter> implements View.OnClickListener, ILoginView {

    private EditText mEtPhone;
    private EditText mEtCode;
    private String mPhone;
    private TextView mTvTip;
    private TextView tvGetCode;
    private TextView tvLogin;
    private final int mMax = 2000;
    private final int mMin = 1000;
    private CountBackUtils countBackUtils;
    private String mOldToken;

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.with(mActivity).transparentStatusBar().navigationBarDarkIcon(false).navigationBarColor(R.color.black).statusBarDarkFont(false, 0.2f).init();
        countBackUtils = new CountBackUtils();
        new ToolBarBuilder().showBack(true)
                .setTitle("手机号码登录/注册")
                .setTitleColor(R.color.white)
                .setRightTextColor(R.color.white)
                .setBackgroundColor(0)
                .setBackTint(R.color.white)
                .build();
        mEtPhone = findViewById(R.id.et_phone);
        mEtCode = findViewById(R.id.et_code);
        mTvTip = findViewById(R.id.tv_tip);
        tvGetCode = findViewById(R.id.tv_get_code);
        tvLogin = findViewById(R.id.tv_login);
        tvGetCode.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        findViewById(R.id.tv_one_key_login).setOnClickListener(this);
        mEtPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (tvGetCode.isEnabled())
                    tvGetCode.performClick();
                else
                    Utils.setFocus(mEtCode);
                return true;
            }
            return false;
        });
        mEtCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                toLogin();
                return true;
            }
            return false;
        });
        mEtPhone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/gilroy-bold-4.otf"));
        mEtCode.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/gilroy-bold-4.otf"));
        mEtPhone.addTextChangedListener(new MyInputFilter());
        mEtCode.addTextChangedListener(new MyInputFilter());
        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() == 4) {
                    toLogin();
                }
            }
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
        mOldToken = UserManager.getInstance().getToken();
        Random random = new Random();
        int number = random.nextInt(mMax - mMin) + mMin + 1;
        String text1 = "附近有 ";
        String text2 = number + "位“已真人认证”";
        String text3 = " 用户在线\n登录即可查看";
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(text1).append(text2).append(text3);
        spanString.append(text1).append(text2).append(text3);
        int start = stringBuffer.indexOf(text2);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFD226")), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, start + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvTip.setText(spanString);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_get_code) {   //获取验证码
            mPhone = mEtPhone.getText().toString().replace(" ", "");
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
        } else if (id == R.id.tv_login) {
            toLogin();
        } else if (id == R.id.tv_one_key_login) {
            finish();
        }
    }

    private void toLogin() {
        mPhone = mEtPhone.getText().toString().replace(" ", "");
        if (Utils.checkNoTell(mPhone)) {
            return;
        }
        String code = mEtCode.getText().toString();
        if (!TextUtils.isEmpty(code) && code.length() == 4) {
            KeyboardUtils.hideSoftInput(this);
            login(code);
        }
    }

    private void login(String code) {
        LoginReq param = new LoginReq();
        LoginReq.LoginData data = new LoginReq.LoginData();
        data.code = code;
        data.phone = mPhone;
        param.data = data;
        param.type = "phone_code";
        param.key = MMKVUtils.getInstance().decodeString(Constants.KEY_H5_PARAM);
        param.from_channel = Utils.getChannel();
        getP().userLogin(this, param, 0);
    }

    private void countTime() {
        countBackUtils.countBack(Interfaces.WAITING_TIME, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                tvGetCode.setText("重新获取(" + time + "s)");
                tvGetCode.setEnabled(false);
                tvGetCode.setAlpha(0.4f);
            }

            @Override
            public void finish() {
                tvGetCode.setText("重新获取");
                tvGetCode.setEnabled(true);
                tvGetCode.setAlpha(1f);
            }
        });
    }

    /**
     * 成功获取验证码
     */
    @Override
    public void getCodeSuccess(String data) {
        mEtCode.setText("");
        Utils.setFocus(mEtCode);
        countTime();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.equals(mOldToken, UserManager.getInstance().getToken())) {
            SPUtils.getInstance().put(Interfaces.SP_PHONE, mPhone);
        }
        countBackUtils.destroy();
    }


    private void editListener() {
        String code = Utils.getText(mEtCode);
        tvLogin.setEnabled(!TextUtils.isEmpty(Utils.getText(mEtPhone))
                && !TextUtils.isEmpty(code) && code.length() == 4);
    }

    private class MyInputFilter implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editListener();
        }
    }
}
