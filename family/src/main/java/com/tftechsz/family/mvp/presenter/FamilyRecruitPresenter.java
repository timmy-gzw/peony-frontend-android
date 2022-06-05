package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.family.mvp.IView.IFamilyRecruitView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

/**
 * 包 名 : com.tftechsz.family.mvp.presenter
 * 描 述 : TODO
 */
public class FamilyRecruitPresenter extends BasePresenter<IFamilyRecruitView> {

    public FamilyApiService service;

    public FamilyRecruitPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }

    public void getRecruitList(int page) {
        addNet(service.getRecruitList(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyRecruitDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyRecruitDto>> response) {
                        if (null == getView()) return;
                        getView().getRecruitListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().getRecruitListFail();
                    }
                }));
    }
}
