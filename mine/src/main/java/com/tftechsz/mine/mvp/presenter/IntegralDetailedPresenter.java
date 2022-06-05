package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.ExchangeRecord;
import com.tftechsz.mine.mvp.IView.IIntegralDetailView;

import java.util.List;

public class IntegralDetailedPresenter extends BasePresenter<IIntegralDetailView> {

    public MineApiService service;
    public MineApiService exchService;

    public IntegralDetailedPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
    }


    /**
     * 获取清单
     */
    public void getIntegralDetailed(int page, boolean isShow) {
        addNet(service.getIntegralDetailed(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }


    /**
     * 获取金币清单
     */
    public void getCoinDetailed(int page, boolean isShow) {
        addNet(service.getCoinDetailed(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }


    /**
     * 获取兑换记录
     */
    public void getExchangeRecord(int page, boolean isShow) {
        addNet(exchService.getExchangeRecord(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }

    /**
     * 获取聊天卡消耗记录
     */
    public void getSignRecord(int page, boolean isShow) {
        addNet(service.getRecordChat(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }


    public void getNoteValueDetailed(int page, boolean isShow) {
        addNet(service.getNoteValueDetailed(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }

    /**
     * 获取金币兑换记录
     */
    public void getNoteValueExchangeRecord(int page, boolean isShow) {
        addNet(exchService.getNoteValueExchangeRecord(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>(isShow ? getView() : null) {
                    @Override
                    public void onSuccess(BaseResponse<List<ExchangeRecord>> response) {
                        if (null == getView()) return;
                        getView().getIntegralDetailSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getIntegralDetailFail(msg);
                    }
                }));
    }
}
