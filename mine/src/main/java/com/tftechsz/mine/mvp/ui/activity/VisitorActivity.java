package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.NetworkUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.VisitorAdapter;
import com.tftechsz.mine.databinding.ActVisitorBinding;
import com.tftechsz.mine.entity.VisitorDto;
import com.tftechsz.mine.mvp.IView.IVisitorView;
import com.tftechsz.mine.mvp.presenter.VisitorPresenter;

import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 访客记录
 */
@Route(path = ARouterApi.ACTIVITY_VISITOR)
public class VisitorActivity extends BaseMvpActivity<IVisitorView, VisitorPresenter> implements IVisitorView {
    private ActVisitorBinding mBind;
    private VisitorAdapter mAdapter;
    private View mNotDataView;
    private TextView tvEmpty;
    @Autowired
    UserProviderService service;
    private PageStateManager mPageManager;

    @Override
    public VisitorPresenter initPresenter() {
        return new VisitorPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_visitor);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ImmersionBar.setTitleBar(this, mBind.include);
        TextView title = mBind.include.findViewById(R.id.toolbar_title);
        title.setText("谁看过我");
        mBind.include.findViewById(R.id.toolbar_back_all).setOnClickListener(v -> finish());
        mBind.tvUnlock.setVisibility(View.VISIBLE);
        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mBind.rvVisitor.getParent(), false);
        tvEmpty = mNotDataView.findViewById(R.id.tv_empty);
        mBind.rvVisitor.setLayoutManager(new LinearLayoutManager(mContext));
        tvEmpty.setText("暂无人查看过您");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.setIsVip(service.getUserInfo().isVip());
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter = new VisitorAdapter();
        mAdapter.onAttachedToRecyclerView(mBind.rvVisitor);
        mAdapter.setIsVip(service.getUserInfo().isVip());
        mBind.rvVisitor.setAdapter(mAdapter);
        mAdapter.setEmptyView(mNotDataView);
        mBind.smartRefresh.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            getP().getVisitor(mPage);
        });
        mBind.smartRefresh.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            getP().getVisitor(mPage);
        });

        if (service.getUserInfo() != null) {
            mBind.setBotVisibility(!service.getUserInfo().isVip());
        }
        mBind.tvUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VipActivity.class);
            }
        });
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (service.getUserInfo().isVip()) {
                ARouterUtils.toMineDetailActivity(mAdapter.getData().get(position).from_user_id + "");
            }
        });

        mPageManager = PageStateManager.initWhenUse(mBind.botCons, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                getP().getVisitor(mPage);
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
            getP().getVisitor(mPage);
        }
    }

    @Override
    public void getVisitorSuccess(VisitorDto data) {
        mPageManager.showContent();
        setData(mPage, data.list);
        mBind.setVisitorCount(data.num);
    }


    private void setData(int pageIndex, List data) {
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mNotDataView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < mPageSize) {
            //第一页如果不够一页就不显示没有更多数据布局
            mBind.smartRefresh.setEnableLoadMore(false);
            //mAdapter.loadMoreEnd(false);
        } else {
            //mAdapter.loadMoreComplete();
        }
        mBind.smartRefresh.finishRefresh();
        mBind.smartRefresh.finishLoadMore();
    }


}
