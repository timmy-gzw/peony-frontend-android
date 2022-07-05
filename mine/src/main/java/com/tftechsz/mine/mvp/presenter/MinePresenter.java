package com.tftechsz.mine.mvp.presenter;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.mvp.IView.IMineView;
import com.tftechsz.mine.utils.UserManager;

public class MinePresenter extends BasePresenter<IMineView> {

    public MineApiService service;
    public MineApiService imService;

    public MinePresenter() {
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        imService = RetrofitManager.getInstance().createIMApi(MineApiService.class);
    }


    public void getUserInfo() {
        addNet(service.getUserInfo().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null && getView() != null) {
                            UserManager.getInstance().setUserInfo(response.getData());
                            getView().getUserInfoSuccess(response.getData());
                        }
                    }
                }));
    }



    public void getLiveToken(String roomName){
        addNet(imService.createLiveRoom(roomName).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<LiveTokenDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<LiveTokenDto> response) {
                        if (response.getData() != null && getView() != null) {
                            MMKVUtils.getInstance().encode("room_token",response.getData().room_token);
                            MMKVUtils.getInstance().encode("room_name",response.getData().channel_name);
                            NERTCVideoCall.sharedInstance().setTokenService((uid, callback) -> callback.onSuccess(response.getData()));
                            ARouterUtils.toTeamCallActivity(response.getData().tid);
                        }
                    }
                }));

    }

    public void setInviteCode(String inviteCode){
        addNet(service.setInviteCode(inviteCode).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.getCode() == 0){
                            if(getView() == null) return;
                            getView().setInviteCodeSuccess();
                        }else{
                            getView().setInviteCodeFail(response.getMessage());
                        }
                    }
                }));

    }

}
