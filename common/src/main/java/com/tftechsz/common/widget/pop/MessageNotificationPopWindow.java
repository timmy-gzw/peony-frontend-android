package com.tftechsz.common.widget.pop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.tftechsz.common.R;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.CommonUtil;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 消息推送pop
 */
public class MessageNotificationPopWindow extends BaseCenterPop {

    private ImageView mIvGif;
    private Context context;

    public MessageNotificationPopWindow(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    private void initUI() {
        setOutSideDismiss(false);
        findViewById(R.id.iv_del).setOnClickListener(v -> dismiss());
        mIvGif = findViewById(R.id.iv_gif);
        if(CommonUtil.isGa()){
            Glide.with(context).load(R.drawable.ga_notification).into(mIvGif);
        }else if (RomUtils.isHuawei()) {
            Glide.with(context).load(R.drawable.gif_notification_huawei).into(mIvGif);
        } else if (RomUtils.isVivo()) {
            Glide.with(context).load(R.drawable.gif_notification_vivo).into(mIvGif);
        } else {
            Glide.with(context).load(R.drawable.gif_notification_xiaomi).into(mIvGif);
        }

        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = manager.getNotificationChannel(Interfaces.CHANNELID);
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, AppUtils.getAppPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                    context.startActivity(intent);
                }
                dismiss();
            }
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(CommonUtil.isGa()?R.layout.pop_message_notification_ga:R.layout.pop_message_notification);
    }
}
