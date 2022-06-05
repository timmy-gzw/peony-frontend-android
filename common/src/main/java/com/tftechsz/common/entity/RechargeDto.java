package com.tftechsz.common.entity;

import java.io.Serializable;

public class RechargeDto implements Serializable {

    public int id; //支付类型id
    public String rmb;   //价值的金币
    public String coin;   //设置的视频金币数
    public String title;     ///描述
    public String image; ////图片
    public int is_first; //是否第一次充值
    public int is_active;
    public String desc;

    public RechargeDto() {
    }

    public RechargeDto(int id, String rmb, String coin) {
        this.id = id;
        this.rmb = rmb;
        this.coin = coin;
    }
}
