package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.NoteValueConfirmDto;
import com.tftechsz.mine.mvp.IView.INoteValueShopView;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class INoteValueShopPresenter extends BasePresenter<INoteValueShopView> {
    public MineApiService service;
    public MineApiService exchService;

    public INoteValueShopPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
    }

    /**
     * 获取我的音符
     */
    public void getNoteValue() {
        addNet(service.getNoteValue().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<IntegralDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<IntegralDto> response) {
                        if (getView() == null || response == null || response.getData() == null) return;
                        getView().getNoteValueSuccess(response.getData());
                    }
                }));
    }

    public void exchangeNoteValue(int coin) {
        addNet(exchService.exchangeNoteValue(coin).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null || response == null) return;
                        getView().exchangeNoteValueSuccess();
                    }
                }));
    }

    public void convertConfirm(int ceil) {
        addNet(exchService.convertConfirm("musical:note:coin", ceil).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<NoteValueConfirmDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<NoteValueConfirmDto> response) {
                        if (getView() == null || response == null) return;
                        getView().convertConfirmSuccess(response.getData());
                    }
                }));
    }
}
