package com.tftechsz.im.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.netease.lava.nertc.sdk.NERtc;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.avsignalling.model.MemberInfo;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.TeamAdapter;
import com.tftechsz.im.model.TeamG2Item;
import com.tftechsz.im.mvp.iview.ITeamCallView;
import com.tftechsz.im.mvp.presenter.TeamCallPresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.entity.LiveTokenDto;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.model.CallParams;
import com.tftechsz.common.nim.model.JoinChannelCallBack;
import com.tftechsz.common.nim.model.NERTCCallingDelegate;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.ProfileManager;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.tftechsz.im.model.TeamG2Item.TYPE.TYPE_DATA;

@Route(path = ARouterApi.ACTIVITY_TEAM_CALL)
public class TeamCallActivity extends BaseMvpActivity<ITeamCallView, TeamCallPresenter> implements ITeamCallView {
    // CONST
    private static final String TAG = "TeamAVChat";
    private static final String KEY_TEAM_ID = "teamid";
    private static final String KEY_ACCOUNTS = CallParams.INVENT_USER_IDS;
    private static final String KEY_TNAME = "teamName";
    private static final int AUTO_REJECT_CALL_TIMEOUT = 45 * 1000;
    private static final int CHECK_RECEIVED_CALL_TIMEOUT = 45 * 1000;
    private static final int MAX_SUPPORT_ROOM_USERS_COUNT = 3;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x100;
    private NERTCVideoCall nertcVideoCall;
    // DATA
    private String teamId;
    private String chatId;
    private ArrayList<String> accounts;
    private boolean receivedCall;
    private boolean destroyRTC;
    private String teamName;
    private String groupId;//群Id

    // CONTEXT
    private Handler mainHandler;

    // LAYOUT
    private View callLayout;
    private View surfaceLayout;
    private NERtcVideoView surfaceCenter, surfaceBoy, surfaceGirl;

    // VIEW
    private RecyclerView recyclerView;
    private TeamAdapter adapter;
    private List<TeamG2Item> data;
    private View voiceMuteButton;

    // TIMER
    private Timer timer;
    private int seconds;
    private TextView timerText;
    private Runnable autoRejectTask;

    // CONTROL STATE
    boolean videoMute = false;
    boolean microphoneMute = false;
    boolean speakerMode = true;

    private UserProviderService service;
    // AVCAHT OBSERVER
    NERTCCallingDelegate nertcCallingDelegate;

    //invited
    private String invitedChannelId;
    private String invitedRequestId;
    private String invitedAccid;

    public static void startActivity(Context context, boolean receivedCall, String teamId, ArrayList<String> accounts, String teamName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(context, TeamCallActivity.class);
        intent.putExtra(CallParams.INVENT_CALL_RECEIVED, receivedCall);
        intent.putExtra(KEY_TEAM_ID, teamId);
        intent.putExtra(KEY_ACCOUNTS, accounts);
        intent.putExtra(KEY_TNAME, teamName);
        intent.putExtra(CallParams.TEAM_CHAT_GROUP_ID, teamId);
        context.startActivity(intent);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_team_avchat;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        dismissKeyguard();
        onInit();
        onIntent();
        findLayouts();
        showViews();

        //TODO
        // 音视频权限检查
        // 启动音视频
        startRtc();

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 禁止自动锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "TeamAVChatActivity onDestroy");

        if (timer != null) {
            timer.cancel();
        }

        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        hangup(); // 页面销毁的时候要保证离开房间，rtc释放。
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, false);
        if (nertcCallingDelegate != null) {
            NERTCVideoCall.sharedInstance().removeDelegate(nertcCallingDelegate);
        }

    }

    @Override
    public TeamCallPresenter initPresenter() {
        return new TeamCallPresenter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        callInstance();
        super.onSaveInstanceState(outState);
        Log.i(TAG, "TeamAVChatActivity onSaveInstanceState");
    }

    private synchronized void callInstance() {
        Utils.runOnUiThread(() -> {
            if (nertcVideoCall == null)
                nertcVideoCall = NERTCVideoCall.sharedInstance();
            nertcVideoCall.initNERtc(NERTCVideoCallImpl.CALL, null);
            if (nertcCallingDelegate != null) {
                nertcVideoCall.removeDelegate(nertcCallingDelegate);
            }
            nertcVideoCall.addDelegate(nertcCallingDelegate);
        });
    }

    /**
     * ************************************ 初始化 ***************************************
     */

    // 设置窗口flag，亮屏并且解锁/覆盖在锁屏界面上
    private void dismissKeyguard() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    private void onInit() {
        mainHandler = new Handler(this.getMainLooper());
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, true);
    }

    private void onIntent() {
        Intent intent = getIntent();
        receivedCall = intent.getBooleanExtra(CallParams.INVENT_CALL_RECEIVED, false);
        teamId = intent.getStringExtra(KEY_TEAM_ID);
        accounts = (ArrayList<String>) intent.getSerializableExtra(KEY_ACCOUNTS);
        accounts = accounts == null ? new ArrayList<String>(0) : accounts;
        teamName = intent.getStringExtra(KEY_TNAME);
        invitedChannelId = intent.getStringExtra(CallParams.INVENT_CHANNEL_ID);
        invitedRequestId = intent.getStringExtra(CallParams.INVENT_REQUEST_ID);
        invitedAccid = intent.getStringExtra(CallParams.INVENT_FROM_ACCOUNT_ID);
        groupId = intent.getStringExtra(CallParams.TEAM_CHAT_GROUP_ID);
        if (!receivedCall)
            accounts.add(service.getUserId() + "");
        Log.i(TAG, "onIntent teamId=" + teamId
                + ", receivedCall=" + receivedCall + ", accounts=" + accounts.size() + ", teamName = " + teamName + ", invitedChannelId = " + invitedChannelId
                + ", invitedRequestId = " + invitedRequestId + ", invitedAccid = " + invitedAccid);
    }

    private void findLayouts() {
        callLayout = findViewById(R.id.team_avchat_call_layout);
        surfaceLayout = findViewById(R.id.team_avchat_surface_layout);
        voiceMuteButton = findViewById(R.id.avchat_shield_user);
        surfaceCenter = findViewById(R.id.surface_center);
        surfaceBoy = findViewById(R.id.surface_boy);
        surfaceGirl = findViewById(R.id.surface_girl);
    }

    /**
     * ************************************ 主流程 ***************************************
     */

    private void showViews() {
        if (receivedCall) {
            showReceivedCallLayout();
        } else {
            showSurfaceLayout();
        }
    }

    /*
     * 接听界面
     */
    private void showReceivedCallLayout() {
        callLayout.setVisibility(View.VISIBLE);
        // 提示
        TextView textView = (TextView) callLayout.findViewById(R.id.received_call_tip);

        final String tipText = TextUtils.isEmpty(teamName) ? "你有一条视频通话" : teamName + "的视频通话";
        textView.setText(tipText);
        //邀请参数
        final InviteParamBuilder inviteParam = new InviteParamBuilder(invitedChannelId,
                invitedAccid, invitedRequestId);
        // 拒绝
        callLayout.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NERTCVideoCall.sharedInstance().reject(inviteParam, new RequestCallback<Void>() {
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
            }
        });

        // 接听
        callLayout.findViewById(R.id.receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveTokenDto data = new LiveTokenDto();
                data.room_token = MMKVUtils.getInstance().decodeString("room_token");
                data.channel_name = MMKVUtils.getInstance().decodeString("room_name");
                NERTCVideoCall.sharedInstance().setTokenService((uid, callback) -> {
                    callback.onSuccess(data);
                });
                if (ProfileManager.getInstance().getUserModel() == null) {
                    return;
                }
                NERTCVideoCall.sharedInstance().accept(inviteParam, ProfileManager.getInstance().getUserModel().imAccid, new JoinChannelCallBack() {
                    @Override
                    public void onJoinChannel(ChannelFullInfo channelFullInfo) {
                        if (channelFullInfo != null && !TextUtils.isEmpty(channelFullInfo.getChannelId())) {
                            for (MemberInfo member : channelFullInfo.getMembers()) {
                                if (ProfileManager.getInstance().getUserModel() != null && TextUtils.equals(member.getAccountId(), ProfileManager.getInstance().getUserModel().imAccid)) {
                                    ProfileManager.getInstance().getUserModel().g2Uid = member.getUid();
                                    break;
                                }
                            }

                            Log.i(TAG, "join room success, chatId=" + chatId);
                            chatId = channelFullInfo.getChannelId();
                            onJoinRoomSuccess();
                        } else {
                            int code = -1;
                            onJoinRoomFailed(ResponseCode.RES_ENONEXIST, null);
                            Log.i(TAG, "join room failed, code=" + code);
                        }
                    }

                    @Override
                    public void onJoinFail(String msg, int code) {
                        onJoinRoomFailed(ResponseCode.RES_ENONEXIST, null);
                        Log.i(TAG, "join room failed, code=" + code + ", msg=" + msg);
                    }
                });
                cancelAutoRejectTask();
                callLayout.setVisibility(View.GONE);
                showSurfaceLayout();
            }
        });

        startAutoRejectTask();
    }

    /*
     * 通话界面
     */
    private void showSurfaceLayout() {
        // 列表
        surfaceLayout.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) surfaceLayout.findViewById(R.id.recycler_view);
        initRecyclerView();

        // 通话计时
        timerText = (TextView) surfaceLayout.findViewById(R.id.timer_text);

        // 控制按钮
        ViewGroup settingLayout = (ViewGroup) surfaceLayout.findViewById(R.id.avchat_setting_layout);
        for (int i = 0; i < settingLayout.getChildCount(); i++) {
            View v = settingLayout.getChildAt(i);
            if (v instanceof RelativeLayout) {
                ViewGroup vp = (ViewGroup) v;
                if (vp.getChildCount() == 1) {
                    vp.getChildAt(0).setOnClickListener(settingBtnClickListener);
                }
            }
        }
    }

    /**
     * ************************************ 音视频事件 ***************************************
     */

    private void startRtc() {
        // rtc init
        Log.i(TAG, "start rtc done");

        // state observer
        if (nertcCallingDelegate != null) {
            NERTCVideoCall.sharedInstance().removeDelegate(nertcCallingDelegate);
        }
        nertcCallingDelegate = new NERTCCallingDelegate() {


            @Override
            public void onError(int errorCode, String userId, String errorMsg) {
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
                onAVChatUserJoined(userId, userId + "");
            }

            @Override
            public void onJoinChannel(long channelId) {

            }

            @Override
            public void onUserHangup(long userId) {

            }

            @Override
            public void onRejectByUserId(String userId) {
                onSignalingUserReject(userId);
            }

            @Override
            public void onAcceptByUserId(String userId) {

            }

            @Override
            public void onUserBusy(String userId) {
                onSignalingUserReject(userId);
            }

            @Override
            public void onUserLeave(String userId, String reason) {
                onAVChatUserLeave(userId);
            }

            @Override
            public void onCancelByUserId(String userId) {
                if (!isDestroyed() && receivedCall) {
                    ToastUtils.showLong("对方取消");
                    hangup();
                    finish();

                }
            }

            @Override
            public void onJoinRoomFailed(int code) {

            }

            @Override
            public void onError(int code) {

            }

            @Override
            public void onCameraAvailable(long userId, boolean isVideoAvailable) {

            }

            @Override
            public void onUserVideoStop(long userId) {

            }

            @Override
            public void onAudioAvailable(long userId, boolean isAudioAvailable) {

            }

            @Override
            public void timeOut(int type) {

            }

            @Override
            public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {

            }

        };
        NERTCVideoCall.sharedInstance().addDelegate(nertcCallingDelegate);
        Log.i(TAG, "observe rtc state done");

        if (!receivedCall) {
            // join
            Utils.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
//                    onJoinRoomSuccess();
                    LogUtil.e("==============", groupId + "====");
                    NERTCVideoCall.sharedInstance().groupCall(accounts, groupId, service.getUserId() + "", ChannelType.VIDEO, "", new JoinChannelCallBack() {
                        @Override
                        public void onJoinChannel(ChannelFullInfo channelFullInfo) {
                            if (channelFullInfo != null && !TextUtils.isEmpty(channelFullInfo.getChannelId())) {
                                for (MemberInfo member : channelFullInfo.getMembers()) {
                                    if (TextUtils.equals(member.getAccountId(), service.getUserId() + "")) {
                                        ProfileManager.getInstance().getUserModel().g2Uid = member.getUid();
                                        break;
                                    }
                                }
                                Log.i(TAG, "join room success, chatId=" + chatId);
                                chatId = channelFullInfo.getChannelId();
                                onJoinRoomSuccess();
                            } else {
                                int code = -1;
                                onJoinRoomFailed(code, null);
                                Log.i(TAG, "join room failed, code=" + code);
                            }
                        }

                        @Override
                        public void onJoinFail(String msg, int code) {
                            onJoinRoomFailed(code, null);
                            Log.i(TAG, "join room failed, code=" + code + ", msg=" + msg);
                        }
                    });
                }
            }, 1000);


        }
        Log.i(TAG, "start join room");
    }

    private void onJoinRoomSuccess() {
        startTimer();
        startLocalPreview();
        startTimerForCheckReceivedCall();
        Log.i(TAG, "team onJoinRoomSuccess...");
    }

    private void onJoinRoomFailed(int code, Throwable e) {
        if (code == ResponseCode.RES_ENONEXIST) {
            showToast("加入房间失败");
        } else {
            showToast("join room failed, code=" + code + ", e=" + (e == null ? "" : e.getMessage()));
        }
        hangup();
        finish();
    }


    public void onAVChatUserJoined(long uid, String accId) {
        int sex = 1;
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(accId);
        if (userInfo != null && userInfo.getGenderEnum() != null) {
            sex = userInfo.getGenderEnum().getValue();
        }
        if (!receivedCall) {  //拨打方
            if (sex == 1) {  //男
                data.get(1).state = TeamG2Item.STATE.STATE_PLAYING;
                data.get(1).videoLive = true;
                NERTCVideoCall.sharedInstance().setupRemoteView(surfaceBoy, uid);
            } else {  //女
                data.get(2).state = TeamG2Item.STATE.STATE_PLAYING;
                data.get(2).videoLive = true;
                NERTCVideoCall.sharedInstance().setupRemoteView(surfaceGirl, uid);
            }
        } else {
            data.get(0).state = TeamG2Item.STATE.STATE_PLAYING;
            data.get(0).videoLive = true;
            NERTCVideoCall.sharedInstance().setupRemoteView(surfaceCenter, uid);
        }

        if (getItemIndex(accId) < 0 && receivedCall) {
            data.set(2, new TeamG2Item(TYPE_DATA, teamId, accId));
            adapter.notifyItemChanged(2);
        }
        int index = getItemIndex(accId);
        if (index >= 0) {
            TeamG2Item item = data.get(index);
            NERtcVideoView surfaceView = adapter.getViewHolderSurfaceView(item);
            LogUtil.e("================================", item + "======" + surfaceView);
            if (surfaceView != null) {
                item.state = TeamG2Item.STATE.STATE_PLAYING;
                item.videoLive = true;
                item.uid = uid;
                adapter.notifyItemChanged(index);
                NERTCVideoCall.sharedInstance().setupRemoteView(surfaceView, uid);
            }
        }
        LogUtil.e("================================", index + "======" + data.size());
        updateAudioMuteButtonState();

        Log.i(TAG, "on user joined, account=" + accId);
    }

    public void onAVChatUserLeave(String account) {
        int index = getItemIndex(account);
        if (index >= 0) {
            TeamG2Item item = data.get(index);
            item.state = TeamG2Item.STATE.STATE_HANGUP;
            item.volume = 0;
            adapter.notifyItemChanged(index);
        }
        updateAudioMuteButtonState();

        Log.i(TAG, "on user leave, account=" + account);
    }

    public void onSignalingUserReject(String accountId) {
        int index = getItemIndex(accountId);
        if (index >= 0) {
            TeamG2Item item = data.get(index);
            item.state = TeamG2Item.STATE.STATE_REJECTED;
            item.volume = 0;
            adapter.notifyItemChanged(index);
        }
        updateAudioMuteButtonState();

        Log.i(TAG, "on user reject, account=" + accountId);
    }

    private void startLocalPreview() {
        if (!receivedCall) { //拨打方
            NERTCVideoCall.sharedInstance().setupLocalView(surfaceCenter);
            NERtc.getInstance().startVideoPreview();
            NERtc.getInstance().enableLocalVideo(true);
            data.get(0).state = TeamG2Item.STATE.STATE_PLAYING;
            data.get(0).videoLive = true;
        } else {  //接收方
            if (service.getUserInfo().isBoy()) {
                NERTCVideoCall.sharedInstance().setupLocalView(surfaceBoy);
                NERtc.getInstance().startVideoPreview();
                NERtc.getInstance().enableLocalVideo(true);
                data.get(1).state = TeamG2Item.STATE.STATE_PLAYING;
                data.get(1).videoLive = true;
            } else {
                NERTCVideoCall.sharedInstance().setupLocalView(surfaceGirl);
                NERtc.getInstance().startVideoPreview();
                NERtc.getInstance().enableLocalVideo(true);
                data.get(2).state = TeamG2Item.STATE.STATE_PLAYING;
                data.get(2).videoLive = true;
            }
        }

//        LogUtil.e("=============", "执行了啊12");
//
//
//
//
//        if (data.size() > 1 && data.get(0).account.equals(ProfileManager.getInstance().getUserModel().imAccid)) {
//            NERtcVideoView surfaceView = adapter.getViewHolderSurfaceView(data.get(0));
//            LogUtil.e("=============", "执行了啊12" + ProfileManager.getInstance().getUserModel().imAccid + "=====" + data.get(0).account + surfaceView);
//            if (surfaceView != null) {
//                NERTCVideoCall.sharedInstance().setupLocalView(surfaceView);
//                NERtc.getInstance().startVideoPreview();
//                NERtc.getInstance().enableLocalVideo(true);
//                data.get(0).state = TeamG2Item.STATE.STATE_PLAYING;
//                data.get(0).videoLive = true;
//                adapter.notifyItemChanged(0);
//                LogUtil.e("=============", "执行了啊12");
//            }
//        }
//        LogUtil.e("=============", "执行了啊" + ProfileManager.getInstance().getUserModel().imAccid + "=====" + data.get(0).account);
    }

    /**
     * ************************************ 音视频状态 ***************************************
     */

    private void updateSelfItemVideoState(boolean live) {
        if (ProfileManager.getInstance().getUserModel() == null) {
            return;
        }
        int index = getItemIndex(ProfileManager.getInstance().getUserModel().imAccid);
        if (index >= 0) {
            TeamG2Item item = data.get(index);
            item.videoLive = live;
            adapter.notifyItemChanged(index);
        }
    }

    private void hangup() {
        if (destroyRTC) {
            return;
        }
        try {
            NERTCVideoCall.sharedInstance().leave(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }

                @Override
                public void onFailed(int i) {
                    Log.e(TAG, "leave failed code = " + i);
                }

                @Override
                public void onException(Throwable throwable) {
                    Log.e(TAG, "leave failed onException", throwable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyRTC = true;
        Log.i(TAG, "destroy rtc & leave room");
    }

    /**
     * ************************************ 定时任务 ***************************************
     */

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
        timerText.setText("00:00");
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            seconds++;
            int m = seconds / 60;
            int s = seconds % 60;
            final String time = String.format(Locale.CHINA, "%02d:%02d", m, s);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timerText.setText(time);
                }
            });
        }
    };

    private void startTimerForCheckReceivedCall() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                for (TeamG2Item item : data) {
                    if (item.type == TYPE_DATA && item.state == TeamG2Item.STATE.STATE_WAITING) {
                        item.state = TeamG2Item.STATE.STATE_END;
                        adapter.notifyItemChanged(index);
                    }
                    index++;
                }
                checkAllHangUp();
            }
        }, CHECK_RECEIVED_CALL_TIMEOUT);
    }

    private void startAutoRejectTask() {
        if (autoRejectTask == null) {
            autoRejectTask = new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            };
        }

        mainHandler.postDelayed(autoRejectTask, AUTO_REJECT_CALL_TIMEOUT);
    }

    private void cancelAutoRejectTask() {
        if (autoRejectTask != null) {
            mainHandler.removeCallbacks(autoRejectTask);
        }
    }

    /*
     * 除了所有人都没接通，其他情况不做自动挂断
     */
    private void checkAllHangUp() {
        for (TeamG2Item item : data) {
            if (item.account != null && ProfileManager.getInstance().getUserModel() != null &&
                    !TextUtils.equals(item.account, ProfileManager.getInstance().getUserModel().imAccid) &&
                    item.state != TeamG2Item.STATE.STATE_END) {
                return;
            }
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hangup();
                finish();
            }
        }, 200);
    }

    /**
     * ************************************ 点击事件 ***************************************
     */

    private View.OnClickListener settingBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.avchat_switch_camera) {// 切换前后摄像头
                NERTCVideoCall.sharedInstance().switchCamera();
            } else if (i == R.id.avchat_enable_video) {// 视频
                videoMute = !videoMute;
                NERTCVideoCall.sharedInstance().enableCamera(!videoMute);
//                v.setBackgroundResource(videoMute ? R.drawable.t_avchat_camera_mute_selector : R.drawable.t_avchat_camera_selector);
                updateSelfItemVideoState(!videoMute);
            } else if (i == R.id.avchat_enable_audio) {// 麦克风开关
                NERTCVideoCall.sharedInstance().setMicMute(microphoneMute = !microphoneMute);
//                v.setBackgroundResource(microphoneMute ? R.drawable.t_avchat_microphone_mute_selector : R.drawable.t_avchat_microphone_selector);
            } else if (i == R.id.avchat_volume) {// 听筒扬声器切换
                showIncompleteFeatureToast("听筒扬声器切换");
            } else if (i == R.id.avchat_shield_user) {// 屏蔽用户音频
                disableUserAudio();
            } else if (i == R.id.hangup) {// 挂断
                hangup();
                finish();

            }
        }
    };

    private void updateAudioMuteButtonState() {
        boolean enable = false;
        for (TeamG2Item item : data) {
            if (item.state == TeamG2Item.STATE.STATE_PLAYING && ProfileManager.getInstance().getUserModel() != null &&
                    !TextUtils.equals(ProfileManager.getInstance().getUserModel().imAccid, item.account)) {
                enable = true;
                break;
            }
        }
        voiceMuteButton.setEnabled(enable);
        voiceMuteButton.invalidate();
    }

    private void disableUserAudio() {
        // TODO G2
        showIncompleteFeatureToast("屏蔽用户音频");
//        List<Pair<String, Boolean>> voiceMutes = new ArrayList<>();
//        for (TeamG2Item item : data) {
//            if (item.state == TeamG2Item.STATE.STATE_PLAYING &&
//                    !SDKCache.getAccount().equals(item.account)) {
//                voiceMutes.add(new Pair<>(item.account, AVChatManager.getInstance().isRemoteAudioMuted(item.account)));
//            }
//        }
//        TeamAVChatVoiceMuteDialog dialog = new TeamAVChatVoiceMuteDialog(this, teamId, voiceMutes);
//        dialog.setTeamVoiceMuteListener(new TeamAVChatVoiceMuteDialog.TeamVoiceMuteListener() {
//            @Override
//            public void onVoiceMuteChange(List<Pair<String, Boolean>> voiceMuteAccounts) {
//                if (voiceMuteAccounts != null) {
//                    for (Pair<String, Boolean> voiceMuteAccount : voiceMuteAccounts) {
//                        AVChatManager.getInstance().muteRemoteAudio(voiceMuteAccount.first, voiceMuteAccount.second);
//                    }
//                }
//            }
//        });
//        dialog.show();
    }

    private void showIncompleteFeatureToast(String feature) {
        Toast.makeText(TeamCallActivity.this, String.format("该功能暂未实现：%s", feature), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        // 屏蔽BACK
    }

    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        } else {
            super.finish();
        }
    }

    private int count = 0;

    /**
     * 命令消息接收观察者
     */
    private final Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!TextUtils.isEmpty(message.getContent()) && message.getContent().contains("groupId")) {
                JSONObject json = JSON.parseObject(message.getContent());
                String groupId = json.getString("groupId");
                count++;
                if (count >= 3) return;
                if (!TextUtils.isEmpty(groupId)) {
                    accounts.add(message.getSessionId());
                    data.set(count, new TeamG2Item(TYPE_DATA, teamId, message.getSessionId()));
                    adapter.notifyItemChanged(count);
                    if (ProfileManager.getInstance().getUserModel() == null) {
                        return;
                    }
                    NERTCVideoCall.sharedInstance().groupCall(accounts, groupId, ProfileManager.getInstance().getUserModel().imAccid, ChannelType.VIDEO, "invite", new JoinChannelCallBack() {
                        @Override
                        public void onJoinChannel(ChannelFullInfo channelFullInfo) {
//                            if (channelFullInfo != null && !TextUtils.isEmpty(channelFullInfo.getChannelId())) {
//                                for (MemberInfo member : channelFullInfo.getMembers()) {
//                                    if (TextUtils.equals(member.getAccountId(), ProfileManager.getInstance().getUserModel().imAccid)) {
//                                        ProfileManager.getInstance().getUserModel().g2Uid = member.getUid();
//                                        break;
//                                    }
//                                }
//
//                                Log.i(TAG, "join room success, chatId=" + chatId);
//                                chatId = channelFullInfo.getChannelId();
////                                onJoinRoomSuccess();
//                                startLocalPreview();
//                                startTimerForCheckReceivedCall();
//                            } else {
//                                int code = -1;
//                                onJoinRoomFailed(code, null);
//                                Log.i(TAG, "join room failed, code=" + code);
//                            }
                        }

                        @Override
                        public void onJoinFail(String msg, int code) {
//                            onJoinRoomFailed(code, null);
//                            Log.i(TAG, "join room failed, code=" + code + ", msg=" + msg);
                        }
                    });
                }
            }
        }
    };


    /**
     * ************************************ 数据源 ***************************************
     */

    private void initRecyclerView() {
        // 确认数据源,自己放在首位
        data = new ArrayList<>(accounts.size() + 1);
        LogUtil.e("=============", "===" + accounts + "=====" + data.size());
        for (String account : accounts) {
            if (ProfileManager.getInstance().getUserModel() != null && TextUtils.equals(account, ProfileManager.getInstance().getUserModel().imAccid)) {
                continue;
            }

            data.add(new TeamG2Item(TYPE_DATA, teamId, account));
        }
        if (ProfileManager.getInstance().getUserModel() == null) {
            return;
        }
        TeamG2Item selfItem = new TeamG2Item(TYPE_DATA, teamId, ProfileManager.getInstance().getUserModel().imAccid);
        selfItem.state = TeamG2Item.STATE.STATE_PLAYING; // 自己直接采集摄像头画面
        selfItem.isSelf = true;
        data.add(0, selfItem);

        // 补充占位符
        int holderLength = MAX_SUPPORT_ROOM_USERS_COUNT - data.size();
        for (int i = 0; i < holderLength; i++) {
            data.add(new TeamG2Item(teamId));
        }
        LogUtil.e("=============", "===" + accounts + "=====" + data.size());
        // RecyclerView
        adapter = new TeamAdapter(recyclerView, data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemMuteChangeListener((uid, isMute) -> {
            NERTCVideoCall.sharedInstance().setAudioMute(isMute, uid);
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(1), ScreenUtil.dip2px(1), true));
    }

    private int getItemIndex(final String account) {
        int index = 0;
        boolean find = false;
        for (TeamG2Item i : data) {
            if (TextUtils.equals(i.account, account)) {
                find = true;
                break;
            }
            index++;
        }

        return find ? index : -1;
    }

    /**
     * ************************************ 权限检查 ***************************************
     */
    // TODO G2

    /**
     * ************************************ helper ***************************************
     */

    private void showToast(String content) {
        ToastUtil.showToast(BaseApplication.getInstance(), content);
    }

    /**
     * 在线状态观察者
     */
    private Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                hangup();
                finish();
            }
        }
    };
}
