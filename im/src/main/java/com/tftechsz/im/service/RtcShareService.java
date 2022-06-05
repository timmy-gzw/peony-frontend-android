package com.tftechsz.im.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.netease.lava.api.Trace;

import io.reactivex.annotations.Nullable;

@TargetApi(21)
public class RtcShareService extends Service {
    private static final String TAG = "RtcShareService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "RtcShareService";

    private RtcShareBinder mRtcShareBinder;
    private RtcShareNotification mRtcShareNotification;

    public static Intent mediaProjectionIntent = null;

    public RtcShareService() {
        mRtcShareBinder = new RtcShareBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Trace.i(TAG, "onBind ");
        startForeground();
        return mRtcShareBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Trace.i(TAG, "onUnbind");
        stopForeground(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Trace.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_ID);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification() {
        mRtcShareNotification = new RtcShareNotification() {
            @Override
            public Notification getNotification() {

                Intent notificationIntent = new Intent(getApplicationContext(), getApplicationContext().getClass());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                Notification.Builder builder = new Notification.Builder(getApplicationContext())
                        .setContentTitle(CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setContentText(CHANNEL_ID);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(CHANNEL_ID);
                }

                return builder.build();
            }

        };
    }

    public class RtcShareBinder extends Binder {
        public RtcShareService getService() {
            return RtcShareService.this;
        }
    }

    private void startForeground() {

        createNotificationChannel();
        createNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Trace.i(TAG, "SDK Ver:" + Build.VERSION.SDK_INT + " using FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION");
            try {
                startForeground(NOTIFICATION_ID, mRtcShareNotification.getNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
                stopForeground(true);
                startForeground(NOTIFICATION_ID, mRtcShareNotification.getNotification());
            }

        } else {
            startForeground(NOTIFICATION_ID, mRtcShareNotification.getNotification());
        }
    }

}
