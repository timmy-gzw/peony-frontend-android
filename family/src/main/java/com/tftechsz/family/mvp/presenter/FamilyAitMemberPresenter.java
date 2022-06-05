package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IFamilyAitMemberView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;

import java.util.List;

public class FamilyAitMemberPresenter extends BasePresenter<IFamilyAitMemberView> {

    public FamilyApiService service;

    public FamilyAitMemberPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 家族成员获取
     */
    public void getFamilyMember(int roleId) {
        addNet(service.getFamilyMember().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberDto>> response) {
                        if (null == getView()) return;
                        getView().getMemberSuccess(response.getData(),roleId);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }



    public void getFamilyId(String tid) {
        mCompositeDisposable.add(service.getFamilyId(tid)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            getFamilyMember(response.getData().role_id);
                        }
                    }
                }));
    }


}
