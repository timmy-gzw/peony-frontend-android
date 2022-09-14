package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.LabelDto;
import com.tftechsz.mine.mvp.IView.ILabelView;

import java.util.List;

public class LabelPresenter extends BasePresenter<ILabelView> {

    public MineApiService service;

    public LabelPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    public void getTag() {
        addNet(service.getTag().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<LabelDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LabelDto>> response) {
                        if (getView() == null) return;
                        getView().getTagSuccess(response.getData());
                    }

                }));
    }


    public void setTag(String tagId) {
        addNet(service.setTag(tagId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().setTagSuccess(response.getData());
                    }

                }));
    }

}
