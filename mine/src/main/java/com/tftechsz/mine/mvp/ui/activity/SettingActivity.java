package com.tftechsz.mine.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.AppUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.SignNumBean;
import com.tftechsz.mine.mvp.IView.ISettingView;
import com.tftechsz.mine.mvp.presenter.SettingPresenter;

import androidx.appcompat.app.AlertDialog;

@Route(path = ARouterApi.ACTIVITY_SETTING)
public class SettingActivity extends BaseMvpActivity<ISettingView, SettingPresenter> implements View.OnClickListener, ISettingView {

    private CommonItemView mItemNotice;
    private CommonItemView mItemDebug;
    private CommonItemView mItemRechargeSetting;
    private CommonItemView mItemPrivacySetting;


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
        mMItemChatsignnum.setOnClickListener(this);    //聊天卡
        findViewById(R.id.item_account_binding).setOnClickListener(this);  //找号绑定
        findViewById(R.id.item_face_setting).setOnClickListener(this);  //美颜设置
        findViewById(R.id.item_black_list).setOnClickListener(this);    //黑名单设置
        CommonItemView itemAbout = findViewById(R.id.item_about);
        itemAbout.setOnClickListener(this);   //关于我们
        findViewById(R.id.item_cancellation).setOnClickListener(this);  //注销账号
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

            if (service.getConfigInfo().share_config.is_open_chat_card == 1) {
                mMItemChatsignnum.setVisibility(View.VISIBLE);
            } else {
                mMItemChatsignnum.setVisibility(View.GONE);
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
        if (id != R.id.item_about && id != R.id.tv_exit && id != R.id.item_debug && id != R.id.item_cancellation && CommonUtil.hasPerformAccost(service.getUserInfo()))
            return;
        if (id == R.id.tv_exit) {  //退出登录
            p.loginOutPop(this);
        } else if (id == R.id.item_charge_setting) {   //收费设置
            startActivity(ChargeSettingActivity.class);
        } else if (id == R.id.item_account_binding) {   //账号绑定
            startActivity(AccountBindingActivity.class);
        } else if (id == R.id.item_privacy_setting) {  //隐私设置
            startActivity(PrivacySettingActivity.class);
        } else if (id == R.id.item_black_list) {   // 黑名单
            startActivity(BlackListActivity.class);
        } else if (id == R.id.item_chatsignnum) {   // 聊天卡
            startActivity(SignChatNumActivity.class);
        } else if (id == R.id.item_cancellation) {  //注销账号
            startActivity(CancellationActivity.class);
        } else if (id == R.id.item_about) {   //关于我们
            startActivity(AboutUsActivity.class);
        } else if (id == R.id.item_notification) {  //通知
            startActivity(NotifySettingActivity.class);
        } else if (id == R.id.item_face_setting) {   //美颜设置
            mCompositeDisposable.add(new RxPermissions(this)
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            startActivity(FaceUnitySettingActivity.class);
                        } else {
                            PermissionUtil.showPermissionPop(SettingActivity.this);
                        }
                    }));
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
                        if (TextUtils.equals(hostUrl[selectedItem], Constants.HOST)) {
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
