package com.tftechsz.moment.entity;


/**
 * 动态数据
 */
public class CommentListItem {

    private int user_id;
    private int comment_id;
    private int reply_user_id;
    private String reply_nickname;
    private String content;
    private String created_at;
    private String nickname;
    private int sex;
    private String icon;
    private int is_real;
    private int is_self;
    public int picture_frame;
    private int is_vip;

    public boolean isVip() {
        return getIs_vip() == 1;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public int getReply_user_id() {
        return reply_user_id;
    }

    public void setReply_user_id(int reply_user_id) {
        this.reply_user_id = reply_user_id;
    }

    public String getReply_nickname() {
        return reply_nickname;
    }

    public void setReply_nickname(String reply_nickname) {
        this.reply_nickname = reply_nickname;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIs_real() {
        return is_real;
    }

    public void setIs_real(int is_real) {
        this.is_real = is_real;
    }

    public int getIs_self() {
        return is_self;
    }

    public void setIs_self(int is_self) {
        this.is_self = is_self;
    }

    @Override
    public String toString() {
        return "CommentListDataItem{" +
                "user_id=" + user_id +
                ", content='" + content + '\'' +
                ", created_at='" + created_at + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", icon='" + icon + '\'' +
                ", is_real=" + is_real +
                ", is_self=" + is_self +
                '}';
    }

}
