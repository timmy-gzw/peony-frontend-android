package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.NobleBean;
import com.tftechsz.mine.mvp.IView.IMyNobleView;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class MyNoblePresenter extends BasePresenter<IMyNobleView> {
    public MineApiService service;

    public MyNoblePresenter() {
        service = RetrofitManager.getInstance().createFamilyApi(MineApiService.class);
    }

    public void getNobilityList() {
        addNet(service.getNobilityList().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<NobleBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<NobleBean> response) {
                        if (response.getData() != null && getView() != null) {
                            getView().getDataSuccess(response.getData());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }
}
