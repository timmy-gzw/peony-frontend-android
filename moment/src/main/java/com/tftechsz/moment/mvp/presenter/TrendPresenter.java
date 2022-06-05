package com.tftechsz.moment.mvp.presenter;


import android.widget.EditText;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.entity.req.PublishReq;
import com.tftechsz.moment.mvp.IView.ITrendView;

import java.util.List;


public class TrendPresenter extends BasePresenter<ITrendView> {
    public TrendApiService service;

    public TrendPresenter() {
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
    }

    /**
     * 发布
     */
    public void publish(PublishReq mPublishReq, EditText editText, List<String> uploadServerUrlList) {
        //GlobalDialogManager.getInstance().dismiss();
        addNet(service.publish(mPublishReq).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().sendTrendSuccess(response.getData());
                        GlobalDialogManager.getInstance().dismiss();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));

    }

    public void beforeCheck() {
        addNet(service.beforeCheck().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().beforeCheckSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));
    }

}
