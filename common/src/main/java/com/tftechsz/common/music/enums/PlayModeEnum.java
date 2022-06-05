package com.tftechsz.common.music.enums;

/**
 * 包 名 : com.tftechsz.common.music.enums
 * 描 述 : 播放模式
 */
public enum PlayModeEnum {
    /**
     * 顺序播放，默认的播放模式
     */
    LOOP(0),
   /* *//*
    SHUFFLE(1),*/
    /**
     * 单曲循环
     */
    SINGLE(1);

    private final int value;

    PlayModeEnum(int value) {
        this.value = value;
    }

    public static PlayModeEnum valueOf(int value) {
        if (value == 1) {
            return SINGLE;
        }
        return LOOP;
    }

    public int value() {
        return value;
    }
}
