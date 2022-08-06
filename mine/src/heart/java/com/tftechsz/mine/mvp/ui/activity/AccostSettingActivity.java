package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.NetworkUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.AccostSettingAdapter;
import com.tftechsz.mine.adapter.BaseItemAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.AccostSettingAdapterBean;
import com.tftechsz.mine.entity.AccostSettingBean;
import com.tftechsz.mine.entity.AccostSettingListBean;
import com.tftechsz.mine.entity.BaseItemBean;
import com.tftechsz.mine.mvp.IView.IAccostSettingView;
import com.tftechsz.mine.mvp.presenter.IAccostSettingPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.activity
 * 描 述 : 招呼设置
 */
@Route(path = ARouterApi.ACTIVITY_ACCOST_SETTING)
public class AccostSettingActivity extends BaseMvpActivity<IAccostSettingView, IAccostSettingPresenter> implements IAccostSettingView {

    private AccostSettingAdapter mAdapter;
    private List<AccostSettingAdapterBean> mNav;
    private PageStateManager mPageManager;
    private LinearLayout mLl_recy;

    @Override
    public IAccostSettingPresenter initPresenter() {
        return new IAccostSettingPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.act_accost_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(Interfaces.EXTRA_TITLE);
        new ToolBarBuilder().showBack(true)
                .setTitle(title == null ? "招呼设置" : title)
                .build();
        mLl_recy = findViewById(R.id.ll_recy);
        RecyclerView recy = findViewById(R.id.rv_accost_setting);
        recy.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AccostSettingAdapter();
        recy.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((adapter, view, position) -> CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(mNav.get(position).url), 0, 0));

        mPageManager = PageStateManager.initWhenUse(mLl_recy, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                getAdApter();
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
            getAdApter();
        }
    }

    private void getAdApter() {
        mCompositeDisposable.add(RetrofitManager.getInstance().createConfigApi(MineApiService.class)
                .getAccostSettingAdapter().compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<AccostSettingBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<AccostSettingBean> response) {
                        if (mPageManager != null) mPageManager.showContent();
                        if (!isDestroyed() && response.getData() != null && response.getData().nav != null) {
                            mNav = response.getData().nav;
                            List<BaseItemBean> beanList = new ArrayList<>();
                            for (AccostSettingAdapterBean nav : response.getData().nav) {
                                beanList.add(new BaseItemBean(nav.icon, nav.title, nav.complete_msg, nav.complete_msg_color));
                            }
                            mAdapter.setList(beanList);
                        }
                    }
                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.isConnected()) {
            if (mPageManager != null)
                mPageManager.showError(null);
        } else {
            getAdApter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNav != null) {
            mNav.clear();
            mNav = null;
        }
    }

    @Override
    public void getAccostListSuccess(List<AccostSettingListBean> data) {

    }

    @Override
    public void addAccostSettingSuccess() {

    }

    @Override
    public void addAccostSettingError() {

    }

    @Override
    public void updateAccostSettingSuccess(int position) {

    }

    @Override
    public void delAccostSettingSuccess(int position) {

    }
}
