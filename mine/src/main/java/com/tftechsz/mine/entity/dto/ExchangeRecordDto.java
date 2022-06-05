package com.tftechsz.mine.entity.dto;

public class ExchangeRecordDto {

    public String image_small;
    public String type; // //coin | rmb
    public int type_id;     //   //对应config coin[type_id]
    public String title;     //"兑换了金币", //描述
    public int status; //    //0.申请中，1.已兑换，2.不允许兑换
    public String integral; //, //使用的积分
    public String created_at; //"2020-11-20 18:02:54" //创建时间
    public String cost_balance;   //当时剩余的余额
    public String cost;
    public String coin;
    public String free_coin;
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
