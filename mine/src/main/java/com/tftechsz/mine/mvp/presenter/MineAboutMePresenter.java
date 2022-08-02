package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.mvp.IView.IMineAboutMeView;

import java.util.ArrayList;

public class MineAboutMePresenter extends BasePresenter<IMineAboutMeView> {

    public MineApiService service;
    public MineApiService exchService;

    public MineAboutMePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
    }

    /**
     * 获取所有礼物
     */
    public void getGiftList(String userId) {
        addNet(exchService.getGiftList(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ArrayList<GiftDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<ArrayList<GiftDto>> response) {
                        if (getView() == null) return;
                        getView().getGiftSuccess(response.getData());
                    }
                }));
    }
}
