package com.tftechsz.moment.mvp.presenter;


import android.widget.EditText;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.moment.api.TrendApiService;
import com.tftechsz.moment.mvp.IView.IReportView;


public class ReportPresenter extends BasePresenter<IReportView> {

    public TrendApiService service;
    public TrendApiService userService;

    public ReportPresenter() {
        service = RetrofitManager.getInstance().createBlogApi(TrendApiService.class);
        userService = RetrofitManager.getInstance().createUserApi(TrendApiService.class);
    }


    /**
     * 举报
     * {
     * "type_id": 1, //举报原因id
     * "blog_id": 1,  //动态id
     * "message": "",  //举报内容
     * "resource": [],  //举报图片
     * }
     */
    public void report(EditText mEditText, int type_id, int blog_id, String message, String resource) {
        addNet(service.report(type_id, blog_id, message, resource).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        if (response.getData()) {
                            mEditText.setText("");
                            getView().toastTip("举报提交成功！");
                            getView().commitSuccess("");
                        }
                        getView().hideLoadingDialog();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().toastTip("举报提交失败！" + msg);
                        getView().hideLoadingDialog();
                    }
                }));
    }


    public void feedback(EditText mEditText, String message, String resource) {
        addNet(userService.feedback(message, resource).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        if (response.getData()) {
                            mEditText.setText("");
                            getView().toastTip("反馈提交成功！");
                            getView().commitSuccess("");
                        }
                        getView().hideLoadingDialog();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().toastTip("反馈提交失败！" + msg);
                        getView().hideLoadingDialog();
                    }
                }));
    }


    public void reportUser(EditText mEditText, int type_id, int userId, String message, String resource) {
        addNet(userService.reportUser(type_id, userId, message, resource).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        if (response.getData()) {
                            mEditText.setText("");
                            getView().toastTip("举报提交成功！");
                            getView().commitSuccess("");
                        }
                        getView().hideLoadingDialog();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().toastTip("举报失败" + msg);
                        getView().hideLoadingDialog();
                    }
                }));
    }


}
