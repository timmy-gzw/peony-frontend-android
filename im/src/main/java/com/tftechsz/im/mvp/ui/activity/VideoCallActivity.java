package com.tftechsz.im.mvp.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.faceunity.nama.FURenderer;
import com.faceunity.nama.IFURenderer;
import com.faceunity.nama.ui.FaceUnityView;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.lava.api.model.RTCVideoCropMode;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.video.NERtcVideoConfig;
import com.netease.lava.nertc.sdk.video.NERtcVideoFrame;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.opensource.svgaplayer.SVGAImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.qgame.animplayer.AnimView;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.custom.CameraUtils;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.nim.model.JoinChannelCallBack;
import com.tftechsz.common.nim.model.NERTCCallingDelegate;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.utils.face.LifeCycleSensorManager;
import com.tftechsz.common.widget.gift.GiftRootLayout;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.CallMessageAdapter;
import com.tftechsz.im.model.CallStatusInfo;
import com.tftechsz.im.model.dto.CallMessageDto;
import com.tftechsz.im.mvp.iview.ICallView;
import com.tftechsz.im.mvp.presenter.CallPresenter;
import com.tftechsz.im.service.FloatVideoWindowService;
import com.tftechsz.im.widget.pop.CallHangUpPopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Route(path = ARouterApi.ACTIVITY_VIDEO_CALL)
public class VideoCallActivity extends BaseMvpActivity<ICallView, CallPresenter> implements ICallView, LifeCycleSensorManager.OnAccelerometerChangedListener, View.OnClickListener {

    private static final String TAG = "VideoCallActivity";
    private static final String INVENT_EVENT = "call_in_event";
    private static final String CALL_OUT_USER = "call_out_user";
    private static final String CALL_DIR = "call_dir";
    private static final String CALL_TYPE = "call_type";
    private static final String CALL_IS_MATCH = "call_is_match";
    private static final String CALL_ID = "call_id";
    private static final String CALL_MATCH_TYPE = "call_match_type";
    private static final String CALL_USER_IS_ON_LINE = "call_user_is_on_line";
    private static final String CALL_FROM_ID = "from_id";

    private View viewVideoView, viewRemote;
    private NERTCVideoCall nertcVideoCall;
    private NERtcVideoView localVideoView, videoView, remoteVideoView;
    private TextView tvSwitch;  //切换摄像头
    private HeadImageView ivUserIcon,ivAvater;
    private TextView tvCallUser, tvCallComment,tvName;
    private LinearLayout mLlIncome;
    private TextView tvSpeaker, tvMute;
    private TextView tvVideoSpeaker, tvVideoMute;
    private TextView mTvCancel;
    private LinearLayout llyBingCall;
    private LinearLayout llyDialogOperation;
    private TextView tvAccept;
    private TextView tvReject;
    private TextView tvFaceUnity;   //美颜设置
    private RelativeLayout rlyTopUserInfo;
    private LinearLayout llUserInfo;
    public static boolean ISBOUND = false;
    public static long startTime = 0;
    public static long endTime = 0;
    public static long seconds = 0;
    private Chronometer audioTime;
    private Chronometer videoTime;
    private UserInfo callOutUser;//呼出用户
    private InvitedEvent invitedEvent;//来电事件
    private int mCallDir;//0 call out 1 call in
    private int mChannelType;  // 1 语音， 2 视频
    private boolean isMatch;  //是否自动接听
    private String matchType;  //是否强制匹配
    private boolean isOpen, isMute = false;//是否开启扬声器
    private MediaPlayer mediaPlayer;
    private boolean mIsAccept = false;   //是否接听语音
    private FrameLayout mFlVoiceBg;  //语音背景
    private ImageView mIvVoiceAvatar;
    private int mNum = 0;  //失败请求次数
    private long videoUid;
    private boolean isChangeVideo;  //切换视频头像
    private UserProviderService service;
    private boolean isSelfEnd = false; //是否自己挂断
    private String fromId;  //哪个用户打入
    private String callId;
    private int closeType = 2;   //呼叫超时状态
    private static final int DELAY_TIME = 1000;//延时
    private ImageView ivGift;   //礼物
    private SVGAImageView svgaImageView;
    private AnimView mPlayerView;
    private GiftRootLayout.GiftRootListener giftViewListener;


    private TextView tvNetwork;
    private boolean isOpenFace;  //接收到对方是否露脸

    private ConstraintLayout mClRecharge;  //余额不足去弹窗
    private TextView tvRecharge;

    private AudioManager mAudioManager;
    //聊天消息
    private CallMessageAdapter mAdapter;
    private List<CallMessageDto> callMessage;

    protected GiftRootLayout giftRoot;
    private boolean isFaceOn;  //切换是否露脸
    //视频通话相关
    private LinearLayout llCalVideo;
    private LinearLayout clVideoCall;
    private LinearLayout llVideoTime; //语音通话时长
    private TextView tvFaceSwitch;  //不露脸视频切换
    private ImageView ivCloseFace, ivSmallCloseFace;  //开启了不露脸图标
    private TextView tvCallFaceUnity;
    private TextView tvVideoFace;

    //语音通话相关
    private ConstraintLayout clVoiceCall;
    private LinearLayout llAudioTime;   //语音通话时长

    public int isOnLine;   // 0  否 1 是
    //chat
    private RecyclerView rvCallChat;


    //相芯美颜相关
    protected FURenderer mFURenderer;
    private FaceUnityView faceUnityView;
    private Handler mHandler;
    private int mSkipFrame = 5;
    private boolean isFirstInit = true;
    private boolean mIsSendMessage = false;  //是否发送了消息
    private boolean isEnter;  //判断是否执行了进入实时聊天，断网重新联网问题
    private String sessionId = "";
    private boolean isAccept = false;
    private long mChannelId;
    private long peerUid;

    private CountDownTimer timer;//接通倒计时
    private boolean mIsSpeak;  //是否正在通话

    private boolean isShowFace = true;  //是否显示面具
    private int callTotalTime;
    private ChatMsgUtil.OnMessageListener messageListener;
    private NERTCCallingDelegate nertcCallingDelegate;

    private CallHangUpPopWindow callHangUpPopWindow;

    //违规相关处理
    private TextView mTvViolation, mTvViolationTip, mTvIncome;
    private Boolean mIsWarm = false, mIsMeWarm = false;  //是否违规,是否自己违规
    private CountBackUtils countBackUtils;

    //new
    private ImageView mIvSmallVoice;
    private TextView mTvReportUser;
    private TextView tvCallTip;
    private ConstraintLayout mClVideo;
    private TextView mtvGenderAge,mtvCity,mtvConstellation,mtvJob;//语音通话：对方性别年龄，城市，星座，工作
    private TextView mtvVideoGenderAge,mtvVideoCity,mtvVideoConstellation,mtvVideoJob;//视屏通话：对方性别年龄，城市，星座，工作

    private void initPhoneStateListener() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        LogUtil.e(TAG, "-----CALL_STATE_RINGING----");
                        break;
                }
            }
        };
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    private void initCallingDelegate() {
        nertcCallingDelegate = new NERTCCallingDelegate() {
            @Override
            public void onError(int errorCode, String userId, String errorMsg) {
                int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                if (!mIsSendMessage) {
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_NOT_ACCEPT, String.valueOf(service.getUserId()), String.valueOf(userId), service.getChannelId(), callId, time, 1, 2, isMatch, messageListener);
                    mIsSendMessage = true;
                }
                isShowFace = false;
                toastTip(errorMsg);
                stopPlayer();
                finish();
            }

            @Override
            public void onInvitedByUser(InvitedEvent invitedEvent, String callId) {

            }

            @Override
            public void onInvitedByUser(String fromId, String callId) {

            }


            @Override
            public void onUserEnter(long userId) {
                LogUtil.e(TAG, userId + "用户进入了");
                peerUid = userId;
                mIsSpeak = true;
                getP().sendNotification(String.valueOf(userId), 2);
                if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_call_after_join_room == 1) {
                    return;
                }
                userEnter();
            }

            @Override
            public void onJoinChannel(long channelId) {
                mChannelId = channelId;
                if (mCallDir == 1 && !isAccept) {
                    isAccept = true;
                    getP().buriedPoint(channelId + "", "joinChannel", callId, "");
                    if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_call_after_join_room == 1) {
                        return;
                    }
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_ACCEPT, String.valueOf(service.getUserId()), sessionId, mChannelId, callId, 0, 1, 2, isMatch, messageListener);
                }
            }

            @Override
            public void onUserHangup(long userId) {
                if (!isDestroyed() && service.getUserId() != userId) {
                    int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                    if (!isSelfEnd && time != 0)
                        toastTip("对方已经挂断");
                    hungUpAndFinish();
                    isShowFace = false;
                    if (mCallDir == 0 && !mIsSendMessage) {  //发送方
                        ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), String.valueOf(userId), service.getChannelId(), callId, time, 1, closeType, isMatch, messageListener);
                        mIsSendMessage = true;
                    }
                    finish();
                }
            }

            @Override
            public void onRejectByUserId(String userId) {
                if (!isDestroyed() && mCallDir == 0) {
                    toastTip("对方已经拒绝");
                    isShowFace = false;
                    hungUpAndFinish();
                    int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                    if (!mIsSendMessage) {
                        if (time == 0) {
                            toastTip("对方已经拒绝");
                            ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_REJECT, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, 0, 1, 0, isMatch, messageListener);
                        } else {
                            toastTip("对方已经挂断");
                            ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 0, 2, isMatch, messageListener);
                        }
                        mIsSendMessage = true;
                    }
                    finish();
                }
            }

            @Override
            public void onAcceptByUserId(String userId) {
                LogUtil.e(TAG, "用户接收了" + userId);
                if (mChannelType == 1) {  //语音通话
                } else {
                    tvCallComment.setText("连接中...");
                }
                startCount();
            }


            @Override
            public void onUserBusy(String userId) {
                if (!isDestroyed() && mCallDir == 0) {
                    toastTip("对方忙碌中");
                    if (!mIsSendMessage) {
                        int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                        ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_BUSY, String.valueOf(service.getUserId()), userId, service.getChannelId(), callId, time, 1, 0, isMatch, messageListener);
                        mIsSendMessage = true;
                    }
                    stopPlayer();
                    handler.postDelayed(() -> {
                        finish();
                    }, DELAY_TIME);
                }
            }

            @Override
            public void onUserLeave(String userId, String reason) {
                if (!TextUtils.equals(fromId, userId))
                    return;
                if (!isDestroyed()) {
                    int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                    if (!isSelfEnd && time != 0)
                        toastTip("对方已经挂断");
                    isShowFace = false;
                    if (mCallDir == 0 && !mIsSendMessage) {  //发送方
                        ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), userId, service.getChannelId(), callId, time, 1, 2, isMatch, messageListener);
                        mIsSendMessage = true;
                    }
                    hungUpAndFinish();
                    finish();
                }
            }

            @Override
            public void onCancelByUserId(String uid) {
                if (!TextUtils.equals(fromId, uid))
                    return;
                if (!isDestroyed() && mCallDir == 1) {
                    toastTip("对方已取消");
                    nertcVideoCall.cancel(null);
                    stopPlayer();
                    finish();
                    isShowFace = false;
                } else {
                    if (mCallDir == 0 && closeType != 1 && !mIsSendMessage) {  //不是呼叫超时
                        ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), uid, service.getChannelId(), callId, TimeUtil.getChronometerSeconds(audioTime), 1, 2, isMatch, messageListener);
                        mIsSendMessage = true;
                    }
                }
            }

            @Override
            public void onJoinRoomFailed(int code) {
                toastTip("对方已取消");
                if (!mIsSendMessage && mCallDir == 0) {
                    int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, time, 1, 2, isMatch, messageListener);
                    mIsSendMessage = true;
                }
                hungUpAndFinish();
                finish();
            }

            @Override
            public void onError(int code) {
                int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                if (!isSelfEnd && time != 0)
                    toastTip("对方已经挂断");
                if (!mIsSendMessage && mCallDir == 0) {
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, time, 1, 2, isMatch, messageListener);
                    mIsSendMessage = true;
                }
                hungUpAndFinish();
                finish();
            }


            @Override
            public void onCameraAvailable(long userId, boolean isVideoAvailable) {
                LogUtil.e(TAG, "执行打开视频");
                if (service.getUserId() != userId) {
                    if (mChannelType == 2) {
                        if (isVideoAvailable) {
                            rlyTopUserInfo.setVisibility(View.GONE);
                            llUserInfo.setVisibility(View.VISIBLE);
                            if (remoteVideoView != null) {
                                remoteVideoView.setBackgroundColor(ContextCompat.getColor(VideoCallActivity.this, R.color.transparent));
                            }
                            ivCloseFace.setVisibility(View.GONE);
//                            ivSmallCloseFace.setVisibility(View.GONE);
                            setupRemoteVideo(userId);
                        } else {
                            LogUtil.e(TAG, "" + isShowFace);
                        }
                    }
                }
            }

            @Override
            public void onUserVideoStop(long userId) {
                if (remoteVideoView != null && localVideoView != null) {
                    if (isChangeVideo) {
                        localVideoView.clearImage();
                    } else {
                        remoteVideoView.clearImage();
                    }
                }
            }

            /**
             *  0	网络质量未知
             *  1	网络质量极好
             *  2	用户主观感觉和极好差不多，但码率可能略低于极好
             *  3	能沟通但不顺畅
             *  4	网络质量差
             *  5	完全无法沟通
             */
            @Override
            public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {
                if (stats == null || stats.length == 0) {
                    setTipViewVisible(tvCallTip);
                    return;
                }
                if (netStatus <= 1) {
                    netStatus++;
                    return;
                }
                selfNetStatus = false;
                otherNetStatus = false;
                setTipViewVisible(tvCallTip);
                for (NERtcNetworkQualityInfo networkQualityInfo : stats) {
                    if (networkQualityInfo.upStatus >= 4 || networkQualityInfo.upStatus == 0) {
                        if (service.getUserId() == networkQualityInfo.userId) {
                            selfNetStatus = true;
                        } else if (networkQualityInfo.userId == peerUid) {
                            otherNetStatus = true;
                        }
                    }
                }
                if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_show_net_not_ok == 1) {
                    if (selfNetStatus) {
                        tvNetwork.setText("您当前网络质量较差，通话卡顿");
                        setTipViewVisible(tvNetwork);
                    }
                    if (otherNetStatus) {
                        tvNetwork.setText("对方网络质量较差，通话卡顿");
                        setTipViewVisible(tvNetwork);
                    }
                }
            }

            @Override
            public void onAudioAvailable(long userId, boolean isVideoAvailable) {
                LogUtil.e(TAG, "执行打开音频");
            }

            @Override
            public void timeOut(int type) {
                closeType = 1;
                toastTip(type == 1 ? "对方已取消" : "对方手机可能不在线，建议稍后再次尝试");
                stopPlayer();
                int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                if (mCallDir == 0 && !mIsSendMessage) {
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_NOT_ACCEPT, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 1, 1, isMatch, messageListener);
                    mIsSendMessage = true;
                }
                finish();
            }
        };
    }

    private int netStatus = 0;
    private boolean selfNetStatus = false;
    private boolean otherNetStatus = false;

    private final Handler handler = new Handler(Looper.getMainLooper());


    public static void startCallOther(Context context, UserInfo callOutUser, int callType, String matchType, String callId, int isOnLine, boolean isMatch) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra(CALL_OUT_USER, callOutUser);
        intent.putExtra(CALL_DIR, 0);
        intent.putExtra(CALL_TYPE, callType);
        intent.putExtra(CALL_ID, callId);
        intent.putExtra(CALL_IS_MATCH, isMatch);
        intent.putExtra(CALL_USER_IS_ON_LINE, isOnLine);
        intent.putExtra(CALL_MATCH_TYPE, matchType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void startPlayer() {
//        NERtcCreateAudioMixingOption option = new NERtcCreateAudioMixingOption();
//        option.path = AssetsUtil.getFileFromAssetsFile("avchat_ring.mp3"); //混音文件路径
//        LogUtil.e("========================",option.path+"=====");
//        option.playbackEnabled = true;    //是否本地播放
//        option.playbackVolume = 100;      //本地播放音量
//        option.sendEnabled = false;        //是否编码发送
//        option.sendVolume = 100;          //发送音量
//        option.loopCount = 0;             //循环次数
//        int ret = NERtcEx.getInstance().startAudioMixing(option);
//        if (ret == NERtcConstants.ErrorCode.OK) {
//            //创建混音任务成功
//        } else {
//            //创建混音任务失败
//        }
//        LogUtil.e("========================",ret+"=====");
        stopPlayer();
        initSpeakerDrwables();
        handler.postDelayed(runnable1, 1000);

    }

    private void setTipViewVisible(View view) {
        tvNetwork.setVisibility(View.GONE);
        tvCallTip.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }


    Runnable runnable1 = () -> {
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        initSpeakerDrwables();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
//        mAudioManager.setSpeakerphoneOn(speakerphoneOn);
        mediaPlayer = MediaPlayer.create(VideoCallActivity.this, R.raw.avchat_ring);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    };

    private void initSpeakerDrwables() {
        boolean speakerphoneOn = mAudioManager.isSpeakerphoneOn();
        isOpen = speakerphoneOn;
        if (isOpen) {
            tvSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.mipmap.chat_ic_speaker_on), null, null);
        } else {
            tvSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.mipmap.chat_ic_speaker_off), null, null);
        }
        setSpeakerOnDrawble();
    }


    private void stopPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mAudioManager != null)
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        handler.removeCallbacks(runnable1);
    }

    @Override
    protected boolean getImmersionBar() {
        return false;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mClVideo = findViewById(R.id.cl_video);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).titleBar(mClVideo).init();
        localVideoView = findViewById(R.id.local_video_view);
        viewVideoView = findViewById(R.id.view_video_view);
        videoView = findViewById(R.id.video_view);
        remoteVideoView = findViewById(R.id.remote_video_view);
        viewRemote = findViewById(R.id.view_remote);
        tvSwitch = findViewById(R.id.tv_camera_switch);
        ivUserIcon = findViewById(R.id.iv_call_user);
        ivAvater = findViewById(R.id.iv_avatar);
        tvCallUser = findViewById(R.id.tv_call_user);
        tvName = findViewById(R.id.tv_name);
        tvCallComment = findViewById(R.id.tv_call_comment);
        mLlIncome = findViewById(R.id.ll_income);
        tvSpeaker = findViewById(R.id.iv_speaker_control);  //扬声器
        tvMute = findViewById(R.id.iv_mute_control);
        tvVideoMute = findViewById(R.id.tv_video_microphone);//视频免提按钮
        tvVideoSpeaker = findViewById(R.id.tv_video_speak);//视频免提按钮
        mTvCancel = findViewById(R.id.tv_cancel);
        tvAccept = findViewById(R.id.tv_accept);
        tvReject = findViewById(R.id.tv_reject);
        tvFaceUnity = findViewById(R.id.tv_face_unity);
        llyBingCall = findViewById(R.id.lly_invited_operation);
        llyDialogOperation = findViewById(R.id.lly_dialog_operation);
        rlyTopUserInfo = findViewById(R.id.rly_top_user_info);
        llUserInfo = findViewById(R.id.ll_userinfo);
        mFlVoiceBg = findViewById(R.id.fl_voice_bg);   //语音头像蒙城
        mIvVoiceAvatar = findViewById(R.id.iv_voice_avatar);
        ivGift = findViewById(R.id.iv_gift);  //礼物
        svgaImageView = findViewById(R.id.svg_image);
        mPlayerView = findViewById(R.id.player_view);
        giftRoot = findViewById(R.id.gift_root);
        rvCallChat = findViewById(R.id.rv_call_chat);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        rvCallChat.setLayoutManager(layout);
        //视频通话相关
        llCalVideo = findViewById(R.id.ll_call_video);
        clVideoCall = findViewById(R.id.cl_video_btn);
        llVideoTime = findViewById(R.id.ll_video_time);  //视频通话时长
        videoTime = findViewById(R.id.video_time);
        tvFaceSwitch = findViewById(R.id.tv_face_switch);  //不露脸视频切换
        ivCloseFace = findViewById(R.id.iv_close_face);
        ivSmallCloseFace = findViewById(R.id.iv_small_close_face);
        tvCallFaceUnity = findViewById(R.id.tv_call_face_unity);
        tvVideoFace = findViewById(R.id.tv_video_face);

        //语音通话相关
        llAudioTime = findViewById(R.id.ll_audio_time);  //语音通话时长
        audioTime = findViewById(R.id.audio_time);
        clVoiceCall = findViewById(R.id.cl_voice_call);
        mIvSmallVoice = findViewById(R.id.iv_small_voice);


        tvNetwork = findViewById(R.id.tv_network);

        mtvGenderAge = findViewById(R.id.tv_call_user_gender_age);
        mtvCity = findViewById(R.id.tv_call_user_city);
        mtvConstellation = findViewById(R.id.tv_call_user_constellation);
        mtvJob = findViewById(R.id.tv_call_user_job);

        mtvVideoGenderAge = findViewById(R.id.tv_video_user_gender_age);
        mtvVideoCity = findViewById(R.id.tv_video_user_city);
        mtvVideoConstellation = findViewById(R.id.tv_video_user_constellation);
        mtvVideoJob = findViewById(R.id.tv_video_user_job);


        //余额不足
        mClRecharge = findViewById(R.id.cl_recharge);
        tvRecharge = findViewById(R.id.tv_recharge);

        //违规
        mTvViolation = findViewById(R.id.tv_violation);
        mTvViolationTip = findViewById(R.id.tv_violation_tip);
        mTvIncome = findViewById(R.id.tv_income);
        mTvReportUser = findViewById(R.id.tv_report_user);
        tvCallTip = findViewById(R.id.tv_call_tip);
        initListener();
    }


    private void initListener() {
        ivGift.setOnClickListener(this);
        tvSwitch.setOnClickListener(this);
        tvFaceSwitch.setOnClickListener(this);
        tvFaceUnity.setOnClickListener(this);
        remoteVideoView.setOnClickListener(this);
        tvRecharge.setOnClickListener(this);
        tvCallFaceUnity.setOnClickListener(this);
        tvVideoFace.setOnClickListener(this);
        viewVideoView.setOnClickListener(this);

        mIvSmallVoice.setOnClickListener(this);
        mTvReportUser.setOnClickListener(this);
    }


    private void initIntent() {
        invitedEvent = (InvitedEvent) getIntent().getSerializableExtra(INVENT_EVENT);
        fromId = getIntent().getStringExtra(CALL_FROM_ID);
        if(fromId == null && invitedEvent != null){
            fromId = invitedEvent.getFromAccountId();
        }
        callId = getIntent().getStringExtra(CALL_ID);
        callOutUser = (UserInfo) getIntent().getSerializableExtra(CALL_OUT_USER);
        mCallDir = getIntent().getIntExtra(CALL_DIR, 0);
        mChannelType = getIntent().getIntExtra(CALL_TYPE, 1);    // 1: 语音 2：视频
        isMatch = getIntent().getBooleanExtra(CALL_IS_MATCH, false);
        matchType = getIntent().getStringExtra(CALL_MATCH_TYPE);   //是否是询问匹配中
        isOnLine = getIntent().getIntExtra(CALL_USER_IS_ON_LINE, 1);
        SPUtils.remove(Constants.INVITED_EVENT);
    }

    private synchronized void callInstance() {
        Utils.runOnUiThread(() -> {
            if (nertcVideoCall == null)
                nertcVideoCall = NERTCVideoCall.sharedInstance();
            nertcVideoCall.initNERtc(NERTCVideoCallImpl.CALL, null);
            nertcVideoCall.setTimeOut(60 * 1000);
            if (nertcCallingDelegate != null) {
                nertcVideoCall.removeDelegate(nertcCallingDelegate);
            }
            initCallingDelegate();
            nertcVideoCall.addDelegate(nertcCallingDelegate);
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        callInstance();
        super.onCreate(savedInstanceState);
//        initPhoneStateListener();
    }


    @Override
    protected void initData() {
        initIntent();
        //获取对方用户信息
        if(!TextUtils.isEmpty(fromId) || null != callOutUser) {
            getP().getUserInfoById(TextUtils.isEmpty(fromId) ? callOutUser.getUser_id() + "" : fromId);
        }
//        tvCallComment.setText("速配成功，即将开始语音通话");
//        tvCallComment.setVisibility(View.VISIBLE);
        p.startThread(this, svgaImageView, mPlayerView);
        callMessage = new ArrayList<>();
        mAdapter = new CallMessageAdapter(callMessage);
        rvCallChat.setAdapter(mAdapter);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageReceiverObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, true);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, true);
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, true);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        Drawable drawable;
        if (mChannelType == 2) {  //视频
            initFace();
            drawable = ContextCompat.getDrawable(this, R.mipmap.call_accept);
            tvFaceUnity.setVisibility(View.VISIBLE);
            //判断男生和女生不同逻辑
            int grader = service.getUserInfo().getSex();
            if (grader == 1) {
                tvCallFaceUnity.setVisibility(View.GONE);  //男生不显示美颜
                tvFaceUnity.setVisibility(View.GONE);
                tvVideoFace.setVisibility(View.VISIBLE);
                tvFaceSwitch.setVisibility(View.VISIBLE);  //男生显示露脸功能
            } else {
                tvCallFaceUnity.setVisibility(View.VISIBLE);
//                tvCallFaceUnity.setPadding(DensityUtils.dp2px(BaseApplication.getInstance(), 46), 0, 0, 0);
                tvFaceUnity.setVisibility(View.VISIBLE);
                tvVideoFace.setVisibility(View.GONE);
                tvFaceSwitch.setVisibility(View.GONE);
            }
        } else {
            drawable = ContextCompat.getDrawable(this, R.mipmap.call_accept);
            tvFaceUnity.setVisibility(View.GONE);
            tvVideoFace.setVisibility(View.GONE);
        }
        tvAccept.setCompoundDrawablesWithIntrinsicBounds(null,
                drawable, null, null);
        tvSpeaker.setOnClickListener(v -> {
            isOpen = !isOpen;
            if (nertcVideoCall != null) {
                nertcVideoCall.setSpeakerphoneOn(isOpen);
            }

            if (isOpen) {
                tvSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.chat_ic_speaker_on), null, null);
            } else {
                tvSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.chat_ic_speaker_off), null, null);
            }
        });
        tvMute.setOnClickListener(v -> {
            isMute = !isMute;
            if (nertcVideoCall != null) {
                nertcVideoCall.setMicMute(isMute);
            }

            if (isMute) {
                tvMute.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.chat_ic_mute_on), null, null);
            } else {
                tvMute.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.chat_ic_mute_off), null, null);
            }
        });
        tvVideoSpeaker.setOnClickListener(v -> {
            isOpen = !isOpen;
            if (nertcVideoCall != null) {
                nertcVideoCall.setSpeakerphoneOn(isOpen);
            }

            setSpeakerOnDrawble();
        });
        tvVideoMute.setOnClickListener(v -> {
            isMute = !isMute;
            if (nertcVideoCall != null) {
                nertcVideoCall.setMicMute(isMute);
            }

            if (isMute) {
                tvVideoMute.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.ic_micorphone_on), null, null);
                tvVideoMute.setText("静音已开");
            } else {
                tvVideoMute.setCompoundDrawablesWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this, R.mipmap.ic_micorphone_off), null, null);
                tvVideoMute.setText("静音");
            }
        });
        //切换视频头像
        localVideoView.setOnClickListener(v -> {
//            if (isFaceOn || isOpenFace) return;
            isChangeVideo = !isChangeVideo;
            Utils.runOnUiThread(() -> {
                if (isFaceOn || isOpenFace) {
                    viewRemote.setVisibility(View.GONE);
                    ivCloseFace.setVisibility(View.GONE);
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                }
                if (mIsWarm) {
                    viewRemote.setVisibility(View.GONE);
                    viewVideoView.setVisibility(View.VISIBLE);
                }
                if (mIsMeWarm)
                    mTvViolationTip.setVisibility(View.VISIBLE);
                if (nertcVideoCall == null) {
                    return;
                }
                if (isChangeVideo) { //女神逻辑
                    if (localVideoView != null) {
                        nertcVideoCall.setupRemoteView(localVideoView, videoUid);
                    }
                    NERtc.getInstance().startVideoPreview();
                    NERtc.getInstance().enableLocalVideo(true);
                    if (videoView != null && videoView.getVisibility() == View.VISIBLE) {
                        nertcVideoCall.setupLocalView(videoView);
                    } else {
                        if (remoteVideoView != null) {
                            nertcVideoCall.setupLocalView(remoteVideoView);
                        }
                    }
                } else {
                    if (localVideoView != null) {
                        nertcVideoCall.setupLocalView(localVideoView);
                    }
                    if (remoteVideoView != null) {
                        nertcVideoCall.setupRemoteView(remoteVideoView, videoUid);
                    }
                }
            });
        });
        if (mCallDir == 0 && callOutUser != null) {
            mTvCancel.setVisibility(View.VISIBLE);
            llyBingCall.setVisibility(View.GONE);
            mTvCancel.setOnClickListener(v -> {
                if (isFastClick()) return;
                if (service.getUserInfo().getSex() == 1) {
                    if (callHangUpPopWindow == null) {
                        callHangUpPopWindow = new CallHangUpPopWindow(this, mIsSpeak ? 1 : 0);
                        callHangUpPopWindow.addOnClickListener(this::cancelCall);
                    }
                    callHangUpPopWindow.startTime();
                    callHangUpPopWindow.setPopupGravity(Gravity.CENTER);
                    callHangUpPopWindow.showPopupWindow();
                } else {
                    cancelCall();
                }
            });
        } else if (invitedEvent != null || !TextUtils.isEmpty(fromId)) {   //来了语音视频通话
            mTvCancel.setVisibility(View.GONE);
            llyBingCall.setVisibility(View.VISIBLE);
            if (TextUtils.equals(matchType, ChatMsg.CALL_MATCH_FORCE)) {   //当时视频速配过来询问匹配时候调用
                llyBingCall.setVisibility(View.GONE);
            }
            mTvCancel.setOnClickListener(v -> {   //挂断
                if (isFastClick()) return;
                if (service.getUserInfo().getSex() == 1) {
                    if (callHangUpPopWindow == null) {
                        callHangUpPopWindow = new CallHangUpPopWindow(this, mIsSpeak ? 1 : 0);
                        callHangUpPopWindow.addOnClickListener(() -> {
                            isSelfEnd = true;
                            hungUpAndFinish();
                            finish();
                        });
                    }
                    callHangUpPopWindow.startTime();
                    callHangUpPopWindow.setPopupGravity(Gravity.CENTER);
                    callHangUpPopWindow.showPopupWindow();
                } else {
                    isSelfEnd = true;
                    hungUpAndFinish();
                    finish();
                }
            });
        }
        if (mCallDir == 0 && null != callOutUser) {   //打出
            sessionId = String.valueOf(callOutUser.getUser_id());
        } else if (mCallDir == 1) {
            if (null != invitedEvent)
                sessionId = String.valueOf(invitedEvent.getFromAccountId());
            else
                sessionId = fromId;
        }
        final com.netease.nimlib.sdk.uinfo.model.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(sessionId);
        String url = "";   //头像地址
        if (userInfo != null && null != service.getConfigInfo() && null != service.getConfigInfo().api && null != service.getConfigInfo().api.oss && null != service.getConfigInfo().api.oss.cdn)
            url = service.getConfigInfo().api.oss.cdn_scheme + service.getConfigInfo().api.oss.cdn.user + url + userInfo.getAvatar();
        //配置平台规则
        if (null != service.getConfigInfo() && null != service.getConfigInfo().sys && null != service.getConfigInfo().sys.content) {
            tvCallTip.setText(service.getConfigInfo().sys.content.audio_warn);
        }
        rlyTopUserInfo.setVisibility(View.VISIBLE);
        if (mCallDir == 0) {
            callOUt();
            if (mChannelType == 2) {   // 拨打视频电话
                clVoiceCall.setVisibility(View.VISIBLE);
                clVideoCall.setVisibility(View.VISIBLE);
                llCalVideo.setVisibility(View.VISIBLE);
            } else {   // 拨打语音电话
                llyDialogOperation.setVisibility(View.VISIBLE);
                clVoiceCall.setVisibility(View.VISIBLE);
            }
            tvCallComment.setText("等待对方接听...");
        } else {//接听通话
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.cmd_type = "call";
            chatMsg.from = sessionId;
            chatMsg.to = service.getUserId() + "";
            chatMsg.cmd = mChannelType == 2 ? ChatMsg.CALL_TYPE_VIDEO : ChatMsg.CALL_TYPE_VOICE;
            if (isMatch) {
                chatMsg.cmd = mChannelType == 2 ? ChatMsg.CALL_TYPE_VIDEO_MATCH : ChatMsg.CALL_TYPE_VOICE_MATCH;
            }
            getP().buriedPoint("callEnter", "enter", callId, JSON.toJSONString(chatMsg));
            callIn();
            clVoiceCall.setVisibility(View.VISIBLE);

            String text = "语音";
            if (mChannelType == 2) {//视频
                text = "视频";
            }
            tvCallComment.setText("邀请你"+text+"通话");
        }
        int sex = service.getUserInfo().getSex(); //用户性别：0.未知，1.男，2.女
        if(sex == 2){
            setCost("接听后将收益更多积分","");
        }
        if (mChannelType == 1) {  //语音
            mFlVoiceBg.setVisibility(View.VISIBLE);
            GlideUtils.loadImageGaussian(this, mIvVoiceAvatar, url, R.mipmap.ic_default_avatar);
        } else {
            mIvSmallVoice.setVisibility(View.GONE);
        }
        if (!TextUtils.equals(matchType, ChatMsg.CALL_MATCH_FORCE)) {   //当时视频速配过来询问匹配时候调用
            startPlayer();
        }

        //当是男性用户拨打视频的时候提示不露脸功能
        int faceOffTip = SPUtils.getInt(Constants.IS_SHOW_FACE_OFF, 0);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.content != null) {
            if (!TextUtils.isEmpty(service.getConfigInfo().sys.content.no_face_protect) && faceOffTip == 0 && service.getUserInfo().getSex() == 1 && mChannelType == 2) {
                SPUtils.put(Constants.IS_SHOW_FACE_OFF, 1);
            }
        }
        if (mChannelType == 1) {
            audioTime.setOnChronometerTickListener(chronometer -> {
                int time = TimeUtil.getChronometerSeconds(chronometer);
                if (callTotalTime != 0 && time >= callTotalTime - 1) {
                    closeType = 3;
                    automaticEnd();
                }

            });
        } else {
            videoTime.setOnChronometerTickListener(chronometer -> {
                int time = TimeUtil.getChronometerSeconds(chronometer);
                if (callTotalTime != 0 && time >= callTotalTime - 1) {
                    closeType = 3;
                    automaticEnd();
                }
            });
        }
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        getP().buriedPoint("noPermission", "permissionCheck", callId, String.valueOf(service.getUserId()));
                        Utils.runOnUiThreadDelayed(() -> getP().showPermission(VideoCallActivity.this), 500);
                    } else {
                        NERtcVideoConfig videoConfig = new NERtcVideoConfig();
                        videoConfig.frontCamera = true;
                        videoConfig.frameRate = NERtcVideoConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_30;
                        videoConfig.minFramerate = 25;
                        videoConfig.videoProfile = NERtcConstants.VideoProfile.HD720P;
                        videoConfig.videoCropMode = RTCVideoCropMode.kRTCVideoCropMode16x9;
                        NERtc.getInstance().setLocalVideoConfig(videoConfig);
                        if (mChannelType == 2) {  //视频
                            int code = NERtcEx.getInstance().startVideoPreview();
                            NERtc.getInstance().enableLocalVideo(true);
                            if (code != 0) { //失败
                                NERtcEx.getInstance().stopVideoPreview();
                                code = NERtcEx.getInstance().startVideoPreview();
                            }
                            if (code != 0) {
                                toastTip("相机被其它应用占用,请退出应用进入系统相机重启尝试");
                            }
                            Log.e("----neRtcVideoFrame-00-", code + "");
                            if (nertcVideoCall != null && videoView != null) {
                                nertcVideoCall.setupLocalView(videoView);
                            }
                            setVideoCallback();
                        }
                        NERtcEx.getInstance().adjustRecordingSignalVolume(400);
                        NERtcEx.getInstance().adjustPlaybackSignalVolume(400);
                    }
                }));
        LogUtil.e(TAG, isOnLine + "");
        if (mCallDir == 0 && isOnLine == 0) {
            toastTip("对方不在线");
            int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
            if (mCallDir == 0 && !mIsSendMessage) {
                ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_OFF_LINE, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 1, 1, isMatch, (code, message) -> RxBus.getDefault().post(new MessageCallEvent(message)));
                mIsSendMessage = true;
            }
            hungUpAndFinish();
            Utils.runOnUiThreadDelayed(this::finish, DELAY_TIME);
        }
        messageListener = (code, message) -> {
            RxBus.getDefault().post(new MessageCallEvent(message));
        };
        initBus();
    }

    /**
     * 设置免提图片
     */
    private void setSpeakerOnDrawble() {
        if (isOpen) {
            tvVideoSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.mipmap.ic_speaker_on), null, null);
            tvVideoSpeaker.setText("免提已开");
        } else {
            tvVideoSpeaker.setCompoundDrawablesWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(this, R.mipmap.ic_speaker_off), null, null);
            tvVideoSpeaker.setText("免提");
        }
    }


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(VideoCallActivity.this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_CALL_COST) {
                                ChatMsg.VideoStyle videoStyle = JSON.parseObject(event.code, ChatMsg.VideoStyle.class);
                                if (videoStyle != null && !TextUtils.isEmpty(videoStyle.msg) && !TextUtils.isEmpty(videoStyle.cost)) {
                                    setCost(videoStyle.msg, videoStyle.cost);
                                }
                            }
                        }
                ));
    }

    /**
     * 取消拨打
     */
    private void cancelCall() {
        Utils.runOnUiThread(() -> {
            isSelfEnd = true;
            int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
            if (!mIsSendMessage) {
                ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 0, 2, isMatch, messageListener);
                mIsSendMessage = true;
            }
            stopPlayer();
            if (time == 0)
                nertcVideoCall.cancel(null);
            else
                hungUpAndFinish();
            finish();
        });

    }


    /**
     * 用户信息变更观察者
     */
    private final UserInfoObserver userInfoObserver = accounts -> {
        if (!accounts.contains(sessionId)) {
            return;
        }
        getUserInfo();
    };


    private void getUserInfo() {
        final com.netease.nimlib.sdk.uinfo.model.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(sessionId);
        String url = "";   //头像地址
        if (userInfo != null && null != service.getConfigInfo() && null != service.getConfigInfo().api && null != service.getConfigInfo().api.oss && null != service.getConfigInfo().api.oss.cdn)
            url = service.getConfigInfo().api.oss.cdn_scheme + service.getConfigInfo().api.oss.cdn.user + url + userInfo.getAvatar();
        if (mCallDir == 0) {
//            if (mChannelType == 1) {   // 拨打语音电话
            clVoiceCall.setVisibility(View.VISIBLE);
//            }
            if (null != callOutUser) {   //打出
                ivUserIcon.loadBuddyAvatar(String.valueOf(callOutUser.getUser_id()));
                tvCallUser.setText(UserInfoHelper.getUserTitleName(String.valueOf(callOutUser.getUser_id()), SessionTypeEnum.P2P));
                ivAvater.loadBuddyAvatar(String.valueOf(callOutUser.getUser_id()));
                tvName.setText(UserInfoHelper.getUserTitleName(String.valueOf(callOutUser.getUser_id()), SessionTypeEnum.P2P));
            }
        }
        if (mCallDir == 1 && null != invitedEvent) {
            ivUserIcon.loadBuddyAvatar(String.valueOf(invitedEvent.getFromAccountId()));
            tvCallUser.setText(UserInfoHelper.getUserTitleName(String.valueOf(invitedEvent.getFromAccountId()), SessionTypeEnum.P2P));
            ivAvater.loadBuddyAvatar(String.valueOf(invitedEvent.getFromAccountId()));
            tvName.setText(UserInfoHelper.getUserTitleName(String.valueOf(invitedEvent.getFromAccountId()), SessionTypeEnum.P2P));
        }
        if (mChannelType == 1) {  //语音
            GlideUtils.loadImageGaussian(mActivity, mIvVoiceAvatar, url, R.mipmap.ic_default_avatar);
        } else {
            mIvSmallVoice.setVisibility(View.GONE);
        }
    }


    @Override
    protected int getLayout() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_call;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (!ClickUtil.canOperate()) {
            return;
        }
        if (id == R.id.iv_gift) {  //礼物相关
            getP().getGiftData(this, sessionId, 1);
        } else if (id == R.id.tv_camera_switch) {   //切换摄像头
            if (nertcVideoCall != null) {
                nertcVideoCall.switchCamera();
            }
        } else if (id == R.id.tv_face_unity || id == R.id.tv_call_face_unity) {  //美颜未接通
            faceUnityView.setVisibility(View.VISIBLE);
        } else if (id == R.id.tv_face_switch || id == R.id.tv_video_face) {  //不露脸
            Drawable drawable;
            Drawable drawable1;
            isFaceOn = !isFaceOn;
            if (isFaceOn) {  //开启
                NERtc.getInstance().enableLocalVideo(false);
                if (isChangeVideo) {
                    if (remoteVideoView != null)
                        remoteVideoView.clearImage();
                } else {
                    if (localVideoView != null)
                        localVideoView.clearImage();
                }
                mSkipFrame = 5;
                drawable = ContextCompat.getDrawable(this, R.mipmap.ic_face_on);
                drawable1 = ContextCompat.getDrawable(this, R.mipmap.chat_ic_face_on);
                tvFaceSwitch.setText("不露脸已开");
                if (!mIsAccept) {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                } else {
                    if (isChangeVideo) {
                        viewRemote.setVisibility(View.VISIBLE);
                        ivCloseFace.setVisibility(View.VISIBLE);
                    } else {
                        viewVideoView.setVisibility(View.VISIBLE);
                        ivSmallCloseFace.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                NERtc.getInstance().startVideoPreview();
                mSkipFrame = 5;
                NERtc.getInstance().enableLocalVideo(true);
                if (nertcVideoCall != null) {
                    if (videoView != null) {
                        nertcVideoCall.setupLocalView(videoView);
                    }

                }

                viewVideoView.setVisibility(View.GONE);
                viewRemote.setVisibility(View.GONE);
                drawable = ContextCompat.getDrawable(this, R.mipmap.ic_face_off);
                drawable1 = ContextCompat.getDrawable(this, R.mipmap.chat_ic_face_off);
                tvFaceSwitch.setText("不露脸视频");
                ivSmallCloseFace.setVisibility(View.GONE);
                ivCloseFace.setVisibility(View.GONE);
                if (mIsWarm) {
                    if (isChangeVideo) {
                        viewVideoView.setVisibility(View.VISIBLE);
                    } else {
                        viewRemote.setVisibility(View.VISIBLE);
                    }
                }
            }
            LogUtil.e("=================", "+" + isChangeVideo + isFaceOn + mIsAccept);
            getP().sendNotification(sessionId, isFaceOn);
            tvVideoFace.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable1, null, null);
            tvFaceSwitch.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable, null, null);
        } else if (id == R.id.remote_video_view) {  //点击消失美颜弹窗
            if (faceUnityView != null && faceUnityView.getVisibility() == View.VISIBLE)
                faceUnityView.setVisibility(View.GONE);
        } else if (id == R.id.tv_recharge) {
            getP().showRechargePop(VideoCallActivity.this, 3, 2, sessionId);
        } else if (id == R.id.view_video_view) {  // 不露脸
            isChangeVideo = !isChangeVideo;
            Utils.runOnUiThread(() -> {
                if (isChangeVideo) { //男生逻辑
                    if (nertcVideoCall != null) {
                        if (remoteVideoView != null) {
                            nertcVideoCall.setupLocalView(remoteVideoView);
                            if (localVideoView != null) {
                                nertcVideoCall.setupRemoteView(localVideoView, videoUid);
                            }
                        }

                    }
                    viewVideoView.setVisibility(View.GONE);
                    ivSmallCloseFace.setVisibility(View.GONE);
                    viewRemote.setVisibility(View.VISIBLE);
                    if (isFaceOn || isOpenFace)
                        ivCloseFace.setVisibility(View.VISIBLE);
                    if (mIsMeWarm)
                        mTvViolationTip.setVisibility(View.GONE);
                } else {  // 女生逻辑
                    if (nertcVideoCall != null && remoteVideoView != null) {
                        nertcVideoCall.setupRemoteView(remoteVideoView, videoUid);
                    }
                    viewVideoView.setVisibility(View.GONE);
                    ivSmallCloseFace.setVisibility(View.GONE);
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                    NERtc.getInstance().startVideoPreview();
                    NERtc.getInstance().enableLocalVideo(true);
                    if (nertcVideoCall != null && localVideoView != null) {
                        nertcVideoCall.setupLocalView(localVideoView);
                    }
                    if (mIsMeWarm)
                        mTvViolationTip.setVisibility(View.VISIBLE);
                }
            });
        } else if (id == R.id.iv_small_voice) {
            //音视频通话进入后台模式，显示悬浮按钮
            if(!checkOverlayDisplayPermission()){
                getP().showAlertPermission(this);
            }else{
                smallWindow();
            }

        } else if (id == R.id.tv_report_user) {
            //举报
            ARouterUtils.toBeforeReportActivity(mCallDir == 0?callOutUser.getUser_id():Integer.parseInt(fromId),1);//0 call out 1 call in
        }
    }

    private boolean checkOverlayDisplayPermission() {
        // API23以后需要检查权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        } else {
            return true;
        }
    }


    /**
     * 消息状态变化观察者
     */
    private final Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (isMyMessage(message)) {
                NimLog.i(TAG, String.format("content: %s, callbackExt: %s", message.getContent(), message.getCallbackExtension()));
                getCallBackContent(message, message.getCallbackExtension());
            }
            LogUtil.e("=====================", message.getCallbackExtension() + "===" + isMyMessage(message) + "==" + message.getFromAccount() + "===" + service.getUserId());
        }
    };


    /**
     * 显示爱心
     */
    private void getCallBackContent(IMMessage message, String content) {
        if (TextUtils.isEmpty(content)) return;
        try {
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(content);
            if (null != chatMsg) {   //消息回调（弹窗类型）
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //通过消息返回值 弹出充值
                    ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
                    if (alert == null) return;
                    if (message != null && alert.is_fail) {
                        message.setStatus(MsgStatusEnum.fail);
                        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
                        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                        RxBus.getDefault().post(new MessageCallEvent(message, 1));
                    }
                }
                LogUtil.e("===============", chatMsg.cmd_type + "======================");
                //判断是否是礼物
                if (TextUtils.equals("gift_play", chatMsg.cmd_type)) {
                    GiftDto gift = JSON.parseObject(chatMsg.content, GiftDto.class);
                    List<String> user = new ArrayList<>();
                    user.add(chatMsg.to);
                    getP().sendGiftSuccess(gift, message, user);
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_TYPE)) {  //亲密度通知
                    ChatMsg.Intimacy intimacy = JSON.parseObject(chatMsg.content, ChatMsg.Intimacy.class);
                    //判断是否是礼物
                    if (!TextUtils.isEmpty(chatMsg.child)) {
                        ChatMsg chatMsgChild = JSON.parseObject(chatMsg.child, ChatMsg.class);
                        if (chatMsgChild != null && TextUtils.equals("gift_play", chatMsgChild.cmd_type)) {
                            GiftDto gift = JSON.parseObject(chatMsgChild.content, GiftDto.class);
                            List<String> user = new ArrayList<>();
                            user.add(chatMsgChild.to);
                            getP().sendGiftSuccess(gift, message, user);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == SessionTypeEnum.P2P
                && message.getFromAccount() != null
                && message.getFromAccount().equals(service.getUserId() + "");
    }


    private final Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {

        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages != null) {
                for (IMMessage imMessage : imMessages) {
                    try {
                        ChatMsg chatMsg = ChatMsgUtil.parseMessage(imMessage);
                        if (chatMsg != null && !isDestroyed()) {
                            if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE) && !isSelfEnd) {
                                ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                                if (TextUtils.equals(callId, callMsg.call_id)) {
                                    if (callMsg.state == ChatMsg.EVENT_END) {
                                        if (callMsg.close_type == 1) {    // 呼叫超时
                                            toastTip("对方已取消");
                                        } else {
                                            if (callMsg.answer_time == 0) {
                                                toastTip("对方已取消");
                                            } else {
                                                toastTip("对方已经挂断");
                                            }
                                        }
                                        cancelCall();
                                    } else if (callMsg.state == ChatMsg.EVENT_OFF_LINE) {
                                        toastTip("对方已取消");
                                        hungUpAndFinish();
                                        finish();
                                    }
                                    if (callMsg.state == ChatMsg.EVENT_ERROR) {
                                        toastTip("网络异常，通话结束");
                                        cancelCall();
                                    }
                                }
                                LogUtil.e("==============执行了", "===" + callMsg.state + "=======" + callMsg.answer_time + callId + "======" + callMsg.call_id);
                            } else if (!TextUtils.equals(imMessage.getFromAccount(), String.valueOf(service.getUserId()))) {  //不是自己发送的礼物
                                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
                                    if (TextUtils.equals("p2p", chatMsg.cmd)) {
                                        String sessionId = "";
                                        if (mCallDir == 0 && null != callOutUser) {   //打出
                                            sessionId = String.valueOf(callOutUser.getUser_id());
                                        } else if (mCallDir == 1) {
                                            if (null != invitedEvent)
                                                sessionId = String.valueOf(invitedEvent.getFromAccountId());
                                            else
                                                sessionId = fromId;
                                        }
                                        if (TextUtils.equals(chatMsg.from, sessionId)) {
                                            ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
                                            getP().showGift(imMessage, chatMsg, 1);
                                            CallMessageDto data = new CallMessageDto();
                                            data.gift = gift.gift_info.name;
                                            callMessage.add(data);
                                            mAdapter.setList(callMessage);
                                            rvCallChat.scrollToPosition(callMessage.size() - 1);
                                            NIMClient.getService(MsgService.class).clearUnreadCount(sessionId, SessionTypeEnum.P2P);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 命令消息接收观察者
     */
    private final Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!sessionId.equals(message.getSessionId()) || message.getSessionType() != SessionTypeEnum.P2P) {
                return;
            }
            if (!TextUtils.isEmpty(message.getContent()) && message.getContent().contains("data")) {
                JSONObject json = JSON.parseObject(message.getContent());
                isOpenFace = json.getBoolean("data");
                setIsOnFace();
                Utils.runOnUiThread(() -> {
                    if (mIsMeWarm) {   //自己违规了
                        mTvViolationTip.setVisibility(View.VISIBLE);
                        NERtc.getInstance().enableLocalVideo(false);
                        mSkipFrame = 5;
                        //是否切换了摄像头
                        if (isChangeVideo) {
                            if (remoteVideoView != null)
                                remoteVideoView.clearImage();
                            viewRemote.setVisibility(View.VISIBLE);
                            mTvViolationTip.setVisibility(View.GONE);
                        } else {
                            viewVideoView.setVisibility(View.VISIBLE);
                            mTvViolationTip.setVisibility(View.VISIBLE);
                            if (localVideoView != null)
                                localVideoView.clearImage();
                        }
                    }
                });
            } else if (!TextUtils.isEmpty(message.getContent()) && message.getContent().contains("call")) {
                JSONObject json = JSON.parseObject(message.getContent());
                int type = json.getIntValue("call");
                if (type == 0 && !isDestroyed() && mCallDir == 0) {
                    if (mChannelType == 1) {  //语音通话
                    } else {
                        tvCallComment.setText("连接中...");
                    }
                    startCount();
                } else if (type == 1 && !isDestroyed() && mCallDir == 0) {
                    isShowFace = false;
                    hungUpAndFinish();
                    int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
                    if (!mIsSendMessage) {
                        if (time == 0) {
                            toastTip("对方已经拒绝");
                            ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_REJECT, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, 0, 1, 0, isMatch, messageListener);
                        } else {
                            toastTip("对方已经挂断");
                            ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 0, 2, isMatch, messageListener);
                        }
                        mIsSendMessage = true;
                    }
                    finish();
                } else if (type == 2) {
                    LogUtil.e(TAG, "用户进入了" + mChannelId);
                    if (!isEnter) {
                        getP().sendNotification(sessionId, 2);
                        userEnter();
                        if (mCallDir == 1) {
                            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_call_after_join_room == 1) {
                                ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_ACCEPT, String.valueOf(service.getUserId()), sessionId, mChannelId, callId, 0, 1, 2, isMatch, messageListener);
                            }
                        }
                        isEnter = true;
                    }

                } else if (type == 4) {
                    closeType = 4;
                }
            }
            final ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getContent());
            if (null == chatMsg) {
                return;
            }
            try {
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.VIDEO_STYLE)) {   //音视频违规
                    if (chatMsg.cmd.equals("warm")) {   //当前违规了
                        mIsWarm = true;
                        ChatMsg.VideoStyle videoStyle = JSON.parseObject(chatMsg.content, ChatMsg.VideoStyle.class);
                        if (videoStyle != null) {
                            mTvViolation.setVisibility(View.VISIBLE);
                            if (countBackUtils == null)
                                countBackUtils = new CountBackUtils();
                            countBackUtils.countBack(videoStyle.second, new CountBackUtils.Callback() {
                                @Override
                                public void countBacking(long time) {
                                }

                                @Override
                                public void finish() {
                                    closeWarm();
                                }
                            });
                            if (videoStyle.is_to_black) {   //自己违规了
                                mIsMeWarm = true;
                                new CustomPopWindow(VideoCallActivity.this, 1)
                                        .setContent(videoStyle.msg)
                                        .setSingleButton()
                                        .setRightButton(getString(R.string.i_know))
                                        .showPopupWindow();
                                mTvViolationTip.setVisibility(View.VISIBLE);
                                NERtc.getInstance().enableLocalVideo(false);
                                //是否切换了摄像头
                                if (isChangeVideo) {
                                    viewRemote.setVisibility(View.VISIBLE);
                                    mTvViolationTip.setVisibility(View.GONE);
                                } else {
                                    viewVideoView.setVisibility(View.VISIBLE);
                                    mTvViolationTip.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mIsMeWarm = false;
                                mTvViolation.setText(R.string.chat_video_other_warm_tip);
                                new CustomPopWindow(VideoCallActivity.this, 1)
                                        .setContent(videoStyle.msg)
                                        .setSingleButton()
                                        .setRightButton(getString(R.string.i_know))
                                        .showPopupWindow();
                                if (isChangeVideo) {
                                    viewVideoView.setVisibility(View.VISIBLE);
                                } else {
                                    viewRemote.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if (TextUtils.equals("close", chatMsg.cmd)) {   //关闭违规
                        closeWarm();
                    } else if (TextUtils.equals("cost", chatMsg.cmd)) {   //花费多少钱
                        ChatMsg.VideoStyle videoStyle = JSON.parseObject(chatMsg.content, ChatMsg.VideoStyle.class);
                        if (videoStyle != null && !TextUtils.isEmpty(videoStyle.msg) && !TextUtils.isEmpty(videoStyle.cost)) {
                            setCost(videoStyle.msg, videoStyle.cost);
                        }
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    };


    /**
     * 显示打电话多少金币
     */
    private void setCost(String msg, String cost) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(msg);
//        int start = builder.toString().indexOf(cost);
//        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5A7B")), start, start + cost.length(),
//                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mTvIncome.setText(builder);
        mTvIncome.setVisibility(View.VISIBLE);
    }

    /**
     * 设置是否露脸
     */
    private void setIsOnFace() {
        if (isOpenFace && mIsAccept) {  //判断是否露脸
            if (service.getUserInfo().getSex() == 2) {
                if (isChangeVideo) {
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                } else {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                }
            }
        } else {
            viewVideoView.setVisibility(View.GONE);
            viewRemote.setVisibility(View.GONE);
            ivCloseFace.setVisibility(View.GONE);
            ivSmallCloseFace.setVisibility(View.GONE);
            NERtc.getInstance().startVideoPreview();
            NERtc.getInstance().enableLocalVideo(true);
            if (isChangeVideo) {
                if (remoteVideoView != null) {
                    nertcVideoCall.setupLocalView(remoteVideoView);
                }
            } else {
                if (localVideoView != null) {
                    nertcVideoCall.setupLocalView(localVideoView);
                }
            }
        }
    }


    /**
     * 关闭违规弹窗
     */
    private void closeWarm() {
        mTvViolation.setVisibility(View.GONE);
        mTvViolationTip.setVisibility(View.GONE);
        NERtc.getInstance().startVideoPreview();
        viewVideoView.setVisibility(View.GONE);
        viewRemote.setVisibility(View.GONE);
        mSkipFrame = 5;
        if (mIsMeWarm)
            NERtc.getInstance().enableLocalVideo(true);
        mIsWarm = false;
        mIsMeWarm = false;
        Drawable drawable1 = null;
        if (mCallDir == 0 && isFaceOn) {  //开启
            drawable1 = ContextCompat.getDrawable(this, R.mipmap.chat_ic_face_on);
            if (!mIsAccept) {
                viewRemote.setVisibility(View.VISIBLE);
                ivCloseFace.setVisibility(View.VISIBLE);
            } else {
                if (isChangeVideo) {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                } else {
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                }
            }
        }
        tvVideoFace.setCompoundDrawablesWithIntrinsicBounds(null,
                drawable1, null, null);
        if ((mCallDir == 1 && (mIsAccept && isOpenFace))) {
            if (service.getUserInfo().getSex() == 2) {
                if (isChangeVideo) {
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                } else {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                }
            }
        }
        LogUtil.e("=================", "+" + isChangeVideo + isFaceOn + mIsAccept + isOpenFace);
    }


    private void userEnter() {
        nertcVideoCall.setStatsObserver();
        //默认不开启免提
        if (mCallDir == 0 && !mIsAccept) {
            rvCallChat.setVisibility(View.VISIBLE);
            mLlIncome.setVisibility(View.GONE);
            if (mChannelType == 2) {  //视频通话
                clVideoCall.setVisibility(View.VISIBLE);
                llCalVideo.setVisibility(View.VISIBLE);
                tvSpeaker.setVisibility(View.GONE);
                tvMute.setVisibility(View.GONE);
                rlyTopUserInfo.setVisibility(View.GONE);
                llUserInfo.setVisibility(View.VISIBLE);
                llVideoTime.setVisibility(View.VISIBLE);
                videoTime.setBase(SystemClock.elapsedRealtime());
                videoTime.start();
            } else {   //语音
                llAudioTime.setVisibility(View.VISIBLE);
                clVoiceCall.setVisibility(View.VISIBLE);

                audioTime.setBase(SystemClock.elapsedRealtime());
                audioTime.start();
            }
            setupLocalVideo();
            //延时点击
            mTvCancel.setEnabled(false);
            Utils.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvCancel.setEnabled(true);
                }
            }, 1500);
            getP().getCurrentCoin(callId);
        }
        if (mCallDir == 1) {
            setupLocalVideo();
            mLlIncome.setVisibility(View.GONE);
            if (mChannelType == 1) {   //语音
                llyDialogOperation.setVisibility(View.VISIBLE);
                llAudioTime.setVisibility(View.VISIBLE);
                mTvCancel.setVisibility(View.VISIBLE);

                audioTime.setBase(SystemClock.elapsedRealtime());
                audioTime.start();
            } else {  //视频
                clVideoCall.setVisibility(View.VISIBLE);
                llCalVideo.setVisibility(View.VISIBLE);
                llyDialogOperation.setVisibility(View.VISIBLE);
                tvSpeaker.setVisibility(View.GONE);
                tvMute.setVisibility(View.GONE);
                rlyTopUserInfo.setVisibility(View.GONE);
                llUserInfo.setVisibility(View.VISIBLE);
                mTvCancel.setVisibility(View.VISIBLE);
                llVideoTime.setVisibility(View.VISIBLE);
                videoTime.setBase(SystemClock.elapsedRealtime());
                videoTime.start();
            }
//            if (null == p)
//                p = new CallPresenter();
//            getP().getCurrentCoin();
        }
        setTipViewVisible(tvCallTip);
        mClVideo.setVisibility(View.VISIBLE);
    }


    private void initFace() {
        if (!FURenderer.isInit()) {
            FURenderer.setup(this);
        }
        if (mFURenderer == null) {
            int mCameraFacing = FURenderer.CAMERA_FACING_FRONT;
            mFURenderer = new FURenderer.Builder(this)
                    .setInputTextureType(FURenderer.INPUT_TEXTURE_EXTERNAL_OES)
                    .setCameraFacing(mCameraFacing)
                    .setInputImageOrientation(CameraUtils.getCameraOrientation(mCameraFacing))
                    .setRunBenchmark(true)
                    .setOnDebugListener((fps, callTime) -> {
                    })
                    .build();
            faceUnityView = findViewById(R.id.fu_view);
            if (faceUnityView != null)
                faceUnityView.setModuleManager(mFURenderer);
            Utils.runOnUiThread(() -> {
                LifeCycleSensorManager lifeCycleSensorManager = new LifeCycleSensorManager(VideoCallActivity.this, getLifecycle());
                lifeCycleSensorManager.setOnAccelerometerChangedListener(VideoCallActivity.this);
            });
        }
    }


    private void smallWindow() {
        startTime = System.currentTimeMillis();
        seconds = TimeUtil.getChronometerSeconds(audioTime);
        moveTaskToBack(true);//最小化Activity
        Intent intent = new Intent(BaseApplication.getInstance(), FloatVideoWindowService.class);//开启服务显示悬浮框
        intent.putExtra(CALL_DIR, mCallDir);
        intent.putExtra(CALL_TYPE, mChannelType);
        intent.putExtra(INVENT_EVENT, invitedEvent);
        intent.putExtra(CALL_OUT_USER, callOutUser);
        bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (ISBOUND) {
            unbindService(mVideoServiceConnection);//不显示悬浮框
            Intent intent = new Intent(BaseApplication.getInstance(), FloatVideoWindowService.class);
            intent.putExtra(CALL_DIR, mCallDir);
            intent.putExtra(CALL_TYPE, mChannelType);
            intent.putExtra(INVENT_EVENT, invitedEvent);
            intent.putExtra(CALL_OUT_USER, callOutUser);
            stopService(intent);
            ISBOUND = false;
        }

    }

    ServiceConnection mVideoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取服务的操作对象
            FloatVideoWindowService.MyBinder binder = (FloatVideoWindowService.MyBinder) service;
            binder.getService();
            ISBOUND = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * 打出
     */
    private void callOUt() {
        nertcVideoCall.call(String.valueOf(callOutUser.getUser_id()), mChannelType == 1 ? ChannelType.AUDIO : ChannelType.VIDEO);
        if (null != callOutUser) {   //打出
            ivUserIcon.loadBuddyAvatar(String.valueOf(callOutUser.getUser_id()));
            tvCallUser.setText(UserInfoHelper.getUserTitleName(String.valueOf(callOutUser.getUser_id()), SessionTypeEnum.P2P));
            ivAvater.loadBuddyAvatar(String.valueOf(callOutUser.getUser_id()));
            tvName.setText(UserInfoHelper.getUserTitleName(String.valueOf(callOutUser.getUser_id()), SessionTypeEnum.P2P));
        }
    }


    /**
     * 电话打入
     */
    private void callIn() {
        if (null != invitedEvent || !TextUtils.isEmpty(fromId)) {
            String accountId = invitedEvent != null ? invitedEvent.getFromAccountId() : fromId;
            ivUserIcon.loadBuddyAvatar(accountId);
            tvCallUser.setText(UserInfoHelper.getUserTitleName(String.valueOf(accountId), SessionTypeEnum.P2P));
            ivAvater.loadBuddyAvatar(accountId);
            tvName.setText(UserInfoHelper.getUserTitleName(String.valueOf(accountId), SessionTypeEnum.P2P));
            InviteParamBuilder invitedParam = null;
            List<String> userAccount = new ArrayList<>();
            if (invitedEvent != null) {
                invitedParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                        invitedEvent.getFromAccountId(), invitedEvent.getRequestId());
                userAccount.add(invitedEvent.getFromAccountId());
            } else {
                userAccount.add(fromId);
            }
            InviteParamBuilder finalInvitedParam = invitedParam;
            tvAccept.setOnClickListener(view -> {   //接听
                if (TextUtils.equals(matchType, ChatMsg.CALL_MATCH_INQUIRY)) {   //当时视频速配过来询问匹配时候调用
                    if (mChannelType == 1) {
                        getP().voiceAction(accountId, service.getUserId() + "", "yes");
                    } else {
                        getP().videoAction(accountId, service.getUserId() + "", "yes");
                    }
                }
                if (!ClickUtil.canOperate()) {
                    return;
                }
                getP().checkAcceptCheck(finalInvitedParam, accountId);
            });
            tvReject.setOnClickListener(view -> reject());
            InviteParamBuilder finalInvitedParam1 = invitedParam;
            NIMClient.getService(UserService.class).fetchUserInfo(userAccount).setCallback(new RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> param) {
                    if (isMatch) {
                        accept(finalInvitedParam1, accountId);
                    }
                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        } else {
            hungUpAndFinish();
            finish();
        }
    }


    private void reject() {
        if (TextUtils.equals(matchType, ChatMsg.CALL_MATCH_INQUIRY)) {
            String accountId = invitedEvent != null ? invitedEvent.getFromAccountId() : fromId;
            if (mChannelType == 1) {
                getP().voiceAction(accountId, service.getUserId() + "", "no");
            } else {
                getP().videoAction(accountId, service.getUserId() + "", "no");
            }
        }
        stopPlayer();
        ChatSoundPlayer.instance().play(ChatSoundPlayer.RingerTypeEnum.PEER_REJECT);
        if (invitedEvent != null) {
            InviteParamBuilder invitedParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                    invitedEvent.getFromAccountId(), invitedEvent.getRequestId());
            nertcVideoCall.reject(invitedParam, new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }

                @Override
                public void onFailed(int i) {
                    if (i == ResponseCode.RES_ETIMEOUT) {
                        ToastUtil.showToast(BaseApplication.getInstance(), "拒绝超时");
                    } else {
                        if (i == ResponseCode.RES_CHANNEL_NOT_EXISTS || i == ResponseCode.RES_INVITE_NOT_EXISTS ||
                                i == ResponseCode.RES_INVITE_HAS_REJECT || i == ResponseCode.RES_PEER_NIM_OFFLINE ||
                                i == ResponseCode.RES_PEER_PUSH_OFFLINE) {
                            finish();
                        }
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    finish();
                }
            });
        } else {
            getP().sendNotification(fromId, 1);
        }
        hungUpAndFinish();
        finish();
    }

    /**
     * 设置本地视频视图
     */
    private void setupLocalVideo() {
        mIsAccept = true;  //接听了语音
        stopPlayer();
        NERtc.getInstance().enableLocalAudio(true);
        if (mChannelType == 1) {   //语音
            if (localVideoView != null) {
                localVideoView.setVisibility(View.GONE);
            }
            NERtc.getInstance().enableLocalVideo(false);   //1
        } else {
            NERtcEx.getInstance().stopVideoPreview();
            if (mCallDir == 0) {  //拨打方
                if (!isFaceOn) {
                    if (localVideoView != null && nertcVideoCall != null) {
                        nertcVideoCall.setupLocalView(localVideoView);
                    }
                }
            } else {
                if (localVideoView != null && nertcVideoCall != null) {
                    nertcVideoCall.setupLocalView(localVideoView);
                }
            }
            if (localVideoView != null) {
                localVideoView.setVisibility(View.VISIBLE);
            }

            if (isFaceOn) {
                if (isChangeVideo) {
                    viewVideoView.setVisibility(View.VISIBLE);
                } else {
                    viewRemote.setVisibility(View.VISIBLE);
                }
            }
        }

        if (isOpenFace) {
            if (service.getUserInfo().getSex() == 2) {
                if (isChangeVideo) {
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                } else {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                }
            } else {
                if (isChangeVideo) {
                    viewRemote.setVisibility(View.VISIBLE);
                    ivCloseFace.setVisibility(View.VISIBLE);
                } else {
                    viewVideoView.setVisibility(View.VISIBLE);
                    ivSmallCloseFace.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (isChangeVideo) {
                viewVideoView.setVisibility(View.GONE);
            } else {
                viewRemote.setVisibility(View.GONE);
            }
        }
        //男方开启不露脸
        if (isFaceOn) {
            viewVideoView.setVisibility(View.VISIBLE);
            ivSmallCloseFace.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 接受通话
     */
    private void accept(InviteParamBuilder invitedParam, String userId) {
        ChatSoundPlayer.instance().stop();
        if (invitedParam != null)
            nertcVideoCall.accept(invitedParam, service.getUserId() + "", new JoinChannelCallBack() {
                @Override
                public void onJoinChannel(ChannelFullInfo channelFullInfo) {

                }

                @Override
                public void onJoinFail(String msg, int code) {

                }
            });
        else
            nertcVideoCall.accept(userId);
//        setupLocalVideo();
        rvCallChat.setVisibility(View.VISIBLE);
        llyBingCall.setVisibility(View.GONE);
        mLlIncome.setVisibility(View.GONE);
        if (mChannelType == 1) {   //语音
            rlyTopUserInfo.setVisibility(View.VISIBLE);
            Utils.runOnUiThreadDelayed(() -> {
                llyDialogOperation.setVisibility(View.VISIBLE);
                mTvCancel.setVisibility(View.VISIBLE);
            }, 1500);
        } else {  //视频
            mTvCancel.setVisibility(View.VISIBLE);
            Utils.runOnUiThreadDelayed(() -> {
                llyDialogOperation.setVisibility(View.VISIBLE);
                tvSpeaker.setVisibility(View.GONE);
                tvMute.setVisibility(View.GONE);
                rlyTopUserInfo.setVisibility(View.GONE);
                llUserInfo.setVisibility(View.VISIBLE);
//                    llVideoTime.setVisibility(View.VISIBLE);

            }, 1500);
        }
        startCount();
        getP().sendNotification(sessionId, 0);
        getP().getCurrentCoin(callId);
    }

    /**
     * 设置远程视频视图
     *
     * @param uid 远程用户Id
     */
    private void setupRemoteVideo(long uid) {
        videoUid = uid;
        if (!isOpenFace) {
            if (service.getUserInfo().getSex() == 2)
                viewRemote.setVisibility(View.GONE);
        } else {
            viewVideoView.setVisibility(View.VISIBLE);
        }
        if (nertcVideoCall != null) {
            if (isChangeVideo) {
                if (localVideoView != null) {
                    nertcVideoCall.setupRemoteView(localVideoView, uid);
                }
            } else {
                if (remoteVideoView != null) {
                    nertcVideoCall.setupRemoteView(remoteVideoView, uid);
                }

            }
        }

        if (isFaceOn) {
            if (videoView != null)
                videoView.setVisibility(View.GONE);
        } else {
            Utils.runOnUiThreadDelayed(() -> {
                if (videoView != null)
                    videoView.setVisibility(View.GONE);
            }, 2000);
        }
    }


    private void hungUpAndFinish() {
        Utils.runOnUiThread(() -> {
            stopPlayer();
            if (nertcVideoCall != null) {
                nertcVideoCall.hangup();
                nertcVideoCall.removeDelegate(nertcCallingDelegate);
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
//        /**
//         * 在有视频通话时退到后台时直接关闭
//         */
//        if (mChannelType == 2) {
////            if (!mIsSendMessage && mCallDir == 0) {
////                int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
////                ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, time, 1, 2, isMatch, messageListener);
////                mIsSendMessage = true;
////            }
//            isSelfEnd = true;
//            hungUpAndFinish();
//            handler.postDelayed(this::finish, DELAY_TIME);
//        } else {
////            if (isFastClick()) return;
////                isSelfEnd = true;
////                int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
////                if (!mIsSendMessage) {
////                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), String.valueOf(callOutUser.getUser_id()), service.getChannelId(), callId, time, 0, 2, isMatch, messageListener);
////                    mIsSendMessage = true;
////                }
////                nertcVideoCall.cancel();
////            finish();
//        }


    }

    @Override
    public void onPause() {
        super.onPause();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
    }


    @Override
    protected void onResume() {
        super.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
    }


    private void release() {
        stopPlayer();
        if (countBackUtils != null)
            countBackUtils.destroy();
        NERtc.getInstance().leaveChannel();
        if (mChannelType == 2) {
            if (remoteVideoView != null) {
                remoteVideoView.release();
                remoteVideoView = null;
            }
            if (localVideoView != null) {
                localVideoView.release();
                localVideoView = null;
            }
            if (videoView != null) {
                videoView.release();
                videoView = null;
            }
        }
        if (callMessage != null) {
            callMessage.clear();
            callMessage = null;
        }
        if (audioTime != null) {
            audioTime.stop();
            audioTime = null;
        }
        if (videoTime != null) {
            videoTime.stop();
            videoTime = null;
        }
        if (giftRoot != null) {
            giftRoot.setPlayGiftEndListener(null);
            giftRoot = null;
        }
        if (mPlayerView != null) {
            mPlayerView.stopPlay();
        }
        invitedEvent = null;
        SPUtils.remove(Constants.INVITED_EVENT);
        if (nertcCallingDelegate != null && nertcVideoCall != null) {
            nertcVideoCall.removeDelegate(nertcCallingDelegate);
            nertcVideoCall.releaseNERtc();
            nertcCallingDelegate = null;
            nertcVideoCall = null;
        }
        if (mChannelType == 2) {
            NERtcEx.getInstance().stopVideoPreview();
        }
        ChatSoundPlayer.instance().stop();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
        if (runnable2 != null) {
            handler.removeCallbacks(runnable2);
            runnable2 = null;
        }
        if (mChannelType == 2) {
            runOnUiThread(this::destroyFU);
        }
        handler.removeCallbacksAndMessages(null);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(messageReceiverObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, false);
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, false);
        if (mAudioManager != null) {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    if (isMatch)   // 如果语音视频速配关闭速配页面
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CLOSE_MATCH));
                    smallWindow();
                }
            }
        }
    }

    @Override
    public CallPresenter initPresenter() {
        return new CallPresenter();
    }


    @Override
    public void getChatUserInfo(List<UserInfo> infoList) {
        System.out.println("userinfo----:" + infoList.toString());
        //显示来电/拨出用户的个人信息
        if (infoList.size() == 0) {
            return;
        }
        System.out.println("--------");
        UserInfo userInfo = infoList.get(0);

    }


    private boolean mIsFirstAlter = true;  //是否第一次弹窗了

    @Override
    public void getCallUserInfoSuccess(CallStatusInfo data) {
        mNum = 0;
        callTotalTime = data.total_second;
        if (data.talk_status == 2 && data.is_pop_recharge) {   // 2 不足1分钟  需要弹窗提示余额不足  3 余额不足直接挂断 4隐藏充值
            mClRecharge.setVisibility(View.VISIBLE);
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && mIsFirstAlter)
                if (service.getConfigInfo().sys.check_order_is_show_recharge == 1) {
                    getP().showRechargePop(VideoCallActivity.this, 3, 1, sessionId);
                    mIsFirstAlter = false;
                }
        } else if (data.talk_status == 3) {
            if (isMatch)   // 如果语音视频速配关闭速配页面
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CLOSE_MATCH));
            closeType = 4;
            automaticEnd();
        } else if (data.talk_status == 4) {
            mClRecharge.setVisibility(View.GONE);
        }
        handler.postDelayed(runnable, 6000);
    }

    @Override
    public void checkAcceptCheckSuccess(InviteParamBuilder paramBuilder, String
            account, ChatMsg data) {
        if (data != null && TextUtils.equals("accept_and_recharge", data.cmd_type)) {
            getP().showRechargePop(VideoCallActivity.this, 3, 1, account);
            return;
        }
        accept(paramBuilder, account);
    }

    @Override
    public void getCallUserInfoNoNet() {
        handler.postDelayed(runnable, 6000);
    }


    Runnable runnable = () -> getP().getCurrentCoin(callId);

    Runnable runnable2 = () -> getP().getCurrentAgainCoin(callId);


    private void automaticEnd() {
        int time = mChannelType == 1 ? TimeUtil.getChronometerSeconds(audioTime) : TimeUtil.getChronometerSeconds(videoTime);
        isSelfEnd = true;
        int finTime = Math.max(time, 1);
        getP().sendNotification(sessionId, 4);
        if (!mIsSendMessage && mCallDir == 0) {
            ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_END, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, finTime, 0, closeType, isMatch, messageListener);
            mIsSendMessage = true;
        }
        hungUpAndFinish();
        finish();
    }


    /**
     * 执行挂断操作
     */
    @Override
    public void hungUp() {
        mNum++;
        handler.postDelayed(runnable, 6000);
        if (mNum >= 2) {
            if (isMatch)   // 如果语音视频速配关闭速配页面
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CLOSE_MATCH));
            hungUpAndFinish();
            finish();
        }
    }

    @Override
    public void getCallUserInfoNoNetAgain() {
        handler.postDelayed(runnable2, 6000);
    }

    @Override
    public void hungUpAgain() {
        mNum++;
        handler.postDelayed(runnable2, 6000);
        if (mNum >= 5) {
            if (isMatch)   // 如果语音视频速配关闭速配页面
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CLOSE_MATCH));
            hungUpAndFinish();
            finish();
        }
    }

    private void destroyFU() {
        if (mHandler == null || mFURenderer == null) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mHandler.post(() -> {
            isFirstInit = true;
            mFURenderer.onSurfaceDestroyed();
            countDownLatch.countDown();
        });
        try {
            mHandler = null;
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void setVideoCallback() {
        //返回 I420数据 会影响性能
        //是否是双输入
        // 1 双输入 2 单texture 3 单buffer
        int inputType = 2;
        Log.e("----neRtcVideoFrame-0-", "");
        boolean needI420 = inputType != 2;
        NERtcEx.getInstance().setVideoCallback(neRtcVideoFrame -> {

            if (isFirstInit) {
                initFace();
                isFirstInit = false;
                mHandler = new Handler(Looper.myLooper());
                mFURenderer.onSurfaceCreated();
                return false;
            }

            int texId = 0;
            if (inputType == 1) {
                texId = mFURenderer.onDrawFrameDualInput(neRtcVideoFrame.data, neRtcVideoFrame.textureId, neRtcVideoFrame.width, neRtcVideoFrame.height);
            } else if (inputType == 2) {
                texId = mFURenderer.onDrawFrameSingleInput(neRtcVideoFrame.textureId, neRtcVideoFrame.width, neRtcVideoFrame.height);
            } else if (inputType == 3) {
                texId = mFURenderer.onDrawFrameSingleInput(neRtcVideoFrame.data, neRtcVideoFrame.width, neRtcVideoFrame.height, IFURenderer.INPUT_FORMAT_I420_BUFFER);
            }
            if (mSkipFrame-- > 0) {
                return false;
            }
            if (neRtcVideoFrame.format == NERtcVideoFrame.Format.TEXTURE_OES) {
                neRtcVideoFrame.format = NERtcVideoFrame.Format.TEXTURE_RGB;
            }

            if (neRtcVideoFrame.data.length != neRtcVideoFrame.width * neRtcVideoFrame.height * 1.5) {
                texId = neRtcVideoFrame.textureId;
            }

//            if (texId == 0) {
//                texId = neRtcVideoFrame.textureId;
//            }
            neRtcVideoFrame.textureId = texId;
            return true;
        }, needI420);
    }

    @Override
    public void showGiftAnimation(GiftDto bean) {

        giftRoot.setVisibility(View.VISIBLE);
        if (null == giftViewListener) {
            giftViewListener = new GiftRootLayout.GiftRootListener() {
                @Override
                public void showGiftInfo(GiftDto giftBean) {
                }

                @Override
                public void showGiftAmin(GiftDto giftBean, int index) {

                }

                @Override
                public void hideGiftAmin(int index, int giftId) {

                }
            };
        }
        giftRoot.setPlayGiftEndListener(giftViewListener);
        giftRoot.loadGift(bean);
    }

    @Override
    public void cancel() {
        if (mCallDir == 0) {
            cancelCall();
        } else {
            reject();
        }
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {
        if (userInfo.getAge() > 0) {
            mtvGenderAge.setBackground(Utils.getDrawable(userInfo.getSex() == 1 ? R.drawable.shape_blue_alpha30 : R.drawable.shape_pink_alpha30));
            mtvGenderAge.setText(userInfo.getAge() + "");
            mtvGenderAge.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(userInfo.getSex() == 1 ? R.drawable.ic_boy : R.drawable.ic_girl), null, null, null);
            mtvGenderAge.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(getFiled(userInfo.info,"hometown"))) {
            mtvCity.setText(getFiled(userInfo.info,"hometown").split(" ")[0]);
            mtvCity.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(getFiled(userInfo.info,"star_sign"))) {
            mtvConstellation.setText(getFiled(userInfo.info,"star_sign"));
            mtvConstellation.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(getFiled(userInfo.info,"job"))) {
            mtvJob.setText(getFiled(userInfo.info,"job"));
            mtvJob.setVisibility(View.VISIBLE);
        }
        if(mChannelType == 2) {
            if (userInfo.getAge() > 0) {
                mtvVideoGenderAge.setBackground(Utils.getDrawable(userInfo.getSex() == 1 ? R.drawable.shape_blue_alpha30 : R.drawable.shape_pink_alpha30));
                mtvVideoGenderAge.setText(userInfo.getAge() + "");
                mtvVideoGenderAge.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(userInfo.getSex() == 1 ? R.drawable.ic_boy : R.drawable.ic_girl), null, null, null);
                mtvVideoGenderAge.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(getFiled(userInfo.info,"hometown"))) {
                mtvVideoCity.setText(getFiled(userInfo.info,"hometown").split(" ")[0]);
                mtvVideoCity.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(getFiled(userInfo.info,"star_sign"))) {
                mtvVideoConstellation.setText(getFiled(userInfo.info,"star_sign"));
                mtvVideoConstellation.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(getFiled(userInfo.info,"job"))) {
                mtvVideoJob.setText(getFiled(userInfo.info,"job"));
                mtvVideoJob.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getFiled(List<UserInfo.BaseInfo> list,String field){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).name.equals(field)){
                return list.get(i).value;
            }
        }
        return null;
    }

    /**
     * 启动倒计时
     */
    private void startCount() {
        //电话接通之后先试试礼物按钮
        ivGift.setVisibility(View.VISIBLE);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                if (mIsSpeak && timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void onFinish() {
                toastTip("网络异常，请稍后重试");
                NERtcEx.getInstance().uploadSdkInfo();
                if (!mIsSendMessage && mCallDir == 0) {
                    ChatMsgUtil.sendCustomMessage(mChannelType, ChatMsg.EVENT_ERROR, String.valueOf(service.getUserId()), sessionId, service.getChannelId(), callId, 0, 0, 2, isMatch, messageListener);
                    mIsSendMessage = true;
                }
                hungUpAndFinish();
                finish();
            }
        };
        timer.start();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    public void onAccelerometerChanged(float x, float y, float z) {
    }


}
