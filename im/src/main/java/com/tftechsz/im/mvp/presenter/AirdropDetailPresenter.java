package com.tftechsz.im.mvp.presenter;

import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.AirdropDetailDto;
import com.tftechsz.im.mvp.iview.IAirdropDetailView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class AirdropDetailPresenter extends BasePresenter<IAirdropDetailView> {

    ChatApiService service;

    public AirdropDetailPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
    }


    public void getAirdropDetail(int airdropId) {
        addNet(service
                .getAirdropDetail(airdropId)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<AirdropDetailDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<AirdropDetailDto> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getAirdropDetailSuccess(response.getData());
                    }

                }));
    }

}
