package com.tftechsz.party.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.util.CenteredImageSpan;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.SpannableStringUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.ChatMessageSpannableStr;
import com.tftechsz.common.widget.CircleUrlImageSpan;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.MultipleChatRoomMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 聊天室消息
 */
public class PartyRoomMsgAdapter extends BaseMultiItemQuickAdapter<MultipleChatRoomMessage, BaseViewHolder> {

    private boolean isAdminOrOnSeat;  //判断是不是房管和麦上用户
    private final UserProviderService service;
    //获取土豪等级
    private String mLevel;
    //获取自己的贵族等级
    private String mBadge;

    public PartyRoomMsgAdapter() {
        super();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        addItemType(0, R.layout.item_party_room_msg);
    }

    public String getLastLevel() {
        return mLevel;
    }

    public String getLastBadge() {
        return mBadge;
    }

    public void setIsAdminOrOnSeat(boolean isAdminOrOnSeat) {
        this.isAdminOrOnSeat = isAdminOrOnSeat;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MultipleChatRoomMessage mcr) {
        TextView tvContent = holder.getView(R.id.tv_msg_content);
        ImageView ivLevel = holder.getView(R.id.iv_level);
        ImageView ivBadgeLevel = holder.getView(R.id.iv_badge_level);  //贵族等级图标
        RelativeLayout llContent = holder.getView(R.id.ll_content);
        ImageView ivFailed = holder.getView(R.id.iv_failed);
        TextView tvSex = holder.getView(R.id.tv_sex);
        ChatRoomMessage message = mcr.getMessage();
        ivFailed.setVisibility(message.getStatus().equals(MsgStatusEnum.fail) ? View.VISIBLE : View.INVISIBLE);
        tvContent.setText(message.getContent());
        llContent.setBackgroundResource(R.drawable.bg_black_tran10_radius12);
        holder.setGone(R.id.tv_welcome, true);
        ivLevel.setVisibility(View.GONE);
        tvSex.setVisibility(View.GONE);
        ivBadgeLevel.setVisibility(View.GONE);
        tvContent.setPadding(0, 0, 0, 0);
        tvContent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        llContent.setPadding(DensityUtils.dp2px(BaseApplication.getInstance(), 5), DensityUtils.dp2px(BaseApplication.getInstance(), 4), DensityUtils.dp2px(BaseApplication.getInstance(), 8), DensityUtils.dp2px(BaseApplication.getInstance(), 8));
        //系统推送消息
        if (message.getMsgType() == MsgTypeEnum.custom) {
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
            if (chatMsg != null) {
                ChatMsg.PartyMsg msg = JSON.parseObject(chatMsg.content, ChatMsg.PartyMsg.class);
                if (msg != null) {
                    if (TextUtils.equals(ChatMsg.PARTY_JOIN_SYSTEM_NOTICE, chatMsg.cmd)) {   //系统消息推送
                        tvContent.setText(msg.msg);
                        tvContent.setTextColor(Color.parseColor("#93B4F5"));
                        llContent.setBackgroundResource(R.drawable.bg_black_tran10_radius12);
                    } else if (TextUtils.equals(ChatMsg.PARTY_WELCOME, chatMsg.cmd) || TextUtils.equals(ChatMsg.PARTY_SPECIAL_WELCOME, chatMsg.cmd)) {   //欢迎ta
                        tvContent.setTextColor(Color.parseColor("#93B4F5"));
                        //自己不能点击欢迎自己
                        if (TextUtils.equals("" + service.getUserId(), message.getFromAccount())) {
                            holder.setGone(R.id.tv_welcome, true);
                        } else {
                            if (message.isChecked() != null && message.isChecked())
                                holder.setGone(R.id.tv_welcome, true);
                            else {
                                holder.setGone(R.id.tv_welcome, !isAdminOrOnSeat);
                            }
                        }
                        setContent(message, llContent, ivBadgeLevel, tvSex, tvContent, holder.getLayoutPosition(), mcr.getItemType());
                    } else if (TextUtils.equals(ChatMsg.PARTY_TURNTABLE_TV, chatMsg.cmd)) {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                        stringBuilder.append(ChatMsg.PARTY_TURNTABLE_TV);
                        stringBuilder.append(" ");
                        CenteredImageSpan roomSpan = new CenteredImageSpan(getContext(), R.drawable.party_icon_turntable, DensityUtils.dp2px(BaseApplication.getInstance(), 14), DensityUtils.dp2px(BaseApplication.getInstance(), 14));
                        stringBuilder.setSpan(roomSpan, 0, ChatMsg.PARTY_TURNTABLE_TV.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvContent.setTextColor(Color.parseColor("#9B7414"));
                        llContent.setBackgroundResource(R.drawable.bg_orange_radius_message);
                        CharSequence content = new ChatMessageSpannableStr.Builder()
                                .append(stringBuilder)
                                .append(message.getContent())
                                .build().getMessageInfo();
                        tvContent.setPadding(DensityUtils.dp2px(BaseApplication.getInstance(), 10), 0, 0, 0);
                        tvContent.setText(content);
                    } else if (TextUtils.equals(ChatMsg.PARTY_BIG_EMOTICON, chatMsg.cmd)) {  //骰子消息
                        setContent(message, msg, llContent, ivBadgeLevel, tvSex, tvContent, holder.getLayoutPosition(), 2);
                    } else if (TextUtils.equals(ChatMsg.BLINK_BOX_GIFT, chatMsg.cmd_type)) {//盲盒通知
                    } else if (TextUtils.equals(ChatMsg.NOBILITY_LEVEL_UP, chatMsg.cmd)) {//贵族升级通知
                        ChatMsg.NobilityLevelUp levelUp = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
                        if (levelUp != null) {
                            tvContent.setTextColor(Color.parseColor("#93B4F5"));
                            ivLevel.setVisibility(View.VISIBLE);
                            GlideUtils.loadImage(getContext(), ivLevel, levelUp.badge);
                            SpannableStringBuilder spannableString = new SpannableStringBuilder(levelUp.msg);
                            int start = spannableString.toString().indexOf(levelUp.nickname);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), start, start + levelUp.nickname.length(),
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            if (levelUp.level == 1) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level1);
                            } else if (levelUp.level == 2) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level2);
                            } else if (levelUp.level == 3) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level3);
                            } else if (levelUp.level == 4) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level4);
                            } else if (levelUp.level == 5) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level5);
                            } else if (levelUp.level == 6) {
                                llContent.setBackgroundResource(R.drawable.bg_nobility_level6);
                            }
                            tvContent.setText(spannableString);
                        }
                    }
                }
            }
        } else if (message.getMsgType() == MsgTypeEnum.tip) { //tips消息 处理拓展字段
            Map<String, Object> remote = message.getRemoteExtension();
            String cmd_type = (String) remote.get(Interfaces.CMD_TYPE);
            String json = (String) remote.get(Interfaces.JSON_STRING);
            if (TextUtils.equals(cmd_type, ChatMsg.PARTY_GIFT_PLAY)) {  //派对接收礼物
                ChatMsg.PartyGiftMessage msg = JSON.parseObject(json, ChatMsg.PartyGiftMessage.class);
                if (msg == null) return;
                message.setContent("赠送给 " + msg.to + " [" + msg.gift + "] x" + msg.num);
                setContent(message, llContent, ivBadgeLevel, tvSex, tvContent, holder.getLayoutPosition(), 1);
            } else if (TextUtils.equals(cmd_type, ChatMsg.BLINK_BOX_GIFT)) { //盲盒通知
                ChatMsg.BlindBoxNotifyData blindBoxNotifyData = JSON.parseObject(json, ChatMsg.BlindBoxNotifyData.class);
                if (blindBoxNotifyData == null) return;
                String clickSpan = "我也去送>>";
                String str = blindBoxNotifyData.from_user.nickname + " 送给 " + blindBoxNotifyData.to_user.nickname + " " + blindBoxNotifyData.to_user.gift_num + "个" + blindBoxNotifyData.gift.gift_name + ",开出了" +
                        blindBoxNotifyData.get_gift.gift_name + "x" + blindBoxNotifyData.get_gift.gift_num + " 价值" + blindBoxNotifyData.get_gift.cost + "金币 " + clickSpan;
                SpannableStringBuilder spannableString = new SpannableStringBuilder(str);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (onSelectClickListener != null)
                            onSelectClickListener.onNameClick(blindBoxNotifyData.from_user.uid + "");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#FFD2EF"));
                        ds.setUnderlineText(false);
                    }
                }, 0, str.indexOf(blindBoxNotifyData.from_user.nickname) + blindBoxNotifyData.from_user.nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (onSelectClickListener != null)
                            onSelectClickListener.onNameClick(blindBoxNotifyData.to_user.uid + "");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#F0D3FF"));
                        ds.setUnderlineText(false);
                    }
                }, str.indexOf(blindBoxNotifyData.to_user.nickname), str.indexOf(blindBoxNotifyData.to_user.nickname) + blindBoxNotifyData.to_user.nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFE67D")), str.indexOf(blindBoxNotifyData.get_gift.gift_name), str.indexOf(blindBoxNotifyData.get_gift.gift_name) + (blindBoxNotifyData.get_gift.gift_name + "x" + blindBoxNotifyData.get_gift.gift_num + " 价值" + blindBoxNotifyData.get_gift.cost + "金币").length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                int start = spannableString.toString().indexOf(clickSpan);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (onSelectClickListener != null)
                            onSelectClickListener.giftClick();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor("#C3E8FF"));
                        ds.setUnderlineText(true);
                    }
                }, start, start + clickSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvContent.setPadding(DensityUtils.dp2px(BaseApplication.getInstance(), 10), 0, 0, 0);
                llContent.setBackgroundResource(R.drawable.party_notify_room_manghe);
                llContent.setGravity(Gravity.CENTER_VERTICAL);
                tvContent.setText(spannableString);

            }
        } else {
            setContent(message, llContent, ivBadgeLevel, tvSex, tvContent, holder.getLayoutPosition(), 0);
        }
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void setContent(ChatRoomMessage message, RelativeLayout llContent, ImageView ivBadgeLevel, TextView
            tvSex, TextView tvContent, int position, int itemType) {
        setContent(message, null, llContent, ivBadgeLevel, tvSex, tvContent, position, itemType);
    }

    /**
     * 设置内容
     *
     * @param tvSex     性别年龄
     * @param tvContent 内容
     * @param position  位置
     * @param itemType  1: 礼物 2：石头剪刀布  骰子
     */
    private void setContent(ChatRoomMessage message, ChatMsg.PartyMsg partyMsg, RelativeLayout
            llContent, ImageView ivBadgeLevel, TextView tvSex, TextView tvContent, int position, int itemType) {
        int bgId = 0;
        String chatBubble = "0";  //聊天气泡
        String roleId = "";
        String badge = "";  //贵族图标
        String name = "";
        int level = 0;
        String userLevel = "0";
        if (message.getRemoteExtension() != null) {
            if (message.getRemoteExtension().get("role_id") != null) {
                roleId = (String) message.getRemoteExtension().get("role_id");
            }
            if (message.getRemoteExtension().get("nickname") != null) {
                name = (String) message.getRemoteExtension().get("nickname");
            }
            if (message.getRemoteExtension().get("badge") != null) {
                badge = (String) message.getRemoteExtension().get("badge");
            }
            if (message.getRemoteExtension().get("rich_level") != null) {
                userLevel = (String) message.getRemoteExtension().get("rich_level");
            }
            if (message.getRemoteExtension().get("chat_bubble") != null) {
                chatBubble = (String) message.getRemoteExtension().get("chat_bubble");
            }
            if (TextUtils.equals(service.getUserId() + "", message.getFromAccount())) {
                mLevel = userLevel;
                mBadge = badge;
            }
            if (!TextUtils.isEmpty(userLevel))
                level = Integer.parseInt(userLevel);
            tvSex.setBackgroundResource(GlideUtils.getResId(GlideUtils.getUserLevel(level), R.drawable.class));
            tvSex.setText(userLevel);
            tvSex.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(badge)) {
                GlideUtils.loadImage(getContext(), ivBadgeLevel, badge);
                ivBadgeLevel.setVisibility(View.VISIBLE);
            }
        }
        //自己聊天气泡
        if (TextUtils.equals(service.getUserId() + "", message.getFromAccount())) {
            bgId = getChatBg(message.getFromAccount());
        } else {
            if (!TextUtils.isEmpty(chatBubble)) {
                bgId = Integer.parseInt(chatBubble);
            }
        }
        String nickNameColor = bgId >= 100 ? "#FE4D6B" : "#999999";
        if (message.getCallbackExtension() != null) {
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getCallbackExtension());
            try {
                if (null != chatMsg) {
                    ChatMsg.RoomExtInfo roomExtInfo = JSON.parseObject(chatMsg.content, ChatMsg.RoomExtInfo.class);
                    if (roomExtInfo != null) {
                        roleId = roomExtInfo.role_id;
                        level = roomExtInfo.rich_level;
                        name = roomExtInfo.nickname;
                        badge = roomExtInfo.badge;
                        tvSex.setBackgroundResource(GlideUtils.getResId(GlideUtils.getUserLevel(level), R.drawable.class));
                        tvSex.setText(level + "");
                        tvSex.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(service.getUserId() + "", message.getFromAccount())) {
                            if (level != 0) {
                                mLevel = level + "";
                            }
                            if (!TextUtils.isEmpty(badge))
                                mBadge = badge;
                        }
                        if (!TextUtils.isEmpty(badge)) {
                            GlideUtils.loadImage(getContext(), ivBadgeLevel, badge);
                            ivBadgeLevel.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(roleId)) {  //128：超管 64：房主 32：管理员 0：普通用户
            if (TextUtils.equals("128", roleId)) {  //超级管理员
                stringBuilder.append("128");
                CenteredImageSpan roomSpan = new CenteredImageSpan(getContext(), R.drawable.party_ic_super_admin, DensityUtils.dp2px(BaseApplication.getInstance(), 14), DensityUtils.dp2px(BaseApplication.getInstance(), 14));
                stringBuilder.setSpan(roomSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (TextUtils.equals("64", roleId)) {  //房主
                stringBuilder.append("64");
                CenteredImageSpan roomSpan = new CenteredImageSpan(getContext(), R.drawable.party_ic_house_owner, DensityUtils.dp2px(BaseApplication.getInstance(), 14), DensityUtils.dp2px(BaseApplication.getInstance(), 14));
                stringBuilder.setSpan(roomSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (TextUtils.equals("32", roleId)) {  //管理员
                stringBuilder.append("32");
                CenteredImageSpan roomSpan = new CenteredImageSpan(getContext(), R.drawable.party_ic_admin, DensityUtils.dp2px(BaseApplication.getInstance(), 14), DensityUtils.dp2px(BaseApplication.getInstance(), 14));
                stringBuilder.setSpan(roomSpan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (TextUtils.equals("0", roleId)) {  //普通用户
            }
        }

        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        if (itemType == 1) { //设置送礼文本
            try {
                String content = message.getContent();  // 赠送给 大意的项链 [http://public-cdn1.peony125.com/gift/private_msg/小心心.png] x1
                String url = content.substring(content.indexOf("[http") + 1, content.lastIndexOf("]"));
                spannableString.append(content.substring(0, content.indexOf("[")));
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FE4D6B")), 0, content.substring(0, content.indexOf("[")).length(),
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                Map<String, Object> remote = message.getRemoteExtension();
                if (remote != null) {
                    String json = (String) remote.get(Interfaces.JSON_STRING);
                    if (!TextUtils.isEmpty(json)) {
                        ChatMsg.PartyGiftMessage partyGiftMessage = JSON.parseObject(json, ChatMsg.PartyGiftMessage.class);
                        if (partyGiftMessage != null) {
                            int start = spannableString.toString().indexOf(partyGiftMessage.to);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(nickNameColor)), start, start + partyGiftMessage.to.length(),
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            spannableString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View widget) {
                                    if (onSelectClickListener != null)
                                        onSelectClickListener.onNameClick(partyGiftMessage.toId);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setColor(Color.parseColor(nickNameColor));
                                    ds.setUnderlineText(false);
                                }
                            }, start, start + partyGiftMessage.to.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
                spannableString.append("[icon]");  // 赠送给 大意的项链 [icon]
                int headerStart = spannableString.toString().indexOf("[icon]");
                CircleUrlImageSpan headerSpan = new CircleUrlImageSpan(getContext(), url, tvContent, ConvertUtils.dp2px(22), ConvertUtils.dp2px(22));
                spannableString.setSpan(headerSpan, headerStart, headerStart + 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.append(new SpannableStringUtils.Builder()
                        .append(content.substring(content.lastIndexOf("]") + 1))
                        .setForegroundColor(Utils.getColor(R.color.colorPrimary)).create()); //  赠送给 大意的项链 [icon] x1
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (itemType == 2) {  //游戏
            String content = message.getContent();
            String url = partyMsg.image;
            spannableString.append(content);
            spannableString.append("[icon]");
            int headerStart = spannableString.toString().indexOf("[icon]");
            CircleUrlImageSpan headerSpan = new CircleUrlImageSpan(getContext(), url, tvContent, ConvertUtils.dp2px(22), ConvertUtils.dp2px(22));
            spannableString.setSpan(headerSpan, headerStart, headerStart + 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.append(MoonUtil.makeSpannableStringTags(getContext(), message.getContent(), tvContent, ImageSpan.ALIGN_BOTTOM));
        }
        if (TextUtils.equals(service.getUserId() + "", message.getFromAccount()) && itemType == 2) {
            name = "我";
        }
        //管理员超级管理员，房主加空格
        String roleName = ((!TextUtils.isEmpty(roleId) && !TextUtils.equals("0", roleId)) ? " " : "") + name + " ";
        CharSequence content = new ChatMessageSpannableStr.Builder()
                .append(stringBuilder)
                .append(roleName, Color.parseColor(nickNameColor), () -> {  //姓名
                    if (onSelectClickListener != null)
                        onSelectClickListener.onNameClick(getData().get(position).getMessage().getFromAccount());
                })
                .append(spannableString)
                .build().getMessageInfo();
        tvContent.setText(content);
        if (bgId >= 100) {
            VipUtils.setPersonalise(llContent, Integer.parseInt(Objects.requireNonNull(chatBubble)), false, false, true);
            tvContent.setTextColor(Color.parseColor("#FFFFFF"));
        } else {

        }

    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

    }


    /**
     * 获取聊天框背景
     */
    public int getChatBg(String fromAccount) {
        int bg = 0;
        NimUserInfo nimUserInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(fromAccount);
        if (nimUserInfo != null && nimUserInfo.getExtension() != null) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(nimUserInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    bg = vip.chat_bubble;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bg;
    }


    public interface OnSelectClickListener {
        void onNameClick(String id);

        void giftClick();
    }

    private OnSelectClickListener onSelectClickListener;

    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
    }
}
