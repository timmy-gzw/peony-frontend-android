package com.tftechsz.mine.entity.dto;

public class ChargeInfoDto {


    public int msg_coin; //设置的聊天金币数
    public int voice_coin;   //设置的语音金币数
    public int video_coin;   //设置的视频金币数
    public int is_voice;     //是否开启语音收费：0.不开启，1.开启 默认开启
    public int is_video; //是否开启视频收费：0.不开启，1.开启 默认开启
    public String desc;
    public String url;
}
