package com.tftechsz.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.VoicePlayRegionsBean;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.AudiencePlay;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoom;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoomDef;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomMessage;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.Utils;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class PartyAudioService extends Service implements NERtcVoiceRoomDef.RoomCallback {
    protected String mRoomId = "";
    protected int mId;
    protected int mFightPattern;  //当前模式  1:普通模式  2：pk 模式
    private int partyLiveType;   //0 默认cdn  1 rtc
    private boolean enableEarBack = false;
    private int mUserId;
    protected VoiceRoomInfo mVoiceRoomInfo;   //房间信息
    protected NERTCVideoCall voiceRoom;
    private boolean isInit = false;
    private boolean isListener = false;
    //view
    protected CompositeDisposable mCompositeDisposable;
    private VoiceRoomSeat mVoiceRoomSeat;
    protected Audience audience;  // 观众

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    public class MyBinder extends Binder {
        public PartyAudioService getService() {
            return PartyAudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, true);
        mCompositeDisposable = new CompositeDisposable();
        isListener = false;
    }


    public void setListener() {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, true);
    }

    public void setSeatList(List<VoiceRoomSeat> microphone) {
        if (voiceRoom != null && microphone != null)
            voiceRoom.setSeatList(microphone);
    }


    public void muteAudio() {
        voiceRoom.muteLocalAudio(true);
    }


    public void muteLocalAudio(int status) {
        if (voiceRoom == null) return;
        boolean muted = voiceRoom.muteLocalAudio(status != 0);
        if (listener != null)
            listener.muteVoice(status, muted);
    }


    public void setEnableEarBack(boolean enableEarBack){
        this.enableEarBack = enableEarBack;
    }

    public boolean getEnableEarBack(){
        return enableEarBack;
    }


    public final void toggleMuteRoomAudio() {
        if (voiceRoom == null) return;
        boolean muted = voiceRoom.muteRoomAudio(!voiceRoom.isRoomAudioMute());
        if (listener != null)
            listener.muteRoomAudio(muted);
    }


    public void leaveRoom() {
        if (voiceRoom == null) return;
        voiceRoom.leaveRoom(false);
    }


    public boolean getRoomAudioStatus() {
        if (voiceRoom == null) return false;
        boolean muted = voiceRoom.isRoomAudioMute();
        return muted;
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            if (listener != null)
                listener.onIncomingMessage(messages);
            for (ChatRoomMessage message : messages) {
                if (message.getSessionType() != SessionTypeEnum.ChatRoom ||
                        !message.getSessionId().equals(mRoomId)) {
                    continue;
                }
                if (message.getMsgType() == MsgTypeEnum.custom) {
                    ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                    if (chatMsg == null) return;
                    ChatMsg.PartyMsg msg = JSON.parseObject(chatMsg.content, ChatMsg.PartyMsg.class);
                    if (TextUtils.equals(ChatMsg.PARTY_NOTICE, chatMsg.cmd_type)) {  //通知
                        if (TextUtils.equals("close_party_notice", chatMsg.cmd)) {   //关闭聊天室
                            leaveRoomService();
                        } else if (TextUtils.equals("operate_notice", chatMsg.cmd)) {  //房主或房管将某人设为房管，禁言，拉黑，踢出时，小秘书通知被设置人  msg:消息信息;type确定房管，禁言，拉黑，踢出类型，0房管，1拉黑，2踢出，3禁言
                            if (msg.type == 0) {

                            } else if (msg.type == 1 || msg.type == 2) {
                                Utils.toast(msg.msg);
                                leaveRoomService();
                            }
                        }
                    }
                }
            }
        }
    };


    public void leaveRoomService() {
        stopSelf();
        releaseAudience();
        removeCallBack();
        onDestroy();
    }


    /**
     * 初始化
     */
    public void initVoiceRoom(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser voiceRoomUser, int userId, String roomId, int partyLiveType) {
        this.partyLiveType = partyLiveType;
        mUserId = userId;
        mRoomId = roomId;
        if (mVoiceRoomSeat == null)
            mVoiceRoomSeat = new VoiceRoomSeat(voiceRoomInfo.index);
        NERtcVoiceRoom.setAccountMapper(accountId -> userId);
        voiceRoom = NERtcVoiceRoom.sharedInstance();
        voiceRoom.initRoom(voiceRoomInfo, mVoiceRoomSeat, voiceRoomUser, partyLiveType);
        if (!isInit) {
            voiceRoom.initNERtc(NERTCVideoCallImpl.VOICE_ROOM, this);
            isInit = true;
        }
    }


    /**
     * 初始化
     */
    public void setMode() {
        if (voiceRoom != null)
            voiceRoom.setMode(NERTCVideoCallImpl.VOICE_ROOM, this);
    }


    /**
     * 上麦
     */
    public void onSeat(VoiceRoomInfo voiceRoomInfo, VoiceRoomUser voiceRoomUser, int userId, int partyLiveType) {
        this.mVoiceRoomInfo = voiceRoomInfo;
        initVoiceRoom(mVoiceRoomInfo, voiceRoomUser, userId, mRoomId, partyLiveType);
        enterRoom(false, mVoiceRoomSeat);
        //观众 cdn 执行
        if (partyLiveType == 0) {
            audience = voiceRoom.getAudience();
            if (audience != null && audience.getAudiencePlay() != null)
                audience.getAudiencePlay().release();
            if (mVoiceRoomSeat == null)
                mVoiceRoomSeat = new VoiceRoomSeat(voiceRoomInfo.index);
            if (audience != null)
                audience.onSeat(mVoiceRoomSeat, new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {

                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
        }
    }

    /**
     * 离开麦位
     */
    public void setLeaveSeat(int partyLiveType) {
        if (voiceRoom == null) return;
        //0-cdn 1-rtc
        if (partyLiveType == 0) {
            voiceRoom.leaveRoom(false);
        } else {
            voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_AUDIENCE);
        }
    }

    /**
     * 拉流监听
     */
    public void setPullUrlInfo(VoiceRoomInfo voiceRoomInfo, int partyLiveType) {
        this.mVoiceRoomInfo = voiceRoomInfo;
        //0-cdn 1-rtc
        if (partyLiveType == 0) {
            if (audience == null && voiceRoom != null) {
                audience = voiceRoom.getAudience();
            }
            if (mVoiceRoomInfo != null && !TextUtils.isEmpty(mVoiceRoomInfo.rtmp_pull_url)) {
                audience.getAudiencePlay().play(mVoiceRoomInfo.rtmp_pull_url);
                audience.getAudiencePlay().registerNotify(new AudiencePlay.PlayerNotify() {
                    @Override
                    public void onPreparing() {
                    }

                    @Override
                    public void onPlaying() {
                    }

                    @Override
                    public void onError() {
                        if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance())) {
                            Utils.toast("主播网络好像出了问题");
                        }
                    }

                    @Override
                    public void getSeatVoice(List<VoicePlayRegionsBean> regions) {
                        if (listener != null)
                            listener.getSeatVoice(regions);
                    }
                });
            }
        } else if (partyLiveType == 1) {
            //观众
            if (voiceRoom != null) {
                voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_AUDIENCE);
                voiceRoom.enterRoom(false, null);
            }
        }
    }

    /**
     * 进入房间
     */
    public final void enterRoom(boolean anchorMode, VoiceRoomSeat voiceRoomSeat) {
        if (voiceRoom != null) {
            if (partyLiveType == 0) {  //cdn模式
                voiceRoom.enterRoom(anchorMode, voiceRoomSeat);
            } else {   //cdn
                voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_BROADCASTER);
//                voiceRoom.startLocalAudio();
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 释放播放器
     */
    public void releaseAudience() {
        if (audience != null && audience.getAudiencePlay() != null) {
            audience.getAudiencePlay().release();
        }
    }

    /**
     * 移除监听
     */
    public void removeCallBack() {
        if (voiceRoom != null) {
            voiceRoom.setMode(1, null);
        }
        addRoomCallBack(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        enableEarBack = false;
        int close = MMKVUtils.getInstance().decodeInt(Constants.PARAM_IS_CALL_CLOSE);
        releaseAudience();
        if (close == 1) {
            if (voiceRoom != null) {
                voiceRoom.releaseNERtc();
            }
            return;
        }
        if (voiceRoom != null) {
            voiceRoom.muteRoomAudio(false);
            voiceRoom.onDestroy();
        }
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, false);
        MMKVUtils.getInstance().encode(Constants.PARTY_IS_RUN, 0);
        isListener = false;
        isInit = false;
        if (NERTCVideoCallImpl.sharedInstance() != null) {
            if (NERTCVideoCallImpl.sharedInstance().getAudience() != null && NERTCVideoCallImpl.sharedInstance().getAudience().getAudiencePlay() != null)
                NERTCVideoCallImpl.sharedInstance().getAudience().getAudiencePlay().release();
            NERTCVideoCallImpl.sharedInstance().onDestroy();
        }
    }

    @Override
    public void onEnterRoom(boolean success) {

    }

    @Override
    public void onLeaveRoom() {
        if (listener != null)
            listener.onLeaveRoomSuccess();
    }

    @Override
    public void onRoomDismiss() {

    }

    @Override
    public void onOnlineUserCount(int onlineUserCount) {

    }

    @Override
    public void onAnchorInfo(VoiceRoomUser user) {

    }

    @Override
    public void onAnchorMute(boolean muted) {

    }

    @Override
    public void onAnchorVolume(int volume) {

    }

    @Override
    public void onMute(boolean muted) {

    }

    @Override
    public void updateSeats(List<VoiceRoomSeat> seats) {

    }

    @Override
    public void updateSeat(VoiceRoomSeat seat) {

    }

    @Override
    public void onSelfSeatVolume(int volume) {
        if (listener != null)
            listener.onSelfSeatVolume(volume);
    }


    @Override
    public void onSeatVolume(VoiceRoomSeat seat, int volume) {
        if (listener != null) {
            listener.onSeatVolume(seat, volume);
        }
    }

    @Override
    public void onVoiceRoomMessage(VoiceRoomMessage message) {

    }

    @Override
    public void onMusicStateChange(int type) {

    }

    @Override
    public void sendLeaveSeat() {

    }

    @Override
    public void sendOnSeat() {
        ChatMsgUtil.sendRoomVoiceTipMessage(mUserId + "", mRoomId, "party_seat_up");
    }


    public interface RoomCallBack {

        void onIncomingMessage(List<ChatRoomMessage> messages);

        void muteVoice(int status, boolean mute);

        void muteRoomAudio(boolean mute);

        void onSelfSeatVolume(int volume);

        void onSeatVolume(VoiceRoomSeat seat, int volume);

        void getSeatVoice(List<VoicePlayRegionsBean> regions);

        //云信回调离开房间成功才加入下一个房间
        void onLeaveRoomSuccess();
    }

    public RoomCallBack listener;

    public void addRoomCallBack(RoomCallBack listener) {
        this.listener = listener;
    }


}

