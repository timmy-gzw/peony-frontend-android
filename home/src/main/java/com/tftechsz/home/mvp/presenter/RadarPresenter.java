package com.tftechsz.home.mvp.presenter;

import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.mvp.iview.IRadarView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class RadarPresenter extends BasePresenter<IRadarView> {

    public HomeApiService service;

    public RadarPresenter() {
        service = RetrofitManager.getInstance().createUserApi(HomeApiService.class);
    }

    /**
     * 匹配用户
     */
    public void videoMatch() {
        addNet(service.videoMatch().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        getView().matchSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        if (code == 1000)
                            getView().exitActivity();
                    }
                }));
    }

    /**
     * 退出音频匹配
     */
    public void quitVoiceMatch() {
        addNet(service.quitVoiceMatch().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 语音心跳
     */
    public void voiceBeat() {
        addNet(service.voiceBeat().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 视频心跳
     */
    public void videoBeat() {
        addNet(service.videoBeat().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 匹配用户
     */
    public void voiceMatch() {
        addNet(service.voiceMatch().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        getView().matchSuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        if (code == 1000)
                            getView().exitActivity();
                    }
                }));
    }

    /**
     * 退出视频匹配
     */
    public void quitVideoMatch() {
        addNet(service.quitVideoMatch().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

}
