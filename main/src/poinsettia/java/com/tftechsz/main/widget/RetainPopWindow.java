package com.tftechsz.main.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.entity.MessageInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 挽留弹窗
 */
public class RetainPopWindow extends BaseCenterPop implements View.OnClickListener {

    private MessageInfo messageInfo;
    private final TextView tvTip;
    private final TextView tvName;
    private final AvatarImageView ivPeople;
    private final UserProviderService service;
    private final TextView tvNum;

    public RetainPopWindow(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        tvTip = findViewById(R.id.tv_tip);
        tvName = findViewById(R.id.tv_name);
        ivPeople = findViewById(R.id.iv_people);
        tvNum = findViewById(R.id.tv_message_num);
        findViewById(R.id.tv_quit).setOnClickListener(this);
        findViewById(R.id.tv_stay).setOnClickListener(this);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_retain);
    }


    public void initData(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
        tvName.setTextColor(ContextCompat.getColor(getContext(), R.color.color_normal));
        tvName.setText(UserInfoHelper.getUserTitleName(messageInfo.getContactId(), SessionTypeEnum.P2P));
        NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(messageInfo.getContactId());
        CommonUtil.setNameColor(userInfo, messageInfo.getContactId(), tvName, false);
        if (TextUtils.equals(UserInfoHelper.getUserTitleName(messageInfo.getContactId(), SessionTypeEnum.P2P), messageInfo.getContactId())) {
            List<String> list = new ArrayList<>();
            list.add(messageInfo.getFromAccount());
            NIMClient.getService(UserService.class).fetchUserInfo(list).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {
                @Override
                public void onResult(int code, List<NimUserInfo> result, Throwable exception) {
                    if (result != null && result.size() > 0) {
                        NimUserInfo nimUserInfo = result.get(0);
                        tvName.setText(nimUserInfo.getName());
                        CommonUtil.setNameColor(nimUserInfo, nimUserInfo.getAccount(), tvName, false);
                    }
                }
            });
        }
        ivPeople.loadBuddyAvatar(String.valueOf(messageInfo.getContactId()), 21);
        if (service.getUserInfo() != null && service.getUserInfo().isBoy()) {
            tvTip.setText("小姐姐发来新消息，确定不去看看嘛？");
        } else {
            tvTip.setText("小哥哥发来新消息，确定不去看看嘛？");
        }
        tvNum.setText(String.valueOf(messageInfo.getUnreadCount()));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_quit) {
            dismiss();
            AppManager.getAppManager().finishAllActivity();
        } else if (id == R.id.tv_stay) {
            dismiss();
            if (messageInfo != null) {
                ARouterUtils.toChatP2PActivity(messageInfo.getContactId(), NimUIKit.getCommonP2PSessionCustomization(), null);
            }
        }
    }
}
