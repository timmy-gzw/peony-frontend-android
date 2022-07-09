package com.tftechsz.mine.entity.dto;

public class FriendDto {


    public int user_id;
    public int sex;   //  //用户性别：0.未知，1.男，2.女
    public String nickname;  //用户昵称
    public String birthday;  //用户出生日期
    public int is_self;   //        //是否真人认证
    public String height;   //        //身高
    public String job;   //          //职业
    public String icon;   //         //头像
    public String province;   // "广东", //省
    public String city;   // "深圳",     //市
    public String age;
    public String user_code;   // 用户芍药码
    public int is_real;   //  //是否真人 0 不是，1是

    public String created_at;   //拉黑时间
    public int is_vip;
    public int picture_frame;

    public float intimacy_val; //亲密度

}
