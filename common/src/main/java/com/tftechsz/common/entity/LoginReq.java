package com.tftechsz.common.entity;

public class LoginReq {

    public String type;  // 一键登陆 "phone_one"  /验证码登录 "phone_code", //qq登录 //微信登录
    public String from_channel;
    public LoginData data;
    public String phone;
    public String key;

    public static class LoginData {
        public String token;
        public String code;
        public String phone;
        public String openid;   //微信登录appid
    }

}
