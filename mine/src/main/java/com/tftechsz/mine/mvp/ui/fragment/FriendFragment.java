package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseListFragment;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.FriendDto;
import com.tftechsz.mine.mvp.ui.activity.MineFriendActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;

public class FriendFragment extends BaseListFragment<FriendDto> {

    public static final String TYPE = "type";
    public static final String TYPE_WATCH = "watch";   //关注列表
    public static final String TYPE_FANS = "fans";   //粉丝列表
    public static final String TYPE_FRIEND = "friend";   //好友列表
    public String mType;
    private View view;
    private PageStateManager mPageManager;

    public static FriendFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Flowable setNetObservable() {
        mType = getArguments().getString(TYPE);
        return new RetrofitManager().createUserApi(MineApiService.class).getFriend(mPage, mType);
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void setData(List<FriendDto> datas, int page) {
        mPageManager.showContent();
        super.setData(datas, page);
        view.setVisibility(datas.size() > 0 ? View.VISIBLE : View.GONE);
        adapter.setOnItemClickListener((adapter1, view, position) ->
                ARouterUtils.toMineDetailActivity(String.valueOf(adapter.getItem(position).user_id)));
        requestMessages(true,datas);
    }

    private void requestMessages(boolean delay,List<FriendDto> datas) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
                @Override
                public void onEvent(Boolean aBoolean) {
                    NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(this, false);
                    // 查询最近联系人列表数据
                    NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(
                            new RequestCallbackWrapper<List<RecentContact>>() {

                                @Override
                                public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                                    if (code != ResponseCode.RES_SUCCESS || recents == null) {
                                        return;
                                    }
                                    for (RecentContact contact : recents) {
                                        if (contact != null) {
                                            if (contact.getExtension() != null) {
                                                String intimacy = (String) contact.getExtension().get("self_attach");
                                                if (intimacy != null && !TextUtils.isEmpty(intimacy) && intimacy.length() < 10) {
                                                    for (int i = 0; i < datas.size(); i++) {
                                                        if(String.valueOf(datas.get(i).user_id).equals(contact.getContactId())){
                                                            datas.get(i).intimacy_val = Float.parseFloat(intimacy);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    adapter.setList(datas);
                                }
                            });
                }
            }, true);
        }, delay ? 250 : 0);
    }


    @Override
    public int setItemLayoutRes() {
        return R.layout.item_friend;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, FriendDto item, int position) {
        CircleImageView ivAvatar = helper.getView(R.id.iv_avatar);
        CommonUtil.setUserName(helper.getView(R.id.tv_name), item.nickname, false,item.is_vip == 1);
        Glide.with(getActivity()).load(item.icon).into(ivAvatar);
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.iv_sex));
        helper.setGone(R.id.iv_real_people, item.is_real != 1);  //是否真人
        helper.setGone(R.id.tv_vip, item.is_vip != 1);  //是否vip
        if(item.intimacy_val != 0) {
            helper.setText(R.id.intimacy, item.intimacy_val+"");
            helper.setVisible(R.id.intimacy,true);
        }
//        helper.setVisible(R.id.view, helper.getLayoutPosition() != getData().size() - 1);

    }

    @Override
    public String setEmptyContent() {
        if (TextUtils.equals(mType, TYPE_WATCH)) {
            return "你还没有任何关注的人哦，现在去关注吧";
        } else if (TextUtils.equals(mType, TYPE_FANS)) {
            return "你还没有粉丝呢";
        }
        return "相互关注 才是好友";
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mType = getArguments().getString(TYPE);
        LinearLayout root = getView(R.id.root);
        view = getView(R.id.view);

        mPageManager = PageStateManager.initWhenUse(root, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                onRefresh();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        if (!NetworkUtils.isConnected()) {
            root.postDelayed(() -> mPageManager.showError(null), 300);
        }
    }

    @Override
    protected void onRefresh() {
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
            return;
        }
        super.onRefresh();
        if (getActivity() != null) {
            ((MineFriendActivity)getActivity()).onRefresh(mType);
        }
    }

    @Override
    public int setEmptyImg() {
        return R.mipmap.ic_empty_friend;
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initData() {

    }
}
