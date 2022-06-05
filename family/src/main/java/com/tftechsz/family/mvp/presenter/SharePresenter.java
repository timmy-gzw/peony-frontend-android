package com.tftechsz.family.mvp.presenter;

import com.tftechsz.family.mvp.IView.IShareView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.UserShareDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

/**
 * 包 名 : com.tftechsz.family.mvp.presenter
 * 描 述 : TODO
 */
public class SharePresenter extends BasePresenter<IShareView> {
    public PublicService service;


    public SharePresenter() {
        service = RetrofitManager.getInstance().createUserApi(PublicService.class);
    }

    public void getUserShareList(String type, int page) {
        addNet(service.getUserShareList(type, page).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserShareDto>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<UserShareDto> response) {
                        if (null == getView()) return;
                        getView().getUserShareListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().getUserShareListError();
                    }
                }));
    }
}
