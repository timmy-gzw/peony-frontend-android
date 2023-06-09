package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.appbar.AppBarLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.recyclerview.decoration.SpacingDecoration;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.adapter.MyBannerImageAdapter;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.event.VoiceChatEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.other.SpaceItemDecoration;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.TopSmoothScroller;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.MyBannerImageHolder;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.UserBanPopWindow;
import com.tftechsz.common.widget.pop.VideoCallPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.BaseUserInfoAdapter;
import com.tftechsz.mine.adapter.GiftAdapter;
import com.tftechsz.mine.adapter.LabelDisplayAdapter;
import com.tftechsz.mine.adapter.ProfilePhotoAdapter;
import com.tftechsz.mine.adapter.TrendAdapter;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.entity.dto.TrendDto;
import com.tftechsz.mine.mvp.IView.IMineDetailView;
import com.tftechsz.mine.mvp.presenter.MineDetailPresenter;
import com.tftechsz.mine.utils.UserManager;
import com.tftechsz.mine.widget.pop.GuardPopWindow;
import com.tftechsz.mine.widget.pop.MineDetailMorePopWindow;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import razerdp.basepopup.BasePopupWindow;

/**
 * 个人资料
 */
@Route(path = ARouterApi.ACTIVITY_MINE_DETAIL)
public class MineDetailActivity extends BaseMvpActivity<IMineDetailView, MineDetailPresenter> implements IMineDetailView, View.OnClickListener {
    private final int MAX_SIZE = 9;

    private Banner mBanner;
    private RecyclerView mRvPic;//相册
    private RecyclerView mRvTrend;   //动态
    private ImageView ivPicAdd;
    private View viewPicMask;
    private TextView mTvEditInfo;
    private ImageView mTobBack, mTobMore;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTvTobTitle;
    private TextView mTvName, mTvSign;    //姓名,个性签名

    private TextView mTvAttention;   //关注
    private TextView mTvEdit;   //编辑资料
    private LinearLayout mLlOther;  //他人

    private TextView mIvSex;  //性别，是否真人，是否认证
    private ImageView mIvIsRealPeople, mIvIsAuth, mIvIsVIP;
    private GestureDetector gestureDetector;
    private TextView mTvTrend, mTvTrendMore;   //动态

    //女性用户逻辑私信
    private LinearLayout mLlAccost, mLlOperate, llBottom;
    private TextView mTvChatMessage, mTvAccost;
    private ImageView mIvAccost;

    private LinearLayout mllAuth;

    private ProfilePhotoAdapter profilePhotoAdapter; //相册

    private UserInfo mUserInfo;
    private int MAX_SCROLL;

    private ConstraintLayout mLlVoice;
    private TextView mTvVoiceTime;  //录音
    private ImageView mIvVoice;

    private AudioPlayer mIjkPlayer;

    private LottieAnimationView mLottie;
    private VideoCallPopWindow mVideoCallPopWindow;   //语音视频弹窗

    private ConfigInfo mConfig;

    @Autowired(name = "user_id")
    public String mUserId;

    @Autowired
    UserProviderService service;
    private Timer mTimer;
    private int mediaTime, mediaTimeTemp;
    private PageStateManager mPageManager;
    private LottieAnimationView mLottieVoice;

    //等级相关
    private ConstraintLayout mClLevel;
    private TextView tvLevelTitle, tvLevelVip;
    private TextView mTvLocalTyrantTitle, mTvLocalTyrantLevel;    //土豪值相关
    private TextView mTvCharmTitle, mTvCharmLevel;   //魅力值相关
    private ImageView ivCharmBg, ivLocalTyrantBg;

    //礼物相关
    private RecyclerView mRvGift;  //礼物
    private TextView mTvGift, mTvGiftVip, tvGiftTitle, tvGiftCount;
    private ConstraintLayout clGift;
    private List<TrendDto> mTrendList;

    private TrendAdapter mTrendAdapter;   //动态

    //标签
    private TextView mTvLabel;
    //标签
    private View clLabel;
    private RecyclerView rvLabel;
    private LabelDisplayAdapter labelDisplayAdapter;

    private BaseUserInfoAdapter userInfoAdapter;
    private GiftAdapter giftAdapter;
    private ArrayList<GiftDto> gifts = null;

    @Override
    public MineDetailPresenter initPresenter() {
        return new MineDetailPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mToolbar = findViewById(R.id.toolbar);
        ImmersionBar.with(this).fullScreen(true).titleBar(mToolbar).statusBarDarkFont(true)
                .transparentStatusBar()
                .init();
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        mTobBack = findViewById(R.id.tob_back);
        mLottieVoice = findViewById(R.id.lottie_voice);
        llBottom = findViewById(R.id.ll_bottom);

        //banner
        mBanner = findViewById(R.id.banner);
        mBanner.addBannerLifecycleObserver(this);
        //个人相册
        ivPicAdd = findViewById(R.id.iv_profile_add);
        viewPicMask = findViewById(R.id.view_mask);
        ivPicAdd.setOnClickListener(this);
        ivPicAdd.setVisibility(TextUtils.isEmpty(mUserId) ? View.VISIBLE : View.GONE);
        mRvPic = findViewById(R.id.rv_profile_pic);
        mRvPic.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mRvPic.addItemDecoration(new SpaceItemDecoration(ConvertUtils.dp2px(6), 0, false));

        //动态
        mRvTrend = findViewById(R.id.rv_trend);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 4);
        mRvTrend.setLayoutManager(gridLayoutManager2);
        mTvTrend = findViewById(R.id.tv_trend);
        mTvTrendMore = findViewById(R.id.tv_mine_trend);  //动态

        mTvTobTitle = findViewById(R.id.tob_title);  //名称
        mTobMore = findViewById(R.id.tob_more);
        mTvEditInfo = findViewById(R.id.edit_info);
        mTvName = findViewById(R.id.tv_name);   //姓名
        mTvSign = findViewById(R.id.tv_sign);   //个性签名

        mTvEdit = findViewById(R.id.tv_edit);  //编辑资料
        mTvEdit.setOnClickListener(this);

        mllAuth = findViewById(R.id.ll_auth);

        mLlAccost = findViewById(R.id.ll_accost);
        mLlOperate = findViewById(R.id.ll_operate);
        mTvChatMessage = findViewById(R.id.tv_chat_message);
        mTvAccost = findViewById(R.id.tv_accost);

        //性别，真人，认证
        mIvSex = findViewById(R.id.iv_sex);
        mIvIsRealPeople = findViewById(R.id.iv_real_people);
        mIvIsAuth = findViewById(R.id.iv_auth);
        mIvIsVIP = findViewById(R.id.iv_vip);

        mLlOther = findViewById(R.id.ll_other);  //他人

        mTvAttention = findViewById(R.id.tv_attention);  //关注

        mLlVoice = findViewById(R.id.ll_voice);
        mTvVoiceTime = findViewById(R.id.tv_voice_time);   //录音
        mIvVoice = findViewById(R.id.iv_voice);


        TextView tvUserInfo = findViewById(R.id.tv_user_info);
        RecyclerView rvUserInfo = findViewById(R.id.rv_user_info);
        rvUserInfo.setLayoutManager(new GridLayoutManager(this, 2));
        rvUserInfo.addItemDecoration(new SpacingDecoration(ConvertUtils.dp2px(15f), ConvertUtils.dp2px(6), false));
        userInfoAdapter = new BaseUserInfoAdapter(CommonUtil.isGa());
        userInfoAdapter.addChildClickViewIds(R.id.iv_copy);
        userInfoAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.iv_copy) {
                UserInfo.BaseInfo item = userInfoAdapter.getItem(position);
                if (item == null) return;
                ClipboardUtils.copyText(item.value);
                toastTip("复制成功");
            }
        });
        rvUserInfo.setAdapter(userInfoAdapter);
        //等级
        mClLevel = findViewById(R.id.cl_level);
        tvLevelTitle = findViewById(R.id.tv_level_title);
        tvLevelVip = findViewById(R.id.tv_level_vip);

        clLabel = findViewById(R.id.cl_label);
        rvLabel = findViewById(R.id.rv_label);
        mTvLabel = findViewById(R.id.tv_label);
        mTvLabel.setOnClickListener(this);

        findViewById(R.id.cl_local_tyrant).setOnClickListener(this);  //土豪值
        findViewById(R.id.cl_charm).setOnClickListener(this);  //亲密度
        //土豪值
        ivLocalTyrantBg = findViewById(R.id.iv_local_tyrant_bg);
        mTvLocalTyrantLevel = findViewById(R.id.tv_local_tyrant_level);
        mTvLocalTyrantTitle = findViewById(R.id.tv_local_tyrant_title);
        //魅力值
        ivCharmBg = findViewById(R.id.iv_charm_bg);
        mTvCharmLevel = findViewById(R.id.tv_charm_level);
        mTvCharmTitle = findViewById(R.id.tv_charm_title);
        //礼物
        mTvGift = findViewById(R.id.tv_gift);
        mTvGiftVip = findViewById(R.id.tv_gift_vip);
        tvGiftTitle = findViewById(R.id.tv_gift_title);
        tvGiftCount = findViewById(R.id.tv_gift_count);
        clGift = findViewById(R.id.cl_mine_gift);
        mRvGift = findViewById(R.id.rv_gift);
        mRvGift.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        giftAdapter = new GiftAdapter();
        mRvGift.setAdapter(giftAdapter);
        giftAdapter.setOnItemClickListener((adapter, view, position) -> {
            Postcard postcard = ARouter.getInstance().build(ARouterApi.ACTIVITY_GIFT_WALL)
                    .withString("user_id", mUserId);
            if (gifts != null && gifts.size() > 0) {
                postcard.withParcelableArrayList("gifts", gifts);
            }
            postcard.navigation();
        });
        if (TextUtils.isEmpty(mUserId)) {   //自己
            tvUserInfo.setText(getString(R.string.personal_info));
            tvLevelTitle.setText(getString(R.string.level_mine));
            tvGiftTitle.setText(getString(R.string.receive_gift_my));
        } else {
            tvUserInfo.setText(getString(R.string.basic_info));
            tvLevelVip.setVisibility(View.GONE);
            mTvGiftVip.setVisibility(View.GONE);
        }
        //心
        //搭讪
        mIvAccost = findViewById(R.id.iv_accost);
        setSupportActionBar(mToolbar);
        initListener();
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (null != mUserInfo)
                    ARouterUtils.toTrendActivity(mUserId, mUserInfo.getNickname());
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }


    private void initListener() {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, true);
        mTobBack.setOnClickListener(this);
        mTvAttention.setOnClickListener(this);
        mTobMore.setOnClickListener(this);
        mTvEditInfo.setOnClickListener(this);
        mLottieVoice.setOnClickListener(this);
        mLlAccost.setOnClickListener(this);   //搭讪
        mTvTrendMore.setOnClickListener(this);   //更多动态
        findViewById(R.id.tv_chat_message).setOnClickListener(this);  //私信
        findViewById(R.id.tv_call_video).setOnClickListener(this);   //语音视频
        findViewById(R.id.rl_guard).setOnClickListener(this);   //守护
        findViewById(R.id.ll_auth).setOnClickListener(this);
        findViewById(R.id.tv_mine_gift).setOnClickListener(this);

        mLlVoice.setOnClickListener(this);  //去录音
        mIvVoice.setOnClickListener(this);
//        mRvPic.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!mRvPic.canScrollHorizontally(-1)) {
//                    viewPicMask.setVisibility(View.VISIBLE);
//                } else {
//                    viewPicMask.setVisibility(View.GONE);
//                }
//            }
//        });

        MyBannerImageAdapter<String> bannerImageAdapter = new MyBannerImageAdapter<String>(null) {

            @Override
            public void onBindView(MyBannerImageHolder holder, String data, int position, int size) {
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideUtils.loadPicImage(mContext, holder.imageView, data);
            }
        };
        mBanner.setAdapter(bannerImageAdapter)
                .addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        mRvPic.post(() -> {
                            TopSmoothScroller smoothScroller = new TopSmoothScroller(mContext, 3f);
                            smoothScroller.setTargetPosition(position);

                            mRvPic.getLayoutManager().startSmoothScroll(smoothScroller);
                            profilePhotoAdapter.setIndex(position);
                        });
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                }).setOnBannerListener((data1, position) -> {
                    BannerAdapter adapter = mBanner.getAdapter();
                    if (adapter != null && adapter.getItemCount() > 0) {
                        Object firstIcon = adapter.getData(0);
                        ARouterUtils.toUserPicBrowserActivity(!TextUtils.isEmpty(mUserId) ? Integer.parseInt(mUserId) : service.getUserId(), position, (String) firstIcon, (mUserInfo != null && mUserInfo.isBoy()));
                    }

                });
    }

    private void initNet() {
        if (TextUtils.isEmpty(mUserId)) {   //自己
            //默认添加第一条数据
            TrendDto trendDto = new TrendDto();
            trendDto.image = "ADD";
            mTrendList.add(trendDto);
            p.getSelfTrend(3);
            p.getUserInfoDetail();
        } else {
            p.getUserInfoById(mUserId);
            p.getUserTrend(4, mUserId);
            if (service.getUserInfo() != null && service.getUserInfo().getSex() != 1) {   //女性用户看其他人信息
                if (!CommonUtil.infoBtnTextUpdate3(service)) {
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
                    mLlAccost.setLayoutParams(param);
                    param.setMargins(0, 0, DensityUtils.dp2px(this, 20), 0);
                    LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.MATCH_PARENT, 1.2f);
                    mLlOperate.setLayoutParams(param1);
                }

                if (CommonUtil.infoBtnTextUpdate3(service)) {
                    mTvChatMessage.setVisibility(View.VISIBLE);
                    mTvAccost.setText("喜欢");
                } else {
                    mTvChatMessage.setVisibility(View.GONE);
                    mTvAccost.setText("私信");
                }

            } else {
                mIvAccost.setImageResource(R.mipmap.mine_ic_accost);
                AnimationUtil.createAnimation(mIvAccost);
            }
        }
    }

    private void setupUserTag() {
        if (mUserInfo != null && mUserInfo.getTagList() != null && !mUserInfo.getTagList().isEmpty()) {
            clLabel.setVisibility(View.VISIBLE);
            initLabelView();
            labelDisplayAdapter.setList(mUserInfo.getTagList());
        } else {
            if (TextUtils.isEmpty(mUserId)) {
                clLabel.setVisibility(View.VISIBLE);
                initLabelView();
                labelDisplayAdapter.setList(Collections.singletonList("ADD_TAG"));
            } else {
                clLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mTrendList = new ArrayList<>();
        MAX_SCROLL = DensityUtils.dp2px(this, 340);
        mConfig = service.getConfigInfo();
        mIjkPlayer = new AudioPlayer(BaseApplication.getInstance());
        initRxBus();
        mTrendAdapter = new TrendAdapter(mTrendList, mUserId);
        mRvTrend.setAdapter(mTrendAdapter);
        mRvTrend.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        mPageManager = PageStateManager.initWhenUse(this, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                initNet();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                finish();
            }
        });
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
        } else {
            mPageManager.showContent();
            initNet();
        }

        profilePhotoAdapter = new ProfilePhotoAdapter();
        mRvPic.setAdapter(profilePhotoAdapter);
        profilePhotoAdapter.setOnItemClickListener((adapter, view, position) -> {
            mBanner.post(() -> {
                mBanner.setCurrentItem(position + 1, true);
            });
        });

        mLottie = findViewById(R.id.animation_view);
        mLottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            final int colorBlack = Color.argb(255, 0, 0, 0);
            final int colorWhite = Color.argb(255, 255, 255, 255);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                {
                    int dy = Math.abs(i);
                    mToolbar.setSelected(dy > 10);
                    float alpha = Math.min(MAX_SCROLL, dy) / (float) MAX_SCROLL;
                    if (alpha > 0.5 && mUserInfo != null) {
                        mTvTobTitle.setText(mUserInfo.getNickname());
                    }
                    int backgroundAlpha = (int) (alpha * 255);
                    int backgroundBlack = Color.argb(backgroundAlpha, 0, 0, 0);
//                    if (backgroundAlpha > 150) {
//                        mTobMore.setImageResource(R.mipmap.ic_back_bg_black);
//                        mTobMore.setImageResource(R.mipmap.mine_ic_more);
//                    } else {
//                        mTobMore.setImageResource(R.mipmap.ic_back_bg_white);
//                        mTobMore.setImageResource(R.mipmap.mine_ic_more_white);
//                    }
                    mTobMore.setImageResource(R.mipmap.ic_back_bg_white);
                    mTobMore.setImageResource(R.mipmap.mine_ic_more_white);
                    mTvTobTitle.setTextColor(backgroundBlack);
//                    mTobMore.setColorFilter(backgroundBlack);
                    StatusBarUtil.setLightStatusBar(mActivity, backgroundAlpha > 150, true);
                }
            }
        });

        mIjkPlayer.setOnPlayListener(new OnPlayListener() {
            @Override
            public void onPrepared() {
                Utils.logE("mIjkPlayer - onPrepared");
                startTimer();
            }

            @Override
            public void onCompletion() {
                Utils.logE("mIjkPlayer - onCompletion");
                stopTimer();
            }

            @Override
            public void onInterrupt() {
                Utils.logE("mIjkPlayer - onInterrupt");
                stopTimer();
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onPlaying(long curPosition) {
            }
        });
    }

    private void startTimer() {
        mLottieVoice.setVisibility(View.VISIBLE);
        mLottieVoice.playAnimation();
        mIvVoice.setVisibility(View.GONE);

        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaTimeTemp > 0) {
                    mediaTimeTemp--;
                }
                if (mIjkPlayer != null && mIjkPlayer.isPlaying()) {
                    runOnUiThread(() -> mTvVoiceTime.setText(String.format("%ss", mediaTimeTemp)));
                }
            }
        };
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        mLottieVoice.setVisibility(View.GONE);
        mLottieVoice.cancelAnimation();
        mIvVoice.setVisibility(View.VISIBLE);
        GlideUtils.loadGif(mContext, mIvVoice, R.mipmap.mine_ic_voice_stop);
        mTvVoiceTime.setText(String.format("%ss", mediaTime));
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS || (event.type == Constants.NOTIFY_UPDATE_USER_INFO && TextUtils.isEmpty(mUserId))) {
                                p.getUserInfoDetail();
                            } else if (event.type == Constants.NOTIFY_SEND_TREND_SUCCESS || event.type == Constants.NOTIFY_DELETE_TREND_SUCCESS) {
                                p.getSelfTrend(3);
                            } else if (event.type == Constants.NOTIFY_EXIT_MINE_DETAIL)
                                finish();
                        }
                ));
    }

    private TimerTask mTimerTask;

    @Override
    protected int getLayout() {
        return R.layout.activity_mine_detail;
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {
        mUserInfo = userInfo;
        if (mPageManager != null)
            mPageManager.showContent();
        if (mUserInfo.isDisable()) { //如果用户被禁用
            /*if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.user_disable != null) {
                BaseWebViewActivity.start(mContext, null, service.getConfigInfo().share_config.user_disable.link + "&user_id=" + mUserId, 0, 0);
                finish();
            }*/
            UserBanPopWindow userBanPopWindow = new UserBanPopWindow(this);
            userBanPopWindow.showPopupWindow();
            //return;
        }
        if (mUserInfo.isLogout()) { //用户不存在
            new CustomPopWindow(mContext).setContent(getString(R.string.user_no_exist)).setRightGone().setRightButton("我知道了").showPopupWindow();
        }
        if (service.getUserInfo() != null) {
            if (TextUtils.isEmpty(mUserId)) { //如果是自己
                llBottom.setVisibility(View.VISIBLE);
                mLlOther.setVisibility(View.GONE);
                mTvEdit.setVisibility(View.VISIBLE);
            } else if ((service.getUserInfo().isGirl() && mUserInfo.isBoy()) //自己是女的 && 对方是男的
                    || (service.getUserInfo().isBoy() && mUserInfo.isGirl())) {
                llBottom.setVisibility(View.VISIBLE);
                mLlOther.setVisibility(View.VISIBLE);
                mTvEdit.setVisibility(View.GONE);

            } else {
                llBottom.setVisibility(View.GONE);
            }
        }
        mTvAccost.setText(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) || CommonUtil.infoBtnTextUpdate3(service) ? "喜欢" : "心动");
       /* if (!CommonUtil.infoBtnTextUpdate3(service)) {
            mIvAccost.setImageResource(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? R.mipmap.peony_xxym_sx_icon_small : R.mipmap.peony_home_xcsx_icon);
        }*/

        setUserInfo();
        if (TextUtils.isEmpty(mUserId)) {   //自己
            getP().getSelfPhoto(MAX_SIZE);
        } else {
            getP().getUserPhoto(MAX_SIZE, mUserId);
        }
        getP().getGiftList(TextUtils.isEmpty(mUserId) ? UserManager.getInstance().getUserId() + "" : mUserId);
    }

    @Override
    public void getGiftSuccess(List<GiftDto> data) {

    }

    /**
     * 成功获取礼物
     *
     * @param data
     */
    @Override
    public void getGiftSuccess(ArrayList<GiftDto> data) {
        gifts = data;
        if (null == data || data.size() <= 0) {
            clGift.setVisibility(View.GONE);
            mTvGift.setVisibility(View.GONE);
            mTvGiftVip.setVisibility(View.GONE);
        } else {
            if (mUserInfo != null && mUserInfo.open_hidden_gift == 1 && !TextUtils.isEmpty(mUserId)) {
                clGift.setVisibility(View.GONE);
                mTvGift.setVisibility(View.GONE);
                mTvGiftVip.setVisibility(View.GONE);
            } else {
                List<GiftDto> giftList = data.subList(0, 3);
                giftAdapter.setList(giftList);
                clGift.setVisibility(View.VISIBLE);
                mTvGift.setVisibility(View.VISIBLE);
                //fixme 礼物计数
                int count = 0;
                for (GiftDto dto : data) {
                    if (dto.number > 0) {
                        count++;
                    }
                }
                if (count <= 0) tvGiftTitle.setText(getString(R.string.receive_gift_empty));
                tvGiftCount.setText(count + "/" + data.size());
            }
        }
    }

    /**
     * 获取动态成功
     *
     * @param data
     */
    @Override
    public void getTrendSuccess(List<TrendDto> data) {
        final int size = data == null ? 0 : data.size();
        mTrendList.clear();
        if (TextUtils.isEmpty(mUserId)) {   //自己
            TrendDto trendDto = new TrendDto();
            trendDto.image = "ADD";
            mTrendList.add(trendDto);
        }
        LogUtil.e("========", "==" + size + "==" + mUserId);
        if (size > 0) {
            mTvTrendMore.setVisibility(View.VISIBLE);
            mTvTrend.setVisibility(View.VISIBLE);
            mTrendList.addAll(data);
        } else {
            if (TextUtils.isEmpty(mUserId)) {
                mTvTrendMore.setVisibility(View.VISIBLE);
                mTvTrend.setVisibility(View.VISIBLE);
            }
        }
        mTrendAdapter.setList(mTrendList);
    }

    /**
     * 获取相册成功
     *
     * @param data
     */
    @Override
    public void getPhotoSuccess(List<String> data) {
        List<String> list = new ArrayList<>();
        list.add(mUserInfo.getIcon());
        if (null != data) {
            list.addAll(data);
        }
        profilePhotoAdapter.setList(list);
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                Glide.with(mContext).load(list.get(i)).preload(mBanner.getWidth(), mBanner.getHeight());
            }
        }
        mBanner.setDatas(list);
    }

    /**
     * 搭讪用户成功
     *
     * @param data
     */
    @Override
    public void accostUserSuccess(AccostDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, mUserInfo.getUser_id()));
            CommonUtil.sendAccostGirlBoy(service, mUserInfo.getUser_id(), data, 3);
            if (data != null && data.gift != null) {
                Utils.playAccostAnimationAndSound(data.gift.name, data.gift.animation);
            }
            if (mConfig != null && mConfig.share_config != null && service.getUserInfo().getSex() != 1) {   //女性用户看其他人信息
                if (mConfig.share_config.is_detail_style_new == 1) {   //只显示私信
                    mUserInfo.setIs_accost(1);
                    mTvAccost.setText("私信");
                    mIvAccost.setImageResource(R.mipmap.peony_home_xcsx_icon);
                    mIvAccost.post(() -> mIvAccost.clearAnimation());
                }
            }
        }
    }

    @Override
    public void attentionSuccess(boolean isAttention) {
        mTvAttention.setVisibility(isAttention ? View.GONE : View.VISIBLE);
        toastTip(isAttention ? "关注成功，互相关注可互为好友" : "取消关注成功");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FOLLOW));
    }

    /**
     * 拉黑用户
     *
     * @param isBlack
     */
    @Override
    public void blackSuccess(boolean isBlack) {
        if (isBlack)
            toastTip("拉黑成功");
    }

    @Override
    public void cancelBlackSuccess(boolean isBlack) {
        if (isBlack)
            toastTip("解除拉黑成功");
    }

    /**
     * 真人认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getRealInfoSuccess(RealStatusInfoDto data) {
        if (data != null) {
            CommonUtil.performReal(data);
        }
    }

    /**
     * 实名认证状态
     *
     * @param data // -1.未认证,0.等待审核，1.审核完成，2.审核驳回
     */
    @Override
    public void getSelfInfoSuccess(RealStatusInfoDto data) {
        if (data != null) {
            if (mUserInfo.isPartyGirl()) {
                CommonUtil.performSelf(data, ARouterApi.ACTIVITY_SELF_CHECK, Interfaces.SHOW_IS_PARTY_SELF);
            } else {
                CommonUtil.performSelf(data);
            }
        }
    }

    /**
     * 获取是否可以拨打视频语音通话
     *
     * @param data
     */
    @Override
    public void getCallCheckSuccess(CallCheckDto data) {

    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data, boolean isAutoShowGiftPanel) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            if (isAutoShowGiftPanel) {
                p.showGiftPop(this, userId);
            } else {
                CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
            }
        }
    }

    @Override
    public void getCheckCallSuccess(String userId, CallCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            if (null == mVideoCallPopWindow)
                mVideoCallPopWindow = new VideoCallPopWindow(this, 3, userId);
            mVideoCallPopWindow.setData(data);
            mVideoCallPopWindow.addOnClickListener(type -> {
                RxBus.getDefault().post(new VoiceChatEvent(Constants.NOTIFY_EXIT_VOICE_ROOM));
                service.setMatchType("");
                ChatMsgUtil.callMessage(type, String.valueOf(service.getUserId()), mUserId, "", false);
            });
            mVideoCallPopWindow.showPopupWindow();
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
                    com.tftechsz.common.entity.GiftDto gift = JSON.parseObject(chatMsg.content, com.tftechsz.common.entity.GiftDto.class);
                    getP().sendGiftSuccess(gift, message);
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_TYPE)) {  //亲密度通知
                    ChatMsg.Intimacy intimacy = JSON.parseObject(chatMsg.content, ChatMsg.Intimacy.class);
                    //判断是否是礼物
                    if (!TextUtils.isEmpty(chatMsg.child)) {
                        ChatMsg chatMsgChild = JSON.parseObject(chatMsg.child, ChatMsg.class);
                        if (chatMsgChild != null && TextUtils.equals("gift_play", chatMsgChild.cmd_type)) {
                            com.tftechsz.common.entity.GiftDto gift = JSON.parseObject(chatMsgChild.content, com.tftechsz.common.entity.GiftDto.class);
                            getP().sendGiftSuccess(gift, message);
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

    /**
     * 设置个人用户信息
     */
    private void setUserInfo() {
        if (null == mUserInfo)
            return;
        if (!TextUtils.isEmpty(mUserInfo.audit_voice)) {
            mTvVoiceTime.setText("审核中");
            mIvVoice.setBackgroundResource(R.mipmap.mine_ic_voice_record);
            mLlVoice.setVisibility(View.VISIBLE);
        } else {
            if (TextUtils.isEmpty(mUserInfo.voice)) {   //没有音视频文件
                mIvVoice.setBackgroundResource(R.mipmap.mine_ic_voice_record);
                mTvVoiceTime.setText("录制语音");
                mLlVoice.setVisibility(TextUtils.isEmpty(mUserId) ? View.VISIBLE : View.GONE);
            } else {
                mIvVoice.setBackgroundResource(R.mipmap.mine_ic_voice_stop);
                mediaTime = Utils.filterTime(mUserInfo.voice_time);
                mediaTimeTemp = mediaTime;
                mTvVoiceTime.setText(mUserInfo.voice_time);
                mLlVoice.setVisibility(View.VISIBLE);
                resetMediaPlayer(mUserInfo.voice, TextUtils.equals(getLocalClassName(), AppManager.getAppManager().currentActivity().getLocalClassName()));
            }
        }

        if (userInfoAdapter != null) {
            List<UserInfo.BaseInfo> infoList = mUserInfo.info;
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(ConvertUtils.dp2px(12f));
            int max = 0;
            for (UserInfo.BaseInfo baseInfo : infoList) {
                float measuredWidth = textPaint.measureText(baseInfo.title);
                max = Math.max(max, Math.round(measuredWidth));
            }
            userInfoAdapter.setTextViewMaxWidth(max);
            userInfoAdapter.setList(infoList);
        }
        if (mUserInfo.levels != null && mUserInfo.levels.rich != null && mUserInfo.levels.charm != null) {
            mClLevel.setVisibility(View.VISIBLE);
            //土豪值
            GlideUtils.loadImage(this, ivLocalTyrantBg, mUserInfo.levels.rich.icon);
            mTvLocalTyrantTitle.setText(mUserInfo.levels.rich.title);
            mTvLocalTyrantLevel.setText(getString(R.string.rich_lv_format, mUserInfo.levels.rich.level));
            //魅力值
            GlideUtils.loadImage(this, ivCharmBg, mUserInfo.levels.charm.icon);
            mTvCharmTitle.setText(mUserInfo.levels.charm.title);
            mTvCharmLevel.setText(getString(R.string.charm_lv_format, mUserInfo.levels.charm.level));
        } else {
            mClLevel.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mUserId)) {   //自己
            if (mUserInfo.open_hidden_rank == 0) {
                tvLevelVip.setVisibility(View.GONE);
            } else {
                tvLevelVip.setVisibility(View.VISIBLE);
            }
            if (mUserInfo.open_hidden_gift == 0) {
                mTvGiftVip.setVisibility(View.GONE);
            } else {
                mTvGiftVip.setVisibility(View.VISIBLE);
            }
        } else {
            tvLevelTitle.setText(mUserInfo.isGirl() ? getString(R.string.level_her) : getString(R.string.level_his));
            tvGiftTitle.setText(mUserInfo.isGirl() ? getString(R.string.receive_gift_her) : getString(R.string.receive_gift_his));
            mTvLabel.setText(mUserInfo.isGirl() ? getString(R.string.label_her) : getString(R.string.label_his));
            mTvLabel.setCompoundDrawables(null, null, null, null);
        }

        setupUserTag();

//        mRlIsSelf.setVisibility(mUserInfo.isPartyGirl() ? View.GONE : View.VISIBLE);// 语音房女用户进入个人主页，不显示真人认证
        CommonUtil.setUserName(mTvName, mUserInfo.getNickname(), false, mUserInfo.isVip());
        if (TextUtils.isEmpty(mUserId)) {    //自己进入不传userId进入
            mTvSign.setText(TextUtils.isEmpty(mUserInfo.getDesc()) ? "填写交友宣言更容易获得别人关注哦~" : mUserInfo.getDesc());
        } else {   //他人
            mTvSign.setText(TextUtils.isEmpty(mUserInfo.getDesc()) ? "对方很懒，还没有交友宣言~" : mUserInfo.getDesc());
        }

        //性别  真人 是否认证
        mIvIsRealPeople.setVisibility(mUserInfo.getIs_real() == 1 ? View.VISIBLE : View.GONE);  //是否真人
        mIvIsAuth.setVisibility(mUserInfo.getIs_self() == 1 ? View.VISIBLE : View.GONE);  //是否实名
        mIvIsVIP.setVisibility(mUserInfo.isVip() ? View.VISIBLE : View.GONE);  //是否vip
        //1.男，2.女
        CommonUtil.setSexAndAge(mContext, mUserInfo.getSex(), String.valueOf(mUserInfo.getAge()), mIvSex);
        //关注
        mTvAttention.setVisibility(TextUtils.isEmpty(mUserId) || mUserInfo.is_follow == 1 ? View.GONE : View.VISIBLE);

        if (mUserInfo.getIs_real() == 1) {   //已经真人认证
            mllAuth.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mUserId)) {   //其他用户
            mTobMore.setVisibility(View.VISIBLE);
            mTvEditInfo.setVisibility(View.GONE);
            mllAuth.setVisibility(View.GONE);
        } else {
            mTobMore.setVisibility(View.GONE);
            mTvEditInfo.setVisibility(View.VISIBLE);
        }
        if (mConfig != null && mConfig.share_config != null && service.getUserInfo().getSex() != 1) {   //女性用户看其他人信息
            if (mConfig.share_config.is_detail_style_new == 1) {  //搭讪后显示私信
                if (mUserInfo.getIs_accost() == 0) {
                    mTvAccost.setText((CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? "喜欢" : "心动"));
                    mIvAccost.setImageResource((CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? R.mipmap.img_0_mainnew2 : R.mipmap.mine_ic_accost));
                    AnimationUtil.createAnimation(mIvAccost);
                } else {
                    mTvAccost.setText("私信");
                    mIvAccost.setImageResource(R.mipmap.peony_home_xcsx_icon);
                }
            } else if (CommonUtil.infoBtnTextUpdate3(service)) {
                if (mUserInfo.getIs_accost() == 0) {
                    mTvAccost.setText("喜欢");
                    mIvAccost.setImageResource(R.mipmap.img_0_mainnew2);
                } else {
                    mTvAccost.setText("私信");
                    mIvAccost.setImageResource(R.mipmap.peony_home_xcsx_icon);
                }
            } else {
                //获得配置失败 默认设置 搭讪
                if (mUserInfo.getIs_accost() == 0) {
                    mTvAccost.setText("心动");
                    mIvAccost.setImageResource(R.mipmap.mine_ic_accost);
                    AnimationUtil.createAnimation(mIvAccost);
                } else {
                    mTvAccost.setText("私信");
                    mIvAccost.setImageResource(R.mipmap.peony_home_xcsx_icon);
                }
            }
        }
    }

    /**
     * 播放录音
     */
    private void resetMediaPlayer(String url, boolean play) {
        if (mIjkPlayer == null) {
            mIjkPlayer = new AudioPlayer(BaseApplication.getInstance());
        }
        mIjkPlayer.setDataSource(url);
        if (play && !mUserInfo.isLogout()) {
            Utils.runOnUiThreadDelayed(() -> {
                if (mIjkPlayer != null && !mIjkPlayer.isPlaying()) {
                    mIjkPlayer.start(AudioManager.STREAM_MUSIC);
                }
            }, 200);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (null != mUserInfo && !TextUtils.isEmpty(mUserInfo.voice) && mIjkPlayer != null && mIjkPlayer.isPlaying()) {
            mIjkPlayer.stop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIjkPlayer != null) {
            mIjkPlayer.stop();
            mIjkPlayer = null;
        }
        stopTimer();
        if (mVideoCallPopWindow != null) {
            if (mVideoCallPopWindow.isShowing())
                mVideoCallPopWindow.dismiss();
            mVideoCallPopWindow = null;
        }
        if (mLottie != null) {
            mLottie.clearAnimation();
            mLottie = null;
        }
        if (mTrendList != null) {
            mTrendList.clear();
            mTrendList = null;
        }
        gestureDetector = null;

        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, false);
    }

    @Override
    public void onClick(View v) {
        if (isFastClick()) return;
        int id = v.getId();
        if (id == R.id.tob_back) {     //返回按钮
            finish();
            return;
        }
        if (null == mUserInfo)
            return;
        if (id == R.id.ll_auth) {  //是否真人
            p.getRealInfo();
        } else if (id == R.id.tv_edit || id == R.id.edit_info) {   //编辑资料
            MineInfoActivity.startActivity(this, mUserInfo);
        } else if (id == R.id.tv_mine_trend) {   //查看更多动态
            ARouterUtils.toTrendActivity(mUserId, mUserInfo.getNickname());
        } else if (id == R.id.tv_attention) {
            followOrUnfollow();
        } else if (id == R.id.cl_local_tyrant) {    //土豪值
//            if (!TextUtils.isEmpty(mUserId))
//                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.rich) {
                MyWealthCharmLevelActivity.startActivity(this, "0", mUserInfo.getSex() + "", mUserId); //用户性别：0.未知，1.男，2.女
            }
        } else if (id == R.id.cl_charm) {    //魅力值
//            if (!TextUtils.isEmpty(mUserId))
//                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.charm) {
                MyWealthCharmLevelActivity.startActivity(this, "1", mUserInfo.getSex() + "", mUserId); //用户性别：0.未知，1.男，2.女
            }
        } else if (id == R.id.ll_accost) {   //搭讪
            if (!isPerformClick()) {
                return;
            }
            if (service.getUserInfo() != null) {
                if (service.getUserInfo().isBoy()) { //男性逻辑
                    p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                    setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                } else {
                    if (mConfig != null && mConfig.share_config != null && mConfig.share_config.is_detail_style_new == 1) {   //女性用户看其他人信息
                        if (mUserInfo.getIs_accost() == 0) {
                            p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                            setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                        } else {
                            p.getMsgCheck(mUserId, false);
                        }
                    } else if (CommonUtil.infoBtnTextUpdate3(service)) {   //搭讪
                        p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                        setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                    } else {
                        if (mUserInfo.getIs_accost() == 0) {
                            p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                            setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                        } else {
                            p.getMsgCheck(mUserId, false);
                        }
                    }
                }
            }
        } else if (id == R.id.tv_chat_message) {   //私信
            if (!isPerformClick()) {
                return;
            }
//            int isAccost = SPUtils.getInt(Constants.IS_FIRST_ACCOST, 0);
//            if (service.getUserInfo().getSex() == 2 && isAccost == 0) {
//                CommonUtil.showFirstAccostPop();
//                return;
//            }
            p.getMsgCheck(mUserId, false);
//            ARouterUtils.toChatP2PActivity(mUserInfo.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
        } else if (id == R.id.ll_voice) {  //去录音
            if (!TextUtils.isEmpty(mUserId)) {
                return;
            }
            if (!TextUtils.isEmpty(mUserInfo.audit_voice)) {
                toastTip("语音签名正在审核中");
                return;
            }
            if (TextUtils.isEmpty(mUserInfo.voice))
                startActivity(VoiceSignActivity.class);
        } else if (id == R.id.iv_voice || id == R.id.lottie_voice) {
            if (!TextUtils.isEmpty(mUserInfo.audit_voice)) {
                toastTip("语音签名正在审核中");
                return;
            }
            if (TextUtils.isEmpty(mUserInfo.voice)) {
                if (TextUtils.isEmpty(mUserId) && TextUtils.isEmpty(mUserInfo.voice)) {   //自己才可以录制
                    startActivity(VoiceSignActivity.class);
                }
            } else {
                if (mIjkPlayer != null) {
                    if (mIjkPlayer.isPlaying()) {
                        mIjkPlayer.stop();
                    } else {
                        mediaTimeTemp = mediaTime;
                        mIjkPlayer.start(AudioManager.STREAM_MUSIC);
                    }
                }
            }
        } else if (id == R.id.tv_call_video) {  //语音视频聊天
            if (!isPerformClick()) {
                return;
            }
            p.getCallCheck(mUserId);
        } else if (id == R.id.tob_more) {  //举报拉黑
            MineDetailMorePopWindow popWindow = new MineDetailMorePopWindow(this, mUserId);
            popWindow.setAttentionVisible(mTvAttention.getVisibility() == View.GONE);
            popWindow.addOnClickListener(new MineDetailMorePopWindow.OnSelectListener() {
                @Override
                public void reportUser() {
                    ARouterUtils.toBeforeReportActivity(Integer.parseInt(mUserId), 1);
                }

                @Override
                public void blackUser(boolean isBlack) {
                    if (isBlack) {
                        getP().cancelBlack(Integer.parseInt(mUserId));
                    } else
                        getP().showBlackPop(mContext, Integer.parseInt(mUserId));
                }

                @Override
                public void unfollow() {
                    followOrUnfollow();
                }
            });
            popWindow.setPopupGravityMode(BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE);
            popWindow.showPopupWindow(mTobMore);
        } else if (id == R.id.rl_guard) {   //守护

            GuardPopWindow popWindow = new GuardPopWindow(mActivity);
            popWindow.showPopupWindow();

        } else if (id == R.id.iv_profile_add) { //查看/添加更多照片
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_PHOTO);
        } else if (id == R.id.tv_mine_gift) {//礼物
            p.getMsgCheck(mUserId, true);
        } else if (id == R.id.tv_label) {
            if (!TextUtils.isEmpty(mUserId))
                return;
            ARouter.getInstance().build(ARouterApi.ACTIVITY_CHOOSE_TAG).withString("from", "info").navigation();
        }
    }

    private void followOrUnfollow() {
        if (mUserInfo == null) return;
        p.attentionUser(mUserInfo.getUser_id());
    }

    private boolean isPerformClick() {
        if (mUserInfo == null) {
            return false;
        }
        if (mUserInfo.isDisable()) {
            Utils.toast(R.string.user_is_disable);
            return false;
        }
        if (mUserInfo.isLogout()) {
            Utils.toast(R.string.user_no_exist);
            return false;
        }
        return true;
    }

    /**
     * 搭讪按钮点击事件
     *
     * @param type 1 搭讪 2喜欢 3打招呼
     */
    private void setAccostClickLog(int type, int toUserId) {
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("搭讪按钮点击事件", "accost_click", "", JSON.toJSONString(new NavigationLogEntity(CommonUtil.getOSName(), Constants.APP_NAME, service.getUserId(), toUserId + "", type, 2, System.currentTimeMillis())), null);

    }

    private void initLabelView() {
        if (labelDisplayAdapter == null) {
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            layoutManager.setAlignItems(AlignItems.FLEX_START);
            rvLabel.setLayoutManager(layoutManager);
            labelDisplayAdapter = new LabelDisplayAdapter();
            rvLabel.setAdapter(labelDisplayAdapter);
            labelDisplayAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (adapter.getItemViewType(position) == LabelDisplayAdapter.TYPE_ADD) {
                    ARouter.getInstance().build(ARouterApi.ACTIVITY_CHOOSE_TAG).withString("from", "info").navigation();
                }
            });
        }
    }
}
