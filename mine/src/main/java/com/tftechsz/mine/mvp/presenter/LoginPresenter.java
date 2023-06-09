package com.tftechsz.mine.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.MD5Util;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.sdk.engine.AIDParams;
import com.sdk.engine.RiskCallBack;
import com.sdk.engine.RiskControlEngine;
import com.sdk.engine.RiskErrorCode;
import com.sdk.engine.RiskInfo;
import com.sdk.engine.RiskRequestParams;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.ApiConstants;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.AnTianConfig;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.common.entity.PackageDto;
import com.tftechsz.common.entity.ReviewBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.NimCache;
import com.tftechsz.common.nim.UserPreferences;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AesUtil;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.CustomParams;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.BuildConfig;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.LoginDto;
import com.tftechsz.mine.entity.dto.NearUserDto;
import com.tftechsz.mine.mvp.IView.ILoginView;
import com.tftechsz.mine.mvp.ui.activity.GetCodeActivity;
import com.tftechsz.mine.mvp.ui.activity.ImproveInfoActivity;
import com.tftechsz.mine.mvp.ui.activity.LoginActivity;
import com.tftechsz.mine.mvp.ui.activity.LoginByPhoneActivity;
import com.tftechsz.mine.utils.ConfigUtils;
import com.tftechsz.mine.utils.UserManager;
import com.tftechsz.mine.widget.pop.PrivacyPopWindow;
import com.tftechsz.peony.SplashActivity;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginPresenter extends BasePresenter<ILoginView> {

    private MineApiService configService;
    private final PublicService publicService;
    private final PublicService publicService2;
    private final UserProviderService userService;
    private IWXAPI api;

    public LoginPresenter() {
        UserManager.init(BaseApplication.getInstance());
        String url = SPUtils.getString(Constants.CURRENT_HOST);
        Utils.logE("baseurl=" + url);
        configService = RetrofitManager.getInstance().createApi(MineApiService.class, BuildConfig.DEBUG ? TextUtils.isEmpty(url) ? ApiConstants.HOST_TEST : url : ApiConstants.HOST);
        publicService = RetrofitManager.getInstance().createApi(PublicService.class, BuildConfig.DEBUG ? TextUtils.isEmpty(url) ? ApiConstants.HOST_TEST : url : ApiConstants.HOST);
        publicService2 =  RetrofitManager.getInstance().createUserApi(PublicService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);

    }

    /**
     * 微信登录
     */
    public void loginWx(Activity context) {
        if (!AppUtils.isWeChatAppInstalled(context)) {
            ToastUtil.showToast(context, "手机未安装微信");
            return;
        }
        regToWx();
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        api.sendReq(req);
    }

    public void regToWx() {
        ConfigInfo configInfo = userService.getConfigInfo();
        String weChatAppId = CommonUtil.getWeChatAppId(configInfo);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), weChatAppId);
        // 将应用的appId注册到微信
        api.registerApp(weChatAppId);
    }

    /**
     * 上传设备信息
     */
    public void uploadDeviceInfo(String oaid, int isFirst, String from_channel, String from_type, String key) {
        if (TextUtils.equals("default", from_type)) {
            Utils.runOnUiThreadDelayed(() -> {
                int isUpload = SPUtils.getInt(Constants.IS_UPLOAD_OAID, 0);
                if (isUpload == 1) return;
                addNet(configService.uploadDeviceInfo(Utils.getUmId(), AppUtils.getIMEI(BaseApplication.getInstance()), oaid, AppUtils.getOrigAndroidID(BaseApplication.getInstance())
                                , AppUtils.getOrigMacAddr(BaseApplication.getInstance()), from_channel, from_type, key, isFirst).compose(BasePresenter.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> response) {
                                SPUtils.put(Constants.IS_UPLOAD_OAID, 1);
                            }
                        }));
            }, 1000);
        } else {
            addNet(configService.uploadDeviceInfo(Utils.getUmId(), AppUtils.getIMEI(BaseApplication.getInstance()), oaid, AppUtils.getOrigAndroidID(BaseApplication.getInstance())
                            , AppUtils.getOrigMacAddr(BaseApplication.getInstance()), from_channel, from_type, key, isFirst).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                        }
                    }));
        }
    }

    /**
     * 合规延迟初始化
     */
    public void lazyInit(BaseApplication.OnGetOaidListener listener) {
        BaseApplication.getInstance().getOaid(listener);
        NIMClient.initSDK();
        BaseApplication.getInstance().initThirdParty();
    }

    /**
     * 统计云手机
     */
    public void buriedPoint() {
        List<PackageDto> list = CommonUtil.getPackageInfo();
        String json = new Gson().toJson(list);
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        LogUtils.e("========================设备信息", json);
        mineService.trackEvent("deviceType", "checkDevice", userService.getUserId() + "", json, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
            }
        });
    }


    /**
     * 获取配置
     *
     * @param flag 3. 使用HOST_RESERVE, 重试
     */
    public void getConfig(int flag) {
        if (flag == 3) {
            configService = RetrofitManager.getInstance().createApi(MineApiService.class, BuildConfig.DEBUG ? ApiConstants.HOST_TEST : ApiConstants.HOST_RESERVE);
        }
        addNet(configService.getConfig().compose(io_main())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (!TextUtils.isEmpty(response.getData())) {
                            try {
                                String key = response.getData().substring(0, 6);
                                byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                                String iv = MD5Util.toMD532(key).substring(0, 16);
                                byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                                LogUtil.e("------------", new String(jsonData));
                                userService.setConfigInfo(new String(jsonData));
                                if ((flag == 0 || flag == 2 || flag == 3) && null != getView()) {
                                    getView().getConfigSuccess(new String(jsonData), flag);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().onFail(flag);
                    }
                }));
    }

    /**
     * 下载配置信息
     */
    public void downloadLaunchConfig() {
        OkHttpClient ClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(BuildConfig.DEBUG ? ApiConstants.HOST_TEST_DOWN : ApiConstants.HOST_DOWN).build();
        Call call = ClientBuilder.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getConfig(3);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String url = Objects.requireNonNull(response.body()).string();
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    configService = RetrofitManager.getInstance().createApi(MineApiService.class, url + "/");
                    getConfig(2);
                } else {
                    getConfig(3);
                }
            }
        });
    }

    /**
     * 获取是否审核配置信息
     */
    public void getReviewConfig() {
        addNet(publicService.getReviewConfig(Utils.getApp().getPackageName(), Utils.getChannel(), AppUtils.getVersionName(BaseApplication.getInstance()))
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResourceSubscriber<ReviewBean>() {
                    @Override
                    public void onNext(ReviewBean reviewBean) {
                        MMKVUtils.getInstance().encode(Constants.KEY_IS_REVIEW, reviewBean.isR());
                        MMKVUtils.getInstance().encode(Constants.KEY_SRL, reviewBean.isSrl());
                        getView().onGetReviewConfig(reviewBean.isR());
                    }

                    @Override
                    public void onError(Throwable t) {
                        MMKVUtils.getInstance().encode(Constants.KEY_IS_REVIEW, true);
                        MMKVUtils.getInstance().encode(Constants.KEY_SRL, false);
                        getView().onGetReviewConfig(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    /**
     * 闪验登陆
     */
    public void openLoginAuth(Activity context) {
        OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(context, type -> {
            if (showPop(context)) {
                return;
            }
            if (type == 0) {   //微信登陆
                loginWx(context);
            } else if (type == 1) {  //QQ登陆
                if (null == getView()) return;
                getView().toastTip("暂未开放");
            } else if (type == 2) {
                Intent intent = new Intent(context, LoginByPhoneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }), null);
        OneKeyLoginManager.getInstance().openLoginAuth(false, (code, result) -> {
            if (1000 == code) {  //拉起授权页成功
                if (null == getView()) return;
                getView().getOpenLoginAuthStatusSuccess();
            } else {   //拉起授权页失败
                if (null == getView()) return;
                getView().getOpenLoginAuthStatusFail();
            }
        }, (code, result) -> {
            if (1011 == code) {  // Log.e("VVV", "用户点击授权页返回： _code==" + code + "   _result==" + result);
                if (null == getView()) return;
                getView().cancelLoginAuth();
            } else if (1000 == code) { //用户点击登录获取token成功
                OneKeyLoginManager.getInstance().setLoadingVisibility(false);
                if (showPop(null))
                    return;
                OneKeyLoginManager.getInstance().setLoadingVisibility(true);
                JSONObject jsonObject = JSON.parseObject(result);
                String token = jsonObject.getString("token");
                LoginReq param = new LoginReq();
                LoginReq.LoginData data = new LoginReq.LoginData();
                data.token = token;
                param.data = data;
                param.type = "phone_one";
                param.key = MMKVUtils.getInstance().decodeString(Constants.KEY_H5_PARAM);
                param.from_channel = Utils.getChannel();
                userLogin(context, param, 0);
            } else {  //用户点击登录获取token失败
            }

        });
    }



    public void getAnTianConfig(){
        addNet(publicService2.anTianConfig().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<AnTianConfig>>() {
                    @Override
                    public void onSuccess(BaseResponse<AnTianConfig> response) {
                        if (response.getData() != null) {
                            if(response.getData().result == 1){
                                AIDParams params = new AIDParams();
                                params.setIDParams(new CustomParams(BaseApplication.getInstance()));
                                int ret = RiskControlEngine.init(BaseApplication.getInstance(), params);
                                getToken(response.getData());
                            }
                        }
                    }
                }));
    }


    public void commitToken(String token){
        addNet(publicService2.commitToken(token).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null) {
                        }
                    }
                }));
    }


    /**
     * 获取token
     * ret:表示执行状态，-1风控sdk未初始化，0执行成功，1执行中
     */
    private void getToken(AnTianConfig data) {
        //构建请求参数
        RiskRequestParams requestParams = new RiskRequestParams.Builder()
                //设置设备ip地址
                .setIp(data.ip)
                //设置用户账户
                .setUserAccount(data.userAccount)
                //设置场景，1买量，2提现，3注册，4登录，5充值
                .setScene(data.scene)
                //设置接口的超时时间
                .setTimeout(data.timeout)
                .build();
        //异步请求获取token
        int ret = RiskControlEngine.getToken(requestParams, new RiskCallBack() {
            @Override
            public void onFinish(RiskInfo riskInfo) {
                int code = riskInfo.getResultCode();
                String token = riskInfo.getToken();
                if (code == RiskErrorCode.SUCCESS) {
                    commitToken(token);
                }
            }
        });
    }


    /**
     * 用户登陆  一键登陆
     *
     * @param pram
     */
    public void userLogin(Activity context, LoginReq pram, int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonUtil.getToken(new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
                    @Override
                    public void onSure() {
                        MineApiService service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
                        addNet(service.userLogin(pram).compose(BasePresenter.applySchedulers())
                                .subscribeWith(new ResponseObserver<BaseResponse<LoginDto>>(getView()) {
                                    @Override
                                    public void onSuccess(BaseResponse<LoginDto> response) {
                                        getAnTianConfig();

                                        UserManager.getInstance().setToken(response.getData().token);
                                        UserManager.getInstance().setUserId(response.getData().user_id);
                                        buriedPoint();
                                        doLogin(context, String.valueOf(response.getData().user_id), response.getData().token, response.getData(), type);
                                    }

                                    @Override
                                    public void onFail(int code, String msg) {
                                        super.onFail(code, msg);
                                        OneKeyLoginManager.getInstance().setLoadingVisibility(false);
                                        if (null == getView()) return;
                                        if (getView() != null) {
                                            getView().onFail(0);
                                        }

                                    }
                                }));
                    }
                });
            }
        }).start();
    }

    /**
     * 云信登陆
     */
    public void doLogin(Activity context, String account, String token, LoginDto data, int type) {
        LogUtil.i("TAG", "云信执行登录");
        NimUIKit.login(new LoginInfo(account, token, null, 0),
                new RequestCallback<LoginInfo>() {

                    @Override
                    public void onSuccess(LoginInfo param) {
                        intentMain(context, account, token, data, type);
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 302) {
                            LogUtil.i("TAG", "云信账号密码错误");
//                            ToastUtil.showToast(context, "账号密码错误");
//                            Intent intent = new Intent(context, LoginActivity.class);
//                            context.startActivity(intent);

                            intentMain(context, account, token, data, type);
                        } else {
                            doLogin(context, account, token, data, type);
                        }
                        if (context instanceof SplashActivity) {
                            context.finish();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        ToastUtil.showToast(context, "登录失败，请退出后重试");
                    }
                });
    }


    /**
     * 跳转主页面
     */
    public void intentMain(Activity context, String account, String token, LoginDto data, int type) {
        // 初始化消息提醒配置
        initNotificationConfig();
        NimCache.setAccount(account);
        NimUIKit.loginSuccess(account);
        //设置bugly上报id
        CrashReport.setUserId(account);
        if (data != null) {   //没有账号登录
            if (data.is_complete == 0) {  //未完善资料
                Intent intent = new Intent(context, ImproveInfoActivity.class);
                intent.putExtra("type", type);   //判断登录类型，手机号码还是第三方登录
                context.startActivity(intent);

                AppManager.getAppManager().finishActivity(SplashActivity.class);
                AppManager.getAppManager().finishActivity(LoginActivity.class);
                AppManager.getAppManager().finishActivity(GetCodeActivity.class);
            } else if (data.is_complete == 1) {   //  已经完善资料
                getUserInfo();
                SPUtils.put(Constants.IS_COMPLETE_INFO, 1);
            }
        } else {
            if (context instanceof GetCodeActivity) {
                context.finish();
            }
            if (null == getView()) return;
            String pass = MMKVUtils.getInstance().decodeString(Constants.YOUTH_MODE_PASS);
            if (!TextUtils.isEmpty(pass)) {
                ARouterUtils.toYouthModelActivity();
                return;
            }
            getView().loginSuccess(null);
        }
        LogUtil.i("TAG", "云信登录成功");
    }

    private void getUserInfo() {
        MineApiService service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        addNet(service.getUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null) {
                            UserManager.getInstance().setUserInfo(response.getData());
                            appInitUser(1);
                        }
                    }
                }));
    }

    /**
     * 获取配置是否显示附近列表
     *
     * @param type 0:launch 进入 1：获取个人信息后进入
     */
    public void appInitUser(int type) {
        MineApiService service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        addNet(service.initNearUser().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<NearUserDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<NearUserDto> response) {
                        if (response.getData() != null && getView() != null) {
                            MMKVUtils.getInstance().encode(Constants.SHOW_NEAR_USER, response.getData().show_near_user);
                            MMKVUtils.getInstance().encode(Constants.SHOW_PARTY_ICON, response.getData().show_party_icon);
                            if (type == 1) {
                                ARouterUtils.toPathWithId(ARouterApi.MAIN_MAIN);
                                AppManager.getAppManager().finishActivity(SplashActivity.class);
                                AppManager.getAppManager().finishActivity(LoginActivity.class);
                                AppManager.getAppManager().finishActivity(GetCodeActivity.class);
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null) {
                            getView().hideLoadingDialog();
                        }

                    }
                }));
    }


    public void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = NimCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }


    /**
     * 未显示弹出隐私协议弹窗
     */
    public boolean showPop(Activity activity) {
        boolean isReturn = false;
        int isAgree = MMKVUtils.getInstance().decodeInt(Constants.IS_AGREE_AGREEMENT);
        if (isAgree == 0) {
            PrivacyPopWindow popWindow = new PrivacyPopWindow(BaseApplication.getInstance());
            popWindow.showPopupWindow();
            popWindow.setPrivacyListener(new PrivacyPopWindow.PrivacyListener() {
                @Override
                public void agree() {
                    if (getView() == null) return;
                    getView().agreementSuccess();
                }

                @Override
                public void cancel() {
                    if (getView() == null) return;
                    activity.finish();
                }
            });
            isReturn = true;
        } else {
            if (getView() == null) return false;
            getView().agreementSuccess();
        }
        return isReturn;
    }


    /**
     * 获取验证码
     *
     * @param pram
     */
    public void sendCode(LoginReq pram, boolean isShow) {
        PublicService service = RetrofitManager.getInstance().createUserApi(PublicService.class);
        addNet(service.sendCode(pram).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().getCodeSuccess(response.getData());
                    }
                }));
    }


}
