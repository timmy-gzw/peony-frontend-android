package com.tftechsz.moment.entity;

public class LikeListItem {

    private int user_id;
    private String created_at;
    private String nickname;
    private int sex;
    private String icon;
    private int is_real;
    private int is_self;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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
                ", created_at='" + created_at + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", icon='" + icon + '\'' +
                ", is_real=" + is_real +
                ", is_self=" + is_self +
                '}';
    }
}
