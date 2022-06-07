package com.tftechsz.common.nim;

import android.content.Context;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * Created by jezhee on 2/20/15.
 */
public class NimCache {

    private static Context context;

    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        return account;
    }

    private static boolean mainTaskLaunching;

    public static void setAccount(String account) {
        NimCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        NimCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }


    public static void setContext(Context context) {
        NimCache.context = context.getApplicationContext();
//        AVChatKit.setContext(context);
    }

    public static void setMainTaskLaunching(boolean mainTaskLaunching) {
        NimCache.mainTaskLaunching = mainTaskLaunching;

    }

    public static boolean isMainTaskLaunching() {
        return mainTaskLaunching;
    }
}
