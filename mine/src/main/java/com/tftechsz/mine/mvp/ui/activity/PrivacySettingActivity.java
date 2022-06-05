package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
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

    private SwitchCompat mSwRich, mSwGift, mSwRank, mSwStories, mSwLocation, mswycgrzy;
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
        initListener();

    }

    private void initListener() {
        findViewById(R.id.cl_rich).setOnClickListener(this);
        findViewById(R.id.cl_gift).setOnClickListener(this);
        findViewById(R.id.cl_rank).setOnClickListener(this);
        findViewById(R.id.cl_stories).setOnClickListener(this);
        findViewById(R.id.cl_location).setOnClickListener(this);
        findViewById(R.id.cl_ycgrzydjzzs).setOnClickListener(this);
        mSwRich = findViewById(R.id.sw_rich);
        mSwGift = findViewById(R.id.sw_gift);
        mSwRank = findViewById(R.id.sw_rank);
        mSwStories = findViewById(R.id.sw_stories);
        mSwLocation = findViewById(R.id.sw_location);
        mswycgrzy = findViewById(R.id.sw_ycgrzy);

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
        if (!service.getUserInfo().isVip() && service.getUserInfo().getSex() == 1) {
            new OpenVipWindow(this).addOnClickListener(() -> {
                startActivity(VipActivity.class);
            }).showPopupWindow();
            return;
        }
        if (id == R.id.cl_rich) {   //土豪魅力
            getP().setPrivilege(1, mSwRich.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_gift) {   //礼物
            getP().setPrivilege(2, mSwGift.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_rank) {   //排行榜
            getP().setPrivilege(4, mSwRank.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_stories) {   //上电视上头条
            getP().setPrivilege(3, mSwStories.isChecked() ? 0 : 1);
        } else if (id == R.id.cl_location) {   //位置信息
            if (mData != null && mData.open_hidden_location == 1) {
                mCompositeDisposable.add(new RxPermissions(PrivacySettingActivity.this)
                        .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                getP().setPrivilege(5, mSwLocation.isChecked() ? 0 : 1);
                            } else {
                                getP().showPop();
                            }
                        }));
            } else {
                getP().setPrivilege(5, mSwLocation.isChecked() ? 0 : 1);
            }
        } else if (id == R.id.cl_ycgrzydjzzs) {
            getP().setPrivilege(6, mswycgrzy.isChecked() ? 0 : 1);
        }
        LogUtil.e("=================", mData + "");
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
            mswycgrzy.setChecked(data.open_show_family == 1);
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
        if (type == 6 && data) {
            mswycgrzy.setChecked(value == 1);
        }
    }

    @Override
    public void setImgCover(String data) {

    }

}
