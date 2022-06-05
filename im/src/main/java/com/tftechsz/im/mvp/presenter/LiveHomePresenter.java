package com.tftechsz.im.mvp.presenter;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.LiveHomeDto;
import com.tftechsz.im.mvp.iview.ILiveHomeView;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;

public class LiveHomePresenter extends BasePresenter<ILiveHomeView> {

    ChatApiService service;

    public LiveHomePresenter() {
        service = RetrofitManager.getInstance().createIMApi(ChatApiService.class);
    }

    /**
     * 获取直播列表
     */
    public void getLiveHome() {
        addNet(service.getLiveHome().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<LiveHomeDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<LiveHomeDto> response) {
                        if(getView() == null) return;
                        getView().getLiveHomeSuccess(response.getData().list);
                    }
                }));


    }

    /**
     * 获取获取token
     */
    public void getLiveHomeToken(int position,String roomName) {
        addNet(service.getLiveHomeToken(roomName).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<LiveTokenDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<LiveTokenDto> response) {
                        if(getView() == null) return;
                        getView().getLiveHomeTokenSuccess(position,roomName,response.getData().token);
                    }
                }));


    }


    /**
     * 发送自定义通知
     */
    public void sendNotification(String sessionId,String groupId) {
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(sessionId);
        notification.setSessionType(SessionTypeEnum.P2P);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        JSONObject json = new JSONObject();
        json.put("groupId",groupId);
        notification.setContent(json.toString());
        // 若接收者不在线，则当其再次上线时，将收到该自定义系统通知。若设置为 true，则再次上线时，将收不到该通知。
        notification.setSendToOnlineUserOnly(true);
        // 配置 CustomNotificationConfig
        CustomNotificationConfig config = new CustomNotificationConfig();
        // 需要推送
        config.enablePush = false;
        config.enableUnreadCount = false;
        notification.setConfig(config);
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

}
