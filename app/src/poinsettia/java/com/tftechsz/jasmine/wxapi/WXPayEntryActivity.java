package com.tftechsz.jasmine.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.Utils;

import io.reactivex.disposables.CompositeDisposable;

import static com.tftechsz.common.base.BasePresenter.applySchedulers;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        ConfigInfo configInfo = service.getConfigInfo();
        String appId = CommonUtil.getWeChatAppId(configInfo);
        api = WXAPIFactory.createWXAPI(this, TextUtils.isEmpty(appId) ? Constants.WX_APP_ID : appId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Utils.logE("onReq: "+req.toString());
    }

    @Override
    public void onResp(BaseResp resp) {
        PayResp resp1 = (PayResp) resp;
        String extData = resp1.extData;
        Utils.logE("微信支付回调: extData=" + extData);

        runOnUiThread(() -> GlobalDialogManager.getInstance().show(getFragmentManager(), "正在加载中,请稍后..."));
        PublicService configApi = RetrofitManager.getInstance().createPaymentApi(PublicService.class);
        mCompositeDisposable.add(configApi.wechatResult(resp.errCode, extData).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null != response.getData()) {
                            Utils.toast("支付成功!");
                            MobclickAgent.onEvent(BaseApplication.getInstance(), "pay_success");
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                            if (Utils.isPayTypeInFamily()) {
                                ARouter.getInstance().navigation(MineService.class).trackEvent(Interfaces.POINT_SCENE_PAY_SUCCESS,
                                        Interfaces.POINT_EVENT_PAGE, Interfaces.POINT_INDEX_PAY_SUCCESS, JSON.toJSONString(new BuriedPointExtendDto()), null);
                            }
                            GlobalDialogManager.getInstance().dismiss();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        GlobalDialogManager.getInstance().dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));


       /* if (resp.errCode == 0) {
            ToastUtil.showToast(BaseApplication.getInstance(), "支付成功");
            MobclickAgent.onEvent(BaseApplication.getInstance(), "pay_success");
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
            if (Utils.isPayTypeInFamily()) {
                ARouter.getInstance().navigation(MineService.class).buriedPoint(Interfaces.POINT_SCENE_PAY_SUCCESS,
                        Interfaces.POINT_EVENT_PAGE, Interfaces.POINT_INDEX_PAY_SUCCESS, JSON.toJSONString(new BuriedPointExtendDto()), null);
            }
        } else {
            ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
        }*/
        Utils.putPayType(0);
        finish();
    }
}
