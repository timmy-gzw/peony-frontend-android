package com.tftechsz.party.entity.dto;

import com.netease.nim.uikit.common.ChatMsg;

import java.util.List;

/**
 * 包 名 : com.tftechsz.party.entity.dto
 * 描 述 : TODO
 */
public class PartyGiftDto {

    public GiftInfoDTO gift_info;
    public List<String> to;//接收方ids
    public String toName; // 接收方的名字
    public String formName; //发送方名字
    public int form_id; //发送方id
    public String fromAccountIcon; //发送方头像
    public int gift_num;   //礼物数量
    public String to_name; //接收方名字 map string
    public int is_show;  //0 : 都展示  1：展示连送顶部效果   2：展示公屏消息


    public static class GiftInfoDTO {
        public String image;
        public int cate;
        public String name;
        public int id;
        public int coin;
        public int animationType;
        public int combo;  // 1：连击礼物  0 ：不可连击
        public String animation;
        public List<String> animations;
        public ChatMsg.BlindBoxNotifyData ext;
    }
}
