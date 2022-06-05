package com.tftechsz.im.model.dto;

public class AirdropBagDto {

    public int gift_bag_id;
    public String gift_bag_name;
    public int rule_type;  //1：男 2：女
    public int time_type;
    public long start_time;
    public int status; //0-倒计时 1-进行中 2-完成 3-失效
    public int is_receive;  //0-未领过 1-领过
    public int seconds;
    public String nickname;
    public int sex;
    public String age;
    public String icon;
    public int family_role;
}
