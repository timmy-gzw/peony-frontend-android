package com.tftechsz.party.widget.pop;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyManagerAdapter;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.PartyManagerListBean;
import com.tftechsz.party.entity.TabEntity;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 派对管理
 */
public class PartyManagerPopWindow extends BaseBottomPop implements View.OnClickListener, PartyManagerAdapter.IClickCancelData {


    private final UserProviderService service;
    private PartyApiService chatApiService;
    protected CompositeDisposable mCompositeDisposable;
    private Context context;
    private RecyclerView mRecView;
    private PartyManagerAdapter partyManagerAdapter;
    private CommonTabLayout mTabLayout;
    private PartyManagerListBean partyManagerListBean;
    private boolean isAdmin; //是否房管
    /**
     * 0房管，1拉黑，2踢出，3禁言
     */
    private int mPosition;
    private String mPartyRoomId;
    private LinearLayout mTvNotData;
    public SmartRefreshLayout mSmartRefreshLayout;
    public TextView mTvBlank;

    public PartyManagerPopWindow(Context context, boolean isAdmin, String partyRoomId) {
        super(context);
        this.context = context;
        this.isAdmin = isAdmin;
        this.mPartyRoomId = partyRoomId;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = new RetrofitManager().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        mRecView = findViewById(R.id.recyclerView);
        setPopupFadeEnable(true);
        initUI();
        setOutSideTouchable(false);
    }


    @SuppressLint("WrongViewCast")
    private void initUI() {
        mTvBlank = findViewById(R.id.tv_blank_data);
        mSmartRefreshLayout = findViewById(R.id.refresh);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> net());

        mTvNotData = findViewById(R.id.emptyBg);
        partyManagerAdapter = new PartyManagerAdapter(this);
        mRecView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecView.setAdapter(partyManagerAdapter);
        mTabLayout = findViewById(R.id.tl_6);
        ArrayList<CustomTabEntity> titles = new ArrayList<>();
        if (isAdmin) {
            titles.add(new TabEntity("房管", 0, 0));
        }
        titles.add(new TabEntity("禁言", 0, 0));
        titles.add(new TabEntity("拉黑", 0, 0));
        titles.add(new TabEntity("踢出", 0, 0));

        mTabLayout.setTabData(titles);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

                initPosition(position);

                if (partyManagerListBean == null) {
                    net();
                } else {
                    partyManagerAdapter.updateTag(mPosition, partyManagerListBean);
                    setNotDataUi();
                }

                if (position == 0) {
                    mTvBlank.setText("你还没有房管呦~");
                } else {
                    mTvBlank.setText("暂无数据");
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    /**
     * 初始化position
     */
    private void initPosition(int position) {
        if (isAdmin) {
            //房管多一项  区分position
            if (position == 2) {
                mPosition = 1;
            } else if (position == 3) {
                mPosition = 2;
            } else if (position == 1) {
                mPosition = 3;
            } else {
                mPosition = 0;
            }
            if (mTvBlank != null && position == 0) {
                mTvBlank.setText("你还没有房管呦~");
            }
        } else {
            if (position == 0) {
                mPosition = 3;
            } else {
                mPosition = position;
            }
        }
    }


    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        if (mTabLayout != null) {
            mTabLayout.setCurrentTab(0);
        }
        initPosition(0);
        net();
    }


    private void net() {
        mCompositeDisposable.add(chatApiService
                .partyManager(mPartyRoomId)
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<PartyManagerListBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<PartyManagerListBean> response) {
                        partyManagerListBean = response.getData();
                        partyManagerAdapter.updateTag(mPosition, response.getData());
                        setNotDataUi();
//                        partyManagerAdapter.addData(response.getData().black_list);
                        mSmartRefreshLayout.finishRefresh();
                        mSmartRefreshLayout.finishLoadMore();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSmartRefreshLayout.finishRefresh();
                        mSmartRefreshLayout.finishLoadMore();
                        setNotDataUi();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mSmartRefreshLayout.finishRefresh();
                        mSmartRefreshLayout.finishLoadMore();
                        setNotDataUi();
                    }
                }));

    }

    private void setNotDataUi() {
        if (partyManagerAdapter.getData().size() > 0) {
            mRecView.setVisibility(View.VISIBLE);
            mTvNotData.setVisibility(View.GONE);
        } else {
            mRecView.setVisibility(View.GONE);
            mTvNotData.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_party_manager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

    }

    @Override
    public void clickCancel(PartyManagerListBean.ListDTO listDTO) {
        //取消
        mCompositeDisposable.add(chatApiService
                .setAct(mPartyRoomId, mPosition, listDTO.user_id + "")
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (response.getData() != null) {
                            ToastUtil.showToast(mContext, response.getData());
                            net();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));


    }
}
