package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.mine.mvp.IView.IMineIntegralView;

public class MineIntegralPresenter extends BasePresenter<IMineIntegralView> {
    public MineApiService service;

    public MineIntegralPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 获取我的积分
     */
    public void getIntegral() {
        addNet(service.getIntegral().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<IntegralDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<IntegralDto> response) {
                        getView().getIntegralSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取我的音符
     */
    public void getNoteValue() {
        addNet(service.getNoteValue().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<IntegralDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<IntegralDto> response) {
                        getView().getIntegralSuccess(response.getData());
                    }
                }));
    }


}
