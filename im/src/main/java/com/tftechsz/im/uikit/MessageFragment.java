package com.tftechsz.im.uikit;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.model.main.CustomPushContentProvider;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.session.SessionEventListener;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.business.ait.AitContactType;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.actions.ImageAction;
import com.netease.nim.uikit.business.session.actions.LocationAction;
import com.netease.nim.uikit.business.session.actions.VideoAction;
import com.netease.nim.uikit.business.session.constant.Extras;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.input.InputPanel;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.EPSoftKeyBoardListener;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.robot.model.RobotMsgType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGADynamicEntity;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.inter.IAnimListener;
import com.tftechsz.common.widget.pop.TopicPop;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.MoreFunAdapter;
import com.tftechsz.common.adapter.TopicAdapter;
import com.tftechsz.im.adapter.ViewPagerScrollAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.api.MultipleItem;
import com.tftechsz.im.model.dto.AirdropBagDto;
import com.tftechsz.im.model.dto.ButtonConfigDto;
import com.tftechsz.im.model.dto.CoupleBagDetailDto;
import com.tftechsz.im.model.dto.CoupleBagDto;
import com.tftechsz.im.model.dto.CoupleLetterDto;
import com.tftechsz.im.model.dto.GroupCoupleDto;
import com.tftechsz.im.model.dto.JoinLeaveRoom;
import com.tftechsz.im.model.dto.MoreFunDto;
import com.tftechsz.im.model.dto.VoiceChatDto;
import com.tftechsz.im.model.event.BgSetEvent;
import com.tftechsz.im.mvp.ui.activity.AirdropDetailActivity;
import com.tftechsz.im.mvp.ui.activity.ChatSettingActivity;
import com.tftechsz.im.mvp.ui.activity.VideoCallActivity;
import com.tftechsz.im.widget.VoiceChatView;
import com.tftechsz.im.widget.activity.MessageActivityView;
import com.tftechsz.im.widget.activity.OnActItemClickListener;
import com.tftechsz.im.widget.pop.AirdropPopWindow;
import com.tftechsz.im.widget.pop.ChatMessagePopWindow;
import com.tftechsz.im.widget.pop.ConfessionLetterPopWindow;
import com.tftechsz.im.widget.pop.ContinueSendGiftPopWindow;
import com.tftechsz.im.widget.pop.CoupleGiftBagDetailPop;
import com.tftechsz.im.widget.pop.CoupleGiftBagPop;
import com.tftechsz.im.widget.pop.CouplesTaskPop;
import com.tftechsz.im.widget.pop.FamilyBoxPop;
import com.tftechsz.im.widget.pop.GroupCouplePopWindow;
import com.tftechsz.im.widget.pop.IntimacyGiftPop;
import com.tftechsz.im.widget.pop.OpenAirdropWindow;
import com.tftechsz.im.widget.pop.OpenRainRedPackagePopWindow;
import com.tftechsz.im.widget.pop.RainRedPackagePopWindow;
import com.tftechsz.im.widget.pop.SendRedEnvelopePopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.BoxInfo;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.ChatTipsContent;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.IntimacyEntity;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.RedDetailInfo;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.event.VoiceChatEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.iservice.FamilyService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.manager.DbManager;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.nim.ChatSoundPlayer;
import com.tftechsz.common.other.GlideRoundTransform2;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.ChoosePicUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CircleUrlImageSpan;
import com.tftechsz.common.widget.CustomMarqueeTextView;
import com.tftechsz.common.widget.DoubleHitGiftChatView;
import com.tftechsz.common.widget.MarqueeTextView;
import com.tftechsz.common.widget.SharePopWindow;
import com.tftechsz.common.widget.gift.GiftRootLayout;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.FamilyLevelUpPop;
import com.tftechsz.common.widget.pop.GiftPopWindow;
import com.tftechsz.common.widget.pop.LightenGiftPop;
import com.tftechsz.common.widget.pop.RechargeBeforePop;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.common.widget.pop.RedEnvelopeDetailsPopWindow;
import com.tftechsz.common.widget.pop.RedEnvelopeReceivePopWindow;
import com.tftechsz.common.widget.pop.RemoveCouplesPop;
import com.tftechsz.common.widget.pop.VideoCallPopWindow;
import com.tftechsz.common.widget.pop.WelcomeToFamilyPopWindow;
import com.tftechsz.common.widget.rain.RedPacketViewHelper;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.widget.pop.MineDetailMorePopWindow;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.KeyboardUtils;

import static android.app.Activity.RESULT_OK;
import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_TYPE_DIALOG_ACTIVITY;
import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT;
import static com.tftechsz.common.Constants.NOTIFY_UPDATE_VOICE_INFO;

/**
 * 聊天界面基类
 */
public class MessageFragment extends TFragment implements ModuleProxy, View.OnClickListener, MessageListPanelEx.OnMessageListener, InputPanel.InputListener {
    private final String COUPLE_OFFICIAL_PUBLICITY = "couple_official_publicity.svga";
    private final int EVENT_MESSAGE = 10000;
    private View rootView;
    private final Handler mHandler = new Handler();
    private SessionCustomization customization;
    protected static final String TAG = "MessageActivity";
    private int mHeight;//派对window显示高度
    // p2p对方Account或者群id
    protected String sessionId;
    protected CompositeDisposable mCompositeDisposable;

    protected ImageView ivChatPhoto, ivChatRed,mIvVoiceCall;
    protected RelativeLayout ivChatCall;

    // modules
    protected InputPanel inputPanel;
    protected MessageListPanelEx messageListPanel;

    protected AitManager aitManager;
    private PartyService partyService;
    private TextView mTvName;   //个人姓名
    private RelativeLayout mRlToolBar;
    private RelativeLayout mRlIntimacy;  //亲密度布局
    private TextView mTvIntimacy;  //请密度
    public String mIntimacy; //亲密度
    private AvatarImageView mIvLeft, mIvRight;  //头像
    private CustomPopWindow customPopWindow;
    private VideoCallPopWindow videoCallPopWindow;  //音视频弹窗
    private ChatMessagePopWindow chatMessagePopWindow;  //亲密度弹窗
    private GroupCouplePopWindow groupCouplePopWindow;  //情侣弹窗
    private CoupleGiftBagPop coupleGiftBagPop;  //情侣背包
    private CoupleGiftBagDetailPop coupleGiftBagDetailPop;
    private SharePopWindow mSharePop;
    private boolean isResume = false;
    private LottieAnimationView lottieAnimationView;
    private ImageView ivChatGift;
    //家族顶部
    private RelativeLayout mRlTeam;
    private TextView mTvTeam, mTvTeamNum;
    //通知
    private LinearLayout mLlNotice;
    private TextView mTvNotice;
    //联系方式
    private MarqueeTextView mTvContactWay;
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    //礼物
    GiftRootLayout.GiftRootListener giftViewListener;
    protected GiftRootLayout giftRoot;
    protected GiftPopWindow giftPopWindow;
    protected final Queue<GiftDto> myGiftList = new ConcurrentLinkedQueue<>();
    private SVGAImageView svgaImageView;
    private SVGAParser svgaParser;
    private Handler safeHandle;
    private ImageView mIvGiftMask;
    private boolean mIsDown, mIsPlay;
    protected int mTeamType;  //1:聊天广场  0家族
    private ImageView mIvSign;   //签到

    private ConstraintLayout mClGroupCouple;  //情侣表白布局
    private TextView mTvFrom, mTvTo, mTvGroupCoupleContent;
    private ImageView mIvFrom, mIvFromHead, mIvTo, mIvToHead;


    //亲密度可以拨打音视频的时候弹出
    private RelativeLayout mRlIntimacyCall;
    private TextView mTvIntimacyCall;
    private boolean isFamilyRecruit;
    protected UserProviderService service;
    private MineApiService mineApiService;
    private SendRedEnvelopePopWindow mRedEnvelopePopWindow;
    private RedEnvelopeReceivePopWindow mGoldRedEnvelopePopWindow;
    private RedEnvelopeDetailsPopWindow mEnvelopeDetailsPopWindow;
    private AirdropPopWindow mAirdropPopWindow;  //空投
    private OpenAirdropWindow mOpenAirdropWindow; //打开空投
    private RechargePopWindow rechargePopWindow;  //充值弹窗
    private ConfessionLetterPopWindow confessionLetterPopWindow;  //表白信弹窗
    private ContinueSendGiftPopWindow continueSendGiftPopWindow; //再次送礼弹窗
    private CustomPopWindow perPop;  //图片提示弹窗
    private LightenGiftPop lightenGiftPop;//甜蜜值减少
    private IntimacyEntity mIntimacyEntity;
    private LinearLayout mRlOpenVip;
    private RecyclerView mRvTopic;
    private TextView mTvOpenVip;
    private MineService mineService;
    private IMMessage redImMessage;
    private LinearLayout mLlAnnouncement;

    //贵族消息飘萍  幸运礼物中奖
    private RelativeLayout mLlFloat;
    private ImageView mIvCriticalLeft;  //幸运礼物左边图片
    private CustomMarqueeTextView mTvFloatContent;
    private final Queue<ChatMsg> mFloatGift = new ConcurrentLinkedQueue<>();   //漂屏队列
    private boolean mIsPlayEnd = false;
    //ait消息
    private FrameLayout mFlAit;
    private TextView mTvAit;
    private TextView mTvAitNum;
    protected SessionTypeEnum sessionType;
    /**
     * 0 聊天 1 派对私聊
     */
    protected int tagIsDialog;//dialog activity
    private MessageActivityView<ChatMsg.Airdrop> mActivityView;   // 空投，表白信活动
    private RechargeBeforePop beforePop;
    //空投
    private ImageView mActivityIcon;
    private CountBackUtils countBackUtils, mSvgBack, animationBack, contactBack, mFloatBack;

    private RecyclerView mRvMore;

    private int intimacyEndTime;
    /**
     * 消息接收观察者
     */
    private Observer<List<IMMessage>> incomingMessageObserver;
    /**
     * 已读回执观察者
     */
    private Observer<List<MessageReceipt>> messageReceiptObserver;
    /**
     * 命令消息接收观察者
     */
    public Observer<CustomNotification> commandObserver;
    /**
     * 用户信息变更观察者
     */
    private UserInfoObserver userInfoObserver;
    private LottieAnimationView mAnimationVip, mAnimationWarn;
    private SessionEventListener listener;
    private WelcomeToFamilyPopWindow mPopWindow;

    private FamilyLevelUpPop mFamilyLevelUpPop;

    protected MoreFunAdapter moreAdapter; //更布局适配器
    private ConstraintLayout mLlFamilyBox;
    private TextView mTvBoxTime, mTvBoxStatus;
    private CountBackUtils mCountBack2Box;
    private FamilyBoxPop mFamilyBoxPop;
    private ChatMsg.FamilyBox mFamilyBox;
    private ProgressBar mPbBox;
    private LottieAnimationView mBoxLottie;
    private ViewPagerScrollAdapter mTopScrollAdapter;
    private ViewPager2 mTopVp2;

    private IntimacyGiftPop mIntimacyGiftPop;

    private boolean mIsLoadRoom = false;  //是否加载语音房
    protected VoiceChatView mVoiceChat;  //语音闲聊
    public int mIsOpenRoom;  //0 未开启  1 开始语音闲聊
    protected RelativeLayout mLlVoiceWarm;
    protected TextView mTvVoiceWarm;
    protected ImageView mIvVoiceWarm;
    private RelativeLayout mRelAttention;
    private AttentionService attentionService;
    private LinearLayout mViewRootInput;//下面聊天
    private PublicService publicService;
    private boolean mIsFirstEnter = false;  // 是否第一次进入家族群聊
    protected LinearLayout mLinAwardGift;//通知礼物奖励动画
    protected TextView mTvAwardHint;//通知礼物奖励提示
    protected FrameLayout rootIntoOnRoom;//进场动画
    SVGAImageView svgImageOnRoom, nobleIntoOnRoom;
    protected LinearLayout mLinRootAward;
    protected List<ChatMsg.LuckyGift> mMapAward = new ArrayList<>();//礼物横幅奖励item
    protected TextView tvAwardText1;
    private boolean isLoadingReward;
    private AnimView mPlayerView;
    //连击礼物
    private DoubleHitGiftChatView mDoubleGift;
    private CouplesTaskPop couplesTaskPop;//情侣任务
    private int mFamilyId;
    private RemoveCouplesPop removeCouplesPop;
    private GroupCoupleDto groupCoupleDto;

    private LottieAnimationView mLavLove;
    private ImageView mIvChatBg,mIvtoolbarmenu,mIvIntimacyDetail;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCompositeDisposable = new CompositeDisposable();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mineApiService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        partyService = ARouter.getInstance().navigation(PartyService.class);
        attentionService = ARouter.getInstance().navigation(AttentionService.class);
        publicService = RetrofitManager.getInstance().createUploadCheatApi(PublicService.class);
        ImmersionBar.setTitleBar(this, findView(R.id.base_tool_bar));
        mIsFirstEnter = true;
        initReceiverObserver();
        initView();
        parseIntent();
        initRxBus();
        initData();
        //getGift();
        startThread();
        setSessionListener();
        isAttention();
        getFamilyId(false, true);
    }

    /**
     * 是否关注
     */
    private void isAttention() {
        if (sessionType != SessionTypeEnum.Team && !TextUtils.isEmpty(sessionId) && tagIsDialog == 1) {
            attentionService.getIsAttention(Integer.parseInt(sessionId), new ResponseObserver<BaseResponse<Boolean>>() {
                @Override
                public void onSuccess(BaseResponse<Boolean> response) {
                    if (getView() == null) return;
                    setAttention(response.getData());
                }
            });
        }

    }

    /**
     * 设置关注状态
     *
     * @param isAttention
     */
    private void setAttention(boolean isAttention) {
        if (isAttention) {
            mRelAttention.setVisibility(View.GONE);
        } else {
            mRelAttention.setVisibility(View.VISIBLE);
        }
    }


    private void initView() {
        mLinRootAward = findView(R.id.lin_award_root);
        tvAwardText1 = findView(R.id.tv_str1);
        rootIntoOnRoom = findView(R.id.root_into_on_room);
        svgImageOnRoom = findView(R.id.svg_image_on_room);
        nobleIntoOnRoom = findView(R.id.noble_into_on_room);
        mViewRootInput = findView(R.id.messageActivityBottomLayout);
        mRlToolBar = findView(R.id.base_tool_bar);
        mTvName = findView(R.id.toolbar_title);
        mRelAttention = findView(R.id.rl_group_attention);
        mIvtoolbarmenu = findView(R.id.toolbar_iv_menu);
        mIvtoolbarmenu.setOnClickListener(this);
        findView(R.id.toolbar_back_all).setOnClickListener(this);
        findView(R.id.tv_gone_close).setOnClickListener(this);
        findView(R.id.tv_btn_attention).setOnClickListener(this);
        //亲密度可以拨打视频显示
        mRlIntimacyCall = findView(R.id.rl_intimacy_call);
        mRlIntimacyCall.setOnClickListener(this);
        mTvIntimacyCall = findView(R.id.tv_content);
        mTvIntimacyCall.setOnClickListener(this);
        findView(R.id.iv_close_call).setOnClickListener(this);
        //家族相关
        mRlTeam = findView(R.id.base_team_tool_bar);

        mTvTeam = findView(R.id.toolbar_team_title);
        mTvTeamNum = findView(R.id.toolbar_team_num);
        findView(R.id.toolbar_team_back_all).setOnClickListener(this);
        findView(R.id.toolbar_team_iv_menu).setOnClickListener(this);
        mTvNotice = findView(R.id.tv_notice);
        mLlNotice = findView(R.id.ll_notice);
        mLlNotice.setOnClickListener(this);
        mTvContactWay = findView(R.id.tv_contact_way);
        mTvContactWay.setPauseScroll(true);
        mIvSign = findView(R.id.iv_sign_in);
        mIvSign.setOnClickListener(this);
        //亲密度相关
        mRlIntimacy = findView(R.id.rl_intimacy);
        mRlIntimacy.setOnClickListener(this);
        mTvIntimacy = findView(R.id.tv_love);
        mLavLove = findView(R.id.iv_love);
        mIvIntimacyDetail = findView(R.id.iv_intimacy_detail);
        mIvLeft = findView(R.id.iv_left);
        mIvLeft.setRoundIcon(true);
        mIvLeft.setOnClickListener(this);
        mIvRight = findView(R.id.iv_right);
        mIvRight.setRoundIcon(true);
        mIvRight.setOnClickListener(this);
        //礼物相关
        giftRoot = findView(R.id.gift_root);
        svgaImageView = findView(R.id.svg_image);
        mIvGiftMask = findView(R.id.iv_gift_mask);
        svgaParser = new SVGAParser(getActivity());
        mTvOpenVip = findView(R.id.tv_open_vip);
        mRlOpenVip = findView(R.id.rl_open_vip);
        //话题列表
        mRvTopic = findView(R.id.rv_topic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvTopic.setLayoutManager(linearLayoutManager);
        TopicAdapter topicAdapter = new TopicAdapter(0);
        ConfigInfo configInfo = service.getConfigInfo();
        List<String> topics = service.getUserInfo().getSex() == 1?configInfo.share_config.boy_quick_topic:configInfo.share_config.girl_quick_topic;
        //打乱顺序，取前五个
        Collections.shuffle(topics);
        topicAdapter.setList(topics.subList(0, Math.min(topics.size(), 5)));
        mRvTopic.setAdapter(topicAdapter);
        topicAdapter.setOnItemClickListener((adapter, view, position) -> {
            formrtAndSendMessage(topicAdapter.getItem(position));
        });
        mAnimationVip = findView(R.id.animation_vip);
        mAnimationWarn = findView(R.id.animation_warn);
        lottieAnimationView = findView(R.id.animation_view);
        //家族公告
        TextView mTvAnnouncement = findView(R.id.tv_announcement);
        mLlAnnouncement = findView(R.id.ll_announcement);
        mLlAnnouncement.setOnClickListener(this);
        //ait消息
        mFlAit = findView(R.id.fl_ait);
        mFlAit.setOnClickListener(this);
        mTvAit = findView(R.id.tv_ait);
        mTvAitNum = findView(R.id.tv_ait_num);
        mActivityIcon = findView(R.id.activity_icon);
        mActivityView = findView(R.id.message_activity);
        mLlFamilyBox = findView(R.id.ll_family_box);
        mLlFamilyBox.setOnClickListener(this);
        mBoxLottie = findView(R.id.box_lottie);
        mTvBoxTime = findView(R.id.tv_box_time);
        mTvBoxStatus = findView(R.id.family_box_status);
        mPbBox = findView(R.id.pb_box);
        //情侣表白布局
        mClGroupCouple = findView(R.id.cl_group_couple);
        mTvFrom = findView(R.id.tv_from);
        mTvGroupCoupleContent = findView(R.id.tv_group_couple_content);
        mTvTo = findView(R.id.tv_to);
        mIvFrom = findView(R.id.iv_from);
        mIvFromHead = findView(R.id.iv_from_head);
        mIvTo = findView(R.id.iv_to);
        mIvToHead = findView(R.id.iv_to_head);
        mTopVp2 = findView(R.id.top_vp2);
        mDoubleGift = findView(R.id.cl_double_gift);
        mPlayerView = findView(R.id.player_view);
        mTopScrollAdapter = new ViewPagerScrollAdapter();
        mTopScrollAdapter.setOnItemClickListener((adapter, view, position) -> {
            MultipleItem item = mTopScrollAdapter.getItem(position);
            if (item.getPerson() != null && !TextUtils.isEmpty(item.getPerson().url)) {
                BaseWebViewActivity.start(getActivity(), "", item.getPerson().url, false, 0, 4);
            }
        });
        mTopVp2.setAdapter(mTopScrollAdapter);
        View child = mTopVp2.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) giftRoot.getLayoutParams();
        layoutParams.topMargin = ConvertUtils.dp2px(120);
        giftRoot.setLayoutParams(layoutParams);

        //新增奖励金额
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) mLinRootAward.getLayoutParams();
        layoutParams2.topMargin = ConvertUtils.dp2px(90);
        mLinRootAward.setLayoutParams(layoutParams2);

        //飘萍
        mLlFloat = findView(R.id.ll_float);
        mIvCriticalLeft = findView(R.id.iv_critical_left);
        mTvFloatContent = findView(R.id.tv_float_content);
        mIvChatBg = findView(R.id.iv_chat_bg);

        mActivityView.setOnActItemClickListener(new OnActItemClickListener() {
            @Override
            public void clickCoupleLetter() {
                getCoupleLetter();
            }

            @Override
            public void openAirdrop() {
                openAirdropWindow(null, 0);
            }

            //打开情侣背包
            @Override
            public void openCoupleBad(int familyId, int bagId) {
                openCoupleBagDetail(familyId, bagId);
            }

            //打开申请解除情侣
            @Override
            public void openRelieveApply(int applyId) {
                if (removeCouplesPop == null) {
                    removeCouplesPop = new RemoveCouplesPop(requireActivity(), applyId);

                }
                removeCouplesPop.getData(applyId);
                removeCouplesPop.showPopupWindow();
            }
        }, sessionType == SessionTypeEnum.Team, sessionId, service.getUserId());


        EPSoftKeyBoardListener.setListener(getActivity(), new EPSoftKeyBoardListener.OnSoftKeyBoardChangeListener() {

            @Override
            public void keyBoardShow(int height) {
                messageListPanel.scrollToBottom();
                if (tagIsDialog == 1) {
                    ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mViewRootInput.getLayoutParams();
                    layoutParams1.bottomMargin = height;
                    mViewRootInput.setLayoutParams(layoutParams1);
                }


            }

            @Override
            public void keyBoardHide(int height) {
                if (tagIsDialog == 1) {
                    ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mViewRootInput.getLayoutParams();
                    layoutParams1.bottomMargin = 0;
                    mViewRootInput.setLayoutParams(layoutParams1);
                }

            }
        });

        if (null == svgaParser)
            svgaParser = new SVGAParser(getActivity());
        if (mParseCompletionCallback == null && !isDestroyed() && isAdded() && isAttach) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    if (isAttach && svgaImageView != null && mIvGiftMask != null) {
                        if (mTvContactWay.isPauseScroll()) {
                            mIvGiftMask.setVisibility(View.VISIBLE);
                        }
                        svgaImageView.setVideoItem(videoItem);
                        svgaImageView.stepToFrame(0, true);
                    }
                }

                @Override
                public void onError() {
                    mIsPlay = false;
                }
            };
        }
        svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                mIvGiftMask.setVisibility(View.GONE);
                mIsDown = false;
                mIsPlay = false;
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimationView.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lottieAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mPlayerView.setAnimListener(new IAnimListener() {
            @Override
            public boolean onVideoConfigReady(@NonNull AnimConfig animConfig) {
                return true;
            }

            @Override
            public void onVideoStart() {

            }

            @Override
            public void onVideoRender(int i, @Nullable AnimConfig animConfig) {

            }

            @Override
            public void onVideoComplete() {
                mIsPlay = false;
                mPlayerView.post(() -> mPlayerView.setVisibility(View.GONE));
            }

            @Override
            public void onVideoDestroy() {

            }

            @Override
            public void onFailed(int i, @Nullable String s) {
                mIsPlay = false;
            }
        });
    }


    public void showGiftAnimation(GiftDto bean) {
        if (bean == null || giftRoot == null) return;
        giftRoot.setVisibility(View.VISIBLE);
        if (null == giftViewListener) {
            giftViewListener = new GiftRootLayout.GiftRootListener() {
                @Override
                public void showGiftInfo(GiftDto giftBean) {
                }

                @Override
                public void showGiftAmin(GiftDto giftBean, int index) {
                 /*   if (giftBean == null)
                        return;*/
                }

                @Override
                public void hideGiftAmin(int index, int giftId) {
                    if (giftRoot != null) {
                        giftRoot.clearAnimation();
                        giftRoot.setVisibility(View.INVISIBLE);
                        giftRoot.invalidate();
                    }
                }
            };
        }
        giftRoot.setPlayGiftEndListener(giftViewListener);
        giftRoot.loadGift(bean);
    }

    private boolean isAttach = false;

    @Override
    public void onAttach(@NonNull Context context) {
        isAttach = true;
        P2PMessageActivity.OnHideKeyboardListener onHideKeyboardListener = event -> {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            inputPanel.getMessageActivityBottomLayout().getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + inputPanel.getMessageActivityBottomLayout().getHeight();
            int right = left + inputPanel.getMessageActivityBottomLayout().getWidth();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x > left && x < right && y > top && y < bottom) {

            } else {
                inputPanel.hideEmojiLayout();
                inputPanel.hideActionPanelLayout();
                KeyboardUtils.close(getActivity());
            }
            return false;
        };
        TeamMessageActivity.OnHideKeyboardListener onHideKeyboardListener1 = event -> {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            inputPanel.getMessageActivityBottomLayout().getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + inputPanel.getMessageActivityBottomLayout().getHeight();
            int right = left + inputPanel.getMessageActivityBottomLayout().getWidth();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x > left && x < right && y > top && y < bottom) {

            } else {
                inputPanel.hideEmojiLayout();
                inputPanel.hideActionPanelLayout();
                KeyboardUtils.close(getActivity());
            }
            return false;
        };
        if (getActivity() instanceof P2PMessageActivity) {
            ((P2PMessageActivity) getActivity()).setOnHideKeyboardListener(onHideKeyboardListener);
        }
        if (getActivity() instanceof TeamMessageActivity) {
            ((TeamMessageActivity) getActivity()).setOnHideKeyboardListener(onHideKeyboardListener1);
        }
        super.onAttach(context);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        if (sessionType == SessionTypeEnum.Team) {   //群聊
            countBackUtils = new CountBackUtils();
            ImmersionBar.setTitleBar(this, mRlTeam);
            mRlToolBar.setVisibility(View.GONE);
            if (inputPanel != null && inputPanel.getChatCall() != null)
                inputPanel.getChatCall().setVisibility(View.GONE); //隐藏打电话
            mRlTeam.setVisibility(View.VISIBLE);
            mRlIntimacyCall.setVisibility(View.GONE);
            if (inputPanel != null && mRvMore != null) {//配置家族add功能
                List<MoreFunDto> moreFunList = new ArrayList<>();
                //0 空投  1： 骰子 2 猜拳 3：组情侣 4 招募红包 5:语音闲聊
                if (null != service.getConfigInfo() && null != service.getConfigInfo().sys && null != service.getConfigInfo().sys.is_open_family_add) { //其中有一个显示了
                    inputPanel.getChatAdd().setVisibility(View.VISIBLE);
                    boolean show_new = SPUtils.getInstance().getBoolean(Interfaces.SP_CHAT_ADD_RED_POINT_FAMILY_LOVERS, true);
                    inputPanel.getChatAddPoint().setVisibility(show_new ? View.VISIBLE : View.GONE);
                    if (service.getConfigInfo().sys.is_open_family_add.gift_bag == 1) {
                        moreFunList.add(new MoreFunDto(0, "召唤空投", R.drawable.chat_ic_short_position));
                    }
                    if (service.getConfigInfo().sys.is_open_family_add.dice == 1) {
                        moreFunList.add(new MoreFunDto(1, "掷骰子", R.drawable.chat_ic_game_dice));
                    }
                    if (service.getConfigInfo().sys.is_open_family_add.fist == 1) {
                        moreFunList.add(new MoreFunDto(2, "猜拳", R.drawable.chat_ic_game_guess));
                    }
                    if (service.getConfigInfo().sys.is_open_family_add.couple == 1) {
                        moreFunList.add(new MoreFunDto(3, "组情侣", R.drawable.chat_ic_group_couple, show_new));
                    }
                } else {
                    inputPanel.getChatAdd().setVisibility(View.GONE);
                }
                moreAdapter = new MoreFunAdapter();
                mRvMore.setAdapter(moreAdapter);
                moreAdapter.setList(moreFunList);
                moreAdapter.addChildClickViewIds(R.id.more_root);
                moreAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                    MoreFunDto data = moreAdapter.getData().get(position);
                    //0 空投  1： 骰子 2 猜拳 3：组情侣 4 招募红包
                    if (data != null) {
                        if (data.id == 0) {
                            Team team = NimUIKit.getTeamProvider().getTeamById(sessionId);
                            int teamId = 0;
                            if (team != null) {
                                try {
                                    teamId = Integer.parseInt(team.getId());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mAirdropPopWindow == null)
                                mAirdropPopWindow = new AirdropPopWindow(getActivity(), teamId);
                            mAirdropPopWindow.getCoin();
                            mAirdropPopWindow.showPopupWindow();
                        } else if (data.id == 1) {  //骰子
                            doGame(1);
                        } else if (data.id == 2) {  //猜拳
                            doGame(2);
                        } else if (data.id == 3) {  //组情侣
                            getCoupleCheck();
                            SPUtils.getInstance().put(Interfaces.SP_CHAT_ADD_RED_POINT_FAMILY_LOVERS, false);
                            data.show_new = false;
                            moreAdapter.setData(position, data);
                            inputPanel.getChatAddPoint().setVisibility(View.GONE);
                        } else if (data.id == 4) {  //招募红包
                            showFamilyRecruitPop();
                        } else if (data.id == 5) {  //语音闲聊
                            if (TextUtils.equals(getString(R.string.chat_voice_chat_text), data.content)) {  //语音闲聊
                                new CustomPopWindow(getContext(), 1)
                                        .setLeftButton("取消")
                                        .setRightButton("确认")
                                        .setContent("模式切换后，所有用户将下麦，\n确认切换吗？")
                                        .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                                            @Override
                                            public void onCancel() {

                                            }

                                            @Override
                                            public void onSure() {
                                                deleteRoom(data, position);
                                            }
                                        })
                                        .showPopupWindow();
                            } else {
                                createRoom(data, position);
                            }
                        } else if (data.id == 6) {  //情侣礼包
                            openCoupleBag();
                        }
                    }
                });
            }

            if (mTeamType == 0) {
                String content = SPUtils.getInstance().getString(Interfaces.SP_FAMILY_LEVEL_UP);
                if (!TextUtils.isEmpty(content)) {
                    SPUtils.getInstance().remove(Interfaces.SP_FAMILY_LEVEL_UP);
                    ChatMsg.FamilyLevelUp levelUp = JSON.parseObject(content, ChatMsg.FamilyLevelUp.class);
                    if (mFamilyLevelUpPop == null) {
                        mFamilyLevelUpPop = new FamilyLevelUpPop(getContext());
                    }
                    KeyboardUtils.close(getActivity());
                    mFamilyLevelUpPop.setData(levelUp);
                    mFamilyLevelUpPop.showPopupWindow();
                }
            }
        } else {
            if (null != service.getConfigInfo() && null != service.getConfigInfo().sys && null != service.getConfigInfo().sys.im) {
                if (service.getUserInfo() != null && service.getUserInfo().getSex() == 1)
                    mTvIntimacyCall.setText(service.getConfigInfo().sys.im.call_greater_then_intimacy_boy_text);
                else
                    mTvIntimacyCall.setText(service.getConfigInfo().sys.im.call_greater_then_intimacy_girl_text);
            }
            if (mRlTeam != null) {
                mRlTeam.setVisibility(View.GONE);
            }
            if (mIvRight != null) {
                mIvRight.loadBuddyAvatar(String.valueOf(service.getUserId()));
            }
            if (mTvName != null) {
                mTvName.setText(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));  //用户姓名
            }
            if (mIvLeft != null) {
                mIvLeft.loadBuddyAvatar(sessionId);
            }

            if(mIvChatBg != null){
                final com.netease.nimlib.sdk.uinfo.model.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(sessionId);
                String url = "";   //头像地址
                if (userInfo != null && null != service.getConfigInfo() && null != service.getConfigInfo().api && null != service.getConfigInfo().api.oss && null != service.getConfigInfo().api.oss.cdn)
                    url = service.getConfigInfo().api.oss.cdn_scheme + service.getConfigInfo().api.oss.cdn.user + url + userInfo.getAvatar();
                GlideUtils.loadImageGaussian(getActivity(), mIvChatBg, url, R.mipmap.ic_default_avatar);
            }

            if (inputPanel != null && inputPanel.getChatRed() != null)
                inputPanel.getChatRed().setVisibility(View.GONE); //隐藏红包

            if (inputPanel != null && inputPanel.getChatAdd() != null)
                inputPanel.getChatAdd().setVisibility(View.GONE); //隐藏添加按钮

            if (tagIsDialog == 1 && inputPanel != null && StatusBarUtil.getVirtualBarHeight(requireActivity()) > 0) {//派对私聊隐藏发送礼物栏
                //兼容问题 暂时处理全部隐藏
                inputPanel.setLayoutPartyBottom(/*StatusBarUtil.checkDeviceHasNavigationBar(requireActivity())*/false);
            }
            if (tagIsDialog == 1 && inputPanel != null) {
                if (inputPanel.switchToAudioButtonInInputBar != null) {
                    inputPanel.switchToAudioButtonInInputBar.setVisibility(View.GONE);
                }
                if (inputPanel.mEmojiButtonNew != null) {
                    inputPanel.mEmojiButtonNew.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void showMoreOperation() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(ChatApiService.class)
                .getRecruitRedPacket()
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ButtonConfigDto>>() {

                    @Override
                    public void onSuccess(BaseResponse<ButtonConfigDto> response) {
                        if (response.getData() != null) {
                            int size = moreAdapter.getData().size();
                            for (int i = 0; i < size; i++) {
                                if (moreAdapter.getData().get(i).id == 4 && !response.getData().recruit)
                                    moreAdapter.remove(moreAdapter.getData().get(i));
                                if (moreAdapter.getData().get(i).id == 5 && !response.getData().family_room)
                                    moreAdapter.remove(moreAdapter.getData().get(i));
                                if (moreAdapter.getData().get(i).id == 6 && !response.getData().couple_gift_bag)
                                    moreAdapter.remove(moreAdapter.getData().get(i));
                            }
                            boolean familyRoom = false, recruit = false, coupleBag = false;
                            for (int i = 0, j = moreAdapter.getItemCount(); i < j; i++) {
                                MoreFunDto item = moreAdapter.getItem(i);
                                if (item.id == 4) {
                                    recruit = true;
                                }
                                if (item.id == 5) {
                                    familyRoom = true;
                                }
                                if (item.id == 6) {
                                    coupleBag = true;
                                }
                            }
                            //招募红包
                            if (response.getData().recruit && !recruit) {
                                moreAdapter.addData(new MoreFunDto(4, "招募红包", R.drawable.chat_ic_red_packet));
                            }
                            //语音闲聊
                            if (response.getData().family_room && !familyRoom)
                                if (mIsOpenRoom == 0) {
                                    moreAdapter.addData(new MoreFunDto(5, getString(R.string.chat_voice_chat), R.drawable.chat_ic_voice_chat));
                                } else {
                                    moreAdapter.addData(new MoreFunDto(5, getString(R.string.chat_voice_chat_text), R.mipmap.chat_ic_voice_chat_text));
                                }
                            //情侣大礼包
                            if (response.getData().couple_gift_bag && !coupleBag)
                                moreAdapter.addData(new MoreFunDto(6, "情侣礼包", R.mipmap.chat_ic_couple_bag));
                        } else {
                            for (int i = 0, j = moreAdapter.getItemCount(); i < j; i++) {
                                MoreFunDto item = moreAdapter.getItem(i);
                                if (item.id == 4) {
                                    moreAdapter.removeAt(i);
                                    break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 显示申请
     */
    private void showApply() {
        if (sessionType == SessionTypeEnum.Team && mTeamType == 0 && mLlNotice != null) {   //群聊
            if (com.tftechsz.common.utils.CommonUtil.isShowApplyNum(service.getUserId(), mTvNotice, 1)) {
                mLlNotice.setVisibility(View.VISIBLE);
            } else {
                mLlNotice.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 显示ait
     */
    private void showAit() {
        if (sessionType == SessionTypeEnum.Team && mTeamType == 0 && mFlAit != null) {   //群聊
            com.tftechsz.common.utils.CommonUtil.isShowAitNum(service.getUserId(), mFlAit, mTvAit, mTvAitNum);
        }
    }


    /**
     * 显示公告
     */
    private void showAnnouncement() {
        if (sessionType == SessionTypeEnum.Team && mTeamType == 0) {
            String announcement = MMKVUtils.getInstance().decodeString(service.getUserId() + Constants.FAMILY_ANNOUNCEMENT);
            if (!TextUtils.isEmpty(announcement)) {
                mLlAnnouncement.setVisibility(View.VISIBLE);
//                mTvAnnouncement.setText(announcement);
            } else {
                mLlAnnouncement.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);
        return rootView;
    }

    private void initRxBus() {
        //刷新列表
        mCompositeDisposable.add(RxBus.getDefault().toObservable(MessageCallEvent.class)
                .subscribe(
                        message -> {
                            if (null != message) {
                                if (message.type != 1) {   //当不等于1的时候才更新
                                    messageListPanel.onMsgSend(message.message);
                                }
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(BgSetEvent.class)
                .subscribe(

                        message -> {
                            setWindowBg(message.bg);
                        }
                ));


        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_DISSOLUTION_SUCCESS
                                    || event.type == Constants.NOTIFY_UPDATE_EXIT_FAMILY || event.type == Constants.NOTIFY_BLACK_SUCCESS) {  //解散家族成功 拉黑成功
                                if (isAdded() && getActivity() != null) {
                                    getActivity().finish();
                                }
                            } else if (event.type == Constants.NOTIFY_USER_APPLY_JOIN) {  //有用户申请加入家族
                                showApply();
                            } else if (event.type == Constants.NOTIFY_FAMILY_FIRST_JOIN) { //首次加入家族
                                ChatMsg.AlertEvent dto = JSON.parseObject(event.code, ChatMsg.AlertEvent.class);
                                mPopWindow = new WelcomeToFamilyPopWindow(getContext(), () -> {
                                    IMMessage message = MessageBuilder.createTextMessage(sessionId, sessionType,
                                            dto != null && !TextUtils.isEmpty(dto.msg) ? dto.msg : "大家好啊,很高兴认识大家~！");
                                    sendMessage(message);
                                    mPopWindow.dismiss();
                                });
                                mPopWindow.showPopupWindow();
                            } else if (event.type == Constants.NOTIFY_SIGN_IN_SUCCESS) {  //签到成功
                                mIvSign.setVisibility(View.GONE);
                            } else if (event.type == Constants.NOTIFY_AIT_SELF) {  //ait消息
                                showAit();
                            } else if (event.type == Constants.NOTIFY_FAMILY_RANK) {   //家族排行
                                ChatMsg.JoinFamily dto = JSON.parseObject(event.code, ChatMsg.JoinFamily.class);
                                if (dto != null) {
                                    for (int i = 0, j = mTopScrollAdapter.getItemCount(); i < j; i++) {
                                        if (mTopScrollAdapter.getItemViewType(i) == Interfaces.ADAPTER_TYPE_1) {
                                            mTopScrollAdapter.setData(i, new MultipleItem(Interfaces.ADAPTER_TYPE_1, dto));
                                            return;
                                        }
                                    }
                                    mTopScrollAdapter.addData(new MultipleItem(Interfaces.ADAPTER_TYPE_1, dto));
                                }
                            } else if (event.type == Constants.NOTIFY_FAMILY_SWEET_RANK) {   //家族情侣
                                ChatMsg.JoinFamily dto = JSON.parseObject(event.code, ChatMsg.JoinFamily.class);
                                if (dto != null) {
                                    for (int i = 0, j = mTopScrollAdapter.getItemCount(); i < j; i++) {
                                        if (mTopScrollAdapter.getItemViewType(i) == Interfaces.ADAPTER_TYPE_2) {
                                            mTopScrollAdapter.setData(i, new MultipleItem(Interfaces.ADAPTER_TYPE_2, dto));
                                            return;
                                        }
                                    }
                                    mTopScrollAdapter.addData(new MultipleItem(Interfaces.ADAPTER_TYPE_2, dto));
                                }
                            } else if (event.type == Constants.NOTIFY_FAMILY_GIFT_BAG) {  //空投通知
                                if (sessionType != SessionTypeEnum.Team) {
                                    return;
                                }
                                ChatMsg.Airdrop airdrop = JSON.parseObject(event.code, ChatMsg.Airdrop.class);
                                airdrop.type = "gift_bag";
                                airdrop.sortId = 1;
                                mActivityView.updateActivityData(airdrop);
                            } else if (event.type == Constants.NOTIFY_FAMILY_COUPLE_LETTER) { //组情侣
                                if (sessionType != SessionTypeEnum.Team) {
                                    return;
                                }
                                ChatMsg.Airdrop airdrop = JSON.parseObject(event.code, ChatMsg.Airdrop.class);
                                airdrop.type = "couple_letter";
                                airdrop.sortId = 2;
                                mActivityView.updateActivityData(airdrop);
                            } else if (event.type == Constants.NOTIFY_FAMILY_COUPLE_PASTER) { //组情侣礼包贴片
                                if (sessionType != SessionTypeEnum.Team) {
                                    return;
                                }
                                ChatMsg.Airdrop airdrop = JSON.parseObject(event.code, ChatMsg.Airdrop.class);
                                airdrop.type = "couple_paster";
                                airdrop.sortId = 4;
                                mActivityView.updateActivityData(airdrop);
                            } else if (event.type == Constants.NOTIFY_FAMILY_RELIEVE_APPLY) { //组情侣礼解除通知
                                if (sessionType != SessionTypeEnum.Team) {
                                    return;
                                }
                                ChatMsg.Airdrop airdrop = JSON.parseObject(event.code, ChatMsg.Airdrop.class);
                                airdrop.type = "couple_relieve_apply";
                                airdrop.sortId = 5;
                                mActivityView.updateActivityData(airdrop);
                            } else if (event.type == Constants.NOTIFY_ACTIVITY_QIXI || event.type == Constants.NOTIFY_ACTIVITY_P2P || event.type == Constants.NOTIFY_ACTIVITY_FAMILY) {//活动-七夕 || 活动-单聊  || 活动私聊
                                if (event.type == Constants.NOTIFY_ACTIVITY_P2P && sessionType != SessionTypeEnum.P2P) {
                                    return;
                                }
                                if (event.type == Constants.NOTIFY_ACTIVITY_FAMILY && sessionType != SessionTypeEnum.Team) {
                                    return;
                                }
                                ChatMsg.Activity activity = JSON.parseObject(event.code, ChatMsg.Activity.class);
                                if (activity != null && activity.activities != null && !activity.activities.isEmpty() && activity.open == 1) {
                                    for (ChatMsg.Activities activities : activity.activities) {
                                        ChatMsg.Airdrop airdrop = JSON.parseObject(event.code, ChatMsg.Airdrop.class);
                                        airdrop.num = 1;
                                        airdrop.type = activities.type;
                                        airdrop.activity = activities;
                                        mActivityView.updateActivityData(airdrop);
                                    }
                                }

                            } else if (event.type == Constants.NOTIFY_FAMILY_LEVEL_UP) { //家族等级提升
                                if (sessionType == SessionTypeEnum.Team && mTeamType == 0) { //家族群聊
                                    SPUtils.getInstance().remove(Interfaces.SP_FAMILY_LEVEL_UP);
                                    ChatMsg.FamilyLevelUp levelUp = JSON.parseObject(event.code, ChatMsg.FamilyLevelUp.class);
                                    if (mFamilyLevelUpPop == null) {
                                        mFamilyLevelUpPop = new FamilyLevelUpPop(getContext());
                                    }
                                    KeyboardUtils.close(getActivity());
                                    mFamilyLevelUpPop.setData(levelUp);
                                    mFamilyLevelUpPop.showPopupWindow();
                                }
                            } else if (event.type == Constants.NOTIFY_FAMILY_BOX) { //家族宝箱
                                if (sessionType == SessionTypeEnum.P2P) {
                                    return;
                                }
                                mFamilyBox = JSON.parseObject(event.code, ChatMsg.FamilyBox.class);
                                if (mFamilyBox != null) {
                                    mLlFamilyBox.setVisibility(View.VISIBLE);
                                    if (mFamilyBox.activity_desc != null && mFamilyBox.activity_desc.size() > 0) {
                                        SPUtils.getInstance().put(Interfaces.SP_FAMILY_BOX_ACTIVITY_DESC, JSON.toJSONString(mFamilyBox.activity_desc));
                                    }
                                    if (mFamilyBox.status == 1) {//如果是抢宝箱状态, 就播放动画
                                        mBoxLottie.playAnimation();
                                        mTvBoxTime.setVisibility(View.INVISIBLE);
                                    } else {
                                        mTvBoxTime.setVisibility(View.INVISIBLE);
                                        mBoxLottie.cancelAnimation();
                                        if (mFamilyBox.status == 0) {
                                            mTvBoxTime.setVisibility(View.VISIBLE);
                                            mTvBoxTime.setText(Utils.getLastTime(mFamilyBox.count_down));
                                            if (mCountBack2Box == null)
                                                mCountBack2Box = new CountBackUtils();
                                            mCountBack2Box.countBack(mFamilyBox.count_down, new CountBackUtils.Callback() {
                                                @Override
                                                public void countBacking(long time) {
                                                    mFamilyBox.count_down = time;
                                                    mTvBoxTime.setText(Utils.getLastTime(time));
                                                }

                                                @Override
                                                public void finish() {
                                                    mFamilyBox.count_down = 0;
                                                    mTvBoxTime.setText(Utils.getLastTime(0));
                                                }
                                            });
                                        }
                                    }
                                    if (mFamilyBox.defaultUsersCount != 0 && mFamilyBox.defaultCoins != 0) { //进度条计算
                                        int v = (int) (1d * Math.min(mFamilyBox.realUsersCount, mFamilyBox.defaultUsersCount) / mFamilyBox.defaultUsersCount * 50
                                                + 1d * Math.min(mFamilyBox.realCoins, mFamilyBox.defaultCoins) / mFamilyBox.defaultCoins * 50);
                                        mPbBox.setProgress(v);
                                        if (v == 100) {
                                            mTvBoxStatus.setText("已完成");
                                        } else {
                                            mTvBoxStatus.setText(v + "%");
                                        }
                                    }

                                    if (mFamilyBoxPop != null && mFamilyBoxPop.isShowing()) {
                                        mFamilyBoxPop.setData(mFamilyBox);
                                    }

                                    if (mFamilyBox.status == 1) {
                                        if (mFamilyBoxPop == null) {
                                            mFamilyBoxPop = new FamilyBoxPop(getContext());
                                        }
                                        if (!mFamilyBoxPop.isShowing()) {
                                            mFamilyBoxPop.setData(mFamilyBox);
                                            mFamilyBoxPop.showPopupWindow();
                                        }
                                    }
                                }
                            } else if (event.type == Constants.NOTIFY_CHAT_ALERT_REAL) {    //聊天真人弹窗
                                ChatMsg.Alert alertDto = JSON.parseObject(event.code, ChatMsg.Alert.class);
                                if (alertDto != null) {
                                    CustomPopWindow alertPop = new CustomPopWindow(getContext()).setTitle(alertDto.title)
                                            .setContent(alertDto.des)
                                            .setLeftButton(alertDto.left_button.msg)
                                            .setRightButton(alertDto.right_button.msg)
                                            .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                                                @Override
                                                public void onCancel() {
                                                    if (alertDto.left_button.is_finish) {
                                                        FragmentActivity activity = getActivity();
                                                        if (activity != null) {
                                                            activity.finish();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onSure() {
                                                    com.tftechsz.common.utils.CommonUtil.performLink(getContext(), new ConfigInfo.MineInfo(alertDto.right_button.event), 0, 0);
                                                    if (alertDto.right_button.is_finish) {
                                                        FragmentActivity activity = getActivity();
                                                        if (activity != null) {
                                                            activity.finish();
                                                        }
                                                    }
                                                }
                                            });
                                    alertPop.setOutSideDismiss(alertDto.is_outside_enable);
                                    alertPop.setBackPressEnable(alertDto.is_outside_enable);
                                    alertPop.showPopupWindow();
                                }
                            } else if (event.type == Constants.NOTIFY_INTIMACY_GIFT) {//亲密度聊天弹窗
                                ChatMsg.IntimacyGift ig = JSON.parseObject(event.code, ChatMsg.IntimacyGift.class);
                                if (ig != null) {
                                    if (mIntimacyGiftPop == null) {
                                        mIntimacyGiftPop = new IntimacyGiftPop(getContext());
                                    }
                                    mIntimacyGiftPop.setData(ig);
                                    mIntimacyGiftPop.showPopupWindow();
                                }
                            }
                        }
                ));


        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == NOTIFY_UPDATE_VOICE_INFO) {
                                if (mVoiceChat != null) {
                                    mVoiceChat.setAnnouncement(event.code, null);
                                }
                            }
                        }
                ));

    }

    /**
     * 新增进场动画
     */
    private void initSvg(ChatMsg.NobleEnterAni enterAni) {
        rootIntoOnRoom.setVisibility(View.VISIBLE);
        //新的贵族入账动效
        FrameLayout.LayoutParams bottom = (FrameLayout.LayoutParams) nobleIntoOnRoom.getLayoutParams();
        if (enterAni.id >= 200 || enterAni.id == 25) {
            bottom.width = DensityUtils.dp2px(requireActivity(), 200);
            nobleIntoOnRoom.setLayoutParams(bottom);
            FrameLayout.LayoutParams top = (FrameLayout.LayoutParams) svgImageOnRoom.getLayoutParams();
            svgImageOnRoom.setLayoutParams(top);
            if (enterAni.id == 200 || enterAni.id == 201 || enterAni.id == 202) {
                aniSVGAImage(nobleIntoOnRoom, "noble_enter_ani_bottom_%s.svga", enterAni);
                aniSVGAImage(svgImageOnRoom, "noble_enter_ani_top_%s.svga", enterAni);
            } else {
                aniSVGAImage(nobleIntoOnRoom, "noble_enter_ani_%s.svga", enterAni);
            }
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_right_in_party_banner);
            Animation animationOut = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_right_out_party_banner);
            animationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rootIntoOnRoom.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            rootIntoOnRoom.startAnimation(animation);
            if (null == mSvgBack)
                mSvgBack = new CountBackUtils();
            mSvgBack.countBack(3, new CountBackUtils.Callback() {
                @Override
                public void countBacking(long time) {
                    if (time == 1) {
                        rootIntoOnRoom.startAnimation(animationOut);
                    }
                }

                @Override
                public void finish() {

                }
            });
        } else {
            bottom.width = FrameLayout.LayoutParams.MATCH_PARENT;
            nobleIntoOnRoom.setLayoutParams(bottom);
            aniSVGAImage(nobleIntoOnRoom, "noble_enter_ani_%s.svga", enterAni);
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
                    if (requireActivity() != null) {
                        if (requireActivity().getResources().getDisplayMetrics().density > 2.5) {
                            textPaint.setTextSize(DensityUtils.dp2px(requireActivity(), 18));
                        } else {
                            textPaint.setTextSize(DensityUtils.dp2px(requireActivity(), 28));
                        }
                    }


                    dynamicEntity.setDynamicText(new StaticLayout(ssb, 0, ssb.length(), textPaint, 0, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false), key1);
                    SpannableStringBuilder ssb2 = new SpannableStringBuilder(enterAni.tips);
                    ssb2.setSpan(new ForegroundColorSpan(Color.parseColor(contentColor)), 0, ssb2.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    TextPaint textPaint2 = new TextPaint();
                    if (requireActivity() != null) {
                        if (requireActivity().getResources().getDisplayMetrics().density > 2.5) {
                            textPaint2.setTextSize(DensityUtils.dp2px(requireActivity(), 14));
                        } else {
                            textPaint2.setTextSize(DensityUtils.dp2px(requireActivity(), 24));
                        }
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


    private void getCoin() {
        service.getField("property", "coin", new ResponseObserver<BaseResponse<IntegralDto>>() {
            @Override
            public void onSuccess(BaseResponse<IntegralDto> response) {
                if (response.getData() != null) {
                    UserInfo userInfo = service.getUserInfo();
                    userInfo.setCoin(response.getData().coin);
                    service.setUserInfo(userInfo);
                    if (giftPopWindow != null) {
                        giftPopWindow.setCoin(response.getData().coin);
                    }
                    if (mRedEnvelopePopWindow != null) {
                        mRedEnvelopePopWindow.setCoin(service.getUserInfo().getCoin());
                    }
                }
            }
        });
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        inputPanel.onPause();
        messageListPanel.onPause();
        isResume = false;
        if (sessionType == SessionTypeEnum.Team && mTeamType == 0) {
            if (mActivityView != null)
                mActivityView.stopAutoScroll();
            joinFamilyRoom("close");
            mHandler.removeCallbacks(runnable);
        }
        if (mIntimacyEntity != null && sessionType == SessionTypeEnum.P2P) {
            mIntimacyEntity.setEndTime(intimacyEndTime);
            mCompositeDisposable.add(DbManager.getInstance().update(mIntimacyEntity).subscribe(l -> {
            }));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        isResume = true;
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        if (getActivity() != null)
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC); // 扬声器播放
        showApply();
        showAit();
        showAnnouncement();
        mTvContactWay.setBackgroundColor(Utils.getColor(R.color.transparent));
        mTvContactWay.setPauseScroll(true);
        mTvContactWay.setText("");
        if (sessionType == SessionTypeEnum.Team) {
            if (mTeamType == 1) {
                mTvTeam.setText("聊天广场");
            } else {
                joinFamilyRoom("open");
                Team team = NimUIKit.getTeamProvider().getTeamById(sessionId);
                String teamTitle = team == null ? sessionId : team.getName();
//                String teamTitltee = "山东威海未发货我发给去东莞是的";
                mTvTeam.setText(teamTitle);
                String teamNum = "(" + (team == null ? -1 : team.getMemberCount()) + ")";
                mTvTeamNum.setText(teamNum);
                mHandler.postDelayed(runnable, Interfaces.CHAT_TOP_SCROLL_TIMES);
                if (mActivityView != null)
                    mActivityView.startAutoScroll();
            }
        } else {
            mActivityView.setVisibility(View.INVISIBLE);
            mCompositeDisposable.add(DbManager.getInstance().query(sessionId, service.getUserId() + "").subscribe(intimacyEntity -> {
                if (intimacyEntity == null) return;
                mIntimacyEntity = intimacyEntity;
                intimacyEndTime = intimacyEntity.getEndTime();
                if (intimacyEntity.getIsShow() == 1) {
                    mRlIntimacyCall.setVisibility(View.VISIBLE);
                }
                if (intimacyEndTime > 0) {
                    setContactTip(intimacyEndTime);
                } else {
                    mTvContactWay.setBackgroundColor(Utils.getColor(R.color.transparent));
                    mTvContactWay.setPauseScroll(true);
                    mTvContactWay.setText("");
                }
            }));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatSoundPlayer.instance().stop();
        messageListPanel.onDestroy();
        try {
            singleThreadExecutor.shutdown();
            if (!singleThreadExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                singleThreadExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            singleThreadExecutor.shutdownNow();
        }
        safeHandle.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        myGiftList.clear();
        mLlNotice = null;
        ImmersionBar.destroy(this);
        if (mPlayerView != null) {
            mPlayerView.stopPlay();
        }
        mFloatGift.clear();
        if (mMapAward != null)
            mMapAward.clear();
        if (countBackUtils != null)
            countBackUtils.destroy();
        if (mFloatBack != null)
            mFloatBack.destroy();
        if (contactBack != null)
            contactBack.destroy();
        if (mSvgBack != null)
            mSvgBack.destroy();
        if (mCountBack2Box != null)
            mCountBack2Box.destroy();
        if (animationBack != null)
            animationBack.destroy();
        if (aitManager != null) {
            aitManager.reset();
            aitManager = null;
        }
        if (mBoxLottie != null) {
            mBoxLottie.clearAnimation();
            mBoxLottie = null;
        }
        if (svgaImageView != null) {
            svgaImageView.clearAnimation();
            svgaImageView.clear();
            svgaImageView = null;
        }
        if (mAnimationVip != null) {
            mAnimationVip.clearAnimation();
            mAnimationVip = null;
        }
        if (mAnimationWarn != null) {
            mAnimationWarn.clearAnimation();
            mAnimationWarn = null;
        }
        if (sessionType == SessionTypeEnum.Team && mTeamType == 1)
            leaveRoom();
        if (mActivityView != null) {
            mActivityView.onDestroy();
            mActivityView.removeAllViews();
            mActivityView = null;
        }
        if (mDoubleGift != null) {
            mDoubleGift.destroy();
            mDoubleGift = null;
        }
        mIsFirstEnter = false;
        NimUIKit.setSessionListener(null);
        Utils.removeHandler();
        destroyView();
    }


    @Override
    public void getCallBackContent(ChatMsg.CallBackMessage callBackMessage, IMMessage
            msg, String content) {
        showIntimacy(msg, content);
    }


    /**
     * 显示爱心
     */
    private void showIntimacy(IMMessage message, String content) {
        if (TextUtils.isEmpty(content)) return;
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(content);
        try {
            if (null != chatMsg) {   //消息回调（弹窗类型）
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //通过消息返回值 弹出充值
                    ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
                    if (alert == null) return;
                    if (message != null && alert.is_fail) {
                        message.setStatus(MsgStatusEnum.fail);
                        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
                        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                        messageListPanel.removeMessageItem(message.getUuid());
                        messageListPanel.refreshMessageList();
                    }
                }
                //判断是否是礼物
                if (TextUtils.equals("gift_play", chatMsg.cmd_type)) {
                    GiftDto gift = JSON.parseObject(chatMsg.content, GiftDto.class);
                    List<String> user = new ArrayList<>();
                    user.add(chatMsg.to);
                    sendGiftSuccess(gift, message, user);
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_AFTER_INTIMACY_TIPS) && sessionType == SessionTypeEnum.P2P) {   //亲密度达到可以音视频通话
                    ChatMsg.EventType intimacy = JSON.parseObject(chatMsg.content, ChatMsg.EventType.class);
                    if (intimacy != null && !TextUtils.isEmpty(intimacy.her_user_id)) {
                        IntimacyEntity entity;
                        if (mIntimacyEntity != null) {
                            entity = mIntimacyEntity;
                        } else {
                            entity = new IntimacyEntity();
                            entity.setUserId(intimacy.her_user_id);
                            entity.setSelfId(service.getUserId() + "");
                        }
                        entity.setIsShow(1);

                        mCompositeDisposable.add(DbManager.getInstance().insert(entity).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) {
                                mIntimacyEntity = entity;
                                mRlIntimacyCall.setVisibility(View.VISIBLE);
                            }
                        }));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_TYPE)) {  //亲密度通知
                    ////{"intimacy":"2822.3℃","change":"0℃","is_show_heart":true}
                    ChatMsg.Intimacy intimacy = JSON.parseObject(chatMsg.content, ChatMsg.Intimacy.class);
                    ViewGroup.LayoutParams params = mRlToolBar.getLayoutParams();
                    if (intimacy.is_show_heart) {
                        mRlIntimacy.setVisibility(View.VISIBLE);
                        mTvName.setVisibility(View.GONE);
                        mTvIntimacy.setText("亲密度"+intimacy.intimacy);
                        setIntimacLottie(intimacy);
                        params.height = ConvertUtils.dp2px(110);
                    } else {
                        mRlIntimacy.setVisibility(View.GONE);
                        mTvName.setVisibility(View.VISIBLE);
                        params.height = ConvertUtils.dp2px(90);
                    }
                    mIntimacy = intimacy.intimacy;
                    if (inputPanel != null) {
                        inputPanel.setSpamExt(mIntimacy, service.getUserInfo() == null ? 0 : service.getUserInfo().getSex());
                    }
                    mRlToolBar.setLayoutParams(params);
                    messageListPanel.scrollToBottom();
                    //判断是否是礼物
                    if (!TextUtils.isEmpty(chatMsg.child)) {
                        ChatMsg chatMsgChild = JSON.parseObject(chatMsg.child, ChatMsg.class);
                        if (chatMsgChild != null && TextUtils.equals("gift_play", chatMsgChild.cmd_type)) {
                            GiftDto gift = JSON.parseObject(chatMsgChild.content, GiftDto.class);
                            List<String> user = new ArrayList<>();
                            user.add(chatMsgChild.to);
                            sendGiftSuccess(gift, message, user);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示爱心lottie动画
     * @param intimacy
     */
    private void setIntimacLottie(ChatMsg.Intimacy intimacy) {
        String s = intimacy.intimacy.replace("℃", "");
        double intimacyInt = Double.parseDouble(s);
        String lottieZipFileNameLast;
        if (intimacyInt>100){
            lottieZipFileNameLast = "11";
        }else if (intimacyInt>90){
            lottieZipFileNameLast = "10";
        }else if (intimacyInt>80){
            lottieZipFileNameLast = "9";
        } else if (intimacyInt>70){
            lottieZipFileNameLast = "8";
        } else if (intimacyInt>60){
            lottieZipFileNameLast = "7";
        } else if (intimacyInt>50){
            lottieZipFileNameLast = "6";
        }else if (intimacyInt>40){
            lottieZipFileNameLast = "5";
        }else if (intimacyInt>30){
            lottieZipFileNameLast = "4";
        }else if (intimacyInt>20){
            lottieZipFileNameLast = "3";
        }else if (intimacyInt>10){
            lottieZipFileNameLast = "2";
        }else {
            lottieZipFileNameLast = "1";
        }
        mLavLove.setAnimation("love"+lottieZipFileNameLast+".zip");
        mLavLove.playAnimation();
    }


    public void onKeyBack() {

    }

    /**
     * 显示聊天实时爱心
     */
    private void showIntimacyAttr(String content) {
        ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(content);
        try {
            if (null != chatMsg && null != chatMsg.msg_intimacy) {   //消息回调（弹窗类型）
                ViewGroup.LayoutParams params = mRlToolBar.getLayoutParams();
                if (chatMsg.msg_intimacy.is_show_heart) {
                    mRlIntimacy.setVisibility(View.VISIBLE);
                    mTvName.setVisibility(View.GONE);
                    mTvIntimacy.setText("亲密度"+chatMsg.msg_intimacy.intimacy);
                    setIntimacLottie(chatMsg.msg_intimacy);
                    params.height = ConvertUtils.dp2px(110);
                } else {
                    mRlIntimacy.setVisibility(View.GONE);
                    mTvName.setVisibility(View.VISIBLE);
                    params.height = ConvertUtils.dp2px(90);
                }
                mIntimacy = chatMsg.msg_intimacy.intimacy;
                if (inputPanel != null) {
                    inputPanel.setSpamExt(mIntimacy, service.getUserInfo() == null ? 0 : service.getUserInfo().getSex());
                }
                mRlToolBar.setLayoutParams(params);
                messageListPanel.scrollToBottom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void showCommandMessage(CustomNotification message) {
        if (!isResume) {
            return;
        }
        String content = message.getContent();
        try {
            JSONObject json = JSON.parseObject(content);
            int id = json.getIntValue("id");
            if (id == 1) {
                // 正在输入
                ToastHelper.showToastLong(getActivity(), "对方正在输入...");
            } else {
            }
        } catch (Exception ignored) {

        }
    }


    public boolean onBackPressed() {
        if (inputPanel != null)
            return inputPanel.collapse(true);
        return true;
    }

    public void refreshMessageList() {
        messageListPanel.refreshMessageList();
    }


    public void parseIntent() {
        Bundle arguments = getArguments();
        sessionId = arguments.getString(Extras.EXTRA_ACCOUNT, "");
        sessionType = (SessionTypeEnum) arguments.getSerializable(Extras.EXTRA_TYPE);
        tagIsDialog = arguments.getInt(EXTRA_TYPE_DIALOG_ACTIVITY);
        mHeight = arguments.getInt(EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT);
        if (tagIsDialog == 1) {//dialog样式
//            mRlToolBar.setBackground(getResources().getDrawable(R.drawable.bg_mine_white_top));
//            mRlTeam.setBackground(getResources().getDrawable(R.drawable.bg_mine_white_top));
            findView(R.id.ll_function).setVisibility(View.GONE);
        } else {
            mRlToolBar.setBackgroundResource(R.color.transparent);
            mRlTeam.setBackgroundResource(R.color.white);
        }
        mTeamType = arguments.getInt("teamType");

        if (tagIsDialog == 1) {
            FrameLayout frameLayout = findView(R.id.frame_root);
            if (frameLayout != null) {
                frameLayout.setBackgroundResource(R.drawable.bg_mine_white_top);
            }
        }


        IMMessage anchor = (IMMessage) arguments.getSerializable(Extras.EXTRA_ANCHOR);
        customization = (SessionCustomization) arguments.getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this, true);
        boolean isFirstTeam = MMKVUtils.getInstance().decodeBoolean(Constants.TEAM_IS_FIRST);
        if (messageListPanel == null) {
            messageListPanel = new MessageListPanelEx(container, rootView, anchor, false, !isFirstTeam && sessionType == SessionTypeEnum.Team);
        } else {
            messageListPanel.reload(container, anchor);
        }
        if (sessionType == SessionTypeEnum.P2P) {
            joinP2PRoom();
        }
        MMKVUtils.getInstance().encode(Constants.TEAM_IS_FIRST, true);
        messageListPanel.addOnMessageListener(this);
        if (inputPanel == null) {
            inputPanel = new InputPanel(container, rootView, getActionList(), tagIsDialog == 1);
            inputPanel.setCustomization(customization);
        } else {
            inputPanel.reload(container, customization);
        }
        inputPanel.getmIvTopicBtn().setOnClickListener(v->{
            //话题icon
            ConfigInfo configInfo = service.getConfigInfo();
            TopicPop topicPop = new TopicPop(getActivity(),service.getUserInfo().getSex() == 1?configInfo.share_config.boy_quick_topic:configInfo.share_config.girl_quick_topic);
            topicPop.setTopicItemClickListener(new TopicPop.TopicItemOnClickListener() {
                @Override
                public void onTopicItemClick(String text) {
                    formrtAndSendMessage(text);
                }
            });
            topicPop.showPopupWindow();
        });
        mRvMore = inputPanel.getRvMore();
        mRvMore.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        ivChatPhoto = inputPanel.getChatPhone();
        ivChatCall = inputPanel.getChatCall();
        mIvVoiceCall = inputPanel.getmIvVoiceCallBtn();
        ivChatRed = inputPanel.getChatRed();
        ivChatGift =  inputPanel.getChatGift();


        initAitManager();

        inputPanel.switchRobotMode(NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null);
        inputPanel.setInputListener(this);

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }

        //视频通话
        ivChatCall.setOnClickListener(v -> {
            checkCallMsg(1);
        });
        //语音通话
        mIvVoiceCall.setOnClickListener(v->{
            checkCallMsg(2);
        });

        //礼物
        ivChatGift.setOnClickListener(v -> {
            showGiftPop(sessionId, mTeamType == 1 ? 5 : 2);
        });

        //红包点击监听
        ivChatRed.setOnClickListener(v -> {
            com.blankj.utilcode.util.KeyboardUtils.hideSoftInput((Activity) getContext());
            if (mRedEnvelopePopWindow == null) {
                mRedEnvelopePopWindow = new SendRedEnvelopePopWindow(getActivity());
                mRedEnvelopePopWindow.addOnclikListener((gold_price, gold_num, receiveUserType, gold_content) -> sendGoldRedEnvelope(mTeamType, gold_price, gold_num, receiveUserType, gold_content));
            }
            getCoin();
            isFamilyRecruit = false;
            mRedEnvelopePopWindow.setIsFamilyRecruit(isFamilyRecruit);
            mRedEnvelopePopWindow.showPopupWindow();
        });

        //图片点击
        ivChatPhoto.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mIntimacy) && service != null && service.getUserInfo() != null && service.getUserInfo().isBoy() && sessionType == SessionTypeEnum.P2P) {
                float level = Float.parseFloat(mIntimacy.replace("℃", ""));
                if (level < service.getConfigInfo().sys.boy2girl_pic_intimacy) {
                    ToastUtil.showToast(requireActivity(), "亲密度>=" + service.getConfigInfo().sys.boy2girl_pic_intimacy + "度才能发送图片");
                    return;
                }
            } else if ((TextUtils.isEmpty(mIntimacy) && service != null && service.getUserInfo() != null && service.getUserInfo().isBoy()) && sessionType == SessionTypeEnum.P2P) {
                ToastUtil.showToast(requireActivity(), "亲密度>=" + service.getConfigInfo().sys.boy2girl_pic_intimacy + "度才能发送图片");
                return;
            }
            if (null == customPopWindow) {
                customPopWindow = new CustomPopWindow(getActivity());
            }
            String content;   //提示文字
            if (null != service.getConfigInfo() && null != service.getConfigInfo().sys && null != service.getConfigInfo().sys.content)
                content = service.getConfigInfo().sys.content.picture_warm;
            else
                content = "注意:照片请勿作假，涉黄，如被举报并核实，系统会自动禁用图片功能，严重者冻结账号";
            customPopWindow.setContent(content);
            customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onSure() {
                    ChoosePicUtils.picSingle(getActivity(), new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            if (result != null && result.size() > 0) {
                                String imgPath;
                                if (result.get(0).isCompressed()) {
                                    imgPath = result.get(0).getCompressPath();
                                } else {
                                    imgPath = result.get(0).getRealPath();
                                }
                                if (!TextUtils.isEmpty(imgPath)) {
                                    File file = new File(imgPath);
                                    onPicked(file);
                                }
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            });
            customPopWindow.showPopupWindow();
        });
    }

    private void formrtAndSendMessage(String text) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            ToastHelper.showToast(getActivity(), "发送消息不能为空");
            return;
        }
        IMMessage textMessage = MessageBuilder.createTextMessage(sessionId, sessionType, text);
        if (sessionType == SessionTypeEnum.P2P) {
            String spamExt = "{\"level\":\"" + mIntimacy + "\",\"gender\":\"" + (service.getUserInfo() == null ? 0 : service.getUserInfo().getSex()) + "\"}";
            textMessage.setYidunAntiSpamExt(spamExt);
        }
        sendMessage(textMessage);
    }


    private void initAitManager() {
        UIKitOptions options = NimUIKitImpl.getOptions();
        if (options.aitEnable) {
            aitManager = new AitManager((teamType, tid) -> {
                if (teamType == 0) {
                    ARouterUtils.toFamilyAitMember(getActivity(), tid);
                } else {
                    ARouterUtils.toRoomMember(getActivity(), tid, 2);
                }
            }, options.aitTeamMember && sessionType == SessionTypeEnum.Team ? sessionId : null, options.aitIMRobot);
            inputPanel.addAitTextWatcher(aitManager);
            aitManager.setTextChangeListener(inputPanel);
            aitManager.setTeamType(mTeamType);
        }
    }

    /* *//**
     * 获取礼物数据
     *//*
    private void getGift() {
        int cate;
        if (mTeamType == 1) {   //广场
            cate = 4;
        } else {
            cate = sessionType == SessionTypeEnum.P2P ? 2 : 3;   //单聊  群聊
        }
        mCompositeDisposable.add(new RetrofitManager().createExchApi(ChatApiService.class).getGift(cate).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (!TextUtils.isEmpty(response.getData())) {
                            String key = response.getData().substring(0, 6);
                            byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                            String iv = MD5Util.toMD532(key).substring(0, 16);
                            byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                            LogUtil.e("------------", new String(jsonData));
                            mGiftList = JSON.parseObject(new String(jsonData), new TypeReference<List<GiftDto>>() {
                            });

                        }
                    }

                }));
    }*/


    /**
     * 显示礼物弹窗
     */
    protected void showGiftPop(String sId, int formType) {
        getCoin();
        int type = sessionType == SessionTypeEnum.P2P ? 1 : formType;
        if (null == giftPopWindow)
            giftPopWindow = new GiftPopWindow(getActivity(), 1, mTeamType == 0 ? 4 : 1, 0, (is_couple, userId) -> {
                if (is_couple == 1) {
                    ARouterUtils.toMineDetailActivity(userId);
                } else {
                    if (giftPopWindow != null && giftPopWindow.isShowing()) {
                        giftPopWindow.dismiss();
                    }
                    getCoupleCheck();
                }
            });
        giftPopWindow.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                giftPopWindow = null;
            }
        });
        giftPopWindow.setOnPopupWindowShowListener(() -> {
            if (groupCouplePopWindow != null && groupCouplePopWindow.isShowing()) {
                groupCouplePopWindow.dismiss();
            }

        });
        if (formType == 3) {  //群组点击头像和ait
            giftPopWindow.setUserIdType(sId, type, sessionId);
        } else {
            giftPopWindow.setUserIdType(sId, type, "");
        }
        if (mTeamType == 0) {
            visitParty(1, 2, sId);
        } else {
            visitParty(1, 1, sId);
        }
        giftPopWindow.setCoin(service.getUserInfo().getCoin());
        giftPopWindow.addOnClickListener(new GiftPopWindow.OnSelectListener() {
            @Override
            public void sendGift(GiftDto data, int num, List<String> toMember, String name) {
                isFirstSend = true;
                sendMessageGift(data, num, toMember, name);
            }

            @Override
            public void getMyCoin() {
                getCoin();
            }

            @Override
            public void atUser(List<String> userId, String name) {
                if (userId != null && userId.size() > 0) {
                    aitManager.insertAitMemberInner(userId.get(0), name, AitContactType.TEAM_MEMBER, inputPanel.getEditSelectionStart(), true);
                }
            }

            @Override
            public void upOrDownSeat(int userId, boolean isOnSeat) {
                mVoiceChat.kickOut(userId);
            }

            @Override
            public void muteVoice(int userId, int voiceStatus) {
                mVoiceChat.microphone(2, userId, voiceStatus != 2 ? VoiceRoomSeat.Status.ON : VoiceRoomSeat.Status.AUDIO_MUTED);
            }
        });

    }

    public void visitParty(int type, int from_type, String toUserId) {
        UserProviderService serviceUser = ARouter.getInstance().navigation(UserProviderService.class);
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("送礼弹窗页面曝光", "gift_send_popup_visit", "", JSON.toJSONString(new NavigationLogEntity(serviceUser.getUserId(), from_type, System.currentTimeMillis(), type, com.tftechsz.common.utils.CommonUtil.getOSName(), Constants.APP_NAME, toUserId, "0", 0)), null
                );
    }


    /**
     * 显示自己连击动画
     *
     * @param data
     * @param toMember
     */
    private void showGiftAnimationAgain(GiftDto data, List<String> toMember) {
        GiftDto bean = new GiftDto();
        bean.group = data.is_choose_num;
        bean.image = data.image;
        bean.headUrl = service.getUserId() + "";
        if (toMember != null && toMember.size() > 0) {
            bean.toUserName = UserInfoHelper.getUserDisplayName(toMember.get(0));
        }
        bean.userName = UserInfoHelper.getUserDisplayName(service.getUserId() + "");
        bean.id = data.id;
        showGiftAnimation(bean);
    }

    /**
     * 发送连击礼物
     */
    private void sendGiftAgain(GiftDto data, int num, List<String> toMember) {
        if (mActivityView == null || mDoubleGift == null) return;
        if (data.cate != 11 || isFirstSend) {
            mDoubleGift.setVisibility(View.VISIBLE);
            mActivityView.setVisibility(View.INVISIBLE);
            mDoubleGift.startAnimation(num, data.id);
            mDoubleGift.setGiftImage(data.image, data.id);
            showGiftAnimationAgain(data, toMember);
        }
        mDoubleGift.setAddCountDownListener(new DoubleHitGiftChatView.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                if (mActivityView == null || mDoubleGift == null) return;
                mDoubleGift.setVisibility(View.INVISIBLE);
                mActivityView.setVisibility(View.VISIBLE);
            }

            @Override
            public void sendAgain() {
                if (data.cate == 11) { //如果是背包礼物
                    isFirstSend = false;
                    if (!ClickUtil.canOperate()) return;
                    mCompositeDisposable.add(RetrofitManager.getInstance()
                            .createUserApi(PublicService.class)
                            .packGiftNum(data.id)
                            .compose(BasePresenter.applySchedulers())
                            .subscribeWith(new ResponseObserver<BaseResponse<GiftDto>>() {
                                @Override
                                public void onSuccess(BaseResponse<GiftDto> response) {
                                    if (response != null && response.getData() != null) {
                                        mBagNumber = response.getData().num;
                                        if (mBagNumber == 0) {
                                            Utils.toast(getString(R.string.not_enough_backpacks));
                                            return;
                                        }
                                        if (mActivityView == null || mDoubleGift == null) return;
                                        mDoubleGift.setVisibility(View.VISIBLE);
                                        mActivityView.setVisibility(View.INVISIBLE);
                                        mDoubleGift.startAnimation(num, data.id);
                                        mDoubleGift.setGiftImage(data.image, data.id);
                                        showGiftAnimationAgain(data, toMember);
                                        sendFamilyGiftMessage(data, num, toMember);
                                    }

                                }
                            }));
                    return;
                }
                sendFamilyGiftMessage(data, num, toMember);
            }
        });
    }

    private int mBagNumber = 1;
    private boolean isFirstSend = true;


    /**
     * 发送群聊礼物
     */
    private void sendFamilyGiftMessage(GiftDto data, int num, List<String> toMember) {
        String teamId = NimUIKit.getTeamProvider().getTeamById(sessionId).getId();
        ChatMsgUtil.sendGiftMessage(sessionType, String.valueOf(service.getUserId()), teamId, data.is_choose_num, data.cate, data.tag_value, data.id, data.coin, data.name, data.image, data.animationType, data.animation, data.animations, data.combo, "", num, mTeamType == 1 ? "room" : "family", teamId, toMember, (code, message) -> {  //执行成功发送礼物
            if (code == 20012 || code == 20013 || code == 20014 || code == 20015 || code == 20016) {
                NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                messageListPanel.removeMessageItem(message.getUuid());
            } else {
                //可以连击礼物且数量为1
                if (data.combo == 1 && num == 1 && code == 20017) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                    if (data.cate == 11 && isFirstSend)
                        mBagNumber = data.number;
                    sendGiftAgain(data, num, toMember);
                    if (giftPopWindow != null)
                        giftPopWindow.dismiss();
                } else {
                    if (mDoubleGift != null) {
                        mDoubleGift.resetNumber();
                        mDoubleGift.setVisibility(View.INVISIBLE);
                    }
                    mActivityView.setVisibility(View.VISIBLE);
                    messageListPanel.onMsgSend(message);
                }
            }
        });
    }


    private void sendMessageGift(GiftDto data, int num, List<String> toMember, String name) {
        if (sessionType == SessionTypeEnum.Team) {  //群组
            sendFamilyGiftMessage(data, num, toMember);
        } else {   //个人
            ChatMsgUtil.sendGiftMessage(sessionType, String.valueOf(service.getUserId()), sessionId, data.is_choose_num, data.cate, data.tag_value, data.id, data.coin, data.name, data.image, data.animationType, data.animation, data.animations, data.combo, "", num, "p2p", "", null, (code, message) -> {
//                if (code == 200) {
//                    sendGiftSuccess(data, message, toMember);
//                }
                if (code == 20012 || code == 20013 || code == 20014 || code == 20015 || code == 20016) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                    messageListPanel.removeMessageItem(message.getUuid());
                } else {
                    messageListPanel.onMsgSend(message);
                    sendFailWithBlackList(code, message);
                }
            });
        }
    }

    /**
     * 发送礼物成功
     */
    private void sendGiftSuccess(GiftDto data, IMMessage message, List<String> toMember) {
        try {
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
            if (chatMsg != null && TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
                showGift(message, chatMsg, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (giftPopWindow != null) {
            giftPopWindow.sendGiftSuccess(data);
        }
    }


    protected void startThread() {
        if (null == safeHandle) {
            safeHandle = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == EVENT_MESSAGE) {
                        try {
                            playGift();
                            showFloatGift();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        safeHandle.sendEmptyMessageDelayed(EVENT_MESSAGE, 1000);
                    }
                }
            };
        }
        if (safeHandle.hasMessages(EVENT_MESSAGE)) return;
        safeHandle.sendEmptyMessage(EVENT_MESSAGE);
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

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTvFloatContent.getLayoutParams();
        //贵族升级飘萍
        if (getActivity() == null) return;
        if (TextUtils.equals(ChatMsg.FAMILY_USER_NOBILITY_LEVEL_UP_NOTICE, data.cmd_type)) {
            mIvCriticalLeft.setVisibility(View.GONE);
            mTvFloatContent.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            mLlFloat.setBackgroundResource(R.drawable.bg_nobility_level);
            params.leftMargin = DensityUtils.dp2px(getActivity(), 34);
            mTvFloatContent.setLayoutParams(params);
            mTvFloatContent.setBackgroundResource(0);
            mTvFloatContent.setPadding(0, 0, 0, 0);
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            String url = partyMsg.badge;
            spannableString.append("[icon]   ");
            spannableString.append(partyMsg.msg);
            int headerStart = spannableString.toString().indexOf("[icon]");
            CircleUrlImageSpan headerSpan = new CircleUrlImageSpan(getActivity(), url, mTvFloatContent, ConvertUtils.dp2px(18), ConvertUtils.dp2px(18));
            spannableString.setSpan(headerSpan, headerStart, headerStart + 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            int start = spannableString.toString().indexOf(partyMsg.nickname);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FEE903")), start, start + partyMsg.nickname.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            int start1 = spannableString.toString().indexOf(partyMsg.gradename);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FEE903")), start1, start1 + partyMsg.gradename.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mTvFloatContent.setText(spannableString);

        } else {  //幸运礼物中奖礼物飘萍
            mIvCriticalLeft.setVisibility(View.VISIBLE);
            mLlFloat.setBackgroundResource(0);
            params.leftMargin = DensityUtils.dp2px(getActivity(), 5);
            mTvFloatContent.setPadding(DensityUtils.dp2px(getActivity(), 50), 0, 0, 0);
            mTvFloatContent.setLayoutParams(params);
            mTvFloatContent.setBackgroundResource(R.drawable.bg_critical_hit_content);
            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            spannableString.append(partyMsg.msg);
            int start1 = spannableString.toString().indexOf(partyMsg.nums);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F8D029")), start1, start1 + partyMsg.nums.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mTvFloatContent.setText(spannableString);
        }
        mLlFloat.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.float_screen_in_right);
        Animation animationOut = AnimationUtils.loadAnimation(getActivity(), R.anim.float_screen_out_left);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLlFloat.setVisibility(View.GONE);
                mIsPlayEnd = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mLlFloat.startAnimation(animation);
        if (null == mFloatBack)
            mFloatBack = new CountBackUtils();
        mFloatBack.countBack(5, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                if (time == 1) {
                    mLlFloat.startAnimation(animationOut);
                }
            }

            @Override
            public void finish() {

            }
        });
    }


    private SVGAParser.ParseCompletion mParseCompletionCallback;

    private void playGift() {
        if (svgaImageView == null) return;
        if (svgaImageView.isAnimating() || mIsDown || myGiftList.isEmpty()) return;
        GiftDto data = myGiftList.peek();
        if (data == null) {
            return;
        }
        File file = new File(DownloadHelper.FILE_PATH + File.separator + data.name);
        Utils.logE(data.name + " -- " + data.animation);
        myGiftList.poll();
        if (file.exists()) {
            playAnimation(file, data);
        } else {
            mIsDown = true;
            DownloadHelper.downloadGift(data.animation, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    mIsDown = false;
                    playAnimation(file, data);
                }

                @Override
                public void failed() {
                    mIsDown = false;
                }

                @Override
                public void onProgress(int progress) {
                }
            });
        }
    }

    private void playAnimation(File file, GiftDto data) {
        mIsPlay = true;
        if (file.getAbsolutePath().endsWith(".mp4")) {
            mPlayerView.setVisibility(View.VISIBLE);
            mPlayerView.startPlay(file);
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mIsPlay = false;
        }

    }

    private void playAirdrop(String name) {
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (TextUtils.equals(COUPLE_OFFICIAL_PUBLICITY, name)) {   //情侣表白svga
                    if (svgaImageView != null && mIvGiftMask != null) {
                        if (mTvContactWay.isPauseScroll()) {
                            mIvGiftMask.setVisibility(View.VISIBLE);
                        }
                        svgaImageView.setVideoItem(videoItem);
                        svgaImageView.stepToFrame(0, true);
                    }
                    Utils.runOnUiThreadDelayed(() -> {
                        if (mClGroupCouple != null)
                            mClGroupCouple.setVisibility(View.VISIBLE);
                    }, 1500);
                }
            }
        }, null);
        if (!TextUtils.equals(COUPLE_OFFICIAL_PUBLICITY, name)) return;
        svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                mIsPlay = false;
                mIvGiftMask.setVisibility(View.GONE);
                if (mClGroupCouple != null)
                    mClGroupCouple.setVisibility(View.GONE);
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
    }


    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected boolean isAllowSendMessage(final IMMessage message) {
        if (customization != null) {
            return customization.isAllowSendMessage(message);
        }
        return false;
    }


    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
        service.observeReceiveMessage(incomingMessageObserver, register);
        // 已读回执监听
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            service.observeMessageReceipt(messageReceiptObserver, register);
        }
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);

        if (!register) {
            commandObserver = null;
            incomingMessageObserver = null;
            messageReceiptObserver = null;
            userInfoObserver = null;
        }
    }


    private void initReceiverObserver() {
        /**
         * 消息接收观察者
         */
        incomingMessageObserver = new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> messages) {
                onMessageIncoming(messages);
            }
        };

        /**
         * 已读回执观察者
         */
        messageReceiptObserver = new Observer<List<MessageReceipt>>() {
            @Override
            public void onEvent(List<MessageReceipt> messageReceipts) {
                messageListPanel.receiveReceipt();
            }
        };


        /**
         * 命令消息接收观察者
         */
        commandObserver = (Observer<CustomNotification>) message -> {
            if (message != null)
                showIntimacy(null, message.getContent());
            if (message != null && !TextUtils.isEmpty(message.getContent())) {  //此处没有返回消息对应用户
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getContent());
                if (null == chatMsg) {
                    return;
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_MARQUEE) && sessionType == SessionTypeEnum.P2P) {
                    ChatMsg.AirdropOpen intimacy = JSON.parseObject(chatMsg.content, ChatMsg.AirdropOpen.class);
                    if (intimacy != null && TextUtils.equals(sessionId, intimacy.to_user_id + "")) {
                        int time = 120;
                        IntimacyEntity entity;
                        if (mIntimacyEntity != null) {
                            entity = mIntimacyEntity;
                        } else {
                            entity = new IntimacyEntity();
                            entity.setUserId(intimacy.to_user_id + "");
                            entity.setSelfId(service.getUserId() + "");
                        }
                        entity.setEndTime(time);
                        mCompositeDisposable.add(DbManager.getInstance().insert(entity).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) {
                                mIntimacyEntity = entity;
                            }
                        }));
                        setContactTip(time);
                    }
                }
            }
            if (message != null && (sessionType == SessionTypeEnum.P2P && !sessionId.equals(message.getSessionId()))) {
                return;
            }
            if (message != null && !TextUtils.isEmpty(message.getContent())) {
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getContent());
                if (null == chatMsg) {
                    return;
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.WINDOW_TIPS)) {   //底部弹窗
                    if (TextUtils.equals(chatMsg.cmd, ChatMsg.MSG_WINDOW_BOTTOM)) {//会员弹窗
                        mRlOpenVip.setVisibility(View.VISIBLE);
                        mRlOpenVip.setBackgroundResource(R.drawable.bg_open_vip);
                        mAnimationVip.setVisibility(View.VISIBLE);
                        mAnimationWarn.setVisibility(View.GONE);
                        ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
                        SpannableStringBuilder span = ChatMsgUtil.getTipContent(alert.des, "#4A4E4E", content -> {
                            String peony = "peony://";
                            if (content.contains(peony)) {   //打开原生页面
                                String curContent = content.substring(peony.length() + 1, content.length() - 1);
                                if (curContent.contains(Constants.GOTO_VIP)) {
                                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_VIP);
                                }
                            }
                        });
                        mTvOpenVip.setText(span);
                        mTvOpenVip.setMovementMethod(LinkMovementMethod.getInstance());
                        Utils.runOnUiThreadDelayed(() -> mRlOpenVip.setVisibility(View.GONE), 5000);
                    } else if (TextUtils.equals(chatMsg.cmd, ChatMsg.MSG_WINDOW_BOTTOM_WARN)) { //违规消息弹窗
                        Utils.logE(chatMsg.content);
                        mRlOpenVip.setVisibility(View.VISIBLE);
                        mRlOpenVip.setBackgroundResource(R.drawable.bg_chat_bot);
                        mAnimationVip.setVisibility(View.GONE);
                        mAnimationWarn.setVisibility(View.VISIBLE);
                        ChatTipsContent alert = JSON.parseObject(chatMsg.content, ChatTipsContent.class);

                        NIMClient.getService(MsgService.class).queryMessageListByUuid(Collections.singletonList(alert.msg_id))
                                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                                    @Override
                                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                                        if (result != null && result.size() > 0) {
                                            IMMessage message1 = result.get(0);
                                            message1.setStatus(MsgStatusEnum.fail);
                                            NIMClient.getService(MsgService.class).updateIMMessageStatus(message1);
                                            messageListPanel.replaceItem(alert.msg_id, message1);
                                        }
                                    }
                                });

                        String[] split = alert.msg.split("&");
                        SpannableStringBuilder ssb = new SpannableStringUtils.Builder()
                                .append(split[0])
                                .setFontSize(14, true)
                                .setForegroundColor(Utils.getColor(R.color.red))
                                .append(split.length > 1 ? "\n" : "")
                                .append(split.length > 1 ? split[1] : "")
                                .setFontSize(12, true)
                                .setForegroundColor(Utils.getColor(R.color.red))
                                .create();
                        mTvOpenVip.setText(ssb);
                        Utils.runOnUiThreadDelayed(() -> mRlOpenVip.setVisibility(View.GONE), 5000);
                    } else if (TextUtils.equals(chatMsg.cmd, ChatMsg.MSG_WARN)) {   //消息添加感叹号
                        ChatTipsContent alert = JSON.parseObject(chatMsg.content, ChatTipsContent.class);
                        NIMClient.getService(MsgService.class).queryMessageListByUuid(Collections.singletonList(alert.msg_id))
                                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                                    @Override
                                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                                        if (result != null && result.size() > 0) {
                                            IMMessage message1 = result.get(0);
                                            message1.setStatus(MsgStatusEnum.fail);
                                            NIMClient.getService(MsgService.class).updateIMMessageStatus(message1);
                                            messageListPanel.replaceItem(alert.msg_id, message1);
                                        }
                                    }
                                });
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.LUCKY_GIFT)) {
                    ChatMsg.LuckyGift luckyGift = JSON.parseObject(chatMsg.content, ChatMsg.LuckyGift.class);
                    if (luckyGift != null && !TextUtils.isEmpty(luckyGift.msg)) {
//                        LogUtil.e("url", luckyGift.msg + "  =====luckyGift======  " + luckyGift.desc);
                        mMapAward.add(luckyGift);
                        startAnimation();
                        updateAwardIMMessage(luckyGift.msg_id, luckyGift.msg, luckyGift.desc);
                        if (service.getUserId() == luckyGift.user_id) { //如果是自己
                            if (giftPopWindow != null && giftPopWindow.isShowing() && giftPopWindow.isPackMode()) { //如果是背包模式则刷新当前列表
                                giftPopWindow.refreshData();
                            }
                        }
                    }
                }
                showCommandMessage(message);
                commandObserverMessage(chatMsg);
            }
        };

        /**
         * 用户信息变更观察者
         */
        userInfoObserver = accounts -> {
            if (!accounts.contains(sessionId)) {
                return;
            }
            if (mIvLeft == null) {
                return;
            }
            initData();
        };
    }

    /**
     * 动画循环展示奖励
     */
    private void startAnimation() {
        if (isLoadingReward) {
            return;
        }
        if (mLinRootAward == null) {
            return;
        }
        isLoadingReward = true;
        if (mMapAward.size() > 0) {
            mLinRootAward.setVisibility(View.VISIBLE);
            tvAwardText1.setText(mMapAward.get(0).msg);
            mMapAward.remove(0);

            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_left_in);
            hyperspaceJumpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (animationBack == null)
                        animationBack = new CountBackUtils();
                    animationBack.countBack(2, new CountBackUtils.Callback() {
                        @Override
                        public void countBacking(long time) {
                        }

                        @Override
                        public void finish() {
                            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_left_out);
                            hyperspaceJumpAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if (mLinRootAward == null) {
                                        return;
                                    }
                                    isLoadingReward = false;
                                    mLinRootAward.setVisibility(View.GONE);
                                    if (mMapAward.size() > 0) {
                                        startAnimation();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            mLinRootAward.startAnimation(hyperspaceJumpAnimation);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLinRootAward.startAnimation(hyperspaceJumpAnimation);
        }
    }

    protected void commandObserverMessage(ChatMsg chatMsg) {

    }


    /**
     * 100度提示
     *
     * @param time
     */
    private void setContactTip(int time) {
        String text1 = "恭喜您，您的亲密度已达到100°，成功解锁发送联系方式，外部联系方式存在不确定性，请谨慎添加，以防上当受骗！";
        String text = "";
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.content != null) {
            text = service.getConfigInfo().sys.content.intimacy_marquee;
        }
        mTvContactWay.setText(TextUtils.isEmpty(text) ? text1 : text);
        mTvContactWay.setVisibility(View.VISIBLE);
        mTvContactWay.setBackgroundColor(Utils.getColor(R.color.white));
        mTvContactWay.setPauseScroll(false);
        if (contactBack == null)
            contactBack = new CountBackUtils();
        contactBack.countBack(time, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                intimacyEndTime = (int) time;
            }

            @Override
            public void finish() {
                intimacyEndTime = 0;
                mTvContactWay.setVisibility(View.GONE);
                mTvContactWay.setPauseScroll(true);
                contactBack.destroy();
            }
        });
    }

    /**
     * 红包雨
     */
    public void rain(RedPacketViewHelper redPacketViewHelper, ChatMsg.Rain rain) {
        redPacketViewHelper.endGiftRain();
        if (getActivity() != null && isAdded()) {
            getActivity().getWindow().getDecorView().postDelayed(() -> {
                List<BoxInfo> boxInfoList = new ArrayList<>();
                for (int i = 0; i < rain.x; i++) {
                    BoxInfo boxInfo = new BoxInfo();
                    boxInfo.setAwardId(i);
                    boxInfoList.add(boxInfo);
                }
                redPacketViewHelper.launchGiftRainRocket(0, boxInfoList, new RedPacketViewHelper.GiftRainListener() {
                    @Override
                    public void startLaunch() {

                    }

                    @Override
                    public void openRedPacket(int id) {
                        clickRain(rain.id, id);
                    }

                    @Override
                    public void startRain() {

                    }

                    @Override
                    public void endRain() {
                        mIvGiftMask.setVisibility(View.GONE);
                        rainEnd(rain.id);
                    }
                });
            }, 500);
        }
    }


    private void onMessageIncoming(List<IMMessage> messages) {
        if (CommonUtil.isEmpty(messages)) {
            return;
        }
        messageListPanel.onIncomingMessage(messages);
        // 发送已读回执
        messageListPanel.sendReceipt();
        //收到的消息是礼物的时候
        try {
            for (IMMessage message : messages) {
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                if (!TextUtils.equals(message.getFromAccount(), String.valueOf(service.getUserId()))) {  //不是自己发送的礼物
                    if (chatMsg != null && TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {  //礼物
                        if (sessionType == SessionTypeEnum.Team && TextUtils.equals("family", chatMsg.cmd) && mTeamType == 0) {
                            showGift(message, chatMsg, 1);
                        } else if (sessionType == SessionTypeEnum.Team && TextUtils.equals("room", chatMsg.cmd) && mTeamType == 1) {
                            showGift(message, chatMsg, 1);
                        } else if (sessionType == SessionTypeEnum.P2P && TextUtils.equals("p2p", chatMsg.cmd)) {
                            if (TextUtils.equals(chatMsg.from, sessionId)) {
                                showGift(message, chatMsg, 1);
                            }
                        }
                    } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_NOTICE, chatMsg.cmd_type)) {  //公告
                        ChatMsg.ApplyMessage applyMessage = JSON.parseObject(chatMsg.content, ChatMsg.ApplyMessage.class);
                        if (applyMessage != null) {
                            MMKVUtils.getInstance().encode(service.getUserId() + Constants.FAMILY_ANNOUNCEMENT, applyMessage.message);
                        }
                        showAnnouncement();
                    }
                }
                if (chatMsg != null && TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_GIFT_BAG_IM)) {  //空投
                    if (sessionType == SessionTypeEnum.Team && mTeamType == 0) {
                        ChatMsg.Airdrop airdrop = JSON.parseObject(chatMsg.content, ChatMsg.Airdrop.class);
                        GiftDto bean = new GiftDto();
                        bean.group = airdrop.num;
                        bean.name = airdrop.gift_bag_name;
                        bean.animation = airdrop.animation;
                        bean.userName = message.getFromNick();
                        myGiftList.offer(bean);
                    }
                }
                if (TextUtils.equals(message.getFromAccount(), sessionId) && message.getRemoteExtension() != null) { //亲密度
                    Map<String, Object> extension = message.getRemoteExtension();
                    if (extension != null && extension.get("self_attach") != null) {
                        showIntimacyAttr((String) extension.get("self_attach"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示礼物动画
     *
     * @param type 发送礼物 0 : 发  1 ： 收
     */
    private void showGift(IMMessage message, ChatMsg chatMsg, int type) {
        //逻辑处理: 如果是自己发出的礼物 && 盲盒礼物 , 使用callbackExt对象添加 animations
        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
        ChatMsg cm = ChatMsgUtil.parseMessage(message.getCallbackExtension());
        if (cm != null) {
            ChatMsg.Gift gift1 = JSON.parseObject(cm.content, ChatMsg.Gift.class);
            if (gift.gift_info.tag_value == 8 || gift.gift_info.tag_value == 9) { //如果是盲盒礼物 就重新赋值对象
                ChatMsg.AccostGift giftInfo = new ChatMsg.AccostGift();
                giftInfo.animation = gift1.animation;
                giftInfo.animations = gift1.animations;
                gift.ext = gift1.ext;
                gift.gift_info = giftInfo;
            }
        }

        if (gift != null && gift.gift_info != null) {
            if (gift.gift_info.animations != null && !gift.gift_info.animations.isEmpty()) {
                for (String ani : gift.gift_info.animations) {
                    addOffer(message, type, gift, ani);
                }
            } else {
                addOffer(message, type, gift, gift.gift_info.animation);
            }
        }
    }

    private void addOffer(IMMessage message, int type, ChatMsg.Gift gift, String ani) {
        if (TextUtils.equals(AppManager.getAppManager().currentActivity().getClass().getSimpleName(), VideoCallActivity.class.getSimpleName())) {
            return;
        }
        GiftDto bean = new GiftDto();
        bean.group = gift.gift_num;
        bean.name = Utils.getFileName(ani).replace(".svga", "");
        bean.image = gift.gift_info.image;
        bean.animation = ani;
        if (type == 0) {
            bean.headUrl = service.getUserId() + "";
        } else {
            bean.headUrl = message.getFromAccount();
        }
        List<String> to = gift.to;  //发送给哪个用户
        if (to != null && to.size() > 0) {
            bean.toUserName = UserInfoHelper.getUserDisplayName(to.get(0));
        }
        bean.userName = message.getFromNick();
        if (gift.gift_info.animationType == 1) {    //动效类型:1.普通PNG 2.炫 3.动
            if (sessionType == SessionTypeEnum.Team) {  //群组
                if (gift.is_append == 1 && (!TextUtils.equals(service.getUserId() + "", message.getFromAccount()) || bean.group != 1))  //因为连击添加了通知   不是自己和发送的礼物数量不是1
                    showGiftAnimation(bean);
            } else {
                lottie(bean, type);
            }
        } else {
            myGiftList.offer(bean);
            if (giftPopWindow != null && type == 0)
                giftPopWindow.dismiss();
        }
    }


    private void lottie(GiftDto data, int type) {
        String[] json = data.animation.split(",");
        lottieAnimationView.setImageAssetsFolder(Constants.ACCOST_GIFT);
        if (json.length >= 2) {
            lottieAnimationView.setAnimationFromUrl(json[type]);
            lottieAnimationView.setFailureListener(Throwable::printStackTrace);
            lottieAnimationView.playAnimation();
            lottieAnimationView.setVisibility(View.VISIBLE);

        }
    }


    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {
        if (isAllowSendMessage(message)) {
            appendTeamMemberPush(message);
            message = changeToRobotMsg(message);
            appendPushConfigAndSend(message);
        } else {
            // 替换成tip
            message = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
            message.setContent("该消息无法发送");
            message.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).saveMessageToLocal(message, false);
        }
        uploadCheat();
        messageListPanel.onMsgSend(message);
        if (aitManager != null) {
            aitManager.reset();
        }
        return true;
    }


    private void uploadCheat() {
        singleThreadExecutor.execute(() -> {
            com.tftechsz.common.utils.CommonUtil.getToken(() -> mCompositeDisposable.add(publicService.uploadCheat(sessionType == SessionTypeEnum.P2P ? "p2p_chat" : "family_chat", "").compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                        @Override
                        public void onSuccess(BaseResponse<Boolean> response) {
                        }
                    })));
        });
    }


    private void appendPushConfigAndSend(IMMessage message) {
        final IMMessage msg = message;
        appendPushConfig(message);
        MsgService service = NIMClient.getService(MsgService.class);
        // send message to server and save to db
        IMMessage replyMsg = null;
        if (inputPanel != null) {
            replyMsg = inputPanel.getReplyMessage();
        }
        if (replyMsg == null) {
            service.sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, msg);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        } else {
            service.replyMessage(message, replyMsg, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    String threadId = message.getThreadOption().getThreadMsgIdClient();
                    messageListPanel.refreshMessageItem(threadId);
                }

                @Override
                public void onFailed(int code) {
                    sendFailWithBlackList(code, msg);
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
        if (inputPanel != null)
            inputPanel.resetReplyMessage();
    }

    // 被对方拉入黑名单后，发消息失败的交互处理
    private void sendFailWithBlackList(int code, IMMessage msg) {
        if (code == ResponseCode.RES_IN_BLACK_LIST || code == 20010) {
            // 如果被对方拉入黑名单，发送的消息前不显示重发红点
//            msg.setStatus(MsgStatusEnum.success);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(msg);
            messageListPanel.refreshMessageList();
            // 同时，本地插入被对方拒收的tip消息
            IMMessage tip = MessageBuilder.createTipMessage(msg.getSessionId(), msg.getSessionType());
            if (getActivity() != null)
                tip.setContent(getActivity().getString(R.string.black_list_send_tip));
            tip.setStatus(MsgStatusEnum.success);
            CustomMessageConfig config = new CustomMessageConfig();
            config.enableUnreadCount = false;
            tip.setConfig(config);
            NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
        } else if (sessionType == SessionTypeEnum.Team && (code == 812 || code == ResponseCode.RES_TEAM_ENACCESS)) {
            getTips();
        }
    }

    private void appendTeamMemberPush(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (sessionType == SessionTypeEnum.Team) {
            List<String> pushList = aitManager.getAitTeamMember();
            if (pushList == null || pushList.isEmpty()) {
                return;
            }
            MemberPushOption memberPushOption = new MemberPushOption();
            memberPushOption.setForcePush(true);
            memberPushOption.setForcePushContent(message.getContent());
            if (pushList.toString().contains("-9999")) {
                memberPushOption.setForcePushList(null);
            } else {
                memberPushOption.setForcePushList(pushList);
            }
            message.setMemberPushOption(memberPushOption);
            Map<String, Object> map = new HashMap<>();
            map.put("ait", 1);
            map.put("member", pushList);
            message.setRemoteExtension(map);
        }
    }

    private IMMessage changeToRobotMsg(IMMessage message) {
        if (aitManager == null) {
            return message;
        }
        if (message.getMsgType() == MsgTypeEnum.robot) {
            return message;
        }
        if (isChatWithRobot()) {
            if (message.getMsgType() == MsgTypeEnum.text && message.getContent() != null) {
                String content = message.getContent().equals("") ? " " : message.getContent();
                message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), message.getSessionId(), content, RobotMsgType.TEXT, content, null, null);
            }
        } else {
            String robotAccount = aitManager.getAitRobot();
            if (TextUtils.isEmpty(robotAccount)) {
                return message;
            }
            String text = message.getContent();
            String content = aitManager.removeRobotAitString(text, robotAccount);
            content = content.equals("") ? " " : content;
            message = MessageBuilder.createRobotMessage(message.getSessionId(), message.getSessionType(), robotAccount, text, RobotMsgType.TEXT, content, null, null);
        }
        return message;
    }

    private boolean isChatWithRobot() {
        return NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(sessionId) != null;
    }

    private void appendPushConfig(IMMessage message) {
        CustomPushContentProvider customConfig = NimUIKitImpl.getCustomPushContentProvider();
        if (customConfig == null) {
            return;
        }
        String content = customConfig.getPushContent(message);
        Map<String, Object> payload = customConfig.getPushPayload(message);
        if (!TextUtils.isEmpty(content)) {
            message.setPushContent(content);
        }
        if (payload != null) {
            payload.put("channel_id", "high_system");
            message.setPushPayload(payload);
        }

    }

    @Override
    public void onInputPanelExpand() {
        if (inputPanel != null && inputPanel.getMessageEdit() != null) {
            inputPanel.getMessageEdit().post(() -> messageListPanel.scrollToBottom());
        }
//        messageListPanel.scrollToBottom();

    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    @Override
    public void onItemFooterClick(IMMessage message) {
        if (aitManager == null) {
            return;
        }
        if (messageListPanel.isSessionMode()) {
            RobotAttachment attachment = (RobotAttachment) message.getAttachment();
            NimRobotInfo robot = NimUIKitImpl.getRobotInfoProvider().getRobotByAccount(attachment.getFromRobotAccount());
            aitManager.insertAitRobot(robot.getAccount(), robot.getName(), inputPanel.getEditSelectionStart());
        }
    }

    @Override
    public void onReplyMessage(IMMessage message) {
        inputPanel.setReplyMessage(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (aitManager != null) {
            aitManager.onActivityResult(requestCode, resultCode, data);
        }
        inputPanel.onActivityResult(requestCode, resultCode, data);
        int GROUP_COUPLE_MESSAGE = 10001;
        if (requestCode == GROUP_COUPLE_MESSAGE && RESULT_OK == resultCode) {
            int userId = data.getIntExtra("userId", 0);
            getGroupCouple(String.valueOf(userId));
        }
        messageListPanel.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        actions.add(new VideoAction());
        actions.add(new LocationAction());
        if (customization != null && customization.actions != null) {
            actions.addAll(customization.actions);
        }
        return actions;
    }

    /**
     * 移出黑名单
     * @param userId
     */
    public void cancelBlack(int userId) {
        mCompositeDisposable.add(mineApiService.cancelBlack(userId).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                ToastHelper.showToast(getActivity(), "解除拉黑成功");
            }
        }));
    }


    public void showBlackPop(Context context, int userId) {
        CustomPopWindow pop = new CustomPopWindow(context);
        pop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                blackUser(userId);
            }
        });
        pop.setTitle("确定要拉黑TA吗");
        pop.setContent("拉黑后你将收不到对方的消息和呼叫，且在好友列表互相看不到对方");
        pop.showPopupWindow();
    }


    /**
     * 拉黑用户
     */
    public void blackUser(int userId) {
        attentionService.blackUser(userId, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                ToastHelper.showToast(getActivity(), "拉黑成功");
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (!ClickUtil.canOperate()) return;
        if (id == R.id.toolbar_iv_menu) {
//            Intent intent = new Intent(getActivity(), ChatSettingActivity.class);
//            intent.putExtra("sessionId", sessionId);
//            intent.putExtra("sessionType", sessionType);
//            startActivity(intent);
            MineDetailMorePopWindow popWindow = new MineDetailMorePopWindow(getActivity(), sessionId);
            popWindow.addOnClickListener(new MineDetailMorePopWindow.OnSelectListener() {
                @Override
                public void reportUser() {
                    ARouterUtils.toBeforeReportActivity(Integer.parseInt(sessionId), 1);
                }

                @Override
                public void blackUser(boolean isBlack) {
                    if (isBlack) {
                        cancelBlack(Integer.parseInt(sessionId));
                    } else
                        showBlackPop(getActivity(), Integer.parseInt(sessionId));
                }
            });
            popWindow.showPopupWindow(mIvtoolbarmenu);
        } else if (id == R.id.toolbar_back_all) {  //返回
            if (getActivity() != null)
                getActivity().finish();
            KeyboardUtils.close(getActivity());
        } else if (id == R.id.rl_intimacy) {   //亲密度
            chatMessagePopWindow = new ChatMessagePopWindow(getActivity(), sessionId);
            chatMessagePopWindow.setHeightWindow(mHeight);
            chatMessagePopWindow.showPopupWindow();
            chatMessagePopWindow.getIntimacy();
        } else if (id == R.id.iv_left) {   // 头像点击
            ARouterUtils.toMineDetailActivity(sessionId);//对方
        } else if (id == R.id.iv_right) {
            ARouterUtils.toMineDetailActivity("");//自己
        } else if (id == R.id.toolbar_team_iv_menu) {  //点击家族更多
            if (mTeamType == 0) {
                getFamilyId(true, false);
            } else {
                ARouterUtils.toRoomMember(getActivity(), sessionId, 0);  //点击更多进入
            }
        } else if (id == R.id.ll_notice) {   //审核申请
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FAMILY_APPLY);
        } else if (id == R.id.tv_content) {   //拨打视频
            initPermissions();
        } else if (id == R.id.iv_close_call) {  //关闭
            mRlIntimacyCall.setVisibility(View.GONE);
            if (mIntimacyEntity != null) {
                mIntimacyEntity.setIsShow(0);
                mCompositeDisposable.add(DbManager.getInstance().update(mIntimacyEntity).subscribe(l -> {
                }));
            }
        } else if (id == R.id.ll_announcement) {  //点击公告
            String desc = MMKVUtils.getInstance().decodeString(service.getUserId() + Constants.FAMILY_ANNOUNCEMENT);
            ARouterUtils.toEditFamily(desc, 0);
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.FAMILY_ANNOUNCEMENT);
        } else if (id == R.id.iv_sign_in) {  //签到
            signIn();
        } else if (id == R.id.fl_ait) {  //ait消息get
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FAMILY_AIT);
        } else if (id == R.id.ll_family_box) {
            if (mFamilyBoxPop == null) {
                mFamilyBoxPop = new FamilyBoxPop(getContext());
            }
            mFamilyBoxPop.setData(mFamilyBox);
            mFamilyBoxPop.showPopupWindow();
        } else if (R.id.tv_gone_close == id) {
            setAttention(true);
        } else if (R.id.tv_btn_attention == id) {
            //关注用户
            attentionService.attentionUser(Integer.parseInt(sessionId), new ResponseObserver<BaseResponse<Boolean>>() {
                @Override
                public void onSuccess(BaseResponse<Boolean> response) {
                    if (getView() == null) return;
                    setAttention(response.getData());
                }
            });

        }
    }


    private void initPermissions() {
        if (getActivity() != null) {
            mCompositeDisposable.add(new RxPermissions(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            checkCallMsg(1);
                        } else {
                            PermissionUtil.showPermissionPop(getActivity());
                        }
                    }));
        }
    }


    protected void onPicked(File file) {
        IMMessage message = MessageBuilder.createImageMessage(sessionId, sessionType, file, file.getName());
        if (sessionType == SessionTypeEnum.P2P) {
            int curLevel;
            if (TextUtils.isEmpty(mIntimacy))
                curLevel = 0;
            else {
                float level = Float.parseFloat(mIntimacy.replace("℃", ""));
                if (level <= 5) {
                    curLevel = 1;
                } else if (level > 5 && level < 100) {
                    curLevel = 2;
                } else {
                    curLevel = 3;
                }
            }
            String spamExt = "{\"level\":\"" + curLevel + "\",\"gender\":\"" + (service.getUserInfo() == null ? 0 : service.getUserInfo().getSex()) + "\"}";
            message.setYidunAntiSpamExt(spamExt);
        }

        sendMessage(message);
    }


    @Override
    public void onEditClick() {

    }

    private CustomPopWindow popWindow;


    public boolean showRecordTip(PartyService partyService) {
        boolean isShow = false;
        boolean isOnSeat = MMKVUtils.getInstance().decodeBoolean(Constants.PARTY_IS_ON_SEAT);
        if ((partyService.isRunFloatService() || partyService.isRunActivity()) && isOnSeat) {
            if (popWindow == null)
                popWindow = new CustomPopWindow(BaseApplication.getInstance());
            popWindow.setContent("在麦位上，需要下麦后，才能进行语音");
            popWindow.setRightButton("我知道了");
            popWindow.setRightGone();
            popWindow.showPopupWindow();
            isShow = true;
        }
        return isShow;
    }


    @Override
    public void onAudioRecord(View v, MotionEvent event) {
        if (showRecordTip(partyService)) {
            return;
        }
        if (getActivity() != null && isAdded() && !isDestroyed())
            mCompositeDisposable.add(new RxPermissions(this)
                    .request(Manifest.permission.RECORD_AUDIO
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                inputPanel.touched = true;
                                inputPanel.initAudioRecord();
                                inputPanel.onStartAudioRecord();
                            } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                                    || event.getAction() == MotionEvent.ACTION_UP) {
                                inputPanel.touched = false;
                                inputPanel.onEndAudioRecord(InputPanel.isCancelled(v, event));
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                inputPanel.touched = true;
                                inputPanel.cancelAudioRecord(InputPanel.isCancelled(v, event));
                            }
                        } else {
                            if (perPop == null)
                                perPop = new CustomPopWindow(getActivity());
                            perPop.setTitle("权限设置");
                            perPop.setContent(getString(R.string.chat_open_voice_permission));
                            perPop.setLeftButton("知道了");
                            perPop.setRightButton("去设置");
                            perPop.setOutSideDismiss(false);
                            perPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onSure() {
                                    PermissionUtil.gotoPermission(BaseApplication.getInstance());
                                }
                            });
                            perPop.showPopupWindow();
                        }
                    }));
    }


    @Override
    public void onEmojiClick() {

    }


    protected void setSessionListener() {
        if (listener == null) {
            listener = new SessionEventListener() {
                @Override
                public void onAvatarClicked(Context context, IMMessage message) {
                    if (TextUtils.equals(String.valueOf(service.getUserId()), message.getFromAccount())) {
                        ARouterUtils.toMineDetailActivity("");
                    } else {
                        if (message.getSessionType() == SessionTypeEnum.Team) {  //群组
                            isInTeamcallback(context, message.getFromAccount(), 0, null, 0, null, null);
                        } else {
                            ARouterUtils.toMineDetailActivity(String.valueOf(message.getFromAccount()));
                        }
                    }
                }

                @Override
                public void onAvatarClicked(String uid) {
                    ARouterUtils.toMineDetailActivity(TextUtils.equals(String.valueOf(service.getUserId()), uid) ? "" : uid);
                }

                @Override
                public void onVoiceSeatClicked(Context context, IMMessage message) {
                    if (TextUtils.equals(String.valueOf(service.getUserId()), message.getFromAccount())) {
                        ARouterUtils.toMineDetailActivity("");
                    } else {
                        ARouterUtils.toMineDetailActivity(String.valueOf(message.getFromAccount()));
                    }
                }

                @Override
                public void onCardClicked(Context context, IMMessage message) {
                    if (TextUtils.equals(String.valueOf(service.getUserId()), message.getSessionId())) {
                        ARouterUtils.toMineDetailActivity("");
                    } else {
                        ARouterUtils.toMineDetailActivity(String.valueOf(message.getSessionId()));
                    }
                }

                @Override
                public void onAitClicked(String account) {
                    if (TextUtils.equals(account, service.getUserId() + "")) {
                        ARouterUtils.toMineDetailActivity("");
                        return;
                    }
                    showGiftPop(account, mTeamType == 0 ? 3 : 4);   //聊天室点击的时候传4  群主点击传3
                }

                @Override
                public void onAvatarLongClicked(Context context, IMMessage message) {
                    if (sessionType == SessionTypeEnum.Team) {  //@用户长按
                        isInTeamcallback(context, message.getFromAccount(), 1, null, 0, null, null);
                    }
                }

                @Override
                public void onAckMsgClicked(Context context, IMMessage message) {

                }

                //跳转金币清单
                @Override
                public void onAddAmount() {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_INTEGRAL);
                }

                @Override
                public void onCreateFamilyEvent(String event) {
                    //peony://familyHome
                    //peony://familyShare
                    //peony://familyDownload
                    if (TextUtils.equals("peony://familyHome", event)) {
                        getFamilyId(true, false);
                    } else if (TextUtils.equals("peony://familyShare", event)) {  //分享好友
                        ARouterUtils.toUserShareList(Interfaces.SHARE_QUEST_TYPE_FAMILY);
                    } else if (TextUtils.equals("peony://familyDownload", event)) {  //
                        if (mSharePop == null) {
                            mSharePop = new SharePopWindow(getActivity());
                        }
                        mSharePop.questData(Interfaces.SHARE_QUEST_TYPE_FAMILY);
                    } else if (TextUtils.equals("peony://familyRecruitRed", event)) {  //家族招募红包
                        showFamilyRecruitPop();
                    }

                }

                @Override
                public void onFamilyJoin(String account, IMMessage message) {
                    if (sessionType == SessionTypeEnum.Team) {
                        IMMessage message1 = MessageBuilder.createTextMessage(sessionId, sessionType, aitManager.insertAitMember(message.getFromAccount(), TeamHelper.getTeamMemberDisplayName(message.getSessionId(), message.getFromAccount()), AitContactType.TEAM_MEMBER, inputPanel.getEditSelectionStart(), true) + " 欢迎加入大家庭！");
                        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
                        antiSpamOption.enable = false;
                        message1.setNIMAntiSpamOption(antiSpamOption);
                        sendMessage(message1);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("family_join", 1);
                        message.setLocalExtension(map);
                        NIMClient.getService(MsgService.class).updateIMMessage(message);
                        messageListPanel.refreshMessageItem(message.getUuid());
                    }
                }

                @Override
                public void openRedEnvelope(int red_packet_id, IMMessage message) {
                    redImMessage = message;
                    getRedDetail(red_packet_id);

                }

                /**
                 * 打开空投
                 */
                @Override
                public void openAirdrop(int airDrop, IMMessage message) {
                    getAirdropDetail(airDrop, message);
                }

                @Override
                public void continueSendGift(IMMessage message) {
                    if (continueSendGiftPopWindow == null)
                        continueSendGiftPopWindow = new ContinueSendGiftPopWindow(getActivity());
                    continueSendGiftPopWindow.initData(message);
                    continueSendGiftPopWindow.addOnClickListener(new ContinueSendGiftPopWindow.OnSelectListener() {
                        @Override
                        public void sendGift(GiftDto data, int num, List<String> userId, String name) {
                            if (message.getSessionType() == SessionTypeEnum.Team) {  //群组
                                if (userId != null) {
                                    isInTeamcallback(getActivity(), userId.get(0), 2, data, num, userId, name);
                                } else {
                                    sendMessageGift(data, num, userId, name);
                                }
                            } else {
                                sendMessageGift(data, num, userId, name);
                            }

                        }
                    });
                    continueSendGiftPopWindow.showPopupWindow();
                }

                @Override
                public void openAirdropDetail(int airDropId) {
                    AirdropDetailActivity.startActivity(getActivity(), airDropId);
                }

                @Override
                public void toFamilyDetail(int familyId, String invite_id) {
                    ARouterUtils.toFamilyDetail(familyId, invite_id, 1);
                }

                @Override
                public void getRedEnvelopeDetail(int red_packet_id) {
                    if (mEnvelopeDetailsPopWindow == null) {
                        mEnvelopeDetailsPopWindow = new RedEnvelopeDetailsPopWindow(getActivity());
                    }
                    mEnvelopeDetailsPopWindow.showPopup(red_packet_id);
                }


            };
        }
        NimUIKit.setSessionListener(listener);
    }

    private void isInTeamcallback(Context context, String tid, int tag, GiftDto data,
                                  int num, List<String> userId, String name) {
        if (ClickUtil.canOperate() && messageListPanel != null) {
            messageListPanel.isInTeam(sessionId, tid, new RequestCallback<TeamMember>() {
                @Override
                public void onSuccess(TeamMember param) {
                    if (!param.isInTeam()) {
                        ToastHelper.showToast(context, context.getResources().getString(R.string.isinteam));
                    } else {
                        if (tag == 0) {
                            showGiftPop(tid, mTeamType == 0 ? 3 : 4);   //聊天室点击的时候传4  群主点击传3
                        } else if (tag == 1 && inputPanel != null) {
                            aitManager.insertAitMemberInner(tid, TeamHelper.getTeamMemberDisplayName(sessionId, tid), AitContactType.TEAM_MEMBER, inputPanel.getEditSelectionStart(), true);
                        } else if (tag == 2) {
                            sendMessageGift(data, num, userId, name);
                        }
                    }
                }

                @Override
                public void onFailed(int code) {
                    if (code == 404) {
                        ToastHelper.showToast(context, context.getResources().getString(R.string.isinteam));
                    }
                }

                @Override
                public void onException(Throwable exception) {

                }
            });

        }

    }


    private void showFamilyRecruitPop() {
        if (mRedEnvelopePopWindow == null) {
            mRedEnvelopePopWindow = new SendRedEnvelopePopWindow(getActivity());
            mRedEnvelopePopWindow.addOnclikListener((gold_price, gold_num, receiveUserType, gold_content) -> sendGoldRedEnvelope(mTeamType, gold_price, gold_num, receiveUserType, gold_content));
        }
        isFamilyRecruit = true;
        mRedEnvelopePopWindow.setIsFamilyRecruit(isFamilyRecruit);
        getCoin();
        mRedEnvelopePopWindow.showPopupWindow();
    }


    /**
     * 获取群id
     */
    private void getFamilyId(boolean isGoActivity, boolean isGetId) {
        Team t = NimUIKit.getTeamProvider().getTeamById(sessionId);
        if (t == null) return;
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getFamilyId(t.getId())
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            if (isGetId) {
                                mFamilyId = (response.getData().family_id);
                                return;
                            }
                            if (isGoActivity) {
                                ARouterUtils.toFamilyDetail(response.getData().family_id, 1);
                            } else {
                                ARouter.getInstance()
                                        .navigation(MineService.class)
                                        .trackEvent("红包雨开始", "family_red_packet_rain_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), System.currentTimeMillis(), com.tftechsz.common.utils.CommonUtil.getOSName(), Constants.APP_NAME, response.getData().family_id)), null);

                            }

                        }
                    }
                }));
    }

    /**
     * 进入聊天室
     */
    private void joinRoom() {
        mCompositeDisposable.add(new RetrofitManager().createChatRoomApi(PublicService.class).joinRoom(sessionId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                    }
                }));
    }

    /**
     * 进入私聊页面
     */
    private void joinP2PRoom() {
        String chatBgJson = MMKVUtils.getInstance().decodeString(Interfaces.SP_CHAT_BG + service.getUserId());
        JSONObject json = new JSONObject();
        if (!TextUtils.isEmpty(chatBgJson)) {
            json = JSONObject.parseObject(chatBgJson);
            setWindowBg(json.getString(sessionId));
        } else {
            setWindowBg("");
        }
        JSONObject finalJson = json;
        mCompositeDisposable.add(new RetrofitManager().createIMApi(ChatApiService.class).joinP2PRoom(sessionId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<JoinLeaveRoom>>() {
                    @Override
                    public void onSuccess(BaseResponse<JoinLeaveRoom> response) {
                        JoinLeaveRoom data = response.getData();
                        if (data != null && data.to_user != null) {
                            String bgValue = finalJson.getString(sessionId);
                            if (bgValue == null || !TextUtils.equals(bgValue, data.chat_bg)) {  //如果没有缓存 || 存的值不一样才设置
                                setWindowBg(data.chat_bg);
                                finalJson.put(sessionId, data.chat_bg);
                                MMKVUtils.getInstance().encode(Interfaces.SP_CHAT_BG + service.getUserId(), JSONObject.toJSONString(finalJson));
                            }

                            JoinLeaveRoom.ToUser toUser = response.getData().to_user;
                            if (toUser.isDisable()) {
                                if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.user_disable != null) {
                                    BaseWebViewActivity.start(getActivity(), "", service.getConfigInfo().share_config.user_disable.link + "&user_id=" + sessionId, 0, 13);
                                    if (getActivity() != null)
                                        getActivity().finish();
                                }
                                return;
                            }
                            if (toUser.isLogout()) {
                                CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance());
                                customPopWindow.setContent(getString(R.string.user_no_exist))
                                        .setRightButton("我知道了")
                                        .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                                            @Override
                                            public void onCancel() {
                                            }

                                            @Override
                                            public void onSure() {
                                                if (getActivity() != null)
                                                    getActivity().finish();
                                            }
                                        })
                                        .setRightGone();
                                customPopWindow.setOutSideDismiss(false);
                                customPopWindow.setBackPressEnable(false);
                                customPopWindow.showPopupWindow();
                            }
                        }
                    }
                }));
    }

    private void setWindowBg(String bg) {
        if (TextUtils.isEmpty(bg)) {
            if (getActivity() != null && isAdded()) {
                if (tagIsDialog != 1) {
                    getActivity().getWindow().setBackgroundDrawable(Utils.getDrawable(R.drawable.shape_bg_f9f9f9));
                }
            }
            return;
        }
        RequestOptions options = new RequestOptions()
                .transform(new GlideRoundTransform2(requireActivity(), 20));

        Glide.with(BaseApplication.getInstance())
                .asDrawable()
                .override(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())
                .centerCrop()
                .load(bg)
                .apply(options)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (getActivity() != null && isAdded()) {
                            if (tagIsDialog != 1) {
                                mRlToolBar.post(() -> getActivity().getWindow().setBackgroundDrawable(Utils.getDrawable(R.drawable.shape_bg_f9f9f9)));
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (getActivity() != null && isAdded()) {
//                            if (tagIsDialog != 1) {
                            mRlToolBar.post(() -> getActivity().getWindow().setBackgroundDrawable(resource));
//                            }
                        }
                        return false;
                    }
                }).submit();
    }

    private void signIn() {
        FamilyService familyService = ARouter.getInstance().navigation(FamilyService.class);
        familyService.familySign("family-im", new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (response.getData()) {
                    mIvSign.setVisibility(View.GONE);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_SIGN_IN_SUCCESS));
                }
            }
        });

    }


    /**
     * 获取当前用户是否组了情侣
     */
    private void getCoupleCheck() {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getCoupleCheck()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<GroupCoupleDto>>() {

                    @Override
                    public void onSuccess(BaseResponse<GroupCoupleDto> baseResponse) {
                        if (baseResponse.getData() == null || baseResponse.getData().user_id == 0) {
                            ARouterUtils.toGroupCouple(getActivity(), sessionId);
                        } else {
                            getGroupCouple(String.valueOf(baseResponse.getData().user_id));
                        }
                    }
                }));
    }


    /**
     * 获取当前用户组情侣信息
     */
    public void getGroupCouple(String userId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getCoupleInfo(userId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<GroupCoupleDto>>() {

                    @Override
                    public void onSuccess(BaseResponse<GroupCoupleDto> baseResponse) {
                        if (baseResponse.getData() == null || isDestroyed()) return;
                        groupCoupleDto = baseResponse.getData();
                        if (baseResponse.getData().is_couple_to_me) {
                            if (couplesTaskPop == null) {
                                if (mFamilyId == 0) {
                                    getFamilyId(false, true);
                                }
                                couplesTaskPop = new CouplesTaskPop(requireActivity(), mFamilyId == 0 ? sessionId : String.valueOf(mFamilyId), userId);
                            }

                            couplesTaskPop.initData(baseResponse.getData());
                            couplesTaskPop.showPopupWindow();
                        } else {
                            initGroupDialog(userId);
                        }

                    }
                }));
    }

    /**
     * 组情侣弹窗
     */
    private void initGroupDialog(String userId) {
        if (groupCouplePopWindow == null)
            groupCouplePopWindow = new GroupCouplePopWindow(getActivity(), userId);
        groupCouplePopWindow.initData(groupCoupleDto, () -> {
            showGiftPop(sessionId, mTeamType == 1 ? 5 : 2);
            //不能点击情况直接送礼物
        });
        groupCouplePopWindow.showPopupWindow();
    }

    /**
     * 打开表白信
     */
    private void getCoupleLetter() {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getCoupleLetter()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<CoupleLetterDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<CoupleLetterDto>> listBaseResponse) {
                        if (listBaseResponse.getData() != null && listBaseResponse.getData().size() > 0) {
                            if (confessionLetterPopWindow == null)
                                confessionLetterPopWindow = new ConfessionLetterPopWindow(getActivity());
                            confessionLetterPopWindow.initData(listBaseResponse.getData());
                            confessionLetterPopWindow.showPopupWindow();
                        }
                    }
                }));
    }


    /**
     * 获取禁用提示
     */
    private void getTips() {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getTips()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse>() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {

                    }
                }));
    }

    /**
     * 进入群聊页面
     */
    private void joinFamilyRoom(String type) {
        mCompositeDisposable.add(new RetrofitManager().createIMApi(ChatApiService.class).joinFamilyRoom(sessionId, type)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (response == null || response.getData() == null) return;
                        if (response.getData().dress_up != null && !TextUtils.isEmpty(response.getData().dress_up.car) && mIsFirstEnter) {
                            GiftDto bean = new GiftDto();
                            bean.name = Utils.getFileName(response.getData().dress_up.car).replace(".svga", "");
                            bean.animation = response.getData().dress_up.car;
                            myGiftList.offer(bean);
                            mIsFirstEnter = false;
                        }
                        if (TextUtils.equals("open", type)) {
                            mIvSign.setVisibility(response.getData().is_sign == 0 ? View.VISIBLE : View.GONE);
                            if (response.getData().room_status == 1) {  //语音房开启
                                mIsOpenRoom = response.getData().room_status;
                                if (getActivity() != null)
                                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                if (!mIsLoadRoom) {
                                    getRoomInfo();
                                }
                            }
                            //甜蜜值减少
                            if (response.getData().coupleSweetInfo != null && !TextUtils.isEmpty(response.getData().coupleSweetInfo.msg)) {
                                lightenGiftPop = new LightenGiftPop(requireActivity(), 2);
                                lightenGiftPop.setData(response.getData().coupleSweetInfo);
                                lightenGiftPop.showPopupWindow();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (code == -1 && getActivity() != null && TextUtils.equals("open", type))
                            getActivity().finish();
                    }
                }));
    }


    /**
     * 获取语音房信息
     */
    protected void getRoomInfo() {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getRoomInfo()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<VoiceChatDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<VoiceChatDto> response) {
                        if (response != null && response.getData() != null) {
                            mVoiceChat.setVisibility(View.VISIBLE);
                            mVoiceChat.setTid(NimUIKit.getTeamProvider().getTeamById(sessionId).getId());
                            mVoiceChat.setVoiceUser(response.getData(), true);
                            MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 1);
                        } else {
                            mVoiceChat.setVisibility(View.GONE);
                            MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 0);
                        }
                        mIsLoadRoom = true;
                    }
                }));
    }


    /**
     * 进行游戏
     * type  1 = 骰子 2 = 剪刀石头布
     */
    private void doGame(int type) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).doGame(type)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }
                }));
    }


    /**
     * 点击红包雨
     */
    private void clickRain(int rainId, int clickId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).clickRain(rainId, clickId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }
                }));
    }

    /**
     * 红包雨结束
     */
    private void rainEnd(int rainId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).rainEnd(rainId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }
                }));
    }

    /**
     * 离开聊天室
     */
    private void leaveRoom() {
        mCompositeDisposable.add(new RetrofitManager().createChatRoomApi(PublicService.class).leaveRoom(sessionId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                            mCompositeDisposable.dispose();
                            mCompositeDisposable.clear();
                        }
                    }
                }));
    }


    /**
     * 退出语音聊天房间
     */
    private void deleteRoom(MoreFunDto data, int position) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).deleteRoom()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null) {
                            if (response.getData()) {
                                mVoiceChat.setVisibility(View.GONE);
                                MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 0);
                                if (TextUtils.equals(getString(R.string.chat_voice_chat_text), moreAdapter.getData().get(position).content)) {
                                    moreAdapter.getData().get(position).content = getString(R.string.chat_voice_chat);
                                    moreAdapter.getData().get(position).bg = R.drawable.chat_ic_voice_chat;
                                    moreAdapter.setData(position, moreAdapter.getData().get(position));
                                }
                            }
                        }
                    }
                }));

    }


    /**
     * 创建语音房模式
     */
    private void createRoom(MoreFunDto data, int position) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).createRoom()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        if (response.getData() != null) {
                            if (TextUtils.equals(getString(R.string.chat_voice_chat), moreAdapter.getData().get(position).content)) {
                                moreAdapter.getData().get(position).content = getString(R.string.chat_voice_chat_text);
                                moreAdapter.getData().get(position).bg = R.mipmap.chat_ic_voice_chat_text;
                                moreAdapter.setData(position, moreAdapter.getData().get(position));
                            }
                        }
                    }
                }));

    }


    private void checkCallMsg(int type) {
        if (com.tftechsz.common.utils.CommonUtil.showCallTip(partyService)) {
            return;
        }
        if (com.tftechsz.common.utils.CommonUtil.showCallTip2(partyService, new com.tftechsz.common.utils.CommonUtil.OnSelectListener() {
            @Override
            public void onSure() {
                call(type);
            }
        }))
            return;
        call(type);

    }


    //type 1 视频通话 2 语音通话
    private void call(int type) {
        mineService.getCallCheck(sessionId, 2, new ResponseObserver<BaseResponse<CallCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<CallCheckDto> response) {
                CallCheckDto data = response.getData();
                if (null == data || !com.tftechsz.common.utils.CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
                    if (type == 2) {
                        if (null != data && data.list != null && null != data.list.video) {
                            if (data.list.video.is_lock) {
                                if (null != data.error && null != data.error.intimacy) {
                                    showCustomPop(data.error.intimacy.msg);
                                } else if (null != data.error && null != data.error.video) {
                                    if (TextUtils.equals(data.error.video.cmd_type, Constants.DIRECT_RECHARGE)) {
                                        if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_limit_from_channel == 1) {
                                            if (beforePop == null)
                                                beforePop = new RechargeBeforePop(getActivity());
                                            beforePop.addOnClickListener(() -> showRechargePop(sessionId));
                                            beforePop.showPopupWindow();
                                        } else {
                                            showRechargePop(sessionId);
                                        }
                                    } else {
                                        showCustomPop(data.error.video.msg);
                                    }
                                }
                            } else {
                                RxBus.getDefault().post(new VoiceChatEvent(Constants.NOTIFY_EXIT_VOICE_ROOM));
                                service.setMatchType("");
                                ChatMsgUtil.callMessage(1, String.valueOf(service.getUserId()), sessionId, "", false);
                            }
                        }
                    } else {
                        if (null != data && data.list != null && null != data.list.video) {
                            if (data.list.video.is_lock) {
                                if (null != data.error && null != data.error.intimacy) {
                                    showCustomPop(data.error.intimacy.msg);
                                } else if (null != data.error && null != data.error.video) {
                                    if (TextUtils.equals(data.error.video.cmd_type, Constants.DIRECT_RECHARGE)) {
                                        if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_limit_from_channel == 1) {
                                            if (beforePop == null)
                                                beforePop = new RechargeBeforePop(getActivity());
                                            beforePop.addOnClickListener(() -> showRechargePop(sessionId));
                                            beforePop.showPopupWindow();
                                        } else {
                                            showRechargePop(sessionId);
                                        }
                                    } else {
                                        showCustomPop(data.error.video.msg);
                                    }
                                }
                            } else {
                                RxBus.getDefault().post(new VoiceChatEvent(Constants.NOTIFY_EXIT_VOICE_ROOM));
                                service.setMatchType("");
                                ChatMsgUtil.callMessage(2, String.valueOf(service.getUserId()), sessionId, "", false);
                            }
                        }
                    }
                }
            }
        });

    }


    /**
     * 显示充值列表
     */
    private void showRechargePop(String userId) {
        if (rechargePopWindow == null)
            rechargePopWindow = new RechargePopWindow(getActivity(), 3, mTeamType == 0 ? 4 : 1, 0, 0, userId);
        rechargePopWindow.getCoin();
        rechargePopWindow.setFormType(sessionType == SessionTypeEnum.Team ? 1 : 0);
        rechargePopWindow.requestData();
        rechargePopWindow.showPopupWindow();
    }


    private void showCustomPop(String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(getActivity());
        customPopWindow.setContent(content);
        customPopWindow.setRightGone();
        customPopWindow.showPopupWindow();
    }


    //发送红包
    private void sendGoldRedEnvelope(int teamType, int gold_price, int gold_num, int receiveUserType, String
            gold_content) {
        if (getActivity() != null)
            GlobalDialogManager.getInstance().show(getActivity().getFragmentManager(), "正在发送中,请稍后...");
        Flowable<BaseResponse<Boolean>> baseResponseFlowable;
        if (isFamilyRecruit) {
            PublicService familyApi = RetrofitManager.getInstance().createFamilyApi(PublicService.class);
            baseResponseFlowable = familyApi.sendGoldRedEnvelopeFamilyRecruit(gold_price, gold_num, gold_content);
        } else {
            PublicService chatRoomApi = RetrofitManager.getInstance().createExchApi(PublicService.class);
            if (teamType == 0) {//家族
                baseResponseFlowable = chatRoomApi.sendGoldRedEnvelopeFamily(gold_price, gold_num, receiveUserType, gold_content);
            } else {  //广场
                baseResponseFlowable = chatRoomApi.sendGoldRedEnvelopeRoom(sessionId, gold_price, gold_num, gold_content);
            }
        }
        mCompositeDisposable.add(baseResponseFlowable.compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        GlobalDialogManager.getInstance().dismiss();
                        mRedEnvelopePopWindow.clearEdit();
                        getCoin();
                        messageListPanel.scrollToBottom();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));
    }

    //获取红包状态
    private void getRedDetail(int red_packet_id) {
        if (redImMessage.getLocalExtension() != null) {
            Object obj = redImMessage.getLocalExtension().get(Interfaces.RED_PACKAGE_TYPE);
            if (obj != null) { //被标记的红包都已经点击过, 直接跳转到红包领取详情
                if (mEnvelopeDetailsPopWindow == null) {
                    mEnvelopeDetailsPopWindow = new RedEnvelopeDetailsPopWindow(getActivity());
                }
                mEnvelopeDetailsPopWindow.showPopup(red_packet_id);
            }
            return;
        }

        mCompositeDisposable.add(RetrofitManager.getInstance().createExchApi(PublicService.class)
                .getRedDetail(red_packet_id)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<RedDetailInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<RedDetailInfo> response) {
                        RedDetailInfo data = response.getData();
                        if (data.is_receive == 0) { // 红包未领,弹窗红包领取
                            if (data.is_complete == 1) { //红包被领完
                                updateRedEnvelopeIMMessage(2);
                            }
                            if (data.is_expire == 1) { //红包过期
                                updateRedEnvelopeIMMessage(3);
                            }
                            if (mGoldRedEnvelopePopWindow == null) {
                                mGoldRedEnvelopePopWindow = new RedEnvelopeReceivePopWindow(getActivity(), type -> updateRedEnvelopeIMMessage(type));
                            }
                            mGoldRedEnvelopePopWindow.showPopup(red_packet_id, response.getData());
                        } else { // 红包被领去过,弹窗红包详情
                            updateRedEnvelopeIMMessage(1);
                            if (mEnvelopeDetailsPopWindow == null) {
                                mEnvelopeDetailsPopWindow = new RedEnvelopeDetailsPopWindow(getActivity());
                            }
                            mEnvelopeDetailsPopWindow.showPopup(red_packet_id);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    //更新红包消息转态
    private void updateRedEnvelopeIMMessage(int type) {
        if (redImMessage != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(Interfaces.RED_PACKAGE_TYPE, type);
            redImMessage.setLocalExtension(map);
            NIMClient.getService(MsgService.class).updateIMMessage(redImMessage);
            messageListPanel.refreshMessageItem(redImMessage.getUuid());
        }
    }

    //更新奖励消息
    private void updateAwardIMMessage(String msgId, String msg, String desc) {

        messageListPanel.adapterNotifyAward(msgId, msg, desc);

    }

    //获取空投状态
    private void getAirdropDetail(int airdropId, IMMessage message) {
        if (message.getLocalExtension() != null) {
            Object obj = message.getLocalExtension().get("airdrop_type");
            if (obj != null) { //被标记的空投都已经点击过, 直接跳转到空投详情
                AirdropDetailActivity.startActivity(getActivity(), airdropId);
            }
            return;
        }
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(ChatApiService.class)
                .getAirdropBag(airdropId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<AirdropBagDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<AirdropBagDto>> response) {
                        if (response.getData() != null && response.getData().size() > 0) {
                            if (airdropId != 0) {
                                AirdropBagDto data = response.getData().get(0);
                                if (data.is_receive == 0) { // 未领取空投
                                    ////0-倒计时 1-进行中 2-完成 3-失效
                                    if (data.status == 0 || data.status == 1) { //进行中
                                        openAirdropWindow(message, airdropId);
                                    } else if (data.status == 2) { //完成
                                        updateAirdropIMMessage(message, 2);
                                        AirdropDetailActivity.startActivity(getActivity(), airdropId);
                                    } else if (data.status == 3) {
                                        updateAirdropIMMessage(message, 3);
                                        AirdropDetailActivity.startActivity(getActivity(), airdropId);
                                    }
                                } else { // 领过,空投详情
                                    updateAirdropIMMessage(message, 1);
                                    AirdropDetailActivity.startActivity(getActivity(), airdropId);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 空投弹窗
     */
    private void openAirdropWindow(IMMessage message, int id) {
        if (mOpenAirdropWindow == null)
            mOpenAirdropWindow = new OpenAirdropWindow(getActivity());
        mOpenAirdropWindow.initData(id);
        mOpenAirdropWindow.addOnClickListener(airdropId -> {
            if (message != null)
                updateAirdropIMMessage(message, 1);
//                AirdropDetailActivity.startActivity(getActivity(), airdropId);
        });
        mOpenAirdropWindow.showPopupWindow();
    }

    //更新红包消息转态
    private void updateAirdropIMMessage(IMMessage message, int type) {
        if (message != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("airdrop_type", type);
            message.setLocalExtension(map);
            NIMClient.getService(MsgService.class).updateIMMessage(message);
            messageListPanel.refreshMessageItem(message.getUuid());
        }
    }

    private void openCoupleBag() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(ChatApiService.class)
                .getGiftBag()
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<CoupleBagDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<CoupleBagDto>> response) {
                        if (response.getData() != null && response.getData().size() > 0) {
                            if (coupleGiftBagPop == null)
                                coupleGiftBagPop = new CoupleGiftBagPop(getActivity());
                            coupleGiftBagPop.setData(sessionId, response.getData());
                            coupleGiftBagPop.showPopupWindow();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));

    }

    private void openCoupleBagDetail(int familyId, int bagId) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(ChatApiService.class).getGiftBagDetail(familyId, bagId).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CoupleBagDetailDto>>() {
            @Override
            public void onSuccess(BaseResponse<CoupleBagDetailDto> response) {
                if (response.getData() != null) {
                    if (coupleGiftBagDetailPop == null)
                        coupleGiftBagDetailPop = new CoupleGiftBagDetailPop(getActivity());
                    coupleGiftBagDetailPop.setData(familyId, bagId, response.getData());
                    coupleGiftBagDetailPop.showPopupWindow();
                }
            }
        }));
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获得轮播图当前的位置
            if (mTopScrollAdapter != null && mTopScrollAdapter.getItemCount() == 2) {
                int currentPosition = mTopVp2.getCurrentItem();
                mTopVp2.setCurrentItem(currentPosition == 0 ? 1 : 0);
            }
            mHandler.postDelayed(runnable, Interfaces.CHAT_TOP_SCROLL_TIMES);
        }
    };

    public void destroyView() {
        registerObservers(false);
        if (giftRoot != null) {
            giftRoot.removeAllViews();
            giftRoot = null;
        }
        if (ivChatCall != null) {
            ivChatCall.removeAllViews();
            ivChatCall = null;
        }
        if (beforePop != null) {
            if (beforePop.isShowing()) {
                beforePop.dismiss();
            }
            beforePop = null;
        }
        if (giftPopWindow != null) {
            if (giftPopWindow.isShowing()) {
                giftPopWindow.dismiss();
            }
            giftPopWindow = null;
        }
        if (chatMessagePopWindow != null) {
            if (chatMessagePopWindow.isShowing()) {
                chatMessagePopWindow.dismiss();
            }
            chatMessagePopWindow = null;
        }
        if (customPopWindow != null) {
            if (customPopWindow.isShowing()) {
                customPopWindow.dismiss();
            }
            customPopWindow = null;
        }
        if (perPop != null) {
            if (perPop.isShowing()) {
                perPop.dismiss();
            }
            perPop = null;
        }
        if (videoCallPopWindow != null) {
            if (videoCallPopWindow.isShowing()) {
                videoCallPopWindow.dismiss();
            }
            videoCallPopWindow = null;
        }
        if (mOpenAirdropWindow != null) {
            if (mOpenAirdropWindow.isShowing()) {
                mOpenAirdropWindow.dismiss();
            }
            mOpenAirdropWindow = null;
        }
        if (inputPanel != null) {
            inputPanel.onDestroy();
            inputPanel.removeAllViews();
            inputPanel = null;
        }
        if (ivChatGift != null) {
            ivChatGift = null;
        }
        ViewGroup viewGroup = (ViewGroup) getView();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
        mRlIntimacy = null;
        if (mClGroupCouple != null) {
            mClGroupCouple.removeAllViews();
            mClGroupCouple = null;
        }
        if (mLlNotice != null) {
            mLlNotice.removeAllViews();
            mLlNotice = null;
        }
        if (mRlIntimacyCall != null) {
            mRlIntimacyCall.removeAllViews();
            mRlIntimacyCall = null;
        }
        if (mLlFamilyBox != null) {
            mLlFamilyBox.removeAllViews();
            mLlFamilyBox = null;
        }
        if (mRlToolBar != null) {
            mRlToolBar.removeAllViews();
            mRlToolBar = null;
        }
        mIvRight = null;
        mIvLeft = null;
        mIvGiftMask = null;
        lottieAnimationView = null;
        ivChatRed = null;
        ivChatPhoto = null;
        redImMessage = null;
        mTvGroupCoupleContent = null;
        if (sessionType == SessionTypeEnum.P2P && mVoiceChat != null) {
//            mVoiceChat.listen(false);
            mVoiceChat.removeAllViews();
            mVoiceChat = null;
        }

    }
}
