package com.tftechsz.party.entity;

import java.util.List;

public class WheelResultBean {
    public List<RewardBean> reward;
    /**
     * 'status' 1-正常 0-异常
     * 'msg'  提示
     */
    public String msg;
    public int status;
    public int index;

    public static class RewardBean {
        public String gift_image;
        public String gift_name;
        public int gift_id;
        public int num;
    }
}
