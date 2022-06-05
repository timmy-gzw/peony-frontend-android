package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyRankDto;
import com.tftechsz.family.mvp.IView.IFamilyRankView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class FamilyRankPresenter extends BasePresenter<IFamilyRankView> {

    public FamilyApiService service;

    public FamilyRankPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 排行
     */
    public void rankList(int page, String type) {
        addNet(service.rankList(page, type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyRankDto>>() {

                    @Override
                    public void onSuccess(BaseResponse<FamilyRankDto> response) {
                        if (null == getView()) return;
                        getView().getFamilyRankSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


}
