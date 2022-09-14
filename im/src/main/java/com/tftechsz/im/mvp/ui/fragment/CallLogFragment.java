package com.tftechsz.im.mvp.ui.fragment;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netease.nim.uikit.api.NimUIKit;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.CallLogAdapter;
import com.tftechsz.im.databinding.FragmentCallLogBinding;
import com.tftechsz.im.model.dto.CallLogDto;
import com.tftechsz.im.mvp.iview.ICallLogView;
import com.tftechsz.im.mvp.presenter.CallLogPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.im.mvp.ui.fragment
 * 描 述 : 通话记录
 */
public class CallLogFragment extends BaseMvpFragment<ICallLogView, CallLogPresenter> implements ICallLogView {

    private FragmentCallLogBinding mBind;
    private int page = 1;
    private CallLogAdapter mAdapter;
    private final ArrayList<CallLogDto> mList = new ArrayList<>();
    private boolean mFlagVisibleFragment;

    @Override
    protected CallLogPresenter initPresenter() {
        return new CallLogPresenter();
    }

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    public int getBindLayout() {
        return R.layout.fragment_call_log;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mBind = (FragmentCallLogBinding) getBind();
        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CallLogAdapter();
        mAdapter.onAttachedToRecyclerView(mBind.recy);
        mBind.recy.setAdapter(mAdapter);

        mBind.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                page++;
                p.getCallList(page);
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                page = 1;
                p.getCallList(page);
            }
        });
        mAdapter.addChildClickViewIds(R.id.root, R.id.icon, R.id.end_call);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            CallLogDto data = mAdapter.getData().get(position);
            if (data == null) return;
            int id = view.getId();
            if (id == R.id.root) {
                boolean from = data.is_from;  // // true-主叫方 false-被叫方
                int ids = from ? data.to_user_id : data.from_user_id;
                ARouterUtils.toChatP2PActivity(String.valueOf(ids), NimUIKit.getCommonP2PSessionCustomization(), null);
            } else if (id == R.id.icon) { //点击头像
                boolean from = data.is_from;  // // true-主叫方 false-被叫方
                int ids = from ? data.to_user_id : data.from_user_id;
                ARouterUtils.toMineDetailActivity(String.valueOf(ids));
            } else if (id == R.id.end_call) { //拨打电话/视频
                if (!ClickUtil.canOperate()) return;
                call(data);
            }
        });
    }

    private void call(CallLogDto data) {
        boolean from = data.is_from;  // // true-主叫方 false-被叫方
        int id = from ? data.to_user_id : data.from_user_id;
        initPermissions(String.valueOf(id), data.isVideo() ? 2 : 1);
    }


    private void initPermissions(String userId, int type) {
        if (getActivity() != null) {
            String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
            PermissionUtil.beforeRequestPermission(getActivity(), permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions(getActivity())
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    p.checkCallMsg(userId, type);
                                } else {
                                    PermissionUtil.showPermissionPop(mActivity);
                                }
                            }));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFlagVisibleFragment) {
            page = 1;
            p.getCallList(page);
        }
    }


    @Override
    protected void initData() {
        p.getCallList(page);
    }

    @Override
    public void getCallListSuccess(List<CallLogDto> data) {
        mBind.refresh.finishRefresh(true);
        mBind.refresh.finishLoadMore(true);
        if (data != null && data.size() > 0) {
            if (page == 1)
                mList.clear();
            mList.addAll(data);
            mAdapter.setList(mList);
            mBind.refresh.setEnableLoadMore(true);
        } else {
            mBind.refresh.setEnableLoadMore(false);
        }
        mBind.setHasData(mList.size() > 0);
    }

    @Override
    public void getCallListError() {
        mBind.refresh.finishRefresh(false);
        mBind.refresh.finishLoadMore(false);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mFlagVisibleFragment = isVisibleToUser;
        if (isVisibleToUser && isAdded() && p != null) {
            page = 1;
            p.getCallList(page);
        }
    }
}
