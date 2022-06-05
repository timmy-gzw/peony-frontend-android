package com.tftechsz.common.entity;

import java.io.Serializable;
import java.util.List;

public class FamilyIdDto {

    public int user_id;
    public int family_id;
    public int sex;
    public String age;
    public String icon;
    public String nickname;
    public String role_title;
    public int role_id;
    public int is_create_family;
    public String family_name;
    public String family_icon;
    public int is_sign;  //0 否 1 是
    private int nobility_level;
    private int is_party_girl;
    private boolean is_to_gift;//是否可收礼
    public DressUp dress_up;
    private String badge;
    public int show_fuxing;//显示新活动
    public String nobility_card_svga; //贵族资料卡svga
    public String icon_svga;
    public int icon_id;
    public OtherInfo other_info;

    /**
     * "same_sex": 0,  // 1-同性别 0-不同性别
     * "is_couple": 1, // 是否组情侣
     * "couple_nickname": "001",   // 情侣名称
     * "couple_icon": "http:\/\/user-cdn1.peony125.com\/user\/avatar\/91981dc9ea67b307972a5841980e330b.jpeg?x-oss-process=image\/resize,m_lfit,h_400,w_400",   // 情侣头像
     * "couple_user_id": 38,   // 情侣user_id
     */
    public int same_sex;
    public int is_couple;
    public String couple_nickname;
    public String couple_icon;
    public int couple_user_id;
    public List<CoupleGiftListInfo> couple_gift_list;//点亮礼物



    public static class OtherInfo {
        public int role_id;
    }

    public static class CoupleGiftListInfo {
        public String gift_name;
        public String gift_icon;
        public int gift_id;
        /**
         * 加了个gift_image大图
         * msg 描述
         * desc 替换字符
         */
        public String desc;
        public String msg;
        public String gift_image;
    }


    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public boolean isToGift() {
        return is_to_gift;
    }

    public boolean isPartyGirl() {
//        return true;
        return is_party_girl == 1;
    }

    public boolean isGirl() {
        return sex == 2;
    }


    public int getNobleLevel() {
        return nobility_level;
    }

    public boolean isNoble() {
        return nobility_level > 0;
    }

    public void setNobility_level(int nobility_level) {
        this.nobility_level = nobility_level;
    }

    public int status;  //0 未开启  1 开始语音闲聊
    public String room_id;  //语音闲聊房间名称
    public int room_status;    //0 未开启  1 开始语音闲聊

    public int party_room_id;
    public LevelValue rich;   //土豪值
    public LevelValue charm;   //魅力值
    public CoupleSweetInfo coupleSweetInfo; //亲密值减少

    /**
     * msg=您与 ⊙∀⊙！ 的甜蜜值减少2500, 当前甜蜜值为10245。在家族内多互动，互送礼物可以增加甜蜜值哦~,
     * nickname= 1,
     * numbers=-2500
     */
    public static class CoupleSweetInfo implements Serializable {
        public String msg;
        public String nickname;
        public String numbers;
    }


    public static class LevelValue implements Serializable {
        public String title; //魅力头衔
        public String level; //等级 lv.0",
        public String value; //  土豪值 99"  //总经验值
    }


    public static class DressUp {
        public String car;
    }

}
