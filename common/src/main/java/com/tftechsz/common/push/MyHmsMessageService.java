package com.tftechsz.common.push;

import com.huawei.hms.push.RemoteMessage;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nimlib.sdk.mixpush.HWPushMessageService;

public
/**
 *  包 名 : com.tftechsz.common.iservice

 *  描 述 : TODO
 */
class MyHmsMessageService  extends HWPushMessageService {

    private static final String TAG = "oHwPushMessageService";

    public void onNewToken(String token) {
        NimLog.i(TAG, " onNewToken, token=" + token);
    }

    /**
     * 透传消息， 需要用户自己弹出通知
     *
     * @param remoteMessage
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NimLog.i(TAG, " onMessageReceived");
    }

    public void onMessageSent(String s) {
        NimLog.i(TAG, " onMessageSent");
    }

    public void onDeletedMessages() {
        NimLog.i(TAG, " onDeletedMessages");
    }

    public void onSendError(String var1, Exception var2) {
        NimLog.e(TAG, " onSendError, " + var1, var2);
    }
}
