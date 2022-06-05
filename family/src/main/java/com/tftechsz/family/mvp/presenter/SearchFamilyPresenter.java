package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.mvp.IView.ISearchFamilyView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class SearchFamilyPresenter extends BasePresenter<ISearchFamilyView> {

    public FamilyApiService service;


    public SearchFamilyPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }

    /**
     * 搜索家族
     */
    public void searchFamily(String familyId) {
        addNet(service.searchFamily(familyId,"").compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null == getView()) return;
                        getView().getSearchSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


}
