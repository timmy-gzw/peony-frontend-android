package com.tftechsz.common.nim.model;

import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.tftechsz.common.nertcvoiceroom.model.Anchor;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.AudioPlay;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoomDef;
import com.tftechsz.common.nertcvoiceroom.model.PushTypeSwitcher;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomMessage;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class NERTCVideoCall {
    protected NERTCVideoCall() {
    }

    public static NERTCVideoCall sharedInstance() {
        return NERTCVideoCallImpl.sharedInstance();
    }

    public static void destroySharedInstance() {
        NERTCVideoCallImpl.destroySharedInstance();
    }


    /**
     * 增加回调接口
     *
     * @param delegate 上层可以通过回调监听事件
     */
    public abstract void addDelegate(NERTCCallingDelegate delegate);


    /**
     * 移除回调接口
     *
     * @param delegate 需要移除的监听器
     */
    public abstract void removeDelegate(NERTCCallingDelegate delegate);

    /**
     * 设置远端的视频接受播放器
     *
     * @param videoRender
     * @param uid
     */
    public abstract void setupRemoteView(NERtcVideoView videoRender, long uid);

    /**
     * 设置本端的视频接受播放器
     *
     * @param videoRender
     */
    public abstract void setupLocalView(NERtcVideoView videoRender);

    /**
     * C2C邀请通话，被邀请方会收到 {@link NERTCCallingDelegate#onInvitedByUser } 的回调
     *
     * @param userId 被邀请方
     * @param type   1-语音通话，2-视频通话
     */
    public abstract void call(String userId, ChannelType type);

    /**
     * 多人邀请通话，被邀请方会收到 {@link NERTCCallingDelegate } 的回调
     *
     * @param callUserIds         被邀请方
     * @param selfUserId          自己的用户Id
     * @param groupId             群Id
     * @param type                1-语音通话，2-视频通话
     * @param joinChannelCallBack channel 回调
     */
    public abstract void groupCall(ArrayList<String> callUserIds, String groupId, String selfUserId, ChannelType type, String extraInfo, JoinChannelCallBack joinChannelCallBack);

    /**
     * 当您作为被邀请方收到 {@link NERTCCallingDelegate#onInvitedByUser } 的回调时，可以调用该函数接听来电
     *
     * @param invitedParam        邀请信息
     * @param selfAccId           自己的accid
     * @param joinChannelCallBack 加入channel的回调
     */
    public abstract void accept(InviteParamBuilder invitedParam, String selfAccId, JoinChannelCallBack joinChannelCallBack);

    public abstract void setStatsObserver();

    public abstract void accept(String fromId);

    public abstract void setCallState(int callStatus);

    public abstract int getCallState();

    public abstract boolean isReceive();

    /**
     * 设置获取token的服务，安全模式必须设置
     *
     * @param tokenService
     */
    public abstract void setTokenService(TokenService tokenService);

    /**
     * 当您作为被邀请方收到 {@link NERTCCallingDelegate#onInvitedByUser } 的回调时，可以调用该函数拒绝来电
     *
     * @param inviteParam 邀请信息
     */
    public abstract void reject(InviteParamBuilder inviteParam, RequestCallback<Void> callback);

    /**
     * 当您处于通话中，可以调用该函数结束通话
     */
    public abstract void hangup();

    /**
     * 当您处于呼叫中，可以调用该函数取消呼叫
     */
    public abstract void cancel(RequestCallback<Void> callback);

    /**
     * 您可以调用该函数开启摄像头，并渲染在指定的TXCloudVideoView中
     * 处于通话中的用户会收到 {@link NERTCCallingDelegate#onCameraAvailable(long, boolean)} 回调
     *
     * @param enable 是否开启摄像头
     */
    public abstract void enableCamera(boolean enable);

    /**
     * 您可以调用该函数切换前后摄像头
     */
    public abstract void switchCamera();

    /**
     * 是否静音mic
     *
     * @param isMute true:麦克风关闭 false:麦克风打开
     */
    public abstract void setMicMute(boolean isMute);

    /**
     * 是否开启扬声器
     *
     * @param isOpen
     */
    public abstract void setSpeakerphoneOn(boolean isOpen);

    /**
     * 指定某个用户静音
     *
     * @param mute   是否静音
     * @param userId 被静音的用户
     */
    public abstract void setAudioMute(boolean mute, long userId);

    /**
     * 通话过程中离开房间，并不关闭房间
     * (多人通话时如果未有其他用户加入底层也会调用取消的逻辑)
     */
    public abstract void leave(RequestCallback<Void> callback);

    /**
     * 设置超时，最长2分钟
     *
     * @param timeOut 超时市场，最长两分钟，单位ms
     */
    public abstract void setTimeOut(int timeOut);


    /**
     * 每次进入时初始化
     */
    public abstract void initNERtc(int mode,NERtcVoiceRoomDef.RoomCallback callback);


    /**
     *
     */
    public abstract void setMode(int mode,NERtcVoiceRoomDef.RoomCallback callback);

    /**
     * 每次退出时释放
     */
    public abstract void releaseNERtc();


    /**
     * 设置音质（需要在进入房间前调用）
     */
    public abstract void setAudioQuality(int quality);

    /**
     * 初始化房间
     *
     * @param voiceRoomInfo 房间信息}
     * @param user          资料信息}
     */
    public abstract void initRoom(VoiceRoomInfo voiceRoomInfo,VoiceRoomSeat microphoneHost,
                                  VoiceRoomUser user,int partyLiveType);


    public abstract void setSeatList(List<VoiceRoomSeat> seatList);

    /**
     * 进入房间
     *
     * @param anchorMode 主播模式
     */
    public abstract void enterRoom(boolean anchorMode, VoiceRoomSeat voiceRoomSeat);


    public abstract void onDestroy();

    /**
     * 离开房间
     */
    public abstract void leaveRoom(boolean sendTip);

    /**
     * 开启本地语音
     */
    public abstract void startLocalAudio();

    /**
     * 停止本地语音
     */
    public abstract void stopLocalAudio();

    /**
     * 本地静音
     *
     * @param mute 是否静音
     * @return 是否静音
     */
    public abstract boolean muteLocalAudio(boolean mute);

    /**
     * 本地是否静音
     *
     * @return 是否静音
     */
    public abstract boolean isLocalAudioMute();

    /**
     * 设置开启扬声器
     *
     * @param useSpeaker true:扬声器 false:听筒
     */
    public abstract void setSpeaker(boolean useSpeaker);

    /**
     * 设置采集音量
     *
     * @param volume 采集音量 0-100
     */
    public abstract void setAudioCaptureVolume(int volume);

    /**
     * 获取采集音量
     *
     * @return 采集音量
     */
    public abstract int getAudioCaptureVolume();

    /**
     * 房间静音
     *
     * @param mute 是否静音
     * @return 是否静音
     */
    public abstract boolean muteRoomAudio(boolean mute);

    /**
     * 设置语音房当前模式
     * @return 是否静音
     */
    public abstract void setClientRole(int role);

    /**
     * 房间是否静音
     *
     * @return 是否静音
     */
    public abstract boolean isRoomAudioMute();

    /**
     * 耳返是否可用
     *
     * @return 是否耳返可用
     */
    public abstract boolean isEarBackEnable();

    /**
     * 开启耳返
     *
     * @param enable 是否开启耳返
     */
    public abstract void enableEarback(boolean enable);

    /**
     * 发送房间文本消息
     *
     * @param text 文字内容
     */
    public abstract void sendTextMessage(String text);

    /**
     * 获取播放接口
     *
     * @return 播放接口
     */
    public abstract AudioPlay getAudioPlay();

    /**
     * 获取推流方式切换开关
     */
    public abstract PushTypeSwitcher getPushTypeSwitcher();

    /**
     * 获取观众接口
     *
     * @return 观众接口
     */
    public abstract Audience getAudience();

    /**
     * 获取主播接口
     *
     * @return 主播接口
     */
    public abstract Anchor getAnchor();

    public static void setAccountMapper(NERtcVoiceRoomDef.AccountMapper mapper) {
        NERTCVideoCallImpl.setAccountMapper(mapper);
    }

    public static void setMessageTextBuilder(VoiceRoomMessage.MessageTextBuilder messageTextBuilder) {
        NERTCVideoCallImpl.setMessageTextBuilder(messageTextBuilder);
    }

}
