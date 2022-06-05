package com.tftechsz.home.mvp.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.UserInfo;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.umeng.analytics.MobclickAgent;
import com.tftechsz.home.R;
import com.tftechsz.home.adapter.RecommendAdapter;
import com.tftechsz.home.mvp.iview.ISearchView;
import com.tftechsz.home.mvp.presenter.SearchPresenter;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;

import java.util.List;

import razerdp.util.KeyboardUtils;

public class SearchActivity extends BaseMvpActivity<ISearchView, SearchPresenter> implements View.OnClickListener, ISearchView {

    private RecyclerView mRvSearch;
    private EditText mEtSearch;
    private String mKeyword;
    private int mPage = 1;
    private RecommendAdapter mAdapter;
    protected SmartRefreshLayout smartRefreshLayout;
    private View mNotDataView;
    private TextView tvEmpty;
    private LottieAnimationView mLottie;
    @Autowired
    UserProviderService service;

    @Override
    public SearchPresenter initPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.toolbar_back_all).setOnClickListener(this);
        smartRefreshLayout = findViewById(R.id.smart_refresh);
        mRvSearch = findViewById(R.id.rv_search);
        mRvSearch.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.toolbar_tv_menu).setOnClickListener(this);
        mEtSearch = findViewById(R.id.et_search);
        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mRvSearch.getParent(), false);
        tvEmpty = mNotDataView.findViewById(R.id.tv_empty);
        tvEmpty.setText(String.format("快速脱单 就上%s", mContext.getString(R.string.app_name)));
        ImageView ivEmpty = mNotDataView.findViewById(R.id.iv_empty);
        ivEmpty.setImageResource(R.mipmap.ic_search_empty);
        mLottie = findViewById(R.id.animation_view);
        mLottie.setImageAssetsFolder(Constants.ACCOST_GIFT);//设置data.json引用的图片资源文件夹名称
    }


    @Override
    protected void initData() {
        super.initData();

        mAdapter = new RecommendAdapter(1);
        mAdapter.onAttachedToRecyclerView(mRvSearch);
        mRvSearch.setAdapter(mAdapter);
        mAdapter.setEmptyView(mNotDataView);
        mAdapter.addChildClickViewIds(R.id.ll_like);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            UserInfo userInfo = mAdapter.getData().get(position);
            ARouterUtils.toMineDetailActivity(String.valueOf(userInfo.getUser_id()));
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            if (id == R.id.ll_like) {   //搭讪
                UserInfo item = mAdapter.getItem(position);
                if (item == null) {
                    return;
                }
                if (item.isAccost()) {// 搭讪过 进入聊天
                    if (service.getUserInfo().isGirl()) {   //判断女性
                        p.getMsgCheck(item.getUser_id() + "");
                    } else {
                        ARouterUtils.toChatP2PActivity(item.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                        MobclickAgent.onEvent(mContext, "home_accost_message_boy");
                    }
                } else { //没有搭讪, 进行搭讪
                    p.accostUser(position, String.valueOf(item.getUser_id()));
                }
            }
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPage = 1;
            p.searchUser(mPage, mKeyword);
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPage += 1;
            p.searchUser(mPage, mKeyword);
        });
        mEtSearch.setOnKeyListener((v, keyCode, event) -> {        // 开始搜索
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                KeyboardUtils.close(this);
                //搜索逻辑
                mPage = 1;
                mKeyword = mEtSearch.getText().toString();
                p.searchUser(mPage, mKeyword);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_back_all) {
            finish();
        } else if (id == R.id.toolbar_tv_menu) {
            mPage = 1;
            mKeyword = mEtSearch.getText().toString();
            p.searchUser(mPage, mKeyword);

        }
    }

    @Override
    public void getSearchSuccess(List<UserInfo> data) {
        setData(mPage, data);
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    @Override
    public void getSearchFail(String msg) {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    @Override
    public void accostUserSuccess(int position, AccostDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS, data.gift.animation));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, mAdapter.getData().get(position).getUser_id()));

            //mAdapter.notifyItemChanged(position);
            try {
                mAdapter.getItem(position).setIs_accost(1);
                mAdapter.startAnimationNew(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
            CommonUtil.sendAccostGirlBoy(service, mAdapter.getItem(position).getUser_id(), data, 2);
        }
    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
        }
    }

    private void setData(int pageIndex, List data) {
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                tvEmpty.setText("用户不存在");
                mAdapter.setEmptyView(mNotDataView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < mPageSize) {
            //第一页如果不够一页就不显示没有更多数据布局
            smartRefreshLayout.setEnableLoadMore(false);
            //mAdapter.loadMoreEnd(false);
        } else {
            //mAdapter.loadMoreComplete();
        }
//        mRvSearch.setItemViewCacheSize(mAdapter.getItemCount());
    }


}
