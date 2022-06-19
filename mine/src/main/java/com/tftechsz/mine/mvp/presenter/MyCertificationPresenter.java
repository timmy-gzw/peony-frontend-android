package com.tftechsz.mine.mvp.presenter;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IMyCertificationView;

public class MyCertificationPresenter extends BasePresenter<IMyCertificationView> {

    public MineApiService service;

    public MyCertificationPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoDetail() {
        addNet(service.getUserInfoDetail().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取真人认证信息
     */
    public void getRealInfo() {
        addNet(service.getRealInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                        if (getView() == null) return;
                        getView().getRealInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取实名认证信息
     */
    public void getSelfInfo() {
        addNet(service.getSelfInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                        if (getView() == null) return;
                        getView().getSelfInfoSuccess(response.getData());
                    }
                }));
    }
}
