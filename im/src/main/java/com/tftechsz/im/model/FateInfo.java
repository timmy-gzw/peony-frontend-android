package com.tftechsz.im.model;

/**
 * 缘分牵线
 */
public class FateInfo {

    private long user_id; //用户ID
    private long created_at; //发送时间
    private int msg_type; //消息类型:1发送文字,emoji表情2图片3语音4搭讪消息5礼物消息
    private FateContentInfo msg_content; //发送内容


    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public FateContentInfo getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(FateContentInfo msg_content) {
        this.msg_content = msg_content;
    }
}
