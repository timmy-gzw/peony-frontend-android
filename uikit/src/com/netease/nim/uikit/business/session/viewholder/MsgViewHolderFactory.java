package com.netease.nim.uikit.business.session.viewholder;

import android.text.TextUtils;

import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 消息项展示ViewHolder工厂类。
 */
public class MsgViewHolderFactory {

    private static HashMap<Class<? extends MsgAttachment>, Class<? extends MsgViewHolderBase>> viewHolders = new HashMap<>();

    private static Class<? extends MsgViewHolderBase> tipMsgViewHolder;

    static {
        // built in
        register(ImageAttachment.class, MsgViewHolderPicture.class);
        register(AudioAttachment.class, MsgViewHolderAudio.class);
        register(VideoAttachment.class, MsgViewHolderVideo.class);
        register(LocationAttachment.class, MsgViewHolderLocation.class);
        register(NotificationAttachment.class, MsgViewHolderNotification.class);
        register(RobotAttachment.class, MsgViewHolderRobot.class);
    }

    public static void register(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        viewHolders.put(attach, viewHolder);
    }

    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        tipMsgViewHolder = viewHolder;
    }

    /**
     * 消息holder
     *
     * @param message
     * @return
     */
    public static Class<? extends MsgViewHolderBase> getViewHolderByType(IMMessage message) {
        if (message.getMsgType() == MsgTypeEnum.text) {
            return MsgViewHolderText.class;
        } else if (message.getMsgType() == MsgTypeEnum.tip) {
            return tipMsgViewHolder == null ? MsgViewHolderBlack.class : tipMsgViewHolder;
        } else {
            try {
                if (TextUtils.equals(ChatMsg.TIP_TYPE, message.getContent())) {
                    return MsgViewHolderTip.class;
                } else if (TextUtils.equals("[视频通话]", message.getContent()) || TextUtils.equals("[语音通话]", message.getContent())) {
                    return MsgViewHolderAVChat.class;
                } else if (TextUtils.equals("[对方发来搭讪红包]", message.getContent())) {
                    return MsgViewHolderAccost.class;
                }
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                if (chatMsg != null && TextUtils.equals(ChatMsg.TIP_TYPE, chatMsg.cmd_type)) {
                    return MsgViewHolderTip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_TIPS, chatMsg.cmd_type)) {   //家族tip
                    return MsgViewHolderFamilyTip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_COUPLE_TIPS, chatMsg.cmd_type)) {   //家族情侣tip
                    return MsgViewHolderFamilyCoupleTip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.VOICE_ROOM_SEAT, chatMsg.cmd_type)) {   //家族tip
                    return MsgViewHolderFamilySeat.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_NOTICE, chatMsg.cmd_type)) {   //家族公告
                    return MsgViewHolderFamilyAnnouncement.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_SAY_HELLO, chatMsg.cmd_type)) {   //进入家族
                    return MsgViewHolderFamilyJoin.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_GIFT_BAG_IM, chatMsg.cmd_type)) {   //空投消息
                    return MsgViewHolderAirdrop.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.GIFT_BAG_RECEIVE_TIPS, chatMsg.cmd_type)) {   //空投打开消息
                    return MsgViewHolderAirdropReceive.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_DICE_GAME, chatMsg.cmd_type)) {   //猜拳 骰子游戏
                    return MsgViewHolderGame.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_USER_NOBILITY_LEVEL_UP_NOTICE, chatMsg.cmd_type)) {   //贵族升级提示
                    return MsgViewHolderFamilyNobilityTip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_RED_PACKET_RAIN_COUNT_DOWN_MSG, chatMsg.cmd_type)) {   //红包通知
                    return MsgViewHolderFamilyRedPackTip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_RED_PACKET_RAIN_TOP3_TO_ALL, chatMsg.cmd_type)) {   //红包通知前三
                    return MsgViewHolderFamilyRedPackBefore3Tip.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.ACCOST_TYPE, chatMsg.cmd_type)) {   //搭讪消息
                    if (TextUtils.equals(ChatMsg.REPLY_ACCOST_TYPE, chatMsg.cmd)) {
                        return MsgViewHolderReplyAccost.class;
                    }
                    return MsgViewHolderAccost.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.ACCOST_CARD, chatMsg.cmd_type)) {   //搭讪卡片消息
                    return MsgViewHolderUserInfo.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.CALL_TYPE, chatMsg.cmd_type)) {   //语音视频通话
                    return MsgViewHolderAVChat.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.GIFT_TYPE, chatMsg.cmd_type)) {   //礼物消息
                    if (message.getSessionType() == SessionTypeEnum.Team) {
                        return MsgViewHolderTeamGift.class;
                    } else {
                        return MsgViewHolderGift.class;
                    }
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_INIT, chatMsg.cmd_type)) {   //创建群
                    return MsgViewHolderCreateFamily.class;
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.RED_PACKET_TYPE_IM, chatMsg.cmd_type)) { //金币红包
                    if (chatMsg.cmd.equals(ChatMsg.RED_PACKET_ROOM) || chatMsg.cmd.equals(ChatMsg.RED_PACKET_FAMILY)) {
                        return MsgViewHolderRedEnvelope.class;
                    }
                    if (chatMsg.cmd.equals(ChatMsg.RED_PACKET_IM_OPEN_ROOM) || chatMsg.cmd.equals(ChatMsg.RED_PACKET_IM_OPEN_FAMILY)) {
                        return MsgViewHolderRedReceive.class;
                    }
                } else if (chatMsg != null && TextUtils.equals(ChatMsg.FAMILY_SHARE_TYPE, chatMsg.cmd_type)) { //家族申请通知
                    return MsgViewHolderFamilyInvite.class;
                }

                Class<? extends MsgViewHolderBase> viewHolder = null;
                if (message.getAttachment() != null) {
                    Class<? extends MsgAttachment> clazz = message.getAttachment().getClass();
                    while (viewHolder == null && clazz != null) {
                        viewHolder = viewHolders.get(clazz);
                        if (viewHolder == null) {
                            clazz = getSuperClass(clazz);
                        }
                    }
                }
                return viewHolder == null ? MsgViewHolderUnknown.class : viewHolder;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MsgViewHolderUnknown.class;
        }
    }

    private static Class<? extends MsgAttachment> getSuperClass(Class<? extends MsgAttachment> derived) {
        Class sup = derived.getSuperclass();
        if (sup != null && MsgAttachment.class.isAssignableFrom(sup)) {
            return sup;
        } else {
            for (Class itf : derived.getInterfaces()) {
                if (MsgAttachment.class.isAssignableFrom(itf)) {
                    return itf;
                }
            }
        }
        return null;
    }

    public static List<Class<? extends MsgViewHolderBase>> getAllViewHolders() {
        List<Class<? extends MsgViewHolderBase>> list = new ArrayList<>();
        list.addAll(viewHolders.values());
        if (tipMsgViewHolder != null) {
            list.add(tipMsgViewHolder);
        }
        list.add(MsgViewHolderUnknown.class);
        list.add(MsgViewHolderText.class);
        list.add(MsgViewHolderTip.class);
        list.add(MsgViewHolderAVChat.class);
        list.add(MsgViewHolderBlack.class);
        list.add(MsgViewHolderUserInfo.class);
        list.add(MsgViewHolderRedEnvelope.class);
        list.add(MsgViewHolderRedReceive.class);
        list.add(MsgViewHolderCreateFamily.class);
        list.add(MsgViewHolderFamilyAnnouncement.class);
        list.add(MsgViewHolderFamilyTip.class);
        list.add(MsgViewHolderFamilyCoupleTip.class);
        list.add(MsgViewHolderFamilyJoin.class);
        list.add(MsgViewHolderFamilyInvite.class);
        list.add(MsgViewHolderAirdrop.class);
        list.add(MsgViewHolderAirdropReceive.class);
        list.add(MsgViewHolderGame.class);
        list.add(MsgViewHolderFamilySeat.class);
        list.add(MsgViewHolderFamilyNobilityTip.class);
        list.add(MsgViewHolderFamilyRedPackTip.class);
        list.add(MsgViewHolderFamilyRedPackBefore3Tip.class);
        return list;
    }
}
