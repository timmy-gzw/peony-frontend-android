package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.UploadHelper;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.common.entity.RealCheckDto;
import com.tftechsz.mine.mvp.IView.IRealAuthView;

import java.io.File;

public class RealAuthPresenter extends BasePresenter<IRealAuthView> {

    public MineApiService service;


    public RealAuthPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 上传照片
     *
     * @param url
     */
    private void updateAuthAvatar(String icon, String url) {
        addNet(service.uploadRealAvatar(icon, url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (null != getView()) {
                            getView().uploadRealAvatarSuccess(response.getData());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null != getView())
                            getView().hideLoadingDialog();
                    }
                }));
    }


    public void uploadAuthAvatar(String icon, String path) {
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_REAL_AUTH, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                updateAuthAvatar(icon, url);
            }

            @Override
            public void onError() {
                if (null != getView())
                    getView().hideLoadingDialog();
            }
        });
    }

    public void uploadAvatar(String path, String imageSelf, boolean isUploadAuth) {
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_AVATAR, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                //updateAvatar(url, imageSelf, isUploadAuth);
                if (isUploadAuth) {
                    uploadAuthAvatar(url, imageSelf);
                } else {
                    updateAuthAvatar(url, imageSelf);
                }
            }

            @Override
            public void onError() {
                if (null != getView())
                    getView().hideLoadingDialog();
            }
        });
    }

    public void facedetectCheck() {
        getView().showLoadingDialog();
        addNet(service.facedetectCheck().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealCheckDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealCheckDto> response) {
                        if (null != getView()) {
                            getView().hideLoadingDialog();
                            if (response.getData() != null)
                                getView().facedetectCheckSuccess(response.getData());
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null != getView()) {
                            getView().hideLoadingDialog();
                        }
                    }
                }));
    }

    public void recheck(String token) {
        addNet(service.recheck(token).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealCheckDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealCheckDto> response) {
                        if (null != getView()) {
                            getView().recheckSuccess(response.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FINISH_REAL));
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FINISH_REAL));
                    }
                }));
    }

    public void partyRecheck(String token) {
        addNet(service.partyRecheck(token).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<RealCheckDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<RealCheckDto> response) {
                        if (null != getView()) {
                            getView().recheckSuccess(response.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FINISH_REAL));
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FINISH_REAL));
                    }
                }));
    }

    public void uploadAvatarNew(String path) {
        if (getView() != null)
            getView().showLoadingDialog();
        File file = new File(path);
        UploadHelper.getInstance(BaseApplication.getInstance()).doUpload(UploadHelper.OSS_USER_TYPE, UploadHelper.PATH_USER_AVATAR, UploadHelper.TYPE_IMAGE, file, new UploadHelper.OnUploadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onLoading(long cur, long total) {

            }

            @Override
            public void onSuccess(String url) {
                updateAvatarNew(url);
            }

            @Override
            public void onError() {
                if (null == getView()) return;
                getView().hideLoadingDialog();
            }
        });
    }

    private void updateAvatarNew(String url) {
        addNet(service.uploadAvatar(url).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (null == getView()) return;
                        getView().uploadAvatarSuccess(response.getData());
                        getView().hideLoadingDialog();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null == getView()) return;
                        //getView().uploadAvatarFail(msg);
                        getView().hideLoadingDialog();
                    }
                }));
    }
}
