package com.tftechsz.family.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyRecruitAdapter;
import com.tftechsz.family.databinding.ActFamilyRecruitBinding;
import com.tftechsz.family.entity.dto.FamilyRecruitDto;
import com.tftechsz.family.mvp.IView.IFamilyRecruitView;
import com.tftechsz.family.mvp.presenter.FamilyRecruitPresenter;
import com.tftechsz.family.widget.pop.FamilyRecruitPop;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 包 名 : com.tftechsz.family.mvp.ui.activity
 * 描 述 : 家族招募
 */
@Route(path = ARouterApi.ACTIVITY_FAMILY_RECRUIT)
public class FamilyRecruitActivity extends BaseMvpActivity<IFamilyRecruitView, FamilyRecruitPresenter> implements IFamilyRecruitView, OnRefreshLoadMoreListener, OnItemChildClickListener {

    private ActFamilyRecruitBinding mBind;
    private FamilyRecruitAdapter mAdapter;
    private int page = 1;
    private FamilyRecruitPop mPop;

    @Override
    public FamilyRecruitPresenter initPresenter() {
        return new FamilyRecruitPresenter();
    }

    @Override
    protected int getLayout() {
        mBind = DataBindingUtil.setContentView(this, R.layout.act_family_recruit);
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true).setTitle("家族招募中").build();
        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new FamilyRecruitAdapter();
        mAdapter.addChildClickViewIds(R.id.root, R.id.red_btn);
        mAdapter.setOnItemChildClickListener(this);
        mBind.recy.setAdapter(mAdapter);
        mBind.refresh.setOnRefreshLoadMoreListener(this);
        p.getRecruitList(page);
    }

    @Override
    public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
        page++;
        p.getRecruitList(page);
    }

    @Override
    public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
        page = 1;
        p.getRecruitList(page);
    }

    @Override
    public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter adapter, @NonNull @NotNull View view, int position) {
        FamilyRecruitDto item = mAdapter.getItem(position);
        if (view.getId() == R.id.root) { //点击打开家族详情
            ARouterUtils.toFamilyDetail(item.family_id,1);
        }
        if (view.getId() == R.id.red_btn) { //点击领红包加入
            if (!ClickUtil.canOperate()) {
                return;
            }
            if (mPop == null) {
                mPop = new FamilyRecruitPop(mContext, () -> mActivity.finish());
            }
            mPop.setItem(item);
            mPop.showPopupWindow();
        }
    }

    @Override
    public void getRecruitListSuccess(List<FamilyRecruitDto> data) {
        mBind.refresh.finishLoadMore(true);
        mBind.refresh.finishRefresh(true);
        if (data != null) {
            if (page == 1) {
                mAdapter.setList(data);
            } else {
                mAdapter.addData(data);
            }
            mBind.refresh.setNoMoreData(false);
        } else {
            mBind.refresh.setNoMoreData(true);
        }
        mBind.include.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void getRecruitListFail() {
        mBind.refresh.finishLoadMore(false);
        mBind.refresh.finishRefresh(false);
    }
}
