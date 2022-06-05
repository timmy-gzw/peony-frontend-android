package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 派对信息
 */
public class PartyInfoDto implements Parcelable {

    private int id;
    private String room_id;
    private int room_code_pretty;   //派对靓号
    private String room_code;
    private String room_name;
    private String icon;   //封面
    private String recommend_icon;   //推荐图片
    private String classify_icon;   //分类图片
    private String heat;  //热度
    private String title;  //公告标题;
    private int love_switch;   //爱意值 开关  0 : 开  1：关


    private String bg_icon;
    private String announcement;  //公告内容
    private String desc;
    private int apply_count;  //申请人数
    private String rank_link;   // 魅力榜单
    private int fight_pattern;  //模式  1：普通模式  2：pk 模式
    private int microphone_pattern;//1/2 自由/麦序
    private String push_url;
    private String rtmp_pull_url;
    private String http_pull_url;
    private int hour_rank;  //当前排名
    private String hour_rank_before_diff;  //和上一名距离
    private String hour_rank_page;
    private String volume;
    private PkDataDto pk_data;
    private int shutup;  //0：正常 1：禁言
    private int turntable_skin;//1、圣诞活动
    private String im_room_token;  //房间token
    private String im_room_name;  //房间名字

    private int dress_switch;   //特效-座驾     开关 开关  0: 开  1：关
    private int gift_switch;   //特效-礼物      开关 开关  0: 开  1：关


    public int getDress_switch() {
        return dress_switch;
    }

    public void setDress_switch(int dress_switch) {
        this.dress_switch = dress_switch;
    }

    public int getGift_switch() {
        return gift_switch;
    }

    public void setGift_switch(int gift_switch) {
        this.gift_switch = gift_switch;
    }

    public int getMicrophonePattern() {
        return microphone_pattern;
    }

    public void setMicrophonePattern(int microphone_pattern) {
        this.microphone_pattern = microphone_pattern;
    }

    public int getTurntable_skin() {
        return turntable_skin;
    }

    public void setTurntable_skin(int turntable_skin) {
        this.turntable_skin = turntable_skin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoom_code_pretty() {
        return room_code_pretty;
    }

    public void setRoom_code_pretty(int room_code_pretty) {
        this.room_code_pretty = room_code_pretty;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRecommend_icon() {
        return recommend_icon;
    }

    public void setRecommend_icon(String recommend_icon) {
        this.recommend_icon = recommend_icon;
    }

    public String getClassify_icon() {
        return classify_icon;
    }

    public void setClassify_icon(String classify_icon) {
        this.classify_icon = classify_icon;
    }

    public String getHeat() {
        return heat;
    }

    public void setHeat(String heat) {
        this.heat = heat;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getFight_pattern() {
        return fight_pattern;
    }

    public void setFight_pattern(int fight_pattern) {
        this.fight_pattern = fight_pattern;
    }

    public String getBg_icon() {
        return bg_icon;
    }

    public void setBg_icon(String bg_icon) {
        this.bg_icon = bg_icon;
    }

    public String getPush_url() {
        return push_url;
    }

    public void setPush_url(String push_url) {
        this.push_url = push_url;
    }

    public String getRtmp_pull_url() {
        return rtmp_pull_url;
    }

    public void setRtmp_pull_url(String rtmp_pull_url) {
        this.rtmp_pull_url = rtmp_pull_url;
    }

    public String getHttp_pull_url() {
        return http_pull_url;
    }

    public void setHttp_pull_url(String http_pull_url) {
        this.http_pull_url = http_pull_url;
    }

    public String getRank_link() {
        return rank_link;
    }

    public void setRank_link(String rank_link) {
        this.rank_link = rank_link;
    }

    public int getLove_switch() {
        return love_switch;
    }

    public void setLove_switch(int love_switch) {
        this.love_switch = love_switch;
    }

    public int getHour_rank() {
        return hour_rank;
    }

    public void setHour_rank(int hour_rank) {
        this.hour_rank = hour_rank;
    }

    public String getHour_rank_before_diff() {
        return hour_rank_before_diff;
    }

    public int getApply_count() {
        return apply_count;
    }

    public void setApply_count(int apply_count) {
        this.apply_count = apply_count;
    }

    public void setHour_rank_before_diff(String hour_rank_before_diff) {
        this.hour_rank_before_diff = hour_rank_before_diff;
    }

    public String getHour_rank_page() {
        return hour_rank_page;
    }

    public void setHour_rank_page(String hour_rank_page) {
        this.hour_rank_page = hour_rank_page;
    }

    public int getMicrophone_pattern() {
        return microphone_pattern;
    }

    public void setMicrophone_pattern(int microphone_pattern) {
        this.microphone_pattern = microphone_pattern;
    }

    public PkDataDto getPk_data() {
        return pk_data;
    }

    public void setPk_data(PkDataDto pk_data) {
        this.pk_data = pk_data;
    }

    public int getShutup() {
        return shutup;
    }

    public void setShutup(int shutup) {
        this.shutup = shutup;
    }

    public String getIm_room_token() {
        return im_room_token;
    }

    public void setIm_room_token(String im_room_token) {
        this.im_room_token = im_room_token;
    }

    public String getIm_room_name() {
        return im_room_name;
    }

    public void setIm_room_name(String im_room_name) {
        this.im_room_name = im_room_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.room_id);
        dest.writeInt(this.room_code_pretty);
        dest.writeString(this.room_code);
        dest.writeString(this.room_name);
        dest.writeString(this.icon);
        dest.writeString(this.recommend_icon);
        dest.writeString(this.classify_icon);
        dest.writeString(this.heat);
        dest.writeString(this.title);
        dest.writeInt(this.love_switch);
        dest.writeString(this.bg_icon);
        dest.writeString(this.announcement);
        dest.writeString(this.desc);
        dest.writeInt(this.apply_count);
        dest.writeString(this.rank_link);
        dest.writeInt(this.fight_pattern);
        dest.writeInt(this.microphone_pattern);
        dest.writeString(this.push_url);
        dest.writeString(this.rtmp_pull_url);
        dest.writeString(this.http_pull_url);
        dest.writeInt(this.hour_rank);
        dest.writeString(this.hour_rank_before_diff);
        dest.writeString(this.hour_rank_page);
        dest.writeString(this.volume);
        dest.writeParcelable(this.pk_data, flags);
        dest.writeInt(this.shutup);
        dest.writeInt(this.turntable_skin);
        dest.writeString(this.im_room_token);
        dest.writeString(this.im_room_name);
        dest.writeInt(this.dress_switch);
        dest.writeInt(this.gift_switch);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.room_id = source.readString();
        this.room_code_pretty = source.readInt();
        this.room_code = source.readString();
        this.room_name = source.readString();
        this.icon = source.readString();
        this.recommend_icon = source.readString();
        this.classify_icon = source.readString();
        this.heat = source.readString();
        this.title = source.readString();
        this.love_switch = source.readInt();
        this.bg_icon = source.readString();
        this.announcement = source.readString();
        this.desc = source.readString();
        this.apply_count = source.readInt();
        this.rank_link = source.readString();
        this.fight_pattern = source.readInt();
        this.microphone_pattern = source.readInt();
        this.push_url = source.readString();
        this.rtmp_pull_url = source.readString();
        this.http_pull_url = source.readString();
        this.hour_rank = source.readInt();
        this.hour_rank_before_diff = source.readString();
        this.hour_rank_page = source.readString();
        this.volume = source.readString();
        this.pk_data = source.readParcelable(PkDataDto.class.getClassLoader());
        this.shutup = source.readInt();
        this.turntable_skin = source.readInt();
        this.im_room_token = source.readString();
        this.im_room_name = source.readString();
        this.dress_switch = source.readInt();
        this.gift_switch = source.readInt();
    }

    public PartyInfoDto() {
    }

    protected PartyInfoDto(Parcel in) {
        this.id = in.readInt();
        this.room_id = in.readString();
        this.room_code_pretty = in.readInt();
        this.room_code = in.readString();
        this.room_name = in.readString();
        this.icon = in.readString();
        this.recommend_icon = in.readString();
        this.classify_icon = in.readString();
        this.heat = in.readString();
        this.title = in.readString();
        this.love_switch = in.readInt();
        this.bg_icon = in.readString();
        this.announcement = in.readString();
        this.desc = in.readString();
        this.apply_count = in.readInt();
        this.rank_link = in.readString();
        this.fight_pattern = in.readInt();
        this.microphone_pattern = in.readInt();
        this.push_url = in.readString();
        this.rtmp_pull_url = in.readString();
        this.http_pull_url = in.readString();
        this.hour_rank = in.readInt();
        this.hour_rank_before_diff = in.readString();
        this.hour_rank_page = in.readString();
        this.volume = in.readString();
        this.pk_data = in.readParcelable(PkDataDto.class.getClassLoader());
        this.shutup = in.readInt();
        this.turntable_skin = in.readInt();
        this.im_room_token = in.readString();
        this.im_room_name = in.readString();
        this.dress_switch = in.readInt();
        this.gift_switch = in.readInt();
    }

    public static final Creator<PartyInfoDto> CREATOR = new Creator<PartyInfoDto>() {
        @Override
        public PartyInfoDto createFromParcel(Parcel source) {
            return new PartyInfoDto(source);
        }

        @Override
        public PartyInfoDto[] newArray(int size) {
            return new PartyInfoDto[size];
        }
    };
}
