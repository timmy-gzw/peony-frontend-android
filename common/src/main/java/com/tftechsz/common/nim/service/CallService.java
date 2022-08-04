package com.tftechsz.common.nim.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.nim.model.NERTCCallingDelegate;
import com.tftechsz.common.nim.model.NERTCVideoCall;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.Utils;


public class CallService extends Service {
    private static final int NOTIFICATION_ID = 1024;
    private UserProviderService service;
    private NERTCVideoCall nertcVideoCall;
    private NERTCCallingDelegate callingDelegate = new NERTCCallingDelegate() {

        @Override
        public void onError(int errorCode,String uid, String errorMsg) {

        }

        @Override
        public void onInvitedByUser(InvitedEvent invitedEvent,String callId) {
//            CustomInfo customInfo = GsonUtils.fromJson(invitedEvent.getCustomInfo(), CustomInfo.class);
//            if (customInfo != null) {
//                if (customInfo.callType == Utils.GROUP_CALL) {
//                    ARouterUtils.toTeamCallActivity(customInfo.callUserList,customInfo.groupId,invitedEvent);
//                    return;
//                }
//            }
            ARouterUtils.toCallActivity(invitedEvent, service.getCallType(), service.getMatchType(),callId, service.getCallIsMatch());
        }

        @Override
        public void onInvitedByUser(String fromId,String callId) {
            ARouterUtils.toCallActivity(fromId, service.getCallType(),service.getMatchType(),callId, service.getCallIsMatch());
        }


        @Override
        public void onUserEnter(long userId) {

        }

        @Override
        public void onJoinChannel(long channelId) {

        }

        @Override
        public void onUserHangup(long userId) {

        }


        @Override
        public void onRejectByUserId(String userId) {

        }

        @Override
        public void onAcceptByUserId(String userId) {

        }


        @Override
        public void onUserBusy(String userId) {

        }

        @Override
        public void onUserLeave(String userId, String reason) {

        }

        @Override
        public void onCancelByUserId(String userId) {

        }

        @Override
        public void onJoinRoomFailed(int code) {

        }

        @Override
        public void onError(int code) {

        }


        @Override
        public void onCameraAvailable(long userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVideoStop(long userId) {

        }

        @Override
        public void onUserNetworkQuality(NERtcNetworkQualityInfo[] stats) {

        }

        @Override
        public void onAudioAvailable(long userId, boolean isVideoAvailable) {

        }

        @Override
        public void timeOut(int type) {

        }


    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public CallService getService() {
            return CallService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initNERTCCall();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


//    public static boolean isServiceRunning(@NonNull final String className) {
//        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(0x7FFFFFFF);
//        if (info == null || info.size() == 0) return false;
//        for (ActivityManager.RunningServiceInfo aInfo : info) {
//            if (className.equals(aInfo.service.getClassName())) return true;
//        }
//        return false;
//    }
//
//    public static boolean isServiceRunning(@NonNull final Class<?> cls) {
//        return isServiceRunning(cls.getName());
//    }

//    public static void start(Context context) {
//        if (isServiceRunning(CallService.class)) {
//            return;
//        }
//        Intent starter = new Intent(context, CallService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(starter);
//        } else {
//            context.startService(starter);
//        }
//    }

//    public static void stop(Context context) {
//        Intent intent = new Intent(context, CallService.class);
//        context.stopService(intent);
//    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
////        // 获取服务通知
////        Notification notification = createForegroundNotification();
////        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
////        startForeground(NOTIFICATION_ID, notification);
////
////        service = ARouter.getInstance().navigation(UserProviderService.class);
//    }

//    private Notification createForegroundNotification() {
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // 唯一的通知通道的id.
//        String notificationChannelId = "notification_008";
//        // Android8.0以上的系统，新建消息通道
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //用户可见的通道名称
//            String channelName = "芍药";
//            //通道的重要程度
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
//            notificationChannel.setDescription("芍药");
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
//        //通知小图标
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
//        //通知标题
//        builder.setContentTitle(getString(R.string.app_name));
//        //通知内容
//        builder.setContentText("正在运行中");
//        //设定通知显示的时间
//        builder.setWhen(System.currentTimeMillis());
//
//        //创建通知并返回
//        return builder.build();
//    }

    private void initNERTCCall() {
        Utils.runOnUiThread(() -> {
            nertcVideoCall = NERTCVideoCall.sharedInstance();
            nertcVideoCall.addDelegate(callingDelegate);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (nertcVideoCall != null) {
            nertcVideoCall.removeDelegate(callingDelegate);
        }
        NERTCVideoCall.destroySharedInstance();
    }


}
