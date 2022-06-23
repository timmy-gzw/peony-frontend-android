package com.netease.nim.uikit.common.ui.imageview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.superteam.SuperTeam;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by huangjun on 2015/11/13.
 */
public class AvatarImageView extends AppCompatImageView {

    public static final int DEFAULT_AVATAR_THUMB_SIZE = (int) NimUIKit.getContext().getResources()
            .getDimension(
                    R.dimen.avatar_max_size);

    public static final int DEFAULT_AVATAR_NOTIFICATION_ICON_SIZE = (int) NimUIKit.getContext()
            .getResources()
            .getDimension(
                    R.dimen.avatar_notification_size);

    private final int DEFAULT_RADIUS = 7;
    private static final int DEFAULT_AVATAR_RES_ID = R.drawable.ic_default_avatar;
    private String configInfo;
    private final static String CONFIG_INFO_KEY = "configInfo";
    public Context context;
    private Gson gson;
    private boolean is_round;

    public AvatarImageView(Context context) {
        super(context);
        this.context = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }


    public ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(configInfo)) {
            configInfo = sp.getString(CONFIG_INFO_KEY, "");
        }
        if (gson == null)
            gson = new Gson();
        ConfigInfo configInfo = gson.fromJson(this.configInfo, ConfigInfo.class);
        return configInfo;
    }


    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param url 头像地址
     */
    public void loadAvatar(String url) {
        if (null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
            url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url;
        changeUrlBeforeLoad(null, url, DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_RADIUS);
    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param url 头像地址
     */
    public void loadAvatar(String roomId, String url) {
        if (null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
            url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url;
        changeUrlBeforeLoad(roomId, url, DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_RADIUS);
    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param url 头像地址
     */
    public void loadAvatar(String url, int radius) {
        if (null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
            url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url;
        changeUrlBeforeLoad(null, url, DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, radius);
    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param account 用户账号
     */
    public void loadBuddyAvatar(String account) {
        if (null == context)
            return;
        try {
            final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
            String url = "";
            if (userInfo != null && null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
                url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url + userInfo.getAvatar();
            changeUrlBeforeLoad(null, userInfo != null ? url : null,
                    DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_RADIUS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setRoundIcon(boolean is_round) {
        this.is_round = is_round;
    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param account 用户账号
     */
    public void loadPartyChatListAvatar(String account) {
        if (null == context)
            return;
        try {
            final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
            String url = "";
            if (userInfo != null && null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
                url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url + userInfo.getAvatar();
            loadImage(userInfo != null ? url : null, DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, 360);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param account 用户账号
     */
    public void loadBuddyAvatar(String account, int radius) {
        if (null == context)
            return;
        try {
            final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
            String url = "";
            if (userInfo != null && null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
                url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url + userInfo.getAvatar();
            changeUrlBeforeLoad(null, userInfo != null ? url : null,
                    DEFAULT_AVATAR_RES_ID, DEFAULT_AVATAR_THUMB_SIZE, radius);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载用户头像（默认大小的缩略图）
     *
     * @param message 消息
     */
    public void loadBuddyAvatar(IMMessage message) {
        String account = message.getFromAccount();
        if (message.getMsgType() == MsgTypeEnum.robot) {
            RobotAttachment attachment = (RobotAttachment) message.getAttachment();
            if (attachment.isRobotSend()) {
                account = attachment.getFromRobotAccount();
            }
        }
        loadBuddyAvatar(account);
    }

    /**
     * 加载群头像（默认大小的缩略图）
     *
     * @param team 群
     */
    public void loadTeamIconByTeam(final Team team) {
        changeUrlBeforeLoad(null, team != null ? team.getIcon() : null, R.drawable.nim_avatar_group,
                DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_RADIUS);
    }

    /**
     * 加载群头像（默认大小的缩略图）
     *
     * @param team 群
     */
    public void loadSuperTeamIconByTeam(final SuperTeam team) {
        changeUrlBeforeLoad(null, team != null ? team.getIcon() : null, R.drawable.nim_avatar_group,
                DEFAULT_AVATAR_THUMB_SIZE, DEFAULT_RADIUS);
    }


    /**
     * 如果图片是上传到云信服务器，并且用户开启了文件安全功能，那么这里可能是短链，需要先换成源链才能下载。
     * 如果没有使用云信存储或没开启文件安全，那么不用这样做
     */
    private void changeUrlBeforeLoad(String roomId, final String url, final int defaultResId,
                                     final int thumbSize, int radius) {
        if (TextUtils.isEmpty(url)) {
            // avoid useless call
            loadImage(url, defaultResId, thumbSize, radius);
        } else {
            /*
             * 若使用网易云信云存储，这里可以设置下载图片的压缩尺寸，生成下载URL
             * 如果图片来源是非网易云信云存储，请不要使用NosThumbImageUtil
             */
//            NIMClient.getService(NosService.class).getOriginUrlFromShortUrl(url).setCallback(
//                    new RequestCallbackWrapper<String>() {
//
//                        @Override
//                        public void onResult(int code, String result, Throwable exception) {
//                            if (TextUtils.isEmpty(result)) {
//                                result = url;
//                            }
//                            final String thumbUrl = makeAvatarThumbNosUrl(result, thumbSize);
//                            loadImage(thumbUrl, defaultResId, thumbSize);
//                        }
//                    });
            loadImage(url, defaultResId, thumbSize, radius);
        }
    }

    /**
     * ImageLoader异步加载
     */
    private void loadImage(final String url, final int defaultResId, final int thumbSize, int radius) {
        if (null == getContext() || null == context)
            return;
        Glide.with(getContext().getApplicationContext()).load(url)
                .dontAnimate()
                .apply(new RequestOptions().placeholder(defaultResId).error(defaultResId).centerCrop()
                        .transform(new GlideRoundTransform(getContext(), is_round ? 360 : radius)).override(thumbSize,
                                thumbSize)).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this);
    }

    /**
     * ImageLoader异步加载
     */
    private void loadImage(final String url, final int defaultResId, final int thumbSize) {
        if (null == getContext() || null == context)
            return;
        Glide.with(getContext().getApplicationContext()).load(url)
                .dontAnimate()
                .apply(new RequestOptions().placeholder(defaultResId).error(defaultResId).centerCrop()
                        .transform(new GlideRoundTransform(getContext(), 6)).override(thumbSize,
                                thumbSize)).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this);
    }

    private RequestBuilder<Drawable> loadTransform(@DrawableRes int placeholderId) {
        return Glide.with(context)
                .load(placeholderId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new GlideRoundTransform(context, 10)));
    }


    /**
     * 解决ViewHolder复用问题
     */
    public void resetImageView() {
        setImageBitmap(null);
    }

    /**
     * 生成头像缩略图NOS URL地址（用作ImageLoader缓存的key）
     */
    private static String makeAvatarThumbNosUrl(final String url, final int thumbSize) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        return thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url,
                NosThumbParam.ThumbType.Crop,
                thumbSize, thumbSize) : url;
    }

    public static String getAvatarCacheKey(final String url) {
        return makeAvatarThumbNosUrl(url, DEFAULT_AVATAR_THUMB_SIZE);
    }
}
