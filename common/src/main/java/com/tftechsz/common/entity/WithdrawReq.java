package com.tftechsz.common.entity;

public class WithdrawReq {
    public int type_id;   //
    public Withdraw withdraw;
    public int out_warn;


    public static class Withdraw{
        public String account;  // 帐号
        public String name;  // 真实名称
        public String identity;    // 身份证
        public String phone;    // 手机号
        public int is_show;//是否显示支付宝备用账号
    }



}
