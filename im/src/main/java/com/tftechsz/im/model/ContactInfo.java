package com.tftechsz.im.model;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.Map;

public class ContactInfo implements RecentContact {

    public String content;
    public String contactId;
    public String fromAccount;
    public String fromNick;
    public SessionTypeEnum sessionType;
    public String recentMessageId;
    public MsgStatusEnum msgStatus;
    public MsgTypeEnum msgType;
    public MsgAttachment attachment;
    public int index;
    public int unreadCount;
    public long time;
    public long tag;
    public long tag_time;
    public boolean is_online;
    public String cmd_type;
    public String cmd;
    public String from_type;
    public Map<String, Object> extension;
    public String userAvatar;
    public float intimacy_val;  //亲密度
    public String accostDesc;  //是否显示附近搭讪文案
    private boolean isSelected = false;
    public int picture_frame;
    public int is_vip;
    public int userId;

    @Override
    public String getContactId() {
        return contactId;
    }

    @Override
    public String getFromAccount() {
        return fromAccount;
    }

    @Override
    public String getFromNick() {
        return fromNick;
    }

    @Override
    public SessionTypeEnum getSessionType() {
        return sessionType;
    }

    @Override
    public String getRecentMessageId() {
        return recentMessageId;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return msgType;
    }

    @Override
    public MsgStatusEnum getMsgStatus() {
        return msgStatus;
    }

    @Override
    public void setMsgStatus(MsgStatusEnum msgStatusEnum) {

    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public MsgAttachment getAttachment() {
        return attachment;
    }

    @Override
    public void setTag(long l) {

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public long getTag() {
        return tag;
    }

    @Override
    public Map<String, Object> getExtension() {
        return extension;
    }

    @Override
    public void setExtension(Map<String, Object> map) {

    }

    @Override
    public boolean setLastMsg(IMMessage message) {
        return true;
    }
}
