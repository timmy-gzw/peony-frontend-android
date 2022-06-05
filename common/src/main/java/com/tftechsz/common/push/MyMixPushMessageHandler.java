package com.tftechsz.common.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.RomUtils;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.mixpush.MixPushMessageHandler;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.common.Constants;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.nim.NimCache;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MyMixPushMessageHandler implements MixPushMessageHandler {

    public static final String PAYLOAD_SESSION_ID = "sessionID";
    public static final String PAYLOAD_SESSION_TYPE = "sessionType";
    MineService mineService;

    // 对于华为推送，这个方法并不能保证一定会回调
    @Override
    public boolean onNotificationClicked(Context context, Map<String, String> payload) {
        mineService = ARouter.getInstance().navigation(MineService.class);
        String sessionId = payload.get(PAYLOAD_SESSION_ID);
        String type = payload.get(PAYLOAD_SESSION_TYPE);
        String params = payload.get("params");
        if (RomUtils.isHuawei() && !TextUtils.isEmpty(params)) {
            JSONObject jsonObject;
            try {
                assert params != null;
                jsonObject = new JSONObject(params);
                sessionId = jsonObject.getString(PAYLOAD_SESSION_ID);
                type = jsonObject.getString(PAYLOAD_SESSION_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.equals(Constants.ACTIVITY_NOTICE, sessionId)) {
            mineService.trackEvent("topPush", "click", "","",new ResponseObserver<BaseResponse<Boolean>>() {
                @Override
                public void onSuccess(BaseResponse<Boolean> response) {
                }
            });
        }
        //
        if (sessionId != null) {
            int typeValue = Integer.valueOf(type);
            ArrayList<IMMessage> imMessages = new ArrayList<>();
            IMMessage imMessage = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
            imMessages.add(imMessage);
            Intent notifyIntent = new Intent();
            notifyIntent.setComponent(initLaunchComponent(context));
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notifyIntent.setAction(Intent.ACTION_VIEW);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 必须
            notifyIntent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessages);

            context.startActivity(notifyIntent);
            return true;
        } else {
            return false;
        }
    }

    private ComponentName initLaunchComponent(Context context) {
        ComponentName launchComponent;
        StatusBarNotificationConfig config = NimCache.getNotificationConfig();
        Class<? extends Activity> entrance = config.notificationEntrance;
        if (entrance == null) {
            launchComponent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        } else {
            launchComponent = new ComponentName(context, entrance);
        }
        return launchComponent;
    }

    // 将音视频通知 Notification 缓存，清除所有通知后再次弹出 Notification，避免清除之后找不到打开正在进行音视频通话界面的入口
    @Override
    public boolean cleanMixPushNotifications(int pushType) {
        Context context = NimCache.getContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
        return true;
    }
}
