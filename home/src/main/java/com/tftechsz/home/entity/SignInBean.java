package com.tftechsz.home.entity;

import java.util.List;

/**
 * 包 名 : com.tftechsz.home.entity
 * 描 述 : TODO
 */
public class SignInBean {
    public int start_day; // 签到的日期
    public List<SignInListBean> list;
    public List<SignTopBean> top_list;
    //sign list new
    public boolean is_complete_today;
    public String desc;


    public static class SignInListBean {
        public int day_number;// 第几日
        public int cost;  // 签到获得的金币
        public String cost_desc;  // 签到获得的金币
        public String day;  // 第几日文案
        public int is_complete; // 是否签到过
        public String tips;
        /**
         * coin/chat_card
         */
        public String type = "coin";
    }

    public static class SignTopBean {
        //     cost/vip/chat_card
        public String type;
        public String title;
        public String tips;

        public SignTopBean(String type, String title, String tips) {
            this.type = type;
            this.title = title;
            this.tips = tips;
        }
    }

}
