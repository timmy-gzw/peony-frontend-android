package com.tftechsz.party.mvp.ui.activity;

import static com.tftechsz.common.constant.Interfaces.RECHARGE_NUMBER;

import android.Manifest;
import android.bluetooth.BluetoothHeadset;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.google.gson.Gson;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgActivity;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.support.glide.ImageLoaderKit;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.ChatTipsContent;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.VoicePlayRegionsBean;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.nertcvoiceroom.model.Audience;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomInfo;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomUser;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.service.PartyAudioService;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.TimeUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CircleUrlImageSpan;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyPkSeatAdapter;
import com.tftechsz.party.adapter.PartyRoomMsgAdapter;
import com.tftechsz.party.adapter.PartySeatAdapter;
import com.tftechsz.party.adapter.PartyWatchersAdapter;
import com.tftechsz.party.databinding.ActivityPartyRoomBinding;
import com.tftechsz.party.entity.MultipleChatRoomMessage;
import com.tftechsz.party.entity.dto.ChatPartyPkDto;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.NoticeRankDto;
import com.tftechsz.party.entity.dto.PartyGiftDto;
import com.tftechsz.party.entity.dto.PartyInfoDto;
import com.tftechsz.party.entity.dto.PartyPkSaveDto;
import com.tftechsz.party.entity.dto.PkDataDto;
import com.tftechsz.party.entity.req.SavePkReq;
import com.tftechsz.party.mvp.IView.IBasePartyRoomView;
import com.tftechsz.party.mvp.presenter.BasePartyRoomPresenter;
import com.tftechsz.party.receiver.HeadSetReceiver;
import com.tftechsz.party.widget.WatermarkView;
import com.tftechsz.party.widget.pop.EarphoneSettingPop;
import com.tftechsz.party.widget.pop.FunctionPopWindow;
import com.tftechsz.party.widget.pop.InviteOnSeatPopWindow;
import com.tftechsz.party.widget.pop.PartyAnnouncementPopWindow;
import com.tftechsz.party.widget.pop.PkResultPopWindow;
import com.tftechsz.party.widget.pop.StartPkPopWindow;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.transformer.ZoomOutPageTransformer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BasePartyRoomActivity extends BaseMvpActivity<IBasePartyRoomView, BasePartyRoomPresenter> implements IBasePartyRoomView, PartyAudioService.RoomCallBack, HeadSetReceiver.HeadSetStatus, View.OnClickListener {
    protected ActivityPartyRoomBinding mBind;
    protected EditText mEtContent;
    protected final int OVERLAY_PERMISSION_RESULT = 1000;
    protected PartyWatchersAdapter mWatcherAdapter;  //观看人数前三名
    protected PartySeatAdapter mSeatAdapter;  //座位适配器
    protected PartyPkSeatAdapter mWeTeamAdapter, mOtherTeamAdapter;  //我方 ，对方
    protected NERTCVideoCall voiceRoom;
    protected UserProviderService service;
    protected String mRoomId = "";
    protected int mLastPartyId;
    protected String mRoomBg = "";
    protected int mMessageNum;  // 消息数量
    protected VoiceRoomSeat mVoiceRoomSeat;
    protected VoiceRoomSeat mHostSeat;  //主播麦位数据
    protected VoiceRoomInfo mVoiceRoomInfo;   //房间信息
    protected String mRoomThumb;   //语音房封面
    protected Audience audience;  // 观众
    protected int mId;
    protected JoinPartyDto mData;   //加入聊天室的信息
    protected int mFightPattern = 1;  //当前模式  1:普通模式  2：pk 模式
    protected int mMicrophonePattern;   //1/2 自由/麦序
    protected int mPkInfoId;  //pk id
    protected SavePkReq mSavePkReq;  //保存的pk数据
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    public LinearLayoutManager mMsgLayoutManager;
    protected PartyRoomMsgAdapter mMsgAdapter;
    protected boolean isRelease = true;  //是否释放资源
    private boolean isFirstLogin = false;  //是否第一次登录
    private StartPkPopWindow mStartPkPopWindow;   //开始pk
    private PkResultPopWindow mPkResultPopWindow;  //pk 停止
    private InviteOnSeatPopWindow mInviteOnSeatPopWindow;  //邀请上麦弹窗
    protected PartyAnnouncementPopWindow mAnnouncePop;  //公告
    protected EarphoneSettingPop mEarphoneSettingPop;  //耳返设置
    protected int mHeadStatus;  //是否有插入耳机
    protected boolean mEnableEarBack = false;  //默认未开启耳返
    private CountBackUtils mPkBack, mFloatBack;
    protected SVGAParser svgaParser;
    private final Queue<ChatMsg> mFloatGift = new ConcurrentLinkedQueue<>();   //漂屏队列
    protected final Queue<GiftDto> myGiftList = new ConcurrentLinkedQueue<>();
    private boolean mIsPlayEnd = false;
    protected int mRoomShutUp, mUserShutUp;   //针对聊天室禁言  针对个人禁言
    protected Handler safeHandle;
    protected int partyLiveType = 0;  //默认cdn  1:rtc
    private String mJoinType;
    //倒计时
    protected CountBackUtils mAnnouncementBack, mSvgBack;
    protected String shutUpToastMsg;//禁言提示
    protected FunctionPopWindow functionPopWindow;
    protected SVGAImageView svgaImageView;
    protected boolean autoScroller = true;
    private HeadSetReceiver mReceiver;
    protected boolean mIsSendPicture = false;  //是否发送了大表情
    //曝光首次进入
    public boolean mIsNetVisit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        Intent intent = getIntent();
        mLastPartyId = intent.getIntExtra("lastPartyId", 0);
        mRoomId = intent.getStringExtra("roomId");
        mId = intent.getIntExtra("id", 0);
        mRoomThumb = intent.getStringExtra("roomThumb");
        mRoomBg = intent.getStringExtra("roomBg");
        mData = intent.getParcelableExtra("partyData");
        mMessageNum = intent.getIntExtra("messageNum", 0);
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ENTER_PARTY_ROOM));
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().addRoomCallBack(BasePartyRoomActivity.this);
            BaseMusicHelper.get().getPartyService().setListener();
        }
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null)
            partyLiveType = service.getConfigInfo().sys.yunxin_live_type;
        GlideUtils.loadImage(this, mBind.ivBg, mRoomBg, R.drawable.party_bg_room1);
        //进入不同的派对
        if (mLastPartyId != mId) {
            enterRoom();
            //如果上一个房间没有且是rtc模式
            if (mLastPartyId != 0 && partyLiveType == 1)
                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                    BaseMusicHelper.get().getPartyService().leaveRoom();
                }
        }
        if (mData == null) {
            getP().joinParty(mId, "in");
        } else {
            setData(mData, "in");
        }
        MMKVUtils.getInstance().encode(Constants.PARTY_ID, mId);
        registerObservers(true);
        mReceiver = new HeadSetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mReceiver.setHeadSetStatus(this);
        mContext.registerReceiver(mReceiver, intentFilter);
    }


    @Override
    public BasePartyRoomPresenter initPresenter() {
        return new BasePartyRoomPresenter();
    }


    /**
     * 初始化
     */
    protected void initVoiceRoom(VoiceRoomInfo voiceRoomInfo) {
        if (!ServiceUtils.isServiceRunning(PartyAudioService.class)) {
            Intent floatService = new Intent(this, PartyAudioService.class);
            startService(floatService);
        }
        if (mVoiceRoomSeat == null)
            mVoiceRoomSeat = new VoiceRoomSeat(voiceRoomInfo.index);
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
            BaseMusicHelper.get().getPartyService().initVoiceRoom(voiceRoomInfo, createUser(), service.getUserId(), mRoomId, partyLiveType);
        }
    }


    /**
     * 拉流监听
     */
    public void setPullUrlInfo(VoiceRoomInfo voiceRoomInfo) {
        if (mVoiceRoomInfo == null)
            mVoiceRoomInfo = new VoiceRoomInfo();
        mVoiceRoomInfo.push_url = voiceRoomInfo.push_url;
        mVoiceRoomInfo.http_pull_url = voiceRoomInfo.http_pull_url;
        mVoiceRoomInfo.rtmp_pull_url = voiceRoomInfo.rtmp_pull_url;
        mVoiceRoomInfo.setRoom_token(voiceRoomInfo.getRoom_token());
        mVoiceRoomInfo.setRoom_name(voiceRoomInfo.getRoom_name());
        if (!isOnSeat() && BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().setPullUrlInfo(mVoiceRoomInfo, partyLiveType);
            BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
        }
    }


    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData data) {
            // 如果遇到错误码13001，13002，13003，403，404，414，表示无法进入聊天室，此时应该调用离开聊天室接口。
            if (data.status == StatusCode.LOGINED) {
                if (isFirstLogin) {
                    getP().joinParty(mId, "login");
                }
                isFirstLogin = true;
            }
        }
    };


    private void registerObservers(boolean register) {
//        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);

    }


    /**
     * 命令消息接收观察者
     */
    private final Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            final ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getContent());
            try {
                if (null == chatMsg) {
                    return;
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_MICROPHONE_NOTICE)) {  //麦位
                    switch (chatMsg.cmd) {
                        case "invite":   //管理员邀请上麦
                            ChatMsg.Inviter inviter = JSON.parseObject(chatMsg.content, ChatMsg.Inviter.class);
                            if (inviter == null) return;
                            if (mInviteOnSeatPopWindow == null)
                                mInviteOnSeatPopWindow = new InviteOnSeatPopWindow(BasePartyRoomActivity.this);
                            mInviteOnSeatPopWindow.addOnClickListener(new InviteOnSeatPopWindow.OnSelectListener() {
                                @Override
                                public void agree() {
                                    mCompositeDisposable.add(new RxPermissions(BasePartyRoomActivity.this)
                                            .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                    , Manifest.permission.READ_EXTERNAL_STORAGE)
                                            .subscribe(grant -> {
                                                if (grant) {
                                                    getP().agreeSeat(mId, service.getUserId(), inviter.inviter);
                                                    BasePartyRoomActivity.this.visitPartyList(2, 3, "", -1);
                                                } else {
                                                    ToastUtil.showToast(BaseApplication.getInstance(), "请允许录音权限");
                                                }
                                            }));
                                }

                                //拒绝上麦
                                @Override
                                public void refuse() {
                                    getP().refuseOnSeat(mId, inviter.inviter);
                                }
                            });
                            mInviteOnSeatPopWindow.showPopupWindow();
                            break;
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.WINDOW_TIPS)) { //系统提示
                    if (TextUtils.equals(chatMsg.cmd, ChatMsg.MSG_WINDOW_BOTTOM_WARN)) { //违规消息
                        ChatTipsContent chatTips = JSON.parseObject(chatMsg.content, ChatTipsContent.class);
                        Utils.logE(chatTips.msg_id);
                        failedToSendMessage(chatTips.msg_id);

                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_USER_EXT)) {  //拓展字段获取
                    ChatMsg.RoomExtInfo roomExtInfo = JSON.parseObject(chatMsg.content, ChatMsg.RoomExtInfo.class);
                    if (roomExtInfo != null) {
                        richLevel = roomExtInfo.rich_level + "";
                        badge = roomExtInfo.badge;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private boolean isFirstSend = true;
    private String richLevel, badge;


    public void failedToSendMessage(String msg_id) {

    }

    public abstract void visitPartyList(int type, int from_type, String content, int tag);

    public abstract void lineUpMacUserRefresh(int userId);


    /**
     * 显示公告弹窗
     *
     * @param isTimeEnd 是否需要倒计时
     */
    protected void showAnnouncement(String title, String content, boolean isTimeEnd) {
        if (mAnnouncePop == null)
            mAnnouncePop = new PartyAnnouncementPopWindow(this);
        mAnnouncePop.setAnnouncementInfo(title, content);
        mAnnouncePop.showPopupWindow(mBind.tvAnnouncement);
        if (isTimeEnd) {
            if (null == mAnnouncementBack)
                mAnnouncementBack = new CountBackUtils();
            mAnnouncementBack.countBack(15, new CountBackUtils.Callback() {
                @Override
                public void countBacking(long time) {
                }

                @Override
                public void finish() {
                    mAnnouncePop.dismiss();
                }
            });
        }

    }

    public void setData(JoinPartyDto data, String type) {
        mJoinType = type;
        //进入相同的房间
        if (mLastPartyId == mId && TextUtils.equals(type, "in")) {
            getP().joinLeaveParty("open", mId, 1);
        }
        mData = data;
        if (!mIsNetVisit) {
            visitPartyList(1, 0, "", -1);
        }
        if (mData != null && mData.getUser() != null) {
            shutUpToastMsg = mData.getUser().getShutup_toast_msg();
        }
        if (mData == null) return;
        PartyInfoDto partyInfo = mData.getRoom();
        mBind.viewMessage.setVisibility(mMessageNum > 0 ? View.VISIBLE : View.GONE);
        if (partyInfo != null) {
            mFightPattern = partyInfo.getFight_pattern();
            mMicrophonePattern = partyInfo.getMicrophonePattern();
            mRoomShutUp = partyInfo.getShutup();
            GlideUtils.loadImage(this, mBind.ivBg, partyInfo.getBg_icon(), R.drawable.party_bg_room1);
            if (mFightPattern == 1) {  //1：普通模式  2：pk 模式
                mBind.rvSeat.setVisibility(View.VISIBLE);
                mBind.clPk.clPkInfo.setVisibility(View.GONE);
            } else {
                //pk
                mBind.rvSeat.setVisibility(View.GONE);
                mBind.clPk.clPkInfo.setVisibility(View.VISIBLE);
                PkDataDto pkData = partyInfo.getPk_data();
                if (pkData != null) {
                    PartyPkSaveDto pkInfo = pkData.getPk_info();  //pk信息
                    mPkInfoId = pkData.getPk_info_id();
                    if (pkInfo != null) {
                        uploadScore(pkData.getRed_cnt(), pkData.getBlue_cnt());
                        if (pkInfo.status == 0 || pkInfo.status == 1) { //1准备阶段  //'2进行中 0已结束 ,1准备阶段，3进入pk结束阶段 4惩罚开始 ',
                            mBind.svgPk.setVisibility(View.VISIBLE);
                            if (getP().isAdminAndOwner(mData)) {
                                mBind.clPk.ivStartPk.setVisibility(View.VISIBLE);
                                AnimationUtil.createAvatarAnimation(mBind.clPk.ivStartPk);
                            }
                        } else if (pkInfo.status == 2) {  //2进行中
                            getP().playAirdrop(svgaParser, mBind.svgPk, "pk_prepare.svga");
                            mBind.clPk.ivStartPk.setVisibility(View.INVISIBLE);
                            mBind.clPk.ivStartPk.clearAnimation();
                            if (getP().isAdminAndOwner(mData)) {
                                mBind.clPk.ivEndPk.setVisibility(View.VISIBLE);
                                mBind.clPk.ivEndPk.setText("结束PK");
                            }
                            mBind.clPk.tvPkTime.setText(TimeUtils.fromSecond(pkInfo.count_down));
                            if (null == mPkBack)
                                mPkBack = new CountBackUtils();
                            mPkBack.countBack(pkInfo.count_down, new CountBackUtils.Callback() {
                                @Override
                                public void countBacking(long time) {
                                    mBind.clPk.tvPkTime.setText(TimeUtils.fromSecond((int) time));
                                }

                                @Override
                                public void finish() {
                                    if (getP().isAdminAndOwner(mData))
                                        getP().stopPartyPk(mId, mPkInfoId);
                                    mBind.clPk.tvPkTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                    mBind.clPk.tvPkTime.setText("惩罚阶段");
                                }
                            });
                        } else if (pkInfo.status == 3) {   //3进入pk结束阶段
                            mBind.svgPk.setVisibility(View.INVISIBLE);

                        } else if (pkInfo.status == 4) {   //惩罚开始
                            mBind.svgPk.setVisibility(View.INVISIBLE);
                            if (mPkBack != null)
                                mPkBack.destroy();
                            mBind.clPk.tvPkTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            mBind.clPk.tvPkTime.setText("惩罚阶段");
                            ChatMsg.PartyPkStop partyPkStop = new ChatMsg.PartyPkStop();
                            partyPkStop.win_site = pkData.getWin_side();
                            mBind.clPk.ivOtherTeam.setVisibility(View.VISIBLE);
                            mBind.clPk.ivWeTeam.setVisibility(View.VISIBLE);
                            if (pkData.getWin_side() == 0) {    //0平局 1红方胜利 2蓝方胜利
                                mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_same);
                                mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_same);
                            } else if (pkData.getWin_side() == 1) {
                                mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_win);
                                mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_lose);
                            } else if (pkData.getWin_side() == 2) {
                                mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_lose);
                                mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_win);
                            }
                            if (getP().isAdminAndOwner(mData)) {
                                mBind.clPk.ivEndPk.setVisibility(View.VISIBLE);
                                mBind.clPk.ivEndPk.setText("结束惩罚");
                            }
                            if (null == mPkBack)
                                mPkBack = new CountBackUtils();
                            mPkBack.countBack(pkInfo.count_down, new CountBackUtils.Callback() {
                                @Override
                                public void countBacking(long time) {
                                    mBind.clPk.tvPkTime.setText("惩罚阶段: " + TimeUtils.fromSecond((int) time));
                                }

                                @Override
                                public void finish() {
                                    getP().stopPunish(mId, mPkInfoId);
                                }
                            });
                        }
                        //pk礼物榜
                        mBind.seatRanking.conRootSeatRanking.setVisibility(pkInfo.status == 2 || pkInfo.status == 3 ? View.VISIBLE : View.GONE);
                    }
                    if (pkData.getPk_bank() != null) {
                        setSeatRanking(pkData.getPk_bank());
                    }
                }
            }
            setRoomName(partyInfo.getRoom_name());
            setRank(partyInfo);
            //热度
            mBind.tvHotValue.setText(data.getRoom().getHeat());
            mBind.tvVolumeValue.setText(partyInfo.getVolume());
            if (mVoiceRoomInfo == null)
                mVoiceRoomInfo = new VoiceRoomInfo();
            mVoiceRoomInfo.setRoom_id(mRoomId);
            mVoiceRoomInfo.push_url = partyInfo.getPush_url();
            mVoiceRoomInfo.http_pull_url = partyInfo.getHttp_pull_url();
            mVoiceRoomInfo.rtmp_pull_url = partyInfo.getRtmp_pull_url();
            mVoiceRoomInfo.setRoom_token(partyInfo.getIm_room_token());
            mVoiceRoomInfo.setRoom_name(partyInfo.getIm_room_name());
            mVoiceRoomInfo.partyLiveType = partyLiveType;
            //第一次进入派对
            if (mLastPartyId == 0 && mLastPartyId != mId && TextUtils.equals(mJoinType, "in")) {
                initVoiceRoom(mVoiceRoomInfo);
            }
            //第一次进入显示
            if (TextUtils.equals(type, "in") && data.getRoom() != null && !TextUtils.isEmpty(data.getRoom().getAnnouncement())) {
                visitPartyList(3, 2, data.getRoom().getAnnouncement(), -1);
                showAnnouncement(partyInfo.getTitle(), data.getRoom().getAnnouncement(), true);
            }
            mBind.tvApplyNum.setVisibility(partyInfo.getApply_count() == 0 ? View.GONE : View.VISIBLE);
            mBind.tvApplyNum.setText(String.valueOf(partyInfo.getApply_count()));
        }
        if (mData != null && mData.getUser() != null) {
            mUserShutUp = mData.getUser().getShutup();
        }
        if (mData == null) return;
        if (mData.getActivity() != null && mData.getActivity().size() > 0) {//派对活动列表
            mBind.banner.setVisibility(View.VISIBLE);
            mBind.banner.setAdapter(new BannerImageAdapter<ChatMsgActivity>(mData.getActivity()) {
                @Override
                public void onBindView(BannerImageHolder holder, ChatMsgActivity data, int position, int size) {
                    GlideUtils.loadRouteImage(mActivity, holder.imageView, data.getIcon());
                    holder.imageView.setOnClickListener(v -> {
                        CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(data.getLink(), new ConfigInfo.Option(data.getOption().getIs_topbar(), data.getOption().getTopbar_color())), 0, 1);
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("派对内新年活动贴片点击", "party_patch_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), data.getLink(), 26, position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, "派对内新年活动贴片点击", mId + "", -1)), null
                                );

                    });
                }
            })
                    .setPageTransformer(new ZoomOutPageTransformer())
                    .addBannerLifecycleObserver(this)
                    .setLoopTime(5000)//设置轮播间隔时间
                    .setScrollTime(400)
                    .setUserInputEnabled(true); //禁止手动滑动
            if (mData.getActivity().size() > 1) { //大于1个才循环播放
                mBind.banner.setIndicator(mBind.indicator, false).start();
                mBind.indicator.setVisibility(View.VISIBLE);
            } else {
                mBind.banner.stop();
                mBind.indicator.setVisibility(View.GONE);
            }
        }

        //排行
        if (partyInfo != null && mData.getRank().size() > 0) {
            Collections.reverse(mData.getRank());
            mWatcherAdapter.setList(mData.getRank());
            mBind.ivHourArrow.setVisibility(View.INVISIBLE);
        } else {
            mBind.ivHourArrow.setVisibility(View.VISIBLE);
        }
        //当前位置
        if (mFightPattern == 1) {  //1：普通模式  2：pk 模式
            if (mData.getMicrophone() != null && mData.getMicrophone().size() > 0) {
                int size = mData.getMicrophone().size();
                //主持
                if (mData.getMicrophone().get(0) != null) {
                    setHostInfo(mData.getMicrophone().get(0));
                }
                for (int i = 0; i < size; i++) {
                    if (mData.getMicrophone().get(i).getIndex() > 0 && i - 1 >= 0) {
                        mSeatAdapter.setData(i - 1, mData.getMicrophone().get(i));
                    }
                    if (mData.getMicrophone().get(i).getUser_id() == service.getUserId())
                        mVoiceRoomSeat = mData.getMicrophone().get(i);
                }
            }
            if (mData.getRoom() != null) {
                mSeatAdapter.setLoveSwitch(mData.getRoom().getLove_switch());
            }
        } else {
            //pk
            if (mData.getMicrophone() != null && mData.getMicrophone().size() > 0) {
                int size = mData.getMicrophone().size();
                //主持
                if (mData.getMicrophone().get(0) != null) {
                    setHostInfo(mData.getMicrophone().get(0));
                }
                for (int i = 0; i < size; i++) {
                    if (mData.getMicrophone().get(i).getIndex() > 0) {
                        if (mData.getMicrophone().get(i).getIndex() <= 4 && mData.getMicrophone().get(i).getIndex() - 1 >= 0) {
                            mWeTeamAdapter.setData(mData.getMicrophone().get(i).getIndex() - 1, mData.getMicrophone().get(i));
                        } else if (mData.getMicrophone().get(i).getIndex() > 4 && mData.getMicrophone().get(i).getIndex() - 5 >= 0) {
                            mOtherTeamAdapter.setData(mData.getMicrophone().get(i).getIndex() - 5, mData.getMicrophone().get(i));
                        }
                        if (mData.getMicrophone().get(i).getUser_id() == service.getUserId())
                            mVoiceRoomSeat = mData.getMicrophone().get(i);
                    }
                }
                if (mData.getRoom() != null) {
                    mWeTeamAdapter.setLoveSwitch(mData.getRoom().getLove_switch());
                    mOtherTeamAdapter.setLoveSwitch(mData.getRoom().getLove_switch());
                }
            }
        }
        updateMacMode();
        //麦上状态
        if (isOnSeat()) {
            mBind.ivMute.setImageResource(getP().getVoiceSeat(mData, service.getUserId()).getSilence_switch() == 1 ? R.drawable.party_ic_voice_close : R.drawable.party_ic_voice_open);
            mBind.ivMute.setVisibility(View.VISIBLE);
            mBind.ivExpression.setVisibility(View.VISIBLE);
            mBind.llSendMessage.setVisibility(View.INVISIBLE);
            mBind.ivSendMessage.setVisibility(View.VISIBLE);
        } else {
            if (partyLiveType == 0 && mLastPartyId != mId) {   //cdn
                setPullUrlInfo(mVoiceRoomInfo);
            } else {
                if (mLastPartyId == 0 && mLastPartyId != mId && TextUtils.equals(mJoinType, "in")) {
                    setPullUrlInfo(mVoiceRoomInfo);
                }
            }
        }
        //重置麦位后
        if (TextUtils.equals(type, "reset")) {
            setPullUrlInfo(mVoiceRoomInfo);
        }
        //聊天室设置是否房管和麦上用户
        if (mMsgAdapter != null) {
            mMsgAdapter.setIsAdminOrOnSeat(getP().isAdmin(mData) || isOnSeat());
        }
        if (mData != null && mData.getUser() != null) {
            setShutUp(mData.getUser().getShutup_toast_msg());
        } else {
            setShutUp("");
        }
        if (getP().isSuper(mData))
            WatermarkView.getInstance().show(this, (service != null && service.getUserInfo() != null) ? service.getUserInfo().getNickname() : "");
        if (data.getRoom().getTurntable_skin() == 0) {
            getP().playAirdrop(svgaParser, mBind.svgaLucky, "party_lucky_turntable.svga");
        } else {
            getP().playAirdrop(svgaParser, mBind.svgaLucky, "activity_party_lucky_turntable.svga");
        }
        if (!isResumeOnce) {
            visitPartyList(1, 0, "", -1);
        }
    }


    private void setRank(PartyInfoDto partyInfoDto) {
        //排行榜
        if (partyInfoDto.getHour_rank() == 0) {
            mBind.tvHourRank.setText("小时榜 暂未上榜");
        } else {
            mBind.tvHourRank.setText(new SpannableStringUtils.Builder().append("小时榜 保持在").append("第" + partyInfoDto.getHour_rank() + "名").setForegroundColor(Utils.getColor(R.color.color_F8D423)).create());
        }
    }

    /**
     * 切换麦序模式
     */
    public void updateMacMode() {
        if (mFightPattern == 2) {
            mBind.flSeatOn.setVisibility(View.VISIBLE);
        } else {
            if (mMicrophonePattern == 1) {  //  自由模式
                mBind.flSeatOn.setVisibility(View.GONE);
            } else {   //麦序模式
                mBind.flSeatOn.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 设置房间名称
     */
    private void setRoomName(String name) {
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.party_ic_pretty_code);
        //靓号
        if (mData == null || mData.getRoom() == null) return;
        if (mData.getRoom().getRoom_code_pretty() == 0) {
            mBind.tvPartyCode.setCompoundDrawablesWithIntrinsicBounds(null,
                    null, null, null);
            mBind.tvPartyCode.setText("ID" + mData.getRoom().getRoom_code() + "   ");
        } else {
            mBind.tvPartyCode.setCompoundDrawablesWithIntrinsicBounds(drawable,
                    null, null, null);
            mBind.tvPartyCode.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 4));
            mBind.tvPartyCode.setText("ID" + mData.getRoom().getRoom_code_pretty() + "   ");
        }
        mBind.tvPartyName.setText(name);
        mBind.tvPartyName.setSelected(true);
        mBind.tvPartyName.setHorizontallyScrolling(true);
    }


    /**
     * 更新cost
     */
    public void uploadScore(int left, int right) {
        View sLeft = mBind.clPk.viewScoreLeft;
        View sRight = mBind.clPk.viewScoreRight;
        int width = sLeft.getMeasuredWidth();
        LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) sLeft.getLayoutParams();
        LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) sRight.getLayoutParams();
        float weightLeft = 1, weightRight = 1;
        if (left == 0 && right == 0) {

        } else if (1f * left / right < 0.2 || 1f * right / left < 0.2) {
            if (left > right) {
                weightLeft = 0.8f;
                weightRight = 0.2f;
            } else {
                weightLeft = 0.2f;
                weightRight = 0.8f;
            }
        } else {
            weightLeft = left;
            weightRight = right;

        }
        leftParams.weight = weightLeft;
        rightParams.weight = weightRight;
        sLeft.setLayoutParams(leftParams);
        sRight.setLayoutParams(rightParams);
        Utils.runOnUiThread(() -> sLeft.post(() -> {
            LinearLayout.LayoutParams pk = (LinearLayout.LayoutParams) mBind.svgPk.getLayoutParams();
            pk.leftMargin = sLeft.getMeasuredWidth() - (mBind.svgPk.getWidth() / 2);
            mBind.svgPk.setLayoutParams(pk);
        }));
        mBind.clPk.tvScoreMine.setText("" + left);
        mBind.clPk.tvScoreOther.setText(right + "");
    }


    /**
     * 设置是否被禁言
     */
    private void setShutUp(String limitDeadline) {

        mBind.tvContent.setText("说点什么…");
        if (mRoomShutUp == 1) {   //0：正常 1：禁言
            if (!getP().isAdmin(mData)) {
                mBind.tvContent.setText("全员禁言中");
                shutUpToastMsg = limitDeadline;
            }

        } else {
            if (mUserShutUp == 1) {
                shutUpToastMsg = limitDeadline;
                mBind.tvContent.setText("您已被禁言");
            } else {
                shutUpToastMsg = "";
            }
        }
    }

    /**
     * 设置主持信息
     */
    private void setHostInfo(VoiceRoomSeat seat) {
        mHostSeat = seat;
        mBind.tvChatLove.setText(TextUtils.isEmpty(mHostSeat.getCost()) ? "0" : mHostSeat.getCost());

        if (seat.getUser_id() == 0) {
            mBind.svgaHead.setVisibility(View.INVISIBLE);
            mBind.svgaHead.clearAnimation();
            mBind.ivFrame.setBackground(null);
            clearHostAnimation();
        } else {
            if (seat.getAvatar_id() != 0) {
                mBind.svgaHead.setVisibility(View.VISIBLE);
                getP().playAirdrop(svgaParser, mBind.svgaHead, seat.getAvatar_id(), mBind.ivFrame);
            }
        }
        if (!TextUtils.isEmpty(mHostSeat.getIcon())) {
            if (mHostSeat.getIcon().startsWith("http"))
                GlideUtils.loadCircleImage(this, mBind.ivHostAvatar, mHostSeat.getIcon(), R.mipmap.ic_default_avatar);
            else {
                GlideUtils.loadCircleImage(this, mBind.ivHostAvatar, CommonUtil.getHttpUrlHead() + mHostSeat.getIcon(), R.mipmap.ic_default_avatar);
            }
        } else {
            if (mHostSeat.getLock() == 1) {  // 是否锁定了麦位
                GlideUtils.loadImage(this, mBind.ivHostAvatar, R.drawable.party_ic_lock_seat);
            } else {
                mBind.ivHostAvatar.setImageResource(R.drawable.party_ic_seat);
            }
        }
        if (TextUtils.isEmpty(mHostSeat.getNickname())) {
            mBind.tvHost.setText("");
        } else {
            mBind.tvHost.setText(mHostSeat.getNickname());
        }
        mBind.ivMuteVoice.setVisibility(mHostSeat.getSilence_switch() == 2 ? View.VISIBLE : View.INVISIBLE);
        if (mData.getRoom() != null && mData.getRoom().getLove_switch() == 0) {
            mBind.tvChatLove.setVisibility(mHostSeat.getUser_id() == 0 ? View.INVISIBLE : View.VISIBLE);
        } else {
            mBind.tvChatLove.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 判断自己是否在麦上
     */
    public boolean isOnSeat() {
        if (mData != null && mData.getMicrophone() != null) {
            for (int i = 0; i < mData.getMicrophone().size(); i++) {
                if (mData.getMicrophone().get(i).getUser_id() == service.getUserId()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断这个用户是否在麦上
     *
     * @param userId 用户id
     */
    public boolean isOnSeat(int userId) {
        if (mData != null && mData.getMicrophone() != null) {
            for (int i = 0; i < mData.getMicrophone().size(); i++) {
                if (mData.getMicrophone().get(i).getUser_id() == userId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 收到聊天室消息
     */
    @Override
    public void onIncomingMessage(List<ChatRoomMessage> chatRoomMessages) {
        for (ChatRoomMessage message : chatRoomMessages) {
            if (message.getSessionType() != SessionTypeEnum.ChatRoom ||
                    !message.getSessionId().equals(mRoomId)) {
                continue;
            }
            try {
                if (message.getMsgType() == MsgTypeEnum.text) {
                    addData(message);
                } else if (message.getMsgType() == MsgTypeEnum.custom) {
                    ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                    if (chatMsg == null) return;
                    ChatMsg.PartyMsg msg = JSON.parseObject(chatMsg.content, ChatMsg.PartyMsg.class);
                    if (TextUtils.equals(ChatMsg.PARTY_NOTICE, chatMsg.cmd_type)) {  //通知
                        if (TextUtils.equals(ChatMsg.PARTY_JOIN_SYSTEM_NOTICE, chatMsg.cmd)) {   //系统消息推送
                            if (msg != null && chatMsg.to_user_list != null && chatMsg.to_user_list.size() > 0 && chatMsg.to_user_list.contains(String.valueOf(service.getUserId()))) {
                                message.setContent(msg.msg);
                                addData(message);
                            }
                        } else if (TextUtils.equals(ChatMsg.PARTY_WELCOME, chatMsg.cmd)) {   //欢迎ta
                            runOnUiThread(() -> {
                                if (getP().isSuper(mData)) return;
                                message.setContent(msg.msg);
                                addData(message);
                            });
                        } else if (TextUtils.equals(ChatMsg.PARTY_SPECIAL_WELCOME, chatMsg.cmd)) {   //贵族进场
                            runOnUiThread(() -> {
                                if (getP().isSuper(mData)) return;
                                message.setContent(msg.msg);
                                addData(message);
                                if (mData.getRoom().getDress_switch() == 1) {
                                    return;
                                }
                                if (!TextUtils.isEmpty(msg.svg)) {
                                    GiftDto bean = new GiftDto();
                                    bean.name = Utils.getFileName(msg.svg).replace(".svga", "");
                                    bean.animation = msg.svg;
                                    myGiftList.offer(bean);
                                }
                            });
                        } else if (TextUtils.equals("banner", chatMsg.cmd)) {    //贵族进场横幅
                            if (mData.getRoom().getDress_switch() == 1) {
                                return;
                            }
                            ChatMsg.NobleEnterAni nea = JSON.parseObject(chatMsg.content, ChatMsg.NobleEnterAni.class);
                            if (nea != null && nea.id != 0 && Constants.NOBLE_BANNER.contains(nea.id)) {
                                initSvg(nea);
                            }
                        } else if (TextUtils.equals(ChatMsg.PARTY_TURNTABLE_TV, chatMsg.cmd)) {  //转盘通知结果
                            message.setContent(msg.msg);
                            addData(message);
                        } else if (TextUtils.equals("gift_tv", chatMsg.cmd)) {   //礼物飘屏
                            mFloatGift.offer(chatMsg);
                        } else if (TextUtils.equals("close_party_notice", chatMsg.cmd)) {   //关闭聊天室
                            ChatMsg.AlertEvent alert = JSON.parseObject(chatMsg.content, ChatMsg.AlertEvent.class);
                            if (alert != null) {
                                toastTip(alert.msg);
                            }
                            finishActivity();
                        } else if (TextUtils.equals("operate_notice", chatMsg.cmd)) {  //房主或房管将某人设为房管，禁言，拉黑，踢出时，小秘书通知被设置人  msg:消息信息;type确定房管，禁言，拉黑，踢出类型，0房管，1拉黑，2踢出，3禁言
                            if (msg.type == 0) {
                                if (mData != null && mData.getUser() != null)
                                    mData.getUser().setRole_id(32);
                                if (mMsgAdapter != null) {
                                    mMsgAdapter.setIsAdminOrOnSeat(getP().isAdmin(mData) || isOnSeat());
                                }
                            } else if (msg.type == 1) {
                                getP().showShotOffPop(this, msg.operate_nickname, msg.msg);
                            } else if (msg.type == 2) {
                                getP().showShotOffPop(this, msg.font_color, msg.msg);
                            } else if (msg.type == 3) {
                                toastTip(msg.limit_deadline);
                                mUserShutUp = 1;
                                setShutUp(msg.limit_deadline);
                            }
                        } else if (TextUtils.equals("cancel_operate_notice", chatMsg.cmd)) {  //取消房管，禁言，拉黑，踢出，0房管，1拉黑，2踢出，3禁言
                            if (msg.type == 0) {
                                if (mData != null && mData.getUser() != null)
                                    mData.getUser().setRole_id(0);
                                if (mMsgAdapter != null) {
                                    mMsgAdapter.setIsAdminOrOnSeat(getP().isAdmin(mData) || isOnSeat());
                                }
                            } else if (msg.type == 1) {
                            } else if (msg.type == 2) {
                            } else if (msg.type == 3) {
                                ToastUtil.showToast(this, msg.msg);
                                mUserShutUp = 0;
                                setShutUp(msg.limit_deadline);
                            }
                        } else if (TextUtils.equals("update_party_room_notice", chatMsg.cmd)) {    //更新房间公告，背景，标题
                            if (msg.update_data != null) {
                                //背景
                                if (!TextUtils.isEmpty(msg.update_data.bg_icon))
                                    GlideUtils.loadImage(this, mBind.ivBg, msg.update_data.bg_icon, R.drawable.party_bg_room1);
                                //房间封面
                                if (!TextUtils.isEmpty(msg.update_data.icon)) {
                                    mRoomThumb = msg.update_data.icon;
                                }
                                //房间名
                                if (!TextUtils.isEmpty(msg.update_data.room_name)) {
                                    setRoomName(msg.update_data.room_name);
                                }
                                //公告
                                if (!TextUtils.isEmpty(msg.update_data.announcement)) {
                                    if (mData != null && mData.getRoom() != null) {
                                        mData.getRoom().setAnnouncement(msg.update_data.announcement);
                                        mData.getRoom().setTitle(msg.update_data.title);
                                    }
                                    showAnnouncement(msg.update_data.title, msg.update_data.announcement, true);
                                }
                            }
                            if (!TextUtils.isEmpty(msg.msg)) {
                                toastTip(msg.msg);
                            }
                        } else if (TextUtils.equals("mute_all_notice", chatMsg.cmd)) {  //全员禁言
                            mRoomShutUp = msg.is_mute;
                            message.setContent(msg.msg);
                            addData(message);
                            setShutUp(shutUpToastMsg);
                        } else if (TextUtils.equals("microphone_pattern_update_notice", chatMsg.cmd)) {   //上麦模式变化
                            mMicrophonePattern = msg.microphone_pattern;
                            updateMacMode();
                        } else if (TextUtils.equals("rank", chatMsg.cmd)) {   //榜单排名变化
                            NoticeRankDto rank = JSON.parseObject(chatMsg.content, NoticeRankDto.class);
                            //排行
                            if (rank != null) {
                                if (rank.rank != null && rank.rank.size() > 0) {
                                    mData.getRank().clear();
                                    mData.getRank().addAll(rank.rank);
                                    Collections.reverse(mData.getRank());
                                    mWatcherAdapter.setList(mData.getRank());
                                    mBind.ivHourArrow.setVisibility(View.INVISIBLE);
                                } else {
                                    mBind.ivHourArrow.setVisibility(View.VISIBLE);
                                }
                                if (mData != null && mData.getRoom() != null) {
                                    mData.getRoom().setHour_rank(rank.hour_rank);
                                    mData.getRoom().setHour_rank_before_diff(rank.hour_rank_before_diff);
                                    if (!TextUtils.isEmpty(rank.volume) && !TextUtils.equals("0", rank.volume))
                                        mBind.tvVolumeValue.setText(rank.volume);
                                    setRank(mData.getRoom());
                                }
                            }
                        } else if (TextUtils.equals("heat", chatMsg.cmd)) {   //热度
                            NoticeRankDto rank = JSON.parseObject(chatMsg.content, NoticeRankDto.class);
                            if (rank != null) {
                                mBind.tvHotValue.setText(rank.heat);
                                if (mData.getRoom() != null) {
                                    mData.getRoom().setHour_rank(rank.hour_rank);
                                    mData.getRoom().setHour_rank_before_diff(rank.hour_rank_before_diff);
                                    setRank(mData.getRoom());
                                }
                            }
                        } else if (TextUtils.equals("apply_count", chatMsg.cmd)) {  //麦位申请通知
                            NoticeRankDto rank = JSON.parseObject(chatMsg.content, NoticeRankDto.class);
                            if (rank != null) {
                                mBind.tvApplyNum.setVisibility(rank.apply_count == 0 ? View.GONE : View.VISIBLE);
                                mBind.tvApplyNum.setText(String.valueOf(rank.apply_count));
                            }
                        } else if (TextUtils.equals(ChatMsg.NOBILITY_LEVEL_UP, chatMsg.cmd)) {  //贵族升级通知
                            ChatMsg.NobilityLevelUp levelUp = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
                            if (levelUp != null) {
                                richLevel = levelUp.level + "";
                                badge = levelUp.badge_small;
                                isFirstSend = true;
                            }
                            message.setContent(msg.msg);
                            addData(message);
                        } else if (TextUtils.equals(ChatMsg.NOBILITY_LEVEL_UP_SPECIAL, chatMsg.cmd)) {  //贵族升级通知飘萍
                            mFloatGift.offer(chatMsg);
                        }
                    } else if (TextUtils.equals(ChatMsg.PARTY_ROOM, chatMsg.cmd_type)) {   //风控提示
                        if (TextUtils.equals(chatMsg.cmd, ChatMsg.CMD_WIN_CONTROL)) {//风控提示
                            ChatMsg.WinControl rank = JSON.parseObject(chatMsg.content, ChatMsg.WinControl.class);
                            if (rank != null) {
                                Utils.logE("风控收到提示:" + rank.msg);
                                if (rank.user_id == service.getUserId()) {
                                    addWarnTips(rank.user_msg);
                                } else {
                                    addWarnTips(rank.msg);
                                }
                            }
                        }
                    } else if (TextUtils.equals(ChatMsg.PARTY_TURNTABLE_TV_ALL, chatMsg.cmd_type)) {  // 全服广播
                        if (TextUtils.equals("gift_tv", chatMsg.cmd) || TextUtils.equals("turntable_tv", chatMsg.cmd) || TextUtils.equals("turntable_top", chatMsg.cmd)) {   //礼物飘屏
                            mFloatGift.offer(chatMsg);
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_MICROPHONE_NOTICE)) {  //麦位
                        switch (chatMsg.cmd) {
                            case "online":  //通知用户上麦
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        VoiceRoomInfo voiceRoomInfo = JSON.parseObject(chatMsg.content, VoiceRoomInfo.class);
                                        if (voiceRoomInfo != null) {
                                            //是否第一个
                                            boolean isFirst = voiceRoomInfo.isIs_first();
                                            if (voiceRoomInfo.user_id == service.getUserId()) {
                                                if (isFirst) {   // 第一个用户创建房间，其他用户加入房间
                                                    initVoiceRoom(voiceRoomInfo);
                                                    if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                                                        BaseMusicHelper.get().getPartyService().enterRoom(true, mVoiceRoomSeat);
                                                } else {
                                                    if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                                                        BaseMusicHelper.get().getPartyService().onSeat(voiceRoomInfo, createUser(), service.getUserId(), partyLiveType);
                                                }
                                                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                                                    BaseMusicHelper.get().getPartyService().muteAudio();
                                            }
                                            VoiceRoomSeat seat = new VoiceRoomSeat();
                                            seat.setIndex(voiceRoomInfo.index);
                                            seat.setLock(0);
                                            seat.setCost(voiceRoomInfo.cost);
                                            seat.setAvatar(voiceRoomInfo.avatar);
                                            seat.setAvatar_id(voiceRoomInfo.avatar_id);
                                            seat.setSilence_switch(voiceRoomInfo.shutup);
                                            seat.setNickname(voiceRoomInfo.getNickname());
                                            seat.setUser_id(voiceRoomInfo.user_id);
                                            seat.setIcon(voiceRoomInfo.icon);
                                            //通知排麦人数列表popwindow刷新
                                            lineUpMacUserRefresh(voiceRoomInfo.user_id);
                                            if (mData != null && mData.getMicrophone() != null) {
                                                int size = mData.getMicrophone().size();
                                                for (int i = 0; i < size; i++) {
                                                    if (mData.getMicrophone().get(i).getIndex() == voiceRoomInfo.index) {
                                                        VoiceRoomSeat voiceSeat = mData.getMicrophone().get(i);
                                                        voiceSeat.setNickname(voiceRoomInfo.getNickname());
                                                        voiceSeat.setUser_id(voiceRoomInfo.user_id);
                                                        voiceSeat.setIndex(voiceRoomInfo.index);
                                                        voiceSeat.setIcon(voiceRoomInfo.icon);
                                                        voiceSeat.setAvatar(voiceRoomInfo.avatar);
                                                        voiceSeat.setAvatar_id(voiceRoomInfo.avatar_id);
                                                        voiceSeat.setCost(voiceRoomInfo.cost);
                                                        voiceSeat.setSilence_switch(voiceRoomInfo.shutup);
                                                        voiceSeat.setLock(0);
                                                        mData.getMicrophone().set(i, voiceSeat);
                                                    }
                                                }
                                            }
                                            if (voiceRoomInfo.index == 0) {
                                                mHostSeat = seat;
                                                setHostInfo(mHostSeat);
                                            }
                                            if (mFightPattern == 1) {   //普通模式
                                                int size = mSeatAdapter.getData().size();
                                                for (int i = 0; i < size; i++) {
                                                    if (mSeatAdapter.getData().get(i).getIndex() == voiceRoomInfo.index) {
                                                        VoiceRoomSeat voiceSeat = mSeatAdapter.getData().get(i);
                                                        voiceSeat.setNickname(voiceRoomInfo.getNickname());
                                                        voiceSeat.setUser_id(voiceRoomInfo.user_id);
                                                        voiceSeat.setIcon(voiceRoomInfo.icon);
                                                        voiceSeat.setAvatar(voiceRoomInfo.avatar);
                                                        voiceSeat.setAvatar_id(voiceRoomInfo.avatar_id);
                                                        voiceSeat.setCost(voiceRoomInfo.cost);
                                                        voiceSeat.setIndex(voiceRoomInfo.index);
                                                        voiceSeat.setSilence_switch(voiceRoomInfo.shutup);
                                                        voiceSeat.setLock(0);
                                                        mSeatAdapter.setData(i, voiceSeat);
                                                    }
                                                }
                                            } else {
                                                if (voiceRoomInfo.index <= 4) {
                                                    int size = mWeTeamAdapter.getData().size();
                                                    for (int i = 0; i < size; i++) {
                                                        if (mWeTeamAdapter.getData().get(i).getIndex() == voiceRoomInfo.index) {
                                                            VoiceRoomSeat voiceSeat = mWeTeamAdapter.getData().get(i);
                                                            voiceSeat.setNickname(voiceRoomInfo.getNickname());
                                                            voiceSeat.setUser_id(voiceRoomInfo.user_id);
                                                            voiceSeat.setIcon(voiceRoomInfo.icon);
                                                            voiceSeat.setCost(voiceRoomInfo.cost);
                                                            voiceSeat.setAvatar(voiceRoomInfo.avatar);
                                                            voiceSeat.setAvatar_id(voiceRoomInfo.avatar_id);
                                                            voiceSeat.setIndex(voiceRoomInfo.index);
                                                            voiceSeat.setSilence_switch(voiceRoomInfo.shutup);
                                                            voiceSeat.setLock(0);
                                                            mWeTeamAdapter.setData(i, voiceSeat);
                                                        }
                                                    }
                                                } else {
                                                    int size = mOtherTeamAdapter.getData().size();
                                                    for (int i = 0; i < size; i++) {
                                                        if (mOtherTeamAdapter.getData().get(i).getIndex() == voiceRoomInfo.index) {
                                                            VoiceRoomSeat voiceSeat = mOtherTeamAdapter.getData().get(i);
                                                            voiceSeat.setNickname(voiceRoomInfo.getNickname());
                                                            voiceSeat.setUser_id(voiceRoomInfo.user_id);
                                                            voiceSeat.setIndex(voiceRoomInfo.index);
                                                            voiceSeat.setCost(voiceRoomInfo.cost);
                                                            voiceSeat.setAvatar(voiceRoomInfo.avatar);
                                                            voiceSeat.setAvatar_id(voiceRoomInfo.avatar_id);
                                                            voiceSeat.setIcon(voiceRoomInfo.icon);
                                                            voiceSeat.setSilence_switch(voiceRoomInfo.shutup);
                                                            voiceSeat.setLock(0);
                                                            mOtherTeamAdapter.setData(i, voiceSeat);
                                                        }
                                                    }
                                                }
                                            }
                                            initVoiceRoom(mVoiceRoomInfo);
                                            //麦上用户
                                            if (isOnSeat()) {
                                                mBind.ivMute.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                                mBind.ivMute.setImageResource(getP().getVoiceSeat(mData, service.getUserId()) == null ? R.drawable.party_ic_voice_close : getP().getVoiceSeat(mData, service.getUserId()).getSilence_switch() == 0 ? R.drawable.party_ic_voice_open : R.drawable.party_ic_voice_close);
                                                mBind.ivExpression.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                                mBind.llSendMessage.setVisibility(isOnSeat() ? View.INVISIBLE : View.VISIBLE);
                                                mBind.ivSendMessage.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                                                    BaseMusicHelper.get().getPartyService().releaseAudience();
                                                }
                                            } else {
                                                //拉流模式
                                                if (partyLiveType == 0 && isFirst)
                                                    setPullUrlInfo(voiceRoomInfo);
                                            }
                                            if (mMsgAdapter != null) {
                                                mMsgAdapter.setIsAdminOrOnSeat(getP().isAdmin(mData) || isOnSeat());
                                            }
                                        }
                                        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                                            BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
                                        MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, isOnSeat());
                                    }
                                });
                                break;
                            case "offline":  //下麦1
                                runOnUiThread(() -> {
                                    VoiceRoomSeat voiceRoomSeat = JSON.parseObject(chatMsg.content, VoiceRoomSeat.class);
                                    if (voiceRoomSeat != null) {
                                        if (voiceRoomSeat.getUser_id() == service.getUserId()) {
                                            mVoiceRoomSeat = voiceRoomSeat;
                                            if (BaseMusicHelper.get().getPlayService() != null) {
                                                BaseMusicHelper.get().getPlayService().stop();
                                            }
                                            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                                                BaseMusicHelper.get().getPartyService().setLeaveSeat(partyLiveType);
                                                //耳返设置
                                                BaseMusicHelper.get().getPartyService().setEnableEarBack(false);
                                                mEnableEarBack = false;
                                            }
                                            if (mEarphoneSettingPop != null)
                                                mEarphoneSettingPop.setEnableEarBack(mEnableEarBack);
                                        }
                                        if (mHostSeat != null && mHostSeat.getUser_id() == voiceRoomSeat.getUser_id()) {
                                            if (mData != null && mData.getMicrophone() != null) {
                                                int size = mData.getMicrophone().size();
                                                for (int i = 0; i < size; i++) {
                                                    if (mData.getMicrophone().get(i).getUser_id() == voiceRoomSeat.getUser_id()) {
                                                        mData.getMicrophone().set(i, resetSeat(mData.getMicrophone().get(i)));
                                                    }
                                                }
                                            }
                                            voiceRoomSeat.setCost("");
                                            voiceRoomSeat.setLock(0);
                                            voiceRoomSeat.setAvatar("");
                                            voiceRoomSeat.setIs_mvp(0);
                                            voiceRoomSeat.setSilence_switch(0);
                                            mHostSeat = voiceRoomSeat;
                                            mHostSeat.setUser_id(0);
                                            setHostInfo(mHostSeat);
                                        } else {
                                            if (mFightPattern == 1) {   //普通模式
                                                int sSize = mSeatAdapter.getData().size();
                                                for (int i = 0; i < sSize; i++) {
                                                    if (mSeatAdapter.getData().get(i).getUser_id() == voiceRoomSeat.getUser_id()) {
                                                        mSeatAdapter.setData(i, resetSeat(mSeatAdapter.getData().get(i)));
                                                    }
                                                }
                                            } else {
                                                int size = mWeTeamAdapter.getData().size();
                                                for (int i = 0; i < size; i++) {
                                                    if (voiceRoomSeat.getUser_id() == mWeTeamAdapter.getData().get(i).getUser_id()) {
                                                        mWeTeamAdapter.setData(i, resetSeat(mWeTeamAdapter.getData().get(i)));
                                                    }
                                                }
                                                int seatSize = mOtherTeamAdapter.getData().size();
                                                for (int i = 0; i < seatSize; i++) {
                                                    if (voiceRoomSeat.getUser_id() == mOtherTeamAdapter.getData().get(i).getUser_id()) {
                                                        mOtherTeamAdapter.setData(i, resetSeat(mOtherTeamAdapter.getData().get(i)));
                                                    }
                                                }
                                            }
                                        }
                                        if (mData != null && mData.getMicrophone() != null) {
                                            int seatSize = mData.getMicrophone().size();
                                            for (int i = 0; i < seatSize; i++) {
                                                if (mData.getMicrophone().get(i).getUser_id() == voiceRoomSeat.getUser_id()) {
                                                    mData.getMicrophone().set(i, resetSeat(mData.getMicrophone().get(i)));
                                                }
                                            }
                                        }
                                        mBind.ivMute.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                        mBind.ivExpression.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                        mBind.llSendMessage.setVisibility(isOnSeat() ? View.INVISIBLE : View.VISIBLE);
                                        mBind.ivSendMessage.setVisibility(isOnSeat() ? View.VISIBLE : View.GONE);
                                        if (partyLiveType == 0 && voiceRoomSeat.getUser_id() == service.getUserId()) {   //拉流模式
                                            setPullUrlInfo(mVoiceRoomInfo);
                                        }
                                    }
                                    //聊天室设置是否房管和麦上用户
                                    if (mMsgAdapter != null) {
                                        mMsgAdapter.setIsAdminOrOnSeat(getP().isAdmin(mData) || isOnSeat());
                                    }
                                    if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                                        BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
                                    }
                                    MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, isOnSeat());
                                });
                                break;
                            case "index_change":   //麦位调整   pk不能跳麦
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        VoiceRoomSeat changeSeat = JSON.parseObject(chatMsg.content, VoiceRoomSeat.class);
                                        if (changeSeat.getIndex() == changeSeat.getOld_index())
                                            return;
                                        if (mFightPattern == 1 && changeSeat != null && mSeatAdapter != null) {   //普通模式
                                            VoiceRoomSeat oldSeat = resetSeat(new VoiceRoomSeat());
                                            //第0个位置切换其他位置
                                            if (changeSeat.getIndex() == 0) {
                                                if (changeSeat.getOld_index() - 1 >= 0) {
                                                    VoiceRoomSeat voiceSeat = mSeatAdapter.getData().get(changeSeat.getOld_index() - 1);
                                                    if (voiceSeat != null) {
                                                        mHostSeat = voiceSeat;
                                                        mHostSeat.setIndex(0);
                                                        setHostInfo(mHostSeat);
                                                    }
                                                    mSeatAdapter.setData(changeSeat.getOld_index() - 1, oldSeat);
                                                }
                                            }
                                            VoiceRoomSeat voiceSeat;
                                            if (changeSeat.getOld_index() - 1 >= 0) {
                                                voiceSeat = mSeatAdapter.getData().get(changeSeat.getOld_index() - 1);
                                            } else {
                                                voiceSeat = mHostSeat;
                                            }
                                            if (changeSeat.getIndex() - 1 >= 0)
                                                mSeatAdapter.setData(changeSeat.getIndex() - 1, voiceSeat);
                                            if (changeSeat.getOld_index() == 0) {
                                                mHostSeat = oldSeat;
                                                mHostSeat.setIndex(0);
                                                setHostInfo(mHostSeat);
                                            } else if (changeSeat.getOld_index() - 1 >= 0) {
                                                mSeatAdapter.setData(changeSeat.getOld_index() - 1, oldSeat);
                                            }
                                            if (mData != null && mData.getMicrophone() != null) {
                                                int size = mData.getMicrophone().size();
                                                for (int i = 0; i < size; i++) {
                                                    if (mData.getMicrophone().get(i).getIndex() == changeSeat.getIndex()) {
                                                        VoiceRoomSeat voiceSeat1 = mData.getMicrophone().get(changeSeat.getOld_index());
                                                        voiceSeat.setIndex(changeSeat.getIndex());
                                                        if (mData.getMicrophone().get(i).getIndex() < 9)
                                                            mData.getMicrophone().set(mData.getMicrophone().get(i).getIndex(), voiceSeat1);
                                                        oldSeat.setIndex(changeSeat.getOld_index());
                                                        mData.getMicrophone().set(changeSeat.getOld_index(), oldSeat);
                                                    }
                                                }
                                            }
                                            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null && mData != null)
                                                BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
                                        }
                                    }
                                });
                                break;

                            case "cost":   //爱意值更新
                                ChatMsg.PartyCost seat = JSON.parseObject(chatMsg.content, ChatMsg.PartyCost.class);
                                if (seat == null) return;
                                if (mHostSeat != null && mHostSeat.getUser_id() == seat.user_id) {
                                    mHostSeat.setCost(seat.cost);
//                                    setHostInfo(mHostSeat);
                                    mBind.tvChatLove.setText(TextUtils.isEmpty(mHostSeat.getCost()) ? "0" : mHostSeat.getCost());
                                }
                                if (mFightPattern == 1) {   //普通模式
                                    int size = mSeatAdapter.getData().size();
                                    for (int i = 0; i < size; i++) {
                                        if (mSeatAdapter.getData().get(i).getUser_id() == seat.user_id) {
                                            VoiceRoomSeat voiceSeat = mSeatAdapter.getData().get(i);
                                            voiceSeat.setCost(seat.cost);
                                            mSeatAdapter.setLoveCost(seat.cost, seat.user_id);
                                        }
                                    }
                                } else {
                                    int size = mWeTeamAdapter.getData().size();
                                    for (int i = 0; i < size; i++) {
                                        if (seat.user_id == mWeTeamAdapter.getData().get(i).getUser_id()) {
                                            VoiceRoomSeat voiceSeat = mWeTeamAdapter.getData().get(i);
                                            voiceSeat.setCost(seat.cost);
                                            voiceSeat.setIs_mvp(0);
//                                            mWeTeamAdapter.setData(i, voiceSeat);
                                            mWeTeamAdapter.setLoveCost(seat.cost, seat.user_id, 0);
                                        } else {
                                            VoiceRoomSeat voiceSeat = mWeTeamAdapter.getData().get(i);
                                            voiceSeat.setIs_mvp(0);
                                            mWeTeamAdapter.setLoveCost(seat.user_id, 0);
                                        }
                                        if (seat.mvp_user_id == mWeTeamAdapter.getData().get(i).getUser_id() && seat.mvp_user_id != 0) {
                                            VoiceRoomSeat voiceSeat = mWeTeamAdapter.getData().get(i);
                                            voiceSeat.setIs_mvp(1);
                                            mWeTeamAdapter.setLoveCost(seat.user_id, 1);
                                        }
                                    }
                                    int size1 = mOtherTeamAdapter.getData().size();
                                    for (int i = 0; i < size1; i++) {
                                        if (seat.user_id == mOtherTeamAdapter.getData().get(i).getUser_id()) {
                                            VoiceRoomSeat voiceSeat = mOtherTeamAdapter.getData().get(i);
                                            voiceSeat.setCost(seat.cost);
                                            voiceSeat.setIs_mvp(0);
                                            mOtherTeamAdapter.setLoveCost(seat.cost, seat.user_id, 0);
                                        } else {
                                            VoiceRoomSeat voiceSeat = mOtherTeamAdapter.getData().get(i);
                                            voiceSeat.setIs_mvp(0);
                                            mOtherTeamAdapter.setLoveCost(seat.user_id, 0);
                                        }
                                        if (seat.mvp_user_id == mOtherTeamAdapter.getData().get(i).getUser_id() && seat.mvp_user_id != 0) {
                                            VoiceRoomSeat voiceSeat = mOtherTeamAdapter.getData().get(i);
                                            voiceSeat.setIs_mvp(1);
                                            mOtherTeamAdapter.setLoveCost(seat.user_id, 1);
                                        }
                                    }
                                    uploadScore(seat.red_cnt, seat.blue_cnt);
                                }
                                if (seat.pk_bank != null && (seat.pk_bank.red_team != null || seat.pk_bank.blue_team != null)
                                        && ((seat.pk_bank.red_team != null && seat.pk_bank.red_team.size() > 0) ||
                                        (seat.pk_bank != null && seat.pk_bank.blue_team != null && seat.pk_bank.blue_team.size() > 0))) {
                                    setSeatRanking(seat.pk_bank);
                                }
                                break;
                            case "cost_switch":   //爱意值开关
                                ChatMsg.RoomSeat status = JSON.parseObject(chatMsg.content, ChatMsg.RoomSeat.class);
                                if (status != null) {
                                    if (mFightPattern == 1) {  // 1:普通模式  2：pk 模式
                                        mSeatAdapter.setLoveSwitch(status.status);
                                    } else {
                                        mWeTeamAdapter.setLoveSwitch(status.status);
                                        mOtherTeamAdapter.setLoveSwitch(status.status);
                                    }
                                    //更新爱意值
                                    if (mData != null && mData.getRoom() != null) {
                                        mData.getRoom().setLove_switch(status.status);
                                    }
                                    if (functionPopWindow != null) {
                                        functionPopWindow.updateLoveData(status.status);
                                    }

                                    if (status.status == 0) {
                                        mBind.tvChatLove.setVisibility(mHostSeat.getUser_id() == 0 ? View.INVISIBLE : View.VISIBLE);
                                    } else {
                                        mBind.tvChatLove.setVisibility(View.INVISIBLE);
                                    }
                                }
                                break;
                            case "lock":   //锁定麦位开关
                                VoiceRoomSeat voiceRoomSeat1 = JSON.parseObject(chatMsg.content, VoiceRoomSeat.class);
                                if (voiceRoomSeat1 != null) {
                                    if (voiceRoomSeat1.getIndex() == 0) {
                                        mHostSeat = voiceRoomSeat1;
                                        if (mHostSeat.getLock() == 1) {
                                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.ivHostAvatar, R.drawable.party_ic_lock_seat);
                                        } else {
                                            GlideUtils.loadCircleImage(BasePartyRoomActivity.this, mBind.ivHostAvatar, mHostSeat.getIcon());
                                            mBind.tvHost.setText(mHostSeat.getNickname());
                                        }
                                        return;
                                    }
                                    int size = mSeatAdapter.getData().size();
                                    for (int i = 0; i < size; i++) {
                                        if (mSeatAdapter.getData().get(i).getIndex() == voiceRoomSeat1.getIndex()) {
                                            mSeatAdapter.getData().get(i).setLock(voiceRoomSeat1.getLock());
                                            mSeatAdapter.setData(i, mSeatAdapter.getData().get(i));
                                        }
                                    }
                                    int wSize = mWeTeamAdapter.getData().size();
                                    for (int i = 0; i < wSize; i++) {
                                        if (voiceRoomSeat1.getIndex() == mWeTeamAdapter.getData().get(i).getIndex()) {
                                            mWeTeamAdapter.getData().get(i).setLock(voiceRoomSeat1.getLock());
                                            mWeTeamAdapter.setData(i, mWeTeamAdapter.getData().get(i));
                                        }
                                    }
                                    int oSize = mOtherTeamAdapter.getData().size();
                                    for (int i = 0; i < oSize; i++) {
                                        if (voiceRoomSeat1.getIndex() == mOtherTeamAdapter.getData().get(i).getIndex()) {
                                            mOtherTeamAdapter.getData().get(i).setLock(voiceRoomSeat1.getLock());
                                            mOtherTeamAdapter.setData(i, mOtherTeamAdapter.getData().get(i));
                                        }
                                    }
                                }
                                break;
                            case "index_reset":   // 清空所有麦位信息
                                if (mData != null && mData.getMicrophone() != null) {
                                    for (int i = 0; i < mData.getMicrophone().size(); i++) {
                                        mData.getMicrophone().set(i, resetSeat(mData.getMicrophone().get(i)));
                                    }
                                }
                                if (mHostSeat != null) {
                                    mHostSeat = resetSeat(new VoiceRoomSeat(-1));
                                    setHostInfo(mHostSeat);
                                }
                                if (mFightPattern == 1) {
                                    for (int i = 0; i < mSeatAdapter.getData().size(); i++) {
                                        mSeatAdapter.setData(i, mSeatAdapter.getData().get(i));
                                    }
                                } else {
                                    for (int i = 0; i < mWeTeamAdapter.getData().size(); i++) {
                                        mWeTeamAdapter.setData(i, resetSeat(mWeTeamAdapter.getData().get(i)));
                                    }
                                    for (int i = 0; i < mOtherTeamAdapter.getData().size(); i++) {
                                        mOtherTeamAdapter.setData(i, resetSeat(mOtherTeamAdapter.getData().get(i)));
                                    }
                                }
                                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                                    BaseMusicHelper.get().getPartyService().leaveRoom();
                                }
                                getP().joinParty(mId, "reset");
                                break;

                            case "shut_up":  //麦位禁言
                                VoiceRoomSeat shutSeat = JSON.parseObject(chatMsg.content, VoiceRoomSeat.class);
                                if (shutSeat != null) {
                                    if (shutSeat.getUser_id() == service.getUserId()) {
                                        mVoiceRoomSeat.setSilence_switch(shutSeat.getStatus());
                                        if (isOnSeat()) {
                                            mBind.ivMute.setImageResource(shutSeat.getStatus() == 1 ? R.drawable.party_ic_voice_close : R.drawable.party_ic_voice_open);
                                            toggleMuteLocalAudio(shutSeat.getStatus());
                                        }
                                    }
                                    if (mHostSeat != null && mHostSeat.getUser_id() == shutSeat.getUser_id()) {
                                        mHostSeat.setSilence_switch(shutSeat.getStatus());
                                        setHostInfo(mHostSeat);
                                    } else {
                                        if (mFightPattern == 1) {   //普通模式
                                            int size = mSeatAdapter.getData().size();
                                            for (int i = 0; i < size; i++) {
                                                if (mSeatAdapter.getData().get(i).getUser_id() == shutSeat.getUser_id()) {
                                                    VoiceRoomSeat voiceSeat = mSeatAdapter.getData().get(i);
                                                    voiceSeat.setSilence_switch(shutSeat.getStatus());
                                                    mSeatAdapter.setData(i, voiceSeat);
                                                }
                                            }
                                        } else {
                                            int size = mWeTeamAdapter.getData().size();
                                            for (int i = 0; i < size; i++) {
                                                if (shutSeat.getUser_id() == mWeTeamAdapter.getData().get(i).getUser_id()) {
                                                    VoiceRoomSeat voiceSeat = mWeTeamAdapter.getData().get(i);
                                                    voiceSeat.setSilence_switch(shutSeat.getStatus());
                                                    mWeTeamAdapter.setData(i, voiceSeat);
                                                }
                                            }
                                            int size1 = mOtherTeamAdapter.getData().size();
                                            for (int i = 0; i < size1; i++) {
                                                if (shutSeat.getUser_id() == mOtherTeamAdapter.getData().get(i).getUser_id()) {
                                                    VoiceRoomSeat voiceSeat = mOtherTeamAdapter.getData().get(i);
                                                    voiceSeat.setSilence_switch(shutSeat.getStatus());
                                                    mOtherTeamAdapter.setData(i, voiceSeat);
                                                }
                                            }
                                        }
                                    }
                                    if (mData != null && mData.getMicrophone() != null) {
                                        int size = mData.getMicrophone().size();
                                        for (int i = 0; i < size; i++) {
                                            if (mData.getMicrophone().get(i).getUser_id() == shutSeat.getUser_id()) {
                                                VoiceRoomSeat voiceSeat = mData.getMicrophone().get(i);
                                                voiceSeat.setSilence_switch(shutSeat.getStatus());
                                                mData.getMicrophone().set(i, voiceSeat);
                                            }
                                        }
                                    }
                                }
                                break;
                            case "big_emoticon":   //大表情
                                ChatMsg.Game game = JSON.parseObject(chatMsg.content, ChatMsg.Game.class);
                                if (game != null) {
                                    if (mHostSeat != null && mHostSeat.getUser_id() == game.user_id) {
                                        showBigEmoticon(mBind.ivBigEmotion, game, msg, message);
                                    }
                                    if (mFightPattern == 1) {   //普通模式
                                        if (mBind.rvSeat.getLayoutManager() == null) {
                                            mBind.rvSeat.setLayoutManager(new GridLayoutManager(this, 4));
                                        }
                                        View itemView = mBind.rvSeat.getLayoutManager().findViewByPosition(game.index - 1);
                                        if (itemView != null) {
                                            ImageView ivGame = itemView.findViewById(R.id.iv_big_emotion);
                                            showBigEmoticon(ivGame, game, msg, message);
                                        }
                                    } else {
                                        if (game.index < 5) {
                                            if (mBind.clPk.rvWeTeam.getLayoutManager() == null) {
                                                mBind.clPk.rvWeTeam.setLayoutManager(new GridLayoutManager(this, 2));
                                            }
                                            View itemView = mBind.clPk.rvWeTeam.getLayoutManager().findViewByPosition(game.index - 1);
                                            if (itemView != null) {
                                                ImageView ivGame = itemView.findViewById(R.id.iv_big_emotion);
                                                showBigEmoticon(ivGame, game, msg, message);
                                            }
                                        } else {
                                            if (mBind.clPk.rvOtherTeam.getLayoutManager() == null) {
                                                mBind.clPk.rvOtherTeam.setLayoutManager(new GridLayoutManager(this, 2));
                                            }
                                            View itemView1 = mBind.clPk.rvOtherTeam.getLayoutManager().findViewByPosition(game.index - 5);
                                            if (itemView1 != null) {
                                                ImageView ivGame = itemView1.findViewById(R.id.iv_big_emotion);
                                                showBigEmoticon(ivGame, game, msg, message);
                                            }
                                        }
                                    }

                                }
                                break;
                        }

                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_ROOM_PK)) {   //pk相关
                        mFightPattern = 2;
                        switch (chatMsg.cmd) {
                            case "pk_save":   //保存pk
                                //pk
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mFightPattern = 2;
                                        mBind.svgPk.setVisibility(View.VISIBLE);
                                        mBind.flSeatOn.setVisibility(View.VISIBLE);
                                        getP().playAirdrop(svgaParser, mBind.svgPk, "pk_prepare.svga");
                                        mBind.rvSeat.setVisibility(View.GONE);
                                        mBind.clPk.clPkInfo.setVisibility(View.VISIBLE);
                                        ChatPartyPkDto chatPartyPkDto = JSON.parseObject(chatMsg.content, ChatPartyPkDto.class);
                                        if (chatPartyPkDto != null) {
                                            mPkInfoId = chatPartyPkDto.pk_info_id;
                                            int pkSize = chatPartyPkDto.pk_users.size();
                                            if (pkSize > 0) {
                                                int size = chatPartyPkDto.pk_users.size();
                                                for (int i = 0; i < size; i++) {
                                                    //主播位置
                                                    if (chatPartyPkDto.pk_users.get(i).getIndex() == 0) {
                                                        mHostSeat = chatPartyPkDto.pk_users.get(i);
                                                        setHostInfo(mHostSeat);
                                                    }
                                                    int weSize = mWeTeamAdapter.getData().size();
                                                    for (int j = 0; j < weSize; j++) {
                                                        if (chatPartyPkDto.pk_users.get(i).getIndex() - 1 >= 0)
                                                            mWeTeamAdapter.setData(chatPartyPkDto.pk_users.get(i).getIndex() - 1, chatPartyPkDto.pk_users.get(i));
                                                    }
                                                    int otherSize = mOtherTeamAdapter.getData().size();
                                                    for (int j = 0; j < otherSize; j++) {
                                                        if (chatPartyPkDto.pk_users.get(i).getIndex() - 5 >= 0)
                                                            mOtherTeamAdapter.setData(chatPartyPkDto.pk_users.get(i).getIndex() - 5, chatPartyPkDto.pk_users.get(i));
                                                    }
                                                    if (mData.getMicrophone() != null) {
                                                        int dataSize = mData.getMicrophone().size();
                                                        for (int j = 0; j < dataSize; j++) {
                                                            //设置当前是否禁言了
                                                            if (mData.getMicrophone().get(j).getUser_id() == chatPartyPkDto.pk_users.get(i).getUser_id()) {
                                                                chatPartyPkDto.pk_users.get(i).setSilence_switch(mData.getMicrophone().get(j).getSilence_switch());
                                                            }
                                                        }
                                                        mData.getMicrophone().set(chatPartyPkDto.pk_users.get(i).getIndex(), chatPartyPkDto.pk_users.get(i));
                                                    }
                                                }
                                            }
                                            mWeTeamAdapter.notifyDataSetChanged();
                                            mOtherTeamAdapter.notifyDataSetChanged();
                                            int size = mSeatAdapter.getData().size();
                                            for (int i = 0; i < size; i++) {
                                                mSeatAdapter.getData().get(i).setLock(0);
                                                mSeatAdapter.setData(i, mSeatAdapter.getData().get(i));
                                            }
                                            mBind.clPk.ivStartPk.setVisibility(View.INVISIBLE);
                                            uploadScore(0, 0);
                                            //有权限的显示PK
                                            if (getP().isAdminAndOwner(mData)) {
                                                mBind.clPk.ivStartPk.setVisibility(View.VISIBLE);
                                                AnimationUtil.createAvatarAnimation(mBind.clPk.ivStartPk);
                                                mBind.clPk.ivEndPk.setText("开始PK");
                                            }
                                            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null && mData != null)
                                                BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
                                        }
                                    }
                                });
                                break;
                            case "pk_start":   //开始Pk
                                mBind.seatRanking.conRootSeatRanking.setVisibility(View.VISIBLE);
                                uploadScore(0, 0);
                                resetCost();
                                ChatMsg.PartyStart partyStart = JSON.parseObject(chatMsg.content, ChatMsg.PartyStart.class);
                                mBind.clPk.ivStartPk.setVisibility(View.INVISIBLE);
                                mBind.clPk.ivStartPk.clearAnimation();
                                mBind.clPk.ivEndPk.setText("结束PK");
                                if (null == mPkBack)
                                    mPkBack = new CountBackUtils();
                                mBind.clPk.tvPkTime.setText("准备阶段: 00:03");
                                mPkBack.countBack(3, new CountBackUtils.Callback() {
                                    @Override
                                    public void countBacking(long time) {
                                        mBind.clPk.tvPkTime.setText("准备阶段: " + TimeUtils.fromSecond((int) time));
                                    }

                                    @Override
                                    public void finish() {

                                    }
                                });
                                if (mStartPkPopWindow == null)
                                    mStartPkPopWindow = new StartPkPopWindow(this);
                                mStartPkPopWindow.initData();
                                mStartPkPopWindow.showPopupWindow();
                                mStartPkPopWindow.addOnClickListener(new StartPkPopWindow.OnSelectListener() {
                                    @Override
                                    public void onEndAnimation() {
                                        if (partyStart != null) {
                                            if (null == mPkBack)
                                                mPkBack = new CountBackUtils();
                                            mPkBack.countBack(partyStart.duration, new CountBackUtils.Callback() {
                                                @Override
                                                public void countBacking(long time) {
                                                    mBind.clPk.tvPkTime.setText(TimeUtils.fromSecond((int) time));
                                                }

                                                @Override
                                                public void finish() {
                                                    if (getP().isAdminAndOwner(mData))
                                                        getP().stopPartyPk(mId, mPkInfoId);
                                                    mBind.clPk.tvPkTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                                    mBind.clPk.tvPkTime.setText("惩罚阶段");
                                                }
                                            });
                                        }
                                        if (getP().isAdminAndOwner(mData))
                                            mBind.clPk.ivEndPk.setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            case "pk_stop":   //结束pk
                                mBind.seatRanking.conRootSeatRanking.setVisibility(View.GONE);
                                resetSeatRankingList();
                                if (mPkBack != null)
                                    mPkBack.destroy();
                                mBind.clPk.tvPkTime.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                mBind.clPk.tvPkTime.setText("惩罚阶段");
                                ChatMsg.PartyPkStop partyPkStop = JSON.parseObject(chatMsg.content, ChatMsg.PartyPkStop.class);
                                if (partyPkStop != null) {
                                    mBind.clPk.ivOtherTeam.setVisibility(View.VISIBLE);
                                    mBind.clPk.ivWeTeam.setVisibility(View.VISIBLE);
                                    if (mPkResultPopWindow == null)
                                        mPkResultPopWindow = new PkResultPopWindow(this, mData.getRoom().getId(), Utils.getText(mBind.tvPartyName));
                                    if (partyPkStop.win_site == 0) {    //0平局 1红方胜利 2蓝方胜利
                                        mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_same);
                                        mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_same);
                                        mPkResultPopWindow.setSeat(mWeTeamAdapter.getData(), partyPkStop);
                                    } else if (partyPkStop.win_site == 1) {
                                        mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_win);
                                        mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_lose);
                                        mPkResultPopWindow.setSeat(mWeTeamAdapter.getData(), partyPkStop);
                                    } else if (partyPkStop.win_site == 2) {
                                        mBind.clPk.ivWeTeam.setImageResource(R.drawable.party_pk_lose);
                                        mBind.clPk.ivOtherTeam.setImageResource(R.drawable.party_pk_win);
                                        mPkResultPopWindow.setSeat(mOtherTeamAdapter.getData(), partyPkStop);
                                    }
                                    mPkResultPopWindow.showPopupWindow();
                                    mBind.clPk.ivEndPk.setText("结束惩罚");
                                    if (null == mPkBack)
                                        mPkBack = new CountBackUtils();
                                    mPkBack.countBack(partyPkStop.punish_time, new CountBackUtils.Callback() {
                                        @Override
                                        public void countBacking(long time) {
                                            mBind.clPk.tvPkTime.setText("惩罚阶段: " + TimeUtils.fromSecond((int) time));
                                        }

                                        @Override
                                        public void finish() {
                                            if (getP().isAdminAndOwner(mData))
                                                getP().stopPunish(mId, mPkInfoId);
                                        }
                                    });
                                }
                                PartyInfoDto partyInfo = mData.getRoom();
                                if (partyInfo != null) {//更新pk信息
                                    if (partyInfo.getFight_pattern() == 2) {
                                        PkDataDto pkData = partyInfo.getPk_data();
                                        if (pkData != null) {
                                            PartyPkSaveDto pkInfo = pkData.getPk_info();
                                            if (pkInfo != null) {
                                                pkInfo.status = 3;
                                            }

                                        }
                                    }
                                }
                                break;
                            case "pk_punish_end":   //结束惩罚
                                uploadScore(0, 0);
                                resetCost();
                                mBind.svgPk.setVisibility(View.INVISIBLE);
                                if (mPkBack != null)
                                    mPkBack.destroy();
                                mBind.clPk.ivOtherTeam.setVisibility(View.GONE);
                                mBind.clPk.ivWeTeam.setVisibility(View.GONE);
                                mBind.clPk.ivEndPk.setVisibility(View.INVISIBLE);
                                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.party_ic_pk);
                                mBind.clPk.tvPkTime.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                                mBind.clPk.tvPkTime.setText("准备阶段");
                                if (getP().isAdminAndOwner(mData)) {
                                    mBind.clPk.ivStartPk.setVisibility(View.VISIBLE);
                                    AnimationUtil.createAvatarAnimation(mBind.clPk.ivStartPk);
                                }
                                mPkInfoId = 0;  //重制
                                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null && mData != null)
                                    BaseMusicHelper.get().getPartyService().setSeatList(mData.getMicrophone());
                                break;
                            case "switch_common_pattern":  //关闭PK
                                mBind.svgPk.setVisibility(View.GONE);
                                mBind.rvSeat.setVisibility(View.GONE);
                                mBind.clPk.clPkInfo.setVisibility(View.GONE);
                                mBind.clPk.ivStartPk.clearAnimation();
                                mBind.rvSeat.setVisibility(View.VISIBLE);
                                mFightPattern = 1;
                                mMicrophonePattern = 2; //切换为麦序模式和关闭pk模式
                                if (mData != null && mData.getMicrophone() != null) {
                                    if (mHostSeat != null) {
                                        mHostSeat.setLock(0);
                                        setHostInfo(mHostSeat);
                                    }
                                    int size = mData.getMicrophone().size();
                                    for (int i = 0; i < size; i++) {
                                        if (mData.getMicrophone().get(i).getIndex() > 0 && i - 1 >= 0) {
                                            mData.getMicrophone().get(i).setLock(0);
                                            mSeatAdapter.setData(i - 1, mData.getMicrophone().get(i));
                                        }
                                        if (mData.getMicrophone().get(i).getUser_id() == service.getUserId()) {
                                            mVoiceRoomSeat.setLock(0);
                                            mVoiceRoomSeat = mData.getMicrophone().get(i);
                                        }

                                    }
                                }
                                break;

                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_GIFT_PLAY)
                            || TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_GIFT_COMBO_PLAY)) { //派对接收礼物
                        addGiftMessage(chatMsg, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void addGiftMessage(ChatMsg chatMsg, ChatRoomMessage message) {
        PartyGiftDto giftDto = JSON.parseObject(chatMsg.content, PartyGiftDto.class);
        if (giftDto != null) {
            giftDto.formName = getRemoteExtensionNickName(message);
            giftDto.fromAccountIcon = message.getFromAccount();
            Map<String, String> map = new HashMap<>();
            map = new Gson().fromJson(giftDto.to_name, map.getClass());

            Iterator<String> iterator = giftDto.to.iterator();
            while (iterator.hasNext()) {//集合遍历
                String id = iterator.next();
                String toname = map.get(id);
                if (giftDto.gift_info.ext != null) { //ext不为空. 说明是盲盒礼物
                    ChatRoomMessage crm = ChatRoomMessageBuilder.createTipMessage(mRoomId);
                    crm.setRemoteExtension(putRemoteExtension(ChatMsg.BLINK_BOX_GIFT, JSON.toJSONString(giftDto.gift_info.ext), message));
                    if (giftDto.gift_info.combo == 1) {
                        if (giftDto.is_show == 2 || giftDto.is_show == 0) {
                            addData(crm);
                        }
                    } else {
                        addData(crm);
                    }
                    if (giftDto.gift_info.ext.get_gift != null) {
                        giftDto.gift_info.id = giftDto.gift_info.ext.get_gift.get_gift_id;
                    }
                } else {
                    ChatMsg.PartyGiftMessage pgm = new ChatMsg.PartyGiftMessage(giftDto.formName, id, toname, giftDto.gift_info.image, giftDto.gift_num);
                    ChatRoomMessage giftMessage = ChatRoomMessageBuilder.createTipMessage(mRoomId);
                    giftMessage.setFromAccount(String.valueOf(giftDto.form_id));
                    giftMessage.setRemoteExtension(putRemoteExtension(ChatMsg.PARTY_GIFT_PLAY, JSON.toJSONString(pgm), message));
                    if (giftDto.gift_info.combo == 1) {
                        if (giftDto.is_show == 2 || giftDto.is_show == 0) {
                            addData(giftMessage);
                        }
                    } else {
                        addData(giftMessage);
                    }
                }
                giftDto.toName = toname;
                //0 : 都展示  1：展示连送顶部效果   2：展示公屏消息
                if (giftDto.gift_info.combo == 1) {
                    if (giftDto.is_show == 1 || giftDto.is_show == 0) {
                        showGift(giftDto,chatMsg);
                    }
                } else {
                    showGift(giftDto,chatMsg);
                }
                iterator.remove();
            }
        }
    }


    /**
     * 显示大表情
     */
    private void showBigEmoticon(ImageView ivGame, ChatMsg.Game game, ChatMsg.PartyMsg msg, ChatRoomMessage message) {
        if (game.type == 2 || game.type == 3) {
            ImageLoaderKit.loadGif(BasePartyRoomActivity.this, ivGame, game.animation);
            Utils.runOnUiThreadDelayed(() -> {
                message.setContent(msg.msg);
                addData(message);
                GlideUtils.loadImageNew(BasePartyRoomActivity.this, ivGame, game.image);
                Utils.runOnUiThreadDelayed(() -> {
                    ivGame.setImageResource(0);
                    getP().setIsSendPic(false);
                }, 2000);
            }, 2500);
        } else {
            ImageLoaderKit.loadOneTimeGif(BasePartyRoomActivity.this, ivGame, game.animation, new ImageLoaderKit.GifListener() {
                @Override
                public void gifPlayComplete() {
                    Utils.runOnUiThreadDelayed(() -> {
                        ivGame.setImageResource(0);
                        getP().setIsSendPic(false);
                    }, 2000);
                }
            });
        }
    }


    /**
     * 充值pk榜单
     */
    private void resetSeatRankingList() {
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot, R.drawable.party_room_seat_blue_not);
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot2, R.drawable.party_room_seat_blue_not);
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot3, R.drawable.party_room_seat_blue_not);
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot, R.drawable.party_room_seat_not);
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot2, R.drawable.party_room_seat_not);
        GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot3, R.drawable.party_room_seat_not);
    }

    /**
     * pk榜单信息
     */
    private void setSeatRanking(ChatMsg.PkBank seat) {
       /* if ((seat != null && seat.blue_team != null && seat.blue_team.size() > 0) || (seat != null && seat.red_team != null && seat.red_team.size() > 0)) {
            mBind.seatRanking.setVisibility(View.VISIBLE);
        } else {
            mBind.seatRanking.setVisibility(View.GONE);
        }*/

        if (seat != null) {
            if (seat.blue_team != null) {
                for (int i = 0; i < seat.blue_team.size(); i++) {
                    switch (seat.blue_team.get(i).rank) {
                        case 1:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot, seat.blue_team.get(i).icon);
                            break;
                        case 2:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot2, seat.blue_team.get(i).icon);
                            break;
                        case 3:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.blueImgPartyRoomSeatNot3, seat.blue_team.get(i).icon);
                            break;
                    }
                }
            }
            if (seat.red_team != null) {
                for (int i = 0; i < seat.red_team.size(); i++) {
                    switch (seat.red_team.get(i).rank) {
                        case 1:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot, seat.red_team.get(i).icon);
                            break;
                        case 2:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot2, seat.red_team.get(i).icon);
                            break;
                        case 3:
                            GlideUtils.loadImage(BasePartyRoomActivity.this, mBind.seatRanking.imgPartyRoomSeatNot3, seat.red_team.get(i).icon);
                            break;
                    }
                }
            }
        }

    }


    /**
     * 新增进场动画
     */
    private void initSvg(ChatMsg.NobleEnterAni enterAni) {
        mBind.rootIntoOnRoom.setVisibility(View.VISIBLE);
        //新的贵族入账动效
        FrameLayout.LayoutParams bottom = (FrameLayout.LayoutParams) mBind.nobleIntoOnRoom.getLayoutParams();
        //25  203 204  为新的svga 动画
        if (enterAni.id >= 200 || enterAni.id == 25) {
            bottom.width = DensityUtils.dp2px(this, 200);
            mBind.nobleIntoOnRoom.setLayoutParams(bottom);
            FrameLayout.LayoutParams top = (FrameLayout.LayoutParams) mBind.svgImageOnRoom.getLayoutParams();
            top.width = DensityUtils.dp2px(this, 200);
            mBind.svgImageOnRoom.setLayoutParams(top);
            if (enterAni.id == 200 || enterAni.id == 201 || enterAni.id == 202) {
                aniSVGAImage(mBind.nobleIntoOnRoom, "noble_enter_ani_bottom_%s.svga", enterAni);
                aniSVGAImage(mBind.svgImageOnRoom, "noble_enter_ani_top_%s.svga", enterAni);
            } else {
                aniSVGAImage(mBind.nobleIntoOnRoom, "noble_enter_ani_%s.svga", enterAni);
            }
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_right_in_party_banner);
            Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.anim_right_out_party_banner);
            animationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mBind.rootIntoOnRoom.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mBind.rootIntoOnRoom.startAnimation(animation);
            if (null == mSvgBack)
                mSvgBack = new CountBackUtils();
            mSvgBack.countBack(3, new CountBackUtils.Callback() {
                @Override
                public void countBacking(long time) {
                    if (time == 1) {
                        mBind.rootIntoOnRoom.startAnimation(animationOut);
                    }
                }

                @Override
                public void finish() {

                }
            });
        } else {
            bottom.width = FrameLayout.LayoutParams.MATCH_PARENT;
            mBind.nobleIntoOnRoom.setLayoutParams(bottom);
            aniSVGAImage(mBind.nobleIntoOnRoom, "noble_enter_ani_%s.svga", enterAni);
        }
    }

    private void aniSVGAImage(SVGAImageView noble_into_on_room, String anima, ChatMsg.NobleEnterAni enterAni) {
        svgaParser.decodeFromAssets(String.format(anima, enterAni.id), new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                SVGADynamicEntity dynamicEntity = new SVGADynamicEntity();
                if (!anima.contains("top")) {
                    String key1, key2, key3, nameColor, contentColor;
                    //203 204 25 新的svga
                    if (enterAni.id >= 200 || enterAni.id == 25) {
                        nameColor = TextUtils.isEmpty(enterAni.nickname_color) ? "#FFFFFF" : enterAni.nickname_color;
                        contentColor = "#DFB313";
                        key1 = "user";
                        key2 = "text";
                        key3 = "img";
                    } else {
                        nameColor = "#FFFFFF";
                        contentColor = TextUtils.isEmpty(enterAni.nickname_color) ? "#662700" : enterAni.nickname_color;
                        key1 = "name";
                        key2 = "name2";
                        key3 = "user";
                    }
                    SpannableStringBuilder ssb = new SpannableStringBuilder(enterAni.nickname);
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor(nameColor)), 0, ssb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    TextPaint textPaint = new TextPaint();
                    textPaint.setFakeBoldText(true);
                    if (BasePartyRoomActivity.this.getResources().getDisplayMetrics().density > 2.5) {
                        textPaint.setTextSize(DensityUtils.dp2px(BasePartyRoomActivity.this, 18));
                    } else {
                        textPaint.setTextSize(DensityUtils.dp2px(BasePartyRoomActivity.this, 28));
                    }
                    dynamicEntity.setDynamicText(new StaticLayout(ssb, 0, ssb.length(), textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false), key1);
                    SpannableStringBuilder ssb2 = new SpannableStringBuilder(enterAni.tips);
                    ssb2.setSpan(new ForegroundColorSpan(Color.parseColor(contentColor)), 0, ssb2.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    TextPaint textPaint2 = new TextPaint();
                    if (BasePartyRoomActivity.this.getResources().getDisplayMetrics().density > 2.5) {
                        textPaint2.setTextSize(DensityUtils.dp2px(BasePartyRoomActivity.this, 14));
                    } else {
                        textPaint2.setTextSize(DensityUtils.dp2px(BasePartyRoomActivity.this, 24));
                    }
                    dynamicEntity.setDynamicText(new StaticLayout(ssb2, 0, ssb2.length(), textPaint2, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false), key2);
                    dynamicEntity.setDynamicImage(enterAni.user_icon, key3); // Here is the KEY implementation.
                }
                SVGADrawable drawable = new SVGADrawable(videoItem, dynamicEntity);
                if (noble_into_on_room != null && !noble_into_on_room.isAnimating()) {
                    noble_into_on_room.setImageDrawable(drawable);
                    noble_into_on_room.startAnimation();
                }
            }

            @Override
            public void onError() {

            }
        }, null);

    }


    /**
     * 重置麦位信息
     */
    private VoiceRoomSeat resetSeat(VoiceRoomSeat voiceRoomSeat) {
        voiceRoomSeat.setNickname("");
        voiceRoomSeat.setUser_id(0);
        voiceRoomSeat.setAvatar("");
        voiceRoomSeat.setAvatar_id(0);
        voiceRoomSeat.setCost("");
        voiceRoomSeat.setBlueSelect(false);
        voiceRoomSeat.setSelect(false);
        voiceRoomSeat.setIs_mvp(0);
        voiceRoomSeat.setSilence_switch(0);
        voiceRoomSeat.setIcon("");
        return voiceRoomSeat;
    }


    protected abstract void addWarnTips(String rank);

    /**
     * 重置cost
     */
    private void resetCost() {
        if (mHostSeat != null) {
            mHostSeat.setCost("0");
            mHostSeat.setIs_mvp(0);
            setHostInfo(mHostSeat);
        }
        if (mWeTeamAdapter != null) {
            int weSize = mWeTeamAdapter.getData().size();
            for (int j = 0; j < weSize; j++) {
                if (mWeTeamAdapter.getData().get(j) != null && !TextUtils.equals("0", mWeTeamAdapter.getData().get(j).getCost())) {
                    mWeTeamAdapter.getData().get(j).setCost("0");
                    mWeTeamAdapter.getData().get(j).setIs_mvp(0);
                    mWeTeamAdapter.notifyItemChanged(j);
                }
            }
        }
        if (mOtherTeamAdapter != null) {
            int otherSize = mOtherTeamAdapter.getData().size();
            for (int j = 0; j < otherSize; j++) {
                if (mOtherTeamAdapter.getData().get(j) != null && !TextUtils.equals("0", mOtherTeamAdapter.getData().get(j).getCost())) {
                    mOtherTeamAdapter.getData().get(j).setCost("0");
                    mOtherTeamAdapter.getData().get(j).setIs_mvp(0);
                    mOtherTeamAdapter.notifyItemChanged(j);
                }
            }
        }
        int size = mSeatAdapter.getData().size();
        for (int i = 0; i < size; i++) {
            mSeatAdapter.getData().get(i).setCost("0");
            mSeatAdapter.getData().get(i).setIs_mvp(0);
            mSeatAdapter.setData(i, mSeatAdapter.getData().get(i));
        }
    }

    /**
     * 显示礼物飘屏
     */
    protected void showFloatGift() {
        if (mIsPlayEnd) return;
        if (mFloatGift.isEmpty()) return;
        ChatMsg data = mFloatGift.peek();
        if (data == null) {
            return;
        }
        mIsPlayEnd = true;
        mFloatGift.poll();
        ChatMsg.PartyMsg partyMsg = JSON.parseObject(data.content, ChatMsg.PartyMsg.class);
        if (partyMsg == null) return;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBind.tvFloatContent.getLayoutParams();
        //贵族升级飘萍
        if (TextUtils.equals(ChatMsg.NOBILITY_LEVEL_UP_SPECIAL, data.cmd)) {
            mBind.tvFloatContent.setTextColor(ContextCompat.getColor(this, R.color.white));
            mBind.rlFloat.setBackgroundResource(R.drawable.bg_nobility_level);
            params.leftMargin = DensityUtils.dp2px(this, 34);
            mBind.tvFloatContent.setLayoutParams(params);
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            String url = partyMsg.badge;
            spannableString.append("[icon]   ");
            spannableString.append(partyMsg.msg);
            int headerStart = spannableString.toString().indexOf("[icon]");
            CircleUrlImageSpan headerSpan = new CircleUrlImageSpan(this, url, mBind.tvFloatContent, ConvertUtils.dp2px(18), ConvertUtils.dp2px(18));
            spannableString.setSpan(headerSpan, headerStart, headerStart + 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            int start = spannableString.toString().indexOf(partyMsg.nickname);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FEE903")), start, start + partyMsg.nickname.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            int start1 = spannableString.toString().indexOf(partyMsg.gradename);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FEE903")), start1, start1 + partyMsg.gradename.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mBind.tvFloatContent.setText(spannableString);
        } else {  //礼物飘萍
            params.leftMargin = DensityUtils.dp2px(this, 66);
            mBind.tvFloatContent.setLayoutParams(params);
            mBind.rlFloat.setBackgroundResource(R.drawable.party_bg_float);
            mBind.tvFloatContent.setText(new SpannableStringUtils.Builder().append(partyMsg.msg)
                    .setForegroundColor(Color.parseColor("#905814"))
                    .append(partyMsg.desc)
                    .create());
        }
        mBind.rlFloat.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.float_screen_in_right);
        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.float_screen_out_left);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBind.rlFloat.setVisibility(View.GONE);
                mIsPlayEnd = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mBind.rlFloat.startAnimation(animation);
        if (null == mFloatBack)
            mFloatBack = new CountBackUtils();
        mFloatBack.countBack(5, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                if (time == 1) {
                    mBind.rlFloat.startAnimation(animationOut);
                }
            }

            @Override
            public void finish() {

            }
        });
    }


    private Map putRemoteExtension(String cmd_type, String json, ChatRoomMessage message) {
        Map<String, Object> remoteExtension = message.getRemoteExtension();
        remoteExtension.put(Interfaces.CMD_TYPE, cmd_type);
        remoteExtension.put(Interfaces.JSON_STRING, json);
        return remoteExtension;
    }

    public String getRemoteExtensionNickName(ChatRoomMessage message) {
        Map<String, Object> ext = message.getRemoteExtension();
        if (ext != null) {
            if (ext.get("nickname") != null) {
                return (String) ext.get("nickname");
            }
        }
        return "";
    }

    public abstract void showGift(PartyGiftDto giftDto,ChatMsg chatMsg);

    private void addData(ChatRoomMessage message) {
        addData(0, message);
    }

    private void addData(int itemType, ChatRoomMessage message) {
        synchronized (BasePartyRoomActivity.this) {
            MultipleChatRoomMessage multipleChatRoomMessage = new MultipleChatRoomMessage(itemType, message);
            mMsgAdapter.addData(multipleChatRoomMessage);
            if (mMsgLayoutManager != null) {
                if (isMyMessage(message) || autoScroller) {
                    mMsgLayoutManager.scrollToPosition(mMsgAdapter.getItemCount() - 1);
                    mBind.showNewMsg.setVisibility(View.GONE);
                    return;
                }
                if (mMsgLayoutManager.findLastVisibleItemPosition() < mMsgAdapter.getItemCount() - 1) {
                    mBind.showNewMsg.setVisibility(View.VISIBLE);
                } else {
                    mBind.showNewMsg.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isMyMessage(ChatRoomMessage message) {
        return message.getFromAccount() != null && !TextUtils.isEmpty(message.getFromAccount())
                && TextUtils.equals(message.getFromAccount(), service.getUserId() + "");
    }

    private boolean isLastMessageVisible() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mBind.rvMessage.getLayoutManager();
        int lastVisiblePosition = 0;
        if (layoutManager != null)
            lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (mMsgAdapter.getData().size() >= 2)
            return lastVisiblePosition >= mMsgAdapter.getData().size() - 2;
        return false;
    }


    @Override
    public void choosePictureSuccess() {
        mIsSendPicture = true;
    }

    /**
     * 进入聊天室
     */
    protected void enterRoom() {
        EnterChatRoomData data = new EnterChatRoomData(mRoomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 3);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {

            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                NimUIKit.enterChatRoomSuccess(result, false);
                getP().joinLeaveParty("open", mId, 0);
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    toastTip("你已被拉入黑名单，不能再进入");
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    toastTip("聊天室不存在");
                } else {
//                    toastTip("enter chat room failed, code=" + code);
                    toastTip("进入房间失败");
                }
                finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                toastTip("进入房间失败");
//                toastTip("enter chat room exception, e=" + exception.getMessage());
                finish();
            }
        });
    }

    /**
     * 发送消息
     */
    protected void sendMessage(String content) {
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(mRoomId, content);
        Map<String, Object> map = new HashMap<>();
        if (service != null && service.getUserInfo() != null) {
            map.put("sex", String.valueOf(service.getUserInfo().getSex()));
            map.put("age", String.valueOf(service.getUserInfo().getAge()));
            map.put("nickname", service.getUserInfo() != null ? String.valueOf(service.getUserInfo().getNickname()) : "");
            if (mMsgAdapter != null) {
                map.put("badge", isFirstSend ? badge : mMsgAdapter.getLastBadge());
                map.put("rich_level", isFirstSend ? richLevel : mMsgAdapter.getLastLevel());
            }
        }
        if (mData != null && mData.getUser() != null) {
            map.put("role_id", String.valueOf(mData.getUser().getRole_id()));
        }
        map.put("chat_bubble", getP().getChatBg(service.getUserId() + "") + "");
        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
        antiSpamOption.enable = false;
        message.setNIMAntiSpamOption(antiSpamOption);
        message.setRemoteExtension(map);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        addData(message);
                    }

                    @Override
                    public void onFailed(int code) {
                        // 失败
//                        toastTip("发送失败");
                        failedToSendMessage(message.getUuid());
                    }

                    @Override
                    public void onException(Throwable exception) {
                        // 错误
                        failedToSendMessage(message.getUuid());
//                        toastTip("发送错误");
                    }
                });
        isFirstSend = false;

    }


    private void onLoginDone() {
        enterRequest = null;
    }

    public final void toggleMuteLocalAudio(int status) {
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
            BaseMusicHelper.get().getPartyService().muteLocalAudio(status);
    }

    boolean isResume = true;
    boolean isResumeOnce;

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        getP().unBind();
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().setMode();
        }
        if (mData != null) {
            isResumeOnce = true;
            visitPartyList(1, 0, "", -1);
        }
        MMKVUtils.getInstance().encode(RECHARGE_NUMBER, 4);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MMKVUtils.getInstance().encode(RECHARGE_NUMBER, -1);
    }


    @Override
    public void finishActivity() {
        if (isOnSeat()) {
            getP().leaveSeat(mId, getP().getIndex(mData, service.getUserId()), 1);
        }
        getP().joinLeaveParty("close", mId, 0);
        Intent intent = new Intent(this, PartyAudioService.class);
        stopService(intent);
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
            BaseMusicHelper.get().getPartyService().releaseAudience();
            BaseMusicHelper.get().getPartyService().removeCallBack();
            BaseMusicHelper.get().getPartyService().onDestroy();
        }
        NIMClient.getService(ChatRoomService.class).exitChatRoom(mRoomId);
        if (BaseMusicHelper.get().getPlayService() != null)
            BaseMusicHelper.get().getPlayService().stop();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_RESULT) {
            isRelease = false;
            MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, isOnSeat());
            MMKVUtils.getInstance().encode(Constants.PARTY_ID, mId);
            MMKVUtils.getInstance().encode(Constants.PARAM_YUN_ROOM_ID, mRoomId);
            getP().smallWindow(this, mMessageNum, mRoomId, mId, mRoomThumb, mRoomBg, isOnSeat(), getP().getIndex(mData, service.getUserId()), mFightPattern);
        }
    }

    protected VoiceRoomUser createUser() {
        UserInfo userInfo = service.getUserInfo();
        return new VoiceRoomUser(String.valueOf(service.getUserId()), userInfo == null ? "" : userInfo.getNickname(), userInfo == null ? "" : userInfo.getIcon());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            isRelease = false;
            MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, isOnSeat());
            MMKVUtils.getInstance().encode(Constants.PARTY_ID, mId);
            MMKVUtils.getInstance().encode(Constants.PARAM_YUN_ROOM_ID, mRoomId);
            getP().smallWindow(this, mMessageNum, mRoomId, mId, mRoomThumb, mRoomBg, isOnSeat(), getP().getIndex(mData, service.getUserId()), mFightPattern);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isResume = false;
        isFirstLogin = false;
        registerObservers(false);
        if (mFloatBack != null) {
            mFloatBack.destroy();
        }
        if (mFloatGift != null)
            mFloatGift.clear();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        if (mPkBack != null)
            mPkBack.destroy();
        mBind.banner.destroy();
        mWeTeamAdapter.getData().clear();
        mSeatAdapter.getData().clear();
        mWeTeamAdapter = null;
        mSeatAdapter = null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLeaveRoomSuccess() {
        //进入不同派对
        if (mLastPartyId != mId && TextUtils.equals(mJoinType, "in")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setPullUrlInfo(mVoiceRoomInfo);
                }
            }).start();
        }
    }

    @Override
    public void muteVoice(int status, boolean mute) {
        if (mute) {
            mBind.ivMute.setImageResource(R.drawable.party_ic_voice_close);
//            Utils.toast(status == 2 ? "话筒已被管理员关闭" : "话筒已关闭");
            Utils.toast("话筒已关闭");
        } else {
            mBind.ivMute.setImageResource(R.drawable.party_ic_voice_open);
            Utils.toast("话筒已打开");
        }
    }

    @Override
    public void muteRoomAudio(boolean mute) {
        if (mute) {
            Utils.toast("已关闭声音");
        } else {
            Utils.toast("已打开声音");
        }
    }


    @Override
    public void onHeadChange(int headStatus) {
        mHeadStatus = headStatus;
        if (headStatus == 0) {
            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                BaseMusicHelper.get().getPartyService().setEnableEarBack(false);
            }
            NERtcEx.getInstance().enableEarback(false, 0);
        }
        if (mEarphoneSettingPop != null) {
            mEarphoneSettingPop.setHeadStatus(headStatus);
        }
    }

    @Override
    public void onSelfSeatVolume(int volume) {
        if (volume == 0) {
            clearHostAnimation();
        }
        if (mHostSeat != null && mHostSeat.getUser_id() == service.getUserId()) {
            getP().showVolume(mBind.circle, mBind.circle1, volume);
        }
        if (mFightPattern == 1) {
            if (mBind.rvSeat.getLayoutManager() == null) {
                mBind.rvSeat.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 4));
            }
            if (mSeatAdapter != null) {
                for (int j = 0; j < mSeatAdapter.getData().size(); j++) {
                    if (mSeatAdapter.getData().get(j) != null && service.getUserId() == mSeatAdapter.getData().get(j).getUser_id()) {
                        View itemView = mBind.rvSeat.getLayoutManager().findViewByPosition(mSeatAdapter.getData().get(j).getIndex() - 1);
                        if (itemView != null) {
                            ImageView circle = itemView.findViewById(R.id.circle);
                            ImageView circle1 = itemView.findViewById(R.id.circle1);
                            getP().showVolume(circle, circle1, volume);
                        }
                    }
                }
            }
        } else {
            if (mWeTeamAdapter != null) {
                if (mBind.clPk.rvWeTeam.getLayoutManager() == null) {
                    mBind.clPk.rvWeTeam.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 2));
                }
                for (int j = 0; j < mWeTeamAdapter.getData().size(); j++) {
                    if (service.getUserId() == mWeTeamAdapter.getData().get(j).getUser_id()) {
                        View itemView = mBind.clPk.rvWeTeam.getLayoutManager().findViewByPosition(mWeTeamAdapter.getData().get(j).getIndex() - 1);
                        if (itemView != null) {
                            ImageView circle = itemView.findViewById(R.id.circle);
                            ImageView circle1 = itemView.findViewById(R.id.circle1);
                            getP().showPKVolume(circle, circle1, volume);
                        }
                    }
                }
            }
            if (mOtherTeamAdapter != null) {
                if (mBind.clPk.rvOtherTeam.getLayoutManager() == null) {
                    mBind.clPk.rvOtherTeam.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 2));
                }
                for (int j = 0; j < mOtherTeamAdapter.getData().size(); j++) {
                    View itemView = mBind.clPk.rvOtherTeam.getLayoutManager().findViewByPosition(mOtherTeamAdapter.getData().get(j).getIndex() - 5);
                    if (service.getUserId() == mOtherTeamAdapter.getData().get(j).getUser_id()) {
                        if (itemView != null) {
                            ImageView circle = itemView.findViewById(R.id.circle);
                            ImageView circle1 = itemView.findViewById(R.id.circle1);
                            getP().showPKVolume(circle, circle1, volume);
                        }
                    }
                }
            }
        }
    }


    private void clearHostAnimation() {
        mBind.circle.setVisibility(View.INVISIBLE);
        mBind.circle1.setVisibility(View.INVISIBLE);
        mBind.circle.clearAnimation();
        mBind.circle1.clearAnimation();
    }

    @Override
    public void onSeatVolume(VoiceRoomSeat seat, int volume) {
        if (seat == null || seat.getUser_id() == service.getUserId()) return;
        if (mHostSeat != null && mHostSeat.getUser_id() == seat.getUser_id()) {
            if (volume == 0) {
                clearHostAnimation();
            }
            getP().showVolume(mBind.circle, mBind.circle1, volume);
        }
        //普通模式
        if (mFightPattern == 1) {
            if (mBind.rvSeat.getLayoutManager() == null) {
                mBind.rvSeat.setLayoutManager(new GridLayoutManager(this, 4));
            }
            View itemView = mBind.rvSeat.getLayoutManager().findViewByPosition(seat.getIndex() - 1);
            if (itemView != null) {
                ImageView circle = itemView.findViewById(R.id.circle);
                ImageView circle1 = itemView.findViewById(R.id.circle1);
                getP().showVolume(circle, circle1, volume);
            }

        } else {
            if (seat.getIndex() < 5) {
                if (mBind.clPk.rvWeTeam.getLayoutManager() == null) {
                    mBind.clPk.rvWeTeam.setLayoutManager(new GridLayoutManager(this, 2));
                }
                View itemView = mBind.clPk.rvWeTeam.getLayoutManager().findViewByPosition(seat.getIndex() - 1);
                if (itemView != null) {
                    ImageView circle = itemView.findViewById(R.id.circle);
                    ImageView circle1 = itemView.findViewById(R.id.circle1);
                    getP().showPKVolume(circle, circle1, volume);
                }
            } else {
                if (mBind.clPk.rvOtherTeam.getLayoutManager() == null) {
                    mBind.clPk.rvOtherTeam.setLayoutManager(new GridLayoutManager(this, 2));
                }
                View itemView1 = mBind.clPk.rvOtherTeam.getLayoutManager().findViewByPosition(seat.getIndex() - 5);
                if (itemView1 != null) {
                    ImageView circle2 = itemView1.findViewById(R.id.circle);
                    ImageView circle3 = itemView1.findViewById(R.id.circle1);
                    getP().showPKVolume(circle2, circle3, volume);
                }
            }
        }
    }

    @Override
    public void getSeatVoice(List<VoicePlayRegionsBean> regions) {
        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 普通模式
                if (mFightPattern == 1) {
                    if (mBind.rvSeat.getLayoutManager() == null) {
                        mBind.rvSeat.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 4));
                    }
                    int size = regions.size();
                    for (int i = 0; i < size; i++) {
                        if (mSeatAdapter != null) {
                            for (int j = 0; j < mSeatAdapter.getData().size(); j++) {
                                if (mSeatAdapter.getData().get(j).getUser_id() == 0) {
                                    View itemView = mBind.rvSeat.getLayoutManager().findViewByPosition(mSeatAdapter.getData().get(j).getIndex() - 1);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showVolume(circle, circle1, 0);
                                    }
                                }
                                if (regions.get(i).uid == mSeatAdapter.getData().get(j).getUser_id()) {
                                    View itemView = mBind.rvSeat.getLayoutManager().findViewByPosition(mSeatAdapter.getData().get(j).getIndex() - 1);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showVolume(circle, circle1, regions.get(i).volume);
                                    }
                                }
                            }
                        }
                        if (mHostSeat != null && regions.get(i).uid == mHostSeat.getUser_id()) {
                            getP().showVolume(mBind.circle, mBind.circle1, regions.get(i).volume);
                        }
                    }
                } else {
                    if (mBind.clPk.rvWeTeam.getLayoutManager() == null) {
                        mBind.clPk.rvWeTeam.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 2));
                    }
                    if (mBind.clPk.rvOtherTeam.getLayoutManager() == null) {
                        mBind.clPk.rvOtherTeam.setLayoutManager(new GridLayoutManager(BasePartyRoomActivity.this, 2));
                    }
                    int size = regions.size();
                    for (int i = 0; i < size; i++) {
                        if (mWeTeamAdapter != null) {
                            for (int j = 0; j < mWeTeamAdapter.getData().size(); j++) {
                                if (mWeTeamAdapter.getData().get(j).getUser_id() == 0) {
                                    View itemView = mBind.clPk.rvWeTeam.getLayoutManager().findViewByPosition(mWeTeamAdapter.getData().get(j).getIndex() - 1);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showPKVolume(circle, circle1, 0);
                                    }
                                }
                                if (regions.get(i).uid == mWeTeamAdapter.getData().get(j).getUser_id()) {
                                    View itemView = mBind.clPk.rvWeTeam.getLayoutManager().findViewByPosition(mWeTeamAdapter.getData().get(j).getIndex() - 1);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showPKVolume(circle, circle1, regions.get(i).volume);
                                    }
                                }
                            }
                        }
                        if (mOtherTeamAdapter != null) {
                            for (int j = 0; j < mOtherTeamAdapter.getData().size(); j++) {
                                if (mOtherTeamAdapter.getData().get(j).getUser_id() == 0) {
                                    View itemView = mBind.clPk.rvOtherTeam.getLayoutManager().findViewByPosition(mOtherTeamAdapter.getData().get(j).getIndex() - 5);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showPKVolume(circle, circle1, 0);
                                    }
                                }
                                if (regions.get(i).uid == mOtherTeamAdapter.getData().get(j).getUser_id()) {
                                    View itemView = mBind.clPk.rvOtherTeam.getLayoutManager().findViewByPosition(mOtherTeamAdapter.getData().get(j).getIndex() - 5);
                                    if (itemView != null) {
                                        ImageView circle = itemView.findViewById(R.id.circle);
                                        ImageView circle1 = itemView.findViewById(R.id.circle1);
                                        getP().showPKVolume(circle, circle1, regions.get(i).volume);
                                    }
                                }
                            }
                        }
                        if (mHostSeat != null && regions.get(i).uid == mHostSeat.getUser_id()) {
                            getP().showVolume(mBind.circle, mBind.circle1, regions.get(i).volume);
                        }
                    }
                }
            }
        });
    }
}
