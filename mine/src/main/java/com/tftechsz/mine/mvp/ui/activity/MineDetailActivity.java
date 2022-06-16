package com.tftechsz.mine.mvp.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
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
import com.tftechsz.common.event.VoiceChatEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.ScreenUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.MyBannerImageHolder;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.ReportPopWindow;
import com.tftechsz.common.widget.pop.VideoCallPopWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.BaseUserInfoAdapter;
import com.tftechsz.mine.adapter.GiftAdapter;
import com.tftechsz.mine.adapter.PhotoAdapter;
import com.tftechsz.mine.adapter.TrendAdapter;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.entity.dto.TrendDto;
import com.tftechsz.mine.mvp.IView.IMineDetailView;
import com.tftechsz.mine.mvp.presenter.MineDetailPresenter;
import com.tftechsz.mine.widget.pop.GuardPopWindow;
import com.tftechsz.mine.widget.pop.MineDetailMorePopWindow;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 个人资料
 */
@Route(path = ARouterApi.ACTIVITY_MINE_DETAIL)
public class MineDetailActivity extends BaseMvpActivity<IMineDetailView, MineDetailPresenter> implements IMineDetailView, View.OnClickListener {
    private final int MAX_SIZE = 9;

    private Banner mBanner;
    private TextView mTvCurrentNum;
    private ImageView mIvAvatar;   //头像
    private ImageView mIvBack, mTobBack, mTobMore;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mTvTobTitle;
    private TextView mTvName, mTvSign;    //姓名,个性签名
    private TextView mTvLocalTyrantTitle, mTvLocalTyrantValue, mTvLocalTyrantLevel;    //土豪值相关
    private TextView mTvCharmTitle, mTvCharmValue, mTvCharmLevel;   //魅力值相关

    private TextView mTvAttention;   //关注
    private TextView mTvEdit;   //编辑资料
    private LinearLayout mLlOther;  //他人

    private TextView mIvSex;  //性别，是否真人，是否认证
    private ImageView mIvIsRealPeople, mIvIsAuth;

    //女性用户逻辑私信
    private LinearLayout mLlAccost, mLlOperate, llBottom;
    private TextView mTvChatMessage, mTvAccost;
    private ImageView mIvAccost;

    private TextView mTvIsReal;   //是否实名
    private TextView mTvRealStatus;
    private TextView mTvRealTip;
    private TextView mTvIsSelf;   //是否真人
    private TextView mTvSelfStatus;
    private TextView mTvSelfTip;

    private RecyclerView mRvTrend;   //动态
    private RecyclerView mRvUserInfo;  //基本信息
    private RecyclerView mRvGift;  //礼物
    private RecyclerView mRvPhoto;  //相册

    private ConstraintLayout mClPhoto;   //相册

    private TrendAdapter mTrendAdapter;   //动态

    private PhotoAdapter mPhotoAdapter;   //相册

    private UserInfo mUserInfo;
    private int MAX_SCROLL;

    private ConstraintLayout mLlVoice;
    private TextView mTvVoiceTime;  //录音
    private ImageView mIvVoice;

    private AudioPlayer mIjkPlayer;

    private TextView mTvTrend, mTvTrendMore;   //动态

    private List<TrendDto> mTrendList;
    private List<String> mPhotoList;
    private LottieAnimationView mLottie;
    private VideoCallPopWindow mVideoCallPopWindow;   //语音视频弹窗

    //会员相关
    private LinearLayout mClVipRich;
    private ConstraintLayout mClVipGift;
    private TextView mTvRichSetting, mTvGiftSetting;

    private TextView mTvGift;

    private ConfigInfo mConfig;

    private GestureDetector gestureDetector;
    private GestureDetector gesturePhoto;
    @Autowired(name = "user_id")
    public String mUserId;

    @Autowired
    UserProviderService service;
    private Timer mTimer;
    private int mediaTime, mediaTimeTemp;
    private PageStateManager mPageManager;
    private ConstraintLayout constraintLayoutFamily;
    private View mViewLineVisible;
    private ImageView mImgFamily;
    private TextView mTvFamilyName, mTvFamilyHint, mTvFamilyWw, mTvFamilyNumber, mTxtFamilyOtherText;
    private LottieAnimationView mLottieVoice;
    private RelativeLayout mRelRoomAudio;
    private ImageView mImgHeaders;
    private RelativeLayout mRlIsSelf;

    @Override
    public MineDetailPresenter initPresenter() {
        return new MineDetailPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .init();
        mRelRoomAudio = findViewById(R.id.rel_room_audio);
        mRelRoomAudio.setOnClickListener(this);
        mTxtFamilyOtherText = findViewById(R.id.tv_photofamily);
        mTvFamilyWw = findViewById(R.id.tv_family_respect);
        mTvFamilyNumber = findViewById(R.id.tv_family_number);
        mImgHeaders = findViewById(R.id.img_agl_);
        mTvFamilyHint = findViewById(R.id.tv_family_hint);
        mTvFamilyName = findViewById(R.id.tv_amd_family_title);
        mImgFamily = findViewById(R.id.iv_familyimg);
        mViewLineVisible = findViewById(R.id.view13);
        constraintLayoutFamily = findViewById(R.id.cl_family);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvBack = findViewById(R.id.iv_back);
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        mToolbar = findViewById(R.id.toolbar);
        mTobBack = findViewById(R.id.tob_back);
        mLottieVoice = findViewById(R.id.lottie_voice);
        llBottom = findViewById(R.id.ll_bottom);
        mRlIsSelf = findViewById(R.id.rl_is_self);

        //banner
        mBanner = findViewById(R.id.banner);
        mTvCurrentNum = findViewById(R.id.tv_current_num);

        mTvTobTitle = findViewById(R.id.tob_title);  //名称
        mTobMore = findViewById(R.id.tob_more);
        mTvName = findViewById(R.id.tv_name);   //姓名
        mTvSign = findViewById(R.id.tv_sign);   //个性签名
        //土豪值response
        mTvLocalTyrantTitle = findViewById(R.id.tv_local_tyrant_title);
        mTvLocalTyrantValue = findViewById(R.id.tv_local_tyrant_value);
        mTvLocalTyrantLevel = findViewById(R.id.tv_local_tyrant_level);
        //魅力值
        mTvCharmTitle = findViewById(R.id.tv_charm_title);
        mTvCharmValue = findViewById(R.id.tv_charm_value);
        mTvCharmLevel = findViewById(R.id.tv_charm_level);

        mClPhoto = findViewById(R.id.cl_photo);

        mTvEdit = findViewById(R.id.tv_edit);  //编辑资料
        mTvEdit.setOnClickListener(this);

        //是否实名
        mTvIsSelf = findViewById(R.id.tv_is_self);
        mTvSelfStatus = findViewById(R.id.tv_self_status);
        mTvSelfTip = findViewById(R.id.tv_self_tip);
        //是否真人
        mTvIsReal = findViewById(R.id.tv_is_real);
        mTvRealStatus = findViewById(R.id.tv_real_status);
        mTvRealTip = findViewById(R.id.tv_real_tip);

        mLlAccost = findViewById(R.id.ll_accost);
        mLlOperate = findViewById(R.id.ll_operate);
        mTvChatMessage = findViewById(R.id.tv_chat_message);
        mTvAccost = findViewById(R.id.tv_accost);

        //性别，真人，认证
        mIvSex = findViewById(R.id.iv_sex);
        mIvIsRealPeople = findViewById(R.id.iv_real_people);
        mIvIsAuth = findViewById(R.id.iv_auth);

        mLlOther = findViewById(R.id.ll_other);  //他人

        mTvAttention = findViewById(R.id.tv_attention);  //关注

        mLlVoice = findViewById(R.id.ll_voice);
        mTvVoiceTime = findViewById(R.id.tv_voice_time);   //录音
        mIvVoice = findViewById(R.id.iv_voice);

        mTvTrend = findViewById(R.id.tv_trend);
        mTvTrendMore = findViewById(R.id.tv_mine_trend);  //动态

        //心
        //搭讪
        mIvAccost = findViewById(R.id.iv_accost);
        //动态
        mRvTrend = findViewById(R.id.rv_trend);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(this, 4);
        mRvTrend.setLayoutManager(gridLayoutManager2);

        //相册
        mRvPhoto = findViewById(R.id.rv_photo);
        GridLayoutManager gridLayoutManager3 = new GridLayoutManager(this, 4);
        mRvPhoto.setLayoutManager(gridLayoutManager3);

        //用户基本信息
        mRvUserInfo = findViewById(R.id.rv_user_info);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRvUserInfo.setLayoutManager(gridLayoutManager);

        //礼物
        mRvGift = findViewById(R.id.rv_gift);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(this, 4);
        mRvGift.setLayoutManager(gridLayoutManager1);


        //vip相关
        mClVipRich = findViewById(R.id.cl_vip_rich);
        mClVipGift = findViewById(R.id.cl_vip_gift);
        mTvGiftSetting = findViewById(R.id.tv_gift_vip_setting);
        mTvRichSetting = findViewById(R.id.tv_rich_vip_setting);

        mTvGift = findViewById(R.id.tv_gift);

        setSupportActionBar(mToolbar);
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mTobBack.setOnClickListener(this);
        mTvAttention.setOnClickListener(this);
        mTobMore.setOnClickListener(this);
        mLottieVoice.setOnClickListener(this);
        mLlAccost.setOnClickListener(this);   //搭讪
        findViewById(R.id.tv_chat_message).setOnClickListener(this);  //私信
        findViewById(R.id.cl_local_tyrant).setOnClickListener(this);  //土豪值
        findViewById(R.id.tv_call_video).setOnClickListener(this);   //语音视频
        findViewById(R.id.cl_charm).setOnClickListener(this);  //亲密度
        findViewById(R.id.rl_guard).setOnClickListener(this);   //守护
        findViewById(R.id.cl_family).setOnClickListener(this);   //家族

        mLlVoice.setOnClickListener(this);  //去录音
        mIvVoice.setOnClickListener(this);
        mTvIsReal.setOnClickListener(this);   //是否实名
        mTvRealStatus.setOnClickListener(this);
        findViewById(R.id.tv_more_photo).setOnClickListener(this);   //更多
        mTvTrendMore.setOnClickListener(this);   //更多动态
        //是否实名
        mTvIsSelf.setOnClickListener(this);
        mTvSelfStatus.setOnClickListener(this);
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
        gesturePhoto = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (TextUtils.isEmpty(mUserId)) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_PHOTO);
                }
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
        findViewById(R.id.ns_info).setMinimumHeight(ScreenUtils.getScreenHeight(this));

    }

    private View mViewPot;

    private void initNet() {
        if (TextUtils.isEmpty(mUserId)) {   //自己
            //默认添加第一条数据
            TrendDto trendDto = new TrendDto();
            trendDto.image = "ADD";
            mTrendList.add(trendDto);
            mPhotoList.add("ADD");
            p.getUserInfoDetail();
            p.getSelfGift(mPageSize);
            p.getSelfTrend(3);
        } else {
            mViewPot = findViewById(R.id.view1);
            mClPhoto.setVisibility(View.GONE);   //相册
            mViewPot.setVisibility(View.GONE);
            mTvSelfTip.setVisibility(View.GONE);
            mTvRealTip.setVisibility(View.GONE);

            p.getUserInfoById(mUserId);
            p.getUserGift(mPageSize, mUserId);
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
            //vip 设置相关
            mClVipRich.setBackgroundResource(0);
            mClVipGift.setBackgroundResource(0);
            mTvRichSetting.setVisibility(View.GONE);
            mTvGiftSetting.setVisibility(View.GONE);

        }
    }

    @Override
    protected void initData() {
        super.initData();
        mTrendList = new ArrayList<>();
        mPhotoList = new ArrayList<>();
        MAX_SCROLL = DensityUtils.dp2px(this, 340);
        mConfig = service.getConfigInfo();
        mIjkPlayer = new AudioPlayer(BaseApplication.getInstance());
        setSupportActionBar(findViewById(R.id.toolbar));
        initRxBus();

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

        mLottie = findViewById(R.id.animation_view);
        mLottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
        mTrendAdapter = new TrendAdapter(mTrendList, mUserId);
        mRvTrend.setAdapter(mTrendAdapter);
        mRvTrend.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        mPhotoAdapter = new PhotoAdapter(mPhotoList);
        mRvPhoto.setAdapter(mPhotoAdapter);
        mRvPhoto.setOnTouchListener((v, event) -> gesturePhoto.onTouchEvent(event));
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            int dy = Math.abs(i);
            mToolbar.setSelected(dy > 10);
            float alpha = Math.min(MAX_SCROLL, dy) / (float) MAX_SCROLL;
            if (alpha > 0.5 && mUserInfo != null) {
                mTvTobTitle.setText(mUserInfo.getNickname());
            }
            int backgroundAlpha = (int) (alpha * 255);
            int backgroundBlack = Color.argb(backgroundAlpha, 0, 0, 0);
            int backgroundColor = Color.argb(backgroundAlpha, 255, 255, 255);
            if (backgroundAlpha > 150) {
                mTobBack.setImageResource(R.mipmap.mine_ic_back);
                mTobMore.setImageResource(R.mipmap.mine_ic_more);
            } else {
                mTobBack.setImageResource(R.mipmap.mine_ic_back_white);
                mTobMore.setImageResource(R.mipmap.mine_ic_more_white);
            }
            mTvTobTitle.setTextColor(backgroundBlack);
            mIvBack.setColorFilter(backgroundColor);
            mTobBack.setColorFilter(backgroundBlack);
            mTobMore.setColorFilter(backgroundBlack);
            if (backgroundAlpha > 150) {
                StatusBarUtil.setLightStatusBar(mActivity, true, true);
            } else {
                StatusBarUtil.setLightStatusBar(mActivity, false, true);
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
            new CustomPopWindow(mContext).setContent(getString(R.string.user_is_disable)).setRightGone().setRightButton("我知道了").showPopupWindow();
            //return;
        }
        if (mUserInfo.isLogout()) { //用户不存在
            new CustomPopWindow(mContext).setContent(getString(R.string.user_no_exist)).setRightGone().setRightButton("我知道了").showPopupWindow();
        }
        //显示派对语音房 样式
        if (mUserInfo.play_party != null && mUserInfo.play_party.id != 0 && mUserInfo.play_party.room_name != null && mUserInfo.play_party.icon != null) {
            mRelRoomAudio.setVisibility(View.VISIBLE);
            GlideUtils.loadCircleImage(this, mImgHeaders, mUserInfo.play_party.icon);
        } else {
            mRelRoomAudio.setVisibility(View.GONE);
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
        mTvAccost.setText(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) || CommonUtil.infoBtnTextUpdate3(service) ? "喜欢" : "搭讪");
       /* if (!CommonUtil.infoBtnTextUpdate3(service)) {
            mIvAccost.setImageResource(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? R.mipmap.peony_xxym_sx_icon_small : R.mipmap.peony_home_xcsx_icon);
        }*/

        setUserInfo();
        if (TextUtils.isEmpty(mUserId)) {   //自己
            getP().getSelfPhoto(MAX_SIZE);
        } else {
            getP().getUserPhoto(MAX_SIZE, mUserId);
        }
        //家族
        if (userInfo.family != null) {
            GlideUtils.loadRoundImage(this, mImgFamily, userInfo.family.icon);
            mTvFamilyName.setText(userInfo.family.tname);
            mTvFamilyNumber.setText(userInfo.family.userCount + "人");
            if (!StringUtils.isTrimEmpty(userInfo.family.prestige)) {
                mTvFamilyWw.setVisibility(View.VISIBLE);
                mTvFamilyWw.setText("总威望值" + userInfo.family.prestige);
            }
            if (!StringUtils.isTrimEmpty(userInfo.family.intro)) {
                mTvFamilyHint.setText(userInfo.family.intro);
            }

            ImageView mIvLevel = findViewById(R.id.iv_level);
            TextView mTvLevel = findViewById(R.id.tv_level);
            if (!StringUtils.isTrimEmpty(userInfo.family.level) && !StringUtils.isTrimEmpty(userInfo.family.level_icon)) {
                mTvLevel.setText(userInfo.family.level);
                GlideUtils.loadRouteImage(this, mIvLevel, userInfo.family.level_icon);
            }
            mViewLineVisible.setVisibility(View.VISIBLE);
            constraintLayoutFamily.setVisibility(View.VISIBLE);
        } else {
            mViewLineVisible.setVisibility(View.GONE);
            constraintLayoutFamily.setVisibility(View.GONE);
        }
    }

    /**
     * 成功获取礼物
     *
     * @param data
     */
    @Override
    public void getGiftSuccess(List<GiftDto> data) {
        GiftAdapter adapter = new GiftAdapter(data);
        mRvGift.setAdapter(adapter);
        if (null == data || data.size() <= 0) {
            mClVipGift.setVisibility(View.GONE);
            mTvGift.setVisibility(View.GONE);
            mClVipGift.setBackgroundResource(0);
            mTvGiftSetting.setVisibility(View.GONE);
        } else {
            mClVipGift.setVisibility(View.VISIBLE);
            mTvGift.setVisibility(View.VISIBLE);
            findViewById(R.id.view4).setVisibility(View.VISIBLE);
        }

        if (mUserInfo != null && mUserInfo.open_hidden_gift == 1 && !TextUtils.isEmpty(mUserId)) {
            mClVipGift.setVisibility(View.GONE);
            mTvGift.setVisibility(View.GONE);
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
        mPhotoList.clear();
        if (TextUtils.isEmpty(mUserId)) {   //自己
            mPhotoList.add("ADD");
        }
        if (null != data) {
            list.addAll(data);
            mPhotoList.addAll(data);
        }
        mPhotoAdapter.setList(mPhotoList);
        if (list.size() > 1) {
            mTvCurrentNum.setVisibility(View.VISIBLE);
            mTvCurrentNum.setText("1/" + list.size());
            for (int i = 0; i < list.size(); i++) {
                Glide.with(mContext).load(list.get(i)).preload(mBanner.getWidth(), mBanner.getHeight());
            }
        } else {
            mTvCurrentNum.setVisibility(View.GONE);
        }
        mBanner.setAdapter(new MyBannerImageAdapter<String>(list) {

            @Override
            public void onBindView(MyBannerImageHolder holder, String data, int position, int size) {
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                GlideUtils.loadPicImage(mContext, holder.imageView, data);

            }
        })
                .addOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (list.size() <= 1) {
                            mTvCurrentNum.setVisibility(View.GONE);
                        } else {
                            mTvCurrentNum.setVisibility(View.VISIBLE);
                            mTvCurrentNum.setText((position + 1) + "/" + list.size());
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }).setOnBannerListener((data1, position) -> {
            ARouterUtils.toUserPicBrowserActivity(!TextUtils.isEmpty(mUserId) ? Integer.parseInt(mUserId) : service.getUserId(), position, list.get(0), (mUserInfo != null && mUserInfo.isBoy()));
        });
        mBanner.addBannerLifecycleObserver(this);
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
            if (service.getUserInfo().getSex() == 1 && data != null) {
                CommonUtil.playJsonAnimation(mLottie, data.gift.animation, 0);
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
        Drawable drawable;
        if (isAttention) {
            drawable = ContextCompat.getDrawable(this, R.mipmap.mine_ic_attention_selector);
        } else {
            drawable = ContextCompat.getDrawable(this, R.mipmap.mine_ic_attention_normal);
        }
        mTvAttention.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
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
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
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
     * 设置个人用户信息
     */
    private void setUserInfo() {
        if (null == mUserInfo)
            return;
        mTxtFamilyOtherText.setText(String.format("%s的家族", TextUtils.isEmpty(mUserId) ? "我" : mUserInfo.isBoy() ? "他" : "她"));
        if (!TextUtils.isEmpty(mUserInfo.audit_voice)) {
            mTvVoiceTime.setText("审核中");
            mIvVoice.setBackgroundResource(R.mipmap.mine_ic_voice_record);
            mLlVoice.setVisibility(View.VISIBLE);
        } else {
            if (TextUtils.isEmpty(mUserInfo.voice)) {   //没有音视频文件
                mIvVoice.setBackgroundResource(R.mipmap.mine_ic_voice_record);
                mTvVoiceTime.setText("去录制");
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

        mRlIsSelf.setVisibility(mUserInfo.isPartyGirl() ? View.GONE : View.VISIBLE);// 语音房女用户进入个人主页，不显示真人认证
        CommonUtil.setUserName(mTvName, mUserInfo.getNickname(), mUserInfo.isVip(), TextUtils.isEmpty(mUserId));
        mTvName.setText(mUserInfo.getNickname());
        GlideUtils.loadImage(this, mIvAvatar, mUserInfo.getIcon(), mUserInfo.getSex() == 1 ? R.mipmap.mine_ic_big_boy_default : R.mipmap.mine_ic_big_girl_default);   //头像
        if (TextUtils.isEmpty(mUserId)) {    //自己进入不传userId进入
            mTvSign.setText(TextUtils.isEmpty(mUserInfo.getDesc()) ? "填写交友宣言更容易获得别人关注哦~" : mUserInfo.getDesc());
        } else {   //他人
            mTvSign.setText(TextUtils.isEmpty(mUserInfo.getDesc()) ? "对方很懒，还没有交友宣言~" : mUserInfo.getDesc());
        }
        if (mUserInfo.levels != null && mUserInfo.levels.rich != null && mUserInfo.levels.charm != null) {
            mClVipRich.setVisibility(View.VISIBLE);
            //土豪值
            mTvLocalTyrantTitle.setText(mUserInfo.levels.rich.title);
            mTvLocalTyrantValue.setText(mUserInfo.levels.rich.value);
            mTvLocalTyrantLevel.setText(mUserInfo.levels.rich.level);
            //魅力值
            mTvCharmTitle.setText(mUserInfo.levels.charm.title);
            mTvCharmValue.setText(mUserInfo.levels.charm.value);
            mTvCharmLevel.setText(mUserInfo.levels.charm.level);
        } else {
            mClVipRich.setVisibility(View.GONE);
        }
        //性别  真人 是否认证
        mIvIsRealPeople.setVisibility(mUserInfo.getIs_real() == 1 ? View.VISIBLE : View.GONE);  //是否真人
        mIvIsAuth.setVisibility(mUserInfo.getIs_self() == 1 ? View.VISIBLE : View.GONE);  //是否实名
        //1.男，2.女
        CommonUtil.setSexAndAge(mContext, mUserInfo.getSex(), String.valueOf(mUserInfo.getAge()), mIvSex);
        //关注
        Drawable attention = ContextCompat.getDrawable(this, R.mipmap.mine_ic_attention_selector);
        Drawable notAttention = ContextCompat.getDrawable(this, R.mipmap.mine_ic_attention_normal);
        mTvAttention.setCompoundDrawablesWithIntrinsicBounds(null,
                mUserInfo.is_follow == 1 ? attention : notAttention, null, null);

        if (mUserInfo.getIs_self() == 1) {   //已经是否实名
            mTvIsSelf.setText("已完成实名认证");
            mTvSelfStatus.setText("已认证");
            mTvSelfStatus.setEnabled(false);
        } else {
            mTvIsSelf.setText("未实名认证");
            mTvSelfStatus.setText("去认证");
        }
        if (mUserInfo.getIs_real() == 1) {   //已经真人认证
            mTvIsReal.setText("已完成真人认证");
            mTvRealStatus.setText("已认证");
            mTvRealStatus.setEnabled(false);
        } else {
            mTvIsReal.setText("未真人认证");
            mTvRealStatus.setText("去认证");
        }
        if (!TextUtils.isEmpty(mUserId)) {   //其他用户
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.mine_ic_white_authenticate);
            if (mUserInfo.getIs_real() == 1) {   //真人
                mTvRealStatus.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvRealStatus.setCompoundDrawablePadding(DensityUtils.dp2px(this, 5));
                mTvRealStatus.setBackgroundResource(R.drawable.bg_other_authenticate);
                mTvRealStatus.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                mTvRealStatus.setVisibility(View.GONE);
            }
            if (mUserInfo.getIs_self() == 1) {
                mTvSelfStatus.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvSelfStatus.setCompoundDrawablePadding(DensityUtils.dp2px(this, 5));
                mTvSelfStatus.setBackgroundResource(R.drawable.bg_other_authenticate);
                mTvSelfStatus.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                mTvSelfStatus.setVisibility(View.GONE);
            }
            mTobMore.setVisibility(View.VISIBLE);

        } else {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.mine_ic_gray_authenticate);
            if (mUserInfo.getIs_real() == 1) {
                mTvRealStatus.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvRealStatus.setCompoundDrawablePadding(DensityUtils.dp2px(this, 5));
                mTvRealStatus.setBackgroundResource(R.drawable.bg_gray_authenticate);
            }
            if (mUserInfo.getIs_self() == 1) {
                mTvSelfStatus.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvSelfStatus.setCompoundDrawablePadding(DensityUtils.dp2px(this, 5));
                mTvSelfStatus.setBackgroundResource(R.drawable.bg_gray_authenticate);
            }
            mTobMore.setVisibility(View.GONE);

            //vip 设置相关
            if (mUserInfo.open_hidden_rank == 0) {
                mClVipRich.setBackgroundResource(0);
                mTvRichSetting.setVisibility(View.GONE);
            } else {
                mClVipRich.setBackgroundResource(R.drawable.bg_vip_rich);
                mTvRichSetting.setVisibility(View.VISIBLE);
            }
            if (mUserInfo.open_hidden_gift == 0) {
                mClVipGift.setBackgroundResource(0);
                mTvGiftSetting.setVisibility(View.GONE);
            } else {
                mClVipGift.setBackgroundResource(R.drawable.bg_vip_rich);
                mTvGiftSetting.setVisibility(View.VISIBLE);
            }
        }
        if (mConfig != null && mConfig.share_config != null && service.getUserInfo().getSex() != 1) {   //女性用户看其他人信息
            if (mConfig.share_config.is_detail_style_new == 1) {  //搭讪后显示私信
                if (mUserInfo.getIs_accost() == 0) {
                    mTvAccost.setText((CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? "喜欢" : "搭讪"));
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
                    mTvAccost.setText("搭讪");
                    mIvAccost.setImageResource(R.mipmap.mine_ic_accost);
                    AnimationUtil.createAnimation(mIvAccost);
                } else {
                    mTvAccost.setText("私信");
                    mIvAccost.setImageResource(R.mipmap.peony_home_xcsx_icon);
                }
            }
        }
        mIvIsRealPeople = findViewById(R.id.iv_real_people);
        mIvIsAuth = findViewById(R.id.iv_auth);
        //用户基本信息
        BaseUserInfoAdapter adapter = new BaseUserInfoAdapter(mUserInfo.info, mUserInfo.is_show_icon);
        mRvUserInfo.setAdapter(adapter);
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
        if (mBanner != null)
            mBanner.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBanner != null)
            mBanner.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIjkPlayer != null) {
            mIjkPlayer.stop();
            mIjkPlayer = null;
        }
        stopTimer();
        if (mBanner != null)
            mBanner.destroy();
        if (mTrendList != null) {
            mTrendList.clear();
            mTrendList = null;
        }
        if (mPhotoList != null) {
            mPhotoList.clear();
            mPhotoList = null;
        }
        if (mVideoCallPopWindow != null) {
            if (mVideoCallPopWindow.isShowing())
                mVideoCallPopWindow.dismiss();
            mVideoCallPopWindow = null;
        }
        if (mLottie != null) {
            mLottie.clearAnimation();
            mLottie = null;
        }
        gestureDetector = null;
        gesturePhoto = null;
        mRvTrend = null;
        mRvGift = null;
    }

    @Override
    public void onClick(View v) {
        if (isFastClick()) return;
        int id = v.getId();
        if (id == R.id.iv_back || id == R.id.tob_back) {     //返回按钮
            finish();
            return;
        }
        if (null == mUserInfo)
            return;
        if (id == R.id.tv_real_status) {  //是否真人
            p.getRealInfo();
        } else if (id == R.id.tv_self_status) {  //是否实名
            if (mUserInfo.isPartyGirl() || mUserInfo.getIs_real() == 1) {
                p.getSelfInfo();
            } else {
                toastTip("请先完成真人认证");
            }
        } else if (id == R.id.tv_edit) {   //编辑资料
            MineInfoActivity.startActivity(this, mUserInfo);
        } else if (id == R.id.tv_attention) {
            p.attentionUser(mUserInfo.getUser_id());
        } else if (id == R.id.tv_more_photo) {   //查看更多照片
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_PHOTO);
        } else if (id == R.id.tv_mine_trend) {   //查看更多动态
            ARouterUtils.toTrendActivity(mUserId, mUserInfo.getNickname());
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
                            p.getMsgCheck(mUserId);
                        }
                    } else if (CommonUtil.infoBtnTextUpdate3(service)) {   //搭讪
                        p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                        setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                    } else {
                        if (mUserInfo.getIs_accost() == 0) {
                            p.accostUser(String.valueOf(mUserInfo.getUser_id()), CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 1 : 2);
                            setAccostClickLog(CommonUtil.isBtnTextDetail(service, mUserInfo.isBoy()) ? 2 : 1, 2);
                        } else {
                            p.getMsgCheck(mUserId);
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
            p.getMsgCheck(mUserId);
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
        } else if (id == R.id.cl_local_tyrant) {    //土豪值
            if (!TextUtils.isEmpty(mUserId))
                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.rich) {
                GradeIntroduceActivity.startActivity(this, 1, mUserInfo.levels.rich);
            }
        } else if (id == R.id.cl_charm) {    //魅力值
            if (!TextUtils.isEmpty(mUserId))
                return;
            if (null != mUserInfo.levels && null != mUserInfo.levels.charm) {
                GradeIntroduceActivity.startActivity(this, 2, mUserInfo.levels.charm);
            }
        } else if (id == R.id.tv_call_video) {  //语音视频聊天
            if (!isPerformClick()) {
                return;
            }
            p.getCallCheck(mUserId);
        } else if (id == R.id.tob_more) {  //举报拉黑
            MineDetailMorePopWindow popWindow = new MineDetailMorePopWindow(this, mUserId);
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
            });
            popWindow.showPopupWindow(mTobMore);
        } else if (id == R.id.rl_guard) {   //守护

            GuardPopWindow popWindow = new GuardPopWindow(mActivity);
            popWindow.showPopupWindow();

        } else if (id == R.id.cl_family) {
            if (mUserInfo.family != null && mUserInfo.family.id != 0) {
                ARouterUtils.toFamilyDetail(mUserInfo.family.id, "0", MMKVUtils.getInstance().decodeInt(Constants.VOICE_IS_OPEN));
            }

        } else if (id == R.id.rel_room_audio) {
            if (mUserInfo != null && mUserInfo.play_party != null) {
                //进入 互动语音房间
                ARouterUtils.joinRoom(mUserInfo.play_party.room_id, mUserInfo.play_party.id);
            }

        }
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
}
