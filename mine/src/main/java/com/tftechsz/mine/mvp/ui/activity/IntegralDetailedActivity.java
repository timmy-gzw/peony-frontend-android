package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.IntegralGroupAdapter;
import com.tftechsz.mine.entity.dto.ExchangeRecord;
import com.tftechsz.mine.mvp.IView.IIntegralDetailView;
import com.tftechsz.mine.mvp.presenter.IntegralDetailedPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 清单
 */
@Route(path = ARouterApi.ACTIVITY_INTEGRAL_DETAILED)
public class IntegralDetailedActivity extends BaseMvpActivity<IIntegralDetailView, IntegralDetailedPresenter> implements IIntegralDetailView {

    protected SmartRefreshLayout smartRefreshLayout;
    private ArrayList<ExchangeRecord> mData;
    private IntegralGroupAdapter mAdapter;
    private RecyclerView mRvIntegralDetail;
    private int mType;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mRvIntegralDetail = findViewById(R.id.recycleview);
        mRvIntegralDetail.setLayoutManager(new LinearLayoutManager(this));
        smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        mLlEmpty = findViewById(com.tftechsz.common.R.id.ll_empty);
        mTvEmpty = findViewById(com.tftechsz.common.R.id.tv_empty);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            loadData(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            loadData(false);
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getIntent().getIntExtra(Interfaces.EXTRA_TYPE, 0);
        String title = "";
        if (mType == 0) {   //清单/收益记录
            title = "收益记录";
            mTvEmpty.setText("暂无收益记录");
        } else if (mType == 1) {   //金币清单/收支记录
            title = "收支记录";
            mTvEmpty.setText("暂无收支记录");
        } else if (mType == 2 || mType == 5) {   //兑换记录
            title = "兑换记录";
            mTvEmpty.setText("暂无兑换记录");
        } else if (mType == 3) {   //聊天卡消耗记录
            title = "道具记录";
            mTvEmpty.setText("暂无道具记录");
        } else if (mType == 4) {   //音符清单
            title = "音符清单";
            mTvEmpty.setText("暂无音符数据");
        }
        new ToolBarBuilder().showBack(true).setTitle(title).build();
        mData = new ArrayList<>();
        mAdapter = new IntegralGroupAdapter(this, mData, mType);
        mRvIntegralDetail.setAdapter(mAdapter);
        mLlEmpty.setVisibility(View.VISIBLE);
        loadData(true);
    }

    /**
     * 加载数据
     */
    private void loadData(boolean isShow) {
        if (mType == 0) {
            p.getIntegralDetailed(mPage, isShow);
        } else if (mType == 1) {
            p.getCoinDetailed(mPage, isShow);
        } else if (mType == 2) {
            p.getExchangeRecord(mPage, isShow);
        } else if (mType == 3) {
            p.getSignRecord(mPage, isShow);
        } else if (mType == 4) {
            p.getNoteValueDetailed(mPage, isShow);
        } else if (mType == 5) { //音符兑换记录
            p.getNoteValueExchangeRecord(mPage, isShow);
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_exchange_record;
    }


    @Override
    public IntegralDetailedPresenter initPresenter() {
        return new IntegralDetailedPresenter();
    }

    @Override
    public void getIntegralDetailSuccess(List<ExchangeRecord> data) {
        if (null == data) {
            if (mPage == 1) {
                mLlEmpty.setVisibility(View.VISIBLE);
            }
        } else {
            if (mPage == 1) {
                mData.clear();
            }
            mData.addAll(data);
            if (mData.size() <= 0)
                mLlEmpty.setVisibility(View.VISIBLE);
            else
                mLlEmpty.setVisibility(View.GONE);
        }
        mAdapter.notifyDataChanged();
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    @Override
    public void getIntegralDetailFail(String data) {

    }


    @Override
    public void showLoadingDialog() {
        //super.showLoadingDialog();
    }
}
