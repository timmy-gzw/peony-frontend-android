package com.tftechsz.common.base;

import android.os.Bundle;
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

import java.util.List;

import io.reactivex.Flowable;

public abstract class BaseListActivity<T> extends BaseMvpActivity<IListView<T>, BaseListPresenter<T>> implements IListView<T> {

    protected int page = 1;
    protected SmartRefreshLayout smartRefreshLayout;
    protected RecyclerView recyclerView;
    protected BaseQuickAdapter<T, BaseViewHolder> adapter;
    public boolean isRefresh = true;

    @Override
    protected void initView(Bundle savedInstanceState) {
        smartRefreshLayout = findViewById(R.id.smartrefreshlayout);
        recyclerView = findViewById(R.id.recycleview);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refresh();
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            isRefresh = false;
            BaseListActivity.this.page = BaseListActivity.this.page + 1;
            p.getListData(page);
        });
        p.getListData(page);
    }

    protected void refresh() {
        isRefresh = true;
        BaseListActivity.this.page = 1;
        p.getListData(1);
    }

    @Override
    public BaseListPresenter<T> initPresenter() {
        return new BaseListPresenter<T>(setNetObservable());
    }

    @Override
    public void setData(List<T> datas, int page) {
        smartRefreshLayout.closeHeaderOrFooter();
        smartRefreshLayout.setEnableLoadMore(datas.size() == 20);
        this.page = page;
        if (isRefresh) {
            if (null == adapter) {
                recyclerView.setLayoutManager(setLayoutManager());
                adapter = new BaseQuickAdapter<T, BaseViewHolder>(setItemLayoutRes(), datas) {
                    @Override
                    protected void convert(@NonNull BaseViewHolder helper, T item) {

                    }

                    @Override
                    public void onBindViewHolder(BaseViewHolder holder, int position) {
                        super.onBindViewHolder(holder, position);
                        if (position >= getDatas().size()) {
                            return;
                        }
                        bingViewHolder(holder, getDatas().get(position), position);
                    }
                };
                recyclerView.setAdapter(adapter);
                if (getHeadView() != null) {
                    adapter.addHeaderView(getHeadView());
                }
                View v = LayoutInflater.from(this).inflate(R.layout.base_empty_view, null, false);

                if (setEmptyImg() != 0) {
                    ImageView ivEmpty = v.findViewById(R.id.iv_empty);
                    ivEmpty.setImageResource(setEmptyImg());
                }
                if (!TextUtils.isEmpty(setEmptyContent())) {
                    TextView tvContent = v.findViewById(R.id.tv_empty);
                    tvContent.setText(setEmptyContent());
                }
                adapter.setEmptyView(v);
            } else {
                adapter.setList(datas);
            }
        } else {
            if (adapter != null)
                adapter.addData(datas);
        }

    }

    public void setNtifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public List<T> getDatas() {
        return adapter.getData();
    }

    @Override
    public Flowable httpRequest() {
        return setNetObservable();
    }

    public View getHeadView() {
        return null;
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
