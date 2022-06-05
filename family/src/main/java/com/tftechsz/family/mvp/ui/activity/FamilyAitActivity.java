package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyAitAdapter;
import com.tftechsz.family.entity.dto.FamilyMemberDto;
import com.tftechsz.family.mvp.IView.IFamilyAitView;
import com.tftechsz.family.mvp.presenter.FamilyAitPresenter;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;

import java.util.List;

@Route(path = ARouterApi.ACTIVITY_FAMILY_AIT)
public class FamilyAitActivity extends BaseMvpActivity<IFamilyAitView, FamilyAitPresenter> implements IFamilyAitView {
    private RecyclerView mRvMember;
    private FamilyAitAdapter mAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private UserProviderService service;

    @Override
    public FamilyAitPresenter initPresenter() {
        return new FamilyAitPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_family_ait;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("@我的消息")
                .build();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mRvMember = findViewById(R.id.rv_ait);
        mSmartRefreshLayout = findViewById(R.id.smart_refresh);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvMember.setLayoutManager(layoutManager);
    }

    @Override
    protected void initData() {
        super.initData();
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                getP().getFamilyMember(mPage);
                refreshLayout.finishLoadMore(500);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                getP().getFamilyMember(mPage);
            }
        });
        mAdapter = new FamilyAitAdapter(null);
        mRvMember.setAdapter(mAdapter);
        getP().getFamilyMember(mPage);
    }

    @Override
    public void getMemberSuccess(List<FamilyMemberDto> data) {
        setData(mPage, data);
    }


    private void setData(int pageIndex, List<FamilyMemberDto> data) {
        mSmartRefreshLayout.finishLoadMore();
        mSmartRefreshLayout.finishRefresh();
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            if (size > 0) {
                getP().aitRead(service.getUserId(), data.get(0).id);
            }
            mAdapter.setList(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            } else {
                mPage -= 1;
            }
        }
    }

}
