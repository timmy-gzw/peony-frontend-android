package com.tftechsz.common.nim.model;

import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;

public interface NERTCCallingDelegate {

    /**
     * 返回操作
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    void onError(int errorCode, String userId, String errorMsg);

    /**
     * 被邀请通话回调
     *
     * @param invitedEvent 邀请参数
     */
    void onInvitedByUser(InvitedEvent invitedEvent,String callId);


    void onInvitedByUser(String fromId,String callId);

    /**
     * 如果有用户同意进入通话频道，那么会收到此回调
     *
     * @param userId 进入通话的用户
     */
    void onUserEnter(long userId);


    void onJoinChannel(long channelId);

    /**
     * 如果有用户同意离开通话，那么会收到此回调
     *
     * @param userId 离开通话的用户
     */
    void onUserHangup(long userId);

    /**
     * 拒绝通话
     *
     * @param userId 拒绝通话的用户
     */
    void onRejectByUserId(String userId);

    /**
     * 接受通话
     *
     * @param userId 接受通话的用户
     */
    void onAcceptByUserId(String userId);

    /**
     * 邀请方忙线
     *
     * @param userId 忙线用户
     */
    void onUserBusy(String userId);


    /**
     * 用户离开
     */
    void onUserLeave(String userId, String reason);


    /**
     * 作为被邀请方会收到，收到该回调说明本次通话被取消了
     */
    void onCancelByUserId(String userId);


    /**
     * 加入房间失败
     *
     * @param code
     */
    void onJoinRoomFailed(int code);

    /**
     * 连接失败了
     *
     * @param code
     */
    void onError(int code);

    /**
     * 远端用户开启/关闭了摄像头
     *
     * @param userId           远端用户ID
     * @param isVideoAvailable true:远端用户打开摄像头  false:远端用户关闭摄像头
     */
    void onCameraAvailable(long userId, boolean isVideoAvailable);


    void onUserVideoStop(long userId);



    void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats);

    /**
     * 远端用户开启/关闭了麦克风
     *
     * @param userId           远端用户ID
     * @param isVideoAvailable true:远端用户打开麦克风  false:远端用户关闭麦克风
     */
    void onAudioAvailable(long userId, boolean isVideoAvailable);

    /**
     * 呼叫超时
     * type  1:被邀请  2；正在呼叫别人
     */
    void timeOut(int type);
}
