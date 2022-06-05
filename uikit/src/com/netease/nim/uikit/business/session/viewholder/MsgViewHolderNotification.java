package com.netease.nim.uikit.business.session.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.business.session.helper.TeamNotificationHelper;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;

public class MsgViewHolderNotification extends MsgViewHolderBase {
    private String configInfo;
    private final static String CONFIG_INFO_KEY = "configInfo";

    public MsgViewHolderNotification(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected TextView notificationTextView;

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_notification;
    }

    @Override
    public void inflateContentView() {
        notificationTextView = (TextView) view.findViewById(R.id.message_item_notification_label);
    }

    @Override
    public void bindContentView() {
        handleTextNotification(getDisplayText());
        NotificationAttachment attachment = (NotificationAttachment) message.getAttachment();
        setRlView(false);
        notificationTextView.setVisibility(View.VISIBLE);
        switch (attachment.getType()) {
            case KickMember:
            case SUPER_TEAM_KICK:
            case LeaveTeam:
            case SUPER_TEAM_LEAVE:
//                setRlView(false);
//                notificationTextView.setVisibility(View.GONE);
                break;
        }
//        if (null != getConfig(context) && null != getConfig(context).sys && null != getConfig(context).sys.room_id_list && getConfig(context).sys.room_id_list.size() > 0) {
//            if(getConfig(context).sys.room_id_list.contains(message.getSessionId())){
//                setRlView(false);
//                notificationTextView.setVisibility(View.GONE);
//            }
//        }
    }

    protected String getDisplayText() {
        return TeamNotificationHelper.getTeamNotificationText(message, message.getSessionId());
    }

    private void handleTextNotification(String text) {
        MoonUtil.identifyFaceExpressionAndATags(context, notificationTextView, text, ImageSpan.ALIGN_BOTTOM);
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony_sp",
                Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(configInfo)) {
            configInfo = sp.getString(CONFIG_INFO_KEY, "");
        }
        ConfigInfo configInfo = JSON.parseObject(this.configInfo, ConfigInfo.class);
        return configInfo;
    }


    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}

