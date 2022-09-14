package com.tftechsz.home.mvp.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.RechargeBeforePop;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.home.R;
import com.tftechsz.home.api.HomeApiService;
import com.tftechsz.home.mvp.ui.activity.RadarActivity;
import com.tftechsz.home.widget.HomeTopItemLayout;

public class SpeedMatchFragment extends BaseMvpFragment implements View.OnClickListener {
    private UserProviderService service;
    private HomeTopItemLayout mHomeItem1, mHomeItem2, mHomeItem3, mHomeItem4;
    private ConfigInfo.HomeTopNav mHome_top_nav;

    private RechargePopWindow rechargePopWindow;
    private RechargeBeforePop beforePop;

    public static SpeedMatchFragment newInstance() {
        SpeedMatchFragment recommend = new SpeedMatchFragment();
        return recommend;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        service = ARouter.getInstance().navigation(UserProviderService.class);
        mHomeItem1 = getView(R.id.home_top_item1);
        mHomeItem2 = getView(R.id.home_top_item2);
        mHomeItem3 = getView(R.id.home_top_item3);
        mHomeItem4 = getView(R.id.home_top_item4);
        mHomeItem1.setOnClickListener(this);
        mHomeItem2.setOnClickListener(this);
        mHomeItem3.setOnClickListener(this);
        mHomeItem4.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_speed_match;
    }

    @Override
    protected void initData() {
        initTop();
    }

    private void initTop() {
        ConfigInfo configInfo = service.getConfigInfo();
        if (configInfo == null || configInfo.share_config == null || configInfo.share_config.home_tab_config_matching == null) {
            return;
        }
        mHome_top_nav = configInfo.share_config.home_tab_config_matching;
        LogUtil.e("====", mHomeItem1 + "====" + configInfo.share_config.home_tab_config_matching + "==" + mHome_top_nav.nav_1);

        setTopData(mHomeItem1, mHome_top_nav.nav_1);
        setTopData(mHomeItem2, mHome_top_nav.nav_2);
        setTopData(mHomeItem3, mHome_top_nav.nav_3);
        setTopData(mHomeItem4, mHome_top_nav.nav_4);
    }

    private void setTopData(HomeTopItemLayout homeTopItemLayout, @Nullable ConfigInfo.Nav nav) {
        if (nav != null && isAdded()) {
            homeTopItemLayout.setData(nav, 2, false);
            homeTopItemLayout.setVisibility(View.VISIBLE);
        } else {
            homeTopItemLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        if (id == R.id.home_top_item1) {
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
        final String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        PermissionUtil.beforeCheckPermission(getActivity(), permissions, agreeToRequest -> {
            if (agreeToRequest) {
                mCompositeDisposable.add(new RxPermissions(this)
                        .request(permissions)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                getUserInfo(type);
                            } else {
                                PermissionUtil.showPermissionPop(getActivity());
                            }
                        }));
            }
        });
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setHomeItem(isVisibleToUser, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        setHomeItem(true, false);
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
        setHomeItem(false, true);
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
