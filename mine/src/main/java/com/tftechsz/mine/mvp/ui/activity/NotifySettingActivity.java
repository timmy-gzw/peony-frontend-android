package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.mixpush.MixPushService;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.UserPreferences;
import com.tftechsz.common.widget.CommonItemView;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.PrivacyDto;
import com.tftechsz.mine.mvp.IView.IPrivacySettingView;
import com.tftechsz.mine.mvp.presenter.PrivacySettingPresenter;

public class NotifySettingActivity extends BaseMvpActivity<IPrivacySettingView,PrivacySettingPresenter> implements View.OnClickListener,IPrivacySettingView {

    private CommonItemView mItemNotice;
    private CommonItemView mItemRing;
    private CommonItemView mItemVibrate;
    private SwitchCompat mSwAccost;

    @Autowired
    UserProviderService service;

    @Override
    public PrivacySettingPresenter initPresenter() {
        return new PrivacySettingPresenter();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        new ToolBarBuilder().showBack(true)
                .setTitle("新消息通知")
                .build();
        mItemNotice = findViewById(R.id.item_notification);
        initListener();
    }

    private void initListener() {
        mItemRing = findViewById(R.id.item_ring);
        mItemRing.setOnClickListener(this);  //声音
        mItemVibrate = findViewById(R.id.item_vibrate);
        mItemVibrate.setOnClickListener(this);
        mItemNotice.setOnClickListener(this);  //通知
        mSwAccost = findViewById(R.id.accost_sw_ycgrzy);
        findViewById(R.id.accost_ycgrzydjzzs).setOnClickListener(this);
        mItemNotice.getMySwitch().setChecked(UserPreferences.getNotificationToggle());
        mItemVibrate.getMySwitch().setChecked(UserPreferences.getVibrateToggle());
        mItemRing.getMySwitch().setChecked(UserPreferences.getRingToggle());
        TextView tvTip = findViewById(R.id.tv_tip);
        tvTip.setText(String.format("当%s运行时,您可以设置是否需要声音或者振动提醒",getString(R.string.app_name)));
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_notify_setting;
    }


    @Override
    protected void initData() {
        super.initData();
        getP().getPrivilege();
    }

    private void setMessageNotify(final boolean checkState) {
        // 如果接入第三方推送（小米），则同样应该设置开、关推送提醒
        // 如果关闭消息提醒，则第三方推送消息提醒也应该关闭。
        // 如果打开消息提醒，则同时打开第三方推送消息提醒。
        NIMClient.getService(MixPushService.class).enable(checkState).setCallback(
                new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        setToggleNotification(checkState);
                        mItemNotice.getMySwitch().setChecked(checkState);
                        toastTip("设置成功");
                    }

                    @Override
                    public void onFailed(int code) {
                        mItemNotice.getMySwitch().setChecked(!checkState);
                        if (code == ResponseCode.RES_UNSUPPORT) {
                            mItemNotice.getMySwitch().setChecked(checkState);
                            setToggleNotification(checkState);
                        } else if (code == ResponseCode.RES_EFREQUENTLY) {
                            toastTip("操作太频繁");
                        } else {
                            toastTip("设置失败，请重试");

                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                    }
                });


    }

    private void setToggleNotification(boolean checkState) {
        try {
            setNotificationToggle(checkState);
            NIMClient.toggleNotification(checkState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNotificationToggle(boolean on) {
        UserPreferences.setNotificationToggle(on);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.item_ring) {  //声音
            boolean isCheck = !UserPreferences.getRingToggle();
            mItemRing.getMySwitch().setChecked(isCheck);
            UserPreferences.setRingToggle(isCheck);
//            StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
//            config.ring = isCheck;
//            UserPreferences.setStatusConfig(config);
//            NIMClient.updateStatusBarNotificationConfig(config);
        } else if (id == R.id.item_vibrate) {   //振动
            boolean isCheck = !UserPreferences.getVibrateToggle();
            mItemVibrate.getMySwitch().setChecked(isCheck);
            UserPreferences.setVibrateToggle(isCheck);
//            StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
//            config.vibrate = isCheck;
//            UserPreferences.setStatusConfig(config);
//            NIMClient.updateStatusBarNotificationConfig(config);
        }else if (id == R.id.item_notification) {  //通知
            setMessageNotify(!UserPreferences.getNotificationToggle());
        } else if (id == R.id.accost_ycgrzydjzzs) {
            getP().setPrivilege(7, mSwAccost.isChecked() ? 1 : 0);
        }
    }

    @Override
    public void getPrivilegeSuccess(PrivacyDto data) {
        if (data != null && mSwAccost != null) {
            mSwAccost.setChecked(data.open_hidden_accost == 0);

        }
    }

    @Override
    public void setPrivilegeSuccess(int type, int value, Boolean data) {
        if (type == 7 && data && mSwAccost != null) {
            mSwAccost.setChecked(value == 0);
            RxBus.getDefault().post(new CommonEvent(value,Constants.NOTIFY_USER_ACCOST));
        }
    }


    @Override
    public void setImgCover(String data) {

    }
}
