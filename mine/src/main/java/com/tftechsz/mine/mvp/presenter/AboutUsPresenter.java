package com.tftechsz.mine.mvp.presenter;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IAboutUsView;

public class AboutUsPresenter extends BasePresenter<IAboutUsView> {

    public MineApiService service;
    private IWXAPI mApi;

    public AboutUsPresenter() {
        service = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
    }


    /**
     *
     */
    public void getUpdateCheck() {
        addNet(service.getUpdateCheck().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ConfigInfo.MineInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<ConfigInfo.MineInfo> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getUpdateCheckSuccess(response.getData());
                    }
                }));
    }

}
