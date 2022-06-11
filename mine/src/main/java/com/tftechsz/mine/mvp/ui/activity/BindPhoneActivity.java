package com.tftechsz.mine.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ActBindPhoneBinding;
import com.tftechsz.mine.entity.req.BindData;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.mine.mvp.IView.IBindPhoneView;
import com.tftechsz.mine.mvp.presenter.BindPhonePresenter;
import com.tftechsz.mine.mvp.ui.fragment.BindPhoneFragment_1;
import com.tftechsz.mine.mvp.ui.fragment.BindPhoneFragment_2;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 绑定电话
 */
public class BindPhoneActivity extends BaseMvpActivity<IBindPhoneView, BindPhonePresenter> implements BindPhoneFragment_1.EdtCallBack, IBindPhoneView, BindPhoneFragment_2.CodeCallBack {
    private ActBindPhoneBinding mBind;
    private String tell,code;
    private int mType; // 0:注册绑定  1:换绑  2:未绑定
    private String mPhone;
    private CountBackUtils countBackUtils;
    private CompleteReq mCompleteReq;

    @Override
    public BindPhonePresenter initPresenter() {
        return new BindPhonePresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_bind_phone);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(Interfaces.EXTRA_TYPE, 0);
        mPhone = getIntent().getStringExtra(Interfaces.EXTRA_PHONE);
        mCompleteReq = (CompleteReq) getIntent().getSerializableExtra(Interfaces.EXTRA_DATA);
        new ToolBarBuilder().setTitle("绑定手机号").showBack(true).build();
        initTopHint();

        countBackUtils = new CountBackUtils();

        mBind.btn.setOnClickListener(v -> {
            if (Utils.checkNoTell(tell)) {
                return;
            }
            if (!ClickUtil.canOperate()) {
                return;
            }
            LoginReq req = new LoginReq();
            req.phone = tell;
            req.type = "bind";
            p.sendCode(req);
        });
        mBind.phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tell = s.toString();
            }
        });
        mBind.sure.setOnClickListener(v->{
            if (Utils.checkNoTell(tell)) {
                return;
            }
            if (code.length()<4) {
                Utils.toast("请输入正确的验证码");
                return;
            }
            if (!ClickUtil.canOperate()) {
                return;
            }
            codeChange(code);
        });
        mBind.codeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    code = s.toString();
            }
        });
    }

    private void initTopHint() {
        switch (mType) {
            case 0: //注册绑定
                mBind.topHint.setText("离交友只差一步");
                break;
            case 1: //换绑
                mBind.topHint.setText("输入替换手机号");
                break;
            case 2: //未绑定
                mBind.topHint.setText("输入绑定手机号");
                break;
        }
        mBind.tvTip.setText("请绑定您的手机号码");
    }

    @Override
    public void edtChange(String str) {
        tell = str.replace(" ", "");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getCodeSuccess(String data) {
//        mBind.viewpager.setCurrentItem(1, true);
//        mFragment2.showSoftInput();
        mBind.topHint.setText("输入验证码");
        mBind.tvTip.setText("验证码已发送到 " + StringUtils.hintPhone(tell));
        performBtn(true, Interfaces.WAITING_TIME);
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
    public void completeInfoSuccess(String data) {
        MobclickAgent.onEvent(this, "user_register");
        SPUtils.put(Constants.IS_COMPLETE_INFO, 1);
        p.getUserInfo(this);
    }

    private void performBtn(boolean b, long waitingTime) {
        if (b) {
            mBind.btn.setText(String.format("重新获取(%ss)", waitingTime));
            mBind.btn.setEnabled(false);
            mBind.btn.setAlpha(0.4f);
        } else {
            mBind.btn.setText(getString(R.string.main_get_phone_code));
            mBind.btn.setEnabled(true);
            mBind.btn.setAlpha(1f);
        }
    }

    @Override
    public void codeChange(String code) {
        BindData data = new BindData("phone", new BindData.DataDTO(tell, code));
        p.bindData(data);
    }

    @Override
    public void bindPhoneSuccess(String data) { //手机绑定成功
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_BIND_PHONE_SUCCESS));
        switch (mType) {
            case 0: //注册绑定
                p.completeInfo(mCompleteReq);
                break;

            case 1: //换绑
            case 2: //未绑定
                com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_PHONE, tell);
                toastTip("手机号绑定成功");
                finish();
                break;
        }
    }
}
