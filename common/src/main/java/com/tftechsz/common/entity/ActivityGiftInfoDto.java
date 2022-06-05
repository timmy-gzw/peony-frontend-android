package com.tftechsz.common.entity;

import com.netease.nim.uikit.common.ConfigInfo;

public class ActivityGiftInfoDto {
    /**
     * "icon": "",         // 用户头像
     * "nickname": "",     // 用户昵称
     * "title": "本周冠名"
     */
    public String nickname;
    public String title;
    public String icon;
    //1显示 下面的横幅 ，2显示上面的冠名
    public int type;
    //背景
    public String bg;
    public String link;
    public ConfigInfo.Option option;
}
