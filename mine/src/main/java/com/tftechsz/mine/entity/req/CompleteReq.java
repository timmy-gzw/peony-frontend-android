package com.tftechsz.mine.entity.req;

import java.io.Serializable;

public class CompleteReq implements Serializable {
    public int sex;   //  //用户性别：0.未知，1.男，2.女
    public String nickname;  //用户昵称
    public String birthday;  //用户出生日期
    public String user_code;  //邀请码
    public String icon;  //头像
    public int birthday_boy;// 男生默认生日
    public int birthday_girl;// 女生默认生日

}
