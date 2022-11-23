package com.tftechsz.common.nim.model.impl;

import android.app.Service;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.GsonUtils;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.stats.NERtcAudioRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcAudioSendStats;
import com.netease.lava.nertc.sdk.stats.NERtcAudioVolumeInfo;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.stats.NERtcStats;
import com.netease.lava.nertc.sdk.stats.NERtcStatsObserver;
import com.netease.lava.nertc.sdk.stats.NERtcVideoRecvStats;
import com.netease.lava.nertc.sdk.stats.NERtcVideoSendStats;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.SignallingServiceObserver;
import com.netease.nimlib.sdk.avsignalling.builder.CallParamBuilder;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelStatus;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.constant.InviteAckStatus;
import com.netease.nimlib.sdk.avsignalling.constant.SignallingEventType;
import com.netease.nimlib.sdk.avsignalling.event.CanceledInviteEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCloseEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCommonEvent;
import com.netease.nimlib.sdk.avsignalling.event.ControlEvent;
import com.netease.nimlib.sdk.avsignalling.event.InviteAckEvent;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserJoinEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserLeaveEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelBaseInfo;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.avsignalling.model.MemberInfo;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomQueueChangeAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomTempMuteAddAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomTempMuteRemoveAttachment;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.ChatRoomQueueChangeType;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.util.Entry;
import com.tftechsz.common.BuildConfig;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.event.VoiceChatEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nertcvoiceroom.model.Anchor;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.AudioPlay;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoomDef;
import com.tftechsz.common.nertcvoiceroom.model.PushTypeSwitcher;
import com.tftechsz.common.nertcvoiceroom.model.StreamTaskControl;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomMessage;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nertcvoiceroom.model.custom.CustomAttachParser;
import com.tftechsz.common.nertcvoiceroom.model.impl.AnchorImpl;
import com.tftechsz.common.nertcvoiceroom.model.impl.AudienceImpl;
import com.tftechsz.common.nertcvoiceroom.model.impl.AudioPlayImpl;
import com.tftechsz.common.nertcvoiceroom.model.impl.ChatRoomInfoExtKey;
import com.tftechsz.common.nertcvoiceroom.model.impl.ChatRoomMsgExtKey;
import com.tftechsz.common.nertcvoiceroom.model.impl.NERtcCallbackExImpl;
import com.tftechsz.common.nertcvoiceroom.model.impl.NERtcVoiceRoomInner;
import com.tftechsz.common.nertcvoiceroom.model.impl.PushTypeSwitcherImpl;
import com.tftechsz.common.nertcvoiceroom.model.impl.RoomQuery;
import com.tftechsz.common.nertcvoiceroom.model.impl.StreamTaskControlImpl;
import com.tftechsz.common.nertcvoiceroom.model.ktv.SEI;
import com.tftechsz.common.nertcvoiceroom.model.ktv.impl.MusicSingImpl;
import com.tftechsz.common.nertcvoiceroom.util.SuccessCallback;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.nim.UserPreferences;
import com.tftechsz.common.nim.model.CallErrorCode;
import com.tftechsz.common.nim.model.CallParams;
import com.tftechsz.common.nim.model.CustomInfo;
import com.tftechsz.common.nim.model.JoinChannelCallBack;
import com.tftechsz.common.nim.model.NERTCCallingDelegate;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.ProfileManager;
import com.tftechsz.common.nim.model.TokenService;
import com.tftechsz.common.nim.model.UserInfoInitCallBack;
import com.tftechsz.common.nim.model.VideoCallOptions;
import com.tftechsz.common.utils.AppUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


public class NERTCVideoCallImpl extends NERtcVoiceRoomInner {
    public static final int CALL = 1;   //音视频
    public static final int VOICE_ROOM = 2;  //语音房


    private static final String CURRENT_VERSION = "1.3.1";

    private static final String LOG_TAG = "NERTCVideoCallImpl";

    private static final int STATE_IDLE = 0;//状态空闲

    private static final int STATE_INVITED = 1;//被邀请了

    private static final int STATE_CALL_OUT = 2;//正在呼叫别人

    private static final int STATE_DIALOG = 3;//通话中

    private static final int TIME_OUT_LIMITED = 60 * 1000;//呼叫超时限制
    private long selfRtcUid;
    private int status;//初始化状态 0
    private boolean handleUserAccept = false;//是否已经处理用户接收事件
    private CopyOnWriteArrayList<InviteParamBuilder> invitedParams;//邀请别人后保留的邀请信息

    private boolean isReceive = false;  //是否收到通话邀请

    private boolean haveJoinNertcChannel = false;//是否加入了NERTC的频道

    private static NERTCVideoCallImpl instance;

    /**
     * 加入 rtc 房间名称
     */
    private final StateParam rtcChannelName = new StateParam();

    private NERTCInternalDelegateManager delegateManager;

    private UserInfoInitCallBack userInfoInitCallBack;//用户信息初始化回调

    private VideoCallOptions options;

    private TokenService tokenService;

    private String callerUserId;

    private String appKey;

    private InviteParamBuilder invitedParam;//邀请别人后保留的邀请信息

    private String imChannelId;//IM渠道号

    private int timeOut = TIME_OUT_LIMITED;//呼叫超时

    //呼叫类型
    private int callType;

    private CountDownTimer timer;//呼出倒计时

    private Map<Long, String> memberInfoMap;

    private boolean isinitNERtc = false;

    private final UserProviderService service;
    private MineService mineService;

    private String token;
    private String callId;
    private String channelName;

    private static final String BUSY_LINE = "i_am_busy";

    /**
     * 音视频引擎
     */
    private NERtcEx engine;
    /**
     * 聊天室服务
     */
    private ChatRoomService chatRoomService;
    /**
     * 房间信息
     */
    private VoiceRoomInfo voiceRoomInfo;


    private VoiceRoomSeat microphoneHost;  //主播

    private VoiceRoomSeat voiceRoomSeat;

    private RoomQuery roomQuery;

    /**
     * 用户信息
     */
    private VoiceRoomUser user;

    /**
     * 主播模式
     */
    private boolean anchorMode;

    /**
     * 房间静音状态
     */
    private boolean muteRoomAudio;

    /**
     * 耳返状态
     */
    private boolean enableEarBack;

    /**
     * 采集音量，默认100
     */
    private int audioCaptureVolume = 100;

    /**
     * 房间状态回调
     */
    private NERtcVoiceRoomDef.RoomCallback roomCallback;

    private final List<VoiceRoomSeat> seats = new ArrayList<>();

    private boolean initial = false;

    private int mode;   //1 音视频  2 语音房

    private int partyLiveType;  //0-cdn 1-rtc 2-rtc+cdn

    private boolean sendTip;


    public static synchronized NERTCVideoCall sharedInstance() {

        if (instance == null) {
            instance = new NERTCVideoCallImpl();
        }
        return instance;
    }

    public static synchronized void destroySharedInstance() {
        if (instance != null) {
            instance.destroy();
            instance = null;
        }
    }

    /**
     * 信令在线消息的回调
     */
    Observer<ChannelCommonEvent> nimOnlineObserver = (Observer<ChannelCommonEvent>) event -> handleNIMEvent(event);
    //接受到消息了
    private Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {

        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages != null) {
                for (IMMessage imMessage : imMessages) {
                    ChatMsg chatMsg = ChatMsgUtil.parseMessage(imMessage);
                    if (chatMsg != null) {
                        if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {
                            ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                            if (callMsg.callee == 0) {   //被动方接收到了消息
                                if (callMsg.state == 0) { // 接收到消息了
                                    //空闲状态
                                    if (status == STATE_IDLE) {
                                        token = callMsg.room_token;
                                        callId = callMsg.call_id;
                                        channelName = callMsg.channel_name;
                                        service.setCallType(chatMsg.type);
                                        if (callMsg.type != null)
                                            service.setMatchType(callMsg.type);
                                        service.setCallId(callId);
                                        if(TextUtils.isEmpty(callId)){
                                            ARouter.getInstance()
                                                    .navigation(MineService.class)
                                                    .trackEvent("callId", "callId为空了", callId,
                                                            JSON.toJSONString(callMsg), null);
                                        }
                                        //当时视频速配过来询问匹配时候调用
                                        service.setCallIsMatch(TextUtils.equals(callMsg.type, ChatMsg.CALL_MATCH_FORCE));
                                        if (System.currentTimeMillis() - imMessage.getTime() > 6000)
                                            return;
                                        //走IM邀请
                                        if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_call_after_join_room == 1) {
                                            if (delegateManager != null && !isReceive) {
                                                RxBus.getDefault().post(new VoiceChatEvent(Constants.NOTIFY_EXIT_VOICE_ROOM));
                                                startCount();
                                                delegateManager.onInvitedByUser(chatMsg.from, callId);
                                                isReceive = true;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (TextUtils.equals(ChatMsg.FAMILY_NOTICE, chatMsg.cmd_type)) {
                            if (!TextUtils.equals(imMessage.getFromAccount(), String.valueOf(service.getUserId()))) {
                                ChatMsg.ApplyMessage message = JSON.parseObject(chatMsg.content, ChatMsg.ApplyMessage.class);
                                if (message != null) {
                                    MMKVUtils.getInstance().encode(service.getUserId() + Constants.FAMILY_ANNOUNCEMENT, message.message);
                                }
                            }
                            setNotify(imMessage);
                        } else {
                            if (status == STATE_IDLE && !TextUtils.equals(chatMsg.cmd_type, ChatMsg.TIP_TYPE) && !TextUtils.equals(ChatMsg.REPLY_ACCOST_TYPE, chatMsg.cmd)) {
                                setNotify(imMessage);
                            }
                        }
                    } else {
                        setNotify(imMessage);
                    }
                }
            }
        }
    };


    private void setNotify(IMMessage imMessage) {
        Team team = NimUIKit.getTeamProvider().getTeamById(imMessage.getSessionId());
        if (team != null && team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.Mute) {
            return;
        }
        //活动通知的消息不显示声音和震动
        if (TextUtils.equals(Constants.ACTIVITY_NOTICE, imMessage.getSessionId()) && imMessage.getSessionType() == SessionTypeEnum.P2P)
            return;
        if (UserPreferences.getRingToggle()) {
            ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.MESSAGE);
        }
        setVibrate();

    }


    /**
     * 消息状态变化观察者 回调
     */
    private final Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            NimLog.i("TAG124123412", String.format("content: %s, callbackExt: %s", message.getContent(), message.getCallbackExtension()));
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getCallbackExtension());
            try {
                if (null != chatMsg && TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) { //语音视频消息回调
                    ChatMsg.CallMsg callMsg1 = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                    if (callMsg1.state == 0) {
                        callId = callMsg1.call_id;
                        token = callMsg1.room_token;
                        channelName = callMsg1.channel_name;
                        service.setCallId(callMsg1.call_id);
                        service.setRoomToken(callMsg1.room_token);
                        service.setChannelName(callMsg1.channel_name);
                        service.setMatchType(callMsg1.type);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void setVibrate() {
        if (UserPreferences.getVibrateToggle()) {
            Vibrator vibrator = (Vibrator) BaseApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
            long[] pattern = {200, 350, 200, 350};
            vibrator.vibrate(pattern, -1);
        }
    }

    /**
     * 信令离线消息
     */
    Observer<ArrayList<ChannelCommonEvent>> nimOfflineObserver = (Observer<ArrayList<ChannelCommonEvent>>) channelCommonEvents -> {
        if (channelCommonEvents != null && channelCommonEvents.size() > 0) {
            handleOfflineEvents(channelCommonEvents);
        }
    };


    /**
     * 处理离线消息
     */
    private void handleOfflineEvents(ArrayList<ChannelCommonEvent> offlineEvent) {
        if (offlineEvent != null && offlineEvent.size() > 0) {
            ArrayList<ChannelCommonEvent> usefulEvent = new ArrayList<>();
            for (ChannelCommonEvent event : offlineEvent) {
                LogUtil.e("===============收到消息了", event.getChannelBaseInfo().getChannelStatus() + "===" + event.getEventType() + "===" + event.getChannelBaseInfo().getChannelId());
                if (event.getChannelBaseInfo().getChannelStatus() == ChannelStatus.NORMAL) {
                    if (event.getEventType() == SignallingEventType.CANCEL_INVITE) {
                        String channelId = event.getChannelBaseInfo().getChannelId();
                        for (ChannelCommonEvent event1 : usefulEvent) {
                            if (TextUtils.equals(channelId, event1.getChannelBaseInfo().getChannelId())) {
                                usefulEvent.remove(event1);
                                break;
                            }
                        }
                    } else {
                        usefulEvent.add(event);
                    }
                }
            }
            for (ChannelCommonEvent commonEvent : usefulEvent) {
                handleNIMEvent(commonEvent);
            }
        }
    }


    /**
     * 处理IM信令事件
     *
     * @param event
     */
    private void handleNIMEvent(ChannelCommonEvent event) {
        SignallingEventType eventType = event.getEventType();
        LogUtil.e("==============", "" + eventType);
        switch (eventType) {
            case CLOSE:
                //信令channel被关闭
                ChannelCloseEvent channelCloseEvent = (ChannelCloseEvent) event;
                if (TextUtils.equals(channelCloseEvent.getChannelBaseInfo().getChannelId(), imChannelId)) {
                    hangup();
                    imChannelId = null;
                }
                break;
            case JOIN:
                UserJoinEvent userJoinEvent = (UserJoinEvent) event;
                MemberInfo memberInfo = userJoinEvent.getMemberInfo();
                updateMemberMap(memberInfo);
                if (delegateManager != null) {
                    delegateManager.onUserEnter(Long.parseLong(userJoinEvent.getFromAccountId()));
                }
                LogUtil.e("==============", "" + eventType + Long.parseLong(userJoinEvent.getFromAccountId()) + memberInfo.getAccountId());
                break;
            case INVITE:
                InvitedEvent invitedEvent = (InvitedEvent) event;
                if (delegateManager != null) {
                    if (status != STATE_IDLE) { //占线，直接拒绝
                        InviteParamBuilder paramBuilder = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                                invitedEvent.getFromAccountId(), invitedEvent.getRequestId());
                        paramBuilder.customInfo(BUSY_LINE);
                        reject(paramBuilder, false, null);
                        buriedPoint("busy", "callStatus", invitedEvent.getChannelBaseInfo().getChannelId(), "");
                    } else {
                        startCount();
                        if (!isReceive) {
                            RxBus.getDefault().post(new VoiceChatEvent(Constants.NOTIFY_EXIT_VOICE_ROOM));
                            isReceive = true;
                            delegateManager.onInvitedByUser(invitedEvent, callId);
                        }
                        SPUtils.saveObject(Constants.INVITED_EVENT, invitedEvent);
                    }
                }
                status = STATE_INVITED;
                setCallType(invitedEvent);
                break;
            case CANCEL_INVITE:
                CanceledInviteEvent canceledInviteEvent = (CanceledInviteEvent) event;
                if (delegateManager != null) {
                    delegateManager.onCancelByUserId(canceledInviteEvent.getFromAccountId());
                }
                status = STATE_IDLE;
                isReceive = false;
                SPUtils.remove(Constants.INVITED_EVENT);
                break;
            case REJECT:
            case ACCEPT:
                InviteAckEvent ackEvent = (InviteAckEvent) event;
                if (delegateManager != null) {
                    if (ackEvent.getAckStatus() == InviteAckStatus.REJECT) {
                        if (TextUtils.equals(ackEvent.getCustomInfo(), BUSY_LINE)) {
                            delegateManager.onUserBusy(ackEvent.getFromAccountId());
                        } else {
                            delegateManager.onRejectByUserId(ackEvent.getFromAccountId());
                        }
                        status = STATE_IDLE;
                        isReceive = false;
                    } else {
                        delegateManager.onAcceptByUserId(ackEvent.getFromAccountId());
                    }
                }
                break;
            case LEAVE:
                UserLeaveEvent userLeaveEvent = (UserLeaveEvent) event;
                SPUtils.remove(Constants.INVITED_EVENT);
//                if (delegateManager != null) {
//                    delegateManager.onUserLeave(userLeaveEvent.getFromAccountId(), "");
//                }
                status = STATE_IDLE;
                break;
            case CONTROL:
                ControlEvent controlEvent = (ControlEvent) event;

                break;
        }
    }

    long mChannelId;

    /**
     * Nertc的回调
     */
    private NERtcCallback rtcCallback = new NERtcCallback() {

        @Override
        public void onJoinChannel(int i, long channelId, long l1, long l2) {
            mChannelId = channelId;
            haveJoinNertcChannel = true;
            service.setChannelId(channelId);
            if (delegateManager != null) {
                delegateManager.onJoinChannel(mChannelId);
            }
            buriedPoint("onJoinChannel", "callStatus", mChannelId + "", "");
        }

        @Override
        public void onLeaveChannel(int i) {
            status = STATE_IDLE;
            isReceive = false;
            haveJoinNertcChannel = false;

        }

        @Override
        public void onUserJoined(long l) {
            if (service.getUserId() != l) {
                status = STATE_DIALOG;
                isReceive = false;
            }
            if (delegateManager != null) {
                delegateManager.onUserEnter(l);
            }
        }

        @Override
        public void onUserLeave(long uid, int reason) {
            status = STATE_IDLE;
            isReceive = false;
            LogUtil.e("NERtcCallback", "onUserLeave -- 用户离开了" + uid + reason);
            if (delegateManager != null) {
                delegateManager.onUserHangup(uid);
            }
        }

        @Override
        public void onUserAudioStart(long l) {
            LogUtil.e("NERtcCallback", "--onUserAudioStart--");
            if (service.getUserId() != l) {
                NERtcEx.getInstance().subscribeRemoteAudioStream(l, true);
            }
        }

        @Override
        public void onUserAudioStop(long l) {
            LogUtil.e("NERtcCallback", "--onUserAudioStop--");
        }

        @Override
        public void onUserVideoStart(long l, int i) {
            LogUtil.e("NERtcCallback", l + "---onUserVideoStart-" + service.getUserId());
            if (service.getUserId() != l) {
                NERtcEx.getInstance().subscribeRemoteVideoStream(l, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
            }
            if (delegateManager != null) {
                delegateManager.onCameraAvailable(l, true);
            }
        }

        @Override
        public void onUserVideoStop(long l) {
            LogUtil.e("NERtcCallback", l + "---onUserVideoStop-" + service.getUserId());
            if (delegateManager != null) {
                delegateManager.onCameraAvailable(l, false);
            }
            if (delegateManager != null) {
                delegateManager.onUserVideoStop(l);
            }

        }

        @Override
        public void onDisconnect(int i) {
            LogUtil.e("NERtcCallback", "未连接上" + i);
            status = STATE_IDLE;
            isReceive = false;
            if (delegateManager != null) {
                delegateManager.onError(i);
            }
        }

        @Override
        public void onClientRoleChange(int i, int i1) {
            LogUtil.e("NERtcCallback", "---onClientRoleChange---" + i);
        }
    };

    /**
     * rtc 状态监控
     */
    private NERtcStatsObserver statsObserver = new NERtcStatsObserver() {

        @Override
        public void onRtcStats(NERtcStats neRtcStats) {

        }

        @Override
        public void onLocalAudioStats(NERtcAudioSendStats neRtcAudioSendStats) {

        }

        @Override
        public void onRemoteAudioStats(NERtcAudioRecvStats[] neRtcAudioRecvStats) {

        }

        @Override
        public void onLocalVideoStats(NERtcVideoSendStats neRtcVideoSendStats) {

        }

        @Override
        public void onRemoteVideoStats(NERtcVideoRecvStats[] neRtcVideoRecvStats) {

        }

        @Override
        public void onNetworkQuality(NERtcNetworkQualityInfo[] neRtcNetworkQualityInfos) {
            if (delegateManager != null) {
                delegateManager.onUserNetworkQuality(neRtcNetworkQualityInfos);
            }
        }
    };

    @Override
    public void initNERtc(int mode, NERtcVoiceRoomDef.RoomCallback callback) {
        LogUtil.e("=========================================", "初始化成功" + isinitNERtc + mode);
        this.mode = mode;
        if (engine == null)
            setInitNERtc(callback);
        if (isinitNERtc && mode == VOICE_ROOM) {
            return;
        }
        setInitNERtc(callback);
    }


    private void setInitNERtc(NERtcVoiceRoomDef.RoomCallback callback) {
        try {
            invitedParams = new CopyOnWriteArrayList<>();
            memberInfoMap = new HashMap<>();
            NERtcParameters parameters = new NERtcParameters();
//            parameters.setBoolean(NERtcParameters.KEY_AUDIO_AI_NS_ENABLE, true);
            parameters.setBoolean(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, true);
            parameters.set(NERtcParameters.KEY_PUBLISH_SELF_STREAM, true);
            NERtc.getInstance().setParameters(parameters);


            //语聊房间
            if (mode == VOICE_ROOM) {
                int scenario = NERtcConstants.AudioScenario.MUSIC;
                int profile = NERtcConstants.AudioProfile.HIGH_QUALITY_STEREO;
                NERtcEx.getInstance().setChannelProfile(NERtcConstants.RTCChannelProfile.LIVE_BROADCASTING);
                NERtcEx.getInstance().setAudioProfile(profile, scenario);
                appKey = Constants.YUNXIN_ROOM_APP_ID;
                //有数据根据配置来读取
                if (service != null && service.getConfigInfo() != null && service.getConfigInfo().sys != null && !TextUtils.isEmpty(service.getConfigInfo().sys.yunxin_live_app_key)) {
                    appKey = service.getConfigInfo().sys.yunxin_live_app_key;
                }
            } else {  //1v1
                int scenario = NERtcConstants.AudioScenario.SPEECH;
                int profile = NERtcConstants.AudioProfile.STANDARD;
                NERtcEx.getInstance().setAudioProfile(profile, scenario);
                appKey = AppUtils.getYXAppId();
            }
            LogUtil.e("==============================", mode + "==================" + appKey);
            options = new VideoCallOptions(null, ProfileManager.getInstance());
            userInfoInitCallBack = options.getUserInfoInitCallBack();
            roomCallback = callback;
            NERtcOption option = new NERtcOption();
            option.logLevel = BuildConfig.DEBUG ? NERtcConstants.LogLevel.DEBUG : NERtcConstants.LogLevel.WARNING;
            engine.init(BaseApplication.getInstance(), appKey, this.callback, option);
            initial = true;
            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
                @Override
                public void onEvent(StatusCode statusCode) {
                    if (statusCode == StatusCode.LOGINED) {
                        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(this, false);
                        if (userInfoInitCallBack != null) {
                            userInfoInitCallBack.onUserLoginToIm(service.getUserId() + "", service.getToken());
                        }
                    }
                }
            }, true);
            isinitNERtc = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMode(int mode, NERtcVoiceRoomDef.RoomCallback callback) {
        this.mode = mode;
        this.roomCallback = callback;
    }

    @Override
    public void releaseNERtc() {
        isinitNERtc = false;
//        NERtcEx.getInstance().setStatsObserver(null);
        if (engine != null)
            engine.release();
        NERtc.getInstance().release();
        NERtcEx.getInstance().release();
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
    }


    /**
     * 设置当前的角色
     *
     * @param role CLIENT_ROLE_AUDIENCE 观众
     *             CLIENT_ROLE_BROADCASTER 主播
     */
    @Override
    public void setClientRole(int role) {
        engine.setClientRole(role);
    }

    @Override
    public void setAudioQuality(int quality) {
        int scenario = NERtcConstants.AudioScenario.MUSIC;
        int profile = NERtcConstants.AudioProfile.HIGH_QUALITY_STEREO;
        engine.setChannelProfile(NERtcConstants.RTCChannelProfile.LIVE_BROADCASTING);
        engine.setAudioProfile(profile, scenario);
        NERtcEx.getInstance().adjustRecordingSignalVolume(400);
    }

    @Override
    public void initRoom(VoiceRoomInfo voiceRoomInfo, VoiceRoomSeat microphoneHost, VoiceRoomUser user, int partyLiveType) {
        this.voiceRoomInfo = voiceRoomInfo;
        this.microphoneHost = microphoneHost;
        this.partyLiveType = partyLiveType;
        this.user = user;
        if (voiceRoomInfo != null)
            this.roomQuery = new RoomQuery(voiceRoomInfo, chatRoomService);
        anchor.initRoom(voiceRoomInfo);
    }


    private NERTCVideoCallImpl() {//先设置参数，后初始化
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        engine = NERtcEx.getInstance();
        delegateManager = new NERTCInternalDelegateManager();
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageReceiverObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, true);
        NIMClient.getService(SignallingServiceObserver.class).observeOnlineNotification(nimOnlineObserver, true);
        chatRoomService = NIMClient.getService(ChatRoomService.class);
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
//        NIMClient.getService(SignallingServiceObserver.class).observeOfflineNotification(nimOfflineObserver, true);
    }

    @Override
    public void addDelegate(NERTCCallingDelegate delegate) {
        delegateManager.addDelegate(delegate);
    }

    @Override
    public void removeDelegate(NERTCCallingDelegate delegate) {
        delegateManager.removeDelegate(delegate);
    }

    @Override
    public void setupRemoteView(NERtcVideoView videoRender, long uid) {
        videoRender.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        NERtc.getInstance().setupRemoteVideoCanvas(videoRender, uid);
    }

    @Override
    public void setupLocalView(NERtcVideoView videoRender) {
        videoRender.setZOrderMediaOverlay(true);
        videoRender.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_BALANCED);
        NERtc.getInstance().setupLocalVideoCanvas(videoRender);
    }

    @Override
    public void setTimeOut(int timeOut) {
        if (timeOut < TIME_OUT_LIMITED) {
            this.timeOut = timeOut;
        }
    }

    /**
     * 启动倒计时，用于实现timeout
     */
    private void startCount() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(timeOut, 1000) {
            @Override
            public void onTick(long l) {
                if (status != STATE_CALL_OUT && status != STATE_INVITED) {
                    timer.cancel();
                }
            }

            @Override
            public void onFinish() {
                if (delegateManager != null) {
                    delegateManager.timeOut(status);
                }
                if (status == STATE_CALL_OUT) {
                    NERTCVideoCallImpl.this.cancel(null);
                } else if (status == STATE_INVITED) {
                    imChannelId = null;
                    hangup();
                }
            }
        };
        timer.start();
    }

    @Override
    public void call(final String userId, ChannelType type) {
        final String requestId = String.valueOf(System.currentTimeMillis());
        if (TextUtils.isEmpty(service.getMatchType())) {
            timeOut = TIME_OUT_LIMITED;
        } else {
            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null)
                timeOut = service.getConfigInfo().share_config.matching_wait_time * 1000;
            else
                timeOut = TIME_OUT_LIMITED;
        }
        status = STATE_CALL_OUT;
        //启动倒计时
        Utils.runOnUiThreadDelayed(this::startCount, 1000);
        CallParamBuilder paramBuilder = new CallParamBuilder(type, userId, requestId);
        paramBuilder.offlineEnabled(true);
        //信令呼叫
        NIMClient.getService(SignallingService.class).call(paramBuilder).setCallback(new RequestCallbackWrapper<ChannelFullInfo>() {
            @Override
            public void onResult(int code, ChannelFullInfo result, Throwable exception) {
                if (result != null) {
                    String channelId = result.getChannelId();
                    //保留邀请信息，取消用
                    invitedParam = new InviteParamBuilder(result.getChannelId(), userId, requestId);
                    imChannelId = result.getChannelId();
                    long uid = service.getUserId();
                    String roomToken = TextUtils.isEmpty(token) ? service.getRoomToken() : token;
                    String channel = TextUtils.isEmpty(channelName) ? service.getChannelName() : channelName;
                    joinChannel(roomToken, channel, uid);
                } else {
                    Log.d(LOG_TAG, "call failed code = " + code);
                    if (delegateManager != null) {
                        delegateManager.onError(code, userId, "对方不在线");
                    }
                    status = STATE_IDLE;
                    isReceive = false;
                }
            }
        });
    }


    @Override
    public void groupCall(ArrayList<String> userIds, String groupId, String selfUserId, ChannelType type, String extraInfo, @NonNull JoinChannelCallBack joinChannelCallBack) {
        if (!TextUtils.equals(extraInfo, "invite") && status != STATE_IDLE) {
            joinChannelCallBack.onJoinFail("status Error", -1);
            delegateManager.onError(CallErrorCode.STATUS_ERROR, "groupCall status error: status = " + status, "");
            return;
        }
//        if (userIds == null || userIds.size() <= 0) {
//            ToastUtils.showLong("呼出参数错误");
//            return;
//        }
        status = STATE_CALL_OUT;
        startCount();//启动倒计时
        callType = CallParams.CallType.TEAM;
        handleUserAccept = false;
        callerUserId = selfUserId;
        //1,创建channel
        createIMChannelAndJoin(CallParams.CallType.TEAM, groupId, type, selfUserId, userIds, null, extraInfo, joinChannelCallBack);

    }

    @Override
    public void accept(InviteParamBuilder invitedParam, String selfAccId, JoinChannelCallBack joinChannelCallBack) {
        final long selfUid = service.getUserId();
        NIMClient.getService(SignallingService.class).acceptInviteAndJoin(invitedParam, selfUid).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {
                    @Override
                    public void onResult(int code, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            if (channelFullInfo != null && !TextUtils.isEmpty(channelFullInfo.getChannelId())) {
                                imChannelId = channelFullInfo.getChannelId();
                                //加入rtc Channel
                                storeUid(channelFullInfo.getMembers(), selfAccId);
                                //保存channel 里面的member 信息
                                for (MemberInfo memberInfo : channelFullInfo.getMembers()) {
                                    updateMemberMap(memberInfo);
                                }
                                if (callType == CallParams.CallType.TEAM) {
                                    loadToken(selfRtcUid, new RequestCallback<LiveTokenDto>() {
                                        @Override
                                        public void onSuccess(LiveTokenDto data) {
                                            LogUtil.e("================", data.token + "===============" + MMKVUtils.getInstance().decodeString("room_token"));
                                            int rtcResult = joinChannel(MMKVUtils.getInstance().decodeString("room_token"), MMKVUtils.getInstance().decodeString("room_name"), selfUid);
                                            if (joinChannelCallBack != null) {
                                                if (rtcResult == 0) {
                                                    joinChannelCallBack.onJoinChannel(channelFullInfo);
                                                } else {
                                                    joinChannelCallBack.onJoinFail("join rtc failed!", -1);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailed(int i) {
                                            if (joinChannelCallBack != null) {
                                                joinChannelCallBack.onJoinFail("join rtc failed!", -1);
                                            }
                                        }

                                        @Override
                                        public void onException(Throwable throwable) {
                                            if (joinChannelCallBack != null) {
                                                joinChannelCallBack.onJoinFail("join rtc failed!", -1);
                                            }
                                        }
                                    });
                                } else {
                                    joinChannel(token, channelName, selfUid);
                                }
                            } else {
                                if (delegateManager != null) {
                                    delegateManager.onJoinRoomFailed(-1);
                                }
                            }
                        } else {
                            if (delegateManager != null) {
                                delegateManager.onJoinRoomFailed(code);
                            }
                        }
                    }
                });
    }

    ChannelBaseInfo channelBaseInfo;

    /**
     * 创建IM渠道并加入
     *
     * @param callType            呼叫类型
     * @param type                通话类型
     * @param selfUserId          自己的用户ID
     * @param userIds             呼叫的用户list
     * @param callUserId          呼叫的单个用户
     * @param joinChannelCallBack 回调
     */
    private void createIMChannelAndJoin(int callType, String groupId, ChannelType type, String selfUserId,
                                        ArrayList<String> userIds, String callUserId, String extraInfo, JoinChannelCallBack joinChannelCallBack) {
        if (TextUtils.equals("invite", extraInfo)) {
            if (userIds != null && userIds.size() > 0) {
                //加入rtc成功，遍历所有的用户并邀请
                ArrayList<String> allUserIds = new ArrayList<>(userIds);
                //多人通话模式需要循环邀请所有用户
                for (String userId : userIds) {
                    if (!TextUtils.isEmpty(userId)) {
                        inviteOneUserWithIM(callType, type, userId, selfUserId, imChannelId, groupId, allUserIds, extraInfo);
                    }
                }
            } else {
                joinChannelCallBack.onJoinFail("join channel failed", -1);
            }
            return;
        }
        NIMClient.getService(SignallingService.class).create(type, null, null).setCallback(new RequestCallback<ChannelBaseInfo>() {
            @Override
            public void onSuccess(ChannelBaseInfo param) {
                //2,join channel
                if (param != null) {
                    imChannelId = param.getChannelId();
                    channelBaseInfo = param;
                    joinIMChannel(callType, groupId, type, param, selfUserId, userIds, callUserId, extraInfo, joinChannelCallBack);
                }
            }

            @Override
            public void onFailed(int code) {
                joinChannelCallBack.onJoinFail("create channel failed code", code);
                callFailed(code, null);
            }

            @Override
            public void onException(Throwable exception) {

            }
        });

    }

    /**
     * 呼叫失败处理
     *
     * @param code
     */
    private void callFailed(int code, String imChannelId) {
        if (delegateManager != null) {
            delegateManager.onError(code, "呼叫失败", "");
        }
        if (!TextUtils.isEmpty(imChannelId)) {
            closeIMChannel(imChannelId, null);
        }
        status = STATE_IDLE;
    }

    /**
     * 加入IM的频道
     *
     * @param channelInfo 待加入信令的通道信息
     * @param selfUserId  当前用户 IM 账号 id
     */
    private void joinIMChannel(int callType, String groupId, ChannelType type, ChannelBaseInfo channelInfo, String selfUserId, ArrayList<String> userIds,
                               String callUserId, String extraInfo, JoinChannelCallBack joinChannelCallBack) {

        NIMClient.getService(SignallingService.class).join(channelInfo.getChannelId(), 0, "", true).setCallback(new RequestCallback<ChannelFullInfo>() {
            @Override
            public void onSuccess(ChannelFullInfo param) {
                //保存Uid
                storeUid(param.getMembers(), selfUserId);
                if (callType == CallParams.CallType.TEAM) {
                    //多人通话直接加入rtc channel 然后发出邀请
                    loadToken(selfRtcUid, new RequestCallback<LiveTokenDto>() {

                        @Override
                        public void onSuccess(LiveTokenDto data) {
                            int rtcResult = joinChannel(MMKVUtils.getInstance().decodeString("room_token"), MMKVUtils.getInstance().decodeString("room_name"), service.getUserId());
                            LogUtil.e("================", data.token + "===============" + MMKVUtils.getInstance().decodeString("room_token"));
                            if (rtcResult == 0 && userIds != null && userIds.size() > 0) {
                                //加入rtc成功，遍历所有的用户并邀请
                                ArrayList<String> allUserIds = new ArrayList<>(userIds);
                                //多人通话模式需要循环邀请所有用户
                                for (String userId : userIds) {
                                    if (!TextUtils.isEmpty(userId)) {
                                        inviteOneUserWithIM(callType, type, userId, selfUserId, channelInfo.getChannelId(), groupId, allUserIds, extraInfo);
                                    }
                                }
                                joinChannelCallBack.onJoinChannel(param);
                            } else {
                                joinChannelCallBack.onJoinFail("join channel failed", rtcResult);
                            }
                        }

                        @Override
                        public void onFailed(int i) {
                            loadTokenError();
                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }
                    });

                }
            }

            @Override
            public void onFailed(int code) {
                joinChannelCallBack.onJoinFail("join im channel failed", code);
                callFailed(code, channelInfo.getChannelId());
            }

            @Override
            public void onException(Throwable exception) {

            }
        });


    }

    private void loadTokenError() {
        Log.d(LOG_TAG, "request token failed ");
        if (callType == Utils.ONE_TO_ONE_CALL) {
            hangup();
            delegateManager.onError(CallErrorCode.LOAD_TOKEN_ERROR, "get token error", "");
        } else {
            leave(null);
        }
    }

    /**
     * 邀请用户加入channel
     *
     * @param callType
     * @param userId
     * @param selfUid
     * @param callUsers
     */
    private void inviteOneUserWithIM(int callType, ChannelType channelType, String userId, String selfUid, String channelId, String groupId, ArrayList<String> callUsers, String extraInfo) {
        String invitedRequestId = getRequestId();
        InviteParamBuilder inviteParam = new InviteParamBuilder(channelId, userId, invitedRequestId);
        CustomInfo customInfo = new CustomInfo(callType, callUsers, groupId, channelId, String.valueOf(selfRtcUid), CURRENT_VERSION, extraInfo);
        inviteParam.customInfo(GsonUtils.toJson(customInfo));
        inviteParam.offlineEnabled(true);
        // 主叫方保存 rtc channelName
        rtcChannelName.updateParam(customInfo.channelName);
        NIMClient.getService(SignallingService.class).invite(inviteParam).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                //保留邀请信息，取消用
                saveInvitedInfo(inviteParam);
            }

            @Override
            public void onFailed(int code) {
                //推送可达算成功
                if (code == ResponseCode.RES_PEER_NIM_OFFLINE || code == ResponseCode.RES_PEER_PUSH_OFFLINE) {
                    saveInvitedInfo(inviteParam);
                }
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }


    private void saveInvitedInfo(InviteParamBuilder inviteParam) {
        invitedParams.add(inviteParam);
    }

    private void loadToken(final long uid, RequestCallback<LiveTokenDto> callback) {
        if (TextUtils.isEmpty(appKey)) {
            callback.onFailed(-1);
            return;
        }
        if (tokenService == null) {
            callback.onFailed(-2);
            return;
        }

        tokenService.getToken(uid, callback);
    }

    private void updateMemberMap(MemberInfo memberInfo) {

        if (memberInfoMap != null)
            memberInfoMap.put(memberInfo.getUid(), memberInfo.getAccountId());
    }

    /**
     * 生成随机数座位requestID
     *
     * @return
     */
    private String getRequestId() {
        int randomInt = (int) (Math.random() * 100);
        Log.d(LOG_TAG, "random int = " + randomInt);
        return System.currentTimeMillis() + randomInt + "_id";
    }

    /**
     * 保存自己再rtc channel 中的uid
     *
     * @param memberInfos
     * @param selfAccid
     */
    private void storeUid(ArrayList<MemberInfo> memberInfos, String selfAccid) {
        for (MemberInfo member : memberInfos) {
            if (TextUtils.equals(member.getAccountId(), selfAccid)) {
                selfRtcUid = member.getUid();
            }
        }
    }

    /**
     * 加入视频通话频道
     *
     * @param token
     * @param uid
     * @param channelName
     * @return 0 方法调用成功，其他失败
     */
    private int joinChannel(String token, String channelName, long uid) {
        return NERtcEx.getInstance().joinChannel(token, channelName, uid);
    }


    @Override
    public void accept(String fromId) {
        final long selfUid = service.getUserId();
        joinChannel(token, channelName, selfUid);
        buriedPoint("joinChannel", "acceptCall", token, "");
    }

    @Override
    public void setStatsObserver() {
        NERtcEx.getInstance().setStatsObserver(statsObserver);
    }

    @Override
    public void setCallState(int callStatus) {
        status = callStatus;
    }

    @Override
    public int getCallState() {
        return status;
    }

    @Override
    public boolean isReceive() {
        return isReceive;
    }

    @Override
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void reject(InviteParamBuilder inviteParam, RequestCallback<Void> callback) {
        reject(inviteParam, true, null);
        status = STATE_IDLE;
        isReceive = false;
    }


    /**
     * 拒绝
     */
    private void reject(InviteParamBuilder inviteParam, boolean byUser, RequestCallback<Void> callback) {
        inviteParam.offlineEnabled(true);
        NIMClient.getService(SignallingService.class).rejectInvite(inviteParam).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (byUser) {
                    status = STATE_IDLE;
                }
                if (callback != null) {
                    callback.onSuccess(aVoid);
                }
            }

            @Override
            public void onFailed(int i) {
                if (byUser && i != ResponseCode.RES_INVITE_HAS_ACCEPT) {//已经接受
                    status = STATE_IDLE;
                }
                if (callback != null) {
                    callback.onFailed(i);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                if (callback != null) {
                    callback.onException(throwable);
                }
                if (byUser) {
                    status = STATE_IDLE;
                }
            }
        });
    }


    @Override
    public void hangup() {
        //离开NERtc的channel
        if (haveJoinNertcChannel) {
            NERtc.getInstance().leaveChannel();
            LogUtil.e("NERtcCallback", "--leaveChannel--");
        }
        //离开信令的channel
        if (!TextUtils.isEmpty(imChannelId)) {
            leaveAndCloseIMChannel(imChannelId);
        }
        haveJoinNertcChannel = false;
        invitedParam = null;
        status = STATE_IDLE;
        isReceive = false;
    }

    @Override
    public void cancel(RequestCallback<Void> callback) {
        if (invitedParam != null) {
            NIMClient.getService(SignallingService.class).cancelInvite(invitedParam);
            hangup();
        }
    }

    /**
     * 收到邀请时设置callType
     *
     * @param invitedEvent
     */
    private void setCallType(InvitedEvent invitedEvent) {
        try {
            CustomInfo customInfo = GsonUtils.fromJson(invitedEvent.getCustomInfo(), CustomInfo.class);
            callType = customInfo.callType;
        } catch (Exception e) {
            callType = Utils.ONE_TO_ONE_CALL;
        }
    }

    private void leaveAndCloseIMChannel(String channelId) {
        NIMClient.getService(SignallingService.class).leave(channelId, false, null)
                .setCallback(new RequestCallbackWrapper<Void>() {

                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            imChannelId = null;
                        }
                    }
                });

        NIMClient.getService(SignallingService.class).close(channelId, false, null)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            imChannelId = null;
                        }
                    }
                });

    }

    @Override
    public void leave(RequestCallback<Void> callback) {
        //群呼如果未接通走取消逻辑
        if (status != STATE_DIALOG) {
            delegateManager.onError(CallErrorCode.STATUS_ERROR, "leave status error,status = " + status, "");
        }
        if (callType == Utils.GROUP_CALL && status == STATE_CALL_OUT) {
            cancel(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    hangup();
                }

                @Override
                public void onFailed(int i) {
                    hangup();
                }

                @Override
                public void onException(Throwable throwable) {
                    hangup();
                }
            });
        } else {
            hangup();
        }
    }


    @Override
    public void enableCamera(boolean enable) {
        NERtcEx.getInstance().enableLocalVideo(enable);
    }

    @Override
    public void switchCamera() {
        NERtcEx.getInstance().switchCamera();
    }

    @Override
    public void setMicMute(boolean isMute) {
        NERtcEx.getInstance().muteLocalAudioStream(isMute);
    }


    @Override
    public void setSpeakerphoneOn(boolean isOpen) {
        NERtcEx.getInstance().setSpeakerphoneOn(isOpen);
    }

    @Override
    public void setAudioMute(boolean mute, long userId) {
        NERtcEx.getInstance().subscribeRemoteAudioStream(userId, !mute);
    }

    /**
     * 关闭IMChannel
     *
     * @param channelId
     */
    private void closeIMChannel(String channelId, RequestCallback<Void> callback) {
        Log.d(LOG_TAG, "closeIMChannel ");
        NIMClient.getService(SignallingService.class).close(channelId, false, null)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            Log.d(LOG_TAG, "closeIMChannel success channelId = " + channelId);
                            imChannelId = null;
                            if (callback != null) {
                                callback.onSuccess(result);
                            }
                        } else {
                            Log.d(LOG_TAG, "closeIMChannel failed code = " + code + "channelId" + channelId);
                            if (callback != null) {
                                callback.onFailed(code);
                            }
                        }
                        status = STATE_IDLE;
                    }
                });
    }

    /**
     * 统计电话进入
     */
    public void buriedPoint(String scene, String event, String index, String extend) {
        mineService.trackEvent(scene, event, index, extend, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
            }
        });
    }


    public void destroy() {
        NIMClient.getService(SignallingServiceObserver.class).observeOnlineNotification(nimOnlineObserver, false);
//        NIMClient.getService(SignallingServiceObserver.class).observeOfflineNotification(nimOfflineObserver, false);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        status = STATE_IDLE;
        isinitNERtc = false;
        try {
            NERtcEx.getInstance().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        isinitNERtc = false;
        if (engine != null) {
            try {
                engine.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void leaveRoom(boolean sendTip) {
        initial = false;
        this.sendTip = sendTip;
        delayHandler.removeMessages(MSG_MEMBER_EXIT);
        int resultCode = engine.leaveChannel();
        LogUtil.e("====>", "level channel code is " + resultCode);
        if (resultCode == 0 && roomCallback != null && mode == VOICE_ROOM && sendTip && partyLiveType == 0)
            roomCallback.sendLeaveSeat();
//        if (anchorMode && voiceRoomSeat != null)
//            NIMClient.getService(MsgService.class).sendCustomNotification(SeatCommands.onSeat(voiceRoomInfo, user, voiceRoomSeat));
    }

    /**
     * 音视频引擎回调
     */
    private final NERtcCallbackExImpl callback = new NERtcCallbackExImpl() {
        @Override
        public void onJoinChannel(int result, long channelId, long elapsed, long l) {
            if (mode == CALL) {
                mChannelId = channelId;
                haveJoinNertcChannel = true;
                service.setChannelId(channelId);
                if (delegateManager != null) {
                    delegateManager.onJoinChannel(mChannelId);
                }
                buriedPoint("onJoinChannel", "callStatus", mChannelId + "", "");
            } else if (mode == VOICE_ROOM) {
                if (anchorMode && voiceRoomInfo.isSupportCDN()) {
                    getStreamTaskControl().addStreamTask(accountToVoiceUid(user.account), voiceRoomInfo.push_url);
                }
                //拉流情况 rtc 情况上麦发送
                if (result == 0 && roomCallback != null) {
                    roomCallback.sendOnSeat();
                }
                onEnterRoom(result == NERtcConstants.ErrorCode.OK || result == NERtcConstants.ErrorCode.ENGINE_ERROR_ROOM_ALREADY_JOINED);
                //设置之前保存的采集音量
                engine.adjustRecordingSignalVolume(audioCaptureVolume);
            }
        }


        /**
         * CDN 模式下添加对应用户的混流设置
         * @param uid 用户id
         */
        @Override
        public void onUserJoined(long uid) {
            if (mode == CALL) {
                if (service.getUserId() != uid) {
                    status = STATE_DIALOG;
                    isReceive = false;
                }
                if (delegateManager != null) {
                    delegateManager.onUserEnter(uid);
                }
            } else if (mode == VOICE_ROOM) {
                if (anchorMode) {
                    getStreamTaskControl().addMixStreamUser(uid);
                }
            }

        }

        /**
         * CDN 模式下 移除对应用户的混流设置
         * @param uid 用户id
         * @param reason 该用户离开原因{@link com.netease.lava.nertc.sdk.NERtcConstants.ErrorCode}
         */
        @Override
        public void onUserLeave(long uid, int reason) {
            if (mode == CALL) {
                status = STATE_IDLE;
                isReceive = false;
                LogUtil.e("NERtcCallback", "onUserLeave -- 用户离开了" + uid + reason);
                if (delegateManager != null) {
                    delegateManager.onUserHangup(uid);
                }
            } else if (mode == VOICE_ROOM) {
                if (anchorMode) {
                    getStreamTaskControl().removeMixStreamUser(uid);
                }
            }
        }

        @Override
        public void onLeaveChannel(int result) {
            if (mode == CALL) {
                status = STATE_IDLE;
                isReceive = false;
                haveJoinNertcChannel = false;
            } else if (mode == VOICE_ROOM) {
                if (roomCallback != null && result == 0)
                    roomCallback.onLeaveRoom();
            }
        }

        /**
         * 通知混音状态
         * 0 播放完成
         * 1 播放出错
         */
        @Override
        public void onAudioMixingStateChanged(int reason) {
            if (mode == VOICE_ROOM) {
                if (audioPlay != null) {
                    audioPlay.onAudioMixingStateChanged(reason);
                }
            }

        }

        @Override
        public void onAudioMixingTimestampUpdate(long timestampMs) {
            if (mode == VOICE_ROOM) {
                String seiString = GsonUtils.toJson(new SEI(timestampMs));
                engine.sendSEIMsg(seiString);
            }

        }

        @Override
        public void onRecvSEIMsg(long userID, String seiMsg) {

        }

        /**
         * 通知音效播放完成
         */
        @Override
        public void onAudioEffectFinished(int effectId) {
            if (mode == VOICE_ROOM) {
                if (audioPlay != null) {
                    audioPlay.onAudioEffectFinished(effectId);
                }
            }
        }

        @Override
        public void onUserAudioStart(long l) {
            if (mode == CALL) {
                LogUtil.e("NERtcCallback", "--onUserAudioStart--");
                if (service.getUserId() != l) {
                    NERtcEx.getInstance().subscribeRemoteAudioStream(l, true);
                }
            }
        }


        @Override
        public void onUserVideoStart(long uid, int maxProfile) {
            super.onUserVideoStart(uid, maxProfile);
            if (mode == CALL) {
                LogUtil.e("NERtcCallback", uid + "---onUserVideoStart-" + service.getUserId());
                if (service.getUserId() != uid) {
                    NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
                }
                if (delegateManager != null) {
                    delegateManager.onCameraAvailable(uid, true);
                }
            } else if (mode == VOICE_ROOM) {
                engine.subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
            }

        }

        @Override
        public void onUserAudioStop(long l) {
            LogUtil.e("NERtcCallback", "--onUserAudioStop--");
        }


        @Override
        public void onUserVideoStop(long l) {
            if (mode == CALL) {
                LogUtil.e("NERtcCallback", l + "---onUserVideoStop-" + service.getUserId());
                if (delegateManager != null) {
                    delegateManager.onCameraAvailable(l, false);
                }
            }

        }


        @Override
        public void onLocalAudioVolumeIndication(int volume) {
            super.onLocalAudioVolumeIndication(volume);
            if (roomCallback != null && mode == VOICE_ROOM)
                roomCallback.onSelfSeatVolume(volume);
        }

        /**
         * 通知房间内用户语音音量，可以知道，“谁”正在说话
         */
        @Override
        public void onRemoteAudioVolumeIndication(NERtcAudioVolumeInfo[] volumeArray, int totalVolume) {

            if (mode == VOICE_ROOM) {
                Map<Long, Integer> volumes = new HashMap<>();
                for (NERtcAudioVolumeInfo volumeInfo : volumeArray) {
                    volumes.put(volumeInfo.uid, volumeInfo.volume);
                }
                updateVolumes(volumes);
            }
        }


        @Override
        public void onDisconnect(int reason) {
            if (mode == CALL) {
                status = STATE_IDLE;
                isReceive = false;
                if (delegateManager != null) {
                    delegateManager.onError(reason);
                }
            } else if (mode == VOICE_ROOM) {
                LogUtil.e("NERtcVoiceRoomImpl", "disconnected from RTC room.  reason is " + reason);
                leaveRoom(true);
//                onLeaveRoom();
            }

        }
    };

    private static final int MSG_MEMBER_EXIT = 500;

    private static final Handler delayHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            removeMessages(msg.what);
            if (msg.obj instanceof Runnable) {
                ((Runnable) msg.obj).run();
            }
        }
    };

    public final AnchorImpl anchor = new AnchorImpl(this);

    public final AudienceImpl audience = new AudienceImpl(this);

    private AudioPlayImpl audioPlay;

    private StreamTaskControl streamTaskControl;

    private PushTypeSwitcher switcher;


    @Override
    public void setSeatList(List<VoiceRoomSeat> seatList) {
        seats.clear();
        seats.addAll(seatList);
    }

    @Override
    public void enterRoom(final boolean anchorMode, VoiceRoomSeat voiceRoomSeat) {
        this.voiceRoomSeat = voiceRoomSeat;
        this.anchorMode = anchorMode;
        if (!anchorMode) {
            audience.enterRoom(voiceRoomInfo, user);
        } else {
            anchor.enterRoom();
//            NIMClient.getService(MsgService.class).sendCustomNotification(SeatCommands.onSeat(voiceRoomInfo, user, voiceRoomSeat));
        }
        if (anchorMode || partyLiveType == 1) {  // //rtc //0-rtc 1-cdn 2-rtc+cdn
            joinChannel();
        } else {
            audience.getAudiencePlay().play(voiceRoomInfo.rtmp_pull_url);
            onEnterRoom(true);
        }
        initSeats();
        initAnchorInfo();
    }


    @Override
    public void startLocalAudio() {
//        engine.enableLocalAudio(true);
    }

    @Override
    public void stopLocalAudio() {
//        engine.enableLocalAudio(false);
    }

    @Override
    public boolean muteLocalAudio(boolean mute) {
        engine.muteLocalAudioStream(mute);
//        boolean muted = isLocalAudioMute();

        if (anchorMode) {
            anchor.muteLocalAudio(mute);
        } else {
            audience.muteLocalAudio(mute);
        }

        if (roomCallback != null) {
            roomCallback.onMute(mute);
        }

        return mute;
    }

    @Override
    public boolean isLocalAudioMute() {
        return engine.isRecordDeviceMute();
    }

    @Override
    public void setSpeaker(boolean useSpeaker) {
        int code = engine.setSpeakerphoneOn(useSpeaker);
    }

    @Override
    public void setAudioCaptureVolume(int volume) {
        audioCaptureVolume = volume;
        engine.adjustRecordingSignalVolume(volume);
    }

    @Override
    public int getAudioCaptureVolume() {
        return audioCaptureVolume;
    }


    @Override
    public boolean muteRoomAudio(boolean mute) {
        muteRoomAudio = mute;
        engine.setPlayoutDeviceMute(mute);
        if (anchorMode) {
            anchor.muteRoomAudio(mute);
        } else {
            if (audience.getAudiencePlay() != null)
                audience.getAudiencePlay().setVolume(mute ? 0f : 1f);
        }
        return mute;
    }

    @Override
    public boolean isRoomAudioMute() {
        return muteRoomAudio;
    }

    @Override
    public boolean isEarBackEnable() {
        return enableEarBack;
    }

    @Override
    public void enableEarback(boolean enable) {
        this.enableEarBack = enable;
        engine.enableEarback(enable, 100);
    }

    @Override
    public void sendTextMessage(String text) {
        sendMessage(text, false);
    }

    @Override
    public AudioPlay getAudioPlay() {
        if (audioPlay == null) {
            audioPlay = new AudioPlayImpl(engine);
        }
        return audioPlay;
    }

    @Override
    public PushTypeSwitcher getPushTypeSwitcher() {
        if (switcher == null) {
            switcher = new PushTypeSwitcherImpl(BaseApplication.getInstance(), engine, audience.getAudiencePlay());
        }
        return switcher;
    }

    public StreamTaskControl getStreamTaskControl() {
        if (streamTaskControl == null) {
            streamTaskControl = new StreamTaskControlImpl(anchor, engine);
        }
        return streamTaskControl;
    }

    @Override
    public Anchor getAnchor() {
        return anchor;
    }

    @Override
    public Audience getAudience() {
        return audience;
    }

    @Override
    public void updateSeat(VoiceRoomSeat seat) {
//        this.seats.set(seat.getIndex() - 1, seat);
        if (roomCallback != null) {
            roomCallback.updateSeat(seat);
        }
    }

    @Override
    public synchronized VoiceRoomSeat getSeat(int index) {
        return seats.get(index);
    }

    @Override
    public void sendSeatEvent(VoiceRoomSeat seat, boolean enter) {
        sendMessage(getMessageTextBuilder().seatEvent(seat, enter), true);
    }

    @Override
    public void sendSeatUpdate(VoiceRoomSeat seat, RequestCallback<Void> callback) {

    }

    @Override
    public void fetchSeats(final RequestCallback<List<VoiceRoomSeat>> callback) {
        chatRoomService.fetchQueue(voiceRoomInfo.getRoom_id()).setCallback(new RequestCallback<List<Entry<String, String>>>() {
            @Override
            public void onSuccess(List<Entry<String, String>> param) {
                if (callback != null) {
                    List<VoiceRoomSeat> seats = createSeats();
                    if (param != null) {
                        fillSeats(param, seats);
                    }
                    callback.onSuccess(seats);
                }
            }

            @Override
            public void onFailed(int code) {
                if (callback != null) {
                    callback.onFailed(code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                if (callback != null) {
                    callback.onException(exception);
                }
            }
        });
    }


    private void onEnterRoom(boolean success) {
        if (success) {
            // 打开扬声器
            setSpeaker(true);
            // 打开耳返
//            enableEarback(false);
            // 设置音量汇报间隔 500ms
            engine.enableAudioVolumeIndication(true, 1000);
        }
        if (roomCallback != null) {
            roomCallback.onEnterRoom(success);
        }
    }

    public void onLeaveRoom() {
        if (roomCallback != null) {
            roomCallback.onLeaveRoom();
        }
    }

    /**
     * 恢复单例中非 长期有效对象内容为默认
     */
    private void restoreInstanceInfo() {
        muteRoomAudio = false;
        user = null;
        voiceRoomInfo = null;
        anchorMode = false;
        audioCaptureVolume = 100;
    }


    private void joinChannel() {
        NERtcEx.getInstance().adjustRecordingSignalVolume(200);
//        setAudioQuality(voiceRoomInfo.getAudioQuality());
//        setupParameters();
        if (anchorMode) {
            startLocalAudio();
        } else {
            stopLocalAudio();
        }
        int result = engine.joinChannel(voiceRoomInfo.getRoom_token(), voiceRoomInfo.getRoom_name(), accountToVoiceUid(user.account));
        LogUtil.e("====>", engine + "join channel code is " + result);
        if (result != 0) {
            if (roomCallback != null) {
                roomCallback.onEnterRoom(false);
            }
        }
    }

    private void setupParameters() {
        NERtcParameters parameters = new NERtcParameters();
        parameters.setBoolean(NERtcParameters.KEY_AUTO_SUBSCRIBE_AUDIO, true);
        parameters.set(NERtcParameters.KEY_PUBLISH_SELF_STREAM, true);
        NERtcEx.getInstance().setParameters(parameters);
    }

    private void updateRoomInfo() {
    }

    private void initAnchorInfo() {
        if (anchorMode) {
            initAnchorInfo(user);
            return;
        }
//        roomQuery.fetchMember(voiceRoomInfo.getCreatorAccount(), new SuccessCallback<ChatRoomMember>() {
//            @Override
//            public void onSuccess(ChatRoomMember chatRoomMember) {
//                if (chatRoomMember != null) {
//                    initAnchorInfo(new VoiceRoomUser(chatRoomMember));
//                }
//            }
//        });
    }


    private void initAnchorInfo(VoiceRoomUser user) {
        if (roomCallback != null) {
            roomCallback.onAnchorInfo(user);
        }
    }

    public void initSeats() {
        if (anchorMode) {

//            return;
        }
//        updateSeats(createSeats());
//        fetchSeats(new SuccessCallback<List<VoiceRoomSeat>>() {
//            @Override
//            public void onSuccess(List<VoiceRoomSeat> seats) {
//                if (!anchorMode) {
//                    audience.initSeats(seats);
//                } else {
//                    anchor.initSeats(seats);
//                }
//                updateSeats(seats);
//            }
//        });
    }

    @Override
    public void refreshSeats() {
        initSeats();
    }

    @Override
    public boolean isInitial() {
        return initial;
    }

    private void onNotification(final ChatRoomNotificationAttachment notification) {
        switch (notification.getType()) {
            case ChatRoomQueueChange: {
                ChatRoomQueueChangeAttachment queueChange = (ChatRoomQueueChangeAttachment) notification;
                onQueueChange(queueChange);
                break;
            }
            case ChatRoomMemberIn: {
                delayHandler.removeMessages(MSG_MEMBER_EXIT);
                updateRoomInfo();
                sendRoomEvent(notification.getTargetNicks(), true);
                break;
            }
            case ChatRoomMemberExit: {
                delayHandler.sendMessageDelayed(delayHandler.obtainMessage(MSG_MEMBER_EXIT, new Runnable() {
                    @Override
                    public void run() {
                        updateRoomInfo();

                        if (anchorMode) {
                            anchor.memberExit(notification.getOperator());
                        }
                        sendRoomEvent(notification.getTargetNicks(), false);
                    }
                }), 500);
                break;
            }
            case ChatRoomRoomMuted: {
                if (!anchorMode) {
                    audience.muteText(true);
                }
                break;
            }
            case ChatRoomRoomDeMuted: {
                if (!anchorMode) {
                    roomQuery.fetchMember(user.account, new SuccessCallback<ChatRoomMember>() {
                        @Override
                        public void onSuccess(ChatRoomMember member) {
                            if (member != null) {
                                audience.updateMemberInfo(member);
                            }
                        }
                    });
                }
                break;
            }
            case ChatRoomMemberTempMuteAdd: {
                ChatRoomTempMuteAddAttachment muteAdd = (ChatRoomTempMuteAddAttachment) notification;
                if (!anchorMode) {
                    if (muteAdd.getTargets().contains(user.account)) {
                        audience.muteText(true);
                    }
                }
                break;
            }
            case ChatRoomMemberTempMuteRemove: {
                ChatRoomTempMuteRemoveAttachment muteRemove = (ChatRoomTempMuteRemoveAttachment) notification;
                if (!anchorMode) {
                    if (muteRemove.getTargets().contains(user.account)) {
                        audience.muteText(false);
                    }
                }
                break;
            }
            case ChatRoomInfoUpdated: {
                Boolean mute = isAnchorMute(notification);
                if (mute != null) {
                    if (roomCallback != null) {
                        roomCallback.onAnchorMute(mute);
                    }
                }
                break;
            }
        }
    }


    private void onQueueChange(ChatRoomQueueChangeAttachment queueChange) {
        LogUtil.i(LOG_TAG, "onQueueChange: type = " + queueChange.getChatRoomQueueChangeType() +
                " key = " + queueChange.getKey() + " content = " + queueChange.getContent());
        ChatRoomQueueChangeType type = queueChange.getChatRoomQueueChangeType();
        if (type == ChatRoomQueueChangeType.DROP) {
            if (anchorMode) {
                anchor.clearSeats();
            } else {
                audience.clearSeats();
            }
            updateSeats(createSeats());
            return;
        }

        if (type == ChatRoomQueueChangeType.OFFER || type == ChatRoomQueueChangeType.POLL) {
            String content = queueChange.getContent();
            String key = queueChange.getKey();
            if (TextUtils.isEmpty(content)) {
                return;
            }
            if (MusicSingImpl.isMusicKey(key)) {

            } else if (type == ChatRoomQueueChangeType.OFFER) {
                VoiceRoomSeat seat = VoiceRoomSeat.fromJson(content);
                if (seat == null) {
                    return;
                }
                VoiceRoomSeat currentSeat = getSeat(seat.getIndex());
                if (currentSeat != null && currentSeat.isOn() && seat.getStatus() == VoiceRoomSeat.Status.INIT && seat.getReason() == VoiceRoomSeat.Reason.CANCEL_APPLY) {
                    if (!anchorMode) {
                        audience.initSeats(seats);
                    }
                    return;
                }
                if (anchorMode) {
                    if (anchor.seatChange(seat)) {
                        updateSeat(seat);
                    }
                } else {
                    updateSeat(seat);
                    audience.seatChange(seat);
                }
            }
        }
    }

    private void sendRoomEvent(List<String> nicks, boolean enter) {
        if (nicks == null || nicks.isEmpty()) {
            return;
        }
        for (String nick : nicks) {
            VoiceRoomMessage message = VoiceRoomMessage.createEventMessage(
                    getMessageTextBuilder().roomEvent(nick, enter));
            if (roomCallback != null) {
                roomCallback.onVoiceRoomMessage(message);
            }
        }
    }

    private void updateSeats(@NonNull List<VoiceRoomSeat> seats) {
        this.seats.clear();
        this.seats.addAll(seats);
        if (roomCallback != null) {
            roomCallback.updateSeats(this.seats);
        }
    }

    private void updateVolumes(Map<Long, Integer> volumes) {
        if (roomCallback != null) {
            boolean enable = !isLocalAudioMute() && !isRoomAudioMute();
            if (microphoneHost != null && service.getUserId() != microphoneHost.getUser_id())
                roomCallback.onAnchorVolume(getVolume(volumes, microphoneHost != null ? String.valueOf(microphoneHost.getUser_id()) : "0"));
            for (VoiceRoomSeat seat : seats) {
                roomCallback.onSeatVolume(seat, getVolume(volumes, String.valueOf(seat.getUser_id())));
            }
        }
    }

    private void sendMessage(String text, boolean event) {
        if (voiceRoomInfo == null) {
            return;
        }
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                voiceRoomInfo.getRoom_name(),
                text);
        if (event) {
            Map<String, Object> remoteExtension = new HashMap<>();
            remoteExtension.put(ChatRoomMsgExtKey.KEY_TYPE, ChatRoomMsgExtKey.TYPE_EVENT);
            message.setRemoteExtension(remoteExtension);
        }
        if (roomCallback != null) {
            VoiceRoomMessage msg = event
                    ? VoiceRoomMessage.createEventMessage(text)
                    : VoiceRoomMessage.createTextMessage(user.icon, text);
            roomCallback.onVoiceRoomMessage(msg);
        }
    }

    private static int getVolume(Map<Long, Integer> volumes, String account) {
        long uid = TextUtils.isEmpty(account) ? 0 : Long.parseLong(account);
        if (uid <= 0) {
            return 0;
        }
        Integer volume = volumes.get(uid);
        return volume != null ? volume : 0;
    }

    public static Boolean isAnchorMute(ChatRoomInfo chatRoomInfo) {
        Map<String, Object> extension = chatRoomInfo.getExtension();
        Object value = extension != null ? extension.get(ChatRoomInfoExtKey.ANCHOR_MUTE) : null;
        return value instanceof Integer ? (Integer) value == 1 : null;
    }

    public static Boolean isAnchorMute(ChatRoomNotificationAttachment attachment) {
        Map<String, Object> extension = attachment.getExtension();
        Object value = extension != null ? extension.get(ChatRoomInfoExtKey.ANCHOR_MUTE) : null;
        return value instanceof Integer ? (Integer) value == 1 : null;
    }

    public static boolean isEventMessage(ChatRoomMessage message) {
        if (message.getMsgType() != MsgTypeEnum.text) {
            return false;
        }
        Map<String, Object> remoteExtension = message.getRemoteExtension();
        Object value = remoteExtension != null ? remoteExtension.get(ChatRoomMsgExtKey.KEY_TYPE) : null;
        return value instanceof Integer && (Integer) value == ChatRoomMsgExtKey.TYPE_EVENT;
    }


    private static List<VoiceRoomSeat> createSeats() {
        int size = VoiceRoomSeat.SEAT_COUNT;
        List<VoiceRoomSeat> seats = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            seats.add(new VoiceRoomSeat(i));
        }
        return seats;
    }

    private static void fillSeats(@NonNull List<Entry<String, String>> entries,
                                  @NonNull List<VoiceRoomSeat> seats) {
        for (Entry<String, String> entry : entries) {
            if (!VoiceRoomSeat.isValidKey(entry.key)) {
                continue;
            }
            if (TextUtils.isEmpty(entry.value)) {
                continue;
            }
            VoiceRoomSeat seat = VoiceRoomSeat.fromJson(entry.value);
            if (seat == null) {
                continue;
            }
            int index = seat.getIndex();
            if (index >= 0 && index < seats.size()) {
                seats.set(seat.getIndex(), seat);
            }
        }
    }

    private static NERtcVoiceRoomDef.AccountMapper accountMapper;

    private static long accountToVoiceUid(String account) {
        return accountMapper != null ? accountMapper.accountToVoiceUid(account) : -1;
    }

    public static void setAccountMapper(NERtcVoiceRoomDef.AccountMapper mapper) {
        accountMapper = mapper;
    }

    private static VoiceRoomMessage.MessageTextBuilder messageTextBuilder;

    public static VoiceRoomMessage.MessageTextBuilder getMessageTextBuilder() {
        if (messageTextBuilder != null) {
            return messageTextBuilder;
        }
        return VoiceRoomMessage.getDefaultMessageTextBuilder();
    }

    public static void setMessageTextBuilder(VoiceRoomMessage.MessageTextBuilder messageTextBuilder) {
        NERTCVideoCallImpl.messageTextBuilder = messageTextBuilder;
    }

    /**
     * 状态参数标记是否有效
     */
    private static class StateParam {
        /**
         * 参数有效
         */
        private static final int STATE_VALID = 1;
        /**
         * 参数无效
         */
        private static final int STATE_INVALID = -1;
        /**
         * 参数未初始化
         */
        private static final int STATE_INIT = 0;

        String param;
        int state;

        public StateParam() {
            this.param = null;
            this.state = STATE_INIT;
        }

        public void updateParam(String param) {
            this.param = param;
            this.state = STATE_VALID;
        }

        public void reset() {
            this.param = null;
            this.state = STATE_INIT;
        }

        public void error() {
            this.param = null;
            this.state = STATE_INVALID;
        }

        /**
         * 是否处于未初始化状态
         *
         * @return true 未初始化，false 已经完成初始化
         */
        public boolean isInit() {
            return this.state == STATE_INIT;
        }

        public boolean isValid() {
            return this.state == STATE_VALID;
        }

        public boolean isInvalid() {
            return this.state == STATE_INVALID;
        }
    }

}
