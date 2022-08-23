package com.tftechsz.im.mvp.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.business.recent.RecentContactsCallback;
import com.netease.nim.uikit.business.recent.TeamMemberAitHelper;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.badger.Badger;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nim.uikit.impl.cache.StickTopCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.StickTopSessionInfo;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.MessageAdapter;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.model.event.MessageListEvent;
import com.tftechsz.im.model.event.MessageRefreshListEvent;
import com.tftechsz.im.mvp.iview.IChatView;
import com.tftechsz.im.mvp.presenter.ChatPresenter;
import com.tftechsz.im.mvp.ui.activity.ActivityNoticeActivity;
import com.tftechsz.im.mvp.ui.activity.PullWiresRecordActivity;
import com.tftechsz.im.uikit.P2PMessageActivity;
import com.tftechsz.im.widget.MLinearLayoutManager;
import com.tftechsz.im.widget.pop.MessageOperationPopWindow;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.MessageInfo;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.entity.UserIDs;
import com.tftechsz.common.entity.UserOnline;
import com.tftechsz.common.event.AccostNowEvent;
import com.tftechsz.common.event.ChatMessageEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageEvent;
import com.tftechsz.common.event.RecruitBaseDto;
import com.tftechsz.common.event.UnReadMessageEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.AutoVerticalTextView;
import com.tftechsz.common.widget.ChatRoomItemLayout;
import com.tftechsz.common.widget.chat.ChatTimeUtils;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.RectangleIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouterApi.FRAGMENT_CHAT)
public class ChatFragment extends BaseMvpFragment<IChatView, ChatPresenter> implements View.OnClickListener, IChatView {
    private final int EVENT_MESSAGE = 100000;
    private int mType;  //1 密友
    private RecyclerView mRvMessage;
    private MessageAdapter mAdapter;
    private List<ContactInfo> items = new ArrayList<>();
    private boolean msgLoaded = false;
    private RecentContactsCallback callback;
    private UserInfoObserver userInfoObserver;
    private TextView emptyBg;
    private TextView mTvTeamName;
    private TextView mTvTeamTime;
    private TextView mTvTeamContent;
    private String teamId;
    private RelativeLayout mRlTeamAvatar;
    private ImageView mCiAvatar;
    private ImageView mIvMine;
    private boolean isFragmentVisible;
    private ImageView mIvLevel;
    private TextView mTvLevel;
    private RecentContact mTeamContact;
    private TextView mTvTeamBadge;
    private ImageView mIvMute;
    private View view2;
    private TextView mTvPlayNum;  //玩的人数

    private TextView mTvApply;  //申请审核数量
    //聊天广场
    private ChatRoomItemLayout mClRoom;
    private View view;


    private List<MessageInfo> messageInfoList;

    //缘分牵线
    private ConstraintLayout mClPullWires;
    private AutoVerticalTextView mTvPullWiresContent;
    private TextView mTvNoTeam;
    private TextView mTvPullWiresTime;
    private Handler pullWiresHandler;
    private final ArrayList<String> mPullWiresContent = new ArrayList<>();
    @Autowired
    UserProviderService service;
    private Banner<ChatHistoryDto.Banner, BannerImageAdapter<ChatHistoryDto.Banner>> mBanner;
    private int mIntimacyNum;
    private boolean isTopTag, isBannerInit, isNoTeam;

    private View viewHead;
    private long mTempCurrentTime;

    private int isUserOnline = 0;
    private MLinearLayoutManager mLayoutManager;

    private boolean mIsChooseDel = false;


    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter();
    }


    public static ChatFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null)
            mType = getArguments().getInt("type", 1);
        if (getActivity() != null)
            getActivity().getWindow().setBackgroundDrawable(null);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mRvMessage = getView(R.id.rv_message);
        emptyBg = getView(R.id.emptyBg);
        mLayoutManager = new MLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        if (((SimpleItemAnimator) mRvMessage.getItemAnimator()) != null)
            ((SimpleItemAnimator) mRvMessage.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMessage.setFocusable(false);
        mRvMessage.setItemAnimator(null);
        mRvMessage.setLayoutManager(mLayoutManager);
        viewHead = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_header, null);
        view2 = viewHead.findViewById(R.id.view2);
        //群组信息
        mTvTeamName = viewHead.findViewById(R.id.tv_team_name);
        mTvTeamContent = viewHead.findViewById(R.id.tv_team_content);
        mTvTeamTime = viewHead.findViewById(R.id.tv_team_time);
        mBanner = viewHead.findViewById(R.id.banner);
        //群组信息
        ConstraintLayout mRlTeam = viewHead.findViewById(R.id.rl_team);
        mRlTeam.setOnClickListener(this);  //家族
        mRlTeamAvatar = viewHead.findViewById(R.id.rl_team_avatar);  //家族头像
        mIvMute = viewHead.findViewById(R.id.iv_mute);
        mTvTeamBadge = viewHead.findViewById(R.id.tv_team_badge);
        mIvLevel = viewHead.findViewById(R.id.iv_level);
        mTvLevel = viewHead.findViewById(R.id.tv_level);
        mCiAvatar = viewHead.findViewById(R.id.ci_team_avatar);
        mIvMine = viewHead.findViewById(R.id.iv_mine);  //是否是我的家族
        mClRoom = viewHead.findViewById(R.id.cl_room);
        view = viewHead.findViewById(R.id.view);
        mTvApply = viewHead.findViewById(R.id.tv_apply);
        mTvApply.setOnClickListener(this);
        mTvPlayNum = viewHead.findViewById(R.id.tv_play_num);
        mTvNoTeam = viewHead.findViewById(R.id.tv_no_team);
        //缘分牵线
        mClPullWires = viewHead.findViewById(R.id.cl_pull_wires);
        mClPullWires.setOnClickListener(this);
        mTvPullWiresContent = viewHead.findViewById(R.id.tv_pull_wires_content);
        mTvPullWiresTime = viewHead.findViewById(R.id.tv_pull_wires_time);
        ImageView ivWiresAvatar = viewHead.findViewById(R.id.iv_pull_wires_avatar);
        ivWiresAvatar.setImageResource(R.mipmap.bg_pull_wires);
        ImageView ivWiresTitle = viewHead.findViewById(R.id.tv_pull_wires_title);
        ivWiresTitle.setImageResource(R.mipmap.chat_ic_pull_wires);
        mBanner.setVisibility(View.GONE);
        mClRoom.setVisibility(View.GONE);
        messageInfoList = new ArrayList<>();
        registerObservers(true);
        registerOnlineStateChangeListener(true);
        initAdapter();
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.user != null)
            mIntimacyNum = service.getConfigInfo().sys.user.intimacy_friend_condition_num;
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null)
            isUserOnline = service.getConfigInfo().sys.is_user_intimacy_online;
        if (mType == 1) {   //密友
//            第一次进入密友页面加载网络数据存储
            boolean isFirstMessage = MMKVUtils.getInstance().decodeBoolean(service.getUserId() + Constants.IS_FIRST_IN_MESSAGE);
            if (!isFirstMessage) {
                p.appInit();
            }
            mRlTeam.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            emptyBg.setPadding(0, DensityUtils.dp2px(BaseApplication.getInstance(), 10), 0, 0);
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.im != null)
                emptyBg.setText(service.getConfigInfo().sys.im.intimacy_friend_empty_text);
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_empty);
            emptyBg.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable, null, null);

            if (service.getUserInfo() == null) {
                p.getUserInfo();
            }
        } else if (mType == 0) {
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.is_show_family == 1) {
                mRlTeam.setVisibility(View.VISIBLE);
            } else {
                mRlTeam.setVisibility(View.GONE);
            }
            requestMessages(true);
            p.getChatHistory();
            if (service.getUserInfo() == null) {
                p.getUserInfo();
            } else {
                setNoMessage();
            }
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.chat_ic_no_message);
            emptyBg.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable, null, null);
        }
    }


    private void initAdapter() {
        // adapter
        mAdapter = new MessageAdapter(items, mType);
        mAdapter.onAttachedToRecyclerView(mRvMessage);
        initCallBack();
        mAdapter.setCallback(callback);
        mRvMessage.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.fl_iv_avatar, R.id.root);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if(mIsChooseDel) return;
            if (mAdapter.getData().get(position) != null && TextUtils.equals(mAdapter.getData().get(position).getContactId(), Constants.CUSTOMER_SERVICE)) {
                startActivity(ActivityNoticeActivity.class, "chat_user", Constants.CUSTOMER_SERVICE);
                return;
            }
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            if (TextUtils.equals(mAdapter.getData().get(position).getContactId(), Constants.CUSTOMER_SERVICE)) {
                startActivity(ActivityNoticeActivity.class, "chat_user", Constants.CUSTOMER_SERVICE);
            } else {
                int id = view.getId();
                if (id == R.id.fl_iv_avatar) {
                    ARouterUtils.toMineDetailActivity(mAdapter.getData().get(position).getContactId());
                } else if (id == R.id.root) {
                    ContactInfo contactInfo = mAdapter.getData().get(position);
                    P2PMessageActivity.start(getActivity(), contactInfo.contactId, NimUIKit.getCommonP2PSessionCustomization(), null, 0);
                }
            }
        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return false;
            ContactInfo recent = mAdapter.getItem(position);
            if (recent != null && !TextUtils.equals(Constants.CUSTOMER_SERVICE, recent.getContactId()) && mType == 0) {   //消息 才可以操作
                MessageOperationPopWindow pop = new MessageOperationPopWindow(getActivity(), recent);
                pop.addOnClickListener(type -> {
                    if (type == 0) {   //置顶取消置顶
                        if (mType == 0) {
                            Utils.runOnUiThread(() -> {
                                isTopTag = true;
                                refreshMessages(false, false);
                            });
                        }
                    } else if (type == 1) {   //删除
                        if (mType == 0) {
                            mAdapter.remove(recent);
                            Utils.runOnUiThread(() -> refreshMessages(true, true));
                        }
                    }
                });
                pop.showPopupWindow();
            }
            return false;
        });
        mRvMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mType == 1 && mAdapter.getItemCount() > 0 && mLayoutManager != null) {  //停止滑动时
                        getOnLineStatus(mAdapter.getData(), mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition());
                    }
                }
            }
        });
        if (mType == 0)
            mAdapter.addHeaderView(viewHead);

    }

    private void getOnLineStatus(List<ContactInfo> contactInfos, int firstVisiblePos, int lastVisiblePos) {
        if(isUserOnline == 0) return;
        if (contactInfos == null || contactInfos.isEmpty() || firstVisiblePos == -1 || lastVisiblePos == -1)
            return;
        long timeMillis = System.currentTimeMillis();
        ArrayList<String> ids = new ArrayList<>();
        for (int i = firstVisiblePos; i <= lastVisiblePos; i++) {
            if (contactInfos.size() > i) {
                ContactInfo item = contactInfos.get(i);
                if (timeMillis - item.tag_time > 30000) {
                    ids.add(item.getContactId());
                }
            }
        }
        if (ids.isEmpty()) return;
        mCompositeDisposable.add(RetrofitManager.getInstance().createUserApi(PublicService.class)
                .is_online(new UserIDs(ids))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResponseObserver<BaseResponse<List<UserOnline>>>() {
                    @Override
                    public void onNext(BaseResponse<List<UserOnline>> response) {
                        if (response != null) {
                            List<UserOnline> data = response.getData();
                            if (data != null && !data.isEmpty()) {
                                for (int i = firstVisiblePos; i <= lastVisiblePos; i++) {
                                    if (contactInfos.size() > i) {
                                        ContactInfo item = contactInfos.get(i);
                                        for (int j = 0; j < data.size(); j++) {
                                            UserOnline userOnline = data.get(j);
                                            if (TextUtils.equals(item.getContactId(), String.valueOf(userOnline.user_id))) {
                                                item.is_online = userOnline.is_online == 1;
                                                item.tag_time = timeMillis;
                                                if (mAdapter.getData().size() > i) {
                                                    AvatarVipFrameView ivAvatar = (AvatarVipFrameView) mAdapter.getViewByPosition(i, R.id.iv_avatar);
                                                    if (ivAvatar != null) {
                                                        ivAvatar.setOnline(userOnline.is_online == 1);
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onSuccess(BaseResponse<List<UserOnline>> listBaseResponse) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    /**
     * 刷新消息
     */
    private void refreshMessages() {
        Utils.runOnUiThread(() -> refreshMessages(true, true));
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initData() {
        initRxBus();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentVisible) {
            return;
        }
        MMKVUtils.getInstance().encode(Constants.VOICE_IS_OPEN, 0);
        if (mType == 0 && isAdded()) {
            if (!isBannerInit || (p != null && System.currentTimeMillis() - mTempCurrentTime > 5000)) {
                //十分钟 活动需要刷新
                p.getChatHistory();
                mTempCurrentTime = System.currentTimeMillis();//在请求回调里面也有
            }

            if (CommonUtil.isShowApplyNum(service.getUserId(), mTvApply, 2)) {
                mTvApply.setVisibility(View.VISIBLE);
                mTvApply.setCompoundDrawablesWithIntrinsicBounds(null,
                        null, null, null);
            } else {
                mTvApply.setVisibility(View.GONE);
            }
            startThread();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFragmentVisible = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);
        if (mType == 1 && isAdded() && mLayoutManager != null && isVisibleToUser) {
            getOnLineStatus(mAdapter.getData(), mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition());
        }
        if (isVisibleToUser && mType == 0 && isAdded() && p != null) {
            p.getChatHistory();
            startThread();
        } else {
            if (pullWiresHandler != null)
                pullWiresHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (pullWiresHandler != null)
            pullWiresHandler.removeCallbacksAndMessages(null);
    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(ChatMessageEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.EVENT_CHAT_MESSAGE_TOP) {   //置顶通知
                                refreshMessages();
                            }
                        }
                ));
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_FAMILY_MESSAGE_MUTE) {  // 免打扰通知
                                if (!TextUtils.isEmpty(teamId)) {
                                    Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
                                    if (team != null && team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.All) {
                                        mIvMute.setVisibility(View.GONE);
                                    } else {
                                        mIvMute.setVisibility(View.VISIBLE);
                                    }
                                }
                                //隐藏列表通知
                                refreshMessages(true, true);
                            } else if (event.type == Constants.NOTIFY_MESSAGE_DOUBLE_CLICK) {  //双击了消息
                                int position = p.findUnReadPosition(items, mRvMessage);
                                p.smoothMoveToPosition(mRvMessage, position);
                            } else if (event.type == Constants.NOTIFY_DELETE_MESSAGE) {   //删除聊天
                                if (mAdapter != null && mType == 0) {
                                    Utils.runOnUiThread(() -> {
                                        mAdapter.setCheckShow(true);
                                        mIsChooseDel = true;
//                                        p.setTranX(mRvMessage);
                                    });
                                }
                            } else if (event.type == Constants.NOTIFY_DELETE_CHOOSE_MESSAGE) {  //删除选中的item
                                if (mType == 0) {
                                    Utils.runOnUiThread(() -> {
                                        for (int i = mAdapter.getData().size() - 1; i >= 0; i--) {
                                            if (mAdapter.getData().get(i).isSelected()) {
                                                NIMClient.getService(MsgService.class).deleteRecentContact(mAdapter.getData().get(i));
                                                NIMClient.getService(MsgService.class).clearChattingHistory(mAdapter.getData().get(i).contactId, SessionTypeEnum.P2P);
                                                mAdapter.remove(mAdapter.getData().get(i));
                                            }
                                        }
                                    });
                                    refreshMessages();
                                    mAdapter.setCheckShow(false);
                                    mIsChooseDel = false;
                                    p.setBackTranX(mRvMessage);
                                }
                            } else if (event.type == Constants.NOTIFY_REMOVE_USER) {   //移除用户
                                Utils.runOnUiThread(() -> {
                                    for (int i = mAdapter.getData().size() - 1; i >= 0; i--) {
                                        if (TextUtils.equals(mAdapter.getData().get(i).getContactId(), String.valueOf(event.familyId))) {
                                            NIMClient.getService(MsgService.class).deleteRecentContact(mAdapter.getData().get(i));
                                            NIMClient.getService(MsgService.class).clearChattingHistory(mAdapter.getData().get(i).contactId, SessionTypeEnum.P2P);
                                            mAdapter.remove(mAdapter.getData().get(i));
                                        }
                                    }
                                });
                                refreshMessages();
                            } else if (event.type == Constants.NOTIFY_DELETE_MESSAGE_CANCEL) {  //取消删除
                                if (mAdapter.getShow()) {
                                    mAdapter.setCheckShow(false);
                                    mIsChooseDel = false;
                                    p.setBackTranX(mRvMessage);
                                }
                            } else if (event.type == Constants.NOTIFY_UPDATE_INTIMACY) {   //更新亲密度
                                if (mType == 1) {
                                    p.updateIntimacy(items);
                                }
                            } else if (event.type == Constants.NOTIFY_DELETE_CHOOSE_MESSAGE_ALL) { //全选
                                int size = mAdapter.getData().size();
                                for (int i = 0; i < size; i++) {
                                    if (!TextUtils.equals(Constants.CUSTOMER_SERVICE, mAdapter.getData().get(i).getContactId())) {
                                        ContactInfo item = mAdapter.getData().get(i);
                                        item.setSelected(true);
                                        mAdapter.setData(i, item);
                                    }
                                }
                            } else if (event.type == Constants.NOTIFY_REFRESH_RECOMMEND) {  //加入和离开家族通知
                                if (mType == 0) {
                                    p.getChatHistory();
                                }
                            } else if (event.type == Constants.NOTIFY_UPDATE_DISSOLUTION_SUCCESS) { //家族解散成功 隐藏item
//                                mRlTeam.setVisibility(View.GONE);
                            } else if (event.type == Constants.NOTIFY_FAMILY_RECRUIT) { //招募红包推送
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RecruitBaseDto dto = JSON.parseObject(event.code, RecruitBaseDto.class);
                                        if (TextUtils.isEmpty(teamId) && mType == 0) {
                                            if (dto == null || dto.value == 0) {
                                                mTvApply.setVisibility(View.GONE);
                                            } else {
                                                Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.chat_icon_red);
                                                mTvApply.setCompoundDrawablesWithIntrinsicBounds(drawable,
                                                        null, null, null);
                                                mTvApply.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 3));
                                                mTvApply.setVisibility(View.VISIBLE);
                                                mTvApply.setText(dto.value + "个招募红包");
                                            }
                                        }
                                    }
                                });
                            } else if (event.type == Constants.NOTIFY_USER_APPLY_JOIN) {  //申请审核
                                if (CommonUtil.isShowApplyNum(service.getUserId(), mTvApply, 2)) {
                                    mTvApply.setVisibility(View.VISIBLE);
                                    mTvApply.setCompoundDrawablesWithIntrinsicBounds(null,
                                            null, null, null);
                                } else {
                                    mTvApply.setVisibility(View.GONE);
                                }
                            } else if (Constants.NOTIFY_USER_ACCOST.equals(event.code)) { //缘份牵线推荐开关
                                if (event.type == 0) {
                                    if (service.getUserInfo().getSex() == 2 && service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.girl_is_show_sys_accost_slide == 1) {
                                        mClPullWires.setVisibility(View.VISIBLE);
                                        //派对女用户
                                        if (service.getUserInfo().isPartyGirl() && service.getConfigInfo().sys.girl_is_show_sys_accost_slide_party == 0) {
                                            mClPullWires.setVisibility(View.GONE);
                                        }
                                        String pullWriesContent = MMKVUtils.getInstance().decodeString(Constants.PULL_WRIES_CONTENT + service.getUserId());
                                        if (!TextUtils.isEmpty(pullWriesContent)) {
                                            mTvPullWiresTime.setText(ChatTimeUtils.getChatTime(MMKVUtils.getInstance().decodeLong(Constants.PULL_WRIES_TIME + service.getUserId())));
                                            setPullWiresData(pullWriesContent);
                                        } else {
                                            setPullWiresData("暂无正在牵线的人～");
                                        }
                                    }
                                } else {
                                    mClPullWires.setVisibility(View.GONE);
                                }


                            }
                        }
                ));

        //刷新数据
        if (mType == 0) {
            mCompositeDisposable.add(RxBus.getDefault().toObservable(MessageRefreshListEvent.class)
                    .compose(this.bindToLifecycle())
                    .subscribe(
                            event -> {
                                int size = items.size();
                                if (null != event.contactList && event.contactList.size() > 0) {
                                    for (int i = 0; i < event.contactList.size(); i++) {
                                        for (int j = 0; j < size; j++) {
                                            if (TextUtils.equals(event.contactList.get(i).user_id, items.get(j).contactId)) {
                                                items.get(j).intimacy_val = Float.parseFloat(event.contactList.get(i).value);
                                            }
                                        }
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                    ));
            mCompositeDisposable.add(RxBus.getDefault().toObservable(AccostNowEvent.class)
                    .compose(this.bindToLifecycle())
                    .subscribe(
                            event -> {
                                ChatMsg.AccostNow accostNow = new ChatMsg.AccostNow();
                                accostNow.pic = event.pic;
                                accostNow.title = event.title;
                                accostNow.desc = event.desc;
                                accostNow.timestamp = event.timestamp;
                                pullWiresMessage.add(accostNow);
                            }
                    ));
        }

        if (mType == 1) {
            mCompositeDisposable.add(RxBus.getDefault().toObservable(MessageListEvent.class)
                    .compose(this.bindToLifecycle())
                    .subscribe(
                            event -> {
                                loadedRecents = event.contactList;
                                getContactInfoSuccess();

                            }
                    ));
        }
        if (service.getUserInfo() != null && service.getUserInfo().isGirl() && mType == 0)
            startThread();

    }

    private final Queue<ChatMsg.AccostNow> pullWiresMessage = new ConcurrentLinkedQueue<>();


    protected void startThread() {
        if (null == pullWiresHandler) {
            pullWiresHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == EVENT_MESSAGE) {
                        try {
                            if (!pullWiresMessage.isEmpty()) {
                                ChatMsg.AccostNow data = pullWiresMessage.peek();
                                if (data == null) {
                                    return;
                                }
                                setPullWiresData(data.desc);
                                mTvPullWiresTime.setText(ChatTimeUtils.getChatTime(data.timestamp * 1000));
                                MMKVUtils.getInstance().encode(Constants.PULL_WRIES_CONTENT + service.getUserId(), data.desc);
                                MMKVUtils.getInstance().encode(Constants.PULL_WRIES_TIME + service.getUserId(), data.timestamp * 1000);
                                pullWiresMessage.poll();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pullWiresHandler.sendEmptyMessageDelayed(EVENT_MESSAGE, 1500);
                    }
                }
            };
        }
        if (pullWiresHandler.hasMessages(EVENT_MESSAGE)) return;
        pullWiresHandler.sendEmptyMessage(EVENT_MESSAGE);
    }


    @Override
    public void onDestroy() {
        registerObservers(false);
        registerOnlineStateChangeListener(false);
        if (pullWiresHandler != null)
            pullWiresHandler.removeMessages(EVENT_MESSAGE);
        super.onDestroy();
    }


    private void initCallBack() {
        if (callback != null) {
            return;
        }
        callback = new RecentContactsCallback() {

            @Override
            public void onRecentContactsLoaded() {
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
            }

            @Override
            public void onItemClick(RecentContact recent) {
                if (recent.getSessionType() == SessionTypeEnum.SUPER_TEAM) {
                    ToastHelper.showToast(getActivity(), getString(com.netease.nim.uikit.R.string.super_team_impl_by_self));
                } else if (recent.getSessionType() == SessionTypeEnum.Team) {
                    NimUIKit.startTeamSession(getActivity(), recent.getContactId());
                } else if (recent.getSessionType() == SessionTypeEnum.P2P) {
                    NimUIKit.startP2PSession(getActivity(), recent.getContactId());
                }
            }

            @Override
            public String getDigestOfAttachment(RecentContact recentContact, MsgAttachment attachment) {
                return null;
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                return null;
            }
        };
    }


    OnlineStateChangeObserver onlineStateChangeObserver = accounts -> notifyDataSetChanged();

    private void registerOnlineStateChangeListener(boolean register) {
        if (!NimUIKitImpl.enableOnlineState()) {
            return;
        }
        NimUIKitImpl.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(onlineStateChangeObserver,
                register);
    }


    private List<ContactInfo> loadedRecents;

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (msgLoaded) {
                return;
            }
            NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
                @Override
                public void onEvent(Boolean aBoolean) {
                    NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(this, false);
                    if (msgLoaded) {
                        return;
                    }
                    // 查询最近联系人列表数据
                    NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(
                            new RequestCallbackWrapper<List<RecentContact>>() {

                                @Override
                                public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                                    if (code != ResponseCode.RES_SUCCESS || recents == null) {
                                        return;
                                    }
                                    loadedRecents = new ArrayList<>();
                                    AtomicInteger atomicInteger = new AtomicInteger();
                                    int size = recents.size();
                                    p.setContactData(loadedRecents, recents, atomicInteger, size);

                                }
                            });
                }
            }, true);
        }, delay ? 250 : 0);
    }


    /**
     * 进入时加载数据成功
     */
    @Override
    public void getContactInfoSuccess() {
        if (mType == 0)
            RxBus.getDefault().post(new MessageListEvent(loadedRecents));
        Utils.runOnUiThread(() -> {
            // 初次加载，更新离线的消息中是否有@我的消息
            for (RecentContact loadedRecent : items) {
                if (loadedRecent.getSessionType() == SessionTypeEnum.Team) {
                    p.updateOfflineContactAited(loadedRecent);
                }
            }
            // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
            msgLoaded = true;
            if (isAdded()) {
                onRecentContactsLoaded();
            }
        });
    }


    private void onRecentContactsLoaded() {
        items.clear();
        if (loadedRecents != null) {
            items.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true, true);
        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private int unreadNum, unReadNumFamily;

    private void refreshMessages(boolean unreadChanged, boolean isRefresh) {
        if (unreadChanged) {
            // 方式一：累加每个最近联系人的未读（快）
            if (mType == 0) {
                unreadNum = 0;
            }

            boolean isMute = false;
            if (!TextUtils.isEmpty(teamId)) {
                Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
                if (team != null && team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.All) {
                    isMute = true;
                }
            }

            for (ContactInfo r : items) {
                if (!TextUtils.equals(r.getContactId(), Constants.ACTIVITY_NOTICE) &&
                        (r.getSessionType() == SessionTypeEnum.P2P && mType == 0)) {
                    unreadNum += r.getUnreadCount();
                }
                if (r.getSessionType() == SessionTypeEnum.Team && isMute) {
                    unReadNumFamily = r.getUnreadCount();
                    Utils.logE("家族未读消息: " + r.getUnreadCount());
                }
                if (r.getSessionType() == SessionTypeEnum.Team && mType == 0) {
                    Utils.runOnUiThread(() -> setTeamInfo(r));
                }
            }
            if (!isMute && mTvTeamBadge != null) {//items.get(0)不是team
                mTvTeamBadge.setVisibility(View.GONE);
            }
            if (mType == 0) {
                if (isMute) {
                    Badger.updateBadgerCount(unreadNum + unReadNumFamily);
                    RxBus.getDefault().post(new UnReadMessageEvent(unreadNum + unReadNumFamily, unreadNum));
                } else {
                    Badger.updateBadgerCount(unreadNum);
                    RxBus.getDefault().post(new UnReadMessageEvent(unreadNum, unreadNum));
                }
            }

        }
        sortRecentContacts(items, isRefresh);
        Utils.runOnUiThread(this::notifyDataSetChanged);

    }

    private long mNotifyTime;

    /**
     * **************************** 排序 ***********************************
     */
    private void sortRecentContacts(List<ContactInfo> list, boolean unreadChanged) {
        int size = list.size();
        if (size == 0) {
            return;
        }
        if (mType == 1) {
            for (int i = size - 1; i >= 0; i--) {
                if ((list.get(i).getSessionType() == SessionTypeEnum.Team) || list.get(i).intimacy_val < mIntimacyNum || TextUtils.equals(Constants.ACTIVITY_NOTICE, list.get(i).getContactId())) {
                    items.remove(i);
                }
            }
        } else {
            //去除群组消息
            for (int i = size - 1; i >= 0; i--) {
                if ((list.get(i).getSessionType() == SessionTypeEnum.Team) || TextUtils.equals(Constants.ACTIVITY_NOTICE, list.get(i).getContactId())) {
                    items.remove(i);
                }
            }
        }
        Collections.sort(list, comp);

        if (unreadChanged && mType == 0) {
            if (messageInfoList != null)
                messageInfoList.clear();
            for (ContactInfo r : items) {
                if ((!TextUtils.equals(Constants.CUSTOMER_SERVICE, r.getContactId())
                        && r.getSessionType() == SessionTypeEnum.P2P)) {   //群组消息不计算
                    if (r.getUnreadCount() > 0) {
                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.contactId = r.getContactId();
                        messageInfo.content = r.getContent();
                        messageInfo.fromAccount = r.getFromAccount();
                        messageInfo.unreadCount = r.getUnreadCount();
                        messageInfo.time = r.getTime();
                        messageInfo.fromNick = r.getFromNick();
                        messageInfoList.add(messageInfo);
                    }
                }
            }
            long now = System.currentTimeMillis();
            if ((now - mNotifyTime) > 500) {
                mNotifyTime = System.currentTimeMillis();
                Utils.runOnUiThread(() -> RxBus.getDefault().post(new MessageEvent(messageInfoList)));
            }
        }
    }

    private static final Comparator<RecentContact> comp = (recent1, recent2) -> {
        // 先比较置顶tag
        boolean isStickTop1 = StickTopCache.isStickTop(recent1);
        boolean isStickTop2 = StickTopCache.isStickTop(recent2);
        if (isStickTop1 ^ isStickTop2) {
            return isStickTop1 ? -1 : 1;
        } else {
            long time = 0;
            if (recent1 != null && recent2 != null) {
                time = recent1.getTime() - recent2.getTime();
            }
            return time == 0 ? 0 : (time > 0 ? -1 : 1);
        }
    };

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(messageReceiverObserver, register);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
        registerTeamUpdateObserver(register);
        registerTeamMemberUpdateObserver(register);
        registerStickTopObserver(register);
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
    }

    /**
     * 注册群信息&群成员更新监听
     */
    private void registerTeamUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
    }

    private void registerTeamMemberUpdateObserver(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver,
                register);
    }

    private void registerStickTopObserver(boolean register) {
        MsgServiceObserve msgObserver = NIMClient.getService(MsgServiceObserve.class);
        msgObserver.observeAddStickTopSession(addStickTopSessionObserver, register);
        msgObserver.observeRemoveStickTopSession(removeStickTopSessionObserver, register);
        msgObserver.observeSyncStickTopSession(syncStickTopSessionObserver, register);
    }


    // 暂存消息，当RecentContact 监听回来时使用，结束后清掉
    private final Map<String, Set<IMMessage>> cacheMessages = new HashMap<>();

    //监听在线消息中是否有@我
    private final Observer<List<IMMessage>> messageReceiverObserver = (Observer<List<IMMessage>>) imMessages -> {
        if (imMessages != null) {
            for (IMMessage imMessage : imMessages) {
                if (!TeamMemberAitHelper.isAitMessage(imMessage)) {
                    continue;
                }
                Set<IMMessage> cacheMessageSet = cacheMessages.get(imMessage.getSessionId());
                if (cacheMessageSet == null) {
                    cacheMessageSet = new HashSet<>();
                    cacheMessages.put(imMessage.getSessionId(), cacheMessageSet);
                }
                cacheMessageSet.add(imMessage);
            }
        }
    };
    Observer<List<RecentContact>> messageObserver = (Observer<List<RecentContact>>) this::onRecentContactChanged;

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
        boolean isRefresh = false;
        final int[] index = new int[1];
        int size = items.size();
        for (RecentContact r : recentContacts) {
            index[0] = -1;
            if (size > 0)
                for (int i = 0; i < size; i++) {
                    if (r.getContactId().equals(items.get(i).getContactId()) && r.getSessionType() == (items.get(i)
                            .getSessionType())) {
                        index[0] = i;
                        break;
                    }
                }
            if (index[0] >= 0) {
                items.remove(index[0]);
            }
            if (r.getSessionType() == SessionTypeEnum.Team && cacheMessages.get(r.getContactId()) != null) {
                TeamMemberAitHelper.setRecentContactAited(r, cacheMessages.get(r.getContactId()));
            }
            RecentContact recentContactInDb = NIMClient.getService(MsgService.class).queryRecentContact(r.getContactId(), r.getSessionType());
            if (recentContactInDb != null) {
                ContactInfo contactInfo = p.setContactData(r, true);
                items.add(contactInfo);
            }
            if (r.getSessionType() == SessionTypeEnum.P2P && !TextUtils.equals(Constants.CUSTOMER_SERVICE, r.getContactId())) {
                isRefresh = true;
            }
        }
        cacheMessages.clear();
        refreshMessages(true, isRefresh);
    }


    /**
     * 设置家族信息
     */
    private void setTeamInfo(RecentContact r) {
        String saveTeamId = MMKVUtils.getInstance().decodeString(Constants.FAMILY_TID);
        if (mType == 0 && (r.getSessionType() == SessionTypeEnum.Team && TextUtils.equals(teamId, r.getContactId()))
                || (r.getSessionType() == SessionTypeEnum.Team && TextUtils.equals(r.getContactId(), saveTeamId))) {
            mTeamContact = r;
            boolean isGift = false;
            teamId = r.getContactId();
            mTvTeamName.setText(UserInfoHelper.getUserTitleName(r.getContactId(), r.getSessionType()));
            String content = r.getContent();
            if (TextUtils.equals("[自定义消息]", content) || TextUtils.isEmpty(content)) {
                IMMessage message = NIMClient.getService(MsgService.class).queryLastMessage(r.getContactId(), SessionTypeEnum.Team);
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                if (chatMsg != null) {
                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
                        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
                        content = "\"" + ((TextUtils.isEmpty(r.getFromNick()) || TextUtils.equals("null", r.getFromNick())) ? "" : r.getFromNick()) + "\"" + " 送出 [" + gift.gift_info.name + "]";
                        isGift = true;
                    }
                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.RED_PACKET_TYPE_IM)) {
                        if (chatMsg.cmd.equals(ChatMsg.RED_PACKET_ROOM) || chatMsg.cmd.equals(ChatMsg.RED_PACKET_FAMILY)) {
                            ChatMsg.GoldRedEnvelope gift = JSON.parseObject(chatMsg.content, ChatMsg.GoldRedEnvelope.class);
                            content = "[" + AppUtils.getAppName() + "红包]" + gift.des;
                            isGift = true;
                        }
                        if (chatMsg.cmd.equals(ChatMsg.RED_PACKET_IM_OPEN_ROOM) || chatMsg.cmd.equals(ChatMsg.RED_PACKET_IM_OPEN_FAMILY)) {
                            ChatMsg.GoldRedReceive goldRedReceive = JSON.parseObject(chatMsg.content, ChatMsg.GoldRedReceive.class);
                            content = goldRedReceive.receive_user + "抢到红包啦";
                            isGift = true;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_TIPS)) {  //提示
                        ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
                        if (tips != null) {
                            SpannableStringBuilder span = ChatMsgUtil.getTipContent(tips.des, "", null);
                            content = span.toString();
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_USER_NOBILITY_LEVEL_UP_NOTICE)) {  //贵族升级
                        ChatMsg.NobilityLevelUp levelUp = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
                        if (levelUp != null) {
                            content = levelUp.msg;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_SAY_HELLO)) {  //进入家族
                        ChatMsg.JoinFamily joinFamily = JSON.parseObject(chatMsg.content, ChatMsg.JoinFamily.class);
                        if (joinFamily != null) {
                            content = joinFamily.name + "加入了家族";
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_COUPLE_TIPS)) {  //家族情侣组成
                        ChatMsg.CoupleLetter joinFamily = JSON.parseObject(chatMsg.content, ChatMsg.CoupleLetter.class);
                        if (joinFamily != null) {
                            content = joinFamily.from_nickname + "与" + joinFamily.to_nickname + "组成情侣，" + joinFamily.desc;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_NOTICE)) {  //进入家族
                        ChatMsg.ApplyMessage applyMessage = JSON.parseObject(chatMsg.content, ChatMsg.ApplyMessage.class);
                        if (applyMessage != null) {
                            String fromId = r.getFromAccount();
                            String tid = r.getContactId();
                            String teamNick = TeamHelper.getTeamMemberDisplayName(tid, fromId);
                            if (applyMessage.count == 1) {
                                content = "家族公告";
                            } else {
                                content = teamNick + "发布了家族公告";
                            }
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_GIFT_BAG_IM)) {  //空投消息
                        ChatMsg.Airdrop airdrop = JSON.parseObject(chatMsg.content, ChatMsg.Airdrop.class);
                        if (airdrop != null) {
                            String fromId = r.getFromAccount();
                            String tid = r.getContactId();
                            String teamNick = TeamHelper.getTeamMemberDisplayName(tid, fromId);
                            content = teamNick + airdrop.title;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_BAG_RECEIVE_TIPS)) {
                        ChatMsg.AirdropOpen airdropOpen = JSON.parseObject(chatMsg.content, ChatMsg.AirdropOpen.class);
                        if (airdropOpen != null) {
                            content = airdropOpen.to_user_name + "抢到空投啦";
                            isGift = true;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_DICE_GAME)) {
                        ChatMsg.Game game = JSON.parseObject(chatMsg.content, ChatMsg.Game.class);
                        if (game != null) {
                            if (game.type == 1) {
                                content = "[骰子]";
                            } else if (game.type == 2) {
                                content = "[猜拳]";
                            }
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.VOICE_ROOM_SEAT)) {
                        ChatMsg.RoomInfo tips = JSON.parseObject(chatMsg.content, ChatMsg.RoomInfo.class);
                        if (tips != null) {
                            content = tips.name + tips.content;
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_RED_PACKET_RAIN_COUNT_DOWN_MSG)) {
                        ChatMsg.NobilityLevelUp redPackageTip = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
                        if (redPackageTip != null) {
                            content = redPackageTip.msg;
                        }
                    }
                }
            }
            String fromId = r.getFromAccount();
            if (!TextUtils.isEmpty(fromId)
                    && !fromId.equals(NimUIKit.getAccount())
                    && !(r.getAttachment() instanceof NotificationAttachment)) {
                String tid = r.getContactId();
                String teamNick = TeamHelper.getTeamMemberDisplayName(tid, fromId);
                if (!isGift)
                    content = teamNick + ": " + content;
                if (TeamMemberAitHelper.hasAitExtension(r)) {
                    if (r.getUnreadCount() == 0) {
                        TeamMemberAitHelper.clearRecentContactAited(r);
                    } else {
                        content = TeamMemberAitHelper.getAitAlertString(content);
                    }
                }
            }
            if (!TextUtils.equals("[通知消息]", content)) {  //通知消息不显示
                MoonUtil.identifyRecentVHFaceExpressionAndTags(getActivity(), mTvTeamContent, content, -1, 0.45f);
            }
            mRlTeamAvatar.setBackgroundResource(R.mipmap.ic_family_custon);
            mIvMine.setVisibility(View.VISIBLE);
            mTvTeamBadge.setVisibility(r.getUnreadCount() == 0 ? View.GONE : View.VISIBLE);
            if (r.getUnreadCount() > 99) {
                mTvTeamBadge.setText("99+");
            } else {
                mTvTeamBadge.setText(r.getUnreadCount() + "");
            }
            Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
            if (team != null && team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.All) {
                mTvTeamBadge.setVisibility(View.VISIBLE);
                mTvTeamBadge.setVisibility(r.getUnreadCount() == 0 ? View.GONE : View.VISIBLE);
                mIvMute.setVisibility(View.GONE);
            } else {
                mIvMute.setVisibility(View.VISIBLE);
                mTvTeamBadge.setVisibility(View.GONE);
            }

            mTvTeamTime.setText(ChatTimeUtils.getChatTime(r.getTime()));
            mTvTeamTime.setVisibility(View.VISIBLE);
        }
    }


    final Observer<IMMessage> statusObserver = (Observer<IMMessage>) message -> {
        if (message == null) {
            return;
        }
        String sessionId = message.getSessionId();
        SessionTypeEnum sessionType = message.getSessionType();
        int index = getItemIndex(sessionId, sessionType);
        if (index >= 0 && index < items.size()) {
            RecentContact recentContact = NIMClient.getService(MsgService.class).queryRecentContact(sessionId, sessionType);
            ContactInfo contactInfo = p.setContactData(recentContact, true);
            items.set(index, contactInfo);
            refreshViewHolderByIndex(index);
        }
    };

    Observer<RecentContact> deleteObserver = (Observer<RecentContact>) recentContact -> {
        if (recentContact != null) {
            for (RecentContact item : items) {
                if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) &&
                        item.getSessionType() == recentContact.getSessionType()) {
                    items.remove(item);
                    refreshMessages(true, true);
                    break;
                }
            }
        } else {
            items.clear();
            refreshMessages(true, true);
        }
    };

    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {

        @Override
        public void onUpdateTeams(List<Team> teams) {
//            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeam(Team team) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_RECOMMEND));
        }
    };

    TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
//            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> member) {
        }
    };

    private final Observer<List<StickTopSessionInfo>> syncStickTopSessionObserver = infoList -> {
        StickTopCache.recordStickTop(infoList, true);
        refreshMessages(false, false);
    };
    private final Observer<StickTopSessionInfo> addStickTopSessionObserver = info -> {
        StickTopCache.recordStickTop(info, true);
        refreshMessages(false, false);
    };
    private final Observer<StickTopSessionInfo> removeStickTopSessionObserver = info -> {
        StickTopCache.recordStickTop(info, false);
        refreshMessages(false, false);
    };

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            RecentContact item = items.get(i);
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    private int getItemIndex(String sessionId, SessionTypeEnum sessionType) {
        for (int i = 0; i < items.size(); i++) {
            RecentContact item = items.get(i);
            if (TextUtils.equals(item.getContactId(), sessionId) && item.getSessionType() == sessionType) {
                return i;
            }
        }
        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> mAdapter.notifyItemChanged(index));
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }

    private void registerUserInfoObserver() {
        if (userInfoObserver == null) {
            userInfoObserver = accounts -> refreshMessages(false, false);
        }
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, true);
    }

    private void unregisterUserInfoObserver() {
        if (userInfoObserver != null) {
            NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, false);
        }
    }

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {

        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            refreshMessages(false, false);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            refreshMessages(false, false);
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            refreshMessages(false, false);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            p.addContact(account);
            refreshMessages(false, false);
        }
    };


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        if (id == R.id.cl_pull_wires) {
            startActivity(PullWiresRecordActivity.class);
        } else if (id == R.id.tv_apply) {   // 申请审核和招募红包
            if (TextUtils.isEmpty(teamId)) {
            } else {
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FAMILY_APPLY);
            }
        }
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {
        setNoMessage();
        notifyDataSetChanged();
    }

    @Override
    public void checkUserInfoSuccess(String userId, UserInfo userInfo) {
       /* if (userInfo.isDisable()) {
            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.user_disable != null) {
                BaseWebViewActivity.start(mContext, null, service.getConfigInfo().share_config.user_disable.link + "&user_id=" + userId);
            }
            return;
        }
        if (userInfo.isLogout()) {
            new CustomPopWindow(BaseApplication.getInstance())
                    .setContent("该用户已注销")
                    .setRightButton("我知道了")
                    .setRightGone()
                    .showPopupWindow();
            return;
        }*/


    }

    private void setNoMessage() {
        if (service.getUserInfo() == null) return;
        if (service.getUserInfo().getSex() == 1) {
            emptyBg.setText(R.string.chat_boy_no_message);
        } else {
            if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.girl_is_show_sys_accost_slide == 1) {
                mClPullWires.setVisibility(View.VISIBLE);
                //派对女用户
                if (service.getUserInfo().isPartyGirl() && service.getConfigInfo().sys.girl_is_show_sys_accost_slide_party == 0) {
                    mClPullWires.setVisibility(View.GONE);
                }
                //牵线内容
                String pullWriesContent = MMKVUtils.getInstance().decodeString(Constants.PULL_WRIES_CONTENT + service.getUserId());
                if (!TextUtils.isEmpty(pullWriesContent)) {
                    mTvPullWiresTime.setText(ChatTimeUtils.getChatTime(MMKVUtils.getInstance().decodeLong(Constants.PULL_WRIES_TIME + service.getUserId())));
                    setPullWiresData(pullWriesContent);
                } else {
                    setPullWiresData("暂无正在牵线的人～");
                }
            }
            emptyBg.setText(R.string.chat_girl_no_message);
        }
    }


    /**
     * 设置牵线消息
     */
    private void setPullWiresData(String content) {
        mPullWiresContent.clear();
        mPullWiresContent.add(content);
        mTvPullWiresContent.init(mPullWiresContent, (textView, index) -> {
            textView.setTextSize(12);
            textView.setTextColor(Color.parseColor("#999999"));
            SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
            textView.setText(builder);
            mTvPullWiresContent.post(() -> {
                TextPaint textPaint = textView.getPaint();
                float textPaintWidth = textPaint.measureText(textView.getText().toString());
                mTvPullWiresContent.setBackgroundResource(R.drawable.bg_pull_wires_gray);
                ViewGroup.LayoutParams params = mTvPullWiresContent.getLayoutParams();
                params.width = (int) (textPaintWidth + DensityUtils.dp2px(BaseApplication.getInstance(), 20));
                params.height = DensityUtils.dp2px(BaseApplication.getInstance(), 20);
                mTvPullWiresContent.setLayoutParams(params);
            });
        }, 0);
    }

    int mCurrentIndex = 0;

    /**
     * 设置没有家族的文案
     */
    private void setNoTeam() {
        mTvNoTeam.setVisibility(View.VISIBLE);
        mTvTeamContent.setVisibility(View.GONE);
        if (!isNoTeam) {
            isNoTeam = true;
            List<String> lists = new ArrayList<>();
            lists.add("想脱单，进家族");
            lists.add("找兴趣相投的朋友一起玩");
            lists.add("同城交友集合地");
            lists.add("进家族抢红包");
            Utils.runOnUiThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    //递增
                    mCurrentIndex++;
                    if (mCurrentIndex >= lists.size()) {
                        mCurrentIndex = 0;
                    }
                    //设置i文字
                    mTvNoTeam.setText(lists.get(mCurrentIndex));
                    Utils.runOnUiThreadDelayed(this, 2000);
                }
            }, 2000);
        }
    }


    /**
     * 获取聊天数据
     */
    @Override
    public void getChatUserInfo(List<ContactInfo> contact) {
        sortRecentContacts(contact, false);
        items = contact;
        notifyDataSetChanged();
    }


    @Override
    public void notifyDataSetChanged() {
        boolean empty = items.isEmpty();
        int size = items.size();
        if (!isTopTag && isResumed() && mType == 0 && unreadNum == 0) { //如果是消息页&& 未读消息为0
            for (int i = 0; i < size; i++) {
                TextView tv_badge = (TextView) mAdapter.getViewByPosition(i, R.id.tv_badge);
                if (tv_badge != null) {
                    tv_badge.setVisibility(View.GONE);
                } else {
                    mAdapter.setData(i, items.get(i));
                }
            }
            isTopTag = false;
        } else {
            mAdapter.setList(items);
            if (mType == 1 && isResumed()) {
                Utils.logE("刷新消息------------------");
                getOnLineStatus(mAdapter.getData(), mLayoutManager.findFirstVisibleItemPosition(), mLayoutManager.findLastVisibleItemPosition());
            }
        }
        emptyBg.setVisibility(empty ? View.VISIBLE : View.GONE);
//        if (mType == 0) {
//            view2.setVisibility(empty ? View.GONE : View.VISIBLE);
//        }
    }

    @Override
    public void getChatHistorySuccess(ChatHistoryDto data) {
        if (mType == 1) return;
        if (data.banner_list != null && data.banner_list.size() > 0 && !isBannerInit) {
            isBannerInit = true;
            mTempCurrentTime = System.currentTimeMillis();
            mBanner.setVisibility(View.VISIBLE);
            mBanner.setIndicator(new RectangleIndicator(mContext))
                    .setIndicatorMargins(new IndicatorConfig.Margins(0, 0, 0, ConvertUtils.dp2px(10)));
            mBanner.setAdapter(new BannerImageAdapter<ChatHistoryDto.Banner>(data.banner_list) {
                @Override
                public void onBindView(BannerImageHolder holder, ChatHistoryDto.Banner data, int position, int size) {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    GlideUtils.loadRouteImage(getActivity(), holder.imageView, data.img);
                    holder.imageView.setOnClickListener(v -> {

                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("banner位点击", "banner_h5_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 2, data.link, position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null
                                );
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("新年活动banner点击", "new_year_banner_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), data.link, 23,
                                        position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, "新年活动banner点击", "-1", -1)), null
                                );

                        CommonUtil.performLink(getActivity(), new ConfigInfo.MineInfo(data.link, data.option), position, 2);
                    });
                }
            });
            mBanner.addBannerLifecycleObserver(this);
            mBanner.setLoopTime(5000);  //设置轮播间隔时间
            mBanner.setScrollTime(600); //设置轮播滑动过程的时间
            mBanner.start();
        }
        //聊天历史消息
        ChatHistoryDto.FamilyInfo mFamilyInfo = data.my_family_info;
        if (mFamilyInfo != null) {  //有家族信息
            mTvPlayNum.setVisibility(View.GONE);  //在玩人数
            mCiAvatar.setVisibility(View.VISIBLE);
            mIvMine.setVisibility(View.VISIBLE);
            mTvNoTeam.setVisibility(View.GONE);
            mTvTeamContent.setVisibility(View.VISIBLE);
            teamId = mFamilyInfo.tid;
            mTvTeamName.setText(mFamilyInfo.family_name);
            Team team = NimUIKit.getTeamProvider().getTeamById(teamId);
            GlideUtils.loadRoundImage(getActivity(), mCiAvatar, mFamilyInfo.icon, 8);
            mRlTeamAvatar.setBackgroundResource(R.mipmap.ic_family_custon);
            //等级
            ChatHistoryDto.FamilyLevel level = mFamilyInfo.family_level;
            if (null != level) {
                mTvLevel.setText(level.level + ""); //等级
                GlideUtils.loadRouteImage(getActivity(), mIvLevel, level.icon);
                mTvLevel.setVisibility(View.VISIBLE);
                mIvLevel.setVisibility(View.VISIBLE);
            }
            //更新家族ID
            if (service != null && service.getUserInfo() != null) {
                UserInfo userInfo = service.getUserInfo();
                userInfo.family_id = mFamilyInfo.family_id;
                service.setUserInfo(userInfo);
                MMKVUtils.getInstance().encode(Constants.FAMILY_TID, teamId);
            }
        } else {  //没有家族
            mTvTeamName.setText("家族群聊");
            setNoTeam();
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.FAMILY_APPLY);
            MMKVUtils.getInstance().removeKey(Constants.FAMILY_TID);
            MMKVUtils.getInstance().removeKey(Constants.TEAM_IS_FIRST);
            teamId = "";
            //招募红包
            RecruitBaseDto dto = data.recruit;
            if (dto == null || dto.value == 0) {
                mTvApply.setVisibility(View.GONE);
            } else {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.chat_icon_red);
                mTvApply.setCompoundDrawablesWithIntrinsicBounds(drawable,
                        null, null, null);
                mTvApply.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 3));
                mTvApply.setVisibility(View.VISIBLE);
                mTvApply.setText(dto.value + "个招募红包");
            }
            //是否有在玩人数
            if (TextUtils.isEmpty(data.family_user_num)) {
                mTvPlayNum.setVisibility(View.GONE);
            } else {
                mTvPlayNum.setText(data.family_user_num);
                mTvPlayNum.setVisibility(View.VISIBLE);
            }
            view2.setVisibility(View.GONE);
            mCiAvatar.setVisibility(View.INVISIBLE);
            mTvLevel.setVisibility(View.GONE);
            mIvLevel.setVisibility(View.GONE);
            mIvMine.setVisibility(View.GONE);
            mTvTeamBadge.setVisibility(View.GONE);
            mIvMute.setVisibility(View.GONE);
            mTvTeamTime.setVisibility(View.INVISIBLE);
            mRlTeamAvatar.setBackgroundResource(R.mipmap.ic_family_all);


            MoonUtil.identifyRecentVHFaceExpressionAndTags(getActivity(), mTvTeamContent, "想脱单进家族", -1, 0.45f);
            if (mTeamContact != null) {  //删除群聊消息
                NIMClient.getService(MsgService.class).deleteRecentContact(mTeamContact);
                NIMClient.getService(MsgService.class).clearChattingHistory(mTeamContact.getContactId(), SessionTypeEnum.Team);
            }
            if (service != null && service.getUserInfo() != null) {
                UserInfo userInfo = service.getUserInfo();
                userInfo.family_id = 0;
                service.setUserInfo(userInfo);
            }
        }
        if (data.chat_room_info != null) {
            mClRoom.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            mClRoom.setData(data.chat_room_info);
        }
    }

    @Override
    public void onSuccessUserList(OnLineListBean onLineListBean) {

    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {

    }

    @Override
    public void accostUserSuccess(int position, AccostDto data) {

    }
}
