package com.tftechsz.common.nertcvoiceroom.model;

import java.io.Serializable;

/**
 * 语聊房信息
 */
public class VoiceRoomInfo implements Serializable {
    /**
     * 房间id
     */
    private String room_id;

    private String room_token;

    private String room_name;

    private String tid;

    private boolean is_apply;
    /**
     * 创建者（主播）账号
     */
    private String creatorAccount;

    /**
     * 房间名
     */
    private String name;

    /**
     * 房间缩略图
     */
    private String thumbnail;

    /**
     * 房间type
     */
    private int room_type;

    /**
     * 房间音质
     */
    private int audioQuality = NERtcVoiceRoomDef.RoomAudioQuality.DEFAULT_QUALITY;

    /**
     * 房间在线用户数
     */
    private int online_user_count;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 当前歌曲作者
     */
    private String currentMusicAuthor;

    /**
     * 当前正在演唱的歌曲
     */
    private String currentMusicName;

    private boolean is_first;

    /**
     * 主播推流地址
     */
    public String push_url;
    /**
     * 观众拉流地址
     */
    public String http_pull_url;

    public String rtmp_pull_url;

    public int partyLiveType;   //0 rtc  1 : cdn

    public int index;

    public int status;

    public int user_id;

    public String cost;

    public String icon;

    public String avatar;  // 头像框

    public int avatar_id;

    public int shutup;  //是否闭麦

    public int is_admin;   //0 无管理员权限  1：有管理员权限

    public String announcement;  // 公告

    /**
     * CDN 模式下的推拉流配置信息
     */
    private StreamConfig stream_config;

    public VoiceRoomInfo() {
    }

    public String getRoom_token() {
        return room_token;
    }

    public void setRoom_token(String room_token) {
        this.room_token = room_token;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public int getRoom_type() {
        return room_type;
    }

    public void setRoom_type(int room_type) {
        this.room_type = room_type;
    }

    public int getOnline_user_count() {
        return online_user_count;
    }

    public void setOnline_user_count(int online_user_count) {
        this.online_user_count = online_user_count;
    }

    public String getCreatorAccount() {
        return creatorAccount;
    }

    public void setCreatorAccount(String creatorAccount) {
        this.creatorAccount = creatorAccount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getAudioQuality() {
        return audioQuality;
    }

    public void setAudioQuality(int audioQuality) {
        this.audioQuality = audioQuality;
    }

    public StreamConfig getStream_config() {
        return stream_config;
    }

    public void setStream_config(StreamConfig stream_config) {
        this.stream_config = stream_config;
    }

    public boolean isSupportCDN() {
        return true;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    public void setCurrentMusicName(String currentMusicName) {
        this.currentMusicName = currentMusicName;
    }

    public String getCurrentMusicAuthor() {
        return currentMusicAuthor;
    }

    public void setCurrentMusicAuthor(String currentMusicAuthor) {
        this.currentMusicAuthor = currentMusicAuthor;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }


    public boolean isIs_apply() {
        return is_apply;
    }

    public void setIs_apply(boolean is_apply) {
        this.is_apply = is_apply;
    }


    public boolean isIs_first() {
        return is_first;
    }

    public void setIs_first(boolean is_first) {
        this.is_first = is_first;
    }
}
