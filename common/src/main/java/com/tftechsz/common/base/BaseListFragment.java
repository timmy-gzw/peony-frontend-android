package com.tftechsz.common.base;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public abstract class BaseListFragment<T> extends BaseMvpFragment<IListView<T>, BaseListPresenter<T>> implements IListView<T> {
    private ArrayList<T> datas;
    protected SmartRefreshLayout smartRefreshLayout;
    protected RecyclerView recyclerView;
    protected BaseQuickAdapter<T, BaseViewHolder> adapter;

    protected int mPage = 1;
    private boolean isRefresh = true;

    @Override
    public void initView(View v) {
        super.initView(v);
        datas = new ArrayList<>();
        smartRefreshLayout = v.findViewById(R.id.smartrefreshlayout);
        recyclerView = v.findViewById(R.id.recycleview);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            smartRefreshLayout.finishRefresh();
            isRefresh = true;
            mPage = 1;
            onRefresh();
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            isRefresh = false;
            mPage = mPage + 1;
            p.getListData(mPage);
        });
        initRv();
        p.getListData(mPage);
    }

    protected void onRefresh() {
        if (null == p) {
            return;
        }
        p.getListData(1);
    }

    public ArrayList<T> getData() {
        return (ArrayList<T>) adapter.getData();
    }

    protected void initRv() {
        recyclerView.setLayoutManager(setLayoutManager());
        adapter = new BaseQuickAdapter<T, BaseViewHolder>(setItemLayoutRes(), datas) {

            @Override
            protected void convert(@NonNull BaseViewHolder helper, T item) {
                int realPosition = getItemPosition(item) - adapter.getHeaderLayoutCount();
                bingViewHolder(helper, item, realPosition);
            }

        };
        recyclerView.setAdapter(adapter);
        if (hasEmptyView()) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.base_empty_view, null, false);
            if (setEmptyImg() != 0) {
                ImageView ivEmpty = v.findViewById(R.id.iv_empty);
                ivEmpty.setImageResource(setEmptyImg());
            }
            if (!TextUtils.isEmpty(setEmptyContent())) {
                TextView tvContent = v.findViewById(R.id.tv_empty);
                tvContent.setText(setEmptyContent());
            }
            adapter.setEmptyView(v);
        }
    }

    public boolean hasEmptyView() {
        return true;
    }

    @Override
    public BaseListPresenter<T> initPresenter() {
        return new BaseListPresenter<>(setNetObservable().compose(bindToLifecycle()));
    }

    @Override
    public void setData(List<T> datas, int page) {
        if (null == datas) {
            return;
        }
        int pageSize = 20;
        if (datas.size() < pageSize) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();
        smartRefreshLayout.finishRefresh();
        mPage = page;
        if (isRefresh || page == 1) {
            adapter.setList(datas);
        } else {
            adapter.addData(datas);
        }

    }

    @Override
    public Flowable httpRequest() {
        return setNetObservable();
    }

    public abstract Flowable setNetObservable();

    public abstract RecyclerView.LayoutManager setLayoutManager();

    public abstract int setItemLayoutRes();

    public abstract void bingViewHolder(BaseViewHolder helper, T item, int position);

    public abstract String setEmptyContent();

    public int setEmptyImg() {
        return 0;
    }

}
