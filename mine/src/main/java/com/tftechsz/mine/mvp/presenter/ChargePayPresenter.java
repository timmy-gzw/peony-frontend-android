package com.tftechsz.mine.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.SXYWxPayResultInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IChargePayView;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChargePayPresenter extends BasePresenter<IChargePayView> {

    public MineApiService service;
    private IWXAPI mApi;

    public ChargePayPresenter() {
        service = RetrofitManager.getInstance().createPaymentApi(MineApiService.class);
    }


    /**
     * 支付宝
     */
    public void getOrderNum(Activity context, int typeId, String from_type) {
        addNet(service.alipay(typeId, from_type).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        wakeUpAliPay(context, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 支付宝
     */
    public void wakeUpAliPay(Activity context, final String orderInfo) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            PayTask alipay = new PayTask(context);
            String result = alipay.pay(orderInfo, true);
            emitter.onNext(result);
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                    AlipayResultInfo payResult = new AlipayResultInfo(s);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        MobclickAgent.onEvent(BaseApplication.getInstance(), "pay_success");
                        getView().paySuccess();
                    } else {
                        if (getView() != null) {
                            getView().payFail();
                        }

                    }
                });
        mCompositeDisposable.add(disposable);
    }


    /**
     * 微信
     */
    public void getWxOrderNum(Activity context, int typeId, String form_type) {
        if (!AppUtils.isWeChatAppInstalled(context)) {
            ToastUtil.showToast(context, "手机未安装微信");
            return;
        }
        addNet(service.wechatPay(typeId, form_type).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<WxPayResultInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<WxPayResultInfo> response) {
                        wxPay(context, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    private void wxPay(Context context, WxPayResultInfo wx) {
        if (wx == null) return;
        new Thread(() -> {
            UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
            ConfigInfo configInfo = service.getConfigInfo();
            String appId = CommonUtil.getWeChatAppId(configInfo);
            mApi = WXAPIFactory.createWXAPI(context, TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
            mApi.registerApp(TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
            mApi.sendReq(CommonUtil.performWxReq(wx));
        }).start();
    }

    public void getSXYAliOrderNum(Activity context, int typeId, String form_type) {
        addNet(service.SXYalipay(typeId, form_type).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        CommonUtil.startIntentToAliPay(context, response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    public void getSXYWeChatPay(Activity context, int typeId, String form_type) {
        addNet(service.SXYwechatPay(typeId, form_type).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<SXYWxPayResultInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<SXYWxPayResultInfo> response) {
                        mApi = WXAPIFactory.createWXAPI(context, response.getData().app_id);
                        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                        req.userName = response.getData().mini_program_original_id; // 填小程序原始id
                        req.path = response.getData().url;
                        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
                        mApi.sendReq(req);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }
}
