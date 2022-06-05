package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyBlackListDto;
import com.tftechsz.family.mvp.IView.IFamilyBlackListView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

/**
 * 包 名 : com.tftechsz.family.mvp.presenter
 * 描 述 : TODO
 */
public class FamilyBlackListPresenter extends BasePresenter<IFamilyBlackListView> {

    public FamilyApiService service;

    public FamilyBlackListPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }

    public void getBlackList(String family_id, int page) {
        addNet(service.getBlackList(family_id, page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyBlackListDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyBlackListDto>> response) {
                        if (null == getView()) return;
                        getView().getFamilyBlackListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        getView().getFamilyBlackListError();
                    }
                }));

    }

    public void blackUser(int familyId, int userId) {
        addNet(service.blackUser(familyId, userId, 0).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().blackUserSuccess(userId);
                    }
                }));
    }

}
