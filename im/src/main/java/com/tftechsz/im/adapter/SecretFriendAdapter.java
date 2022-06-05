package com.tftechsz.im.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.business.recent.RecentContactsCallback;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.impl.cache.StickTopCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.widget.chat.ChatTimeUtils;

import java.util.List;

public class SecretFriendAdapter extends BaseQuickAdapter<ContactInfo, BaseViewHolder> {
    private RecentContactsCallback callback;
    private boolean isShow;
    public ChatApiService chatApiService;
    private final UserProviderService service;
    public int mIntimacyNum;

    public SecretFriendAdapter(@Nullable List<ContactInfo> data) {
        super(R.layout.item_secret_message, data);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.user != null)
            mIntimacyNum = service.getConfigInfo().sys.user.intimacy_friend_condition_num;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, ContactInfo item) {
        TextView tvIntimacy = helper.getView(R.id.tv_intimacy);  //亲密度

        ImageView ivOffice = helper.getView(R.id.iv_office);
        TextView tvName = helper.getView(R.id.tv_name);

        ConstraintLayout root = helper.getView(R.id.root);
        AvatarVipFrameView ivAvatar = helper.getView(R.id.iv_avatar);

        if (StickTopCache.isStickTop(item)) {  //y已经置顶
            root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_color));
        } else {
            root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        ivOffice.setVisibility(View.GONE);
        if (null != item) {

            tvIntimacy.setVisibility(View.VISIBLE);
            tvIntimacy.setText(StringUtils.formatNumbers(item.intimacy_val));
          /*  if (item.userId != 0) {
                tvContent.setText("芍药号:" + item.userId);
            }*/


            helper.setText(R.id.tv_time, item.getTime() == 0 ? "" : ChatTimeUtils.getChatTime(item.getTime()));
            ivAvatar.setBgFrame(item.picture_frame);
            ivAvatar.setAvatar(0);
       /*     if (TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {  //客服小秘书
                ivOffice.setVisibility(View.VISIBLE);
                ivOffice.setImageResource(R.mipmap.ic_official);
                ivAvatar.setAvatar(R.drawable.chat_ic_customer_service_secretary);
                CommonUtil.setUserName(tvName, "客服小秘书", false);
            } else {*/
            ivAvatar.setAvatar(item.getContactId());
            CommonUtil.setUserName(tvName, UserInfoHelper.getUserTitleName(item.getContactId(), item.getSessionType()), false);
//            }
            NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(item.getContactId());
            if (userInfo != null && userInfo.getExtension() != null) {
                CommonUtil.setVipInfo(userInfo, item.getContactId(), tvName, ivAvatar);
            }
        }
      /*  if (TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {
            ivCheck.setVisibility(View.GONE);
        } else {
            ivCheck.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }*/
//        ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
        /*if (!TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {
            ivCheck.setChecked(item.isSelected());
        } else {
            ivCheck.setChecked(false);
        }*/
    }


    public boolean getShow() {
        return isShow;
    }


    public RecentContactsCallback getCallback() {
        return callback;
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }


}
