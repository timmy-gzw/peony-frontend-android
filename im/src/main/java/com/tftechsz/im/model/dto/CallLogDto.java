package com.tftechsz.im.model.dto;

/**
 * 包 名 : com.tftechsz.im.model.dto
 * 描 述 : TODO
 */
public class CallLogDto {

    public int id;
    public int from_user_id;
    public int to_user_id;
    public boolean is_from; // true-主叫方 false-被叫方
    public String user_icon; // 头像
    public String user_nickname;  // 昵称
    public int call_type;  // 类型 1-语音 2-视频 3-语音（速配） 4-视频（速配）
    public String call_desc; // 描述
    public String color; // 描述颜色值
    public String end_time;
    public boolean is_tag;
    public int is_vip;

    public boolean isVideo() {
        return call_type == 2 || call_type == 4;
    }

    public boolean isVip() {
        return is_vip == 1;
    }
}
