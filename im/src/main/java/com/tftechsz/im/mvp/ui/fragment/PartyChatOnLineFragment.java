package com.tftechsz.im.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.DeviceUtils;
import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.OnlineListAdapter;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.mvp.iview.IChatView;
import com.tftechsz.im.mvp.presenter.ChatPresenter;
import com.tftechsz.im.widget.MLinearLayoutManager;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tftechsz.common.Constants.NOTIFY_INFO_DIALOG;

/**
 * 在线列表
 */
public class PartyChatOnLineFragment extends BaseMvpFragment<IChatView, ChatPresenter> implements IChatView, OnlineListAdapter.IOnLine {

    private RecyclerView mRvMessage;
    private OnlineListAdapter mAdapter;

    private TextView emptyBg;

    @Autowired
    UserProviderService service;

    private boolean isFragmentVisible;
    private int page = 1;
    private String roomId;
    public int windowHeight;

    @Override
    protected ChatPresenter initPresenter() {
        return new ChatPresenter();
    }


    public static PartyChatOnLineFragment newInstance(int type, String roomId, int height) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("height", height);
        args.putString("roomId", roomId);
        PartyChatOnLineFragment fragment = new PartyChatOnLineFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {

            int height = getArguments().getInt("height");
            if (height != 0) {
                this.windowHeight = height;
            }
            roomId = getArguments().getString("roomId");
        }

        if (getActivity() != null)
//            getActivity().getWindow().setBackgroundDrawable(null);
            service = ARouter.getInstance().navigation(UserProviderService.class);
        mSmartRefreshLayout = getView(R.id.refresh);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                page = 1;
                p.getPartyList(page, roomId);
            }
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            page++;
            p.getPartyList(page, roomId);
        });
        mRvMessage = getView(R.id.rv_message);
        emptyBg = getView(R.id.emptyBg);
        MLinearLayoutManager layoutManager = new MLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        if (((SimpleItemAnimator) mRvMessage.getItemAnimator()) != null)
            ((SimpleItemAnimator) mRvMessage.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMessage.setFocusable(false);
        mRvMessage.setItemAnimator(null);
        mRvMessage.setLayoutManager(layoutManager);

        initAdapter();

        p.getPartyList(page, roomId);
        setNoMessage();

        initBus();
    }


    private void initAdapter() {
        // adapter
        mAdapter = new OnlineListAdapter(this);
        mAdapter.onAttachedToRecyclerView(mRvMessage);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            RxBus.getDefault().post(new CommonEvent(NOTIFY_INFO_DIALOG, mAdapter.getData().get(position).getUser_id()));
            requireActivity().finish();
        });
        mRvMessage.setAdapter(mAdapter);
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_PIC_ACCOST_SUCCESS) {
                                Utils.runOnUiThread(() -> {
                                    if (mAdapter != null) {
                                        List<UserInfo> data = mAdapter.getData();
                                        for (int i = 0; i < data.size(); i++) {
                                            if (data.get(i).getUser_id() == event.familyId) {
                                                mAdapter.getData().get(i).setIs_accost(1);
                                                mAdapter.notifyItemChanged(i);
                                                break;
                                            }
                                        }
                                    }

                                });
                            }
                        }
                ));
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_online_chat_list_party;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isFragmentVisible = isVisibleToUser;
        super.setUserVisibleHint(isVisibleToUser);

    }


    private void setNoMessage() {
     /*   if (service.getUserInfo().getSex() == 1) {
            emptyBg.setText(R.string.chat_boy_no_message);
        } else {
            emptyBg.setText(R.string.chat_girl_no_message);
        }*/
        emptyBg.setText("暂无数据");
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {

    }

    @Override
    public void checkUserInfoSuccess(String userId, UserInfo userInfo) {

    }

    @Override
    public void getChatUserInfo(List<ContactInfo> contact) {

    }

    @Override
    public void getContactInfoSuccess() {

    }

    @Override
    public void notifyDataSetChanged() {

    }

    @Override
    public void getChatHistorySuccess(ChatHistoryDto data) {

    }

    @Override
    public void onSuccessUserList(OnLineListBean onLineListBean) {
        if (onLineListBean != null) {
            if (page == 1) {
                mAdapter.getData().clear();
            }
            mAdapter.addData(onLineListBean.user_list);
            if (mAdapter.getData().size() > 0) {
                mRvMessage.setVisibility(View.VISIBLE);
                emptyBg.setVisibility(View.GONE);
            } else {
                emptyBg.setVisibility(View.VISIBLE);
                mRvMessage.setVisibility(View.GONE);
            }
        } else {
            emptyBg.setVisibility(View.VISIBLE);
            mRvMessage.setVisibility(View.GONE);
        }
        mSmartRefreshLayout.finishLoadMore();
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        //检测女性用户可用发送消息
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            ARouterUtils.toChatPartyP2PActivity(userId + "", NimUIKit.getCommonP2PSessionCustomization(), null, windowHeight);
        }
    }

    @Override
    public void accostUserSuccess(int position, AccostDto data) {
        //搭讪成功
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS, data.gift.animation));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, mAdapter.getData().get(position).getUser_id()));
//            mAdapter.getData().get(position).setIs_accost(1);
//            mAdapter.startAnimation(mRvUser, position);
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
            CommonUtil.sendAccostGirlBoy(service, mAdapter.getData().get(position).getUser_id(), data, 2);

        }
    }

    @Override
    public void accost(int userId, int position) {
//点击列表搭讪
        p.accostUser(position, String.valueOf(userId), 9);
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("搭讪按钮点击事件", "accost_click", "", JSON.toJSONString(new NavigationLogEntity(CommonUtil.getOSName(), Constants.APP_NAME, service.getUserId(), String.valueOf(userId), 1, 4, System.currentTimeMillis())), null);

    }

    @Override
    public void chat(int userId) {
//私聊
        if (service.getUserInfo().isGirl()) {   //判断女性
            p.getMsgCheck(userId + "");
        } else {
            ARouterUtils.toChatPartyP2PActivity(userId + "", NimUIKit.getCommonP2PSessionCustomization(), null, windowHeight);
            MobclickAgent.onEvent(mContext, "home_accost_message_boy");
        }
    }

    @Override
    public void say(int userId, int position) {
        //女性打招呼
        p.accostUser(position, String.valueOf(userId), 9);
        ARouter.getInstance()
                .navigation(MineService.class)
                .trackEvent("搭讪按钮点击事件", "accost_click", "", JSON.toJSONString(new NavigationLogEntity(DeviceUtils.getSDKVersionName(), Constants.APP_NAME, service.getUserId(), String.valueOf(userId), 3, 4, System.currentTimeMillis())), null);

    }
}
