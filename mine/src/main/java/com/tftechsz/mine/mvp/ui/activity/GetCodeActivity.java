package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.presenter.LoginPresenter;
import com.tftechsz.mine.utils.UserManager;
import com.tftechsz.mine.widget.PhoneCode;

/**
 * 获取验证码
 */
public class GetCodeActivity extends BaseMvpActivity<ILoginView, LoginPresenter> implements View.OnClickListener, ILoginView {
    private TextView mTvTip;
    private PhoneCode phoneCode;
    private TextView mTvGetCode; // 获取验证码
    private String mPhone;
    private CountBackUtils countBackUtils;
    private String mOldToken;

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        countBackUtils = new CountBackUtils();
        new ToolBarBuilder().showBack(true)
                .setTitle("")
                .build();
        phoneCode = findViewById(R.id.phone_code);
        mTvTip = findViewById(R.id.tv_tip);
        mTvGetCode = findViewById(R.id.tv_get_code);
        mTvGetCode.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_get_code;
    }


    @Override
    protected void initData() {
        super.initData();
        mOldToken = UserManager.getInstance().getToken();
        mPhone = getIntent().getStringExtra("phone");
        mTvTip.setText("验证码已发送到: +" + mPhone);
        countTime();
        phoneCode.setOnInputListener(new PhoneCode.OnInputListener() {
            @Override
            public void onSuccess(String numberCode) {
                login(numberCode);

            }

            @Override
            public void onInput() {

            }
        });
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
        getP().userLogin(GetCodeActivity.this, param, 0);
    }


    private void countTime() {
        countBackUtils.countBack(Interfaces.WAITING_TIME, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                mTvGetCode.setText("重新获取(" + time + ")");
                mTvGetCode.setEnabled(false);
                mTvGetCode.setBackgroundResource(R.drawable.shape_bg_login_phone);
                mTvGetCode.setTextColor(Color.parseColor("#999999"));

            }

            @Override
            public void finish() {
                mTvGetCode.setText("重新获取");
                mTvGetCode.setEnabled(true);
                mTvGetCode.setBackgroundResource(R.drawable.shape_login_btn);
                mTvGetCode.setTextColor(Color.parseColor("#ffffff"));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_get_code) {
            LoginReq loginReq = new LoginReq();
            loginReq.phone = mPhone;
            p.sendCode(loginReq, false);

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TextUtils.equals(mOldToken, UserManager.getInstance().getToken())) {
            SPUtils.getInstance().put(Interfaces.SP_PHONE, mPhone);
        }
        countBackUtils.destroy();
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
    public void getCodeSuccess(String data) {
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
    public void onGetReviewConfig(boolean r) {

    }
}
