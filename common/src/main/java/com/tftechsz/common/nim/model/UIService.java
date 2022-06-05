package com.tftechsz.common.nim.model;

import android.app.Activity;

public interface UIService {
    Class<? extends Activity> getOneToOneAudioChat();

    Class<? extends Activity> getOneToOneVideoChat();

    Class<? extends Activity> getGroupVideoChat();

    int getNotificationIcon();

    int getNotificationSmallIcon();
}
