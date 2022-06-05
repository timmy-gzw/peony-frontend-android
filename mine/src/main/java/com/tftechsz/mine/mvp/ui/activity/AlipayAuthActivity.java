package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alipay.mobile.android.verify.sdk.ServiceFactory;
import com.blankj.utilcode.util.RegexUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.mine.R;
import com.tftechsz.mine.mvp.IView.IAlipayAuthView;
import com.tftechsz.mine.mvp.presenter.AlipayAuthPresenter;

/**
 * 支付宝实名认证
 */
public class AlipayAuthActivity extends BaseMvpActivity<IAlipayAuthView, AlipayAuthPresenter> implements View.OnClickListener, IAlipayAuthView {

    private String certifyId;
    private EditText mEtName, mEtAccount, mEtCard;
    private TextView mTvSure;
    private boolean waitForResult = false;

    @Override
    public AlipayAuthPresenter initPresenter() {
        return new AlipayAuthPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .build();
        mEtName = findViewById(R.id.et_name);
        mEtAccount = findViewById(R.id.et_account);
        mEtCard = findViewById(R.id.et_card);
        mTvSure = findViewById(R.id.tv_sure);
        initListener();
        queryCertifyResult(getIntent());
    }

    private void initListener() {
        mTvSure.setOnClickListener(this);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_alipay_auth;
    }


    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sure) {  //// 提交
            String account = mEtAccount.getText().toString();
            String name = mEtName.getText().toString();
            String card = mEtCard.getText().toString();
            if (TextUtils.isEmpty(account)) {
                toastTip("支付宝账号不能为空");
                return;
            }
            if (TextUtils.isEmpty(name)) {
                toastTip("姓名不能为空");
                return;
            }
            if (TextUtils.isEmpty(card)) {
                toastTip("身份证号码不能为空");
                return;
            }
            if (!RegexUtils.isIDCard18Exact(card)) {
                toastTip("请输入正确的身份证号码");
                return;
            }
            getP().userCertify(name, card, account);
        }
    }

    /**
     * 处理通过schema跳转过来的结果查询
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        queryCertifyResult(intent);
    }

    /**
     * 处理回前台触发结果查询
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (waitForResult) {
            // 查询认证结果
            waitForResult = false;
        }
    }

    /**
     * 业务方查询结果
     *
     * @param intent
     */
    protected void queryCertifyResult(Intent intent) {
        if (intent == null) {
            return;
        }
        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        // 如果有很多场景会通过schema调用到当前页面，建议在传给认证回跳的schema中，增加参数识别出需要查询认证结果的场景
        String param = data.getQueryParameter("queryResult");
        if ("true".equals(param)) {
            // 查询认证结果
            waitForResult = false;// 防止走到onresume中再次查询结果
            getP().checkCertify(certifyId);
        }
    }

    /**
     * 支付宝实名认证
     */
    public void alipayCertify(String url, String certifyId) {
        String bizCode = ServiceFactory.build().getBizCode(this);
        // 封装认证数据
        JSONObject requestInfo = new JSONObject();
        requestInfo.put("url", url);
        requestInfo.put("certifyId", certifyId);
        requestInfo.put("bizCode", bizCode);
        // 发起认证
        ServiceFactory.build().startService(AlipayAuthActivity.this, requestInfo, response -> {
            String responseCode = response.get("resultStatus");
            if ("9001".equals(responseCode)) {
                // 9001需要等待回调/回前台查询认证结果
                waitForResult = true;
            } else if ("9000".equals(responseCode)) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                toastTip("认证成功");
                // 查询认证结果
            } else {
                toastTip("认证失败,请重新认证");
            }
        });


    }

    @Override
    public void userCertifySuccess(String url, String certifyId) {
        this.certifyId = certifyId;
        alipayCertify(url, certifyId);
    }

    @Override
    public void checkCertifySuccess(Boolean data) {
        if (data) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            toastTip("认证成功");
            finish();
        } else {
            toastTip("认证失败,请重新认证");
        }

    }
}
