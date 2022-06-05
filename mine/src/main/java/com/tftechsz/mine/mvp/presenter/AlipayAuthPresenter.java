package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.UserCertifyBean;
import com.tftechsz.mine.mvp.IView.IAlipayAuthView;

public class AlipayAuthPresenter extends BasePresenter<IAlipayAuthView> {

    public MineApiService service;


    public AlipayAuthPresenter() {
        service = RetrofitManager.getInstance().createPaymentApi(MineApiService.class);
    }

    /**
     * 实名认证
     */
    public void userCertify(String name, String card, String account) {
        addNet(service.userCertify(name, card, account).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserCertifyBean>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<UserCertifyBean> response) {
                        if (response.getData() != null && getView() != null) {
                            getView().userCertifySuccess(response.getData().url, response.getData().certify_id);
                        }
                    }
                }));
    }

    /**
     * 实名认证是否成功
     */
    public void checkCertify(String certify_id) {
        addNet(service.checkCertify(certify_id).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null && getView() != null) {
                            getView().checkCertifySuccess(response.getData());
                        }
                    }
                }));
    }


}
