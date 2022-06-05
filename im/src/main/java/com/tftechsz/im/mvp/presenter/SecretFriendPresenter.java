package com.tftechsz.im.mvp.presenter;

import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.mvp.iview.ISecretFriendView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

import java.util.List;

public class SecretFriendPresenter extends BasePresenter<ISecretFriendView> {
    public ChatApiService service;

    public SecretFriendPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
    }

    /**
     * 获取聊天中用户信息
     */
    public void getIntimacyList(int page) {
        addNet(service.getIntimacyList(page).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<ContactInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ContactInfo>> response) {
                        if (getView() == null) return;
                        getView().getIntimacyListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


}
