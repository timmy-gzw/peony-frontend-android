package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.widget.PhoneCode;

public class YouthModelPassActivity extends BaseMvpActivity implements View.OnClickListener {
    private PhoneCode youthPass;
    private TextView mTvTitle;
    private boolean isFirst = true;
    private String mPass, mPass1;

    @Override
    protected int getLayout() {
        return R.layout.activty_youth_model_pass;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("青少年模式").showBack(true).build();
        youthPass = findViewById(R.id.phone_code);
        mPass = MMKVUtils.getInstance().decodeString(Constants.YOUTH_MODE_PASS);
        TextView tvNext = findViewById(R.id.tv_next);
        tvNext.setOnClickListener(this);
        mTvTitle = findViewById(R.id.tv_title);
        TextView tvForget = findViewById(R.id.tv_forget);
        tvForget.setOnClickListener(this);
        if (!TextUtils.isEmpty(mPass)) {
            mTvTitle.setText("请输入密码");
            tvForget.setVisibility(View.VISIBLE);
        }else{
            tvForget.setVisibility(View.GONE);
        }
        youthPass.setOnInputListener(new PhoneCode.OnInputListener() {
            @Override
            public void onSuccess(String numberCode) {
                tvNext.setEnabled(true);
                tvNext.setTextColor(Utils.getColor(R.color.white));
            }
            @Override
            public void onInput() {
                tvNext.setEnabled(false);
                tvNext.setTextColor(Utils.getColor(R.color.color_mid_font));
            }
        });

    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_next) {
            if (TextUtils.isEmpty(mPass)) {
                if (isFirst) {
                    mPass1 = youthPass.getPhoneCode();
                    isFirst = false;
                    youthPass.clearCode();
                    mTvTitle.setText("确认密码");
                } else {
                    String pass2 = youthPass.getPhoneCode();
                    if (TextUtils.equals(mPass1, pass2)) {
                        MMKVUtils.getInstance().encode(Constants.YOUTH_MODE_PASS, pass2);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Utils.toast("密码输入不一致");
                    }
                }
            } else {
                String ps = youthPass.getPhoneCode();
                if (TextUtils.equals(ps, mPass)) {
                    MMKVUtils.getInstance().removeKey(Constants.YOUTH_MODE_PASS);
                    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Utils.toast("密码错误，请重新输入");
                }
            }

        }else if(view.getId() == R.id.tv_forget){
            SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
            MMKVUtils.getInstance().removeKey(Constants.YOUTH_MODE_PASS);
            AppManager.getAppManager().finishAllActivity();
            ARouterUtils.toLoginActivity(ARouterApi.MINE_LOGIN);
            NIMClient.getService(AuthService.class).logout();
        }
    }
}
