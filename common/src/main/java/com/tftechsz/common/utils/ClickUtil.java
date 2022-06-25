package com.tftechsz.common.utils;

/**
 * ================================================
 * 描    述：事件间隔控制工具类（防止短时间内多次响应事件）
 * 修订历史：
 * ================================================
 */
public class ClickUtil {
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean canOperate() {
        boolean toDO = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            toDO = true;
            lastClickTime = curClickTime;
        }
        return toDO;
    }

    public static boolean canOperate(int DELAY_TIME) {
        boolean toDO = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= DELAY_TIME) {
            toDO = true;
            lastClickTime = curClickTime;
        }
        return toDO;
    }
}
