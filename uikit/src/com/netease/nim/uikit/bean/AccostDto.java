package com.netease.nim.uikit.bean;

public class AccostDto {

    public AccostGift gift;
    public String msg;
    public String tips_msg;
    public boolean is_real_alert; //真人
    public boolean is_self_alert; //实名
    public AccostPic picture;
    public AccostVoice voice;
    public String from_accost_card;
    public String to_accost_card;
    public int sub_from_type;  //区分消息来自哪
    public int accost_from;  //区分消息来自哪 新加
    public String accost_resume;

    public static class AccostVoice {
        public String url;
        public String title;
        public int time;
    }

    public static class AccostPic {
        public String url;
    }

    public static class AccostGift {
        public String name;
        public String image;
        public String coin;
        public String cate;
        public String animation;
        public int id;
    }
}
