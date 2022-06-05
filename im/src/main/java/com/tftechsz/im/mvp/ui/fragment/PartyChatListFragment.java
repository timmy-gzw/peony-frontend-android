package com.tftechsz.im.mvp.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.main.OnlineStateChangeObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.business.recent.RecentContactsCallback;
import com.netease.nim.uikit.business.recent.TeamMemberAitHelper;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.UserInfo;
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
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.MessageAdapter;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.mvp.iview.IChatView;
import com.tftechsz.im.mvp.presenter.ChatPresenter;
import com.tftechsz.im.mvp.ui.activity.ActivityNoticeActivity;
import com.tftechsz.im.uikit.PartyChatDetailsActivity;
import com.tftechsz.im.widget.MLinearLayoutManager;
import com.tftechsz.im.widget.pop.MessageOperationPopWindow;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netease.nimlib.sdk.msg.model.QueryDirectionEnum.QUERY_OLD;

public class PartyChatListFragment extends BaseMvpFragment<IChatView, ChatPresenter> implements View.OnClickListener, IChatView {

    private RecyclerView mRvMessage;
    private MessageAdapter mAdapter;
    private List<ContactInfo> items = new ArrayList<>();

    private RecentContactsCallback callback;
    private UserInfoObserver userInfoObserver;
    private TextView emptyBg;
    private int unreadNum;
    @Autowired
    UserProviderService service;
    private boolean isTopTag;
    private List<ContactInfo> loadedRecents;
    //刷新加载一次
    private boolean mIsFlagLoading;
    public int mHeight;

    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter();
    }


    public static PartyChatListFragment newInstance(int type, int height) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("height", height);
        PartyChatListFragment fragment = new PartyChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            int height = getArguments().getInt("height");
            if (height != 0) {
                mHeight = height;
            }
        }
        if (getActivity() != null)
//            getActivity().getWindow().setBackgroundDrawable(null);
            service = ARouter.getInstance().navigation(UserProviderService.class);
        mSmartRefreshLayout = getView(R.id.refresh);
        mSmartRefreshLayout.setEnableRefresh(false);

        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (!mIsFlagLoading) {
                requestMessages(false);
            }

        });
        mRvMessage = getView(R.id.rv_message);
        emptyBg = getView(R.id.emptyBg);
        MLinearLayoutManager layoutManager = new MLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        if (((SimpleItemAnimator) mRvMessage.getItemAnimator()) != null)
            ((SimpleItemAnimator) mRvMessage.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMessage.setFocusable(false);
        mRvMessage.setItemAnimator(null);
        mRvMessage.setLayoutManager(layoutManager);
        registerObservers(true);
        registerOnlineStateChangeListener(true);
        initAdapter();
        if (!mIsFlagLoading) {
            requestMessages(true);
        }

        if (service.getUserInfo() == null) {
            p.getUserInfo();
        } else {
            setNoMessage();
        }
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.chat_ic_no_message);
        emptyBg.setCompoundDrawablesWithIntrinsicBounds(null,
                drawable, null, null);
    }


    private void initAdapter() {
        // adapter
        mAdapter = new MessageAdapter(items, 2);
        mAdapter.onAttachedToRecyclerView(mRvMessage);
        initCallBack();
        mAdapter.setCallback(callback);
        mRvMessage.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (mAdapter.getData().get(position) != null && TextUtils.equals(mAdapter.getData().get(position).getContactId(), Constants.CUSTOMER_SERVICE)) {
                startActivity(ActivityNoticeActivity.class, "chat_user", Constants.CUSTOMER_SERVICE);
                return;
            }
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            if (TextUtils.equals(mAdapter.getData().get(position).getContactId(), Constants.CUSTOMER_SERVICE)) {
                startActivity(ActivityNoticeActivity.class, "chat_user", Constants.CUSTOMER_SERVICE);
            } else {
                ContactInfo contactInfo = mAdapter.getData().get(position);
                PartyChatDetailsActivity.start(getActivity(), contactInfo.contactId, NimUIKit.getCommonP2PSessionCustomization(), null, mHeight);
            }
        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return false;
            ContactInfo recent = mAdapter.getItem(position);
            if (recent != null && !TextUtils.equals(Constants.CUSTOMER_SERVICE, recent.getContactId())) {   //消息 才可以操作
                MessageOperationPopWindow pop = new MessageOperationPopWindow(getActivity(), recent, true);
                pop.addOnClickListener(type -> {
                    if (type == 1) {   //删除

                        mAdapter.remove(recent);
                        Utils.runOnUiThread(() -> refreshMessages(true, true));

                    }
                });
                pop.showPopupWindow();
            }
            return false;
        });


    }

    /**
     * 刷新消息
     */
    private void refreshMessages() {
        Utils.runOnUiThread(() -> refreshMessages(true, true));
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_chat_list_party;
    }

    @Override
    protected void initData() {
        initRxBus();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFragmentVisible = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);

    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_DELETE_MESSAGE) {   //删除聊天
                                if (mAdapter != null) {
                                    Utils.runOnUiThread(() -> {
                                        mAdapter.setCheckShow(true);
//                                        p.setTranX(mRvMessage);
                                    });
                                }
                            } else if (event.type == Constants.NOTIFY_REMOVE_USER) {   //移除用户
                                Utils.runOnUiThread(() -> {
                                    for (int i = mAdapter.getData().size() - 1; i >= 0; i--) {
                                        if (TextUtils.equals(mAdapter.getData().get(i).getContactId(), String.valueOf(event.familyId))) {
                                            mAdapter.remove(mAdapter.getData().get(i));
                                        }
                                    }
                                });
                                refreshMessages();
                            }
                        }
                ));


    }


    @Override
    public void onDestroy() {
        registerObservers(false);
        registerOnlineStateChangeListener(false);
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


    private void requestMessages(boolean isFirst) {
        if (!isFirst && mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() < 10) {
            mSmartRefreshLayout.finishLoadMore();
            return;
        }
        mIsFlagLoading = true;
        NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
            @Override
            public void onEvent(Boolean aBoolean) {
                NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(this, false);

                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts(isFirst ? null : mAdapter.getData().get(mAdapter.getData().size() - 1), QUERY_OLD, 10).setCallback(
                        new RequestCallbackWrapper<List<RecentContact>>() {

                            @Override
                            public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                                mSmartRefreshLayout.finishLoadMore();
                                if (code != ResponseCode.RES_SUCCESS || recents == null) {
                                    return;
                                }
                                loadedRecents = new ArrayList<>();
                                AtomicInteger atomicInteger = new AtomicInteger();
                                int size = recents.size();
                                p.setContactData(loadedRecents, recents, atomicInteger, size);
                                mIsFlagLoading = false;
                            }

                            @Override
                            public void onFailed(int i) {
                                super.onFailed(i);
                                mSmartRefreshLayout.finishLoadMore();
                                mIsFlagLoading = false;
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                super.onException(throwable);
                                mSmartRefreshLayout.finishLoadMore();
                                mIsFlagLoading = false;
                            }
                        });
            }
        }, true);

    }


    private boolean isFragmentVisible;


    /**
     * 进入时加载数据成功
     */
    @Override
    public void getContactInfoSuccess() {

        Utils.runOnUiThread(() -> {
            // 初次加载，更新离线的消息中是否有@我的消息
            for (RecentContact loadedRecent : items) {
                if (loadedRecent.getSessionType() == SessionTypeEnum.Team) {
                    p.updateOfflineContactAited(loadedRecent);
                }
            }
            // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
            if (isAdded()) {
                onRecentContactsLoaded();
            }
        });
    }


    private void onRecentContactsLoaded() {
//        items.clear();
        if (loadedRecents != null) {
            items.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true, true);
        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }


    private void refreshMessages(boolean unreadChanged, boolean isRefresh) {
        // 方式一：累加每个最近联系人的未读（快）

        unreadNum = 0;
        for (ContactInfo r : items) {
            if (!TextUtils.equals(r.getContactId(), Constants.ACTIVITY_NOTICE) &&
                    (r.getSessionType() == SessionTypeEnum.P2P)) {
                unreadNum += r.getUnreadCount();
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

        //去除群组消息
        for (int i = size - 1; i >= 0; i--) {
            if ((list.get(i).getSessionType() == SessionTypeEnum.Team) || TextUtils.equals(Constants.ACTIVITY_NOTICE, list.get(i).getContactId())) {
                items.remove(i);
            }
        }

//        Collections.sort(list, comp);
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
        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
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

            RecentContact recentContactInDb = NIMClient.getService(MsgService.class).queryRecentContact(r.getContactId(), r.getSessionType());
            if (recentContactInDb != null) {
                ContactInfo contactInfo = p.setContactData(r, true);
                items.add(0,contactInfo);
            }
            if (r.getSessionType() == SessionTypeEnum.P2P && !TextUtils.equals(Constants.CUSTOMER_SERVICE, r.getContactId())) {
                isRefresh = true;
            }
        }
        cacheMessages.clear();
        refreshMessages(true, isRefresh);
//        Utils.runOnUiThreadDelayed(() -> refreshMessages(true), 800);
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
    public void getUserInfoSuccess(UserInfo userInfo) {
        setNoMessage();
        notifyDataSetChanged();
    }

    @Override
    public void checkUserInfoSuccess(String userId, UserInfo userInfo) {


    }

    private void setNoMessage() {
      /*  if (service.getUserInfo().getSex() == 1) {
            emptyBg.setText(R.string.chat_boy_no_message);
        } else {
            emptyBg.setText(R.string.chat_girl_no_message);
        }*/
        emptyBg.setText(R.string.chat_girl_no_message);
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
        if (!isTopTag && isResumed() && unreadNum == 0) { //如果是消息页&& 未读消息为0
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
        }
        emptyBg.setVisibility(empty ? View.VISIBLE : View.GONE);

    }

    @Override
    public void getChatHistorySuccess(ChatHistoryDto data) {


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

    @Override
    public void onClick(View v) {

    }
}
