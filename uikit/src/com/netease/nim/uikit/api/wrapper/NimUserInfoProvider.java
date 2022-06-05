package com.netease.nim.uikit.api.wrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.superteam.SuperTeam;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 初始化sdk 需要的用户信息提供者，现主要用于内置通知提醒获取昵称和头像
 * <p>
 * 注意不要与 IUserInfoProvider 混淆，后者是 UIKit 与 demo 之间的数据共享接口
 * <p>
 */

public class NimUserInfoProvider implements UserInfoProvider {

    private final Context context;
    private String configInfo;
    private final static String CONFIG_INFO_KEY = "configInfo";

    public NimUserInfoProvider(Context context) {
        this.context = context;
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
    public UserInfo getUserInfo(String account) {
        return NimUIKit.getUserInfoProvider().getUserInfo(account);
    }

    @Override
    public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
        /*
         * 注意：这里最好从缓存里拿，如果加载时间过长会导致通知栏延迟弹出！该函数在后台线程执行！
         */
        Bitmap bm = null;
        int defResId = R.drawable.ic_launcher;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] originUrl = new String[1];
        String url = "";
        if (null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
            url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url;
        if (SessionTypeEnum.P2P == sessionType) {
            if(TextUtils.equals(sessionId,"1")||TextUtils.equals(sessionId,"2")){
                originUrl[0] = "";
            }else {
                UserInfo user = getUserInfo(sessionId);
                originUrl[0] = user != null ? (url + user.getAvatar()) : null;
            }
        } else if (SessionTypeEnum.Team == sessionType) {
            Team team = NimUIKit.getTeamProvider().getTeamById(sessionId);
            originUrl[0] = team != null ? (url + team.getIcon()) : null;
        } else if (SessionTypeEnum.SUPER_TEAM == sessionType) {
            SuperTeam team = NimUIKit.getSuperTeamProvider().getTeamById(sessionId);
            originUrl[0] = team != null ? (url + team.getIcon()) : null;
        }
        NIMClient.getService(NosService.class).getOriginUrlFromShortUrl(originUrl[0]).setCallback(
                new RequestCallbackWrapper<String>() {

                    @Override
                    public void onResult(int code, String result, Throwable exception) {
                        originUrl[0] = result;
                        countDownLatch.countDown();
                    }
                });
        try {
            countDownLatch.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(originUrl[0])) {
            bm = NimUIKit.getImageLoaderKit().getNotificationBitmapFromCache(originUrl[0]);
        }
        if (bm == null) {
            if (SessionTypeEnum.Team == sessionType || SessionTypeEnum.SUPER_TEAM == sessionType) {
                defResId = R.drawable.nim_avatar_group;
            }
            Drawable drawable = context.getResources().getDrawable(defResId);
            if (drawable instanceof BitmapDrawable) {
                bm = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        return bm;
    }

    @Override
    public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                   SessionTypeEnum sessionType) {
        String nick = null;
        if (sessionType == SessionTypeEnum.P2P) {
            nick = NimUIKit.getContactProvider().getAlias(account);
        } else if (sessionType == SessionTypeEnum.Team) {
            nick = NimUIKit.getContactProvider().getAlias(account);
            if (TextUtils.isEmpty(nick)) {
                nick = TeamHelper.getTeamNick(sessionId, account);
            }
        }
        if (TextUtils.equals(account, "1") || TextUtils.equals(account, "2")) {
            IMMessage message = NIMClient.getService(MsgService.class).queryLastMessage(account, SessionTypeEnum.P2P);
            if (message != null) {
                Map<String, Object> map = message.getPushPayload();
                if (!TextUtils.isEmpty((String) map.get("pushTitle")))
                    nick = (String) map.get("pushTitle");
            }
        }
        if (TextUtils.isEmpty(nick)) {
            return null; // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
        }
        return nick;
    }
}
