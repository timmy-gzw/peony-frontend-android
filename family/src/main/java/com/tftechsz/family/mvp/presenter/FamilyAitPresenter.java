package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IFamilyAitView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.RxUtil;

import java.util.List;

public class FamilyAitPresenter extends BasePresenter<IFamilyAitView> {

    public FamilyApiService service;

    public FamilyAitPresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(FamilyApiService.class);
    }


    /**
     * 家族成员获取
     */
    public void getFamilyMember(int page) {
        addNet(service.getAitList(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyMemberDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FamilyMemberDto>> response) {
                        if (null == getView()) return;
                        getView().getMemberSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    public void aitRead(int userId, int id) {
        mCompositeDisposable.add(service.aitRead(id)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        MMKVUtils.getInstance().removeKey(userId + Constants.FAMILY_AIT);
                    }
                }));
    }


}
