package com.tftechsz.mine.mvp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.BuildConfig;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.presenter.LoginPresenter;
import com.tftechsz.mine.widget.TextClick;
import com.tftechsz.mine.widget.pop.PrivacyPopWindow;
import com.umeng.umlink.MobclickLink;
import com.umeng.umlink.UMLinkListener;

import java.util.HashMap;

/**
 * 登陆页面
 */
@Route(path = ARouterApi.MINE_LOGIN)
public class LoginActivity extends BaseMvpActivity<ILoginView, LoginPresenter> implements View.OnClickListener, ILoginView {

    private TextView mTvOtherLogin;
    private TextView mTvAgreement;
    private ChatMsg.JumpMessage mJumpMessage;
    private final long[] hits = new long[5];
    private CheckBox mCheckBox;
    @Autowired
    UserProviderService service;
    private int type = 0;
    private LinearLayout mLlBottom;
    private boolean isFirst;
    private int umLinkCount = 0;


    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.iv_url).setOnClickListener(this);
        mCheckBox = findViewById(R.id.checkbox);
        mTvOtherLogin = findViewById(R.id.tv_other_phone);
        mTvOtherLogin.setVisibility(View.GONE);
        mTvAgreement = findViewById(R.id.tv_agreement);
        mLlBottom = findViewById(R.id.ll_bottom);
        findViewById(R.id.tv_wx_login).setOnClickListener(this);  //微信登录
        findViewById(R.id.tv_qq_login).setOnClickListener(this);   //QQ 登录
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mLlBottom.getLayoutParams();
        lp.bottomMargin = ImmersionBar.getNavigationBarHeight(mActivity) + ConvertUtils.dp2px(22);
        mLlBottom.setLayoutParams(lp);
        Uri data = getIntent().getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
        boolean hasGetInstallParams = MMKVUtils.getInstance().decodeBoolean(Constants.KEY_HAS_GET_INSTALL_PARAMS);
        if (!hasGetInstallParams) {
            //从来没调用过getInstallParam方法，适当延时调用getInstallParam方法
            MobclickLink.getInstallParams(LoginActivity.this, umlinkAdapter);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        super.initData();
        ImmersionBar.with(this).statusBarDarkFont(true).transparentBar().init();
        getP().uploadDeviceInfo(MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID), 0, "", "default", "");
        initRxBus();
        mJumpMessage = (ChatMsg.JumpMessage) getIntent().getSerializableExtra("jumpMessage");
        try {
            Utils.runOnUiThreadDelayed(() -> {
                if (mJumpMessage != null) {
                    ChatMsg chatMsg = mJumpMessage.cmd_data;
                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //弹窗
                        final ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
                        if (alert.template != 2) {
                            CommonUtil.showCustomPop(alert);
                        }
                    }
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.runOnUiThreadDelayed(this::setShowPop, 1000);
        mCheckBox.setChecked(false);
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MMKVUtils.getInstance().encode(Constants.AGREED_TO_TOS, isChecked);
        });
        ConfigInfo configInfo = service.getConfigInfo();
        String res = "我已阅读并同意《用户协议》和《隐私政策》";
        if (configInfo != null && configInfo.sys.user_protocol == 1) {
            mCheckBox.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.GONE);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(res);
        int stat = res.indexOf("《");
        builder.setSpan(new TextClick(this, 0), stat, stat + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#5397FF")), stat, stat + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new TextClick(this, 1), stat + 7, stat + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#5397FF")), stat + 7, stat + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvAgreement.setText(builder);
        mTvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_WX_LOGIN_SUCCESS) {
                                if (!AppManager.getAppManager().currentActivity().getLocalClassName().equals(getLocalClassName())) {
                                    return;
                                }
                                LoginReq param = new LoginReq();
                                LoginReq.LoginData data = new LoginReq.LoginData();
                                data.code = event.code;
                                param.data = data;
                                param.type = "wechat";
                                param.key = MMKVUtils.getInstance().decodeString(Constants.KEY_H5_PARAM);
                                param.from_channel = Utils.getChannel();
                                p.userLogin(mActivity, param, 1);
                            }
                        }
                ));
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

    }

    @Override
    public void loginSuccess(LoginDto data) {

    }

    @Override
    public void getConfigSuccess(String msg, int flag) {

    }


    @Override
    public void agreementSuccess() {
        if (type == 1) {
            startActivity(new Intent(this, LoginByPhoneActivity.class));
        } else if (type == 2) {
            p.loginWx(this);
        }
    }

    @Override
    public void onGetReviewConfig(boolean r) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.removeHandler();
    }


    //双击退出app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_login) {
            type = 1;
        } else if (id == R.id.tv_wx_login) {   //微信登录
            type = 2;
        } else if (id == R.id.tv_qq_login) {  //QQ登录

        } else if (id == R.id.iv_url) { //切换环境
            if (BuildConfig.DEBUG) {
                type = 3;
                //将mHints数组内的所有元素左移一个位置
                System.arraycopy(hits, 1, hits, 0, hits.length - 1);
                //获得当前系统已经启动的时间
                hits[hits.length - 1] = SystemClock.uptimeMillis();
                if (SystemClock.uptimeMillis() - hits[0] <= 2000) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("选择环境")
                            .setItems(R.array.host_name, (dialogInterface, i) -> {
                                CharSequence[] textArray = getResources().getTextArray(R.array.host_url);
                                SPUtils.put(Constants.CURRENT_HOST, textArray[i]);
                                new Handler(getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                }, 1000);
                            })
                            .create();
                    alertDialog.show();
                }
            }
        }
        isFirst = true;
        setShowPop();

    }


    private void setShowPop() {
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.sys.user_protocol == 1) {   //审核中状态
            int isAgree = MMKVUtils.getInstance().decodeInt(Constants.IS_AGREE_AGREEMENT);
            if (isAgree == 0) {
                PrivacyPopWindow popWindow = new PrivacyPopWindow(LoginActivity.this);
                popWindow.setPrivacyListener(new PrivacyPopWindow.PrivacyListener() {
                    @Override
                    public void agree() {
                        isFirst = true;
                    }

                    @Override
                    public void cancel() {
                        finish();
                    }
                });
                popWindow.showPopupWindow();
            } else {
                if (mCheckBox.isChecked()) {
                    goLogin();
                } else {
                    if (isFirst) {
                        toastTip("请阅读并勾选协议");
                        TranslateAnimation animation = new TranslateAnimation(0, -20, 0, 0);
                        animation.setInterpolator(new OvershootInterpolator());
                        animation.setDuration(80);
                        animation.setRepeatCount(2);
                        animation.setRepeatMode(Animation.REVERSE);
                        mLlBottom.startAnimation(animation);
                    }
                }
            }
        } else {
            getP().showPop(this);
        }
    }


    UMLinkListener umlinkAdapter = new UMLinkListener() {
        @Override
        public void onLink(String path, HashMap<String, String> query_params) {
            if (!query_params.isEmpty() && query_params.containsKey("invite_code") && MMKVUtils.getInstance().decodeString(Constants.H5_INVITE_CODE_PARAM).isEmpty()) {
                String invite_code = query_params.get("invite_code");
                MMKVUtils.getInstance().encode(Constants.H5_INVITE_CODE_PARAM, invite_code);
            }
        }

        @Override
        public void onInstall(HashMap<String, String> install_params, Uri uri) {
            if (install_params.isEmpty() && uri.toString().isEmpty()) {
            } else {
                if (!uri.toString().isEmpty()) {
                    MobclickLink.handleUMLinkURI(mContext, uri, umlinkAdapter);
                    String key = uri.getQueryParameter("key");
                    MMKVUtils.getInstance().encode(Constants.KEY_H5_PARAM, key);
                    getP().uploadDeviceInfo(MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID), 0, uri.getQueryParameter("channel_id"), "h5", key);
                    MMKVUtils.getInstance().encode(Constants.KEY_HAS_GET_INSTALL_PARAMS, true);
                } else {
                    if (umLinkCount == 0) {
                        MobclickLink.getInstallParams(LoginActivity.this, false, umlinkAdapter);
                        umLinkCount++;
                        MMKVUtils.getInstance().encode(Constants.KEY_HAS_GET_INSTALL_PARAMS, true);
                    }
                }
                //友盟新装带参获取
                if (!install_params.isEmpty() && install_params.containsKey("invite_code")) {
                    String invite_code = install_params.get("invite_code");
                    MMKVUtils.getInstance().encode(Constants.H5_INVITE_CODE_PARAM, invite_code);
                }
            }

        }

        @Override
        public void onError(String error) {
        }
    };

    private void goLogin() {
        if (type == 1) {
            startActivity(new Intent(this, LoginByPhoneActivity.class));
        } else if (type == 2) {
            p.loginWx(this);
        }
    }

}
