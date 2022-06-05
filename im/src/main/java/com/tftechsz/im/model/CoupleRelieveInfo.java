package com.tftechsz.im.model;

public class CoupleRelieveInfo {

    public int user_id;
    public String icon;
    public String nickname;
    public int couple_user_id;
    public String couple_user_icon;
    public String couple_user_nickname;
    public int couple_sweet;
    public int apply_limit;  // 是否限制 0-否 1-是
    public int apply_id; // 当前生效申请ID 判断是否显示申请中倒计时
    public long apply_time;  // 倒计时
    public String apply_desc;
    public String coin;// 解除所需金币
    public String coin_desc;
    public int sex;  //1:男 2 ：女
    public String couple_rule_desc;
}
