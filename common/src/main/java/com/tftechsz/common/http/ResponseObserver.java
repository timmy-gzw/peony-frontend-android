package com.tftechsz.common.http;


import android.content.Context;
import android.net.ParseException;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.MvpView;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ToastUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

/**
 * 统一的Code封装处理
 */

public abstract class ResponseObserver<T> extends ResourceSubscriber<T> {

    private MvpView mvpView;
    private long currentTime = 0;
    private UserProviderService serviceUser;

    public ResponseObserver(MvpView view) {
        mvpView = view;
    }

    public ResponseObserver() {
    }

    public abstract void onSuccess(T t);

    public void onFail(int code, String msg) {
        if (!TextUtils.isEmpty(msg))
            ToastUtil.showToast(BaseApplication.getInstance(), msg);
    }

    @Override
    public void onComplete() {
        if (mvpView != null && mvpView.isLoadingDialogShow()) {
            mvpView.hideLoadingDialog();
        }
    }


    @Override
    public void onError(Throwable e) {
        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance())) {
            ToastUtil.showToast(BaseApplication.getInstance(), "请检查您的网络连接是否正常!");
            this.dispose();
            onFail(10000, "");
            return;
        }
        if (mvpView != null) {
            mvpView.hideLoadingDialog();
        }
        int code = 0;
        if (e instanceof HttpException) {
            code = ((HttpException) e).code();
            switch (code) {
                case 401:
                    if (!ClickUtil.canOperate()) {
                        return;
                    }
                    long curClickTime = System.currentTimeMillis();
                    if ((curClickTime - currentTime) > 3000) {
                        currentTime = curClickTime;
                        AppManager.getAppManager().finishAllActivity();
                        SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
                        ARouterUtils.toLoginActivity(ARouterApi.MINE_LOGIN);
                        ToastUtil.showToast(BaseApplication.getInstance(), "登录过期，请重新登录！");
                        NIMClient.getService(AuthService.class).logout();
                    }
                    break;
                case 404:
                case 500:
                case 502:
                    if (AppUtils.isAppDebug()) {
//                        ToastUtil.showToast(BaseApplication.getInstance(), "服务器异常！" + code);
                    }
                    break;
                default:
                    break;
            }
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException || e instanceof MalformedJsonException) {
            code = 1001;
        } else if (e instanceof java.net.UnknownHostException) {
            code = 1002;  //地址未知解析
        } else if (e instanceof ConnectTimeoutException) {
            code = 1003;  //连接超时
        } else if (e instanceof java.net.SocketTimeoutException) {
            code = 1004;  //连接超时
        }else if (e instanceof ConnectException) {
            code = 1005;  //连接失败
        } else if (e instanceof javax.net.ssl.SSLException) {
            code = 1006;  //证书验证失败
        }
        String msg = "服务器异常";
        if (e != null) {
            msg = e.toString();
        }
        if (serviceUser == null)
            serviceUser = ARouter.getInstance().navigation(UserProviderService.class);
        if (serviceUser == null || serviceUser.getConfigInfo() == null) {
            requestLog("" + code, msg);
        } else {
            if (serviceUser.getConfigInfo() != null && serviceUser.getConfigInfo().sys != null && serviceUser.getConfigInfo().sys.is_sumbmit_error_log == 1) {
                requestLog("" + code, msg);
            }
        }
        onFail(0, "");
    }


    private void requestLog(String code, String msg) {
        String url = MMKVUtils.getInstance().decodeString(Constants.CURRENT_REQUEST_URL);
        if (!TextUtils.isEmpty(url) && url.contains("behavior/aliyun/log"))
            return;
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("接口请求异常", url, code, msg, null);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (null != mvpView) {
            if (!NetworkUtil.isNetworkAvailable((Context) mvpView)) {
                mvpView.toastTip("暂无网络，请连接网络后重试！");
                this.dispose();
                return;
            }
            mvpView.showLoadingDialog();
        }
    }

    @Override
    public void onNext(T t) {
        if (mvpView != null) {
            mvpView.hideLoadingDialog();
        }
        if (t instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) t;
            try {
                if (response.cmd_data != null) {
                    ChatMsg chatMsg = response.cmd_data;
                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //弹窗
                        final ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
                        if (alert.template != 2) {
                            CommonUtil.showCustomPop(alert);
                            onFail(0, "");
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.TIP_TYPE)) {  //吐司
                        final ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
                        if (!TextUtils.isEmpty(tips.des))
                            ToastUtil.showToast(BaseApplication.getInstance(), tips.des);
                        if (response.getCode() == 0) {
                            onSuccess(t);
                            onComplete();
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.TIP_ACCEPT_AND_RECHARGE)) {
                        if (response.getCode() == 0) {
                            onSuccess(t);
                            onComplete();
                        }
                    }
                    return;
                }
                if (response.getCode() != 0) {   //请求失败
                    onFail(response.getCode(), response.getMessage());
                    return;
                }
                onSuccess(t);
                onComplete();
            } catch (Exception e) {
                e.printStackTrace();
                onError(e);
            }

        } else {
            onFail(0, "");
        }
    }


}
