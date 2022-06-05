package com.netease.nim.uikit.impl.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.netease.nim.uikit.api.NimUIKit;

/**
 * Created by hzxuwen on 2015/10/21.
 */
public class UserPreferences {

    private final static String KEY_EARPHONE_MODE = "KEY_EARPHONE_MODE";
    private final static String KEY_CLICK_CALL = "KEY_CLICK_CALL";
    private final static String KEY_KEYBOARD_HEIGHT = "KEY_KEYBOARD_HEIGHT";

    public static void setEarPhoneModeEnable(boolean on) {
        saveBoolean(KEY_EARPHONE_MODE, on);
    }

    public static boolean isEarPhoneModeEnable() {
        return getBoolean(KEY_EARPHONE_MODE, true);
    }

    public static void setIsClickCall(boolean on) {
        saveBoolean(KEY_CLICK_CALL, on);
    }

    /**
     * 是否点击了视频
     */
    public static boolean isClickCall() {
        return getBoolean(KEY_CLICK_CALL, false);
    }


    public static void setKeyboardHeight(int height) {
        savInt(KEY_KEYBOARD_HEIGHT, height);
    }

    /**
     * 获取键盘高度
     */
    public static int getKeyboardHeight(int height) {
        return getInt(KEY_KEYBOARD_HEIGHT,height);
    }

    private static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    private static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static int getInt(String key,int height) {
        return getSharedPreferences().getInt(key, height);
    }

    private static void savInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences() {
        return NimUIKit.getContext().getSharedPreferences("tfpeony.nim." + NimUIKit.getAccount(), Context.MODE_PRIVATE);
    }
}
