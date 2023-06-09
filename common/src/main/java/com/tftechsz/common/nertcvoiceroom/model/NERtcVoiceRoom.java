package com.tftechsz.common.nertcvoiceroom.model;

import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;

import java.util.List;


/**
 * 语聊房
 */
public abstract class NERtcVoiceRoom {
    /**
     * 获取实例
     *
     */
    public static synchronized NERTCVideoCall sharedInstance() {
        return NERTCVideoCallImpl.sharedInstance();
    }

    /**
     * 销毁实例
     */
    public static void destroySharedInstance() {
        NERTCVideoCallImpl.destroySharedInstance();
    }

    /**
     * 初始化
     *
     * @param appKey   app kay
     */
    public abstract void init(String appKey, NERtcVoiceRoomDef.RoomCallback callback);

    /**
     * 设置音质（需要在进入房间前调用）
     *
     */
    public abstract void setAudioQuality(int quality);

    /**
     * 初始化房间
     *
     * @param voiceRoomInfo 房间信息}
     * @param user         资料信息}
     */
    public abstract void initRoom(VoiceRoomInfo voiceRoomInfo,
                                  VoiceRoomUser user);


    public abstract void setSeatList(List<VoiceRoomSeat> seatList);

    /**
     * 进入房间
     *
     * @param anchorMode 主播模式
     */
    public abstract void enterRoom(boolean anchorMode,VoiceRoomSeat voiceRoomSeat);


    public abstract void destroy();

    /**
     * 离开房间
     */
    public abstract void leaveRoom();

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
