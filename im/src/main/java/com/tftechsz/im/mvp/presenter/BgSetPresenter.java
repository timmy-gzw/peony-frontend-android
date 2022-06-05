package com.tftechsz.im.mvp.presenter;

import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.BgSetBean;
import com.tftechsz.im.mvp.iview.IBgSetView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

/**
 * 包 名 : com.tftechsz.im.mvp.presenter
 * 描 述 : TODO
 */
public class BgSetPresenter extends BasePresenter<IBgSetView> {
    ChatApiService service;

    public BgSetPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
    }

    public void getBgConfig(String uid) {
        addNet(service.getBgConfig(uid)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<BgSetBean>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<BgSetBean>> response) {
                        getView().getBgConfigSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }

    public void bgSet(String uid, int id, String bg, int used) {
        addNet(service.setChatBg(uid, id, used)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        getView().setBgSuccess(bg);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }
}
