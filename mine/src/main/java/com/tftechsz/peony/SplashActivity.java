package com.tftechsz.peony;

import static java.lang.Boolean.TRUE;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.TimeUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.mobsec.InitCallback;
import com.netease.mobsec.WatchMan;
import com.netease.mobsec.WatchManConf;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.push.MyMixPushMessageHandler;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.presenter.LoginPresenter;
import com.tftechsz.mine.mvp.ui.activity.LoginActivity;
import com.tftechsz.mine.utils.UserManager;
import com.tftechsz.mine.widget.pop.PrivacyPopWindow;
import com.umeng.umlink.MobclickLink;
import com.umeng.umlink.UMLinkListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 放在包下面处理切换logo找不到资源文件问题
 */
public class SplashActivity extends BaseMvpActivity<ILoginView, LoginPresenter> implements ILoginView, CancelAdapt {


    @Autowired
    UserProviderService service;
    private static boolean firstEnter = true; // 是否首次进入
    private int requestCount = 0;
    private int umLinkCount = 0;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BaseApplication.APP_STATUS = BaseApplication.APP_STATUS_NORMAL; // App正常的启动，设置App的启动状态为正常启动
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected boolean getImmersionBar() {
        return false;
    }


    @Override
    protected void initData() {
        super.initData();
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        UserManager.init(this);
        //是否完善个人信息
        int isComplete = SPUtils.getInt(Constants.IS_COMPLETE_INFO, 0);
        if (isComplete != 0) {
            getP().appInitUser(0);
        }
        initRxBus();
        Uri data = getIntent().getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
        MMKVUtils.getInstance().encode(Constants.UPDATE_CONFIG_LAUNCH, 0);
        int isAgree = MMKVUtils.getInstance().decodeInt(Constants.IS_AGREE_AGREEMENT);
        if (isAgree == 0) {
            PrivacyPopWindow popWindow = new PrivacyPopWindow(this);
            popWindow.setPrivacyListener(new PrivacyPopWindow.PrivacyListener() {
                @Override
                public void agree() {
                    getP().lazyInit();
                    initPermission();
                }

                @Override
                public void cancel() {
                    finish();
                }
            });
            popWindow.showPopupWindow();
        } else {
            initPermission();
        }
    }


    private void initPermission() {
        getP().getReviewConfig();
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_WX_LOGIN_SUCCESS) {
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


    private void loadData() {
        WatchManConf conf = new WatchManConf();
        conf.setCollectApk(TRUE);
        conf.setCollectSensor(TRUE);
        WatchMan.init(getApplicationContext(), Constants.YUNXIN_PRODUCT_NUMBER, conf, new InitCallback() {
            @Override
            public void onResult(int code, String msg) {
                Log.e("==", "init OnResult , code = " + code + " msg = " + msg);
            }
        });
        boolean hasGetInstallParams = MMKVUtils.getInstance().decodeBoolean(Constants.KEY_HAS_GET_INSTALL_PARAMS);
        if (!hasGetInstallParams) {
            //从来没调用过getInstallParam方法，适当延时调用getInstallParam方法
            MobclickLink.getInstallParams(SplashActivity.this, umlinkAdapter);
        }
        if (firstEnter) {
            SPUtils.remove(Constants.INVITED_EVENT);
            Utils.runOnUiThread(() -> {
                if (canAutoLogin()) {
                    onIntent();
                } else {
                    enter();
                }
            });
            firstEnter = false;
        } else {
            onIntent(); // APP进程还在，Activity被重新调度起来
        }
    }

    private void loadMoreData() {
        //   MdidSdkHelper.InitSdk()
        MMKVUtils.getInstance().encode(Constants.CURRENT_TIME, System.currentTimeMillis());
        getP().uploadDeviceInfo(MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID), 0, "", "default", "");
        loadData();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (firstEnter) {
            firstEnter = false;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int isAgree = MMKVUtils.getInstance().decodeInt(Constants.IS_AGREE_AGREEMENT);
        if (isAgree != 1) return;
        Uri data = intent.getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
        setIntent(intent);
        onIntent();
    }


    // 处理收到的Intent
    private void onIntent() {
        // 已经登录过了，处理过来的请求
        Intent intent = getIntent();
        if (intent != null) {
            LogUtil.e("=====================", (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT) || intent.hasExtra(NimIntent.EXTRA_NOTIFY_SESSION_CONTENT)) + "" + intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT) + intent.hasExtra(NimIntent.EXTRA_NOTIFY_SESSION_CONTENT));
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT) || intent.hasExtra(NimIntent.EXTRA_NOTIFY_SESSION_CONTENT)) {
                parseNotifyIntent(intent);

            } else if (NIMClient.getService(MixPushService.class).isFCMIntent(intent)) {
                parseFCMNotifyIntent(NIMClient.getService(MixPushService.class).parseFCMPayload(intent));
                return;
            } else {
                enter();
                return;
            }
        }
        if (!firstEnter) {
            finish();
        } else {
            int isComplete = SPUtils.getInt(Constants.IS_COMPLETE_INFO, 0);
            if (isComplete == 1) {
                ARouterUtils.toMainActivity(null);
            } else {
                enter();
            }
        }
    }


    private boolean canAutoLogin() {
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        String account = String.valueOf(service.getUserId());
        String token = service.getToken();
        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }

    private void parseFCMNotifyIntent(String payloadString) {
        Map<String, String> payload = JSON.parseObject(payloadString, Map.class);
        String sessionId = payload.get(MyMixPushMessageHandler.PAYLOAD_SESSION_ID);
        String type = payload.get(MyMixPushMessageHandler.PAYLOAD_SESSION_TYPE);
        if (sessionId != null && type != null) {
            int typeValue = Integer.parseInt(type);
            IMMessage message = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
            ARouterUtils.toMainActivity(message);
        } else {
            ARouterUtils.toMainActivity(null);
        }
    }


    private void parseNotifyIntent(Intent intent) {
        if (SPUtils.getInt(Constants.IS_COMPLETE_INFO, 0) != 1) {
            startActivity(LoginActivity.class);
            finish();
            return;
        }
        IMMessage msg = null;
        ArrayList<IMMessage> msgListExtra = null;
        try {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                msgListExtra = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                msg = (CommonUtil.isEmpty(msgListExtra) || msgListExtra.size() > 1) ? null : msgListExtra.get(0);
            } else if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_SESSION_CONTENT)) {
                String sessionInfoExtra = intent.getStringExtra(NimIntent.EXTRA_NOTIFY_SESSION_CONTENT);
                JSONArray arr = new JSONArray(sessionInfoExtra);
                if (arr.length() > 0 && arr.length() < 2) {
                    JSONObject firstObj = arr.optJSONObject(0);
                    String uuid = firstObj.optString("uuid");
                    List<String> uuids = new ArrayList<>();
                    uuids.add(uuid);
                    NIMClient.getService(MsgService.class).queryMessageListByUuid(uuids).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                        @Override
                        public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                            if (messages != null && messages.size() > 0) {
                                ChatMsg chatMsg = ChatMsgUtil.parseMessage(messages.get(0).getCallbackExtension());
                                if (chatMsg != null) {
                                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {
                                        InvitedEvent invitedEvent = (InvitedEvent) SPUtils.readObject(BaseApplication.getInstance(), Constants.INVITED_EVENT);
                                        if (invitedEvent != null)
                                            ARouterUtils.toCallActivity(invitedEvent, service.getCallType(), service.getMatchType(), service.getCallId(), service.getCallIsMatch());
                                    }
                                }
                            }
                        }
                    });
                    String sessionId = firstObj.optString("sessionId");
                    SessionTypeEnum sessionType = SessionTypeEnum.typeOfValue(firstObj.optInt("sessionType"));
                    long time = firstObj.optLong("time");
                    msg = MessageBuilder.createEmptyMessage(sessionId, sessionType, time);
                }
            }
        } catch (Throwable ignore) {
        }
        ARouterUtils.toMainActivity(msg);
        finish();
    }


    /**
     * 获取config后进入页面
     */
    private void enter() {
        /* XXX: check this */
//        if (service.getConfigInfo() == null) {
//            p.getConfig(0);
//        } else {
//            p.getConfig(1);
//
//        }
        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance()) && service.getConfigInfo() != null) {
            enterMain();
        } else {
            p.getConfig(0);
        }

    }


    private void enterMain() {
        //是否完善个人信息
        int isComplete = SPUtils.getInt(Constants.IS_COMPLETE_INFO, 0);
        if (isComplete == 0) {
            p.openLoginAuth(SplashActivity.this);
        } else {   //跳转主页
            String token = UserManager.getInstance().getToken();
            if (TextUtils.isEmpty(token)) {
                p.openLoginAuth(SplashActivity.this);
            } else {
                p.intentMain(this, String.valueOf(UserManager.getInstance().getUserId()), token, null, 0);
            }
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
                    MMKVUtils.getInstance().encode(Constants.KEY_H5_PARAM, TextUtils.isEmpty(key) ? "" : key);
                    getP().uploadDeviceInfo(MMKVUtils.getInstance().decodeString(Interfaces.SP_OAID), 0, uri.getQueryParameter("channel_id"), "h5", TextUtils.isEmpty(key) ? "" : key);
                    MMKVUtils.getInstance().encode(Constants.KEY_HAS_GET_INSTALL_PARAMS, true);
                } else {
                    if (umLinkCount == 0) {
                        MobclickLink.getInstallParams(SplashActivity.this, false, umlinkAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneKeyLoginManager.getInstance().finishAuthActivity();
        ImmersionBar.destroy(this, null);
    }

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void getOpenLoginAuthStatusSuccess() {
        Utils.runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (p != null)
                    p.showPop(SplashActivity.this);
            }
        }, 1000);
    }

    @Override
    public void getOpenLoginAuthStatusFail() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onFail(int flag) {
        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance()) || flag == 3) {
            toastTip("连接失败，请稍后重试");
            finish();
            return;
        }
        if (flag == 0 || flag == 1) {
            requestCount++;
            if (requestCount < 2) {
                getP().getConfig(0);
            } else {
                getP().downloadLaunchConfig();
            }
        } else if (flag == 2) {
            getP().getConfig(3);
        }
    }

    @Override
    public void cancelLoginAuth() {
        finish();
    }

    @Override
    public void getCodeSuccess(String data) {

    }

    /**
     *
     */
    @Override
    public void loginSuccess(LoginDto data) {
        ARouterUtils.toMainActivity();
        finish();
    }

    @Override
    public void getConfigSuccess(String msg, int flag) {
        if (requestCount <= 3 && (flag == 0 || flag == 2 || flag == 3)) {
            enterMain();
        } else {
            toastTip("服务器升级中!");
            finish();
        }

    }

    @Override
    public void agreementSuccess() {

    }

    @Override
    public void onGetReviewConfig(boolean r) {
        long time = MMKVUtils.getInstance().decodeLong(Constants.CURRENT_TIME);
        if (r || (time != 0 && TimeUtils.isToday(new Date(time)))) {
            loadMoreData();
        } else {
            mCompositeDisposable.add(new RxPermissions(SplashActivity.this)
                    .request(Manifest.permission.READ_PHONE_STATE)
                    .subscribe(aBoolean -> loadMoreData()));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return false;
    }

}
