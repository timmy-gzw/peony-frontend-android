package com.tftechsz.family.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tftechsz.family.R;
import com.tftechsz.family.adapter.FamilyRankAdapter;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyInfoDto;
import com.tftechsz.family.entity.dto.FamilyRankDto;
import com.tftechsz.family.mvp.IView.IFamilyRankView;
import com.tftechsz.family.mvp.presenter.FamilyRankPresenter;
import com.tftechsz.family.mvp.ui.activity.FamilyDetailActivity;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;

/**
 * 家族排行
 */
public class FamilyRankFragment extends BaseMvpFragment<IFamilyRankView, FamilyRankPresenter> implements IFamilyRankView, OnRefreshListener {

    public static final String TYPE = "type";
    public static final String TYPE_FAMILY_ID = "family_id";
    public String mType;
    private int mFamilyId;
    private RecyclerView mRvRank;
    private FamilyRankAdapter mAdapter;
    private SmartRefreshLayout smartRefreshLayout;

    public static FamilyRankFragment newInstance(String type, int familyId) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putInt(TYPE_FAMILY_ID, familyId);
        FamilyRankFragment fragment = new FamilyRankFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
            mFamilyId = getArguments().getInt(TYPE_FAMILY_ID);
        }
//        ImmersionBar.with(this).statusBarDarkFont(false).init();
        smartRefreshLayout = getView(R.id.smartrefreshlayout);
        mRvRank = getView(R.id.recycleview);
        mRvRank.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FamilyRankAdapter(null, mFamilyId);
        mAdapter.onAttachedToRecyclerView(mRvRank);
        mRvRank.setAdapter(mAdapter);
        smartRefreshLayout.setOnRefreshListener(this);
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage = mPage + 1;
            p.rankList(mPage, mType);
        });
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.base_empty_view, null, false);
        mAdapter.setEmptyView(v);
        mAdapter.addChildClickViewIds(R.id.tv_add);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_add) {
                    apply(mAdapter.getItem(position).family_id);
                }
            }
        });
        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            Intent intent = new Intent(getActivity(), FamilyDetailActivity.class);
            intent.putExtra("familyId", mAdapter.getData().get(position).family_id);
            startActivity(intent);
        });
        initBus();
        p.rankList(mPage, mType);
    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_REFRESH_FAMILY_BTN) {
                                if (mAdapter.getData().size() > 0) {
                                    for (int i = 0, j = mAdapter.getItemCount(); i < j; i++) {
                                        FamilyInfoDto dto = mAdapter.getItem(i);
                                        if (dto.family_id == event.familyId) {
                                            dto.is_apply_status = 1;
                                            TextView tvAdd = (TextView) mAdapter.getViewByPosition(i, R.id.tv_add);
                                            if (tvAdd != null) {
                                                tvAdd.setText("已申请");
                                                tvAdd.setEnabled(false);
                                                tvAdd.setTextColor(Color.parseColor("#999999"));
                                                tvAdd.setBackgroundResource(R.drawable.bg_gray_radius17);
                                            }
                                            break;
                                        }
                                    }
                                }
                            } else if (event.type == Constants.NOTIFY_FAMILY_REFRESH) {
                                mPage = 1;
                                p.rankList(mPage, mType);
                            }
                        }
                ));
    }


    /**
     * 申请加入群组
     */
    private void apply(int familyId) {
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(FamilyApiService.class).apply(familyId, "")
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                            toastTip(response.getData());
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_FAMILY_BTN, familyId));
                            //p.rankList(mPage, mType);
                    }
                }));
    }


    @Override
    protected int getLayout() {
        return R.layout.base_list;
    }


    @Override
    protected void initData() {
        //StatusBarUtil.setLightStatusBar(getActivity(), false, true);
    }

    @Override
    public void getFamilyRankSuccess(FamilyRankDto data) {
        if (null == data.list) {
            return;
        }
        mAdapter.setFamilyId(data.family_id);
        int pageSize = 20;
        if (data.list.size() < pageSize) {
            smartRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        smartRefreshLayout.finishLoadMore();
        smartRefreshLayout.finishRefresh();
        if (mPage == 1) {
            mAdapter.setList(data.list);
        } else {
            mAdapter.addData(data.list);
        }
    }

    @Override
    public FamilyRankPresenter initPresenter() {
        return new FamilyRankPresenter();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_REFRESH));
    }
}
