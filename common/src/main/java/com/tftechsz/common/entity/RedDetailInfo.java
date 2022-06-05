package com.tftechsz.common.entity;

public
/**
 *  包 名 : com.tftechsz.common.entity

 *  描 述 : TODO
 */
class RedDetailInfo {
    public int create_red_packet_user_id;  // 红包发送者id
    public String icon; // "http://user-cdn.peony125.com?x-oss-process=image/resize,m_lfit,h_800,w_800", // 用户头像
    public String title; // "的红包", // 谁的红包
    public int coin; // 1, // 红包价值总金币
    public String des; // "", // 红包描述
    public int is_receive; // 0, // 是否开过红包
    public int is_complete; // 1 // 红包是否被领取完
    public int is_expire; //是否过期

}
