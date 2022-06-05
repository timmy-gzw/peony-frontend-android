package com.tftechsz.party.entity;

/**
 * "id": 6,
 * "icon": "http://public-cdn1.peony125.com/party/12.png",
 * "icon_value": "/user/party/12.png",
 * "room_name": "语音聊天10",
 * "fight_pattern": 1,
 * "is_mute": 0,
 * "microphone_pattern": 1,
 * "title": "测试",
 * "announcement": "1",
 * "bg_icon": "http://public-cdn1.peony125.com/party/background/peony_pd_bg_img01.png",
 * "bg_icon_value": "peony_pd_bg_img01.png"
 */
public class PartyEditBean {
    public int id;
    public String icon;
    public String icon_value;
    public String room_name;
    public int fight_pattern;
    public int is_mute;
    public int microphone_pattern;
    public String title;
    public String announcement;
    public String bg_icon;
    public String bg_icon_value;
    //       1 展示 0 不展示
    public int show_close_btn;
}
