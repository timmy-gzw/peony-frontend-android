package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.GradeLevelDto;
import com.tftechsz.mine.mvp.IView.IGradeIntroduceView;

public class GradeIntroducePresenter extends BasePresenter<IGradeIntroduceView> {

    public MineApiService service;

    public GradeIntroducePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    /**
     * 更新用户信息
     */
    public void getGradeLevel(String type) {
        addNet(service.getAccostList(type).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<GradeLevelDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<GradeLevelDto> response) {
                        getView().getGradeIntroduceSuccess(response.getData());
                    }
                }));
    }


}
