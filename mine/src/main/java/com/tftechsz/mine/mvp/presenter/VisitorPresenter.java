package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.VisitorDto;
import com.tftechsz.mine.mvp.IView.IVisitorView;

public class VisitorPresenter extends BasePresenter<IVisitorView> {

    public MineApiService service;

    public VisitorPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 获取访客记录
     */
    public void getVisitor(int page) {
        addNet(service.getVisitor(page).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<VisitorDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<VisitorDto> response) {
                        if (null != getView() && response.getData() != null){
                            getView().getVisitorSuccess(response.getData());
                        }

                    }
                }));
    }


}
