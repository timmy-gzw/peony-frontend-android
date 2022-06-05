package com.tftechsz.im.model.dto;

public class RoomApplyDto {
    public int user_id;
    public int id;
    public String nickname;
    public String age;
    public String icon;
    public int sex;
    public int status;   //0-待处理 1-同意 2-拒绝
    public int confirm; // 0-无需确认 1-需确认后上麦
    private int nobility_level;
    public String room_name;
    private String badge;

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public boolean isNoble() {
        return nobility_level > 0;
    }

    public int getNobleLevel() {
        return nobility_level;
    }
}
