package com.tftechsz.mine.mvp.presenter;

import android.app.Activity;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.req.BindData;
import com.tftechsz.mine.entity.req.GetBindData;
import com.tftechsz.mine.mvp.IView.IAccountBindingView;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class AccountBindingPresenter extends BasePresenter<IAccountBindingView> {
    private final MineApiService service;
    private IWXAPI api;

    public AccountBindingPresenter() {
        regToWx();
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), Constants.WX_APP_ID);
        // 将应用的appId注册到微信
        api.registerApp(Constants.WX_APP_ID);
    }

    public void loginWx(Activity context) {
        if (!AppUtils.isWeChatAppInstalled(context)) {
            ToastUtil.showToast(context, "手机未安装微信");
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";
        api.sendReq(req);
    }

    public void getBindData() {
        addNet(service.getBindData().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<GetBindData>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<GetBindData> response) {
                        if (null == getView()) return;
                        getView().getBindDataSuccess(response.getData());
                    }
                }));
    }

    public void unBindThird(String type) {
        addNet(service.unBindThird(type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (null == getView()) return;
                        getView().unBindThirdSuccess(response);
                    }
                }));
    }

    public void bindData(boolean enabled, BindData pram) {
        if (enabled) {
            addNet(service.bindData(pram).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                        @Override
                        public void onSuccess(BaseResponse<String> response) {
                            if (null == getView()) return;
                            getView().bindPhoneSuccess(response.getData());
                        }

                    }));
        } else {
            addNet(service.bindWechatData(pram.data.code).compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                        @Override
                        public void onSuccess(BaseResponse<String> response) {
                            if (null == getView()) return;
                            getView().bindPhoneSuccess(response.getData());
                        }

                    }));
        }
    }
}
