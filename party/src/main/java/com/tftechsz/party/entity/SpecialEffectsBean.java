package com.tftechsz.party.entity;

public class SpecialEffectsBean {
    /**
     * type 1礼物开关 2座驾开关
     */
    public int type;

    /**
     * status 1打开开关  2关闭开关
     */
    public int status;

    public SpecialEffectsBean(int type, int status) {
        this.status = status;
        this.type = type;
    }
}
