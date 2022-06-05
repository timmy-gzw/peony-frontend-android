package com.tftechsz.party.mvp.ui.activity;

import static com.tftechsz.common.Constants.NOTIFY_CLOSE_PARTY;
import static com.tftechsz.common.Constants.NOTIFY_ENTER_PARTY_UPDATE_PER_MANAGER;
import static com.tftechsz.common.Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN;
import static com.tftechsz.common.Constants.NOTIFY_PARTY_UPDATE;
import static com.tftechsz.common.Constants.PARAM_ROOM_ID;
import static com.tftechsz.common.Constants.PAR_LOVE_SWITCH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hoko.blur.HokoBlur;
import com.netease.nim.uikit.business.session.emoji.EmoticonPickerView;
import com.netease.nim.uikit.business.session.emoji.IEmoticonSelectedListener;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.EPSoftKeyBoardListener;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.inter.IAnimListener;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CallbackExt;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.UnReadMessageEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.music.base.BaseMusicHelper;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.service.PartyAudioService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ConstraintUtil;
import com.tftechsz.common.utils.Keyboard1Util;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.StatusBarUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.DoubleHitGiftChatView;
import com.tftechsz.common.widget.SharePopWindow;
import com.tftechsz.common.widget.gift.GiftRootLayout;
import com.tftechsz.common.widget.pop.GiftPopWindow;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyPkSeatAdapter;
import com.tftechsz.party.adapter.PartyRoomMsgAdapter;
import com.tftechsz.party.adapter.PartySeatAdapter;
import com.tftechsz.party.adapter.PartyWatchersAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.CoinBean;
import com.tftechsz.party.entity.MultipleChatRoomMessage;
import com.tftechsz.party.entity.WheelResultBean;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.PartyGiftDto;
import com.tftechsz.party.entity.req.SavePkReq;
import com.tftechsz.party.utils.ViewUtils;
import com.tftechsz.party.widget.pop.AdminOperatePopWindow;
import com.tftechsz.party.widget.pop.CreatePkPopWindow;
import com.tftechsz.party.widget.pop.EarphoneSettingPop;
import com.tftechsz.party.widget.pop.FunctionPopWindow;
import com.tftechsz.party.widget.pop.LeaveOnSeatPopWindow;
import com.tftechsz.party.widget.pop.LineUpMacPartyPop;
import com.tftechsz.party.widget.pop.LuckyResultPopWindow;
import com.tftechsz.party.widget.pop.LuckyWheelPopWindow;
import com.tftechsz.party.widget.pop.MusicPlayerPop;
import com.tftechsz.party.widget.pop.PartyHourRankPopWindow;
import com.tftechsz.party.widget.pop.PartyInfoPopWindow;
import com.tftechsz.party.widget.pop.PartyManagerPopWindow;
import com.tftechsz.party.widget.pop.PartyOptionBannedPop;
import com.tftechsz.party.widget.pop.SpecialEffectsPartyPop;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

@Route(path = ARouterApi.ACTIVITY_ROOM_STUDIO)
public class PartyRoomActivity extends BasePartyRoomActivity implements FunctionPopWindow.IFunctionListener, IEmoticonSelectedListener, LuckyWheelPopWindow.ICallBackGiftResult, LineUpMacPartyPop.IOnSeatListener, SpecialEffectsPartyPop.IOnSpecialEffectsListener {

    private LuckyWheelPopWindow mLuckyWheelPopWindow;  //幸运转盘
    private MusicPlayerPop mMusicPlayerPop;  //音乐弹窗
    private AdminOperatePopWindow mOperatePop;  //管理员控制麦位弹窗
    private PartyInfoPopWindow mPartyInfoPop;   //派对信息弹窗
    private PartyHourRankPopWindow mHourRankPop; //小时榜
    private LeaveOnSeatPopWindow mLeavePop;  //离开麦位弹窗
    private EmoticonPickerView emoticonPickerView;

    //派对管理
    private PartyManagerPopWindow partyManagerPopWindow;
    //转盘结果dialog
    private LuckyResultPopWindow luckyResultPopWindow;
    private GiftPopWindow giftPopWindow;

    //转盘结果通知
    private WheelResultBean wheelResultBean;
    private GiftRootLayout.GiftRootListener giftViewListener;
    private boolean mIsDown, mIsPlay;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    //单机次数
    public static int mNumber;

    public PartyOptionBannedPop partyBannedPop;
    //分享
    private SharePopWindow mPopWindowShare;
    //弹窗userid
    private String mUseridPop;
    //排序上麦弹窗
    private LineUpMacPartyPop lineUpMacPartyPop;

    //是否当前界面显示
    private boolean mFlagIsOnResume;
    private TextView mTvSend;
    private SpecialEffectsPartyPop specialEffectsPartyPop;//特效

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_party_room);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtil.fullScreen(this);
        initRxBus();
        svgaParser = new SVGAParser(this);
        mEtContent = findViewById(R.id.et_content);
        svgaImageView = findViewById(R.id.svga_lucky);
        emoticonPickerView = findViewById(R.id.emoticon_picker_view);
        setListener();
        startThread();

        //普通模式适配器
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mBind.rvSeat.setLayoutManager(layoutManager);
        //聊天室消息
        mMsgLayoutManager = new LinearLayoutManager(this);
        mBind.rvMessage.setLayoutManager(mMsgLayoutManager);
        if (mBind.rvSeat.getItemAnimator() != null)
            ((SimpleItemAnimator) mBind.rvSeat.getItemAnimator()).setSupportsChangeAnimations(false);
        mSeatAdapter = new PartySeatAdapter();
        mBind.rvSeat.setAdapter(mSeatAdapter);
        List<VoiceRoomSeat> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            list.add(new VoiceRoomSeat(i));
        }
        mSeatAdapter.setList(list);
        mSeatAdapter.setOnItemClickListener((adapter, view, position) -> {

            VoiceRoomSeat seat = mSeatAdapter.getData().get(position);
            if (seat == null) return;
            mVoiceRoomSeat = seat;
            if (seat.getUser_id() == service.getUserId()) {  //自己在麦上
                if (!ClickUtil.canOperate()) return;
                if (mLeavePop == null)
                    mLeavePop = new LeaveOnSeatPopWindow(PartyRoomActivity.this, "");
                mLeavePop.addOnClickListener(new LeaveOnSeatPopWindow.OnSelectListener() {
                    @Override
                    public void leaveOnSeat() {
                        getP().leaveSeat(mId, seat.getIndex(), 1);
                    }
                });
                mLeavePop.showPopupWindow();
            } else {
                //麦位上没人
                if (seat.getUser_id() == 0) {
                    applySeatPre(seat);
                    visitPartyList(2, 2, "", -1);
                } else {
                    mUseridPop = seat.getUser_id() + "";
                    p.getGift(mUseridPop, null);
                }
            }
        });
        mMsgAdapter = new PartyRoomMsgAdapter();
        mMsgAdapter.setHasStableIds(true);
        mBind.rvMessage.setAdapter(mMsgAdapter);
        if (mBind.rvMessage.getItemAnimator() != null)
            ((SimpleItemAnimator) mBind.rvMessage.getItemAnimator()).setSupportsChangeAnimations(false);
        mBind.rvMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mMsgLayoutManager.findLastVisibleItemPosition() == mMsgAdapter.getItemCount() - 1) {
                    autoScroller = true;
                    mBind.showNewMsg.setVisibility(View.GONE);
                } else {
                    autoScroller = false;
                }
            }
        });
        mMsgAdapter.addChildClickViewIds(R.id.tv_welcome);
        mMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tv_welcome) {  //欢迎ta
                if (!ClickUtil.canOperate()) {
                    return;
                }
                MultipleChatRoomMessage multipleChatRoomMessage = mMsgAdapter.getData().get(position);
                if (multipleChatRoomMessage == null) return;
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(multipleChatRoomMessage.getMessage());
                if (chatMsg != null) {
                    ChatMsg.PartyMsg msg = JSON.parseObject(chatMsg.content, ChatMsg.PartyMsg.class);
                    if (msg != null) {
                        multipleChatRoomMessage.getMessage().setChecked(true);
                        mMsgAdapter.setData(position, multipleChatRoomMessage);
                        mMsgAdapter.notifyItemChanged(position);
                    }
                }
                String content = getP().getSendMsg(getRemoteExtensionNickName(multipleChatRoomMessage.getMessage()));
                sendMessage(content);
            }
        });
        mMsgAdapter.setOnSelectClickListener(new PartyRoomMsgAdapter.OnSelectClickListener() {
            @Override
            public void onNameClick(String id) {
                mUseridPop = id;
                getP().getGift(id, null, true);
            }

            @Override
            public void giftClick() {
                if (mData != null && mData.getMicrophone() != null) {
                    p.getGift("0", mData.getMicrophone());
                }
            }
        });
        EPSoftKeyBoardListener.setListener(this, new EPSoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (mFlagIsOnResume) {
                    showBottomInput();
                    ConstraintLayout.MarginLayoutParams params = (ConstraintLayout.MarginLayoutParams) mBind.clInputMsg.getLayoutParams();
                    params.height = height + DensityUtils.dp2px(PartyRoomActivity.this, 60);
                    mBind.clInputMsg.setLayoutParams(params);
                }
            }

            @Override
            public void keyBoardHide(int height) {
                showEjo();
            }
        });
        //我方
        GridLayoutManager weManager = new GridLayoutManager(this, 2);
        mBind.clPk.rvWeTeam.setLayoutManager(weManager);
        if (mBind.clPk.rvWeTeam.getItemAnimator() != null)
            ((SimpleItemAnimator) mBind.clPk.rvWeTeam.getItemAnimator()).setSupportsChangeAnimations(false);
        mWeTeamAdapter = new PartyPkSeatAdapter();
        mWeTeamAdapter.setTeamType(1);
        mBind.clPk.rvWeTeam.setAdapter(mWeTeamAdapter);
        List<VoiceRoomSeat> list1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list1.add(new VoiceRoomSeat(i));
        }
        mWeTeamAdapter.setList(list1);
        mWeTeamAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                pkTeamClick(mWeTeamAdapter.getData().get(position));
            }
        });
        //对方
        GridLayoutManager otherManager = new GridLayoutManager(this, 2);
        mBind.clPk.rvOtherTeam.setLayoutManager(otherManager);
        if (mBind.clPk.rvOtherTeam.getItemAnimator() != null)
            ((SimpleItemAnimator) mBind.clPk.rvOtherTeam.getItemAnimator()).setSupportsChangeAnimations(false);
        mOtherTeamAdapter = new PartyPkSeatAdapter();
        mOtherTeamAdapter.setTeamType(2);
        mBind.clPk.rvOtherTeam.setAdapter(mOtherTeamAdapter);
        List<VoiceRoomSeat> list2 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list2.add(new VoiceRoomSeat(i + 4));
        }
        mOtherTeamAdapter.setList(list2);
        mOtherTeamAdapter.setOnItemClickListener((adapter, view, position) -> {
            pkTeamClick(mOtherTeamAdapter.getData().get(position));
        });
        //观看人
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        linearLayoutManager.setReverseLayout(true);
        mBind.rlWatcher.setLayoutManager(linearLayoutManager);
        mWatcherAdapter = new PartyWatchersAdapter();
        mBind.rlWatcher.setAdapter(mWatcherAdapter);
        mWatcherAdapter.setOnItemClickListener((adapter, view, position) -> {
            getP().setHourPop(this, mData, mId);
        });
        getP().wheelSwitch(mId);

        if (null == svgaParser) svgaParser = new SVGAParser(this);
        mBind.svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                mBind.ivGiftMask.setVisibility(View.GONE);
                mBind.svgaImageView.setVisibility(View.GONE);
                peedData = null;
                mIsPlay = false;
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
        mBind.playerView.setAnimListener(new IAnimListener() {
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
                mBind.playerView.post(() -> mBind.playerView.setVisibility(View.GONE));
            }

            @Override
            public void onVideoDestroy() {

            }

            @Override
            public void onFailed(int i, @Nullable String s) {
                mIsPlay = false;
            }
        });
        mParseCompletionCallback = new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                Utils.logE("onComplete: " + videoItem.getFPS());
                mBind.ivGiftMask.setVisibility(View.VISIBLE);
                mBind.svgaImageView.setVisibility(View.VISIBLE);
                mBind.svgaImageView.setVideoItem(videoItem);
                mBind.svgaImageView.stepToFrame(0, true);
            }

            @Override
            public void onError() {
                Utils.logE("onError: ");
                mIsPlay = false;
            }
        };
    }


    /**
     * PK 位置点击
     */
    private void pkTeamClick(VoiceRoomSeat seat) {
        if (seat == null) return;
        mVoiceRoomSeat = seat;
        if (seat.getUser_id() == service.getUserId()) {  //自己在麦上
            if (mLeavePop == null)
                mLeavePop = new LeaveOnSeatPopWindow(PartyRoomActivity.this, "");
            mLeavePop.addOnClickListener(new LeaveOnSeatPopWindow.OnSelectListener() {
                @Override
                public void leaveOnSeat() {
                    getP().leaveSeat(mId, seat.getIndex(), 1);
                }
            });
            mLeavePop.showPopupWindow();
        } else {
            //麦位上没人
            if (seat.getUser_id() == 0) {
                visitPartyList(2, 2, "", -1);
                applySeatPre(seat);
            } else {
                mUseridPop = seat.getUser_id() + "";
                p.getGift(mUseridPop, null);
            }
        }
    }


    public void visitPartyList(int type, int from_type, String content, int tag) {
        if (service == null || mData == null) {
            return;
        }
        p.upLog(type, from_type, content, tag, service, mRoomId, mData);

    }

    //通知排麦人数列表popwindow刷新
    @Override
    public void lineUpMacUserRefresh(int userId) {
        if (lineUpMacPartyPop != null && lineUpMacPartyPop.isShowing()) {
            lineUpMacPartyPop.notifyListItem(userId);
        }
    }

    @Override
    protected void addWarnTips(String rank) {
        mBind.rlPartyScrollerView.setVisibility(View.VISIBLE);
        mBind.PartyScrollerView.addWarnTips(rank);
    }

    /**
     * 上麦送礼物
     */
    public void visitPartyList11(int type, int from_type, String toUserId, List<Integer> userIds) {
        UserProviderService serviceUser = ARouter.getInstance().navigation(UserProviderService.class);
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("送礼弹窗页面曝光", "gift_send_popup_visit", "", JSON.toJSONString(new NavigationLogEntity(serviceUser.getUserId(), from_type, System.currentTimeMillis(), type, CommonUtil.getOSName(), Constants.APP_NAME, toUserId, mRoomId, 0, userIds)), null
                );
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (service == null || mData == null) {
            return;
        }
        visitPartyList(1, 0, "", -1);
        mIsNetVisit = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFlagIsOnResume = false;
    }

    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT) {//转盘结果通知
                                wheelResultBean = JSON.parseObject(event.code, WheelResultBean.class);
                                //动画结束再显示
                                if (mLuckyWheelPopWindow != null && !mLuckyWheelPopWindow.isShowing()) {
                                    showWindowResult();
                                }
                            } else if (event.type == NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN) {//转盘再来一次
                                if (mLuckyWheelPopWindow != null && !mLuckyWheelPopWindow.isShowing()) {
                                    mLuckyWheelPopWindow.setIsActivityBg(mData != null && mData.getRoom() != null && mData.getRoom().getTurntable_skin() == 1);
                                    mLuckyWheelPopWindow.showPopupWindow();
                                }
                            } else if (event.type == NOTIFY_ENTER_PARTY_UPDATE_PER_MANAGER) {//权限管理
                                boolean isOpen = false;
                                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                                    isOpen = BaseMusicHelper.get().getPartyService().getRoomAudioStatus();
                                if (functionPopWindow == null) {
                                    functionPopWindow = new FunctionPopWindow(this, isOpen, this, mFightPattern, p.isAdmin(mData), mId, 0, mUseridPop, mData);
                                } else {
                                    functionPopWindow.initFunctionMenuManager(mFightPattern, 0, mUseridPop, mData, false, event.partyInfoDto != null ? event.partyInfoDto.permissions : null);
                                    functionPopWindow.updateSpecialData(mData.getRoom().getDress_switch() == 1 && mData.getRoom().getGift_switch() == 1);
                                }
                                functionPopWindow.showPopupWindow();

                                if (giftPopWindow != null && giftPopWindow.isShowing()) {
                                    giftPopWindow.dismiss();
                                }
                            } else if (event.type == NOTIFY_PARTY_UPDATE) {//刷新数据
                                getP().joinParty(mId, "in");
                            } else if (event.type == NOTIFY_CLOSE_PARTY) {//关闭派对
                                finish();
                            } else if (event.type == Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS) {
                                if (giftPopWindow != null && giftPopWindow.isShowing()) {
                                    getCoin();
                                }
                            } else if (event.type == Constants.NOTIFY_INFO_DIALOG) {//dialog弹窗
                                getP().getGift(String.valueOf(event.familyId), null, true);
                            } else if (event.type == Constants.NOTIFY_PARTY_SCROLLER_GONE) {    //警告框关闭
                                mBind.rlPartyScrollerView.setVisibility(View.GONE);
                            }
                        }
                ));
        //判断是否有消息
        mCompositeDisposable.add(RxBus.getDefault().toObservable(UnReadMessageEvent.class)
                .subscribe(
                        event -> Utils.runOnUiThread(() -> mBind.viewMessage.setVisibility(event.p2pNumber > 0 ? View.VISIBLE : View.GONE))
                ));
    }

    /**
     * 显示结果通知dialog
     */
    private void showWindowResult() {

        if (wheelResultBean.status != 0) {
            //通知转盘结果
            if (wheelResultBean != null) {
                if (luckyResultPopWindow == null) {
                    luckyResultPopWindow = new LuckyResultPopWindow(mContext, wheelResultBean.reward, false);
                }
                luckyResultPopWindow.setAdapter(wheelResultBean.reward, wheelResultBean.status);
                luckyResultPopWindow.showPopupWindow();
            }
        } else {
            if (luckyResultPopWindow == null) {
                luckyResultPopWindow = new LuckyResultPopWindow(mContext, null, false);
            }
            luckyResultPopWindow.setAdapter(null, 0);
            luckyResultPopWindow.showPopupWindow();
        }


    }

    /**
     * 事件监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        mBind.svgaLucky.setOnClickListener(this);
        mBind.ivFunction.setOnClickListener(this);
        mBind.clInfo.setOnClickListener(this);  //派对信息
        mBind.llSendMessage.setOnClickListener(this);  // 聊天输入
        mBind.ivGift.setOnClickListener(this); //礼物
        mBind.ivHostAvatar.setOnClickListener(this);   //主持位点击
        mBind.ivClose.setOnClickListener(this);   //关闭聊天室
        mBind.showNewMsg.setOnClickListener(this);   //显示新消息
        mBind.inputView.setOnTouchListener((v, event) -> {
            hideBottomInput();
            return true;
        });
        mTvSend = mBind.clInputMsg.findViewById(R.id.tv_send);
        findViewById(R.id.iv_hour_arrow).setOnClickListener(this);   //查看小时榜
        findViewById(R.id.tv_send).setOnClickListener(this);   //发送文本
        findViewById(R.id.iv_emj).setOnClickListener(this);  // 表情
        findViewById(R.id.tv_hour_rank).setOnClickListener(this);  //小时榜
        findViewById(R.id.tv_announcement).setOnClickListener(this);  //公告
        findViewById(R.id.iv_start_pk).setOnClickListener(this);   //开始pk
        findViewById(R.id.iv_end_pk).setOnClickListener(this);   //结束PK
        findViewById(R.id.iv_message).setOnClickListener(this);  //消息
        findViewById(R.id.tv_seat_on).setOnClickListener(this);  //上麦
        findViewById(R.id.iv_mute).setOnClickListener(this);  //闭麦 开买
        findViewById(R.id.iv_emoji).setOnClickListener(this);  //表情
        findViewById(R.id.iv_expression).setOnClickListener(this);  //大表情
        findViewById(R.id.iv_send_message).setOnClickListener(this);  //发送消息


        mEtContent.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvSend.setAlpha(s.length() > 0 ? 1f : 0.7f);
                mTvSend.setEnabled(s.length() > 0);
                MoonUtil.replaceEmoticons(PartyRoomActivity.this, s, start, count);
                int editEnd = mEtContent.getSelectionEnd();
                mEtContent.removeTextChangedListener(this);
                while (StringUtil.counterChars(s.toString()) > NimUIKitImpl.getOptions().maxInputTextLength && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd);
                    editEnd--;
                }
                mEtContent.setSelection(editEnd);
                mEtContent.addTextChangedListener(this);
            }
        });
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.svga_lucky) {   // 转盘点击
            visitPartyList(6, 1, "", -1);
            if (null == mLuckyWheelPopWindow) {
                mLuckyWheelPopWindow = new LuckyWheelPopWindow(this, String.valueOf(mId), this, mData != null && mData.getRoom() != null && mData.getRoom().getTurntable_skin() == 1);
            }
            mLuckyWheelPopWindow.setIsActivityBg(mData != null && mData.getRoom() != null && mData.getRoom().getTurntable_skin() == 1);
            mLuckyWheelPopWindow.showPopupWindow();
        } else if (id == R.id.iv_close) {  //关闭聊天室
            isRelease = false;
            MMKVUtils.getInstance().encode(Constants.PARTY_IS_ON_SEAT, isOnSeat());
            MMKVUtils.getInstance().encode(Constants.PARTY_ID, mId);
            MMKVUtils.getInstance().encode(Constants.PARAM_YUN_ROOM_ID, mRoomId);
            getP().smallWindow(this, mMessageNum, mRoomId, mId, mRoomThumb, mRoomBg, isOnSeat(), getP().getIndex(mData, service.getUserId()), mFightPattern);
        } else if (id == R.id.ll_send_message || id == R.id.iv_send_message) {  //输入聊天内容
            if (mRoomShutUp == 1 && !getP().isAdmin(mData)) {   //0：正常 1：禁言
                toastTip("全员禁言中，不能发言哦");
                return;
            }
            if (mUserShutUp == 1) {
                toastTip(StringUtil.isEmpty(shutUpToastMsg) ? "您已被禁言" : shutUpToastMsg);
                return;
            }
            mFlagIsOnResume = true;
            Keyboard1Util.showSoftInput(this, mEtContent);
        } else if (id == R.id.tv_send) {  //发送消息
            String content = mEtContent.getText().toString();
            if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
                toastTip("发送消息不能为空");
                return;
            }
            getP().uploadCheat();
            sendMessage(content);
            mEtContent.setText("");
            //hideBottomInput();
        } else if (id == R.id.iv_function) { //多功能栏
            if (functionPopWindow == null) {
                boolean isOpen = false;
                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
                    isOpen = BaseMusicHelper.get().getPartyService().getRoomAudioStatus();
                functionPopWindow = new FunctionPopWindow(this, isOpen, this, mFightPattern, getP().isAdminAndOwner(mData), mId, 1, mData);
            } else {
                functionPopWindow.initFunctionMenuManager(mFightPattern, 1, "", mData, true, null);
                if (mData != null && mData.getRoom() != null)
                    functionPopWindow.updateSpecialData(mData.getRoom().getDress_switch() == 1 && mData.getRoom().getGift_switch() == 1);
            }
            functionPopWindow.showPopupWindow();
        } else if (id == R.id.iv_host_avatar) {   //主持位点击
            if (mHostSeat != null && mHostSeat.getUser_id() != 0) {  //主播麦位上麦有人
                if (mHostSeat.getUser_id() == service.getUserId()) {  //自己在主播麦位
                    getP().showLeavePop(this, mId, mHostSeat);
                } else {
                    mUseridPop = mHostSeat.getUser_id() + "";
                    p.getGift(mUseridPop, null);
                }
            } else {
                applySeatPre(mHostSeat);
            }
        } else if (id == R.id.tv_seat_on) {   //随机上麦
            if (!ClickUtil.canOperate()) {
                return;
            }
            //麦序模式 显示弹窗
            if (lineUpMacPartyPop == null) {
                lineUpMacPartyPop = new LineUpMacPartyPop(this, mId, getP().isAdminAndOwner(mData), service.getUserId(), this);
            }
            lineUpMacPartyPop.setPkInfoId(mFightPattern == 1 ? 0 : mPkInfoId);
            lineUpMacPartyPop.showPopupWindow();
            lineUpMacPartyPop.setOnSeat(isOnSeat());

        } else if (id == R.id.iv_mute) {  //开麦
            // 自己user_id传0
            getP().muteMicrophone(mId, 0, getP().getIndex(mData, service.getUserId()), getP().getVoiceSeat(mData, service.getUserId()) == null ? 0 : getP().getVoiceSeat(mData, service.getUserId()).getSilence_switch() == 0 ? 1 : 0);

        } else if (id == R.id.iv_emj) {   // 点击表情
            mFlagIsOnResume = true;
            showEjo();
        } else if (id == R.id.iv_emoji) {
            if (mRoomShutUp == 1 && !getP().isAdmin(mData)) {   //0：正常 1：禁言
                toastTip("全员禁言中，不能发言哦");
                return;
            }
            if (mUserShutUp == 1) {
                toastTip(StringUtil.isEmpty(shutUpToastMsg) ? "您已被禁言" : shutUpToastMsg);
                return;
            }
            mFlagIsOnResume = true;
            showBottomInput();
            showEjo();
        } else if (id == R.id.iv_expression) {  //大表情
            getP().getPicture(this, mId);
        } else if (id == R.id.cl_info) {   // 派对信息
            if (mPartyInfoPop == null)
                mPartyInfoPop = new PartyInfoPopWindow(this);
            mPartyInfoPop.getPartyInfo(mId);
            mPartyInfoPop.showPopupWindow();
        } else if (id == R.id.tv_hour_rank) { //小时榜
            if (mHourRankPop == null)
                mHourRankPop = new PartyHourRankPopWindow(this);
            mHourRankPop.setData(mData);
            mHourRankPop.showPopupWindow(mBind.tvHourRank);
        } else if (id == R.id.tv_announcement) {  //公告
            if (mData != null && mData.getRoom() != null) {
                showAnnouncement(mData.getRoom().getTitle(), mData.getRoom().getDesc(), false);
                visitPartyList(3, 1, mData.getRoom().getAnnouncement(), -1);
            }
        } else if (id == R.id.iv_start_pk) {  //开始PK
            if (mPkInfoId != 0) {
                getP().startPartyPk(mId, mPkInfoId);
            } else {
                if (mData == null || mData.getRoom() == null)
                    return;
                List<Integer> red = new ArrayList<>();
                List<Integer> blue = new ArrayList<>();
                if ((mWeTeamAdapter == null || mWeTeamAdapter.getData().size() <= 0) || (mOtherTeamAdapter == null || mOtherTeamAdapter.getData().size() <= 0)) {
                    return;
                }
                for (int i = 0; i < mWeTeamAdapter.getData().size(); i++) {
                    if (mWeTeamAdapter.getData().get(i).getUser_id() != 0)
                        red.add(mWeTeamAdapter.getData().get(i).getUser_id());
                }
                for (int i = 0; i < mOtherTeamAdapter.getData().size(); i++) {
                    if (mOtherTeamAdapter.getData().get(i).getUser_id() != 0)
                        blue.add(mOtherTeamAdapter.getData().get(i).getUser_id());
                }
                if (blue.size() <= 0 || red.size() <= 0) {
                    toastTip("当前人员不齐，无法开始PK");
                    return;
                }
                if (mSavePkReq == null) {
                    SavePkReq startPkReq = new SavePkReq();
                    startPkReq.blue = blue;
                    startPkReq.red = red;
                    if (mData != null && mData.getRoom() != null && mData.getRoom().getPk_data() != null && mData.getRoom().getPk_data().getPk_info() != null)
                        startPkReq.duration = mData.getRoom().getPk_data().getPk_info().minute;
                    startPkReq.party_id = mId;
                    getP().startPkAgain(startPkReq);
                } else {
                    mSavePkReq.blue = blue;
                    mSavePkReq.red = red;
                    getP().startPkAgain(mSavePkReq);
                }
            }
        } else if (id == R.id.iv_end_pk) {  //结束PK
            if (TextUtils.equals(mBind.clPk.ivEndPk.getText().toString(), "结束PK")) {
                getP().showEndPkPop(this, mId, mPkInfoId);
            } else if (TextUtils.equals(mBind.clPk.ivEndPk.getText().toString(), "结束惩罚")) {
                getP().stopPunish(mId, mPkInfoId);
            }
        } else if (id == R.id.iv_message) {
            if (mData != null && mData.getRoom() != null) {
                ARouterUtils.toChatListActivity(mData.getRoom().getId() + "", mData.getRoom().getRoom_name(), mData.getRoom().getIcon());
            }

        } else if (id == R.id.iv_gift) { //礼物
            if (mData != null && mData.getMicrophone() != null) {
                p.getGift("0", mData.getMicrophone());
            }
        } else if (id == R.id.iv_hour_arrow) {   //小时榜
            getP().setHourPop(this, mData, mId);
        } else if (id == R.id.show_new_msg) { //显示新消息
            mBind.showNewMsg.setVisibility(View.GONE);
            mBind.rvMessage.smoothScrollToPosition(mMsgAdapter.getItemCount() - 1);
//            mMsgLayoutManager.scrollToPosition(mMsgAdapter.getItemCount() - 1);
        }
    }

    /**
     * 显示表情
     */
    private void showEjo() {
        emoticonPickerView.show(PartyRoomActivity.this);
        emoticonPickerView.setBackGround(Utils.getColor(R.color.c2f2f2f));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtContent.getWindowToken(), 0);
        mEtContent.clearFocus();
        mEtContent.requestFocus();
        ConstraintLayout.MarginLayoutParams params = (ConstraintLayout.MarginLayoutParams) mBind.clInputMsg.getLayoutParams();
        params.height = DensityUtils.dp2px(PartyRoomActivity.this, 270);
        mBind.clInputMsg.setLayoutParams(params);
        Utils.runOnUiThreadDelayed(() -> emoticonPickerView.setVisibility(View.VISIBLE), 100);
        emoticonPickerView.setVisibility(View.VISIBLE);
        emoticonPickerView.show(PartyRoomActivity.this);
    }

    /**
     * 上麦前判断是不是管理员
     */
    private void applySeatPre(VoiceRoomSeat seat) {
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(grant -> {
                    if (grant) {
                        if (mFightPattern == 2 && isOnSeat() && !getP().isAdminAndOwner(mData)) {
                            toastTip("PK中不能跳麦");
                            return;
                        }
                        //麦序显示 申请列表
                        if ((mMicrophonePattern == 2 && !getP().isAdminAndOwner(mData)) || (mFightPattern == 2 && !getP().isAdminAndOwner(mData))) {
                            //麦序模式 显示弹窗
                            if (lineUpMacPartyPop == null) {
                                lineUpMacPartyPop = new LineUpMacPartyPop(this, mId, getP().isAdminAndOwner(mData), service.getUserId(), this);
                            }
                            lineUpMacPartyPop.setPkInfoId(mFightPattern == 1 ? 0 : mPkInfoId);
                            lineUpMacPartyPop.showPopupWindow();
                            lineUpMacPartyPop.setOnSeat(isOnSeat());
                            return;
                        }

                        if (getP().isAdminAndOwner(mData)) {
                            if (mOperatePop == null)
                                mOperatePop = new AdminOperatePopWindow(this);
                            mOperatePop.setStatus(seat.getLock(), mFightPattern, isOnSeat());
                            mOperatePop.addOnClickListener(new AdminOperatePopWindow.OnSelectListener() {
                                //上麦
                                @Override
                                public void onSeat() {
                                    getP().applySeat(mId, seat.getIndex(), mFightPattern == 1 ? 0 : mPkInfoId);
                                }

                                //锁定麦位
                                @Override
                                public void lockSeat(int lock) {
                                    getP().lockMicrophone(mId, seat.getIndex(), lock);
                                }
                            });
                            mOperatePop.showPopupWindow();
                        } else {
                            getP().applySeat(mId, seat == null ? -1 : seat.getIndex(), mFightPattern == 1 ? 0 : mPkInfoId);
                        }
                    } else {
                        ToastUtil.showToast(BaseApplication.getInstance(), "请允许录音权限");
                    }
                }));
    }


    /**
     * 进入派对获取基本信息
     */
    @Override
    public void joinPartySuccess(JoinPartyDto data, String type) {
        setData(data, type);
    }


    /**
     * 成功离开麦位
     */
    @Override
    public void leaveSeatSuccess(int position) {
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null && partyLiveType == 0) //拉流模式下麦离开房间
            BaseMusicHelper.get().getPartyService().leaveRoom();
    }


    /**
     * 获取礼物数据成功
     *
     * @param id              用户id
     * @param users           麦上用户
     * @param clickOnSeatDown 是否是麦下评论区用户
     */
    @Override
    public void getGiftSuccess(String id, List<VoiceRoomSeat> users, boolean clickOnSeatDown) {
        if (null == giftPopWindow) {
            giftPopWindow = new GiftPopWindow(mActivity, 2, 5, mId);
        }
        giftPopWindow.addOnClickListener(new GiftPopWindow.OnSelectListener() {
            @Override
            public void sendGift(GiftDto data, int num, List<String> userId, String name) {
                sendPartyGift(data, num, userId, id);
            }

            @Override
            public void getMyCoin() {
                getCoin();
            }

            @Override
            public void atUser(List<String> userId, String name) {
                if (userId != null && userId.size() > 0) {
                    if (mRoomShutUp == 1 && !getP().isAdmin(mData)) {   //0：正常 1：禁言
                        toastTip("全员禁言中，不能发言哦");
                        return;
                    }
                    if (mUserShutUp == 1) {
                        toastTip(StringUtil.isEmpty(shutUpToastMsg) ? "您已被禁言" : shutUpToastMsg);
                        return;
                    }
                    mFlagIsOnResume = true;
                    Utils.runOnUiThreadDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEtContent.setText("@" + name + " ");
                            mBind.clInputMsg.setFocusableInTouchMode(true);
                            mBind.clInputMsg.requestFocus();
                            mEtContent.setFocusableInTouchMode(true);
                            mEtContent.requestFocus();
                            Editable editable = mEtContent.getText();
                            Selection.setSelection(editable, editable.length());
                            KeyboardUtils.showSoftInput(PartyRoomActivity.this);
                        }
                    }, 300);
                }
            }

            @Override
            public void upOrDownSeat(int userId, boolean isOnSeat) {
                int index = getP().getIndex(mData, userId);
                if (isOnSeat) {   //自己下麦
                    getP().leaveSeat(mId, index, 1);
                } else {  //管理员下麦
                    getP().leaveSeat(mId, index, 2);
                }
            }

            @Override
            public void muteVoice(int userId, int voiceStatus) {
                //管理员闭麦
                VoiceRoomSeat seat = getP().getVoiceSeat(mData, userId);
                if (seat != null)
                    getP().muteMicrophone(mId, userId, getP().getIndex(mData, userId), seat.getSilence_switch() == 0 ? 2 : 0);
            }
        });
        //判断是否是管理员，是否是自己
        giftPopWindow.setPartyManager(isOnSeat(Integer.parseInt(id)),
                mData != null && mData.getUser() != null ? mData.getUser().getRole_id() : 0,
                TextUtils.equals(String.valueOf(service.getUserId()), id),
                getP().getVoiceSeat(mData, Integer.parseInt(id)) == null ? 0 : getP().getVoiceSeat(mData, Integer.parseInt(id)).getSilence_switch());
        if (users != null) { //对麦上的人送礼
            List<Integer> lists = giftPopWindow.setUserIds(users, mFlagIsOpen);
            visitPartyList11(mFlagIsOpen ? 2 : 1, id.equals("0") ? 3 : 4, id, lists);
        } else { //单人送礼
            visitPartyList11(mFlagIsOpen ? 2 : 1, id.equals("0") ? 3 : 4, id, null);
            giftPopWindow.setUserIdType(id, clickOnSeatDown ? 7 : 6, String.valueOf(mId));
        }
        getCoin();
        hideBottomInput();
        //giftPopWindow.showPopupWindow();
        giftPopWindow.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                giftPopWindow = null;
                mFlagIsOpen = false;
            }
        });
    }


    private void sendPartyGift(GiftDto data, int num, List<String> userId, String id) {
        mCompositeDisposable.add(RetrofitManager.getInstance().createIMApi(PublicService.class)
                .sendPartyGift(data.id, num, userId == null ? ("[\"" + id + "\"]") : JSON.toJSONString(userId), String.valueOf(mData.getRoom().getId()), mFightPattern)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CallbackExt>>() {
                    @Override
                    public void onSuccess(BaseResponse<CallbackExt> response) {
                        if (response != null && response.getData() != null) {
                            CallbackExt callbackExt = response.getData();
                            if (callbackExt.responseCode == 0) { //请求成功
                                if (data.combo == 1 && num == 1) {  //幸运礼物 且数量为1
                                    ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(callbackExt.callbackExt.body);
                                    sendGiftAgain(chatMsg, data, num, userId, id);
                                    if (giftPopWindow != null)
                                        giftPopWindow.dismiss();
                                } else {
                                    mBind.doubleGift.setVisibility(View.GONE);
                                    mBind.doubleGift.resetNumber();
                                    mBind.banner.setVisibility(View.VISIBLE);
                                    mBind.indicator.setVisibility(View.VISIBLE);
                                }
                                if (giftPopWindow != null) {
                                    giftPopWindow.sendGiftSuccess(data);
                                }
                            } else {
                                CommonUtil.performCallbackExt(callbackExt);
                            }
                        }
                    }
                }));
    }

    private void sendGiftAgain(ChatMsg chatMsg, GiftDto data, int num, List<String> userId, String id) {
        mBind.doubleGift.setVisibility(View.VISIBLE);
        mBind.banner.setVisibility(View.INVISIBLE);
        mBind.indicator.setVisibility(View.INVISIBLE);
        mBind.doubleGift.setRootBg(R.drawable.shape_party_double_circle);
        mBind.doubleGift.startAnimation(1, data.id);
        if (chatMsg != null) {
            ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
            if (gift != null && gift.to_name != null && gift.to_name.size() > 0) {
                for (String name : gift.to_name) {
                    GiftDto bean = new GiftDto();
                    bean.group = 1;
                    bean.image = gift.image;
                    bean.headUrl = chatMsg.from;
                    bean.toUserName = name;
                    bean.userName = gift.from_user_nickname;
                    bean.id = data.id;
                    bean.setBgResId(R.drawable.shape_patry_gift_bg);
                    showGiftAnimation(bean);
                }
            }
        }
        mBind.doubleGift.setGiftImage(data.image, data.id);
        mBind.doubleGift.setAddCountDownListener(new DoubleHitGiftChatView.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                mBind.doubleGift.setVisibility(View.GONE);
                mBind.banner.setVisibility(View.VISIBLE);
                mBind.indicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void sendAgain() {
                sendPartyGift(data, num, userId, id);
            }
        });
    }


    private void getCoin() {
        mCompositeDisposable.add(RetrofitManager.getInstance()
                .createUserApi(PartyApiService.class)
                .getCoinConfig("coin")
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CoinBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<CoinBean> response) {
                        if (response != null) {
                            CoinBean bean = response.getData();
                            giftPopWindow.setCoin(bean.coin + "");
                        }
                    }

                }));
    }


    @Override
    public void dismissDialogs(int tag, int value) {
        //设置成功 0房管，1 拉黑
    /*    if (giftPopWindow != null && giftPopWindow.mTvName != null && tag != 3) {
            toastTip(tag == 0 ? "成功设为房管" : "已将" + giftPopWindow.mTvName.getText().toString() + "拉黑并踢出派对");
        }*/
        if (PAR_LOVE_SWITCH == tag) {
            //爱意值数据
            if (mData != null && mData.getRoom() != null) {
                mData.getRoom().setLove_switch(value);
            }
            functionPopWindow.updateLoveData(value == -1 ? 0 : value);
        }

        if (giftPopWindow != null && giftPopWindow.isShowing()) {
            giftPopWindow.dismiss();
        }
    }

    @Override
    public void switchWheel(Integer data) {
        //转盘开关   "is_open": 1    // 1-开启 0-关闭
        if (data != null && data == 1) {
            svgaImageView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 管理-音乐
     */
    @Override
    public void music() {
        if (mMusicPlayerPop == null) {
            mMusicPlayerPop = new MusicPlayerPop(mActivity);
        }
        mMusicPlayerPop.showPopupWindow();

    }


    /**
     * 管理-设置
     */
    @Override
    public void set() {
        Intent intent = new Intent(this, PartySettingActivity.class);
        intent.putExtra(PARAM_ROOM_ID, mId);
        startActivity(intent);
    }


    /**
     * 设为房管
     * 0房管，1 拉黑，2踢出，3禁言
     */
    @Override
    public void setHoursManager(boolean isManager) {
        if (!isManager) {
            p.setManagerPartySetAct(String.valueOf(mId), mUseridPop, 0, 0);
        } else {
            p.setCancelManager(String.valueOf(mId), mUseridPop, 0);
        }

    }

    /**
     * 抱上麦序
     */
    @Override
    public void onMai(boolean isUp) {
        if (isUp) {
            //下麦抱序
            p.holdOnMaiKick(mId, mUseridPop);
        } else {
            p.holdOnMai(mId, mUseridPop);
        }
    }

    /**
     * 踢出房间
     */
    @Override
    public void outRoom(boolean isKi) {//弹出来第二弹窗
        if (!isKi) {
            //0 禁言  1 踢出
            if (null == partyBannedPop) {
                partyBannedPop = new PartyOptionBannedPop(this, mUseridPop, String.valueOf(mId), 1, this);
            } else {
                partyBannedPop.setTag(1, mUseridPop);
            }

            partyBannedPop.showPopupWindow();
        } else {
            p.setCancelManager(String.valueOf(mId), mUseridPop, 2);
        }

    }

    /**
     * 房间禁言
     */
    @Override
    public void roomShut(boolean isMute) {//弹出来第二弹窗
        if (!isMute) {
            //0 禁言  1 踢出
            if (null == partyBannedPop) {
                partyBannedPop = new PartyOptionBannedPop(this, mUseridPop, String.valueOf(mId), 0, this);
            } else {
                partyBannedPop.setTag(0, mUseridPop);
            }

            partyBannedPop.showPopupWindow();
        } else {
            p.setCancelManager(String.valueOf(mId), mUseridPop, 3);
        }

    }

    /**
     * 拉黑
     */
    @Override
    public void blacklist(boolean isBlack) {
        if (!isBlack) {
            //第二个参数  0房管，1 拉黑，2踢出，3禁言
            p.setManagerPartySetAct(String.valueOf(mId), mUseridPop, 1, 0);
        } else {
            p.setCancelManager(String.valueOf(mId), mUseridPop, 1);
        }

    }

    /**
     * 管理-爱意值
     */
    @Override
    public void aiYiZhi(int open) {
        int status = open == 0 ? 1 : 0;
        getP().loveSwitch(mId, status);
    }

    /**
     * 管理-管理
     */
    @Override
    public void manager() {
        if (partyManagerPopWindow == null) {
            if (mData != null && mData.getUser() != null) {
                partyManagerPopWindow = new PartyManagerPopWindow(this, mData != null && mData.getUser() != null && (mData.getUser().getRole_id() == 64 || mData.getUser().getRole_id() == 32), String.valueOf(mId));
            } else {
                partyManagerPopWindow = new PartyManagerPopWindow(this, false, String.valueOf(mId));
            }
        }
        partyManagerPopWindow.showPopupWindow();
    }

    /**
     * 管理和成员-分享
     */
    @Override
    public void share() {
        if (mPopWindowShare == null) {
            mPopWindowShare = new SharePopWindow(mActivity);
        }
        mPopWindowShare.questData(Interfaces.SHARE_QUEST_TYPE_PARTY, mData.getRoom().getId(), Utils.getText(mBind.tvPartyName))
        ;
    }

    /**
     * 管理-装扮
     */
    @Override
    public void dressUp() {
    }

    /**
     * 管理和成员-静音
     */
    @Override
    public void silence() {
        if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null)
            BaseMusicHelper.get().getPartyService().toggleMuteRoomAudio();
    }

    /**
     * 停止PK
     */
    @Override
    public void stopPkSuccess() {
        mBind.clPk.ivEndPk.setVisibility(View.GONE);
    }

    /**
     * pk
     */
    @Override
    public void pk() {
        //当前PK模式可以关闭
        if (mFightPattern == 2) {
            getP().showEndPk(this, mId);
            if (functionPopWindow != null)
                functionPopWindow.dismiss();
            return;
        }
        CreatePkPopWindow mCreatePkPopWindow = new CreatePkPopWindow(this, mId);
        mCreatePkPopWindow.addOnClickListener(new CreatePkPopWindow.OnSelectListener() {
            @Override
            public void savePkSuccess(int pk_info_id, SavePkReq savePkReq) {
                mPkInfoId = pk_info_id;
                mSavePkReq = savePkReq;
            }
        });
        if (mData != null && mData.getMicrophone() != null && mData.getMicrophone().size() > 0) {
            mCreatePkPopWindow.setRoomSeat(mData.getMicrophone().subList(1, mData.getMicrophone().size()), mData.getMicrophone().get(0));
        }
        mCreatePkPopWindow.showPopupWindow();
        if (functionPopWindow != null)
            functionPopWindow.dismiss();
    }

    /**
     * 禁言、踢出 成功
     * 2踢出，3禁言
     */
    @Override
    public void outSuccess(int type) {
      /*  if (giftPopWindow != null && giftPopWindow.isShowing() && giftPopWindow.mTvName != null) {
            ToastUtil.showToast(mContext, type == 3 ? "成功禁言" : giftPopWindow.mTvName.getText().toString() + "已被踢出派对 ");
        }*/
        if (giftPopWindow != null && giftPopWindow.isShowing()) {
            giftPopWindow.dismiss();
        }
    }

    @Override
    public void specialEffects() {
        //特效管理

        if (mData == null || mData.getRoom() == null) {
            return;
        }
        if (specialEffectsPartyPop == null) {
            specialEffectsPartyPop = new SpecialEffectsPartyPop(this, mData.getRoom().getDress_switch(), mData.getRoom().getGift_switch(), this);
        }
        specialEffectsPartyPop.setSwitch(mData.getRoom().getGift_switch(), mData.getRoom().getDress_switch());
        specialEffectsPartyPop.showPopupWindow();

    }

    @Override
    public void earphoneSetting() {
        if (mEarphoneSettingPop == null)
            mEarphoneSettingPop = new EarphoneSettingPop(this);
        mEarphoneSettingPop.setEnableEarBack(mEnableEarBack);
        mEarphoneSettingPop.setHeadStatus(mHeadStatus);
        mEarphoneSettingPop.addOnClickListener(new EarphoneSettingPop.OnSelectListener() {
            @Override
            public void enableEarBack(boolean status) {
                mEnableEarBack = status;
                if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                    BaseMusicHelper.get().getPartyService().setEnableEarBack(mEnableEarBack);
                }
            }
        });
        mEarphoneSettingPop.showPopupWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        // 键盘区域外点击收起键盘
        if (!ViewUtils.isInView(mBind.clInputMsg, x, y)) {
            // hideBottomInput();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showBottomInput() {
        Bitmap blur = HokoBlur.with(mContext).radius(12).blur(mBind.rootCl);
        mBind.muhuView.setBackground(ImageUtils.bitmap2Drawable(blur));
        mMsgLayoutManager.scrollToPosition(mMsgAdapter.getItemCount() - 1);

        mBind.muhuView.setVisibility(View.VISIBLE);
        mBind.inputView.setVisibility(View.VISIBLE);
        mBind.clInputMsg.setVisibility(View.VISIBLE);
        new ConstraintUtil(mBind.rootCl).begin()
                .setHeight(R.id.rv_message, mBind.rvMessage.getHeight())
                .Bottom_toTopOf(R.id.rv_message, R.id.cl_input_msg)
//                .Top_toTopOf(R.id.rv_message, R.id.ll_info)
                .Top_toTopOf(R.id.rv_message, -1)
                .commit();
    }

    private void hideBottomInput() {
        mBind.muhuView.setVisibility(View.GONE);
        mBind.inputView.setVisibility(View.GONE);
        KeyboardUtils.hideSoftInput(this);
        mBind.clInputMsg.setVisibility(View.INVISIBLE);
        new ConstraintUtil(mBind.rootCl).begin()
                .setHeight(R.id.rv_message, 0)
                .Bottom_toTopOf(R.id.rv_message, R.id.ll_send_message)
                .Top_toBottomOf(R.id.rv_message, R.id.cl_seat)
                .commit();
    }

    @Override
    public void onEmojiSelected(String key) {
        Editable mEditable = mEtContent.getText();
        if (key.equals("/DEL")) {
            mEtContent.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        } else {
            int start = mEtContent.getSelectionStart();
            int end = mEtContent.getSelectionEnd();
            start = (start < 0 ? 0 : start);
            end = (start < 0 ? 0 : end);
            mEditable.replace(start, end, key);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnnouncePop != null)
            mAnnouncePop.dismiss();
        if (mAnnouncementBack != null)
            mAnnouncementBack.destroy();
        mBind.playerView.stopPlay();
        if (mSvgBack != null) mSvgBack.destroy();
        myGiftList.clear();
        safeHandle.removeCallbacksAndMessages(null);
        mBind.svgaImageView.clearAnimation();
        mBind.svgaLucky.clearAnimation();
        mBind.svgImageOnRoom.clearAnimation();
        mBind.svgaHead.clearAnimation();
        mBind.PartyScrollerView.stopLoop();
        int close = MMKVUtils.getInstance().decodeInt(Constants.PARAM_IS_CALL_CLOSE);
        if (close == 1) {
            getP().joinLeaveParty("close", mId, 0);
            Intent intent = new Intent(this, PartyAudioService.class);
            stopService(intent);
            if (BaseMusicHelper.get() != null && BaseMusicHelper.get().getPartyService() != null) {
                BaseMusicHelper.get().getPartyService().releaseAudience();
                if (partyLiveType == 1)  // rtc模式
                    BaseMusicHelper.get().getPartyService().leaveRoom();
                BaseMusicHelper.get().getPartyService().removeCallBack();
                BaseMusicHelper.get().getPartyService().onDestroy();
            }
            NIMClient.getService(ChatRoomService.class).exitChatRoom(mRoomId);
        }
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName) {

    }

    @Override
    public void stopAniShowPop() {
        //显示转盘结果通知
        showWindowResult();
    }

    private void startThread() {
        if (null == safeHandle) {
            safeHandle = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Interfaces.EVENT_MESSAGE) {
                        try {
                            playGift();
                            showFloatGift();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        safeHandle.sendEmptyMessageDelayed(Interfaces.EVENT_MESSAGE, 1000);
                    }
                }
            };
        }
        if (safeHandle.hasMessages(Interfaces.EVENT_MESSAGE)) return;
        safeHandle.sendEmptyMessage(Interfaces.EVENT_MESSAGE);
    }

    GiftDto peedData;

    /**
     * 开始播放动画
     */
    private void playGift() {
        if (mBind.svgaImageView.isAnimating() || mIsDown || myGiftList.isEmpty()) return;
        peedData = myGiftList.peek();
        if (peedData == null) {
            return;
        }
        myGiftList.poll();
        File file = new File(DownloadHelper.FILE_PATH + File.separator + peedData.name);
        if (file.exists()) {
            playAnimation(file, peedData);
        } else {
            mIsDown = true;
            DownloadHelper.downloadGift(peedData.animation, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    mIsDown = false;
                    playAnimation(file, peedData);
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
        Utils.logE("onComplete: " + mIsDown + mBind.svgaImageView.isAnimating() + myGiftList.isEmpty() + "====" + file.getAbsolutePath());
        if (file.getAbsolutePath().endsWith(".mp4")) {
            mBind.playerView.setVisibility(View.VISIBLE);
            mBind.playerView.startPlay(file);
            return;
        }
        mBind.svgaImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mIsPlay = false;
        }
    }

    /**
     * 显示礼物动画
     *
     * @param gift    礼物数据
     * @param chatMsg 判断发送者，区分自己发送还是
     */
    @Override
    public void showGift(PartyGiftDto gift, ChatMsg chatMsg) {
        if (gift != null && gift.gift_info != null) {
            if (!myGiftList.isEmpty()) {
                for (GiftDto giftDto : myGiftList) {
                    if (giftDto.id == gift.gift_info.id) {
                        return;
                    }
                }
            }
            if (gift.gift_info.animations != null && !gift.gift_info.animations.isEmpty()) {
                for (String ani : gift.gift_info.animations) {
                    addOffer(gift, ani, chatMsg);
                }
            } else {
                addOffer(gift, gift.gift_info.animation, chatMsg);
            }
        }
    }

    private void addOffer(PartyGiftDto gift, String ani, ChatMsg chatMsg) {
        GiftDto bean = new GiftDto();
        bean.group = gift.gift_num;
        bean.name = Utils.getFileName(ani).replace(".svga", "");
        bean.image = gift.gift_info.image;
        bean.animation = ani;
        bean.headUrl = gift.fromAccountIcon;
        bean.toUserName = gift.toName;
        bean.userName = gift.formName;
        bean.id = gift.gift_info.id;
        bean.setBgResId(R.drawable.shape_patry_gift_bg);
        if (gift.gift_info.animationType == 1) {    //动效类型:1.普通PNG 2.炫 3.动
            if (gift.gift_info.combo == 1 && gift.gift_num == 1 && TextUtils.equals(chatMsg.from, service.getUserId() + "")) {  //幸运礼物 且数量为1
                return;
            }
            showGiftAnimation(bean);
        } else {
            if (giftPopWindow != null && gift.form_id == service.getUserId()/* && !isBlinkType(gift)*/)
                giftPopWindow.dismiss();
            //不显示礼物动画
            if (mData != null && mData.getRoom() != null && mData.getRoom().getGift_switch() == 1) {
                return;
            }
            if (peedData != null && peedData.id == gift.gift_info.id)
                return;
            myGiftList.offer(bean);
        }
    }

    /**
     * 是否是盲盒类型
     *
     * @param gift
     * @return
     */
    private boolean isBlinkType(PartyGiftDto gift) {
        return gift != null && gift.gift_info != null && gift.gift_info.ext != null && gift.gift_info.ext.blink_type == 1;
    }

    public void showGiftAnimation(GiftDto bean) {
        if (bean == null) return;
        mBind.include.giftRoot.setVisibility(View.VISIBLE);

        if (null == giftViewListener) {
            giftViewListener = new GiftRootLayout.GiftRootListener() {
                @Override
                public void showGiftInfo(GiftDto giftBean) {
                }

                @Override
                public void showGiftAmin(GiftDto giftBean, int index) {
                    if (giftBean == null)
                        return;
                }

                @Override
                public void hideGiftAmin(int index, int giftId) {
                    if (index == 1) {
                        mBind.include.gift1.clearAnimation();
                        mBind.include.gift1.setVisibility(View.GONE);
                    } else if (index == 2) {
                        mBind.include.gift2.clearAnimation();
                        mBind.include.gift2.setVisibility(View.GONE);
                    } else if (index == 3) {
                        mBind.include.gift3.clearAnimation();
                        mBind.include.gift3.setVisibility(View.GONE);
                    }
                    if (mBind.include.gift1.getVisibility() == View.GONE && mBind.include.gift2.getVisibility() == View.GONE && mBind.include.gift3.getVisibility() == View.GONE) {
                        mBind.include.giftRoot.setVisibility(View.GONE);
                        mBind.include.giftRoot.invalidate();
                    }

                }
            };
        }
        mBind.include.giftRoot.setPlayGiftEndListener(giftViewListener);
        mBind.include.giftRoot.loadGift(bean);
    }

    private boolean mFlagIsOpen;

    @Override
    public void openBag() {
        if (mData != null && mData.getMicrophone() != null) {
            mFlagIsOpen = true;
            p.getGift("0", mData.getMicrophone());
        }
    }

    @Override
    public void tagItem(int position) {
        //曝光埋点
        visitPartyList(7, position, "", -1);
    }

    @Override
    public void tagDrawNum(int position, int tag) {
        //抽奖按钮点击	埋点
        visitPartyList(8, position, "", tag);
    }

    @Override
    public void rankingCallback() {
        visitPartyList(9, 0, "", 0);
    }

    @Override
    public void recordCallback() {
        visitPartyList(10, 0, "", 0);
    }

    @Override
    public void failedToSendMessage(String msg_id) { //发送消息失败的处理
        if (mMsgAdapter == null) {
            return;
        }
        mBind.rvMessage.postDelayed(() -> {
            List<MultipleChatRoomMessage> data = mMsgAdapter.getData();
            for (int i = data.size() - 1; i > -1; i--) { //倒序遍历
                MultipleChatRoomMessage mcr = data.get(i);
                ChatRoomMessage message = mcr.getMessage();
                if (TextUtils.equals(message.getUuid(), msg_id)) { //判断消息id一致
                    message.setStatus(MsgStatusEnum.fail);
                    mMsgAdapter.setData(i, mcr);
                    break;
                }
            }
        }, 500);
    }

    @Override
    public void bottomSeat() {
        if (mLeavePop == null)
            mLeavePop = new LeaveOnSeatPopWindow(PartyRoomActivity.this, "");
        mLeavePop.addOnClickListener(() -> getP().leaveSeat(mId, p.getIndex(mData, service.getUserId()), 1));
        mLeavePop.showPopupWindow();

    }

    @Override
    public void specialEffectsListener(int type, int status) {
        //设置成功
        if (mData != null && mData.getRoom() != null) {
            if (type == 1) {
                mData.getRoom().setGift_switch(status);
            } else
                mData.getRoom().setDress_switch(status);
        }
    }
}
