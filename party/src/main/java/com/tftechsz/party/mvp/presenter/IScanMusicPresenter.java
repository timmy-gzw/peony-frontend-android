package com.tftechsz.party.mvp.presenter;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.MusicCountBean;
import com.tftechsz.party.entity.dto.MusicActionDto;
import com.tftechsz.party.mvp.IView.IScanMusicView;

import java.util.List;

/**
 * 包 名 : com.tftechsz.party.mvp.presenter
 * 描 述 : TODO
 */
public class IScanMusicPresenter extends BasePresenter<IScanMusicView> {
    public PartyApiService service;

    public IScanMusicPresenter() {
        service = RetrofitManager.getInstance().createUserApi(PartyApiService.class);

    }

    public void getUploadMusicCount() {
        addNet(service.getUploadMusicCount()
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<MusicCountBean>>() {

                    @Override
                    public void onSuccess(BaseResponse<MusicCountBean> response) {
                        if (null == getView() || response.getData() == null) return;
                        getView().getCountSuccess(response.getData());
                    }

                }));
    }

    public void uploadMusic(List<AudioBean> uploadServerList) {
        addNet(service.musicSave(uploadServerList)
                .compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<MusicActionDto>>() {

                    @Override
                    public void onSuccess(BaseResponse<MusicActionDto> response) {
                        getView().uploadSuccess();
                    }

                }));
    }
}
