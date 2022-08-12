package com.tftechsz.im.adapter;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
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
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.impl.cache.StickTopCache;
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
import com.tftechsz.common.Constants;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.StringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.chat.ChatTimeUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.ContactInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public class MessageAdapter extends BaseQuickAdapter<ContactInfo, BaseViewHolder> {
    private RecentContactsCallback callback;
    private boolean isShow;
    public ChatApiService chatApiService;
    private final UserProviderService service;
    private final int mType;   //0:消息  1：密友  2:聊天房私信
    public int mIntimacyNum;
    private final CompositeDisposable mCompositeDisposable;

    public MessageAdapter(@Nullable List<ContactInfo> data, int type) {
        super(R.layout.item_message, data);
        mType = type;
        mCompositeDisposable = new CompositeDisposable();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        chatApiService = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null && service.getConfigInfo().sys.user != null)
            mIntimacyNum = service.getConfigInfo().sys.user.intimacy_friend_condition_num;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, ContactInfo item) {
        TextView tvIntimacy = helper.getView(R.id.tv_intimacy);  //亲密度
        TextView tvContent = helper.getView(R.id.tv_content);
        ImageView ivOffice = helper.getView(R.id.iv_office);
        CheckBox ivCheck = helper.getView(R.id.iv_check);
        TextView tvName = helper.getView(R.id.tv_name);
        ImageView ivRedPackage = helper.getView(R.id.iv_red_packet);
        ConstraintLayout root = helper.getView(R.id.root);
        AvatarVipFrameView ivAvatar = helper.getView(R.id.iv_avatar);
        ivAvatar.setOnline(mType == 1 && item.is_online);
        //是否在线
        helper.setVisible(R.id.iv_online, item.is_online);
        //文本颜色
        helper.setTextColor(R.id.tv_content, getContext().getResources().getColor(R.color.color_light_font));
        if (StickTopCache.isStickTop(item)) {  //y已经置顶
            root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_color));
        } else {
            root.setBackgroundResource(R.drawable.item_chat_selector);
        }
        helper.setVisible(R.id.tv_badge, false);  //消息
        ivOffice.setVisibility(View.GONE);
        if (null != item) {
            if (mType == 2) {   //消息
                ivRedPackage.setVisibility(View.GONE);
//                helper.setVisible(R.id.iv_red_packet, false);  // 红包
                tvIntimacy.setVisibility(View.GONE);
                if (item.intimacy_val >= mIntimacyNum) {
                    ivOffice.setImageResource(R.mipmap.chat_ic_fast_friend);
                    ivOffice.setVisibility(View.VISIBLE);
                } else {
                    ivOffice.setVisibility(View.GONE);
                }
            } else {  //密友
                ivRedPackage.setVisibility(View.GONE);
                if(item.intimacy_val>0) {
                    tvIntimacy.setVisibility(View.VISIBLE);
                    tvIntimacy.setText(StringUtils.formatNumbers(item.intimacy_val));
                }else{
                    tvIntimacy.setVisibility(View.GONE);
                }
            }
            if (TextUtils.equals(item.cmd_type, ChatMsg.ACCOST_TYPE)) {  //搭讪消息
                if (!TextUtils.equals(item.cmd, ChatMsg.REPLY_ACCOST_TYPE)) {
                    if (!TextUtils.equals(String.valueOf(service.getUserId()), item.getFromAccount())) {
                        ivRedPackage.setVisibility(View.VISIBLE);
                        tvContent.setTextColor(Utils.getColor(R.color.red));
                    }
                }
            } else if (TextUtils.equals(item.cmd_type, ChatMsg.TIP_TYPE)) {   //提示消息
                if (TextUtils.equals(ChatMsg.ACCOST_TYPE, item.from_type)) {
                    ivRedPackage.setVisibility(View.VISIBLE);
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setTextColor(getContext().getResources().getColor(R.color.red));
                } else if (TextUtils.equals(ChatMsg.ACCOST_LOCATION, item.from_type)) {
                    ivRedPackage.setVisibility(View.VISIBLE);
                    tvContent.setTextColor(Utils.getColor(R.color.red));
                }
            }
            if (TextUtils.equals("[提示消息]", item.getContent()) || TextUtils.equals("[提醒消息]", item.getContent())) {
                loadFromLocal(item, tvContent, ivRedPackage, QueryDirectionEnum.QUERY_OLD);
            } else {
                if (!TextUtils.isEmpty(item.accostDesc)) {
                    tvContent.setText(item.accostDesc);
                    tvContent.setTextColor(Utils.getColor(R.color.red));
                } else {
                    SpannableStringBuilder span = ChatMsgUtil.getTipContent(item.getContent(), "", content -> {
                    });
                    tvContent.setText(span);
                }
            }
            helper.setText(R.id.tv_badge, item.getUnreadCount() > 99 ? "99+" : String.valueOf(item.getUnreadCount()));  //未读数量
            helper.setText(R.id.tv_time, ChatTimeUtils.getChatTime(item.getTime()));
            helper.setVisible(R.id.tv_badge, item.getUnreadCount() != 0);  //消息
            ivAvatar.setBgFrame(item.picture_frame);
            ivAvatar.setAvatar(0);
            ivAvatar.setAvatar(R.drawable.bg_trans);
            tvContent.setTextColor(Utils.getColor(R.color.color_light_font));
            if (TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {  //客服小秘书
                ivOffice.setVisibility(View.VISIBLE);
                ivOffice.setImageResource(R.mipmap.ic_official);
                ivAvatar.setAvatar(R.drawable.chat_ic_customer_service_secretary);
                CommonUtil.setUserName(tvName, "客服小秘书", false);
                tvContent.setTextColor(Utils.getColor(R.color.c_im_content));
            } else {
                if (mType == 2) {
                    ivAvatar.setPartyAvatar(item.getContactId());
                } else {
                    ivAvatar.setAvatar(item.getContactId());
                }
                //获取不到年龄 22.6.17
//                NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(item.getContactId());
//                helper.setVisible(R.id.tv_age,true);
//                helper.setText(R.id.tv_age,"["+userInfo.getAvatar()+"]");
                CommonUtil.setUserName(tvName, UserInfoHelper.getUserTitleName(item.getContactId(), item.getSessionType()), false);
            }
            NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(item.getContactId());
            if (userInfo != null && userInfo.getExtension() != null && !TextUtils.equals("", userInfo.getExtension())) {
                CommonUtil.setVipInfo(userInfo, item.getContactId(), tvName, ivAvatar);
            }
        }
        if (TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {
            ivCheck.setVisibility(View.GONE);
        } else {
            ivCheck.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));
        if (!TextUtils.equals(Constants.CUSTOMER_SERVICE, item.getContactId())) {
            ivCheck.setChecked(item.isSelected());
        } else {
            ivCheck.setChecked(false);
        }
    }


    private void loadFromLocal(ContactInfo item, TextView textView, ImageView redPackage, QueryDirectionEnum direction) {
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(MessageBuilder.createEmptyMessage(item.getFromAccount(), SessionTypeEnum.P2P, item.getTime()), direction, 3, false)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                        if (messages != null) {
                            for (IMMessage message : messages) {
                                if (TextUtils.isEmpty(message.getContent()) || TextUtils.equals("[礼物]", message.getContent()) || TextUtils.equals("call", message.getContent())) {
                                    String content = CommonUtil.getMessage(getContext(), message, String.valueOf(service.getUserId()), message.getFromAccount(), textView, redPackage);
                                    if (!TextUtils.equals("[提示消息]", content)) {
                                        if (message.getMsgType() == MsgTypeEnum.audio) {
                                            content = setContent(textView, message, "[语音消息]");
                                        } else if (message.getMsgType() == MsgTypeEnum.image) {
                                            content = setContent(textView, message, "[图片]");
                                        } else if (message.getMsgType() == MsgTypeEnum.text) {
                                            content = setContent(textView, message, content);
                                        }
                                        textView.setText(content);
                                        item.content = content;
                                        break;
                                    } else {
                                        item.content = "";
                                        textView.setText(item.content);
                                    }
                                } else {
                                    String content = message.getContent();
                                    if (String.valueOf(service.getUserId()).equals(message.getFromAccount()) && content.equals("[对方发来搭讪红包]")) {
                                        content = "[发出搭讪红包]";
                                    }
                                    item.content = setContent(textView, message, content);
                                    textView.setText(setContent(textView, message, content));
                                    break;
                                }
                            }
                        }
                    }
                });

    }


    private String setContent(TextView textView, IMMessage message, String msg) {
        Map<String, Object> extension = message.getRemoteExtension();
        if (extension != null && extension.get("self_attach") != null) {
            ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage((String) extension.get("self_attach"));
            if (chatMsg != null && !TextUtils.isEmpty(chatMsg.boy_accost_desc)) {
                textView.setTextColor(Utils.getColor(R.color.red));
                return chatMsg.boy_accost_desc;
            }
        }
        return msg;
    }

    public boolean getShow() {
        return isShow;
    }

    /**
     * 是否显示删除按钮
     */
    public void setCheckShow(boolean show) {
        this.isShow = show;
        notifyDataSetChanged();
    }


    public RecentContactsCallback getCallback() {
        return callback;
    }

    public void setCallback(RecentContactsCallback callback) {
        this.callback = callback;
    }


}
