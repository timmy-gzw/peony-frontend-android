package com.netease.nim.uikit.common;


import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
    public int is_show_icon;//靓号展示
    public FamilyDTO family;
    private int user_id;
    private int sex;   //  //用户性别：0.未知，1.男，2.女
    private String nickname;  //用户昵称
    private String birthday;  //用户出生日期
    private int is_icon_read;   //  //是否是真人    0 否 1 是
    public int is_follow;  //是否关注
    private int is_self;   //        //是否实名认证
    private int is_accost;   //     //是否搭讪过
    private String height;   //        //身高
    private String weight;   //        //身高
    private String job;   //          //职业
    private String desc;   //          //简介
    private String icon;   //         //头像
    private String province;   // "广东", //省
    private String city;   // "深圳",     //市
    public String tags;
    public String voice;  //音视频文件
    public String voice_time;
    public int family_id;  //家族id
    public int is_create_family;  //是否有创建家族权限
    public float intimacy;  //亲密度
    public String audit_nickname;
    public String audit_desc;
    public String audit_icon; // 头像在审核中的内容
    public String audit_voice; // 在审核中的数据 语音播放文件
    public String audit_voice_time; // 在审核中的数据 语音的时长
    public int is_save_audit_icon; // 是否可以修改 头像
    public int is_save_audit_voice; // 是否可以修改 语音
    public String limit_audit_icon_msg; // 头像 限制的消息
    public String limit_audit_voice_msg; // 语音 限制的消息
    public String is_ban_msg;  //是否禁用了
    public String constellation;  //星座
    public String hometown;  //家乡
    public String income;   // 收入
    private int age;   // 年龄
    private String user_code;   // 用户芍药码
    private int watch_number;   // 我关注的用户
    private int fans_number;   // 我的粉丝数
    private int friend_number;   // 我的好友数
    private int is_real;   //  //是否真人认证 0 不是，1是
    private String coin;   // 用户金币
    private String integral;   // 用户积分
    private String note_value; //用户音浪值
    private boolean isSelected = true;
    private int is_online;// 0 不在线，1 在线
    private String online_message; // 在线描述
    private List<String> picture; // 相册
    private int is_vip; //是否是vip
    private long vip_expiration_time; //剩余时间
    public int open_hidden_rank;  //是否开启 “隐藏土豪值，魅力值” 1-开启 0-关闭
    public int open_hidden_gift;   // 是否开启礼物墙   1-开启 0-关闭
    private String vip_background_icon;
    private String vip_desc;
    public int picture_frame;
    public int chat_bubble;
    public int visitor_count;
    public String tag_distance;  //距离显示
    public String vip_expiration_time_desc;
    public String vip_expiration_time_desc_new;
    public int status;  // 1=正常;2=禁用;3=禁言;4=注销;5=禁用
    private int nobility_level; //贵族等级
    public PartyPlayMy play_party;//显示派对信息

    public List<BaseInfo> info;
    public Level levels;
    //搭讪缩放动画
    public boolean transitionAnima;
    public int is_party_girl;//是否是语音房女用户
    public int rich_level;//土豪值

    public static class PartyPlayMy implements Serializable {
        public int id;
        public String room_name;
        public String icon;
        public String room_id;

    }

    public static class Level implements Serializable {

        public LevelValue rich;   //土豪值
        public LevelValue charm;   //魅力值
    }

    public static class LevelValue implements Serializable {

        public String title; //魅力头衔
        public String level; //等级 lv.0",
        public String value; //  土豪值 99"  //总经验值

    }

    public static class BaseInfo implements Serializable {

        public String title;
        public String value;

    }

    public boolean isPartyGirl() {
//        return true;
        return is_party_girl == 1;
    }

    public String getNote_value() {
        return note_value;
    }

    public void setNote_value(String note_value) {
        this.note_value = note_value;
    }

    public String getVip_background_icon() {
        return vip_background_icon;
    }

    public void setVip_background_icon(String vip_background_icon) {
        this.vip_background_icon = vip_background_icon;
    }

    public boolean isNoble() {
        return nobility_level > 0;
    }

    public int getNobleLevel() {
        return nobility_level;
    }

    public void setNobility_level(int is_noble) {
        this.nobility_level = is_noble;
    }

    public boolean isDisable() {
        return status == 2 || status == 5;
    }

    public boolean isLogout() {
        return status == 4;
    }

    public String getVip_desc() {
        return vip_desc;
    }

    public void setVip_desc(String vip_desc) {
        this.vip_desc = vip_desc;
    }

    public boolean isVip() {
        return is_vip != 0;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public long getVip_expiration_time() {
        return vip_expiration_time * 1000;
    }

    public void setVip_expiration_time(int vip_expiration_time) {
        this.vip_expiration_time = vip_expiration_time;
    }

    public List<String> getPicture() {
        return picture;
    }

    public void setPicture(List<String> picture) {
        this.picture = picture;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public String getOnline_message() {
        return online_message;
    }

    public void setOnline_message(String online_message) {
        this.online_message = online_message;
    }

    public int getIs_icon_read() {
        return is_icon_read;
    }

    public void setIs_icon_read(int is_icon_read) {
        this.is_icon_read = is_icon_read;
    }

    public int getIs_self() {
        return is_self;
    }

    public boolean isSelf() {
        return is_self == 1;
    }

    public void setIs_self(int is_self) {
        this.is_self = is_self;
    }

    public int getIs_accost() {
        return is_accost;
    }

    public boolean isAccost() {
        return is_accost == 1;
    }

    public void setIs_accost(int is_accost) {
        this.is_accost = is_accost;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getSex() {
        return sex;
    }

    public boolean isBoy() {
        return sex != 2;
    }

    public boolean isGirl() {
        return sex == 2;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public int getWatch_number() {
        return watch_number;
    }

    public void setWatch_number(int watch_number) {
        this.watch_number = watch_number;
    }

    public int getFans_number() {
        return fans_number;
    }

    public void setFans_number(int fans_number) {
        this.fans_number = fans_number;
    }

    public int getFriend_number() {
        return friend_number;
    }

    public void setFriend_number(int friend_number) {
        this.friend_number = friend_number;
    }

    public int getIs_real() {
        return is_real;
    }

    public boolean isReal() { //是否真人
        return is_real == 1;
    }

    public void setIs_real(int is_real) {
        this.is_real = is_real;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public int getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(int is_follow) {
        this.is_follow = is_follow;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public List<BaseInfo> getInfo() {
        return info;
    }

    public void setInfo(List<BaseInfo> info) {
        this.info = info;
    }

    public Level getLevels() {
        return levels;
    }

    public void setLevels(Level levels) {
        this.levels = levels;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static class FamilyDTO implements Serializable {
        public int id;
        public String icon;
        public String tname;
        public String intro;
        public int userCount;
        //level   level_icon   prestige
        public String level;
        public String level_icon;
        public String prestige;
    }
}
