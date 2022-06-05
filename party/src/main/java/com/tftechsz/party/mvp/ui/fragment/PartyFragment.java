package com.tftechsz.party.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.service.PartyAudioService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyAdapter;
import com.tftechsz.party.databinding.FragmentPartyBinding;
import com.tftechsz.party.entity.dto.PartyDto;
import com.tftechsz.party.entity.dto.PartyInfoDto;
import com.tftechsz.party.mvp.IView.IPartyView;
import com.tftechsz.party.mvp.presenter.PartyPresenter;
import com.tftechsz.party.mvp.ui.activity.PartySettingActivity;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.RectangleIndicator;

import java.util.ArrayList;
import java.util.List;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static com.tftechsz.common.Constants.PARAM_ROOM_ID;

public class PartyFragment extends BaseMvpFragment<IPartyView, PartyPresenter> implements IPartyView, View.OnClickListener {
    private PartyAdapter mAdapter;
    private PageStateManager mPageManager;
    private FragmentPartyBinding mBind;
    private View mNotDataView;
    private String mHourRankUrl;
    private int mPartyRoomId;
    private String mRoomId;
    private String mRoomIcon;
    private int mDressStatus; // 是否是装扮 1装扮 0第一次装扮
    private boolean isFirst = false;
    private int mMessageNum;
    @Autowired
    UserProviderService service;
    private Banner<PartyDto.OptionDTOData, BannerImageAdapter<PartyDto.OptionDTOData>> mBanner;
    //初始化banner
    private boolean mFlagIsInitBanner;
    //可见item
    int mFirstVisiblePosition, mLastVisiblePosition;
    //    private View viewHead;
    private int mLoginStatus = 6;

    @Override
    protected PartyPresenter initPresenter() {
        return new PartyPresenter();
    }

    @Override
    protected int getLayout() {
        return 0;
    }


    @Override
    public int getBindLayout() {
        return R.layout.fragment_party;
    }


    @Override
    public void initUI(Bundle savedInstanceState) {
        mBind = (FragmentPartyBinding) getBind();
        ImmersionBar.with(this).titleBarMarginTop(mBind.rlTop).init();
        mBind.tvDressUp.setOnClickListener(this);
        mBind.ivHourRank.setOnClickListener(this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mBind.rvParty.setLayoutManager(layoutManager);
        if (mBind.rvParty.getItemAnimator() != null)
            ((SimpleItemAnimator) mBind.rvParty.getItemAnimator()).setSupportsChangeAnimations(false);
        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mBind.rvParty.getParent(), false);
        mBind.refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage += 1;
                p.getPartyList(mPage);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtils.isConnected()) {
                    if (mPageManager != null) {
                        mPageManager.showError(null);
                        mBind.refresh.finishRefresh(false);
                    }
                } else {
                    refresh();
                }
            }
        });
        scrollviewItemListener();
    }

    /**
     * 监听当前可见item-日志统计使用
     */
    private void scrollviewItemListener() {
        mBind.rvParty.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_DRAGGING) {
                    // DES: 找出当前可视Item位置
                    RecyclerView.LayoutManager layoutManager = mBind.rvParty.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        mFirstVisiblePosition = linearManager.findFirstVisibleItemPosition();
                        mLastVisiblePosition = linearManager.findLastVisibleItemPosition();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    /**
     * 设置消息数量
     */
    public void setMessageNum(int num) {
        mMessageNum = num;
    }

    private boolean mFlagRefresh;//刷新一次

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst)
            refresh();
        isFirst = true;
        MMKVUtils.getInstance().encode(Constants.PARAM_IS_CALL_CLOSE, 0);
        if (ServiceUtils.isServiceRunning(PartyAudioService.class))
            MMKVUtils.getInstance().encode(Constants.PARTY_IS_RUN, 1);
        else
            MMKVUtils.getInstance().encode(Constants.PARTY_IS_RUN, 0);
        if (!mFlagRefresh) {
            mFlagRefresh = true;
            //和initview一样执行一次  每天进入一次派对功能用到
            if (!NetworkUtils.isConnected()) {
                mPageManager.showError(null);
            } else {
                refresh();
            }
        }
    }


    private void refresh() {
        mPage = 1;
        p.getPartyList(mPage);
    }

    @Override
    protected void initData() {
        initRxBus();
        mPageManager = PageStateManager.initWhenUse(mBind.refresh, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showContent();
                mBind.refresh.autoRefresh();
            }
        });
        mAdapter = new PartyAdapter();
//        viewHead = LayoutInflater.from(mContext).inflate(R.layout.layout_party_header, null);
        mBanner = mBind.banner;
//        mAdapter.addHeaderView(viewHead);

        mBind.rvParty.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            PartyInfoDto infoDto = mAdapter.getData().get(position);
            if (infoDto != null) {
                if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
                p.joinParty(getActivity(), mLoginStatus, mMessageNum, infoDto.getRoom_id(), infoDto.getId(), infoDto.getIcon(), infoDto.getBg_icon(), "in");
            }
        });
    }


    private void initRxBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            //当前的登录状态
                            mLoginStatus = event.familyId;
                        }
                ));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_hour_rank) {   //小时榜
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            BaseWebViewActivity.start(getActivity(), "", mHourRankUrl, false, 0, 1);
        } else if (id == R.id.tv_dress_up) {  // 装扮派对
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            if (mDressStatus == 0) {
                Intent intent = new Intent(requireActivity(), PartySettingActivity.class);
                intent.putExtra(PARAM_ROOM_ID, mPartyRoomId);
                startActivity(intent);
            } else {
                p.joinParty(getActivity(), mLoginStatus, mMessageNum, mRoomId, mPartyRoomId, mRoomIcon, "", "in");
            }
        }
    }

    @Override
    public void getPartyListSuccess(PartyDto data) {
        mBind.refresh.finishRefresh(true);
        mBind.refresh.finishLoadMore(true);
        mDressStatus = data.getRoom_dress_status();
        mHourRankUrl = data.getRank_link();
        mPartyRoomId = data.getParty_room_id();
        mRoomId = data.getRoom_id();
        mRoomIcon = data.getIcon();
        // 0不是房主 1是房主
        mBind.tvDressUp.setVisibility(data.getIs_room_mainer() == 1 ? View.VISIBLE : View.GONE);
        //是否是装扮 1装扮 0第一次装扮
        if (mDressStatus == 0) {
            mBind.tvDressUp.setText("装扮派对");
        } else {
            mBind.tvDressUp.setText("我的派对");
        }
        //小时榜
        mBind.ivHourRank.setVisibility(TextUtils.isEmpty(mHourRankUrl) ? View.GONE : View.VISIBLE);
        setData(mPage, data.getList());
        if (!mFlagIsInitBanner && data.banner_list != null && data.banner_list.size() > 0) {
            //banner
            mBanner.setVisibility(View.VISIBLE);
            mBanner.setIndicator(new RectangleIndicator(mContext))
                    .setIndicatorMargins(new IndicatorConfig.Margins(0, 0, 0, ConvertUtils.dp2px(10)));
            mBanner.setAdapter(new BannerImageAdapter<PartyDto.OptionDTOData>(data.banner_list) {
                @Override
                public void onBindView(BannerImageHolder holder, PartyDto.OptionDTOData data, int position, int size) {
                    holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    GlideUtils.loadRouteImage(getActivity(), holder.imageView, data.img);
                    holder.imageView.setOnClickListener(v -> {

                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("banner位点击", "banner_h5_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), data.link, 25, position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME, "新年活动banner点击", mRoomId, -1)), null
                                );
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("站内活动入口点击", "event_entrance_click", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 15, data.link, position, System.currentTimeMillis(), CommonUtil.getOSName(), Constants.APP_NAME)), null
                                );

                        CommonUtil.performLink(getActivity(), new ConfigInfo.MineInfo(data.link, data.optionDTO2), position, 15);
                    });
                }
            });
            mBanner.addBannerLifecycleObserver(this);
            mBanner.setLoopTime(5000);  //设置轮播间隔时间
            mBanner.setScrollTime(600); //设置轮播滑动过程的时间
            mBanner.start();
            mFlagIsInitBanner = true;
        }

    }

    @Override
    public void getPartyListFail() {
        mBind.refresh.finishRefresh(false);
        mBind.refresh.finishLoadMore(false);
        mAdapter.setEmptyView(mNotDataView);
    }


    /**
     * 上传 列表曝光数据
     */
    public void visitPartyList() {
        if (mAdapter != null && mAdapter.getData() != null) {
            int size = mAdapter.getData().size();
            if (size > 0) {
                List<String> listRooms = new ArrayList<>();
                //上传当前曝光item
                for (int i = 0; i < size; i++) {
                    if (mFirstVisiblePosition == 0 && mLastVisiblePosition == 0) {
                        if (i < 6) {
                            listRooms.add(mAdapter.getData().get(i).getRoom_id());
                        }
                    } else {
                        if (size > mLastVisiblePosition && i >= mFirstVisiblePosition && i <= mLastVisiblePosition) {
                            listRooms.add(mAdapter.getData().get(i).getRoom_id());
                        }
                    }
                }
                if (listRooms.size() > 0) {
                    UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
                    if (service == null) {
                        return;
                    }
                    ARouter.getInstance()
                            .navigation(MineService.class)
                            .trackEvent("语音房间列表页曝光", "party_list_visit", "", JSON.toJSONString(new NavigationLogEntity(service.getUserId(), 1, System.currentTimeMillis(), listRooms, CommonUtil.getOSName(), Constants.APP_NAME)), null);

                }
            }

        }

    }

    private void setData(int pageIndex, List<PartyInfoDto> data) {
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mNotDataView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            } else {
                mPage -= 1;
            }
        }
    }

}
