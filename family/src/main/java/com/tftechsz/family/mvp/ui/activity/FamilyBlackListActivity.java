package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyBlackListAdapter;
import com.tftechsz.family.databinding.ActFamilyBlackListBinding;
import com.tftechsz.family.entity.dto.FamilyBlackListDto;
import com.tftechsz.family.mvp.IView.IFamilyBlackListView;
import com.tftechsz.family.mvp.presenter.FamilyBlackListPresenter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.ARouterUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 包 名 : com.tftechsz.family.mvp.ui.activity
 * 描 述 : 家族黑名单
 */
public class FamilyBlackListActivity extends BaseMvpActivity<IFamilyBlackListView, FamilyBlackListPresenter> implements IFamilyBlackListView {

    private ActFamilyBlackListBinding mBind;
    private int page = 1, mFamilyId;
    private FamilyBlackListAdapter mAdapter;

    @Override
    public FamilyBlackListPresenter initPresenter() {
        return new FamilyBlackListPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_family_black_list);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().setTitle("家族黑名单").showBack(true).build();
        mFamilyId = getIntent().getIntExtra(Interfaces.EXTRA_ID, 0);
        mBind.setHasData(false);
        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FamilyBlackListAdapter();
        mBind.recy.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.root_black_list, R.id.tv_black_list_unlock);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            FamilyBlackListDto item = mAdapter.getItem(position);
            if (view.getId() == R.id.root_black_list) {
                if (item != null) {
                    ARouterUtils.toMineDetailActivity(String.valueOf(item.user_id));
                }
            } else if (view.getId() == R.id.tv_black_list_unlock) {
                if (item != null) {
                    p.blackUser(mFamilyId, item.user_id);
                }
            }
        });
        mBind.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                page++;
                p.getBlackList(String.valueOf(mFamilyId), page);
            }

            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                page = 1;
                p.getBlackList(String.valueOf(mFamilyId), page);
            }
        });
        p.getBlackList(String.valueOf(mFamilyId), page);
    }

    @Override
    public void getFamilyBlackListSuccess(List<FamilyBlackListDto> data) {
        if (page == 1) {
            mAdapter.setList(data);
        } else {
            mAdapter.addData(data);
        }
        mBind.setHasData(mAdapter.getItemCount() != 0);
        mBind.refresh.setEnableLoadMore(data != null && data.size() > 0);
        mBind.refresh.finishLoadMore(true);
        mBind.refresh.finishRefresh(true);
    }

    @Override
    public void getFamilyBlackListError() {
        mBind.refresh.finishLoadMore(false);
        mBind.refresh.finishRefresh(false);
    }

    @Override
    public void blackUserSuccess(int userId) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            FamilyBlackListDto item = mAdapter.getItem(i);
            if (item.user_id == userId) {
                mAdapter.removeAt(i);
                break;
            }
        }
        mBind.setHasData(mAdapter.getItemCount() != 0);
    }
}
