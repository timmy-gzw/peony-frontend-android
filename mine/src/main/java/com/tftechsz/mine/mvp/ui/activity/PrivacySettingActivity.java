package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.UserInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.RecommendChangeEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.OpenVipWindow;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.PrivacyDto;
import com.tftechsz.mine.mvp.IView.IPrivacySettingView;
import com.tftechsz.mine.mvp.presenter.PrivacySettingPresenter;

/**
 * 隐私设置
 */
@Route(path = ARouterApi.ACTIVITY_PRIVACY_SETTING)
public class PrivacySettingActivity extends BaseMvpActivity<IPrivacySettingView, PrivacySettingPresenter> implements View.OnClickListener, IPrivacySettingView {

    private SwitchCompat mSwRich, mSwGift, mSwRank, mSwStories, mSwLocation, mSwRecommend;
    @Autowired
    UserProviderService service;
    private PrivacyDto mData;

    @Override
    public PrivacySettingPresenter initPresenter() {
        return new PrivacySettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("隐私设置")
                .build();

        UserInfo userInfo = service.getUserInfo();
        int visible = userInfo.isVip() || userInfo.isGirl() ? View.GONE : View.VISIBLE;
        findViewById(R.id.tv_vip_rich).setVisibility(visible);
        findViewById(R.id.tv_vip_gift).setVisibility(visible);
        findViewById(R.id.tv_vip_rank).setVisibility(visible);
//        findViewById(R.id.tv_vip_stories).setVisibility(visible);
        mSwRecommend = findViewById(R.id.sw_personalized_recommendation);
        boolean checked = MMKVUtils.getInstance().decodeBoolean(Constants.PARAMS_PERSONALIZED_RECOMMENDATION, true);
        mSwRecommend.setChecked(checked);

        initListener();
    }

    private void initListener() {
        findViewById(R.id.cl_rich).setOnClickListener(this);
        findViewById(R.id.cl_gift).setOnClickListener(this);
        findViewById(R.id.cl_rank).setOnClickListener(this);
        findViewById(R.id.cl_stories).setOnClickListener(this);
        findViewById(R.id.cl_location).setOnClickListener(this);
        findViewById(R.id.cl_personalized_recommendation).setOnClickListener(this);
        mSwRich = findViewById(R.id.sw_rich);
        mSwGift = findViewById(R.id.sw_gift);
        mSwRank = findViewById(R.id.sw_rank);
        mSwStories = findViewById(R.id.sw_stories);
        mSwLocation = findViewById(R.id.sw_location);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_privacy_setting;
    }


    @Override
    protected void initData() {
        super.initData();
        getP().getPrivilege();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cl_personalized_recommendation) {
            boolean checked = mSwRecommend.isChecked();
            if (checked) {
                showRecommendClosePop();
            } else {
                mSwRecommend.setChecked(true);
                MMKVUtils.getInstance().encode(Constants.PARAMS_PERSONALIZED_RECOMMENDATION, true);
                RxBus.getDefault().post(new RecommendChangeEvent(true));
                toastTip("更改成功");
            }
        } else if (id == R.id.cl_location) {   //位置信息
            if (mData != null && mData.open_hidden_location == 1) {
                final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                    if (agreeToRequest) {
                        mCompositeDisposable.add(new RxPermissions(PrivacySettingActivity.this)
                                .request(permissions)
                                .subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        getP().setPrivilege(5, mSwLocation.isChecked() ? 0 : 1);
                                    } else {
                                        getP().showPop();
                                    }
                                }));
                    } else {
                        getP().showPop();
                    }
                });
            } else {
                getP().setPrivilege(5, mSwLocation.isChecked() ? 0 : 1);
            }
        } else if (id == R.id.cl_rich) {   //土豪魅力
            if (vipLimit()) return;
            getP().setPrivilege(1, mSwRich.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_gift) {   //礼物
            if (vipLimit()) return;
            getP().setPrivilege(2, mSwGift.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_rank) {   //排行榜
            if (vipLimit()) return;
            getP().setPrivilege(4, mSwRank.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_stories) {   //上电视上头条
//            if (vipLimit()) return;
            getP().setPrivilege(3, mSwStories.isChecked() ? 0 : 1);
        }
    }

    private boolean vipLimit() {
        if (!service.getUserInfo().isVip() && service.getUserInfo().getSex() == 1) {
            new OpenVipWindow(this).addOnClickListener(() -> ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_VIP)).showPopupWindow();
            return true;
        }
        return false;
    }

    private void showRecommendClosePop() {
        CustomPopWindow popWindow = new CustomPopWindow(this)
                .setTitle("温馨提示")
                .setContent("关闭后，无法使用推荐功能，\n确定要关闭吗")
                .addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onSure() {
                        mSwRecommend.setChecked(false);
                        MMKVUtils.getInstance().encode(Constants.PARAMS_PERSONALIZED_RECOMMENDATION, false);
                        RxBus.getDefault().post(new RecommendChangeEvent(false));
                        toastTip("更改成功");
                    }
                });
        popWindow.showPopupWindow();
    }

    @Override
    public void getPrivilegeSuccess(PrivacyDto data) {
        if (data != null) {
            mData = data;
            mSwGift.setChecked(data.open_hidden_gift == 1);
            mSwRich.setChecked(data.open_hidden_rank == 1);
            mSwStories.setChecked(data.open_hidden_headlines == 1);
            mSwRank.setChecked(data.open_hidden_intimacy_rank == 1);
            mSwLocation.setChecked(data.open_hidden_location == 1);
        }
    }

    @Override
    public void setPrivilegeSuccess(int type, int value, Boolean data) {
        if (type == 1 && data) {
            mSwRich.setChecked(value == 1);
        }
        if (type == 2 && data) {
            mSwGift.setChecked(value == 1);
        }
        if (type == 3 && data) {
            mSwStories.setChecked(value == 1);
        }
        if (type == 4 && data) {
            mSwRank.setChecked(value == 1);
        }
        if (type == 5 && data) {
            mSwLocation.setChecked(value == 1);
            if (mData != null) {
                mData.open_hidden_location = value == 1 ? 1 : 0;
            }
        }
    }

    @Override
    public void setImgCover(String data) {

    }

}
