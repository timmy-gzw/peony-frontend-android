package com.tftechsz.common.widget.pop;

import static com.tftechsz.common.constant.Interfaces.FIY_NUMBER;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UIUtils;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.MD5Util;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.team.model.Team;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.robinhood.ticker.TickerView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.GifTitleAdapter;
import com.tftechsz.common.adapter.GiftChildVpAdapter;
import com.tftechsz.common.adapter.GiftVpAdapter;
import com.tftechsz.common.adapter.GiftWheatAdapter;
import com.tftechsz.common.adapter.IconCouplesAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.IGiftVpOnItemClick;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.databinding.PopGiftBinding;
import com.tftechsz.common.entity.ActivityGiftInfoDto;
import com.tftechsz.common.entity.ActivityGiftInfoQuestBean;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.entity.FamilyRoleDto;
import com.tftechsz.common.entity.GifTitleDto;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.PartyInfoDto;
import com.tftechsz.common.event.BuriedPointExtendDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.FamilyMemberEvent;
import com.tftechsz.common.event.KeyBordEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.AesUtil;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.NetworkUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.MySegmentTabLayout;

import org.apache.commons.codec.binary.Base64;
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

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GiftPopWindow extends BaseBottomPop implements View.OnClickListener, IGiftVpOnItemClick, OnTabSelectListener {

    private ConstraintLayout mRlInfo;   //个人信息
    private LinearLayout mLlOperation;  //操作信息
    private final Context mContext;
    private GiftChildVpAdapter mGiftChildVpAdapter;
    private int currentPage = 0;   // 当前选中页数
    private TextView mTvSendGift;  //发送礼物
    public GiftDto dto;
    private int mType;  //聊天类型   //1:私聊页面  2:群组页面  3：群组点击头像    4:聊天广场点击头像 5:聊天广场点击下面的礼物   6:聊天房  7:仅显示用户个人信息
    private final CompositeDisposable mCompositeDisposable;
    private TickerView mTvCoin;  //金币
    private String sessionId;
    private String mCoin;
    //个人信息
    public TextView mTvName, mTvSex;
    private ImageView mCiAvatar;
    private TextView mTvRoleType;
    //家族信息
    private LinearLayout mLlTeam;
    private CircleImageView mIvTeamAvatar;
    private TextView mTvTeamName;
    //语音房
    private LinearLayout mLlVoiceChat;
    private TextView mTvAddVoice;
    private ImageView mIvVoice;
    private int mVoiceStatus;
    //@对象
    private TextView mTvAtSend, mTvAtName, mTvAtUser;
    private ImageView mIvAtAvatar;
    private LinearLayout mRlAtUser;  //at用户
    private TextView mTvUserMessage;
    private String mAtUserName;
    private final List<String> mAtUserList;  //@用户
    private RechargePopWindow rechargePopWindow;
    private int mFamilyId;
    private final UserProviderService service;
    private int formType; //0默认  1家族   2派对
    private final boolean mIsFirst = true;
    private ImageView mMore;
    private RechargeBeforePop beforePop;
    private ConstraintLayout constraintLayout;
    //请求过的 giftdto
    private final Map<Integer, ActivityGiftInfoDto> mMapGiftSelected = new HashMap<>();
    private RoundedImageView roundedImageView;//国庆活动
    private TextView mTvGiftText, mTvPopGiftNameText;
    //替换服务器 背景图片
    private ImageView mImgActivity, mImgHeader2;
    private ImageView mImgActivityBottom1;
    private ImageView mNobleIcon;
    private MySegmentTabLayout mStLayout;
    private String[] segmentData_temp;
    private PopGiftBinding mBind;
    private ViewPager2 mGiftVp;
    private GiftWheatAdapter mWheatUserAdapter;
    private List<VoiceRoomSeat> mSelWheatUserList;
    private List<GiftDto> mPackGiftList;
    private FamilyIdDto mUserdata;
    protected SVGAParser svgaParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;

    //派对相关管理
    private boolean mIsMySelf, mIsManager, isOnSeat;
    private final int from_type;
    private final int scene;
    private int partyRoleId;
    private int silenceSwitch;
    private PartyInfoDto partyInfoDto;
    private GifTitleAdapter mGifTitleAdapter;
    private int titleIndex = 0;
    private final int roomId;
    private String toUserId;//接收人
    private RecyclerView recyclerViewIcon;//情侣图标

    //送出礼物点亮
    private LightenGiftPop lightenGiftPop;
    private TextView mTvAvatarHint;//跟ta表白  hint
    private RelativeLayout mRelTvNotAvatarHint;
    private ImageView mImgAvatar;//情侣头像
    private ILGiftCallBack ilGiftCallBack;
    public GiftDto giftDto1;

    public GiftPopWindow(Context context, int from_type, int scene, int roomId) {
        super(context);
        //setBackground(R.drawable.bg_trans);
        mContext = context;
        this.roomId = roomId;
        this.from_type = from_type;
        this.scene = scene;
        mCompositeDisposable = new CompositeDisposable();
        mAtUserList = new ArrayList<>();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initUI();
        initData();
    }

    public GiftPopWindow(Context context, int from_type, int scene, int roomId, ILGiftCallBack ilGiftCallBack) {
        super(context);
        //setBackground(R.drawable.bg_trans);
        mContext = context;
        this.roomId = roomId;
        this.from_type = from_type;
        this.scene = scene;
        mCompositeDisposable = new CompositeDisposable();
        mAtUserList = new ArrayList<>();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        this.ilGiftCallBack = ilGiftCallBack;
        initUI();
        initData();
    }


    private void initUI() {
        mImgAvatar = findViewById(R.id.img_not_avatar);
        mImgAvatar.setOnClickListener(this);
        mRelTvNotAvatarHint = findViewById(R.id.tv_not_avatar_hint);
        mRelTvNotAvatarHint.setOnClickListener(this);
        mTvAvatarHint = findViewById(R.id.tv_avatar_hint_btn);
        mTvAvatarHint.setOnClickListener(this);
        recyclerViewIcon = findViewById(R.id.rv_couples_icon);
        mImgHeader2 = findViewById(R.id.img_headernew);
        constraintLayout = findViewById(R.id.cos_activityvisible2);
        roundedImageView = findViewById(R.id.iv_activitys_avatars);
        mTvGiftText = findViewById(R.id.tv_popgift_text);
        mTvPopGiftNameText = findViewById(R.id.tv_popgift_name);
        mImgActivity = findViewById(R.id.imgalgin_popgift_bg);
        mImgActivityBottom1 = findViewById(R.id.popgift_algin_img);
        mMore = findViewById(R.id.more);
        //家族信息
        mLlTeam = findViewById(R.id.ll_team);
        mLlTeam.setOnClickListener(this);
        mIvTeamAvatar = findViewById(R.id.iv_team_avatar);
        mTvTeamName = findViewById(R.id.tv_team_name);
        //个人信息
        mCiAvatar = findViewById(R.id.ci_avatar);
        mTvName = findViewById(R.id.tv_name);
        mNobleIcon = findViewById(R.id.noble_icon);

        mTvSex = findViewById(R.id.tv_sex);
        mTvRoleType = findViewById(R.id.tv_type);
        //@用户
        mRlAtUser = findViewById(R.id.rl_at_user);
        mTvAtSend = findViewById(R.id.tv_at_send);
        mTvAtName = findViewById(R.id.tv_at_name);
        mTvAtUser = findViewById(R.id.tv_at_user);
        mIvAtAvatar = findViewById(R.id.iv_at_avatar);
        //语音房
        mLlVoiceChat = findViewById(R.id.ll_voice_chat);
        mIvVoice = findViewById(R.id.iv_voice);
        mTvAddVoice = findViewById(R.id.tv_add_voice);

        mLlOperation = findViewById(R.id.ll_operation);
        mRlInfo = findViewById(R.id.rl_info);
        mGiftVp = findViewById(R.id.vp);
        mTvSendGift = findViewById(R.id.tv_send_gift);
        mTvCoin = findViewById(R.id.tv_coin);
        mTvUserMessage = findViewById(R.id.tv_user_message);

        mStLayout = findViewById(R.id.st_layout);
        segmentData_temp = Interfaces.SEGMENT_DATA;
        mStLayout.setTabData(Interfaces.SEGMENT_DATA);
        mStLayout.setOnTabSelectListener(this);

        serListener();
        mGiftChildVpAdapter = new GiftChildVpAdapter(this);
        mGiftVp.setAdapter(mGiftChildVpAdapter);


        mGiftVp.setCurrentItem(0);
        setOverScrollMode(mGiftVp);

        mGifTitleAdapter = new GifTitleAdapter();
        mBind.gifTitleRecy.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        mBind.gifTitleRecy.setAdapter(mGifTitleAdapter);
        mGifTitleAdapter.onAttachedToRecyclerView(mBind.gifTitleRecy);
        mGifTitleAdapter.setOnItemClickListener((adapter, view, position) -> { //礼物类型title item点击操作
            if (!ClickUtil.canOperate(500)) return;
            if (titleIndex == position) { //相同索引点击不操作
                return;
            }
            mGiftVp.setCurrentItem(position, false);
        });

        //全麦用户
        mBind.wheatRecy.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        mWheatUserAdapter = new GiftWheatAdapter();
        mWheatUserAdapter.setOnItemClickListener((adapter, view, position) -> {//item点击事件
            VoiceRoomSeat item = mWheatUserAdapter.getItem(position);
            item.setGiftPopSel(!item.isGiftPopSel());
            notifyWheatUser(position, item.isGiftPopSel());
            if (!item.isGiftPopSel()) {
                mBind.setIsAllWheat(false);
            } else {
                for (VoiceRoomSeat seat : mWheatUserAdapter.getData()) {
                    if (!seat.isGiftPopSel()) {
                        mBind.setIsAllWheat(false);
                        return;
                    }
                }
                mBind.setIsAllWheat(true);
            }
        });
        mBind.wheatRecy.setAdapter(mWheatUserAdapter);
        mWheatUserAdapter.onAttachedToRecyclerView(mBind.wheatRecy);

        mGiftVp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitleSelected(position, mGiftChildVpAdapter.getItem(position).isEmpty());
                onitemClickGiftInfoActivityGet(mGiftChildVpAdapter.getSelGiftDto(position));
            }
        });
    }

    /**
     * title选中逻辑操作
     *
     * @param isClick 是否选中
     */
    private void setTitleSelected(int newTitleIndex, boolean isClick) {
        Utils.logE("setTitleSelected: 当前:" + titleIndex + "   改后最终:" + newTitleIndex + " - " + isClick);
        GifTitleDto item = mGifTitleAdapter.getItem(titleIndex);
        item.setIs_active(false);
        mGifTitleAdapter.setData(titleIndex, item);
        GifTitleDto itemNew = mGifTitleAdapter.getItem(newTitleIndex);
        itemNew.setIs_active(true);
        mGifTitleAdapter.setData(newTitleIndex, itemNew);
        titleIndex = newTitleIndex;
        mGiftVp.setCurrentItem(newTitleIndex, false);
        if (isClick) {
            mImgActivityBottom1.setVisibility(View.GONE);
            mImgActivityBottom1.setImageBitmap(null);
            getGiftList(getScene(), itemNew.getChild_cate());
            ARouter.getInstance()
                    .navigation(MineService.class)
                    .trackEvent("礼物栏分类", "gifts_sort_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), itemNew.getChild_cate(), System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);

            try {
                String number = MMKVUtils.getInstance().decodeString(FIY_NUMBER);
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("送礼弹窗页面曝光", "gift_send_popup_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), getSceneNew(), System.currentTimeMillis(), titleIndex + 1, CommonUtil.getOSName(), Constants.APP_NAME, getUserId(), roomId + "", !TextUtils.isEmpty(number) && !number.equals("-1") ? Long.parseLong(number) : 0, null)), null
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setOverScrollMode(ViewPager2 giftVp) {
        View childAt = giftVp.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    private void notifyWheatUser(int position, boolean isGiftPopSel) {
        View view = mWheatUserAdapter.getViewByPosition(position, R.id.gift_view);
        if (view != null) {
            view.setVisibility(isGiftPopSel ? View.VISIBLE : View.INVISIBLE);
        }
        TextView name = (TextView) mWheatUserAdapter.getViewByPosition(position, R.id.gift_name);
        if (name != null) {
            name.setTextColor(Utils.getColor(isGiftPopSel ? R.color.gift_wheat_text_color : R.color.white));
            name.setBackgroundResource(isGiftPopSel ? R.drawable.bg_orange_radius100 : R.drawable.shape_radio100_7f89f3);
        }
    }

    private void serListener() {
        mMore.setOnClickListener(this);
        mCiAvatar.setOnClickListener(this);
        mTvSendGift.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);
        mTvAddVoice.setOnClickListener(this);
        findViewById(R.id.ll_coin).setOnClickListener(this);
        mRlAtUser.setOnClickListener(this);
        findViewById(R.id.tv_at_team_user).setOnClickListener(this); //@
        findViewById(R.id.tv_user_detail).setOnClickListener(this); //主页
        mTvUserMessage.setOnClickListener(this); //私信
//        mBind.tvGift.setOnClickListener(this);
//        mBind.tvPack.setOnClickListener(this);
        mBind.ivAllWheat.setOnClickListener(this);
        mBind.tvAGift.setOnClickListener(this);
        mBind.report.setOnClickListener(this);
        mBind.selView.setOnClickListener(this);
        mBind.tvAllProperties.setOnClickListener(this);
        findViewById(R.id.tv_manager).setOnClickListener(this);   //管理
        findViewById(R.id.tv_down_wheat).setOnClickListener(this);
        findViewById(R.id.tv_close_wheat).setOnClickListener(this);  //闭麦
        findViewById(R.id.view_dismiss).setOnClickListener(this);

    }

    private void initData() {
        initBus();
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(KeyBordEvent.class)
                .subscribe(event -> setGiftCount(event.num)));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(FamilyMemberEvent.class)
                .subscribe(
                        event -> {
                            mAtUserList.clear();
                            mTvAtSend.setVisibility(View.VISIBLE);
                            mTvAtName.setVisibility(View.VISIBLE);
                            mIvAtAvatar.setVisibility(View.VISIBLE);
                            mTvAtUser.setVisibility(View.GONE);
                            mTvAtName.setText(event.name);

                            mBind.peopleNum.setText(event.name);
                            toUserId = String.valueOf(event.userId);
                            mBind.selIcon1.setVisibility(View.VISIBLE);
                            GlideUtils.loadRoundImage(mContext, mBind.selIcon1, event.icon);
                            mAtUserList.add(event.userId + "");
                            mAtUserName = event.name;
                        }
                ));
    }

    public void setVoiceChat(int status) {
        mLlVoiceChat.setVisibility(View.VISIBLE);
        mVoiceStatus = status;
        if (status == 2) {
            mIvVoice.setImageResource(R.mipmap.chat_ic_voice_open);
        } else {
            mIvVoice.setImageResource(R.mipmap.chat_ic_voice_close);
        }
    }

    /**
     * 设置金币
     *
     * @param coin 金币
     */
    public void setCoin(String coin) {
        mCoin = coin;
        mTvCoin.setAnimationDuration(Interfaces.TICKERVIEW_ANIMATION_MONEY);
        mTvCoin.setText(coin);
    }


    /**
     * 设置金币
     */
    public void setUserIdType(String userId, int type, String teamId) {
        this.sessionId = userId;
        mType = type;
        if (mType == 2 || mType == 3) {
            formType = 1;
        } else if (mType == 6 || mType == 7) {
            formType = 2;
        } else {
            formType = 0;
        }
        mBind.setIsWheatMode(false);
        if (mType == 1) {  //私聊进入
            mRlInfo.setVisibility(View.GONE);
            mLlOperation.setVisibility(View.GONE);
            mRlAtUser.setVisibility(View.GONE);
            mLlTeam.setVisibility(View.GONE);
            mBind.giftRootTopLine.setVisibility(View.GONE);
            mBind.llGift.setBackgroundResource(R.drawable.bg_white_top_radius10);
            toUserId = userId;
//            constraintLayout.setVisibility(View.GONE);//国庆item
        } else if (mType == 2 || mType == 5) {  //家族页面  聊天广场
            mRlInfo.setVisibility(View.GONE);
            mLlOperation.setVisibility(View.GONE);
            mRlAtUser.setVisibility(View.GONE);
            mLlTeam.setVisibility(View.GONE);
            mBind.giftRootTopLine.setVisibility(View.GONE);
            mBind.llGift.setBackgroundResource(R.drawable.bg_white_top_radius10);
            mBind.setBotSel(true);
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBind.stLayout.getLayoutParams();
            lp.height = ConvertUtils.dp2px(30);
            mBind.stLayout.setLayoutParams(lp);
//            constraintLayout.setVisibility(View.GONE);
        } else if (mType == 3 || mType == 4) {   // 家族页面点击头像
            mRlAtUser.setVisibility(View.GONE);
            mRlInfo.setVisibility(View.VISIBLE);
            mLlOperation.setVisibility(View.VISIBLE);
            mLlTeam.setVisibility(View.GONE);
            getFamilyId(1, teamId);
//            constraintLayout.setVisibility(View.GONE);
            if (mType == 4) {
                mLlTeam.setVisibility(View.VISIBLE);
            }

        } else if (mType == 6 || mType == 7) {   // 派对进入
            getPartyInfo(teamId);
            mRlAtUser.setVisibility(View.GONE);
            mRlInfo.setVisibility(View.VISIBLE);
            mLlOperation.setVisibility(View.VISIBLE);
            mLlTeam.setVisibility(View.GONE);
            mMore.setVisibility(View.GONE);
            if (!TextUtils.equals(sessionId, String.valueOf(service.getUserId()))) { //如果是自己不显示举报
                mBind.report.setVisibility(View.VISIBLE);
            }
            mBind.llManager.setVisibility(View.VISIBLE);
            if (mType == 7) {
                mBind.llGiftRoot.setVisibility(View.GONE);
                //mBind.tvAGift.setVisibility(View.VISIBLE);
            }
        }
        getGiftChildCate(getScene(), 0);
    }

    /**
     * 初始化情侣图标数据
     */
    private void couplesDate(FamilyIdDto familyIdDto) {
        if (familyIdDto.same_sex == 1) {
            mTvAvatarHint.setVisibility(View.INVISIBLE);
        } else {
            mTvAvatarHint.setVisibility(View.VISIBLE);
        }
        //情侣头像
        mTvAvatarHint.setText(familyIdDto.is_couple == 1 ? "TA的情侣" : "跟TA表白");
        mTvAvatarHint.setVisibility(View.VISIBLE);
        mRelTvNotAvatarHint.setVisibility(View.VISIBLE);
        if (familyIdDto.is_couple == 1) {
            GlideUtils.loadRoundImage(mContext, mImgAvatar, familyIdDto.couple_icon);
            mImgAvatar.setVisibility(View.VISIBLE);
            mRelTvNotAvatarHint.setVisibility(View.GONE);
        } else {
            mImgAvatar.setVisibility(View.GONE);
        }

        //新增情侣图标
        recyclerViewIcon.setVisibility(View.VISIBLE);
        recyclerViewIcon.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        IconCouplesAdapter iconCouplesAdapter = new IconCouplesAdapter();
        recyclerViewIcon.setAdapter(iconCouplesAdapter);

        iconCouplesAdapter.addData(familyIdDto.couple_gift_list);
        iconCouplesAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (lightenGiftPop == null)
                lightenGiftPop = new LightenGiftPop(mContext, 1, iconCouplesAdapter.getData().get(position));
            lightenGiftPop.updateDate(iconCouplesAdapter.getData().get(position));
            lightenGiftPop.showPopupWindow();
        });
    }

    private void getGiftChildCate(int scene, int isPack) {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createExchApi(PublicService.class)
                .getGiftChildCate(scene, isPack)
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<GifTitleDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<GifTitleDto>> response) {
                        if (response != null && response.getData() != null) {
                            mGifTitleAdapter.setList(response.getData());
                            List<List<GiftDto>> lists = new ArrayList<>();
                            List<GiftDto> selGift = new ArrayList<>();
                            List<GiftVpAdapter> mGiftVpAdapters = new ArrayList<>();
                            int is_active = 0;
                            for (int i = 0; i < response.getData().size(); i++) {
                                lists.add(new ArrayList<>());
                                selGift.add(new GiftDto());
                                mGiftVpAdapters.add(null);
                                GifTitleDto dto = response.getData().get(i);
                                if (dto.isIs_active()) {
                                    is_active = i;
                                }
                            }
                            mGiftChildVpAdapter.setList(lists);
                            mGiftChildVpAdapter.setSelGiftList(mGiftVpAdapters, selGift);
                            mGiftVp.setOffscreenPageLimit(lists.size());
                            setTitleSelected(is_active, true);
                            titleIndex = is_active;
                        }
                    }
                }));
    }

    private void getGiftList(int scene, int child_cate) {
        getGiftList(scene, child_cate, true, null);
    }

    private void getGiftList(int scene, int child_cate, boolean reSetCurrentItem, GiftDto giftDto) {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createExchApi(PublicService.class)
                .getGiftList(scene, child_cate)
                .compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (response != null && response.getData() != null) {
                            if (!TextUtils.isEmpty(response.getData())) {
                                String key = response.getData().substring(0, 6);
                                byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                                String iv = MD5Util.toMD532(key).substring(0, 16);
                                byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                                LogUtil.e("===============", new String(jsonData));
                                mPackGiftList = JSON.parseObject(new String(jsonData), new TypeReference<List<GiftDto>>() {
                                });
                                if (reSetCurrentItem) {
                                    mGiftChildVpAdapter.setData(titleIndex, mPackGiftList);
                                } else {
//                                    mGiftChildVpAdapter.setData(mGiftVp.getCurrentItem(), mPackGiftList);
                                    GiftVpAdapter vpAdapters = mGiftChildVpAdapter.getGiftVpAdapter(mGiftVp.getCurrentItem());
                                    int checkPosition = vpAdapters.getCheckPosition(currentPage);
                                    vpAdapters.setList(mPackGiftList, currentPage, checkPosition, giftDto != null ? giftDto.getId() : 0);

                                    //mGiftChildVpAdapter.setGiftList(mGiftVp.getCurrentItem(), vpAdapters.getGift(currentPage));
                                }
                                showPopupWindow();
                              /*  if (!mPackGiftList.isEmpty()) {
                                    //重新获得活动横幅
                                    mGiftVp.postDelayed(() -> onitemClickGiftInfoActivityGet(vpAdapters.getGift(vpAdapters.getCheckPosition(currentPage))), 50);
                                }*/

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
     * //1:私聊页面  2:群组页面  3：群组点击头像    4:聊天广场点击头像 5:聊天广场点击下面的礼物   6:聊天房  7:仅显示用户个人信息
     *
     * @return 返回场景 1 家族 2派对 3 私人聊天
     */
    private int getScene() {
        if (mType == 2 || mType == 3 || mType == 4 || mType == 5) return 1;
        if (mType == 6 || mType == 7) return 2;
        return 3;
    }

    /**
     * @return 埋点用到的接收方
     */
    private String getUserId() {
        if (mWheatUserAdapter != null && mWheatUserAdapter.getData().size() > 0) {
            int size = mWheatUserAdapter.getData().size();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                stringBuilder.append(mWheatUserAdapter.getData().get(i).getUser_id());
                if (i != size - 1) {
                    stringBuilder.append(",");
                }
            }
            toUserId = stringBuilder.toString();
        }
        return toUserId;
    }

    /**
     * from_type(1.私聊入口2.家族入口3.语音房底部送礼按钮4.语音房用户资料卡)
     * <p>
     * .type(1.礼物列表2.背包)
     */
    private int getSceneNew() {
        if (mType == 2 || mType == 3 || mType == 4 || mType == 5) return 2;
        if (mType == 6 || mType == 7) return 3;
        return 1;
    }

    /**
     * 设置派对管理
     *
     * @param isOnSeat      是否在麦上
     * @param party_role_id 当前角色id  128：超管 64：房主 32：管理员 0：普通用户
     * @param isMySelf      是否自己
     */
    public void setPartyManager(boolean isOnSeat, int party_role_id, boolean isMySelf, int silenceSwitch) {
        mIsManager = isManager(party_role_id);
        partyRoleId = party_role_id;
        mIsMySelf = isMySelf;
        this.isOnSeat = isOnSeat;
        this.silenceSwitch = silenceSwitch;
        mBind.tvManager.setVisibility(View.GONE);
        mBind.tvDownWheat.setVisibility(View.GONE);
        mBind.tvCloseWheat.setVisibility(View.GONE);
    }

    private boolean isManager(int role_id) {
        return role_id == 128 || role_id == 64 || role_id == 32;
    }

    public List<Integer> setUserIds(List<VoiceRoomSeat> seats, boolean showPackView) {
        sessionId = "0";
        mType = 6;
        formType = 2;
        mRlInfo.setVisibility(View.GONE);
        mLlOperation.setVisibility(View.GONE);
        mRlAtUser.setVisibility(View.GONE);
        mLlTeam.setVisibility(View.GONE);
        mMore.setVisibility(View.GONE);
        mBind.report.setVisibility(View.VISIBLE);
        mBind.setIsWheatMode(true);
        mBind.setIsAllWheat(true);
        List<Integer> listUserIds = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<VoiceRoomSeat> list = new ArrayList<>();
        for (VoiceRoomSeat seat : seats) {
            if (seat.getUser_id() != 0) {
                list.add(seat);
                seat.setGiftPopSel(true);
                sb.append(seat.getUser_id()).append(",");
                listUserIds.add(seat.getUser_id());
            }
        }
        if (sb.length() > 0) {
            mCompositeDisposable.add(RetrofitManager.getInstance()
                    .createPartyApi(PublicService.class)
                    .hasReceiveGift(sb.substring(0, sb.length()))
                    .compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<Integer>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<Integer>> response) {
                            if (null != response.getData() && list.size() == response.getData().size()) {
                                for (int i = response.getData().size() - 1; i >= 0; i--) {
                                    int status = response.getData().get(i);
                                    if (status != 1) {
                                        list.remove(i);
                                    }
                                }
                                mWheatUserAdapter.setList(list);
                                mBind.ivAllWheat.setEnabled(!list.isEmpty());
                                mBind.setIsAllWheat(!list.isEmpty());
                            }
                        }
                    }));
        } else {
            mBind.ivAllWheat.setEnabled(false);
            mBind.setIsAllWheat(false);
        }
        mWheatUserAdapter.setList(list);
        getGiftChildCate(getScene(), showPackView ? 5 : 0);
        return listUserIds;
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_gift);
        mBind = DataBindingUtil.bind(view);
        return view;
    }

    private void selectGiftNum() {
        new GiftNumberPopWindow(mContext, new GiftNumberPopWindow.OnSelectListener() {
            @Override
            public void onCheckNumber(int number) {
                setGiftCount(String.valueOf(number));
            }

            @Override
            public void onSendAll() {
                setGiftCount("全部");
            }
        }, true, isPackMode() && (mType != 2 && mBind.clWheat.getVisibility() == View.GONE || mType != 6 && mType != 5)
                , mGiftChildVpAdapter.getSelGiftDto(mGiftVp.getCurrentItem()) != null && mGiftChildVpAdapter.getSelGiftDto(mGiftVp.getCurrentItem()).tag_value == 4)
                .showPopupWindow();
    }

    private void setGiftCount(String number) {
        for (int i = 0; i < mStLayout.getTabCount(); i++) {
            if (TextUtils.equals(number, Utils.getText(mStLayout.getTitleView(i)))) {
                mStLayout.setCurrentTab(i);
                return;
            }
        }
        mStLayout.setCurrentTab(mStLayout.getTabCount() - 1);

        ArrayList<String> assetList = new ArrayList<>();
        Collections.addAll(assetList, segmentData_temp);
        assetList.set(assetList.size() - 1, String.valueOf(number));
        segmentData_temp = assetList.toArray(new String[0]);
        mStLayout.setTabData(segmentData_temp);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_not_avatar_hint == id || R.id.img_not_avatar == id || R.id.tv_avatar_hint_btn == id) {
            if (mUserdata != null) {
                ilGiftCallBack.callBack(mUserdata.is_couple, mUserdata.couple_user_id + "");
            }
        } else if (id == R.id.tv_more) {   //礼物数量选择
            selectGiftNum();
        } else if (id == R.id.tv_send_gift) { //发送礼物
            if (null != listener) {
                dto = mGiftChildVpAdapter.getSelGiftDto(mGiftVp.getCurrentItem());
                if (null == dto || mPackGiftList != null && mPackGiftList.isEmpty()) {
                    Utils.toast("请选择要送出的礼物");
                    return;
                }
                if (dto.id <= 0) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("礼物数据异常", "send_gift", "p2p_send_gift",
                                    JSON.toJSONString(dto), null);
                    dto = giftDto1;
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("礼物数据异常，进行更改后", "send_gift", "p2p_send_gift",
                                    JSON.toJSONString(dto), null);
                    if (null == dto || dto.id <= 0) {
                        Utils.toast("请重新打开礼物弹窗进行赠送");
                        return;
                    }
                }

                if (!dto.can_use && !TextUtils.isEmpty(dto.cannot_use_msg)) { //礼物不可发送
                    CommonUtil.performLink(getContext(), new ConfigInfo.MineInfo(dto.cannot_use_msg));
                    return;
                }

                if (mType == 2 || mType == 5) {  //家族页面  聊天广场
                    if (mBind.selIcon1.getVisibility() == View.GONE) {
                        Utils.toast("请选择送给礼物的用户");
                        return;
                    }
                }
                if (mBind.getIsWheatMode()) { //如果麦上用户送礼模式
                    mSelWheatUserList = getSelWheatUserList();
                    if (mSelWheatUserList.size() == 0) {
                        Utils.toast("请选择送给礼物的用户");
                        return;
                    }
                }
                double selfCoin = TextUtils.isEmpty(mCoin) ? 0 : Double.parseDouble(mCoin);
                String selectedNum = mBind.getIsChooseNum() ? Utils.getText(mStLayout.getTitleView(mStLayout.getCurrentTab())) : "1";
                int number = 0;
                if (!TextUtils.isEmpty(selectedNum)) {
                    if (TextUtils.equals("全部", selectedNum)) {
                        number = dto.getNumber();
                    } else {
                        number = Integer.parseInt(selectedNum);
                    }
                }

                if (isPackMode()) { //如果是背包模式
                    if ((mBind.getIsWheatMode() && dto.getNumber() < number * mSelWheatUserList.size())//如果麦上用户送礼模式 && 背包礼物数量小于选择的数量 * 选中用户数
                            || dto.getNumber() < number) {//如果背包礼物数量小于选择的数量
                        Utils.toast(R.string.not_enough_backpacks);
                        return;
                    }
                } else { //金币付费模式
                    if ((mBind.getIsWheatMode() && dto.coin * number * mSelWheatUserList.size() > selfCoin) || dto.coin * number > selfCoin) {
                        if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_limit_from_channel == 1) {
                            if (beforePop == null)
                                beforePop = new RechargeBeforePop(mContext);
                            beforePop.addOnClickListener(() -> {
                                if (null == rechargePopWindow)
                                    rechargePopWindow = new RechargePopWindow(mContext, from_type, scene, roomId, mFamilyId, sessionId);
                                rechargePopWindow.getCoin();
                                rechargePopWindow.requestData();
                                rechargePopWindow.setFormType(formType);
                                rechargePopWindow.showPopupWindow();
                            });
                            beforePop.showPopupWindow();
                        } else {
                            if (null == rechargePopWindow)
                                rechargePopWindow = new RechargePopWindow(mContext, from_type, scene, roomId, mFamilyId, sessionId);
                            rechargePopWindow.getCoin();
                            rechargePopWindow.requestData();
                            rechargePopWindow.setFormType(formType);
                            rechargePopWindow.showPopupWindow();
                        }
                        if (Utils.isPayTypeInFamily(formType)) {//家族聊天界面发礼物界面，余额不足弹出充值界面的用户数量（去重，按照独立uid统计）和单个用户弹出次数；
                            ARouter.getInstance()
                                    .navigation(MineService.class)
                                    .trackEvent("家族送礼余额不足", "pop", "family_send_gift",
                                            JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.FamilyExtendDto(SPUtils.getInstance().getString(Interfaces.SP_TEAM_ID)))), null);
                        }
                        return;
                    }
                }

                if (!NetworkUtil.isNetworkAvailable(BaseApplication.getInstance())) {
                    ToastUtil.showToast(BaseApplication.getInstance(), "暂无网络，请连接网络后重试！");
                    return;
                }
                if (!ClickUtil.canOperate()) return;
                if (mType == 1 || ((mType == 6 || mType == 7) && !mBind.getIsWheatMode())) {  //单聊 || 语音房类型&&不是麦上送礼模式
                    listener.sendGift(dto, number, null, "");
                } else {
                    if (mBind.getIsWheatMode()) { //麦上送礼模式
                        mAtUserList.clear();
                        if (mSelWheatUserList != null)
                            for (VoiceRoomSeat roomSeat : mSelWheatUserList) {
                                mAtUserList.add(String.valueOf(roomSeat.getUser_id()));
                            }
                    }
                    if (mAtUserList == null || mAtUserList.size() <= 0) {
                        ToastUtil.showToast(mContext, "请选择送礼物的用户");
                    } else {
                        listener.sendGift(dto, number, mAtUserList, mAtUserName);
                    }
                }
            }
        } else if (id == R.id.ll_coin) {  //去充值
            if (Utils.isPayTypeInFamily(formType)) {
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("家族送礼手动去充值", Interfaces.POINT_EVENT_CLICK, "family_gift_go_to_recharge",
                                JSON.toJSONString(new BuriedPointExtendDto(new BuriedPointExtendDto.FamilyExtendDto(SPUtils.getInstance().getString(Interfaces.SP_TEAM_ID)))), null);
            }
            ARouterUtils.toRechargeActivity("", formType);
        } else if (id == R.id.rl_at_user || id == R.id.sel_view) {   //ait用户
            Team team = NimUIKit.getTeamProvider().getTeamById(sessionId);
            if (mType == 5) {  //聊天广场
                ARouterUtils.toRoomMember((Activity) mContext, sessionId, 1);
            } else {  //群聊
                getFamilyId(0, team.getId());
            }
        } else if (id == R.id.tv_at_team_user) {  //@
            if (listener != null)
                if (mAtUserList != null && mAtUserList.size() > 0) {
                    listener.atUser(mAtUserList, mAtUserName);
                    dismiss();
                }
        } else if (id == R.id.tv_user_detail || id == R.id.ci_avatar) {  //主页
            ARouterUtils.toMineDetailActivity(TextUtils.equals(sessionId, String.valueOf(service.getUserId())) ? null : sessionId);
            dismiss();
        } else if (id == R.id.tv_user_message) {  //私信
            ARouterUtils.toChatP2PActivity(sessionId, NimUIKit.getCommonP2PSessionCustomization(), null);
            dismiss();
        } else if (id == R.id.more) { //更多
            RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                    .getFamilyRole(sessionId)
                    .compose(BasePresenter.applySchedulers())
                    .subscribe(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                        @Override
                        public void onSuccess(BaseResponse<FamilyIdDto> response) {
                            getRole(response.getData().role_id, response.getData().other_info);
                        }
                    });
        } else if (id == R.id.tv_add_voice) {  //下麦
            if (listener != null)
                listener.upOrDownSeat(Integer.parseInt(sessionId), true);
            dismiss();
        } else if (id == R.id.iv_voice || id == R.id.tv_close_wheat) {  //声音管理
            if (listener != null)
                listener.muteVoice(Integer.parseInt(sessionId), mVoiceStatus);
            dismiss();
        } else if (id == R.id.iv_all_wheat) {//全麦
            mBind.setIsAllWheat(!mBind.getIsAllWheat());
            for (int i = 0, j = mWheatUserAdapter.getData().size(); i < j; i++) {
                VoiceRoomSeat item = mWheatUserAdapter.getItem(i);
                if (mBind.getIsAllWheat()) {//如果选中全麦 &&没被选中
                    if (!item.isGiftPopSel()) {
                        item.setGiftPopSel(true);
                        notifyWheatUser(i, true);
                    }
                } else {
                    if (item.isGiftPopSel()) {//如果关闭全麦 && 被选中
                        item.setGiftPopSel(false);
                        notifyWheatUser(i, false);
                    }
                }
            }
        } else if (id == R.id.tv_a_gift) {//显示礼物
            mBind.llGiftRoot.setVisibility(View.VISIBLE);
            AnimationUtil.visibleAnimator(mBind.llGiftRoot);
            mBind.llOperation.setVisibility(View.GONE);
            mBind.llLevel.setVisibility(View.GONE);
            mBind.rlInfo.setVisibility(View.GONE);
            mBind.rlAtUser.setVisibility(View.VISIBLE);
            mBind.tvAtSend.setVisibility(View.VISIBLE);
            mBind.ivAtAvatar.setVisibility(View.VISIBLE);
            mBind.tvAtUser.setVisibility(View.GONE);
            mBind.tvAtName.setVisibility(View.VISIBLE);
            mBind.tvAtName.setText(Utils.getText(mBind.tvName));
            mBind.tvAtName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mBind.ivAtAvatar.setImageDrawable(mBind.ciAvatar.getDrawable());
            mBind.rlAtUser.setEnabled(false);
        } else if (id == R.id.tv_manager) {   //派对管理
            if (mUserdata != null) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ENTER_PARTY_UPDATE_PER_MANAGER, mUserdata.user_id, partyInfoDto));
            }
        } else if (id == R.id.tv_down_wheat) {   // 下麦
            if (mIsMySelf) {   //自己下麦
                if (listener != null)
                    listener.upOrDownSeat(Integer.parseInt(sessionId), true);
            } else if (mIsManager) {  //管理员踢下麦
                if (listener != null)
                    listener.upOrDownSeat(Integer.parseInt(sessionId), false);
            }
            dismiss();
        } else if (id == R.id.report) { //举报
            dismiss();
            ARouterUtils.toBeforeReportActivity(TextUtils.isEmpty(sessionId) ? 0 : Integer.parseInt(sessionId), 1);
        } else if (id == R.id.tv_all_properties) {
            try {
                Class aClass = Class.forName("com.tftechsz.im.mvp.ui.activity.VideoCallActivity");
                boolean activityExistsInStack = ActivityUtils.isActivityExistsInStack(aClass);
                if (!activityExistsInStack) {
                    PropertiesPopWindow propertiesPopWindow = new PropertiesPopWindow(getContext());
                    propertiesPopWindow.showPopupWindow();
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance(), "请在视频通话结束后查看");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.view_dismiss)   //隐藏pop
            dismiss();
    }

    /**
     * @return 是否是背包模式
     */
    public boolean isPackMode() {
        return mGifTitleAdapter.getItemCount() > 0 && mGifTitleAdapter.getItem(titleIndex).getChild_cate() == 5;
    }

    /**
     * 获取麦上用户送礼模式下选中的用户
     */
    private List<VoiceRoomSeat> getSelWheatUserList() {
        List<VoiceRoomSeat> seats = new ArrayList<>();
        for (VoiceRoomSeat seat : mWheatUserAdapter.getData()) {
            if (seat.isGiftPopSel()) {
                seats.add(seat);
            }
        }
        return seats;
    }

   /* public void getGiftPack(boolean reSetCurrentItem) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(PublicService.class)
                .getGiftPack()
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (!TextUtils.isEmpty(response.getData())) {
                            String key = response.getData().substring(0, 6);
                            byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                            String iv = MD5Util.toMD532(key).substring(0, 16);
                            byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                            LogUtil.e("===============", new String(jsonData));
                            mPackGiftList = JSON.parseObject(new String(jsonData), new TypeReference<List<GiftDto>>() {
                            });

                            int checkPosition = vpPckAdapter.getCheckPosition(currentPage);
                            vpPckAdapter.setList(mPackGiftList, currentPage, checkPosition);
                            vpPckAdapter.notifyDataSetChanged();
                            if (reSetCurrentItem) {
                                setPoint();
                                mBind.vp.setCurrentItem(0, false);
                            }
                        }
                    }

                }));
    }*/

    private void getRole(int role_id, FamilyIdDto.OtherInfo other_info) {
        dismiss();
        if (!TextUtils.isEmpty(sessionId)) {
            mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                    .getRole(Integer.parseInt(sessionId))
                    .compose(BasePresenter.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<FamilyRoleDto>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<FamilyRoleDto>> response) {
                            getRoleSuccess(Integer.parseInt(sessionId), role_id, other_info != null ? other_info.role_id : 0, response.getData());
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            super.onFail(code, msg);
                        }
                    }));

        }
    }

    private FamilyManagerPopWindow familyManagerWindow;
    private FamilyMutePopWindow familyMutePopWindow;


    private void getRoleSuccess(int userId, int role_id, int other_role_id, List<FamilyRoleDto> data) {
        if (null == familyManagerWindow)
            familyManagerWindow = new FamilyManagerPopWindow(mContext, role_id);
        familyManagerWindow.initData(other_role_id, Utils.getText(mTvName), data);
        familyManagerWindow.addOnClickListener(new FamilyManagerPopWindow.OnSelectListener() {
            @Override
            public void setRole(int roleId) {
                RetrofitManager.getInstance().createFamilyApi(PublicService.class).setUserRole(userId, roleId)
                        .compose(BasePresenter.applySchedulers())
                        .subscribe(new ResponseObserver<BaseResponse<Boolean>>() {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> response) {
                                Utils.toast("设置成功");
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                            }
                        });
            }

            @Override
            public void lookUser() {
                ARouterUtils.toMineDetailActivity(userId == service.getUserId() ? "" : ("" + userId));
            }

            @Override
            public void muteUser(int mute) {
                if (mute == 1) {
                    RetrofitManager.getInstance().createFamilyApi(PublicService.class).getMuteMap().compose(BasePresenter.applySchedulers())
                            .subscribe(new ResponseObserver<BaseResponse<List<FamilyInactiveDto>>>() {
                                @Override
                                public void onSuccess(BaseResponse<List<FamilyInactiveDto>> response) {
                                    if (data == null || data.size() <= 0) return;
                                    if (null == familyMutePopWindow)
                                        familyMutePopWindow = new FamilyMutePopWindow(mContext);
                                    familyMutePopWindow.initData(response.getData());
                                    familyMutePopWindow.addOnClickListener(type -> muteUs(mFamilyId, userId == service.getUserId() ? 0 : userId, type, 1));
                                    familyMutePopWindow.showPopupWindow();
                                }
                            });
                } else {
                    muteUs(mFamilyId, userId == service.getUserId() ? 0 : userId, 0, 0);
                }
            }

            @Override
            public void blockUser(int block) {
                mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                        .blackUser(service.getUserInfo().family_id, userId, block)
                        .compose(BasePresenter.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> response) {
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                            }
                        }));
            }

            @Override
            public void reportUser() {
                ARouterUtils.toBeforeReportActivity(userId == service.getUserId() ? 0 : userId, 1);
            }

            @Override
            public void removeUser() {
                mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                        .removeUser(userId)
                        .compose(BasePresenter.applySchedulers())
                        .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                            @Override
                            public void onSuccess(BaseResponse<Boolean> response) {
                                Utils.toast("踢出成功");
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                            }
                        }));
            }
        });
        familyManagerWindow.showPopupWindow();
    }

    private void muteUs(int familyId, int userId, int type, int operation) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class)
                .muteUser(familyId, userId, type, operation)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }
                }));
    }


    /**
     * 获取群id
     */
    private void getFamilyId(int type, String teamId) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createFamilyApi(PublicService.class).getFamilyId(teamId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            if (type == 0) {   //进入家族详情
                                ARouterUtils.toFamilyMember(response.getData().family_id);
                            } else {  //获取个人信息
                                getFamilyInfo(response.getData().family_id);
                            }

                        }
                    }
                }));
    }


    /**
     * 获取所在家族的信息
     */
    private void getFamilyInfo(int familyId) {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createFamilyApi(PublicService.class)
                .getFamilyInfoById(sessionId, familyId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            setIconInfo(response.getData(), false);
                            couplesDate(response.getData());
                            //家族信息
                            if (!TextUtils.isEmpty(mUserdata.family_name) && (mType == 4)) {
                                mFamilyId = mUserdata.family_id;
                                GlideUtils.loadRoundImage(mContext, mIvTeamAvatar, mUserdata.family_icon);
                                mTvTeamName.setText(mUserdata.family_name);
                                mLlTeam.setVisibility(View.VISIBLE);
                            } else {
                                mLlTeam.setVisibility(View.GONE);
                            }
                        }
                    }
                }));
    }

    public void getPartyInfo(String roomId) {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createPartyApi(PublicService.class)
                .getPartyUserInfo(sessionId, roomId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<PartyInfoDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyInfoDto> response) {
                        if (null != response.getData()) {
                            partyInfoDto = response.getData();
                            setIconInfo(response.getData().userinfo, true);

                            if (isOnSeat) { //在麦上
                                if (partyRoleId > partyInfoDto.userinfo.role_id) {
                                    mBind.tvManager.setVisibility(View.VISIBLE);
                                    mBind.tvCloseWheat.setText(silenceSwitch == 0 ? "闭麦" : "开麦");
                                    mBind.tvCloseWheat.setVisibility(View.VISIBLE);
                                }
                            } else {  //麦下也可以管理
                                if (partyRoleId > partyInfoDto.userinfo.role_id && !mIsMySelf) {
                                    mBind.tvManager.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }));
    }

    public String getSessionId() {
        return sessionId;
    }

    /**
     * 设置头像相关信息
     *
     * @param data      数据
     * @param showLevel 是否显示等级
     */
    private void setIconInfo(FamilyIdDto data, boolean showLevel) {
        mUserdata = data;
        com.netease.nim.uikit.common.CommonUtil.setBadge(mNobleIcon, data.getBadge());
        GlideUtils.loadCircleImage(mContext, mCiAvatar, data.icon);
        mTvName.setText(data.nickname);
        if (!TextUtils.isEmpty(data.nobility_card_svga)) {
            playGift(data.nobility_card_svga, mBind.svgaImageView);
        } else if (!TextUtils.isEmpty(data.icon_svga)) {
            playGift(data.icon_svga, mBind.svgaIconView);
        } else {
            VipUtils.setPersonalise(mBind.ivFrame, data.icon_id, false, true);
        }
        CommonUtil.setSexAndAge(mContext, data.sex, data.age, mTvSex);
        if (service.getUserInfo() != null && data.sex == service.getUserInfo().getSex()) {
            mTvUserMessage.setVisibility(View.GONE);
        } else {
            if (service.getUserInfo() != null && service.getUserInfo().isGirl() && data.sex == 1) {
                if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_girl_open_party_accost == 0)
                    mTvUserMessage.setVisibility(View.GONE);
                else {
                    mTvUserMessage.setVisibility(View.VISIBLE);
                }
            } else {
                mTvUserMessage.setVisibility(View.VISIBLE);
            }
        }
        UIUtils.setFamilyTitle(mTvRoleType, mType == 6 || mType == 7 ? 0 : data.role_id);
        mAtUserList.clear();
        mAtUserList.add(data.user_id + "");
        mAtUserName = data.nickname;

        if (showLevel && data.rich != null && data.charm != null) {
            mBind.llLevel.setVisibility(View.VISIBLE);
            //土豪值
            mBind.level.tvLocalTyrantTitle.setText(data.rich.title);
            mBind.level.tvLocalTyrantLevel.setText(mContext.getString(R.string.rich_lv_format, data.rich.level));
            //魅力值
            mBind.level.tvCharmTitle.setText(data.charm.value);
            mBind.level.tvCharmLevel.setText(mContext.getString(R.string.charm_lv_format, data.charm.level));
        } else {
            mBind.llLevel.setVisibility(View.GONE);
        }

        if (mType == 6 && !data.isToGift()) {
            mBind.llGiftRoot.setVisibility(View.GONE);
        }

        if (data.isToGift() && mType == 7) { // 派对女用户才显示送礼按钮
            mBind.tvAGift.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
        if (mMapGiftSelected != null) {
            mMapGiftSelected.clear();
        }
        if (mAtUserList != null) {
            mAtUserList.clear();
        }
        if (mSelWheatUserList != null) {
            mSelWheatUserList.clear();
        }
        if (mPackGiftList != null) {
            mPackGiftList.clear();
        }
        if (mBind.svgaImageView.isAnimating()) {
            mBind.svgaImageView.clearAnimation();
        }
        if (mBind.svgaIconView.isAnimating()) {
            mBind.svgaIconView.clearAnimation();
        }
    }


    @Override
    public void onitemClickGiftInfoActivityGet(GiftDto giftDto) {
        if (giftDto == null) {
            return;
        }
        giftDto1 = giftDto;
        mGiftChildVpAdapter.setGiftList(mGiftVp.getCurrentItem(), giftDto);

        if (!isPackMode() && giftDto.is_choose_num == 1) {
            segmentData_temp = Interfaces.SEGMENT_DATA;
        }

        mBind.setIsBagPackage(isPackMode());

        if (giftDto.is_choose_num == 1) { //需要选择数量
            mBind.setIsChooseNum(true);
            if (giftDto.tag_value == 4) {//如果是幸运礼物
                if (mStLayout.getTabCount() != Interfaces.SEGMENT_DATA_LUCKY.length) {
                    mStLayout.setCurrentTab(0);
                }
                mStLayout.post(() -> {
                    mStLayout.setTabData(Interfaces.SEGMENT_DATA_LUCKY);
                });
            } else {
                if (mStLayout.getTabCount() != segmentData_temp.length) {
                    segmentData_temp = Interfaces.SEGMENT_DATA;
                    mStLayout.setCurrentTab(0);
                }
                mStLayout.post(() -> {
                    mStLayout.setTabData(segmentData_temp);

                });
            }
        } else {
            mBind.setIsChooseNum(false);

        }
        //perfromSelectNum();

        if (giftDto.tag_value == 4) {
            int type = giftDto.getId() == 173 ? 1 : (giftDto.getId() == 174 ? 2 : (giftDto.getId() == 175 ? 3 : 0));
            if (type != 0) {
                ARouter.getInstance()
                        .navigation(MineService.class)
                        .trackEvent("人气分类中的" +
                                "暴击礼物点击", "pop_luck_gift_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), type, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);
            } else {
                int type2 = giftDto.getId() == 176 ? 1 : (giftDto.getId() == 177 ? 2 : (giftDto.getId() == 178 ? 3 : 0));
                if (type2 != 0) {
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("背包分类中的" +
                                    "暴击礼物点击", "backpack_luck_gift_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), type, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null);
                }
            }

        }

    }

    @Override
    public void onItemCurrent(int pos) {
        currentPage = pos;
    }

    private void setVisibles(ActivityGiftInfoDto accostDtoBaseResponse) {
        mImgActivity.setVisibility(accostDtoBaseResponse.type == 2 ? View.VISIBLE : View.GONE);
        roundedImageView.setVisibility(accostDtoBaseResponse.type == 2 ? View.VISIBLE : View.GONE);
        mTvGiftText.setVisibility(accostDtoBaseResponse.type == 2 ? View.VISIBLE : View.GONE);
        mTvPopGiftNameText.setVisibility(accostDtoBaseResponse.type == 2 ? View.VISIBLE : View.GONE);
        mImgHeader2.setVisibility(accostDtoBaseResponse.type == 2 ? View.VISIBLE : View.GONE);
        mImgActivityBottom1.setVisibility(accostDtoBaseResponse.type == 1 ? View.VISIBLE : View.GONE);
        if (accostDtoBaseResponse.type == 1) {
            Glide.with(getContext()).asBitmap().load(accostDtoBaseResponse.bg).addListener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    mImgActivityBottom1.post(() -> {
                        mImgActivityBottom1.setVisibility(View.GONE);
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    mImgActivityBottom1.post(() -> {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mImgActivityBottom1.getLayoutParams();
                        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                        lp.height = (int) (ScreenUtils.getScreenWidth() * 1f / resource.getWidth() * resource.getHeight());
                        mImgActivityBottom1.setLayoutParams(lp);
                        mImgActivityBottom1.setImageBitmap(resource);
                        mImgActivityBottom1.setOnClickListener(v -> CommonUtil.performLink(getContext(), new ConfigInfo.MineInfo(accostDtoBaseResponse.link, accostDtoBaseResponse.option), 0, 21));
                    });
                    return false;
                }
            }).submit();
        } else {
            GlideUtils.loadImage(mContext, mImgActivity, accostDtoBaseResponse.bg, R.mipmap.ic_default_avatar);
        }
    }

    @Override
    public void onTabSelect(int position) {
        //segmentData_temp = segmentData;
        //mStLayout.setTabData(segmentData_temp);
    }

    @Override
    public void onTabReselect(int position) {

    }

    public void sendGiftSuccess(GiftDto data) {
        if (data.animationType != 1) { //如果不是普通礼物, 会自动关闭无须调用接口获取金币或数量
            return;
        }
        if (listener != null) {
            if (isPackMode()) {//背包模式
                getGiftList(getScene(), mGifTitleAdapter.getItem(titleIndex).getChild_cate(), false, mGiftChildVpAdapter.getSelGiftDto(mGiftVp.getCurrentItem()));
            } else {
                listener.getMyCoin();
            }
        }
    }

    public void refreshData() {
        getGiftList(getScene(), mGifTitleAdapter.getItem(titleIndex).getChild_cate(), !isPackMode(), mGiftChildVpAdapter.getSelGiftDto(mGiftVp.getCurrentItem()));
    }

    public interface OnSelectListener {
        void sendGift(GiftDto data, int num, List<String> userId, String name);

        void getMyCoin();

        void atUser(List<String> userId, String name);

        void upOrDownSeat(int userId, boolean isOnSeat);

        void muteVoice(int userId, int voiceStatus);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public RequestBody createRequestBody(ActivityGiftInfoQuestBean bean) {
        Gson gson = new Gson();
        String json = gson.toJson(bean);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    private void playGift(String svgaName, SVGAImageView svgaImageView) {
        if (TextUtils.isEmpty(svgaName)) {
            return;
        }
        if (null == svgaParser) svgaParser = new SVGAParser(getContext());
        File file = new File(DownloadHelper.FILE_PATH + File.separator + svgaName.substring(svgaName.lastIndexOf("/")).replace(".svga", ""));
        if (file.exists()) {
            loadGift(file, svgaImageView);
        } else {
            DownloadHelper.downloadGift(svgaName, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    loadGift(file, svgaImageView);
                }

                @Override
                public void failed() {
                    Utils.logE("下载失败");
                }

                @Override
                public void onProgress(int progress) {
                }
            });
        }
    }

    private void loadGift(File file, SVGAImageView svgaImageView) {
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    svgaImageView.setVisibility(View.VISIBLE);
                    svgaImageView.setVideoItem(videoItem);
                    svgaImageView.stepToFrame(0, true);
                }

                @Override
                public void onError() {
                }
            };
        }
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
            svgaParser.setFileDownloader(new SVGAParser.FileDownloader());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface ILGiftCallBack {
        void callBack(int is_couple, String userId);
    }
}
