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
    public int picture_frame;
    public int to_user_id;
    public int from_user_id;
    public String desc;
    public int age;

    public boolean isGirl() {
        return sex == 2;
    }
}
