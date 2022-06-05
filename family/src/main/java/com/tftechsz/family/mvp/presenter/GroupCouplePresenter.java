package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IGroupCoupleView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;


public class GroupCouplePresenter extends BasePresenter<IGroupCoupleView> {

    private final FamilyApiService service;


    public GroupCouplePresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 家族成员获取
     */
    public void getGroupCouple() {
        addNet(service.getGroupCouple().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberDto>> response) {
                        if (null == getView()) return;
                        getView().getGroupCoupleSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

}
