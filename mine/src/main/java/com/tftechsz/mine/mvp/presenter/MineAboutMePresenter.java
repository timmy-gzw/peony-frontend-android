package com.tftechsz.mine.mvp.presenter;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.mvp.IView.IMineAboutMeView;

import java.util.List;

public class MineAboutMePresenter extends BasePresenter<IMineAboutMeView> {

    public MineApiService service;
    public MineApiService exchService;


    public MineAboutMePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
    }


    /**
     * 获取他人用户信息
     */
    public void getUserInfoById(String userId) {
        addNet(service.getUserInfoById(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
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
     * 获取他人用户收到礼物
     */
    public void getUserGift(int pageSize, String userId) {
        addNet(exchService.getUserGift(pageSize, userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GiftDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<GiftDto>> response) {
                        if (getView() == null) return;
                        getView().getGiftSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取自己收到礼物
     */
    public void getSelfGift(int pageSize) {
        addNet(exchService.getSelfGift(pageSize).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GiftDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<GiftDto>> response) {
                        if (getView() == null) return;
                        getView().getGiftSuccess(response.getData());
                    }
                }));
    }
}
