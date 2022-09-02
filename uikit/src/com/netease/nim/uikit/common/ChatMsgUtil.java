
package com.netease.nim.uikit.common;

import android.graphics.Color;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.extension.AVChatAttachment;
import com.netease.nim.uikit.business.extension.AccostCardAttachment;
import com.netease.nim.uikit.business.extension.AccostChatAttachment;
import com.netease.nim.uikit.business.extension.FamilyInviteAttachment;
import com.netease.nim.uikit.business.extension.GiftChatAttachment;
import com.netease.nim.uikit.business.extension.GiftChatTeamAttachment;
import com.netease.nim.uikit.common.util.Base64;
import com.netease.nim.uikit.common.util.ClickUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

/**
 * 聊天工具类
 */
public class ChatMsgUtil implements Serializable {


    /**
     * 发送自定义文本消息
     *
     * @param type        1 搭讪私信  2 快捷私信 3 手动私信
     * @param accost_type const TYPE_SYSTEM = 1; // 系统 月老逻辑
     *                    const TYPE_HOME = 2; // 首页搭讪
     *                    const TYPE_DETAIL = 3; // 个人资料页搭讪
     *                    const TYPE_BLOG = 4; // 动态搭讪
     *                    const TYPE_PHOTO = 5; // 相册搭讪
     *                    const TYPE_SEARCH = 6; // 搜索搭讪
     *                    const TYPE_TIPS = 7; // 私聊TIPS搭讪
     *                    const TYPE_ALERT = 8; // 首页日今弹窗搭讪
     */
    public static void sendCustomTextMessage(boolean isYiDun, String to, String content, int subFromType, int accostFrom, int type, int accost_type) {
        IMMessage message = MessageBuilder.createTextMessage(to, SessionTypeEnum.P2P, content);
        String sendMsg = "{\"my_sub_type\":\"" + type + "\",\"accost_type\":\"" + accost_type + "\"}";
        MsgAttachment msgAttachment = (MsgAttachment) send -> sendMsg;
        message.setAttachment(msgAttachment);
        Map<String, Object> ext = new HashMap<>();
        ext.put("my_sub_type", type);
        ext.put("accost_from", accostFrom);
        ext.put("sub_from_type", subFromType);
        message.setRemoteExtension(ext);
        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
        antiSpamOption.enable = isYiDun;
        message.setNIMAntiSpamOption(antiSpamOption);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }

    /**
     * 拨打语音视频电话
     */
    public static void callMessage(int type, String from, String to, String matchType, boolean isMatch) {
        if (!ClickUtils.canOperate()) {
            return;
        }
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.type = type;
        chatMsg.cmd_type = ChatMsg.CALL_TYPE;
        chatMsg.cmd = type == 2 ? ChatMsg.CALL_TYPE_VIDEO : ChatMsg.CALL_TYPE_VOICE;
        if (!TextUtils.isEmpty(matchType)) {
            chatMsg.cmd = type == 2 ? ChatMsg.CALL_TYPE_VIDEO_MATCH : ChatMsg.CALL_TYPE_VOICE_MATCH;
        }
        chatMsg.content = "{\"state\":0,\"call_id\":\"\",\"callee\":0 ,\"type\":" + "\"" + matchType + "\"}";
        chatMsg.to = to;
        chatMsg.from = from;
        HashMap<String, Object> map = new HashMap<>();
        map.put("body", new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes())));
        String json = JSON.toJSONString(map);
        AVChatAttachment attachment = new AVChatAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.P2P, "您收到一个" + (type == 2 ? "[视频通话]" : "[语音通话]"), attachment);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        config.enableHistory = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).sendMessage(message, false);

    }

    /**
     * 挂断操作
     * close_type   1 超时 2 主动 3 余额不足 4:心跳检测结束
     */
    public static void sendCustomMessage(int type, int state, String from, String to, long channelId, String callId, int time, int callee, int closeType, boolean isMatch, OnMessageListener messageListener) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.type = type;
        chatMsg.cmd_type = "call";
        chatMsg.cmd = type == 2 ? ChatMsg.CALL_TYPE_VIDEO : ChatMsg.CALL_TYPE_VOICE;
        if (isMatch) {
            chatMsg.cmd = type == 2 ? ChatMsg.CALL_TYPE_VIDEO_MATCH : ChatMsg.CALL_TYPE_VOICE_MATCH;
        }
        //close_type 1 超时 2 主动
        if (closeType == 0) {
            chatMsg.content = "{\"state\":" + state + ",\"channel_id\":" + channelId + ",\"callee\":" + callee + ",\"text_type\":" + type + ",\"call_id\":" + "\"" + callId + "\"" + ",\"answer_time\":" + time + "}";
        } else {
            chatMsg.content = "{\"state\":" + state + ",\"channel_id\":" + channelId + ",\"callee\":" + callee + ",\"text_type\":" + type + ",\"close_type\":" + closeType + ",\"call_id\":" + "\"" + callId + "\"" + ",\"answer_time\":" + time + "}";
        }
        LogUtil.e("====================content", chatMsg.content);
        chatMsg.from = from;
        chatMsg.to = to;
        HashMap<String, Object> map = new HashMap<>();
        map.put("body", new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes())));
        final String json = JSON.toJSONString(map);
        AVChatAttachment attachment = new AVChatAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        IMMessage message = MessageBuilder.createCustomMessage(chatMsg.to, SessionTypeEnum.P2P, type == 2 ? "[视频通话]" : "[语音通话]", attachment);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        config.enablePush = false;
        if (state == ChatMsg.EVENT_END && time == 0)
            config.enableUnreadCount = true;
        config.enableHistory = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).sendMessage(message, true).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void aVoid, Throwable throwable) {
                if (messageListener != null)
                    messageListener.onGiftListener(code, message);
                if (state == ChatMsg.EVENT_DEFAULT || state == ChatMsg.EVENT_ACCEPT || state == ChatMsg.EVENT_BUSY) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
                }
            }
        });

    }


    /**
     * 发送礼物消息消息
     */
    public static void sendGiftMessage(SessionTypeEnum sessionType, String from, String to, int is_choose_num, int cate, int tag_value, int giftId, int giftCoin, String giftName, String giftImage, int animationType, String animation, List<String> animations, int combo, String msg, int giftNum, String cmd, String familyId, List<String> toMember, OnMessageListener messageListener) {
        if (giftNum <= 0) {
            ToastUtils.showShort("发送礼物数量不能为0");
            return;
        }
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.GIFT_TYPE;
        chatMsg.cmd = cmd;
        chatMsg.to = to;
        chatMsg.from = from;
        ChatMsg.Gift gift = new ChatMsg.Gift();
        ChatMsg.AccostGift accostGift = new ChatMsg.AccostGift();
        accostGift.id = giftId;
        accostGift.name = giftName;
        accostGift.image = giftImage;
        accostGift.coin = String.valueOf(giftCoin);
        accostGift.status = 0;
        accostGift.animationType = animationType;
        accostGift.animation = animation;
        accostGift.animations = animations;
        accostGift.combo = combo;
        accostGift.cate = String.valueOf(cate);
        accostGift.tag_value = tag_value;
        accostGift.is_choose_num = is_choose_num;
        gift.gift_info = accostGift;
        gift.gift_num = giftNum;
        GiftChatTeamAttachment attachmentTeam = null;
        GiftChatAttachment attachment = null;
        if (sessionType == SessionTypeEnum.Team) {
            gift.tid = familyId;
            gift.to = toMember;
            chatMsg.content = JSON.toJSONString(gift);
            attachmentTeam = new GiftChatTeamAttachment(12);
            attachmentTeam.setBody(getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        } else {
            chatMsg.content = JSON.toJSONString(gift);
            attachment = new GiftChatAttachment(12);
            attachment.setBody(getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        }
        IMMessage message = MessageBuilder.createCustomMessage(to, sessionType, "[礼物]", sessionType == SessionTypeEnum.Team ? attachmentTeam : attachment);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (messageListener != null)
                    messageListener.onGiftListener(code, message);
            }
        });
    }


    /**
     * 发送搭讪消息
     */
    public static void sendAccostMessage(String from, String to, int giftId, String giftName, String giftImage, String animation, String msg, int accost_type, int accostFrom) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.ACCOST_TYPE;
        chatMsg.cmd = "default";
        chatMsg.to = to;
        chatMsg.from = from;
        ChatMsg.Accost accost = new ChatMsg.Accost();
        ChatMsg.AccostGift accostGift = new ChatMsg.AccostGift();
        accostGift.id = giftId;
        accostGift.name = giftName;
        accostGift.image = giftImage;
        accostGift.status = 0;
        accostGift.animation = animation;
        accost.msg = msg;
        accost.accost_type = accost_type;
        accost.accost_from = accostFrom;
        accost.gift_info = accostGift;
        chatMsg.content = JSON.toJSONString(accost);

        AccostChatAttachment attachment = new AccostChatAttachment(10);
        attachment.setBody(getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.P2P, "[对方发来搭讪红包]", attachment);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (code == ResponseCode.RES_IN_BLACK_LIST || code == 20010) {
                    NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
                    // 同时，本地插入被对方拒收的tip消息
                    IMMessage tip = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
                    tip.setContent("对方已将您拉黑，不能给TA发消息了哦。解除黑名单后才能给TA发消息~");
                    tip.setStatus(MsgStatusEnum.success);
                    CustomMessageConfig config = new CustomMessageConfig();
                    config.enableUnreadCount = false;
                    tip.setConfig(config);
                    NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
                }
            }
        });
    }

    /**
     * 发送上下麦tip
     */
    public static void sendVoiceTipMessage(String from, String to, String room_name, String cmd, String nickName, String content, OnMessageListener messageListener) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.VOICE_ROOM_SEAT;
        chatMsg.cmd = cmd;
        chatMsg.to = to;
        chatMsg.from = from;
        ChatMsg.RoomInfo roomInfo = new ChatMsg.RoomInfo();
        roomInfo.content = content;
        roomInfo.name = nickName;
        roomInfo.room_name = room_name;
        chatMsg.content = new Gson().toJson(roomInfo);
        HashMap<String, Object> map = new HashMap<>();
        map.put("body", getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        String json = JSON.toJSONString(map);
        AccostCardAttachment attachment = new AccostCardAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.Team, "上下麦", attachment);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (messageListener != null)
                    messageListener.onGiftListener(code, message);
            }
        });
    }


    /**
     * 发送上下麦tip
     */
    public static void sendRoomVoiceTipMessage(String from, String to, String cmd) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.PARTY_VOICE_ROOM_SEAT;
        chatMsg.cmd = cmd;
        chatMsg.to = to;
        chatMsg.from = from;
        HashMap<String, Object> map = new HashMap<>();
        map.put("body", getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        String json = JSON.toJSONString(map);
        AccostCardAttachment attachment = new AccostCardAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(to, attachment);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false);
    }

    /**
     * 发送卡片信息
     */
    public static void sendCardAccostMessage(boolean isYiDun, String from, String to, String content, int subFromType, int accostFrom, Boolean isDelete) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.ACCOST_CARD;
        chatMsg.cmd = "default";
        chatMsg.to = to;
        chatMsg.from = from;
        chatMsg.content = content;
        HashMap<String, Object> map = new HashMap<>();
        map.put("body", new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes())));
        String json = JSON.toJSONString(map);
        AccostCardAttachment attachment = new AccostCardAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.P2P, "[对方发来卡片信息]", attachment);
        Map<String, Object> ext = new HashMap<>();
        ext.put("sub_from_type", subFromType);
        ext.put("accost_from", accostFrom);
        message.setRemoteExtension(ext);
        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
        antiSpamOption.enable = isYiDun;
        message.setNIMAntiSpamOption(antiSpamOption);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (isDelete)
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
            }
        });
    }


    /**
     * 发送进入页面消息
     */
    public static void sendEnterMessage(String from, String to) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.JOIN_LEAVE_ROOM;
        chatMsg.cmd = "join_p2p";
        chatMsg.to = to;
        chatMsg.content = "";
        chatMsg.from = from;
        AccostChatAttachment attachment = new AccostChatAttachment(11);
        attachment.setBody(getURLEncoderString(new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes()))));
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.P2P, ChatMsg.JOIN_LEAVE_ROOM, attachment);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        config.enablePush = false;
        config.enableRoute = false;
        config.enableRoaming = false;
        config.enableHistory = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int i, Void aVoid, Throwable throwable) {
                NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
            }
        });
    }


    /**
     * 移除消息 state == 0  的语音视频消息
     */
    public static void removeMessage(List<IMMessage> messages) {
        Iterator<IMMessage> imMessageIterator = messages.iterator();
        while (imMessageIterator.hasNext()) {
            IMMessage message = imMessageIterator.next();
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
            if (chatMsg != null) {
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {
                    ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                    if (callMsg.state == ChatMsg.EVENT_DEFAULT || callMsg.state == ChatMsg.EVENT_ACCEPT || callMsg.state == ChatMsg.EVENT_BUSY) {
                        imMessageIterator.remove();
                    }
                }
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.JOIN_LEAVE_ROOM)) {
                    imMessageIterator.remove();
                }
            }
        }
    }


    public static void removeMessage(List<IMMessage> items, IMMessage message, String userId) {
        if (message.getFromAccount().equals(userId)) {
            if ((message.getMsgType() == MsgTypeEnum.image || message.getMsgType() == MsgTypeEnum.audio)) {
                Map<String, Object> extension = message.getRemoteExtension();
                if (extension != null && extension.get("is_not_show") != null) {
                    if (1 == (Integer) extension.get("is_not_show")) {
                        NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
                        items.remove(message);
                    }
                }
            }
        }
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg != null) {
            if ((TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE))) {
                ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                if (callMsg.state == ChatMsg.EVENT_DEFAULT || callMsg.state == ChatMsg.EVENT_ACCEPT || callMsg.state == ChatMsg.EVENT_BUSY) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
                    items.remove(message);
                }
            }
            if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.JOIN_LEAVE_ROOM)) {
                NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
                items.remove(message);
            }
//            if ((TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) && message.getSessionType() == SessionTypeEnum.Team) {
//                ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
//                if (gift.gift_info.combo == 1){
//                    NIMClient.getService(MsgService.class).deleteChattingHistory(message, false);
//                    items.remove(message);
//                }
//            }
        }
    }

    /**
     * 解析消息
     * x
     *
     * @param message
     * @return
     */
    public static ChatMsg parseMessage(IMMessage message) {
        ChatMsg chatMsg = null;
        try {
            if (!TextUtils.isEmpty(message.getAttachStr()) && message.getAttachStr().contains("body")) {
                JSONObject jsonObject = JSONObject.parseObject(message.getAttachStr());
                chatMsg = parseAttachMessage(jsonObject.getString("body"));
            }
            return chatMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析消息
     *
     * @param message
     * @return
     */
    public static ChatMsg parseAttachMessage(String message) {
        ChatMsg chatMsg = null;
        try {
            String data = URLDecoderString(new String(Base64.decodeBase64(URLDecoderString(message).getBytes())));
            LogUtil.e("==============data", data);
            chatMsg = JSON.parseObject(data, ChatMsg.class);
            return chatMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMsg;
    }

    /**
     * 解析消息
     *
     * @param message
     * @return
     */
    public static ChatMsg parseMessage(String message) {
        ChatMsg chatMsg = null;
        try {
            if (!TextUtils.isEmpty(message) && message.contains("body")) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                chatMsg = parseAttachMessage(jsonObject.getString("body"));
            }
            return chatMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * tip消息
     */
    public static SpannableStringBuilder getTipContent(String content, String color, OnSelectListener listener) {
        SpannableStringBuilder span = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(content)) {
            //tag截取

            String[] text = content.split("</tag>");
            for (String s : text) {

                SpannableStringBuilder spanString = new SpannableStringBuilder();
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(s.replace("<tag", "")).append("</tag>");
                String tag = "url=";
                int start = stringBuffer.toString().indexOf(tag);
                int start2 = stringBuffer.toString().indexOf(">");
                int start3 = stringBuffer.toString().indexOf("</tag>");
                String text1 = "";
                String text2 = "";
                String text3 = "";

                if (start != -1 && start2 != -1 && start3 != -1) {
                    text1 = stringBuffer.substring(0, start);
                    if (start + tag.length() <= start2) {
                        text2 = stringBuffer.substring(start + tag.length(), start2);
                        if (start2 + 1 <= start3) {
                            text3 = stringBuffer.substring(start2 + 1, start3);
                            spanString.append(text1).append(text3);
                            String finalText = text2;
                            spanString.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(@NonNull View view) {
                                    if (null != listener)
                                        listener.onClick(finalText);

                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                }
                            }, start, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (TextUtils.isEmpty(color)) {
                                spanString.setSpan(new ForegroundColorSpan(Utils.getApp().getResources().getColor(R.color.color_link_color)), start, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                spanString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }

                } else {
                    spanString.append(s);
                }
                span.append(spanString);
            }

        }
        return span;
    }

    /**
     * tip2消息
     */
    public static List<String> getTipContent(String content) {
        SpannableStringBuilder span = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(content)) {
            //tag截取
            String[] text = content.split("</tag>");
            List<String> result = new ArrayList<>();
            for (String s : text) {

                SpannableStringBuilder spanString = new SpannableStringBuilder();
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(s.replace("<tag", "")).append("</tag>");
                String tag = "url=";
                int start = stringBuffer.toString().indexOf(tag);
                int start2 = stringBuffer.toString().indexOf(">");
                int start3 = stringBuffer.toString().indexOf("</tag>");
                String text2 = "";

                if (start != -1 && start2 != -1 && start3 != -1) {
                    if (start + tag.length() <= start2) {
                        text2 = stringBuffer.substring(start + tag.length(), start2);
                        result.add(text2);
                    }

                }
            }
            return result;
        }
        return null;
    }


    /**
     * 发送家族邀请
     */
    public static void sendFamilyShareMessage(String from, String to, int id, String content, String img_url, OnMessageListener messageListener) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.cmd_type = ChatMsg.FAMILY_SHARE_TYPE;
        chatMsg.cmd = "default";
        ChatMsg.FamilyShareMessage fsm = null;
        try {
            fsm = new ChatMsg.FamilyShareMessage(id, Base64.encode(content), img_url, from);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        chatMsg.content = "{\"share_msg\":\"" + content + "\",\"id\":" + id + ",\"img_url\":\"" + img_url + "\"}";
        chatMsg.content = JSON.toJSONString(fsm);
        chatMsg.from = from;
        chatMsg.to = to;
        HashMap<String, Object> map = new HashMap<>();
        Log.e("sendFamilyShareMessage", JSON.toJSONString(chatMsg));
        map.put("body", new String(Base64.encodeBase64(JSON.toJSONString(chatMsg).getBytes())));
        String json = JSON.toJSONString(map);
        FamilyInviteAttachment attachment = new FamilyInviteAttachment(json) {
            @Override
            public String toJson(boolean b) {
                return json;
            }
        };
        IMMessage message = MessageBuilder.createCustomMessage(to, SessionTypeEnum.P2P, content, attachment);
        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
        antiSpamOption.enable = false;
        message.setNIMAntiSpamOption(antiSpamOption);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (messageListener != null)
                    messageListener.onGiftListener(code, message);
            }
        });
    }


    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * tip消息点击事件回调
     */
    public interface OnSelectListener {
        void onClick(String content);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }


    public interface OnMessageListener {
        void onGiftListener(int code, IMMessage message);
    }

    public OnMessageListener messageListener;

    public void addOnMessageListener(OnMessageListener messageListener) {
        this.messageListener = messageListener;
    }

}
