package com.tftechsz.mine.mvp.presenter;

import android.app.Activity;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.req.BindData;
import com.tftechsz.mine.entity.req.CompleteReq;
import com.tftechsz.common.entity.LoginReq;
import com.tftechsz.mine.mvp.IView.IBindPhoneView;
import com.tftechsz.mine.utils.UserManager;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class BindPhonePresenter extends BasePresenter<IBindPhoneView> {
    private final MineApiService service;

    public BindPhonePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    public void sendCode(LoginReq params) {
        addNet(RetrofitManager.getInstance().createUserApi(PublicService.class).sendCode(params).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().getCodeSuccess(response.getData());
                    }

                }));
    }

    public void bindData(BindData data) {
        addNet(service.bindData(data).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().bindPhoneSuccess(response.getData());
                    }

                }));
    }

    public void completeInfo(CompleteReq pram) {
        if (getView() != null)
            getView().showLoadingDialog();
        addNet(service.completeUserInfo(pram).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().completeInfoSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null)
                            getView().hideLoadingDialog();
                    }
                }));
    }

    public void getUserInfo(Activity activity) {
        addNet(service.getUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null && getView() != null) {
                            UserManager.getInstance().setUserInfo(response.getData());
                            ARouterUtils.toPathWithId(ARouterApi.MAIN_MAIN);
                            activity.finish();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null)
                            getView().hideLoadingDialog();
                    }
                }));
    }
}
