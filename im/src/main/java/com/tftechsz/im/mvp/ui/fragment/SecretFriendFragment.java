package com.tftechsz.im.mvp.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.netease.nim.uikit.api.NimUIKit;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.SecretFriendAdapter;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.mvp.iview.ISecretFriendView;
import com.tftechsz.im.mvp.presenter.SecretFriendPresenter;
import com.tftechsz.im.mvp.ui.activity.ActivityNoticeActivity;
import com.tftechsz.im.uikit.P2PMessageActivity;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 包 名 : com.tftechsz.im.mvp.ui.fragment
 * 描 述 : 密友
 */
public class SecretFriendFragment extends BaseMvpFragment<ISecretFriendView, SecretFriendPresenter> implements ISecretFriendView {
    private SecretFriendAdapter mAdapter;
    private View mNotDataView;
    private RecyclerView mRvSecretFriend;
    @Autowired
    UserProviderService service;
    private RefreshLayout refresh;

    @Override
    protected SecretFriendPresenter initPresenter() {
        return new SecretFriendPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_chat_secretfriend;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mRvSecretFriend = getView(R.id.rv_message_new);
        mRvSecretFriend.setLayoutManager(new LinearLayoutManager(mContext));
        refresh = rootView.findViewById(R.id.refresh);

        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mRvSecretFriend.getParent(), false);
        mAdapter = new SecretFriendAdapter(null);
        mRvSecretFriend.setAdapter(mAdapter);
        //列表选择item
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
                P2PMessageActivity.start(getActivity(), contactInfo.contactId, NimUIKit.getCommonP2PSessionCustomization(), null, 0);
            }
        });
        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                mPage = 1;
                p.getIntimacyList(mPage);
            }
        });
        refresh.setEnableLoadMore(false);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        p.getIntimacyList(mPage);
    }


    @Override
    public void getIntimacyListSuccess(List<ContactInfo> data) {
        setData(mPage, data);
    }


    private void setData(int pageIndex, List<ContactInfo> data) {
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mNotDataView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            } else {
                mPage -= 1;
            }
        }
    }

}
