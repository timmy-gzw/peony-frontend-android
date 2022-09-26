package com.tftechsz.home.adapter;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.home.R;
import com.tftechsz.common.entity.MessageInfo;
import com.tftechsz.common.iservice.UserProviderService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeMessageAdapter extends BaseQuickAdapter<MessageInfo, BaseViewHolder> {
    private final UserProviderService service;


    public HomeMessageAdapter() {
        super(R.layout.item_home_message);
        service = ARouter.getInstance().navigation(UserProviderService.class);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MessageInfo item) {
        TextView tvName = baseViewHolder.getView(R.id.tv_name);
        TextView tvContent = baseViewHolder.getView(R.id.tv_content);
        HeadImageView ivAvatar = baseViewHolder.getView(R.id.iv_avatar);
        if (TextUtils.equals("[提示消息]", item.getContent()) || TextUtils.equals("[提醒消息]", item.getContent())) {
            loadFromLocal(item, tvContent, QueryDirectionEnum.QUERY_OLD);
        } else {
            SpannableStringBuilder span = ChatMsgUtil.getTipContent(item.getContent(), "", content -> {
            });
            tvContent.setText(span);
        }
        tvName.setText(UserInfoHelper.getUserTitleName(item.getContactId(), SessionTypeEnum.P2P));
        if (TextUtils.equals(UserInfoHelper.getUserTitleName(item.getContactId(), SessionTypeEnum.P2P), item.getContactId())) {
            List<String> list = new ArrayList<>();
            list.add(item.getFromAccount());
            NIMClient.getService(UserService.class).fetchUserInfo(list).setCallback(new RequestCallbackWrapper<List<NimUserInfo>>() {
                @Override
                public void onResult(int code, List<NimUserInfo> result, Throwable exception) {
                    if (result != null && result.size() > 0) {
                        tvName.setText(result.get(0).getName());
                    }
                }
            });
        }
        ivAvatar.loadBuddyAvatar(item.getFromAccount());
    }

    private void loadFromLocal(MessageInfo item, TextView textView, QueryDirectionEnum direction) {
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(MessageBuilder.createEmptyMessage(item.getFromAccount(), SessionTypeEnum.P2P, item.getTime()), direction, 4, false)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                        if (messages != null) {
                            for (IMMessage message : messages) {
                                if (TextUtils.isEmpty(message.getContent()) || TextUtils.equals("[礼物]", message.getContent()) || TextUtils.equals("call", message.getContent())) {
                                    String content = CommonUtil.getMessage(getContext(), message, String.valueOf(service.getUserId()), message.getFromAccount(), textView, null);
                                    if (!TextUtils.equals("[提示消息]", content)) {
                                        if (message.getMsgType() == MsgTypeEnum.audio) {
                                            content = "[语音消息]";
                                        } else if (message.getMsgType() == MsgTypeEnum.image) {
                                            content = "[图片]";
                                        }
                                        textView.setText(content);
                                        item.content = content;
                                        break;
                                    } else {
                                        item.content = "";
                                        textView.setText(item.content);
                                    }
                                } else {
                                    item.content = message.getContent();
                                    textView.setText(message.getContent());
                                    break;
                                }
                            }
                        }
                    }
                });

    }
}
