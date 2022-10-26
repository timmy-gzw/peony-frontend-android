package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.ApiConstants;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.SignNumBean;
import com.tftechsz.mine.mvp.IView.ISettingView;
import com.tftechsz.mine.mvp.presenter.SettingPresenter;

import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterApi.ACTIVITY_SETTING)
public class SettingActivity extends BaseMvpActivity<ISettingView, SettingPresenter> implements View.OnClickListener, ISettingView {

    private CommonItemView mItemNotice;
    private CommonItemView mItemDebug;
    private CommonItemView mItemRechargeSetting;
    private CommonItemView mItemPrivacySetting;
    private CommonItemView mItemCallSetting;
    private CommonItemView mItemFaceSetting;
    private CommonItemView mItemYouthModel;


    private int checkedItem, selectedItem;
    private AlertDialog mAlertDialog;

    @Autowired
    UserProviderService service;
    private CommonItemView mMItemChatsignnum;

    @Override
    public SettingPresenter initPresenter() {
        return new SettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("设置")
                .build();
        mItemNotice = findViewById(R.id.item_notification);
        initListener();
    }

    private void initListener() {
        mItemRechargeSetting = findViewById(R.id.item_charge_setting);
        mItemRechargeSetting.setOnClickListener(this);  //收费设置
        mItemPrivacySetting = findViewById(R.id.item_privacy_setting);
        mItemPrivacySetting.setOnClickListener(this);
        mMItemChatsignnum = findViewById(R.id.item_chatsignnum);
        mItemCallSetting = findViewById(R.id.item_call_setting);
        mItemCallSetting.setOnClickListener(this);  //招呼设置
        mMItemChatsignnum.setOnClickListener(this);    //聊天卡
        findViewById(R.id.item_account_binding).setOnClickListener(this);  //找号绑定
        mItemFaceSetting = findViewById(R.id.item_face_setting);
        mItemFaceSetting.setOnClickListener(this);  //美颜设置
        findViewById(R.id.item_black_list).setOnClickListener(this);    //黑名单设置

        mItemYouthModel = findViewById(R.id.item_youth_model);
        mItemYouthModel.setOnClickListener(this);

        CommonItemView itemAbout = findViewById(R.id.item_about);
        itemAbout.setOnClickListener(this);   //关于我们
        findViewById(R.id.tv_exit).setOnClickListener(this);   //退出登录
        mItemDebug = findViewById(R.id.item_debug);
        mItemNotice.setOnClickListener(this);  //通知
        itemAbout.setLeftText("关于" + getString(R.string.app_name));
        if (AppUtils.isAppDebug()) {
            CharSequence[] hostName = getResources().getTextArray(R.array.host_name);
            CharSequence[] hostUrl = getResources().getTextArray(R.array.host_url);
            String host = SPUtils.getString(Constants.CURRENT_HOST);
            if (TextUtils.isEmpty(host)) {
                host = (String) hostUrl[2];
            }
            for (int i = 0; i < hostUrl.length; i++) {
                if (host.contentEquals(hostUrl[i])) {
                    checkedItem = i;
                    selectedItem = i;
                    mItemDebug.setRightText(hostName[i]);

                }
            }

            mItemDebug.setVisibility(View.VISIBLE);
            mItemDebug.setOnClickListener(this);
        } else {
            mItemDebug.setVisibility(View.GONE);
        }
        //设置男用户是否开始收费设置
        if (service.getUserInfo() != null && service.getConfigInfo().share_config != null) {
            if (service.getUserInfo().isBoy()) {
                if (service.getConfigInfo().share_config.is_open_boy_charge == 1) {
                    mItemRechargeSetting.setVisibility(View.VISIBLE);
                } else {
                    mItemRechargeSetting.setVisibility(View.GONE);
                }
            }

            List<ConfigInfo.MineInfo> list = CommonUtil.addMineInfo(service.getConfigInfo().share_config.my);
            List<String> links = new ArrayList<>();
            for (ConfigInfo.MineInfo mineInfo : list) {
                links.add(mineInfo.link);
            }

            if (service.getUserInfo().isGirl() && links.contains(Interfaces.LINK_PEONY_ACCOST_SETTING)) {
                mItemCallSetting.setVisibility(View.VISIBLE);
            }

            if (links.contains(Interfaces.LINK_PEONY_FACIAL)) {
                mItemFaceSetting.setVisibility(View.VISIBLE);
            }

            if (service.getConfigInfo().share_config.is_open_chat_card == 1) {
                mMItemChatsignnum.setVisibility(View.VISIBLE);
            } else {
                mMItemChatsignnum.setVisibility(View.GONE);
            }

            if (service.getConfigInfo().share_config.is_open_youth_mode == 1) {
                mItemYouthModel.setVisibility(View.VISIBLE);
            } else {
                mItemYouthModel.setVisibility(View.GONE);
            }

        }
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }


    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.item_account_binding && id != R.id.item_youth_model && id != R.id.item_about && id != R.id.tv_exit && id != R.id.item_debug && CommonUtil.hasPerformAccost(service.getUserInfo()))
            return;
        if (id == R.id.tv_exit) {  //退出登录
            p.loginOutPop(this);
        } else if (id == R.id.item_charge_setting) {   //收费设置
            ARouterUtils.toChargeSettingActivity();
        } else if (id == R.id.item_account_binding) {   //账号绑定
            ARouterUtils.toAccountManagerActivity();
        } else if (id == R.id.item_call_setting) {   //招呼设置
            ARouterUtils.toAccostSettingActivity(null);
        } else if (id == R.id.item_privacy_setting) {  //隐私设置
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_PRIVACY_SETTING);
        } else if (id == R.id.item_black_list) {   // 黑名单
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_BLACK_LIST);
        } else if (id == R.id.item_chatsignnum) {   // 聊天卡
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_SIGN_CHAT_NUM);
        } else if (id == R.id.item_about) {   //关于我们
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ABOUT_US);
        } else if (id == R.id.item_notification) {  //通知
            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_NOTIFY_SETTING);
        } else if (id == R.id.item_youth_model) {
            ARouterUtils.toYouthModelActivity();
        } else if (id == R.id.item_face_setting) {   //美颜设置
            final String[] permissions = {Manifest.permission.CAMERA};
            PermissionUtil.beforeCheckPermission(this, permissions, agreeToRequest -> {
                if (agreeToRequest) {
                    mCompositeDisposable.add(new RxPermissions(this)
                            .request(permissions)
                            .subscribe(aBoolean -> {
                                if (aBoolean) {
                                    startActivity(FaceUnitySettingActivity.class);
                                } else {
                                    PermissionUtil.showPermissionPop(SettingActivity.this);
                                }
                            }));
                } else {
                    PermissionUtil.showPermissionPop(SettingActivity.this);
                }
            });
        } else if (id == R.id.item_debug) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("选择环境")
                    .setSingleChoiceItems(R.array.host_name, checkedItem, (dialog, which) -> selectedItem = which).setNegativeButton("确定", (dialog, i) -> {
                        mAlertDialog.dismiss();
                        if (checkedItem == selectedItem) {
                            return;
                        }
                        CharSequence[] hostName = getResources().getTextArray(R.array.host_name);
                        CharSequence[] hostUrl = getResources().getTextArray(R.array.host_url);
                        SPUtils.put(Constants.CURRENT_HOST, hostUrl[selectedItem]);
                        if (TextUtils.equals(hostUrl[selectedItem], ApiConstants.HOST)) {
                            SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
                            SPUtils.remove("token");
                        }
                        Utils.toast("正在修改至--> " + hostName[selectedItem]);
                        new Handler(getMainLooper()).postDelayed(() -> {
                            //SPUtils.put(BaseApplication.getInstance(), Constants.IS_COMPLETE_INFO,0);
//                            NIMClient.getService(AuthService.class).logout();
//                            AppManager.getAppManager().finishAllActivity();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }, 1000);
                    }).create();
            mAlertDialog.show();
        }
    }


    @Override
    public void exitSuccess(boolean data) {

    }

    @Override
    public void num(SignNumBean bean) {

    }
}
