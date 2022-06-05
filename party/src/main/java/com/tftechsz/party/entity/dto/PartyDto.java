package com.tftechsz.party.entity.dto;

import com.netease.nim.uikit.common.ConfigInfo;

import java.util.List;

public class PartyDto {
    private int party_room_id;  // 获取当前自己的房间id
    private String room_id;
    private String icon;
    private int room_dress_status;  // 是否是装扮 1装扮 0第一次装扮
    private int is_room_mainer;  // 0不是房主 1是房主
    private String hour_rank_page;
    private String rank_link;   //巅峰榜
    private List<PartyInfoDto> list;
    public List<OptionDTOData> banner_list;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getParty_room_id() {
        return party_room_id;
    }

    public void setParty_room_id(int party_room_id) {
        this.party_room_id = party_room_id;
    }

    public int getRoom_dress_status() {
        return room_dress_status;
    }

    public void setRoom_dress_status(int room_dress_status) {
        this.room_dress_status = room_dress_status;
    }

    public int getIs_room_mainer() {
        return is_room_mainer;
    }

    public void setIs_room_mainer(int is_room_mainer) {
        this.is_room_mainer = is_room_mainer;
    }

    public String getHour_rank_page() {
        return hour_rank_page;
    }

    public void setHour_rank_page(String hour_rank_page) {
        this.hour_rank_page = hour_rank_page;
    }

    public List<PartyInfoDto> getList() {
        return list;
    }

    public void setList(List<PartyInfoDto> list) {
        this.list = list;
    }

    public String getRank_link() {
        return rank_link;
    }

    public void setRank_link(String rank_link) {
        this.rank_link = rank_link;
    }

    public static class OptionDTOData {
        public String img;
        public String link;
        public ConfigInfo.Option optionDTO2;
    }



}
