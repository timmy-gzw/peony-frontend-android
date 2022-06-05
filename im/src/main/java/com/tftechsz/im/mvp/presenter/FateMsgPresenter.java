package com.tftechsz.im.mvp.presenter;

import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.FateInfo;
import com.tftechsz.im.mvp.iview.FateMsgView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

public class FateMsgPresenter extends BasePresenter<FateMsgView> {



    private final ChatApiService service;



    public FateMsgPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
    }

    /**
     * 获取聊天中用户信息
     */
    public void getDetailFateMsg(long uid) {
        addNet(service.getDetailFateMsg("4",uid).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<FateInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<FateInfo>> response) {
                        if(getView() == null) return;
                        getView().getFateMsgList(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));


    }

}
