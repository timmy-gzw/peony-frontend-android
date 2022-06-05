package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.mine.mvp.IView.IExchangeDetailView;

public class ExchangeDetailPresenter extends BasePresenter<IExchangeDetailView> {

    public MineApiService service;
    public MineApiService exchService;

    public ExchangeDetailPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
    }


    /**
     * 获取真人认证信息
     */
    public void getRealInfo() {
        addNet(service.getRealInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                        if (null != getView())
                            getView().getSelfInfoSuccess(response.getData());
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
                        if (null != getView())
                            getView().getRealInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 积分兑换
     */
    public void exchange(int typeId) {
        addNet(exchService.exchange(typeId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null != getView())
                            getView().exchange(response.getData());
                    }
                }));
    }


}
