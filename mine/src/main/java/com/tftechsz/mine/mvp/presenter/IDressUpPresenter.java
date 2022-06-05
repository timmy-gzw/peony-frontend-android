package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.DressListDto;
import com.tftechsz.mine.mvp.IView.IDressUpView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.presenter
 * 描 述 : TODO
 */
public class IDressUpPresenter extends BasePresenter<IDressUpView> {
    public MineApiService configService;

    public IDressUpPresenter() {
        configService = RetrofitManager.getInstance().createConfigApi(MineApiService.class);
    }

    public void getCategoryList() {
        addNet(configService.getCategoryList().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<DressListDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<DressListDto>> response) {
                        if (getView() == null) return;
                        getView().getCategoryListSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().getCategoryListError();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getView() == null) return;
                        getView().getCategoryListError();
                    }
                }));
    }
}
