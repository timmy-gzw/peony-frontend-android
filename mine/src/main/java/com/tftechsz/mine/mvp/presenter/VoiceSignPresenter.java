package com.tftechsz.mine.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.UpdateAccostSettingBean;
import com.tftechsz.mine.mvp.IView.IVoiceSignView;

public class VoiceSignPresenter extends BasePresenter<IVoiceSignView> {


    private final MineApiService service;

    public VoiceSignPresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
    }


    /**
     * 上传视频
     */
    public void uploadVoice(String url, int time) {
        addNet(service.uploadVoice(url, time).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if(response.getData()){
                            getView().uploadSuccess(response.getMessage());
                        }
                        GlobalDialogManager.getInstance().dismiss();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));
    }


    /**
     * 搭讪语音添加
     */
    public void addAccostSetting(AccostSettingListBean bean) {
        addNet(service.addAccostSetting(new UpdateAccostSettingBean("voice", bean)).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (getView() == null) return;
                        getView().addAccostSettingSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().addAccostSettingError();
                    }

                }));
    }

}
