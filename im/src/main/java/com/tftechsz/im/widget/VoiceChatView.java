package com.tftechsz.im.widget;

import static com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat.Status.AUDIO_CLOSED;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.VoiceChatAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.VoiceChatDto;
import com.tftechsz.im.model.dto.VoiceRoleCheckDto;
import com.tftechsz.im.model.dto.VoiceRoleCheckNewDto;
import com.tftechsz.im.widget.pop.EditVoiceNamePopWindow;
import com.tftechsz.im.widget.pop.VoiceChatApplyPopWindow;
import com.tftechsz.im.widget.pop.VoiceNoticePopWindow;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.VoicePlayRegionsBean;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.AudiencePlay;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoom;
import com.tftechsz.common.nertcvoiceroom.model.NERtcVoiceRoomDef;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomMessage;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nertcvoiceroom.model.impl.SeatCommands;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 语音闲聊
 */
public class VoiceChatView extends LinearLayout implements View.OnClickListener, NERtcVoiceRoomDef.RoomCallback, Audience.Callback {
    private ChatApiService chatApiService;
    private LinearLayout mLlContent;
    private ImageView mIvVoice, mIvMute;
    private ImageView ivAround, ivAround1;
    private ImageView mIvPeople, mIvHost;
    private FrameLayout mFlApply;
    private CircleImageView mIvAvatar;
    private TextView mTvMode, mTvPutAway;
    private TextView mTvChatMode; // 上下麦
    private TextView mTvApplyNum;
    private VoiceChatAdapter mAdapter;
    private RecyclerView mRvVoiceChat;
    protected NERTCVideoCall voiceRoom;
    private boolean mIsShow;
    private Audience audience;
    protected VoiceRoomInfo voiceRoomInfo;
    private UserProviderService service;
    private CompositeDisposable mCompositeDisposable;
    private VoiceRoomSeat mVoiceRoomSeat;
    private VoiceChatDto mVoiceChatDto;
    private boolean mIsAdmin = false;  //是否有权限操作 管理员
    private CountBackUtils mCountBackUtils;
    private Context mContext;
    private String mTid;
    private long currentTime;  //当前时间
    private TextView mTvNotice;
    private int status;   //声音控制
    private boolean isInit;  //是否初始化
    private int mode = 2;
    private int partyLiveType;
    private VoiceNoticePopWindow popWindow;

    public VoiceChatView(Context context) {
        super(context);
        init(context);
    }

    public VoiceChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        View.inflate(context, R.layout.nim_message_voice_chat, this);
        mTvNotice = findViewById(R.id.tv_notice);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mTvApplyNum = findViewById(R.id.tv_badge);
        mTvPutAway = findViewById(R.id.tv_put_away);
        mLlContent = findViewById(R.id.ll_content);
        mIvPeople = findViewById(R.id.iv_people);
        mTvMode = findViewById(R.id.tv_mode);
        mIvVoice = findViewById(R.id.iv_voice);
        mIvMute = findViewById(R.id.iv_mute);
        mFlApply = findViewById(R.id.rl_apply);
        mIvHost = findViewById(R.id.iv_host);
        mTvChatMode = findViewById(R.id.tv_chat_mode);
        mRvVoiceChat = findViewById(R.id.rv_voice_chat);
        mIvAvatar = findViewById(R.id.iv_avatar);  //主播头像
        ivAround = findViewById(R.id.iv_round);
        ivAround1 = findViewById(R.id.iv_round1);
        mCompositeDisposable = new CompositeDisposable();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        mRvVoiceChat.setLayoutManager(layoutManager);
        mAdapter = new VoiceChatAdapter();
        mRvVoiceChat.setAdapter(mAdapter);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null)
            partyLiveType = service.getConfigInfo().sys.yunxin_live_family_type;
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            VoiceRoomSeat seat = mAdapter.getData().get(position);
            if (seat == null) return;
            switch (seat.getStatus()) {
                case VoiceRoomSeat.Status.INIT:
                case VoiceRoomSeat.Status.FORBID:
                    mVoiceRoomSeat = seat;
                    applySeat(seat);
                    break;
                case VoiceRoomSeat.Status.APPLY:
                    Utils.toast("该麦位正在被申请,\n请尝试申请其他麦位");
                    break;
                case VoiceRoomSeat.Status.ON:
                case VoiceRoomSeat.Status.AUDIO_MUTED:
                case AUDIO_CLOSED:
                case VoiceRoomSeat.Status.AUDIO_CLOSED_AND_MUTED:
                    if (service.getUserId() == seat.getUser_id()) {
                        ARouterUtils.toMineDetailActivity("");
                    } else {
                        roleCheck(seat.getUser_id(), seat.getStatus());
                    }
                    break;
                case VoiceRoomSeat.Status.CLOSED:
                    Utils.toast("该麦位已被关闭");
                    break;
            }
        });
        initListener();

    }


    private void initListener() {
        findViewById(R.id.rl_host).setOnClickListener(this);  //主播
        findViewById(R.id.ll_put_away).setOnClickListener(this);
        findViewById(R.id.tv_notice).setOnClickListener(this);
        mLlContent.setOnClickListener(this);
        mIvPeople.setOnClickListener(this);
        mTvMode.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);
        mIvMute.setOnClickListener(this);
        mTvChatMode.setOnClickListener(this);
//        listen(true);
    }

    /**
     * 设置发送tip的群组ID
     *
     * @param tid
     */
    public void setTid(String tid) {
        this.mTid = tid;
    }

    /**
     * 设置语音房名称
     */
    public void setName(String name) {
        if (mVoiceChatDto != null) {
            mVoiceChatDto.name = name;
            mTvMode.setText(name);
        }
    }

    /**
     * 设置语音房公告
     */
    public void setAnnouncement(String announcement, ChatMsg.RoomInfo roomInfo) {
        if (mVoiceChatDto != null) {
            if (!TextUtils.isEmpty(announcement)) {
                mVoiceChatDto.announcement = announcement;
            }
            if (roomInfo != null) {
                //管理员
                if (mIsAdmin) {
                    mVoiceChatDto.announcement_audit = roomInfo.announcement_audit;
                    mVoiceChatDto.name_audit = roomInfo.name_audit;
                    mVoiceChatDto.checkStatus = !TextUtils.isEmpty(roomInfo.announcement_audit) ? 1 : 0;
                    if (!TextUtils.isEmpty(roomInfo.announcement_audit)) {
                        mVoiceChatDto.announcement = roomInfo.announcement_audit;
                    }
                }
            } else {  //本地修改后同步状态
                if (mIsAdmin) {//本地界面修改
                    mVoiceChatDto.announcement_audit = announcement;
                    mVoiceChatDto.checkStatus = 1;
                    mVoiceChatDto.announcement = announcement;
                }
            }
            showNoticePop();
            if (mCountBackUtils == null)
                mCountBackUtils = new CountBackUtils();
            mCountBackUtils.countBack(6, new CountBackUtils.Callback() {
                @Override
                public void countBacking(long time) {
                }

                @Override
                public void finish() {
                    if (popWindow != null)
                        popWindow.dismiss();
                }
            });
        }

    }


    /**
     * 设置用户信息
     * rtcJoinChannel rtc 模式加入房间
     */
    public void setVoiceUser(VoiceChatDto voiceChatDto, boolean rtcJoinChannel) {
        mVoiceChatDto = voiceChatDto;
        mTvMode.setText(voiceChatDto.name);
        mAdapter.setList(voiceChatDto.microphone);
        if (voiceChatDto.microphone_host != null) {
            if (voiceChatDto.microphone_host.getStatus() != 0) {
                GlideUtils.loadImage(getContext(), mIvAvatar, CommonUtil.getHttpUrlHead() + voiceChatDto.microphone_host.getIcon());
                mIvHost.setImageResource(R.mipmap.chat_ic_voice_chat_host_icon);
                ivAround.setVisibility(VISIBLE);
            } else {
                mIvHost.setImageResource(R.mipmap.chat_ic_voice_chat_host);
                mIvAvatar.setImageResource(R.mipmap.chat_ic_voice_chat_add);
                ivAround.setVisibility(INVISIBLE);
                ivAround1.setVisibility(INVISIBLE);
                ivAround.clearAnimation();
                ivAround1.clearAnimation();
            }
        }
        initVoiceRoom();
        if (isOnSeat()) {
            mIvVoice.setVisibility(View.VISIBLE);
            if (status == 2) {
                mIvVoice.setImageResource(R.mipmap.chat_ic_voice_open);
            } else {
                mIvVoice.setImageResource(R.mipmap.chat_ic_voice_close);
            }
            mFlApply.setVisibility(mIsAdmin ? VISIBLE : GONE);
            mTvChatMode.setText("下麦");
        } else {
            mFlApply.setVisibility(VISIBLE);
            mIvVoice.setVisibility(View.GONE);
            if (voiceRoom.isRoomAudioMute()) {
                mIvMute.setImageResource(R.mipmap.chat_ic_voice_chat_close);
            } else {
                mIvMute.setImageResource(R.mipmap.chat_ic_voice_chat_open);
            }
            mTvChatMode.setText("上麦");
        }
        if (audience == null && voiceRoom != null) {
            audience = voiceRoom.getAudience();
        }
        if (voiceRoom != null)
            voiceRoom.setSeatList(voiceChatDto.microphone);
        roleCheck(0, 0);
        if (!TextUtils.isEmpty(voiceChatDto.rtmp_pull_url) && !isOnSeat() && mode == 2) {
            if (partyLiveType == 0) {  // cdn
                audience.getAudiencePlay().play(voiceChatDto.rtmp_pull_url);
                audience.getAudiencePlay().registerNotify(new AudiencePlay.PlayerNotify() {
                    @Override
                    public void onPreparing() {
                    }

                    @Override
                    public void onPlaying() {
                    }

                    @Override
                    public void onError() {
                        if (!NetworkUtil.isNetworkAvailable(getContext())) {
                            Utils.toast("主播网络好像出了问题");
                        }
                    }

                    @Override
                    public void getSeatVoice(List<VoicePlayRegionsBean> regions) {
                        if (mRvVoiceChat == null) {
                            mRvVoiceChat = findViewById(R.id.rv_voice_chat);
                        }
                        if (mRvVoiceChat.getLayoutManager() == null) {
                            mRvVoiceChat.setLayoutManager(new GridLayoutManager(getContext(), 4));
                        }
                        int size = regions.size();
                        for (int i = 0; i < size; i++) {
                            if (mVoiceChatDto != null && mVoiceChatDto.microphone != null) {
                                for (int j = 0; j < mVoiceChatDto.microphone.size(); j++) {
                                    if (regions.get(i).uid == mVoiceChatDto.microphone.get(j).getUser_id()) {
                                        View itemView = mRvVoiceChat.getLayoutManager().findViewByPosition(mVoiceChatDto.microphone.get(j).getIndex() - 1);
                                        if (itemView != null) {
                                            ImageView circle = itemView.findViewById(R.id.circle);
                                            ImageView circle1 = itemView.findViewById(R.id.circle1);
                                            showVolume(circle, circle1, regions.get(i).volume);
                                        }
                                    }
                                }
                            }
                            if (mVoiceChatDto != null && mVoiceChatDto.microphone_host != null) {
                                if (mVoiceChatDto.microphone_host.getUser_id() == regions.get(i).uid) {
                                    showVolume(ivAround, ivAround1, regions.get(i).volume);
                                }
                            }
                        }
                    }
                });
            }
        }
        if (partyLiveType == 1) {  //rtc
            //观众
            if (voiceRoom != null && rtcJoinChannel) {
                voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_AUDIENCE);
                voiceRoom.enterRoom(false, null);
            }
        }
    }


    /**
     * 设置申请数量
     */
    public void setApplyNum(int num) {
        mTvApplyNum.setText(String.valueOf(num));
        mTvApplyNum.setVisibility(num > 0 ? VISIBLE : INVISIBLE);
    }


    /**
     * 创建房间成功
     */
    public void initData(VoiceRoomInfo voiceRoomInfo) {
        this.voiceRoomInfo = voiceRoomInfo;
        if (mVoiceRoomSeat == null)
            mVoiceRoomSeat = new VoiceRoomSeat(voiceRoomInfo.index);
        initVoiceRoom();
        enterRoom(true);
    }


    protected void initVoiceRoom() {
        NERtcVoiceRoom.setAccountMapper(accountId -> service.getUserId());
        voiceRoom = NERtcVoiceRoom.sharedInstance();
        if (mVoiceChatDto == null) {
            return;
        }
        if (voiceRoomInfo == null) {
            voiceRoomInfo = new VoiceRoomInfo();
        }
        if (partyLiveType == 1 && !TextUtils.isEmpty(mVoiceChatDto.room_name) && !TextUtils.isEmpty(mVoiceChatDto.room_token)) {
            voiceRoomInfo.setRoom_name(mVoiceChatDto.room_name);
            voiceRoomInfo.setRoom_token(mVoiceChatDto.room_token);
        }
        voiceRoom.initRoom(voiceRoomInfo, mVoiceChatDto.microphone_host, createUser(), partyLiveType);
        if (!isInit) {
            voiceRoom.setMode(2, this);
            voiceRoom.initNERtc(NERTCVideoCallImpl.VOICE_ROOM, this);
            isInit = true;
        }
    }

    /**
     * 设置当前模式
     */
    public void setVoiceMode(int mode) {
        this.mode = mode;
        if (voiceRoom != null) {
            voiceRoom.setMode(mode, this);
            resumeAudience();
            if (mVoiceChatDto != null && !isOnSeat() && audience != null && audience.getAudiencePlay() != null && audience.getAudiencePlay().isReleased()) {
                audience.getAudiencePlay().play(mVoiceChatDto.rtmp_pull_url);
            }
        }
    }


    /**
     * 移除监听
     */
    public void removeCallBack() {
        if (voiceRoom != null) {
            voiceRoom.setMode(1, null);
        }
    }


    /**
     * 判断自己是否在麦上
     */
    public boolean isOnSeat() {
        boolean isOnSeat = false;
        if (mVoiceChatDto != null && mVoiceChatDto.microphone != null) {
            for (int i = 0; i < mVoiceChatDto.microphone.size(); i++) {
                if (mVoiceChatDto.microphone.get(i).getUser_id() == service.getUserId()) {
                    isOnSeat = true;
                    status = mVoiceChatDto.microphone.get(i).getStatus();
                }
            }
        }
        if (mVoiceChatDto != null && mVoiceChatDto.microphone_host != null) {
            if (mVoiceChatDto.microphone_host.getUser_id() == service.getUserId()) {
                isOnSeat = true;
                status = mVoiceChatDto.microphone_host.getStatus();
            }
        }
        return isOnSeat;
    }


    /**
     * 申请上麦
     */
    public void applySeat(VoiceRoomSeat seat) {
        if (isOnSeat()) {
            Utils.toast("您已经在麦上");
            return;
        }
        mCompositeDisposable.add(new RxPermissions((FragmentActivity) getContext())
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(grant -> {
                    if (grant) {
                        mCompositeDisposable.add(chatApiService.joinRoom(seat == null ? 99 : seat.getIndex())
                                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                                    @Override
                                    public void onSuccess(BaseResponse<Boolean> response) {

                                    }
                                }));
                    } else {
                        ToastUtil.showToast(BaseApplication.getInstance(), "请允许录音权限");
                    }
                }));
    }


    /**
     * 声音操作
     *
     * @param type    操作对象 1-自己 2-他人
     * @param user_id 操作对象ID,自己可不传或0
     * @param status  2 5 6 7            5 麦位上有人，但是语音被屏蔽（有人） 6  麦位上有人，但是他关闭了自己的语音（有人）(没有被屏蔽)
     */
    public void microphone(int type, int user_id, int status) {
        if ((isOnSeat() && type == 1) || type == 2)
            mCompositeDisposable.add(chatApiService.microphone(type, user_id, status)
                    .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                        }
                    }));
    }


    /**
     * 声音重制所有
     */
    public void resetVoice() {
        if (voiceRoom == null) return;
        if (voiceRoom.isRoomAudioMute()) {
            voiceRoom.muteRoomAudio(false);
        }
        if (microphoneMute)
            voiceRoom.muteLocalAudio(false);
        microphoneMute = false;
    }


    /**
     * 声音重制话筒
     */
    public void resetAudioVoice() {
        if (voiceRoom == null) return;
        if (microphoneMute)
            voiceRoom.muteLocalAudio(false);
        microphoneMute = false;
    }


    /**
     * 上麦
     */
    public void onSeat(VoiceRoomInfo voiceRoomInfo) {
        this.voiceRoomInfo = voiceRoomInfo;
        initVoiceRoom();
        enterRoom(false);
        //观众
        audience = voiceRoom.getAudience();
        audience.setCallback(VoiceChatView.this);
        if (mVoiceRoomSeat == null)
            mVoiceRoomSeat = new VoiceRoomSeat(voiceRoomInfo.index);
        UserInfo userInfo = service.getUserInfo();
        if (mVoiceRoomSeat.getIndex() == 0) {
            if (userInfo != null) {
                GlideUtils.loadImage(getContext(), mIvAvatar, userInfo.getIcon());
            }
            mIvHost.setImageResource(R.mipmap.chat_ic_voice_chat_host_icon);
            ivAround.setVisibility(VISIBLE);
        } else {
            if (userInfo != null) {
                mVoiceRoomSeat.setIcon(userInfo.getIcon());
                mVoiceRoomSeat.setNickname(userInfo.getNickname());
                mVoiceRoomSeat.setStatus(VoiceRoomSeat.Status.ON);
                mVoiceRoomSeat.setIndex(voiceRoomInfo.index);
            }
            if (voiceRoomInfo.index - 1 >= 0)
                mAdapter.setData(voiceRoomInfo.index - 1, mVoiceRoomSeat);
            mTvChatMode.setText("下麦");
        }
        //cdn 模式执行
        if (partyLiveType == 0) {
            if (isOnSeat()) {
                releaseAudience();
            }
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
     * 下麦
     */
    public void down(boolean isLeaveChannel) {
        if (isOnSeat()) {
            mCompositeDisposable.add(chatApiService.down()
                    .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                            if (response.getData() != null && isLeaveChannel) {
                                if (partyLiveType == 0) {  // cdn
                                    leaveChannel(true);
                                }
                            }
                            if (partyLiveType == 1) {
                                voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_AUDIENCE);
                                sendLeaveSeat();
                            }
                        }
                    }));
        }
    }

    /**
     * 通知下麦不调用接口
     */
    public void noticeDown() {
        if (partyLiveType == 0) {  // cdn
            leaveChannel(true);
        }
        if (partyLiveType == 1) {
            voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_AUDIENCE);
            sendLeaveSeat();
        }
    }


    /**
     * 踢掉用户
     *
     * @param userId
     */
    public void kickOut(int userId) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(ChatApiService.class)
                .kickOut(userId)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }
                }));
    }

    /**
     * 查看当前权限信息
     */
    public void roleCheck(int userId, int status) {
        mCompositeDisposable.add(chatApiService.roleCheck(userId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<VoiceRoleCheckDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<VoiceRoleCheckDto> response) {
                        if (response.getData() != null) {
                            VoiceRoleCheckDto data = response.getData();
                            if (userId == 0) {   //进入查看自己是否有权限操作更改名称公告
                                mIsAdmin = data.is_admin;
                                if (mIsAdmin) {
                                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.chat_ic_voice_mode_change);
                                    mTvMode.setCompoundDrawablesWithIntrinsicBounds(null,
                                            null, drawable, null);
                                } else {
                                    mTvMode.setCompoundDrawablesWithIntrinsicBounds(null,
                                            null, null, null);
                                }
                                if (isOnSeat()) {
                                    mFlApply.setVisibility(mIsAdmin ? VISIBLE : GONE);
                                }
                            } else {  //查看其他人的信息
                                if (listener != null)
                                    listener.showUserIdGiftPop(String.valueOf(userId), data.is_operate ? 1 : 0, status);
                            }

                        }
                    }
                }));
    }


    protected final void enterRoom(boolean anchorMode) {
        if (voiceRoom != null) {
            if (partyLiveType == 0) {  // cdn
                voiceRoom.enterRoom(anchorMode, mVoiceRoomSeat);
            } else {  // rtc
                voiceRoom.setClientRole(NERtcConstants.UserRole.CLIENT_ROLE_BROADCASTER);
                sendOnSeat();
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_people) {  //申请成员
            VoiceChatApplyPopWindow popWindow = new VoiceChatApplyPopWindow(getContext());
            popWindow.showPopupWindow();
        } else if (id == R.id.tv_mode) {  // 更改名称
            if (!mIsAdmin) return;
            if (mVoiceChatDto != null) {
                checkNetStatusText(1, true);
            }
        } else if (id == R.id.iv_voice) {  //声音控制
            if (isOnSeat()) {
                boolean muted = microphoneMute;
                microphone(1, 0, muted ? VoiceRoomSeat.Status.ON : VoiceRoomSeat.Status.AUDIO_CLOSED);
            }
        } else if (id == R.id.iv_mute) {  //声音控制
            toggleMuteRoomAudio();
        } else if (id == R.id.tv_notice) {  //公告
            if (!ClickUtil.canOperate() || mVoiceChatDto == null) {
                return;
            }

            mCompositeDisposable.add(chatApiService.editComCheck(2).compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<VoiceRoleCheckNewDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<VoiceRoleCheckNewDto> response) {

                            if (response.getData() != null && response.getData().status == 0) {
                                if (!TextUtils.isEmpty(response.getData().content)) {
                                    mVoiceChatDto.announcement = response.getData().content;
                                }
                                mVoiceChatDto.checkStatus = response.getData().status;
                                //直接打开popwindow
                                showNoticePop();
                            } else {
                                if (response.getData() != null) {
                                    //审核中 测试下 那边的mVoiceChatDto的数据变化没有
                                    if (!TextUtils.isEmpty(response.getData().content)) {
                                        mVoiceChatDto.announcement = response.getData().content;
                                    }
                                    mVoiceChatDto.checkStatus = response.getData().status;
                                    //直接打开popwindow
                                    showNoticePop();
                                   /* popWindow.setAnnouncement(mVoiceChatDto.announcement, mIsAdmin, false);
                                    popWindow.showPopupWindow(mTvNotice);*/

                                }

                            }
                        }

                    }));


//            checkNetStatusText(2, true);

        } else if (id == R.id.ll_put_away) {  //展开和隐藏
            mIsShow = !mIsShow;
            if (mIsShow) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.chat_ic_voice_chat_put_open);
                mTvPutAway.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvPutAway.setText("展开");
                AnimationUtil.invisibleAnimator(mLlContent);
            } else {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.chat_ic_voice_chat_put_away);
                mTvPutAway.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvPutAway.setText("收起");
                AnimationUtil.visibleAnimator(mLlContent);
            }
        } else if (id == R.id.tv_chat_mode) {  //上下麦
            if (TextUtils.equals("下麦", mTvChatMode.getText().toString())) {
                new CustomPopWindow(getContext()).setContent("下麦将清空心动值确认下麦吗？")
                        .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                            @Override
                            public void onCancel() {
                            }

                            @Override
                            public void onSure() {
                                down(true);
                            }
                        }).showPopupWindow();
            } else {
                applySeat(null);
            }
        } else if (id == R.id.rl_host) {   //主播位置
            if (mVoiceChatDto != null) {
                if (mVoiceChatDto.microphone_host != null && mVoiceChatDto.microphone_host.getUser_id() != 0) {
                    if (service.getUserId() == mVoiceChatDto.microphone_host.getUser_id()) {
                        ARouterUtils.toMineDetailActivity("");
                    } else {
                        roleCheck(mVoiceChatDto.microphone_host.getUser_id(), mVoiceChatDto.microphone_host.getStatus());
                    }
                } else {
                    VoiceRoomSeat seat = mVoiceChatDto.microphone_host;
                    seat.setIndex(0);
                    applySeat(seat);
                }
            }
        }
    }


    /**
     * 显示公告弹窗
     */
    private void showNoticePop() {
        if (mContext == null) return;
        if (popWindow == null)
            popWindow = new VoiceNoticePopWindow(mContext, () -> {
                if (mVoiceChatDto != null) {
                    checkNetStatusText(2, true);
                }
            });
        popWindow.setAnnouncement(mVoiceChatDto.announcement, mIsAdmin, mVoiceChatDto.checkStatus == 0);
        popWindow.showPopupWindow(mTvNotice);
    }


    /**
     * 检测是否在审核中状态
     */
    public void checkNetStatusText(int type, boolean flagisShowDialog) {
        mCompositeDisposable.add(chatApiService.editComCheck(type).compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<VoiceRoleCheckNewDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<VoiceRoleCheckNewDto> response) {
                        if (type == 2) {
                            if (response.getData() != null && response.getData().status == 0) {
                                if (!TextUtils.isEmpty(response.getData().content)) {
                                    mVoiceChatDto.announcement = response.getData().content;
                                }
                                mVoiceChatDto.checkStatus = response.getData().status;
                                if (flagisShowDialog) {
                                    ARouterUtils.toEditFamilyfVoice("", 6, mVoiceChatDto.announcement);
                                }
                            } else {
                                if (response.getData() != null) {

                                    if (!TextUtils.isEmpty(response.getData().content)) {
                                        mVoiceChatDto.announcement = response.getData().content;
                                    }

                                    if (mVoiceChatDto.checkStatus == 1) {//审核中
                                        return;
                                    }
                                    //审核中 测试下 那边的mVoiceChatDto的数据变化没有
                                    if (!TextUtils.isEmpty(response.getData().content)) {
                                        mVoiceChatDto.announcement = response.getData().content;
                                    }
                                    mVoiceChatDto.checkStatus = response.getData().status;
                                   /* popWindow.setAnnouncement(mVoiceChatDto.announcement, mIsAdmin, false);
                                    popWindow.showPopupWindow(mTvNotice);*/
                                    if (flagisShowDialog) {
                                        ToastUtil.showToast(mContext, "正在审核中");
                                        if (popWindow != null) {
                                            popWindow.dismiss();
                                        }
                                    }


                                }

                            }
                        } else {
                            if (response.getData().status == 1) {
                                ToastUtil.showToast(mContext, "正在审核中");
                            } else {
                                EditVoiceNamePopWindow popWindow = new EditVoiceNamePopWindow(getContext());
                                popWindow.setName(mVoiceChatDto.name);
                                popWindow.showPopupWindow();
                            }

                        }

                    }

                }));

    }

    /**
     * 通知设置声音
     */
    public void changeVoiceStatus(int userId, int status) {
        if (mVoiceChatDto != null && mVoiceChatDto.microphone != null) {
            for (int i = 0; i < mVoiceChatDto.microphone.size(); i++) {
                if (mVoiceChatDto.microphone.get(i).getUser_id() == userId) {
                    mVoiceChatDto.microphone.get(i).setStatus(status);
                }
            }
        }
        if (mVoiceChatDto != null && mVoiceChatDto.microphone_host != null) {
            if (mVoiceChatDto.microphone_host.getUser_id() == userId) {
                mVoiceChatDto.microphone_host.setStatus(status);
            }
        }
        if (service.getUserId() != userId) return;
        toggleMuteLocalAudio(status);
    }


    /**
     * 设置心动值
     */
    public void changeCost(List<ChatMsg.RoomSeat> seatList) {
        if (mVoiceChatDto != null && mVoiceChatDto.microphone != null) {
            for (int j = 0; j < seatList.size(); j++) {
                for (int i = 0; i < mVoiceChatDto.microphone.size(); i++) {
                    if (mVoiceChatDto.microphone.get(i).getUser_id() == seatList.get(j).user_id) {
                        mVoiceChatDto.microphone.get(i).setCost(seatList.get(j).cost);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    boolean microphoneMute = false;

    public final void toggleMuteLocalAudio(int status) {
        if (voiceRoom == null) return;
        boolean muted = voiceRoom.muteLocalAudio(microphoneMute = !microphoneMute);
        if (muted) {
            mIvVoice.setImageResource(R.mipmap.chat_ic_voice_close);
            Utils.toast(status == 5 ? "话筒已被管理员关闭" : "话筒已关闭");
        } else {
            mIvVoice.setImageResource(R.mipmap.chat_ic_voice_open);
            Utils.toast("话筒已打开");
        }
    }


    protected final void toggleMuteRoomAudio() {
        if (voiceRoom == null) return;
        boolean muted = voiceRoom.muteRoomAudio(!voiceRoom.isRoomAudioMute());
        if (muted) {
            mIvMute.setImageResource(R.mipmap.chat_ic_voice_chat_close);
            Utils.toast("听筒已关闭");
        } else {
            mIvMute.setImageResource(R.mipmap.chat_ic_voice_chat_open);
            Utils.toast("听筒已开启");
        }
    }


    @Override
    public void onEnterRoom(boolean success) {

    }

    @Override
    public void onLeaveRoom() {

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
        showVolume(ivAround, ivAround1, volume);
    }

    @Override
    public void onMute(boolean muted) {

    }

    @Override
    public void updateSeats(List<VoiceRoomSeat> seats) {
    }

    @Override
    public void updateSeat(VoiceRoomSeat seat) {
//        getRoomInfo();
    }

    private int selfVoice;

    @Override
    public void onSelfSeatVolume(int volume) {
        selfVoice = volume;

        if (mRvVoiceChat == null) {
            mRvVoiceChat = findViewById(R.id.rv_voice_chat);
        }
        if (mRvVoiceChat.getLayoutManager() == null) {
            mRvVoiceChat.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        if (volume == 0) {
            ivAround.setVisibility(GONE);
            ivAround1.setVisibility(GONE);
            ivAround.clearAnimation();
            ivAround1.clearAnimation();
        }
        if (mVoiceChatDto != null && mVoiceChatDto.microphone != null) {
            for (int j = 0; j < mVoiceChatDto.microphone.size(); j++) {
                if (service.getUserId() == mVoiceChatDto.microphone.get(j).getUser_id()) {
                    View itemView = mRvVoiceChat.getLayoutManager().findViewByPosition(mVoiceChatDto.microphone.get(j).getIndex() - 1);
                    if (itemView != null) {
                        ImageView circle = itemView.findViewById(R.id.circle);
                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                        showVolume(circle, circle1, volume);
                    }
                }
            }
        }
        if (mVoiceChatDto != null && mVoiceChatDto.microphone_host != null) {
            if (mVoiceChatDto.microphone_host.getUser_id() == service.getUserId()) {
                showVolume(ivAround, ivAround1, volume);
            }
        }
//
//        if (remoteVoice > 60 || selfVoice > 60) {
//            NERtcEx.getInstance().adjustLoopbackRecordingSignalVolume(10);
//        } else {
//            NERtcEx.getInstance().adjustLoopbackRecordingSignalVolume(35);
//        }
    }

    int remoteVoice;

    @Override
    public void onSeatVolume(VoiceRoomSeat seat, int volume) {
        remoteVoice = volume;
        if (mRvVoiceChat == null) {
            mRvVoiceChat = findViewById(R.id.rv_voice_chat);
        }
        if (mRvVoiceChat.getLayoutManager() == null) {
            mRvVoiceChat.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        if (seat.getUser_id() == service.getUserId()) return;
        View itemView = mRvVoiceChat.getLayoutManager().findViewByPosition(seat.getIndex() - 1);
        if (itemView != null) {
            ImageView circle = itemView.findViewById(R.id.circle);
            ImageView circle1 = itemView.findViewById(R.id.circle1);
            showVolume(circle, circle1, volume);
        }

//        if (remoteVoice > 60 || selfVoice > 60) {
//            NERtcEx.getInstance().adjustLoopbackRecordingSignalVolume(10);
//        } else {
//            NERtcEx.getInstance().adjustLoopbackRecordingSignalVolume(35);
//        }

    }


    private void showVolume(ImageView view, ImageView view1, int volume) {
//        volume = toStepVolume(volume);
        if (volume == 0) {
            view.clearAnimation();
            view1.clearAnimation();
        } else {
            setAnimation(view, view1);
        }
    }

    private static int toStepVolume(int volume) {
        int step = 0;
        volume /= 40;
        while (volume > 0) {
            step++;
            volume /= 2;
        }
        if (step > 8) {
            step = 8;
        }
        return step;
    }


    @Override
    public void onVoiceRoomMessage(VoiceRoomMessage message) {
    }

    @Override
    public void onMusicStateChange(int type) {

    }

    @Override
    public void sendLeaveSeat() {
        long now = System.currentTimeMillis();
        if ((now - currentTime) > 1500 && mVoiceChatDto != null) {
            currentTime = System.currentTimeMillis();
            ChatMsgUtil.sendVoiceTipMessage(service.getUserId() + "", mTid, mVoiceChatDto.room_name, "seat_down", service.getUserInfo().getNickname(), "离开了麦位", new ChatMsgUtil.OnMessageListener() {
                @Override
                public void onGiftListener(int code, IMMessage message) {
                    RxBus.getDefault().post(new MessageCallEvent(message));
                }
            });
        }
    }

    @Override
    public void sendOnSeat() {
        if (mVoiceChatDto == null) return;
        ChatMsgUtil.sendVoiceTipMessage(service.getUserId() + "", mTid, mVoiceChatDto.room_name, "seat_up", service.getUserInfo().getNickname(), "成功上麦", new ChatMsgUtil.OnMessageListener() {
            @Override
            public void onGiftListener(int code, IMMessage message) {
                RxBus.getDefault().post(new MessageCallEvent(message));
            }
        });
    }

    protected VoiceRoomUser createUser() {
        UserInfo userInfo = service.getUserInfo();
        return new VoiceRoomUser(String.valueOf(service.getUserId()), userInfo == null ? "" : userInfo.getNickname(), userInfo == null ? "" : userInfo.getIcon());
    }


    public void inviteSeat(VoiceRoomSeat seat) {
        if (seat != null) {
            UserInfo userInfo = service.getUserInfo();
            if (seat.getIndex() == 0) {
                if (userInfo != null) {
                    GlideUtils.loadImage(getContext(), mIvAvatar, userInfo.getIcon());
                }
                mIvHost.setImageResource(R.mipmap.chat_ic_voice_chat_host_icon);
                ivAround.setVisibility(VISIBLE);
            } else {
                if (userInfo != null) {
                    seat.setIcon(userInfo.getIcon());
                    seat.setNickname(userInfo.getNickname());
                    seat.setStatus(VoiceRoomSeat.Status.ON);
                }
                mVoiceRoomSeat = seat;
                mAdapter.setData(seat.getIndex() - 1, seat);
                mTvChatMode.setText("下麦");
            }
        }
    }

    //观众
    @Override
    public void onSeatApplyDenied(boolean otherOn) {

    }

    @Override
    public void onEnterSeat(VoiceRoomSeat seat, boolean last) {
    }

    @Override
    public void onLeaveSeat(VoiceRoomSeat seat, boolean bySelf) {

    }

    @Override
    public void onSeatMuted() {

    }

    @Override
    public void onSeatClosed() {
    }

    @Override
    public void onTextMuted(boolean muted) {

    }

    public void listen(boolean on) {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotification, on);
    }

    private final Observer<CustomNotification> customNotification = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification notification) {
            int command = SeatCommands.commandFrom(notification);
            VoiceRoomSeat seat = SeatCommands.seatFrom(notification);
            if (seat == null) {
                return;
            }
            updateSeat(seat);
        }
    };

    public void onDestroy() {
        if (voiceRoom != null) {
            voiceRoom.muteRoomAudio(false);
            voiceRoom.onDestroy();
        }
        isInit = false;
    }


    /**
     * 离开房间
     *
     * @param sendTip 是否发送tip
     */
    public void leaveChannel(boolean sendTip) {
        if (voiceRoom == null) return;
        voiceRoom.leaveRoom(sendTip);
        openVoice();
        if (partyLiveType == 1) {
            sendLeaveSeat();
        }
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
     * 暂停播放器
     */
    public void pauseAudience() {
        if (audience != null && audience.getAudiencePlay() != null) {
            audience.getAudiencePlay().pause();
        }
    }

    /**
     * 开始播放器
     */
    public void resumeAudience() {
        if (audience != null && audience.getAudiencePlay() != null && audience.getAudiencePlay().isPaused()) {
            audience.getAudiencePlay().resume();
        }
    }


    /**
     * 打开声音
     */
    public void openVoice() {
        resetAudioVoice();
        if (popWindow != null)
            popWindow.dismiss();
    }


    /**
     * 设置播放动画
     */
    private void setAnimation(ImageView imageView, ImageView imageView1) {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.15f, 1f, 1.15f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        as.setDuration(800);
        as.addAnimation(scaleAnimation);
        as.addAnimation(alphaAnimation);
        imageView.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        AnimationSet as1 = new AnimationSet(true);
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.15f, 1.3f, 1.15f, 1.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.5f, 0.1f);
        scaleAnimation1.setDuration(800);
        scaleAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        as1.setDuration(800);
        as1.addAnimation(scaleAnimation1);
        as1.addAnimation(alphaAnimation1);
        imageView1.startAnimation(as1);
    }


    public interface OnSelectListener {
        void showUserIdGiftPop(String userId, int isAdmin, int status);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectCheckListener {
        void check();
    }

}
