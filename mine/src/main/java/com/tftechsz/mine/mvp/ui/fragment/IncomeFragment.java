package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.IntegralGroupAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.ExchangeRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 收益
 */
public class IncomeFragment extends BaseMvpFragment {

    public static final String TYPE = "type";
    private int mType;//0 收益 2 兑换
    protected SmartRefreshLayout smartRefreshLayout;
    private ArrayList<ExchangeRecord> mData;
    private IntegralGroupAdapter mAdapter;
    private RecyclerView mRvIntegralDetail;
    public LinearLayout mLlEmpty;   // 错误页面
    public TextView mTvEmpty;

    public MineApiService service;
    public MineApiService exchService;

    public static IncomeFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        IncomeFragment fragment = new IncomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_income;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mRvIntegralDetail = getView(R.id.recycleview);
        mRvIntegralDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
        smartRefreshLayout = getView(R.id.smartrefreshlayout);
        mLlEmpty = getView(com.tftechsz.common.R.id.ll_empty);
        mTvEmpty = getView(com.tftechsz.common.R.id.tv_empty);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            loadData();
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            loadData();
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (mType == 0) { //收益
            if (service == null) {
                service = RetrofitManager.getInstance().createUserApi(MineApiService.class);
            }
            mCompositeDisposable.add( service.getIntegralDetailed(mPage)
                    .compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<ExchangeRecord>> listBaseResponse) {
                            setData(listBaseResponse);
                        }
                    }));
        } else { // 兑换
            if (exchService == null) {
                exchService = RetrofitManager.getInstance().createExchApi(MineApiService.class);
            }
            mCompositeDisposable.add(exchService.getExchangeRecord(mPage)
                    .compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<ExchangeRecord>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<ExchangeRecord>> listBaseResponse) {
                            setData(listBaseResponse);
                        }

                    }));
        }
    }

    private void setData(BaseResponse<List<ExchangeRecord>> listBaseResponse) {
        List<ExchangeRecord> data = listBaseResponse.getData();
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
    protected void initData() {
        mType = getArguments().getInt(TYPE);
        mData = new ArrayList<>();
        mAdapter = new IntegralGroupAdapter(getActivity(), mData, mType);
        mRvIntegralDetail.setAdapter(mAdapter);
        mLlEmpty.setVisibility(View.VISIBLE);
        loadData();
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
