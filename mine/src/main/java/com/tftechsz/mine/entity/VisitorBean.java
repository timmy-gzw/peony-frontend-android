package com.tftechsz.mine.entity;

/**
 * 包 名 : com.tftechsz.mine.entity
 * 描 述 : TODO
 */
public class VisitorBean {
    public String created_at;
    public String nickname;
    public String birthday;
    public String icon;
    public boolean isShow;
    public int sex;
    public int is_vip;
    public int is_real;  //  //是否真人 0 不是，1是
    public int is_self;//是否实名认证
    public int picture_frame;
    public int to_user_id;
    public int from_user_id;
    public String desc;
    public int age;

    public boolean isGirl() {
        return sex == 2;
    }

    public boolean isVip() {
        return is_vip == 1;
    }

    public boolean isReal() {
        return is_real == 1;
    }
}
