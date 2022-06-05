package com.tftechsz.mine.mvp.presenter;

import android.content.Context;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.OcrCheckDto;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.mine.mvp.IView.IRealNameView;

import java.io.File;

public class RealNamePresenter extends BasePresenter<IRealNameView> {

    public MineApiService service;


    public RealNamePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }

    /**
     * 上传信息
     */
    public void uploadRealNameInfoNew(String name, String phone, String font, String back) {
        addNet(service.uploadRealNameInfoNew(name, phone, font, back).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealCheckDto>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<RealCheckDto> response) {
                        if (null == getView()) return;
                        getView().uploadRealNameInfoNewSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().uploadRealNameInfoError();
                    }
                }));
    }

    public void uploadRealNameInfo(String name, String phone, String font, String back) {
        addNet(service.uploadRealNameInfo(name, phone, font, back).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null == getView()) return;
                        getView().uploadRealNameInfoSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().uploadRealNameInfoError();
                    }
                }));
    }


    public void ocrCheck(String path, String code) {
        addNet(service.ocrCheck(path, code).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<OcrCheckDto>>(getView()) {
                    @Override
                    public void onSuccess(BaseResponse<OcrCheckDto> response) {
                        if (null == getView()) return;
                        getView().ocrCheckSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() != null) {
                            getView().hideLoadingDialog();
                        }
                    }
                }));
    }

    /**
     * 上传身份证正反面照片
     */
    public void uploadRealNameAvatar(String path, int code) {
        File file = new File(path);
        UploadHelper.getInstance((Context) getView()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_REAL_NAME, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                if (null == getView()) return;
                getView().uploadCardSuccess(url, code);

            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });

    }

}
