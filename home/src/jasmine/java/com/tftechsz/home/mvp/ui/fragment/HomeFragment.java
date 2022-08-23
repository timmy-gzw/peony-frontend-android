package com.tftechsz.home.mvp.ui.fragment;

import static android.content.Context.LOCATION_SERVICE;
import static com.tftechsz.common.Constants.NOTIFY_TOP_SCROLLVIEW;
import static com.umeng.socialize.utils.ContextUtil.getPackageName;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.flyco.tablayout.SlidingScaleTabLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.MD5Util;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.MessageEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.DepthPageTransformer;
import com.tftechsz.common.widget.X5WebView;
import com.tftechsz.common.widget.pop.GoStoriesPopWindow;
import com.tftechsz.common.widget.pop.RechargeBeforePop;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.home.R;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.mvp.ui.activity.RadarActivity;
import com.tftechsz.home.mvp.ui.activity.SearchActivity;
import com.tftechsz.home.widget.HomeTopItemLayout;
import com.tftechsz.home.widget.ScrollerForbidView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterApi.FRAGMENT_HOME)
public class HomeFragment extends BaseMvpFragment implements View.OnClickListener {
    private ScrollerForbidView mSvMessage;
    private HomeTopItemLayout mHomeItem1, mHomeItem2, mHomeItem3, mHomeItem4;
    private RechargePopWindow rechargePopWindow;
    private X5WebView webView;
    @Autowired
    UserProviderService service;

    private ConfigInfo.HomeTopNav mHome_top_nav;
    private ImageView mIvSearch;
    private LinearLayout mLl_home_top_item;
    private boolean isFirst = false;  //第一次进入页面不执行调用js方法
    private RechargeBeforePop beforePop;
    public RecommendUserFragment recommendUserFragment;
    private AppBarLayout coordinatorLayout;
    private ArrayList<RecommendUserFragment> fragments;
    private ViewPager mViewPager;

    private LocationManager mLocationManager;
    private int mOpenLocationType;

    private ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateLocationUi();
        }
    };
    private SlidingScaleTabLayout mTabLayout;

    public void updateLocationUi() {
        boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        int locationType = enabled ? 1 : 0;
        if (enabled) {
            boolean permission = (PackageManager.PERMISSION_GRANTED == getActivity().getPackageManager()
                    .checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()));
            if (permission) {
                locationType = 3;
            } else {
                locationType = 2;
            }
        }
        if (mOpenLocationType == locationType) {
            if (locationType == 2)
//                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REQUEST_LOC));
                return;
        }

        mOpenLocationType = locationType;
        if (fragments != null && fragments.size() > 0) {
            Utils.runOnUiThread(() -> {
                for (RecommendUserFragment fragment : fragments) {
                    if (fragment.mType == RecommendUserFragment.TYPE_NEAR) {
                        fragment.updateLocationUi(mOpenLocationType);
                        break;
                    }
                }
            });
        }
    }

    public void scrollTop() {
        if (coordinatorLayout != null) {
            CoordinatorLayout.Behavior behavior =
                    ((CoordinatorLayout.LayoutParams) coordinatorLayout.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.Behavior) {
                AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
                if (topAndBottomOffset != 0) {
                    appBarLayoutBehavior.setTopAndBottomOffset(0);
                }
            }

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initUI(Bundle savedInstanceState) {
        if (isAdded() && getActivity() != null) {
            mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            mOpenLocationType = enabled ? 1 : 0;
            if (enabled) {
                boolean permission = (PackageManager.PERMISSION_GRANTED == getActivity().getPackageManager()
                        .checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()));
                if (permission) {
                    mOpenLocationType = 3;
                } else {
                    mOpenLocationType = 2;
                }
            }
            getActivity().getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED), false, mGpsMonitor);
        }
        ImmersionBar.with(this).titleBarMarginTop(R.id.appbarlayout).init();
        coordinatorLayout = getView(R.id.appbarlayout);
        coordinatorLayout.addOnOffsetChangedListener((AppBarLayout.BaseOnOffsetChangedListener) (appBarLayout, verticalOffset) -> {
            if (verticalOffset < -150) {
                if (mTabLayout != null) mTabLayout.setBackgroundResource(R.color.white);
                //fixme 个性化推荐审核开关使用
                if (mTabLayout != null && mTabLayout.getVisibility() == View.GONE) mViewPager.setBackgroundResource(R.color.white);
            } else {
                if (mTabLayout != null) mTabLayout.setBackgroundResource(R.drawable.bg_white_top_radius20);
                if (mTabLayout != null && mTabLayout.getVisibility() == View.GONE) mViewPager.setBackgroundResource(R.drawable.bg_white_top_radius20);
            }
        });
        mLl_home_top_item = getView(R.id.ll_home_top_item);
        webView = getView(R.id.webView);
        webView.setBackgroundColor(0);
        webView.getSettings().setJavaScriptEnabled(true);// 支持js
        webView.addJavascriptInterface(new AndroidtoJs(), "android");
        mIvSearch = getView(R.id.iv_search);
        ImageView mIvRank = getView(R.id.iv_rank);
        mHomeItem1 = getView(R.id.home_top_item1);
        mHomeItem2 = getView(R.id.home_top_item2);
        mHomeItem3 = getView(R.id.home_top_item3);
        mHomeItem4 = getView(R.id.home_top_item4);
        mSvMessage = getView(R.id.sv_message);
        mTabLayout = getView(R.id.tabLayout);
        mViewPager = getView(R.id.vp);
        initListener();
        fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        //加载排行榜图片
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.loading_h5 != null) {
            showHot();
            if (service.getConfigInfo().sys.loading_h5.lovelist != null) {
                mIvRank.setVisibility(View.VISIBLE);
                GlideUtils.loadImage(mContext, mIvRank, service.getConfigInfo().sys.loading_h5.lovelist.icon);
                mIvRank.setOnClickListener(v -> {
                    if (StringUtils.isTrimEmpty(service.getConfigInfo().sys.loading_h5.lovelist.zip_source)) {
                        return;
                    }
                    File file = new File(DownloadHelper.FILE_PATH + File.separator + MD5Util.toMD516(service.getConfigInfo().sys.loading_h5.lovelist.zip_source) + ".zip");
                    if (file.exists()) {
                        String url = DownloadHelper.FILE_PATH + File.separator + "lovelist.html";
                        if (!new File(url).exists()) {
                            url = DownloadHelper.FILE_PATH + File.separator + "lovelist" + "/lovelist.html";
                        }
                        BaseWebViewActivity.start(mContext, "", "file://" + url, false, "black", 0, 5);
                    } else {
                        DownloadHelper.downloadZip(service.getConfigInfo().sys.loading_h5.lovelist.zip_source, new DownloadHelper.DownloadListener() {
                            @Override
                            public void completed() {
                                try {
                                    ZipUtils.unzipFile(file.getPath(), DownloadHelper.FILE_PATH);
                                    String url = DownloadHelper.FILE_PATH + File.separator + "lovelist.html";
                                    if (!new File(url).exists()) {
                                        url = DownloadHelper.FILE_PATH + File.separator + "lovelist" + "/lovelist.html";
                                    }
                                    BaseWebViewActivity.start(mContext, "", "file://" + url, false, "black", 0, 5);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failed() {
                                toastTip("下载失败,请稍后重试");
                            }

                            @Override
                            public void onProgress(int progress) {
                            }
                        });
                    }
                });
            }
        }
        if (service.getConfigInfo() == null || service.getConfigInfo().share_config == null
                || service.getConfigInfo().share_config.home_tab_config == null) {
            return;
        }

        List<ConfigInfo.HomeTabNav> homeTabLists = service.getConfigInfo().share_config.home_tab_config;
        //是否显示附近列表
        boolean showNearUser = MMKVUtils.getInstance().decodeBoolean(Constants.SHOW_NEAR_USER);

        for (ConfigInfo.HomeTabNav homeTabNav : homeTabLists) {
            recommendUserFragment = RecommendUserFragment.newInstance(homeTabNav.type);
            if (recommendUserFragment != null) {
                fragments.add(recommendUserFragment);
                titles.add(homeTabNav.title);
            }
        }

        if (showNearUser) {
            fragments.add(RecommendUserFragment.newInstance(Interfaces.MAIN_TAB_NEAR, mOpenLocationType));
            titles.add("附近");

        }
        mTabLayout.setVisibility(titles.size() > 1 ? View.VISIBLE : View.GONE);

        mViewPager.setAdapter(new FragmentVpAdapter(getChildFragmentManager(), fragments, titles));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(titles.size());
        mViewPager.setCurrentItem(0);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        if (service.getConfigInfo().share_config != null &&
                (/*AppUtils.isAppDebug() ||*/ service.getConfigInfo().share_config.is_show_search == 1)) {
            mIvSearch.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        mIvSearch.setOnClickListener(this);
        mHomeItem1.setOnClickListener(this);
        mHomeItem2.setOnClickListener(this);
        mHomeItem3.setOnClickListener(this);
        mHomeItem4.setOnClickListener(this);
    }

    public void autoRefresh() {
        if (fragments != null && fragments.get(mViewPager.getCurrentItem()) != null && fragments.get(mViewPager.getCurrentItem()).mSmartRefreshLayout != null) {
            fragments.get(mViewPager.getCurrentItem()).mSmartRefreshLayout.autoRefresh();
            fragments.get(mViewPager.getCurrentItem()).mLayoutManager.smoothScrollToPosition(fragments.get(mViewPager.getCurrentItem()).mRvUser, new RecyclerView.State(), 0);
        }
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initData() {
        initTop();
        initBus();
    }


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_TOP_CONFIG) {
                                Utils.runOnUiThread(this::initTop);
                            } else if (event.type == Constants.NOTIFY_DOWN_HOT_SUCCESS) {
                                if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.loading_h5 != null) {
                                    showHot();
                                }
                            } else if (NOTIFY_TOP_SCROLLVIEW == event.type) {
                                scrollTop();
                            } else if (Constants.NOTIFY_NOTICE_H5 == event.type) {
                                if (webView != null)
                                    webView.loadUrl("javascript:setNotify('" + event.code + "')");
                            }
                        }
                ));

        mCompositeDisposable.add(RxBus.getDefault().toObservable(MessageEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(event -> {
                            Utils.runOnUiThread(() -> {
                                if (event != null && event.messageInfoList != null && event.messageInfoList.size() > 0) {
                                    mSvMessage.setVisibility(View.VISIBLE);
                                    mSvMessage.loadMessage(event.messageInfoList);
                                } else {
                                    mSvMessage.setVisibility(View.GONE);
                                    mSvMessage.stopLoop();
                                }
                            });
                        }
                ));
    }


    private void showHot() {
        Utils.runOnUiThread(() -> {
            ConfigInfo.DownH5Resource onHot = service.getConfigInfo().sys.loading_h5.onhot;

            if (!StringUtils.isTrimEmpty(onHot.url)) {
                webView.loadUrl(onHot.url);
                webView.setVisibility(View.VISIBLE);
            } else if (!StringUtils.isTrimEmpty(onHot.zip_source)) {
                final File file = new File(DownloadHelper.FILE_PATH + File.separator + MD5Util.toMD516(service.getConfigInfo().sys.loading_h5.onhot.zip_source) + ".zip");
                if (file.exists())
                    webView.loadUrl("file://" + DownloadHelper.FILE_PATH + "/onhot.html");
                webView.setVisibility(View.VISIBLE);
            } else {
                webView.setVisibility(View.GONE);
                return;
            }
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setHomeItem(true, false);
            if (webView != null)
                webView.loadUrl("javascript:openRequest()");
            if (mSvMessage != null)
                mSvMessage.startLoop();
        } else {
            setHomeItem(false, false);
            if (webView != null)
                webView.loadUrl("javascript:closeRequest()");
            if (mSvMessage != null)
                mSvMessage.stopLoop();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFirst && webView != null)
            webView.loadUrl("javascript:openRequest()");
        if (mSvMessage != null)
            mSvMessage.startLoop();
        isFirst = true;
        setHomeItem(true, false);
        updateLocationUi();
    }

    private void setHomeItem(boolean isShown, boolean isDestroy) {
        setCarouselState(isShown, mHomeItem1, isDestroy);
        setCarouselState(isShown, mHomeItem2, isDestroy);
        setCarouselState(isShown, mHomeItem3, isDestroy);
        setCarouselState(isShown, mHomeItem4, isDestroy);
    }

    private void setCarouselState(boolean isShow, HomeTopItemLayout homeItem, boolean shouldDestroy) {
        if (homeItem != null && homeItem.mCarrousel != null && homeItem.mCarrousel.handler != null) {
            if (shouldDestroy) {
                homeItem.mCarrousel.destroyAutoRotation();
                return;
            }
            if (isShow) {
                homeItem.mCarrousel.resumeAutoRotation();
            } else {
                homeItem.mCarrousel.stopAutoRotation();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGpsMonitor != null) {
            getActivity().getContentResolver().unregisterContentObserver(mGpsMonitor);
            mGpsMonitor = null;
        }
        setHomeItem(false, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null)
            webView.loadUrl("javascript:closeRequest()");
        if (mSvMessage != null)
            mSvMessage.stopLoop();
        setHomeItem(false, false);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mSvMessage != null)
            mSvMessage.stopLoop();
    }

    /**
     * 初始化homeTop
     */
    int topSize;

    private void initTop() {
        mLl_home_top_item.setVisibility(View.GONE);
        topSize = 0;
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo == null || configInfo.sys == null || configInfo.sys.is_verified == 0 || configInfo.share_config == null || configInfo.share_config.home_top_nav == null) {
            return;
        }
        mHome_top_nav = configInfo.share_config.home_top_nav;
        mLl_home_top_item.setVisibility(View.VISIBLE);

        if (mHome_top_nav.nav_1 != null) {
            topSize += 1;
        }
        if (mHome_top_nav.nav_2 != null) {
            topSize += 1;
        }
        if (mHome_top_nav.nav_3 != null) {
            topSize += 1;
        }
        if (mHome_top_nav.nav_4 != null) {
            topSize += 1;
        }
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mLl_home_top_item.getLayoutParams();
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mHomeItem2.getLayoutParams();
        if (topSize > 2) {
            //lp.height = -1;
            mLl_home_top_item.setPadding(0, 0, 0, 0);
            lp2.setMarginStart(ConvertUtils.dp2px(7));
            lp.height = (int) ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(40)) / 3);
        } else {
            //lp.height = ConvertUtils.dp2px(95);
            mLl_home_top_item.setPadding(0, ConvertUtils.dp2px(10), 0, ConvertUtils.dp2px(10));
            lp.height = (int) ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(40)) / 3.8f);
            lp2.setMarginStart(ConvertUtils.dp2px(15));
        }
        mLl_home_top_item.setLayoutParams(lp);
        mHomeItem2.setLayoutParams(lp2);
        mHomeItem3.setLayoutParams(lp2);
        mHomeItem4.setLayoutParams(lp2);

        setTopData(mHomeItem1, mHome_top_nav.nav_1);
        setTopData(mHomeItem2, mHome_top_nav.nav_2);
        setTopData(mHomeItem3, mHome_top_nav.nav_3);
        setTopData(mHomeItem4, mHome_top_nav.nav_4);
    }

    private void setTopData(HomeTopItemLayout homeTopItemLayout, @Nullable ConfigInfo.Nav nav) {
        if (nav != null && isAdded()) {
            homeTopItemLayout.setData(nav, topSize);
            homeTopItemLayout.setVisibility(View.VISIBLE);
        } else {
            homeTopItemLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        if (id == R.id.iv_search) {   // 搜索
            startActivity(new Intent(getActivity(), SearchActivity.class));
        } else if (id == R.id.home_top_item1) {
            performHomeTop(mHome_top_nav.nav_1);
        } else if (id == R.id.home_top_item2) {
            performHomeTop(mHome_top_nav.nav_2);
        } else if (id == R.id.home_top_item3) {
            performHomeTop(mHome_top_nav.nav_3);
        } else if (id == R.id.home_top_item4) {
            performHomeTop(mHome_top_nav.nav_4);
        }
    }

    private void performHomeTop(ConfigInfo.Nav nav) {
        if (nav != null && nav.link != null) {
            if (nav.link.startsWith(Interfaces.LINK_PEONY_TOAST)) {
                CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(nav.link), 0, 0);
            } else if (nav.link.startsWith(Interfaces.LINK_PEONY)) {
                String link = nav.link.substring(Interfaces.LINK_PEONY.length());
                switch (link) {
                    case Interfaces.LINK_PEONY_JOIN_VIDEO_MATCH: //视频速配
                        if (null != service.getUserInfo() && service.getUserInfo().getSex() == 2) {   //女生
                            initPermissions(2);
                        } else {
                            checkIsVer(50, 2);
                        }
                        break;

                    case Interfaces.LINK_PEONY_JOIN_VOICE_MATCH: //语音速配
                        if (null != service.getUserInfo() && service.getUserInfo().getSex() == 2) {  //女生
                            initPermissions(1);
                        } else {
                            checkIsVer(30, 1);
                        }
                        break;

                    default:
                        CommonUtil.performLink(mActivity, new ConfigInfo.MineInfo(nav.link), 0, 0);
                        break;
                }
            }
        }
    }


    /**
     * 是否是审核包
     *
     * @param money 语音30 视频 50
     * @param type  语音 1 视频 2
     */
    private void checkIsVer(int money, int type) {
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo != null && configInfo.sys.is_verified == 0) {   //审核中状态
            initPermissions(type);
        } else {
            showPop(money, type);
        }
    }


    /**
     * 当金币不足进行弹窗
     *
     * @param money 语音匹配  8   视频匹配 30
     */
    private void showPop(double money, int type) {
        if (service.getUserInfo() == null) return;
        String coin = service.getUserInfo().getCoin();
        double curCoin = TextUtils.isEmpty(coin) ? 0 : Double.parseDouble(coin);
        if (curCoin < money) {
            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_limit_from_channel == 1) {
                if (beforePop == null)
                    beforePop = new RechargeBeforePop(mContext);
                beforePop.addOnClickListener(() -> showRechargePop(3, 3));
                beforePop.showPopupWindow();
            } else {
                showRechargePop(3, 3);
            }
            return;
        }
        initPermissions(type);
    }


    /**
     * 显示充值列表
     */
    private void showRechargePop(int from_type, int scene) {
        if (rechargePopWindow == null)
            rechargePopWindow = new RechargePopWindow(getActivity(), from_type, scene);
        rechargePopWindow.getCoin();
        rechargePopWindow.requestData();
        rechargePopWindow.showPopupWindow();
    }

    /**
     * 申请权限
     */
    private void initPermissions(int type) {
        mCompositeDisposable.add(new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        getUserInfo(type);
                    } else {
                        PermissionUtil.showPermissionPop(getActivity());
                    }
                }));
    }


    public void getUserInfo(int type) {
        HomeApiService homeApiService = RetrofitManager.getInstance().createUserApi(HomeApiService.class);
        mCompositeDisposable.add(homeApiService.getUserInfo().compose(RxUtil.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null) {
                            if (!TextUtils.isEmpty(response.getData().is_ban_msg)) {
                                toastTip(response.getData().is_ban_msg);
                            } else {
                                RadarActivity.startActivity(getActivity(), type);
                            }
                        }
                    }
                }));
    }

    public class AndroidtoJs {
        /**
         * token
         */
        @JavascriptInterface
        public String getNativeToken() {
            return service.getToken();
        }

        @JavascriptInterface
        public String getApiUa() {
            if (service == null || service.getConfigInfo() == null || service.getConfigInfo().sys == null || service.getConfigInfo().sys.is_verified == 0)
                return "";
            return com.tftechsz.common.utils.AppUtils.getApiUa();
        }
        /**
         * 上头条
         */
        @JavascriptInterface
        public void onHot() {
            Utils.runOnUiThread(() -> {
                GoStoriesPopWindow popWindow = new GoStoriesPopWindow(mContext);
                popWindow.showPopupWindow();
            });

        }

        /**
         * 跳转webview
         */
        @JavascriptInterface
        public void openWebView(String url, int is_topbar, String topbar_color) {
            Utils.runOnUiThread(() -> {
                BaseWebViewActivity.start(mContext, "", url, is_topbar != 0, topbar_color, 0, 5);
            });
        }

        /**
         * 跳转资料页面
         */
        @JavascriptInterface
        public void toPersonInfo(int user_id) {
            if (service != null && service.getUserId() == user_id) {
                ARouterUtils.toMineDetailActivity("");
            } else {
                ARouterUtils.toMineDetailActivity(String.valueOf(user_id));
            }
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    public static class FragmentVpAdapter extends FragmentPagerAdapter {

        private final List<RecommendUserFragment> fragmentList;
        private final List<String> titles;


        public FragmentVpAdapter(FragmentManager fm, List<RecommendUserFragment> l) {
            this(fm, l, null);
        }

        public FragmentVpAdapter(FragmentManager fm, List<RecommendUserFragment> l, List<String> titls) {
            super(fm);
            fragmentList = l;
            titles = titls;
        }

        @NotNull
        @Override
        public RecommendUserFragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (null != titles && titles.size() > 0) {
                return titles.get(position);
            } else
                return super.getPageTitle(position);
        }
    }

}

