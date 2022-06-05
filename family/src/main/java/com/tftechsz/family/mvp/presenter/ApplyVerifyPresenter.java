package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.mvp.IView.IApplyVerifyView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class ApplyVerifyPresenter extends BasePresenter<IApplyVerifyView> {

    public FamilyApiService service;

    public ApplyVerifyPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 忽略申请和离开操作
     */
    public void ignoreApply(int familyId, String type) {
        addNet(service.ignoreApply(familyId, type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().ignoreApplySuccess(response.getData());
                    }

                }));
    }


}
