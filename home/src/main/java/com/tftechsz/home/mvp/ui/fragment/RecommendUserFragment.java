package com.tftechsz.home.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.NavigationLogEntity;
import com.tftechsz.common.event.AccostSuccessEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.UpdateEvent;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.refresh.FastScrollLinearLayoutManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.home.R;
import com.tftechsz.home.adapter.RecommendAdapter;
import com.tftechsz.home.entity.req.RecommendReq;
import com.tftechsz.home.mvp.iview.IHomeView;
import com.tftechsz.home.mvp.presenter.HomePresenter;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class RecommendUserFragment extends BaseMvpFragment<IHomeView, HomePresenter> implements IHomeView {

    public static final int TYPE_RECOMMEND = 1;   //推荐
    public static final int TYPE_NEAR = 2;   //附近
    public int mType;
    public RecyclerView mRvUser;
    private View mLocationView;
    private TextView mLocationText;
    private RecommendAdapter mAdapter;
    private int page = 1;
    private View mNotDataView;
    private UserProviderService service;
    private int isShow = 2;
    private int mRefreshCount = 0;   //判断刷新
    //    private View mFooterView;
//    private ProgressDrawable mProgressDrawable;
//    private boolean mIsEnableLoadMore;
    private final int minLeftItemCount = 10;
    private int lastItemCount;
    private boolean canLoadMore = true;
    private PageStateManager mPageManager;
    public FastScrollLinearLayoutManager mLayoutManager;
    private int mOpenLocationType;

    public static RecommendUserFragment newInstance(String type) {
        return newInstance(type, 0);
    }

    public static RecommendUserFragment newInstance(String type, int openLocation) {
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(Interfaces.MAIN_TAB_RECOMMEND)) {
                args.putInt("type", TYPE_RECOMMEND);
            } else if (type.equals(Interfaces.MAIN_TAB_NEAR)) {
                args.putInt("type", TYPE_NEAR);
            } else {
                return null;
            }
        }
        args.putInt("Location", openLocation);
        RecommendUserFragment recommend = new RecommendUserFragment();
        recommend.setArguments(args);
        return recommend;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mType = getArguments().getInt("type", 1);
            mOpenLocationType = getArguments().getInt("Location", 0);
        }
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mRvUser = getView(R.id.rv_user);
        mLocationView = getView(R.id.location_view);
        mLocationText = getView(R.id.location_text);
        getView(R.id.location_bt).setOnClickListener(v -> {
            if (getActivity() != null) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    PermissionUtil.gotoPermission(BaseApplication.getInstance());
                } else {
                    //如果用户没有打开定位服务引导用户打开定位
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, 1);
                    } else {
                        toastTip("该设备不支持位置服务");
                    }
                }
            }
        });

        mSmartRefreshLayout = getView(R.id.smart_refresh);
        mLayoutManager = new FastScrollLinearLayoutManager(getActivity());
        mRvUser.setLayoutManager(mLayoutManager);
        mNotDataView = getLayoutInflater().inflate(R.layout.base_empty_view, (ViewGroup) mRvUser.getParent(), false);
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData(false, false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtils.isConnected()) {
                    if (mPageManager != null && mSmartRefreshLayout != null) {
                        mPageManager.showError(null);
                        mSmartRefreshLayout.finishRefresh(false);
                    }
                } else {
                    loadData(true, false);
                }
            }
        });
        mRvUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    CommonUtil.hasPerformAccost(service.getUserInfo());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int itemCount = layoutManager.getItemCount();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItemCount != itemCount && lastPosition == itemCount - 1) { //最后一条
                        lastItemCount = itemCount;
                        loadData(false, false);
                    } else {
                        if (itemCount > minLeftItemCount) {
                            if (lastPosition == itemCount - minLeftItemCount) {
                                loadData(false, false);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_recommend_user;
    }

    @Override
    protected void initData() {
        mAdapter = new RecommendAdapter(mType);
        mAdapter.onAttachedToRecyclerView(mRvUser);
        mRvUser.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.ll_like);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            UserInfo userInfo = mAdapter.getData().get(position);
            ARouterUtils.toMineDetailActivity(String.valueOf(userInfo.getUser_id()));
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
            int id = view.getId();
            if (id == R.id.ll_like) {   //搭讪

                UserInfo item = mAdapter.getItem(position);
                if (item == null || !ClickUtil.canOperate()) {
                    return;
                }

                if (isShow == 1) {   // 只显示私信
                    ARouterUtils.toChatP2PActivity(item.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                    MobclickAgent.onEvent(mContext, "home_message_boy");
                } else {
                    if (item.isAccost()) {// 搭讪过 进入聊天
                        if (service.getUserInfo().isGirl()) {   //判断女性
                            p.getMsgCheck(item.getUser_id() + "");
                        } else {
                            ARouterUtils.toChatP2PActivity(item.getUser_id() + "", NimUIKit.getCommonP2PSessionCustomization(), null);
                            MobclickAgent.onEvent(mContext, "home_accost_message_boy");
                        }
                    } else { //没有搭讪, 进行搭讪
                        p.accostUser(position, String.valueOf(item.getUser_id()));
                        ARouter.getInstance()
                                .navigation(MineService.class)
                                .trackEvent("搭讪按钮点击事件", "accost_click", "", JSON.toJSONString(new NavigationLogEntity(CommonUtil.getOSName(), Constants.APP_NAME, service.getUserId(), item.getUser_id() + "", CommonUtil.isBtnTextHome(service) ? 2 : 1, 1, System.currentTimeMillis())), null);

                    }
                }
            }
        });
        initBus();
        //Utils.runOnUiThreadDelayed(() -> loadData(true, false), 1500);
//        mSmartRefreshLayout.autoRefresh(1500, 800, 1.5f, false);
        //进入首页有缓存数据进行加载
        if (mType == TYPE_RECOMMEND) {
            String recommend = CacheDiskUtils.getInstance().getString(Constants.RECOMMEND_USER + service.getUserId());
            if (!TextUtils.isEmpty(recommend)) {
                RecommendReq configInfo = new Gson().fromJson(recommend, RecommendReq.class);
                if (configInfo != null)
                    setData(page, configInfo.data);
            }
        }
        mPageManager = PageStateManager.initWhenUse(mSmartRefreshLayout, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showContent();
                mSmartRefreshLayout.autoRefresh();
            }
        });
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
        } else {
            if (mOpenLocationType < 3 && mType == TYPE_NEAR) {
                mLocationText.setText(mOpenLocationType < 2 ? "您未打开定位开关，打开定位授权定位权限，才能查看" : "您未授权定位权限，授权定位权限，才能查看到附近人哦");
                mSmartRefreshLayout.setEnableRefresh(false);
                mSmartRefreshLayout.setVisibility(View.GONE);
                mRvUser.setVisibility(View.GONE);
                mAdapter.setList(null);
                mLocationView.setVisibility(View.VISIBLE);
                return;
            }
            if (mAdapter.getItemCount() == 0) {
                mSmartRefreshLayout.autoRefresh();
            } else {
                Utils.runOnUiThreadDelayed(() -> loadData(true, false), 1500);
            }
        }
    }

    public void updateLocationUi(int openLocation) {
        if (mOpenLocationType == openLocation) {
            return;
        }
        mOpenLocationType = openLocation;
        if (mOpenLocationType > 2) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_START_LOC));
            mSmartRefreshLayout.setEnableRefresh(true);
            mSmartRefreshLayout.setVisibility(View.VISIBLE);
            mRvUser.setVisibility(View.VISIBLE);
            mLocationView.setVisibility(View.GONE);
            canLoadMore = true;
            loadData(true, false);
        } else {
//            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REQUEST_LOC));
            mLocationText.setText(mOpenLocationType < 2 ? "您未打开定位开关，打开定位授权定位权限才能查看附近的人哦" : "您未授权定位权限，授权定位权限才能查看附近的人哦");
            mSmartRefreshLayout.setEnableRefresh(false);
            mSmartRefreshLayout.setVisibility(View.GONE);
            mRvUser.setVisibility(View.GONE);
            mAdapter.setList(null);
            mLocationView.setVisibility(View.VISIBLE);
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_LATITUDE);
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_LONGITUDE);
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_CITY);
            MMKVUtils.getInstance().removeKey(service.getUserId() + Constants.LOCATION_PROVINCE);
        }
    }

    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            if (event.type == Constants.NOTIFY_REFRESH_RECOMMEND && mType == TYPE_RECOMMEND) {
                                page = 1;
                                loadData(true, true);
                            } else if (event.type == Constants.NOTIFY_PIC_ACCOST_SUCCESS) {
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<UserInfo> data = mAdapter.getData();
                                        for (int i = 0; i < data.size(); i++) {
                                            if (data.get(i).getUser_id() == event.familyId) {
//                                                UserInfo userInfo = data.get(i);
//                                                userInfo.setIs_accost(1);
//                                                mAdapter.setData(i, userInfo);
                                                mAdapter.getData().get(i).setIs_accost(1);
                                                break;
                                            }
                                        }
                                    }
                                });
                            } else if (event.type == Constants.NOTIFY_UPDATE_CONFIG_LAUNCH) {
                                p.getConfig(0, "");
                            } else if (event.type == Constants.NOTIFY_REFRESH_NEAR && mType == TYPE_NEAR) {
                                mSmartRefreshLayout.setEnableRefresh(true);
                                mSmartRefreshLayout.setVisibility(View.VISIBLE);
                                mRvUser.setVisibility(View.VISIBLE);
                                mLocationView.setVisibility(View.GONE);
                                canLoadMore = true;
                                p.getNearUser(1, false);
                            }
                        }
                ));
    }


    /**
     * 加载数据
     *
     * @param updateTop 是否更新更不家族数据
     */
    public void loadData(boolean isRefresh, boolean updateTop) {
        if (canLoadMore) {
            if (isRefresh)
                page = 1;
            else
                page += 1;
            canLoadMore = false;
            if (mType == TYPE_RECOMMEND) {  // 推荐用户
                if (p != null) {
                    p.getRecommendUser(page, updateTop);
                }
            } else {      //附近用户
                if (mOpenLocationType < 3) {
                    return;
                }
                p.getNearUser(page, updateTop);
            }
        }
    }


    @Override
    public HomePresenter initPresenter() {
        return new HomePresenter();
    }

    /**
     * 成功获取推荐人列表
     */
    @Override
    public void getRecommendSuccess(RecommendReq data, boolean uploadTop) {
        if (mPageManager != null)
            mPageManager.showContent();
        if (data != null && data.data != null) {
            mAdapter.removeAllFooterView();
        }
        canLoadMore = true;
        mSmartRefreshLayout.finishRefresh(true);
        mSmartRefreshLayout.finishLoadMore(true);
//        mIsEnableLoadMore = data != null && data.data != null && data.data.size() > 0;
//        mSmartRefreshLayout.setEnableLoadMore(data != null && data.data != null && data.data.size() > 0);
        if (null != data) {
            if (page == 1 && mType == TYPE_RECOMMEND) {
                CacheDiskUtils.getInstance().put(Constants.RECOMMEND_USER + service.getUserId(), new Gson().toJson(data));
            }
            if (!uploadTop)
                setData(page, data.data);
            if (null != data.share_config) {
                isShow = data.share_config.home_user_list_contact_style;
                mAdapter.setShow(data.share_config.home_user_list_contact_style);
            }
            //需要更新app
            if (null != data.update_info)
                RxBus.getDefault().post(new UpdateEvent(data.update_info));
            if (null != data.alert_accost)
                RxBus.getDefault().post(new UpdateEvent(data.alert_accost));
            if (null != data.share_config && null != data.share_config.home_top_nav && mRefreshCount <= 1) {
                mRefreshCount++;
                ConfigInfo configInfo = service.getConfigInfo();
                if (configInfo != null) {
                    configInfo.share_config = data.share_config;
                    service.setConfigInfo(new Gson().toJson(configInfo));
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_TOP_CONFIG));
                }
            }
            if (page == 1 && mType != TYPE_NEAR) {
                //刷新整个home视图滑动到最上面
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_TOP_SCROLLVIEW));
            }
        }
    }

    @Override
    public void getRecommendFail(String msg) {
        if (page != 1)
            page -= 1;
        canLoadMore = true;
        mSmartRefreshLayout.finishRefresh(false);
        mSmartRefreshLayout.finishLoadMore(false);
//        mAdapter.removeAllFooterView();
        if (mAdapter.getData().size() <= 0)
            mAdapter.setEmptyView(mNotDataView);
    }

    /**
     * 搭讪成功
     */
    @Override
    public void accostUserSuccess(int position, AccostDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS, data.gift.animation));
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PIC_ACCOST_SUCCESS, mAdapter.getData().get(position).getUser_id()));
            UserInfo item = mAdapter.getItem(position);
            if (item != null) {
                RxBus.getDefault().post(new AccostSuccessEvent(AccostSuccessEvent.ACCOUST_HOME, item.getUser_id() + "", item.getNickname(), item.getIcon()));
            }
            if (data != null && data.gift != null) {
                Utils.playAccostAnimationAndSound(data.gift.name, data.gift.animation);
            }
            //首页搭讪 2  个人资料页搭讪 3  动态搭讪 4  相册搭讪 5
            CommonUtil.sendAccostGirlBoy(service, mAdapter.getData().get(position).getUser_id(), data, 2);
            if (service.getUserInfo().isGirl()) {
                MobclickAgent.onEvent(mContext, "home_accost_girl");
            } else {
                MobclickAgent.onEvent(mContext, "home_accost_boy");
            }
        }
    }

    @Override
    public void getCheckMsgSuccess(String userId, MsgCheckDto data) {
        if (null == data || !CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
            CommonUtil.checkMsg(service.getConfigInfo(), userId, data);
        }
    }

    private void setData(int pageIndex, List<UserInfo> data) {
//        if (mFooterView == null) {
//            mFooterView = View.inflate(mContext, R.layout.footer_view, null);
//            ImageView view = mFooterView.findViewById(R.id.srl_classics_progress);
//            if (mProgressDrawable == null) {
//                mProgressDrawable = new ProgressDrawable();
//                mProgressDrawable.setColor(0xff666666);
//            }
//            view.setImageDrawable(mProgressDrawable);
//        }
        final int size = data == null ? 0 : data.size();
        if (pageIndex == 1) {
            mAdapter.setList(data);
            if (size == 0) {
                mAdapter.setEmptyView(mNotDataView);
            } else {
//                mProgressDrawable.start();
//                mAdapter.addFooterView(mFooterView);
            }
        } else {
            if (size > 0) {
                mAdapter.addData(data);
//                mProgressDrawable.start();
//                mAdapter.addFooterView(mFooterView);
            } else {
                page -= 1;
            }
        }
//        mRvUser.setItemViewCacheSize(mAdapter.getItemCount());
    }
}
