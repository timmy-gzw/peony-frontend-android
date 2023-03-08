package com.tftechsz.common.utils;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ConvertUtils;
import com.netease.mobsec.GetTokenCallback;
import com.netease.mobsec.WatchMan;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.NIMAntiSpamOption;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CallbackExt;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.entity.PackageDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.entity.UserViewInfo;
import com.tftechsz.common.entity.WxPayResultInfo;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.RealAuthPopWindow;
import com.tftechsz.common.widget.pop.WithdrawPop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommonUtil {

    /**
     * 设置性别
     *
     * @param sex 性别 1：男 2：女
     * @param age 年龄
     */
    public static void setSexAndAge(Context context, int sex, String age, TextView view) {
        view.setVisibility(View.VISIBLE);
        if (sex == 1) {
            view.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.ic_boy), null, null, null);
            view.setBackgroundResource(R.drawable.bg_boy);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.ic_girl), null, null, null);
            view.setBackgroundResource(R.drawable.bg_girl);
        }
        view.setText(String.valueOf(age));
        view.setGravity(Gravity.CENTER);
        view.setCompoundDrawablePadding(ConvertUtils.dp2px(2));
        view.setTextSize(10);
    }

    /**
     * 设置性别
     *
     * @param sex 性别 1：男 2：女
     * @param age 年龄
     */
    public static void setSexAndAge(String sex, String age, TextView view) {
        view.setVisibility(View.VISIBLE);
        if (TextUtils.equals(sex, "1")) {
            view.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.ic_boy), null, null, null);
            view.setBackgroundResource(R.drawable.bg_boy);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.ic_girl), null, null, null);
            view.setBackgroundResource(R.drawable.bg_girl);
        }
        view.setText(String.valueOf(age));
        view.setGravity(Gravity.CENTER);
        view.setCompoundDrawablePadding(ConvertUtils.dp2px(2));
        view.setTextSize(10);
    }


    /**
     * 获取反作弊token
     */
    public static void getToken(OnSelectListener listener) {
        WatchMan.setSeniorCollectStatus(true);  // 开启传感器数据采集
        WatchMan.getToken(new GetTokenCallback() {
            @Override
            public void onResult(int code, String msg, String Token) {
                MMKVUtils.getInstance().encode(Constants.YUNDUN_TOKEN, Token);
                if (listener != null)
                    listener.onSure();
            }
        });
        WatchMan.setSeniorCollectStatus(false); //提交token后，关闭传感器数据采集
    }

    /**
     * 判断是否是ga环境
     * @return
     */
    public static boolean isGa() {
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        return isGa(service);
    }

    /**
     * 判断是否是ga环境
     * @param service
     * @return
     */
    public static boolean isGa(UserProviderService service) {
        return (service == null || service.getConfigInfo() == null || service.getConfigInfo().sys == null || service.getConfigInfo().sys.is_verified == 0);
    }
    /**
     * 跳转群聊页面
     */
    public static void startTeamChatActivity(Activity activity, String teamId) {
        ARouterUtils.toChatTeamActivity(teamId, NimUIKit.getCommonTeamSessionCustomization(), null);
    }

    /**
     * 获取图片url地址
     */
    public static String getHttpUrlHead() {
        String url = "";
        String config = (String) SPUtils.get("configInfo", "");
        ConfigInfo configInfo = JSON.parseObject(config, ConfigInfo.class);
        if (null != configInfo && null != configInfo.api && null != configInfo.api.oss && null != configInfo.api.oss.cdn)
            url = configInfo.api.oss.cdn_scheme + configInfo.api.oss.cdn.user + url;
        return url;
    }

    /**
     * 设置点击位置和数据
     */
    public static void computeBoundsBackward(RecyclerView recyclerView, List<UserViewInfo> userViewInfos, int firstCompletelyVisiblePos) {
        for (int i = firstCompletelyVisiblePos; i < userViewInfos.size(); i++) {
            View itemView = recyclerView.getChildAt(i - firstCompletelyVisiblePos);
            Rect bounds = new Rect();
            if (itemView != null) {
                ImageView thumbView = itemView.findViewById(R.id.iv1);
                thumbView.getGlobalVisibleRect(bounds);
            }
            userViewInfos.get(i).setBounds(bounds);
        }
    }


    /**
     * 女生第一次搭讪弹窗
     */
    public static void showFirstAccostPop() {
        SPUtils.put(Constants.IS_FIRST_ACCOST, 1);
        CustomPopWindow popWindow = new CustomPopWindow(BaseApplication.getInstance());
        popWindow.setContent("主动搭讪和私信，将由女生支付金币。");
        popWindow.setRightButton("我知道了");
        popWindow.setRightGone();
        popWindow.showPopupWindow();

    }

    public interface OnSelectListener {

        void onSure();

    }

    public OnSelectListener listener;

    /**
     * 获取微信APP_ID
     *
     * @return
     */
    public static String getWeChatAppId(ConfigInfo configInfo) {
        String appId = Constants.WX_APP_ID;
        if (null != configInfo && null != configInfo.api && null != configInfo.api.wechat) {
            appId = configInfo.api.wechat.appid;
        }
        return appId;
    }


    /**
     * 设置通知消息
     */
    public static String getMessage(IMMessage message) {
        String content = null;
        try {
            if (null != message && !TextUtils.isEmpty(message.getContent())) {
                if (TextUtils.equals(ChatMsg.CALL_TYPE, message.getContent())) {
                    ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                   /* if (chatMsg != null) {
                        if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {
                            if (TextUtils.equals(ChatMsg.CALL_TYPE_VIDEO, chatMsg.cmd)) {
                                content = "[视频通话]";
                            } else if (TextUtils.equals(ChatMsg.CALL_TYPE_VOICE, chatMsg.cmd)) {
                                content = "[语音通话]";
                            }
                        }
                    } else*/
                    if (TextUtils.equals(ChatMsg.ACCOST_TYPE, message.getContent())) {
                        content = BaseApplication.getInstance().getString(R.string.message_accost);
                    } else if (TextUtils.equals(ChatMsg.TIP_TYPE, message.getContent())) {
                        content = "[提示消息]";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message != null && !TextUtils.isEmpty(message.getContent()) && message.getContent().contains("，<tag")) {
            //你刚刚漏接了哈哈啊图的来电，快点给Ta回电哦，<tag url="peony://chatActivity/655">点击前往>></tag>
            String msg = message.getContent();
            content = msg.substring(0, msg.indexOf("<tag"));
            content += msg.substring(msg.indexOf(">") + 1, msg.indexOf("</tag>"));
        }
        return content;
    }


    /**
     * 获取最近消息
     */
    public static String getMessage(Context context, IMMessage message, String userId, String fromAccount, TextView tvContent, ImageView ivRedPacket) {
        String content = "";
        try {
            if (null != message) {
                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
                if (chatMsg != null) {
                    if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {
                        if (TextUtils.equals(ChatMsg.CALL_TYPE_VIDEO, chatMsg.cmd) || TextUtils.equals(ChatMsg.CALL_TYPE_VIDEO_MATCH, chatMsg.cmd)) {
                            content = "[视频通话]";
                        } else if (TextUtils.equals(ChatMsg.CALL_TYPE_VOICE, chatMsg.cmd) || TextUtils.equals(ChatMsg.CALL_TYPE_VOICE_MATCH, chatMsg.cmd)) {
                            content = "[语音通话]";
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_TYPE)) {
                        if (TextUtils.equals(chatMsg.cmd, ChatMsg.REPLY_ACCOST_TYPE)) {
                            List<ChatMsg.AccostGift> accostGift = JSONObject.parseArray(chatMsg.content, ChatMsg.AccostGift.class);
                            if (accostGift != null && accostGift.size() > 0) {
                                content = "对方发来" + "[" + accostGift.get(0).name + "]";
                            }
                        } else {
                            if (!TextUtils.equals(userId, fromAccount)) {
                                content = context.getString(R.string.message_accost);
                                if (ivRedPacket != null)
                                    ivRedPacket.setVisibility(View.VISIBLE);
                                tvContent.setTextColor(context.getResources().getColor(R.color.red));
                            } else {
                                ChatMsg.Accost accost = JSON.parseObject(chatMsg.content, ChatMsg.Accost.class);
                                content = "送给Ta" + "[" + accost.gift_info.name + "]";
                            }
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.TIP_TYPE)) {
                        ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
                        if (TextUtils.equals(ChatMsg.ACCOST_TYPE, tips.from_type)) {
                            content = context.getString(R.string.message_accost);
                            if (ivRedPacket != null)
                                ivRedPacket.setVisibility(View.VISIBLE);
                            tvContent.setTextColor(context.getResources().getColor(R.color.red));
                        } else if (TextUtils.equals(ChatMsg.ACCOST_EXPIRED_TYPE, tips.from_type)) {   //过期红包
                            content = context.getString(R.string.message_accost);
                        } else if (TextUtils.equals(ChatMsg.ACCOST_LOCATION, tips.from_type)) {   //附近搭讪消息
                            content = tips.des;
                        } else {
                            content = "[提示消息]";
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {   //礼物
                        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
                        if (TextUtils.equals(userId, fromAccount)) {
                            content = "送给Ta" + "[" + gift.gift_info.name + "]";
                        } else {
                            content = "对方发来" + "[" + gift.gift_info.name + "]";
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_CARD)) {   //搭讪卡片信息
                        content = "[" + "卡片信息" + "]";
                    } else {
                        content = "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    public static void checkMsg(ConfigInfo configInfo, String userId, MsgCheckDto data) {
        ARouterUtils.toChatP2PActivity(userId + "", NimUIKit.getCommonP2PSessionCustomization(), null);
    }

    /**
     * 是否显示申请数量
     *
     * @param type 0：显示number  1:显示message
     * @return
     */
    public static boolean isShowApplyNum(int userId, TextView textView, int type) {
        boolean isShow = false;
        String apply = MMKVUtils.getInstance().decodeString(userId + Constants.FAMILY_APPLY);
        if (!TextUtils.isEmpty(apply)) {
            ChatMsg.ApplyMessage applyMessage = JSON.parseObject(apply, ChatMsg.ApplyMessage.class);
            if (applyMessage != null) {
                if (type == 0) {
                    textView.setText(applyMessage.num + "");
                } else if (type == 1) {
                    textView.setText(applyMessage.message);
                } else {
                    textView.setText(applyMessage.num + "条审核消息");
                }
                if (applyMessage.num > 0) {
                    isShow = true;
                }
            }
        }
        return isShow;
    }

    /**
     * 是否显示ait
     *
     * @param
     * @return
     */
    public static void isShowAitNum(int userId, FrameLayout frameLayout, TextView textView, TextView num) {
        String ait = MMKVUtils.getInstance().decodeString(userId + Constants.FAMILY_AIT);
        if (!TextUtils.isEmpty(ait)) {
            ChatMsg.ApplyMessage applyMessage = JSON.parseObject(ait, ChatMsg.ApplyMessage.class);
            if (applyMessage != null) {
                textView.setText(applyMessage.message);
                if (applyMessage.num > 0) {
                    frameLayout.setVisibility(View.VISIBLE);
                    num.setText(applyMessage.num + "");
                    num.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                    num.setVisibility(View.GONE);
                }
            }
        } else {
            frameLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 是否显示动态通知数量
     *
     * @return
     */
    public static boolean isShowTrendNum(TextView textView) {
        boolean isShow = false;
        String apply = (String) SPUtils.get(Constants.BLOG_NOTICE, "");
        if (!TextUtils.isEmpty(apply)) {
            ChatMsg.ApplyMessage applyMessage = JSON.parseObject(apply, ChatMsg.ApplyMessage.class);
            if (applyMessage != null) {
                textView.setText(applyMessage.num + "");
                if (applyMessage.num > 0) {
                    isShow = true;
                }
            }
        }
        return isShow;
    }

    /**
     * 播放json动画
     */
    public static void playJsonAnimation(LottieAnimationView lottieAnimationView, String animation, int type) {
        String[] json = animation.split(",");
        lottieAnimationView.setImageAssetsFolder(Constants.ACCOST_GIFT);
        if (json.length >= 2) {
            lottieAnimationView.setAnimationFromUrl(json[type]);
        }
        lottieAnimationView.setFailureListener(result -> result.printStackTrace());
        lottieAnimationView.playAnimation();
    }

    /**
     * 设置宽度
     */
    public static void setMaskWidth(ImageView imageView) {
        FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        param.width = ScreenUtils.getScreenWidth(BaseApplication.getInstance()) + DensityUtils.dp2px(BaseApplication.getInstance(), 35);
        imageView.setLayoutParams(param);
    }

    private static final List<String> NATIVE_STRING = Arrays.asList(Interfaces.LINK_PEONY_RECHARGE,
            Interfaces.LINK_PEONY_INCOME,
            Interfaces.LINK_PEONY_SETTING,
            Interfaces.LINK_PEONY_VISITOR,
            Interfaces.LINK_PEONY_ACCOST_SETTING,
            Interfaces.LINK_PEONY_HELP_FEEDBACK,
            Interfaces.LINK_PEONY_MY_CERTIFICATION,
            Interfaces.LINK_PEONY_FACIAL,
            Interfaces.LINK_PEONY_MY_OUTFIT,
            Interfaces.LINK_PEONY_MY_NOBLE,
            Interfaces.LINK_PEONY_NOTE_VALUE,
            Interfaces.LINK_PEONY_TREND,
            Interfaces.LINK_PEONY_INVITE
    );


    public static List<ConfigInfo.MineInfo> addMineInfo(List<ConfigInfo.MineInfo> my) {
        List<ConfigInfo.MineInfo> list = new ArrayList<>();
        if (my == null) {
            return list;
        }
        for (ConfigInfo.MineInfo mineInfo : my) {
            if (!TextUtils.isEmpty(mineInfo.link)) {
                if (mineInfo.link.startsWith(Interfaces.LINK_WEBVIEW) //webview
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY_TOAST))//peony://toast
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_FAMILY_HOME)) // peony://family_home
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_CHAT_SQUARE)) // peony://chat_square
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_CREATE_LIVE)) // peony://blind_date_create
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY + Interfaces.LINK_PEONY_DATE_LIST)) // peony://blind_date_list
                        || (mineInfo.link.startsWith(Interfaces.LINK_PEONY) && NATIVE_STRING.contains(mineInfo.link.substring(Interfaces.LINK_PEONY.length())))) {
                    list.add(mineInfo);
                }
            }
        }
        return list;
    }

    public static void performLink(Context context, ConfigInfo.MineInfo info) {
        performLink(context, info, 0, 0);
    }

    public static void performLink(Context context, ConfigInfo.MineInfo info, int bannerIndex, int from_type) {
        if (info == null || info.link == null) {
            return;
        }
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        if (info.link.contains(Interfaces.LINK_PEONY_ACCOST_SETTING) || info.link.contains(Interfaces.LINK_PEONY_RECOMMANDVALUE)
                || info.link.contains(Interfaces.LINK_PEONY_NEWTASK) || info.link.contains(Interfaces.LINK_PEONY_MY_OUTFIT) || info.link.contains(Interfaces.LINK_PEONY_MY_NOBLE))
            if (CommonUtil.hasPerformAccost(service.getUserInfo())) return;
        Utils.logE("link--> " + info.link);
        if (info.link.startsWith(Interfaces.LINK_WEBVIEW)) { // webview
            boolean isShowBar = true;
            String color = "";
            if (info.option != null) {
                if (info.option.is_topbar == 0) {   //是否显示title
                    isShowBar = false;
                }
                color = info.option.topbar_color;   // 顶部title颜色
            }
            BaseWebViewActivity.start(context, info.title, info.link.substring(Interfaces.LINK_WEBVIEW.length()), isShowBar, color, bannerIndex, from_type);
            return;
        }

        if (info.link.startsWith(Interfaces.LINK_BROWSER)) { // browser://
            Uri uri = Uri.parse(info.link.substring(Interfaces.LINK_BROWSER.length()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            return;
        }

        if (info.link.startsWith(Interfaces.LINK_PEONY_TOAST)) {  // peony://toast?
            String toastSubstring = info.link.substring(Interfaces.LINK_PEONY_TOAST.length());
            String[] split = toastSubstring.split("&");// msg=该功能正在完善，敬请期待
            for (String s : split) {
                if (!s.contains("=")) {
                    continue;
                }
                String[] msg = s.split("=");// msg , 该功能正在完善，敬请期待
                if (msg.length != 2) {
                    continue;
                }
                switch (msg[0]) {
                    case "msg":
                        Utils.toast(msg[1], true);
                        break;
                }
            }
            return;
        }

        if (info.link.startsWith(Interfaces.LINK_PEONY_STORE_DOWNLOAD)) { // peony://store_download
            AppUtils.toMarket(context, com.blankj.utilcode.util.AppUtils.getAppPackageName(), null);
            return;
        }

        if (info.link.startsWith(Interfaces.LINK_PEONY)) { // peony://
            String substring = info.link.substring(Interfaces.LINK_PEONY.length());
            if (substring.contains(Interfaces.LINK_PEONY_CHAT_SQUARE)) {//聊天广场
                String rid = substring.substring(Interfaces.LINK_PEONY_CHAT_SQUARE.length());
                String cusRid = rid.replace("/", "");
                ARouterUtils.toChatTeamActivity(cusRid, NimUIKit.getCommonTeamSessionCustomization(), null, 1);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_RECHARGE)) {//充值
                ARouterUtils.toRechargeActivity();
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_INCOME)) {  //收益
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_INTEGRAL_NEW);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_SETTING)) { //设置
                ARouterUtils.toSettingActivity();
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_HELP_FEEDBACK)) {//反馈
                ARouterUtils.toReportActivity(-1, -1, 3);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MY_CERTIFICATION)) {//我的认证
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MY_CERTIFICATION);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_FACIAL)) {//美颜设置
                //Fixme 美颜设置权限申请优化
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    String[] permissions = {Manifest.permission.CAMERA};
                    PermissionUtil.beforeCheckPermission(activity, permissions, agreeToRequest -> {
                        if (agreeToRequest) {
                            new RxPermissions(activity).request(permissions)
                                    .subscribe(aBoolean -> {
                                        if (aBoolean) {
                                            ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FACIAL_SETTING);
                                        } else {
                                            PermissionUtil.showPermissionPop(activity);
                                        }
                                    });
                        } else {
                            PermissionUtil.showPermissionPop(activity);
                        }
                    });
                } else {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FACIAL_SETTING);
                }
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_ACCOST_SETTING)) {//招呼设置
                ARouterUtils.toAccostSettingActivity(info.title);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_ACCOST_SETTING_AUDIO)) {//语音招呼
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_AUDIO);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_ACCOST_SETTING_PIC)) {//相册招呼
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_PIC);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_ACCOST_SETTING_CUSTOMIZE)) {//自定义招呼
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_CUSTOMIZE);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_VISITOR)) {//访客记录
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_VISITOR);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_HOME)) {//首页
                ARouterUtils.toMainActivity();
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MYINFO)) {//我的资料
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_INFO);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MYDETAIL)) {//我的详情
                ARouterUtils.toMineDetailActivity("");
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MYPHOTO)) {//我的相册
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_PHOTO);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_VOICESIGN)) { //语音签名
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_VOICESIGN);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_TREND)) { //动态
                ARouterUtils.toTrendActivity("", "");
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_DATE_LIST)) { //直播房间
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_LIVE_HOME);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_REAL)) { //真人认证
                Utils.isOpenAuth(data -> {
                    if (data) {
                        ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION_NEW);
                    } else {
                        ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION);
                    }
                });
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MY_OUTFIT)) { //我的装扮
                ARouterUtils.toVipDressUp(-1, true);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MY_NOBLE)) { //我的贵族
                ARouterUtils.toNobleActivity();
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_NOTE_VALUE)) { //音符值
//                ARouterUtils.toMineIntegralActivity(1);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_PARTY_SELF_CHECK)) { //派对实名认证
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_SELF_CHECK);
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_MESSAGE_LIST)) { //首页-消息tab页
                int chatIndex = MMKVUtils.getInstance().decodeInt(Constants.KEY_CHAT_TAB_INDEX);
                ARouterUtils.toMainActivity(chatIndex);
                return;
            }
            if (substring.contains(Interfaces.LINK_PEONY_WITHDRAW_POP)) { //提现弹窗 peony://withdraw_pop&type_id=10
                String popSub = substring.substring(Interfaces.LINK_PEONY_WITHDRAW_POP.length());
                String[] split = popSub.replace("?", "").split("&");// msg=该功能正在完善，敬请期待
                for (String s : split) {
                    if (!s.contains("=")) {
                        continue;
                    }
                    String[] msg = s.split("=");// msg , 该功能正在完善，敬请期待
                    if (msg.length != 2) {
                        continue;
                    }
                    if ("type_id".equals(msg[0])) {
                        new WithdrawPop(msg[1]).showPopupWindow();
                        return;
                    }
                }
                return;
            }
            if (substring.equals(Interfaces.LINK_PEONY_ABOUT_US)) { //关于我们
                ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ABOUT_US);
                return;
            }
        }
    }

    public static boolean hasPerformAccost(UserInfo userInfo) {
        return hasPerformAccost(null, !userInfo.isPartyGirl(), userInfo.isPartyGirl(), userInfo);
    }

    public static boolean hasPerformAccost(String tips_msg, boolean is_real_alert, boolean is_self_alert, UserInfo userInfo) {
        if (!TextUtils.isEmpty(tips_msg)) {
            Utils.toast(tips_msg, true);
            return true;
        }
        if (userInfo != null) {
            if (userInfo.isGirl()) {//女性
                if (userInfo.isPartyGirl() && !userInfo.isSelf()) {//如果是派对女用户 && 未实名
                    showRealAuthPopWindow(Interfaces.SHOW_IS_PARTY_SELF);
                    return true;
                }
                if (is_real_alert && !userInfo.isReal()) { //未真人认证
                    showRealAuthPopWindow(Interfaces.SHOW_IS_REAL);
                    return true;
                }
                if (is_self_alert && !userInfo.isSelf()) { //未实名认证
                    showRealAuthPopWindow(Interfaces.SHOW_IS_SELF);
                    return true;
                }
            }
        }
        return false;
    }

    private static void showRealAuthPopWindow(String type) {
        if (AppManager.getAppManager().currentActivity() != null &&
                TextUtils.equals("LoginActivity", AppManager.getAppManager().currentActivity().getClass().getSimpleName())) { //登录界面不显示弹窗
            return;
        }
        RealAuthPopWindow popWindow = new RealAuthPopWindow(BaseApplication.getInstance());
        popWindow.setPopType(type);
        popWindow.showPopupWindow();
    }


    /**
     * 显示自定义弹窗
     *
     * @param alert
     */
    public static void showCustomPop(ChatMsg.Alert alert) {
        CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance(), alert.pop_style);
        if (null != alert.left_button) {
            customPopWindow.setLeftButton(alert.left_button.msg);
        }
        if (null != alert.right_button)
            customPopWindow.setRightButton(alert.right_button.msg);
        else
            customPopWindow.setRightGone();
        customPopWindow.setTitle(alert.title);
        customPopWindow.setContent(alert.des);
//        customPopWindow.setContent("<span>该礼物 <span style=\"color:#FFB951;\">男爵贵族</span> 以上才能赠送，快去了解贵族特权吧~</span>");
        customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                if (alert.left_button.is_finish) {
                    Activity activity = AppManager.getAppManager().currentActivity();
                    if (activity != null && !TextUtils.equals(activity.getClass().getSimpleName(), "MainActivity")) {
                        activity.finish();
                    }
                }
            }

            @Override
            public void onSure() {
                if (null != alert.right_button) {
                    String event = alert.right_button.event;
                    CommonUtil.performLink(AppManager.getAppManager().currentActivity(), new ConfigInfo.MineInfo(event), 0, 0);
                    if (alert.right_button.is_finish) {
                        Activity activity = AppManager.getAppManager().currentActivity();
                        if (activity != null && !TextUtils.equals(activity.getClass().getSimpleName(), "MainActivity")) {
                            activity.finish();
                        }
                    }
                }
            }
        });
        customPopWindow.setOutSideDismiss(alert.is_outside_enable);
        customPopWindow.setBackPressEnable(alert.is_outside_enable);
        customPopWindow.showPopupWindow();
    }


    /**
     * 发送搭讪消息
     *
     * @param userId 对方ID
     * @param type   1:图片 2:语音
     */
    public static void sendAccost(boolean isYiDun, String userId, int subFromTpe, int accostFrom, int type, String url, long duration) {
        String realName = DownloadHelper.getFileNameFromFileUrl(url);
        File file = new File(DownloadHelper.FILE_PATH + File.separator + realName.split("\\.")[0]);
        if (file.exists()) {
            sendAudioOrPicMessage(isYiDun, userId, subFromTpe, accostFrom, type, file, duration);
        } else {
            DownloadHelper.download(url, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    sendAudioOrPicMessage(isYiDun, userId, subFromTpe, accostFrom, type, file, duration);
                }

                @Override
                public void failed() {
                }

                @Override
                public void onProgress(int progress) {
                }
            });
        }
    }


    /**
     * 发送图片和语音
     *
     * @param isYiDun    是否易顿检测
     * @param userId     用户id
     * @param subFromTpe 判断来自哪里 服务端判断是否需要付费
     * @param type       1： 图片  2：语音
     * @param file       文件
     * @param duration   语音时长
     */
    public static void sendAudioOrPicMessage(boolean isYiDun, String userId, int subFromTpe, int accostFrom, int type, File file, long duration) {
        IMMessage message;
        if (type == 1) {
            message = MessageBuilder.createImageMessage(userId, SessionTypeEnum.P2P, file, file.getName());
        } else {
            message = MessageBuilder.createAudioMessage(userId, SessionTypeEnum.P2P, file, duration);
        }
        Map<String, Object> ext = new HashMap<>();
        ext.put("my_sub_type", 1);
        ext.put("accost_from", accostFrom);
        ext.put("sub_from_type", subFromTpe);
        message.setRemoteExtension(ext);
        NIMAntiSpamOption antiSpamOption = new NIMAntiSpamOption();
        antiSpamOption.enable = isYiDun;
        message.setNIMAntiSpamOption(antiSpamOption);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }


    private static final ScheduledExecutorService singleThreadExecutor = Executors.newScheduledThreadPool(5);

    public static void sendAccostGirlBoy(UserProviderService service, int user_id, AccostDto data, int accost_type) {
        if (service.getUserInfo().isGirl()) {
//            Map<Integer, Integer> map = new HashMap<>();
//            Random random = new Random();
//            map.put(1, random.nextInt(100));
//            if (map.size() == 1) {
//                ChatMsgUtil.sendCustomTextMessage(String.valueOf(user_id), data.msg, 1, 1);
//                return;
//            }
//            List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(map.entrySet());
//            Collections.sort(entryList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//            for (Map.Entry<Integer, Integer> entry : entryList) {
//                Utils.logE(entry.getKey() + "   随机数:" + entry.getValue());
//                if (entry.getKey() == 1) {
//                    // singleThreadExecutor.execute(() -> ChatMsgUtil.sendCustomTextMessage(String.valueOf(user_id), data.msg, 1, 1));
//
//                } else if (entry.getKey() == 2) {
//                    //singleThreadExecutor.execute(() -> sendAccost(String.valueOf(user_id), 1, data.picture.url, 0));
//
//                } else if (entry.getKey() == 3) {
//                    //singleThreadExecutor.execute(() -> sendAccost(String.valueOf(user_id), 2, data.voice.url, data.voice.time * 1000));
//                    singleThreadExecutor.schedule(() -> singleThreadExecutor.execute(() -> sendAccost(String.valueOf(user_id), 2, data.voice.url, data.voice.time * 1000)), 100, TimeUnit.MILLISECONDS);
//                }
//            }

            if (null == data) return;

            boolean isYiDun = true;
            if (service.getConfigInfo() != null && service.getConfigInfo().share_config != null)
                isYiDun = service.getConfigInfo().share_config.is_handle_accost_yidun == 1;
            boolean finalIsYiDun = isYiDun;
            if (!TextUtils.isEmpty(data.accost_resume)) {
                singleThreadExecutor.schedule(() -> ChatMsgUtil.sendCardAccostMessage(finalIsYiDun, String.valueOf(service.getUserId()), String.valueOf(user_id), data.accost_resume, data.sub_from_type, data.accost_from, false), 100, TimeUnit.MILLISECONDS);
            }
            if (!TextUtils.isEmpty(data.from_accost_card)) {
                singleThreadExecutor.schedule(() -> ChatMsgUtil.sendCardAccostMessage(finalIsYiDun, String.valueOf(service.getUserId()), String.valueOf(user_id), data.from_accost_card, data.sub_from_type, data.accost_from, true), 100, TimeUnit.MILLISECONDS);
            }
            if (!TextUtils.isEmpty(data.to_accost_card)) {
                singleThreadExecutor.schedule(() -> ChatMsgUtil.sendCardAccostMessage(finalIsYiDun, String.valueOf(service.getUserId()), String.valueOf(user_id), data.to_accost_card, data.sub_from_type, data.accost_from, false), 100, TimeUnit.MILLISECONDS);
            }
            if (!TextUtils.isEmpty(data.msg)) {
                singleThreadExecutor.schedule(() -> ChatMsgUtil.sendCustomTextMessage(finalIsYiDun, String.valueOf(user_id), data.msg, data.sub_from_type, data.accost_from, 1, 1), 200, TimeUnit.MILLISECONDS);
            }
            if (data.picture != null && !TextUtils.isEmpty(data.picture.url)) {
                singleThreadExecutor.schedule(() -> singleThreadExecutor.execute(() -> sendAccost(finalIsYiDun, String.valueOf(user_id), data.sub_from_type, data.accost_from, 1, data.picture.url, 0)), 200, TimeUnit.MILLISECONDS);
            }
            if (data.voice != null && !TextUtils.isEmpty(data.voice.url)) {
                singleThreadExecutor.schedule(() -> singleThreadExecutor.execute(() -> sendAccost(finalIsYiDun, String.valueOf(user_id), data.sub_from_type, data.accost_from, 2, data.voice.url, data.voice.time * 1000)), 200, TimeUnit.MILLISECONDS);
            }
        } else {
            ChatMsgUtil.sendAccostMessage(String.valueOf(service.getUserId()), String.valueOf(user_id), data.gift.id, data.gift.name, data.gift.image, data.gift.animation, data.msg, accost_type, data.accost_from);
        }
    }


    /**
     * @param textview TextView
     * @param name     用户名
     * @param isVip    是否vip
     */
    public static void setUserName(TextView textview, String name, boolean isVip) {
        setUserName(textview, name, isVip, isVip);
    }

    public static void setUserName(TextView textview, String userName, boolean isVip, boolean isSetColor) {
        if (null == textview) {
            return;
        }
        textview.setText(userName);

        if (isVip) {
            textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.vip_icon_bg2, 0);
            textview.setCompoundDrawablePadding(ConvertUtils.dp2px(6));
        } else {
            textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textview.setCompoundDrawablePadding(ConvertUtils.dp2px(0));
        }

        if (isSetColor) {
            textview.setTextColor(Utils.getColor(R.color.vip_color));
        } else {
            textview.setTextColor(Utils.getColor(R.color.color_normal));
        }
    }

    public static void setUserNameNotVip(TextView textview, String userName, boolean isSetColor, boolean isVip, ImageView img) {
        if (null == textview) {
            return;
        }
        textview.setText(userName);
        img.setVisibility(isVip ? View.VISIBLE : View.GONE);
        if (isSetColor) {
            textview.setTextColor(Utils.getColor(R.color.red));
        } else {
            textview.setTextColor(Utils.getColor(R.color.color_normal));
        }
    }

    /**
     * 设置vip 信息
     */
    public static void setVipInfo(NimUserInfo userInfo, String contactId, TextView textView, AvatarVipFrameView ivAvatar) {
        if (userInfo != null && userInfo.getExtension() != null && !TextUtils.isEmpty(userInfo.getExtension()) && !TextUtils.equals("\"\"", userInfo.getExtension())) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(userInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    setUserName(textView, UserInfoHelper.getUserTitleName(contactId, SessionTypeEnum.P2P), vip.is_vip == 1);
                    if (ivAvatar != null) ivAvatar.setBgFrame(vip.picture_frame);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置vip 信息
     */
    public static void setNameColor(NimUserInfo userInfo, String contactId, TextView textView, boolean showVipBadge) {
        if (userInfo != null && userInfo.getExtension() != null && !TextUtils.isEmpty(userInfo.getExtension()) && !TextUtils.equals("\"\"", userInfo.getExtension())) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(userInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    setUserName(textView, UserInfoHelper.getUserTitleName(contactId, SessionTypeEnum.P2P), showVipBadge, vip.is_vip == 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startIntentToAliPay(Context context, String data) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(uri);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, Interfaces.PAY_REQUEST_CODE);
        } else {
            context.startActivity(intent);
        }
    }


    /**
     * SD卡路径+"/hmcp-apkinfos"  爱云兔
     * SD卡路径+"/lgcloud"  蓝光云
     * "/system/app/romex" ch.deletescape.lawnchair.plah 虚拟大师
     * com.redfinger.appstore 红手指云手机
     * com.nenly.agent  打鱼助手
     * com.ld.yunstore  雷电云
     * com.cyjh.huawei.launcher3 多多云
     * com.lz.appmarket 金山云
     */
    public static List<PackageDto> getPackageInfo() {
        List<PackageDto> packageDtoList = new ArrayList<>();
        List<PackageInfo> packageInfoList = AppUtils.getAllPackageInfo();
        if (packageInfoList != null && packageInfoList.size() > 0) {
            for (int i = 0; i < packageInfoList.size(); i++) {
                String pn = packageInfoList.get(i).packageName;
                if (pn.equalsIgnoreCase("ch.deletescape.lawnchair.plah")) {
                    packageDtoList.add(new PackageDto("ch.deletescape.lawnchair.plah", "虚拟大师"));
                }
                if (pn.equalsIgnoreCase("com.redfinger.appstore")) {
                    packageDtoList.add(new PackageDto("com.redfinger.appstoreh", "红手指云手机"));
                }
                if (pn.equalsIgnoreCase("com.nenly.agent")) {
                    packageDtoList.add(new PackageDto("com.nenly.agent", "打鱼助手"));
                }
                if (pn.equalsIgnoreCase("com.ld.yunstore")) {
                    packageDtoList.add(new PackageDto("com.ld.yunstore", "雷电云"));
                }
                if (pn.equalsIgnoreCase("com.cyjh.huawei.launcher3")) {
                    packageDtoList.add(new PackageDto("com.cyjh.huawei.launcher3", "多多云"));
                }
                if (pn.equalsIgnoreCase("com.lz.appmarket")) {
                    packageDtoList.add(new PackageDto("com.lz.appmarket", "金山云"));
                }
            }
        }
        packageDtoList.add(new PackageDto(BaseApplication.getInstance().getPackageName(), AppUtils.isRoot() ? "已root" : "未root"));
        return packageDtoList;
    }


    public static void performReal(RealStatusInfoDto response) { //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
        Utils.isOpenAuth(data -> {
            if (data) {
                if (response.status == -1 || response.status == 2) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION_NEW);
                } else if (response.status == 0) {
                    Utils.toast(R.string.auditing);
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance(), "您已完成真人认证");
                }
            } else {
                if (response.status == -1) {
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_REAL_AUTHENTICATION);
                } else if (response.status == 0 || response.status == 2) {
                    ARouterUtils.toRealAuthentication(response.status, null, response);
                } else {
                    ToastUtil.showToast(BaseApplication.getInstance(), "您已完成真人认证");
                }
            }
        });
    }

    // //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
    public static void performSelf(RealStatusInfoDto data) {
        performSelf(data, ARouterApi.ACTIVITY_REAL, "");
    }

    public static void performSelf(RealStatusInfoDto data, String path, String popType) {
        if (data == null) {
            return;
        }
        Utils.isOpenAuth(datas -> {
            if (datas) {
                if (data.status == -1 || data.status == 2) {
                    ARouterUtils.toPathWithId(path);
                } else if (data.status == 0) {
                    //ARouterUtils.toRealSucessActivity(data.status);
                } else {
                    Utils.toast("您已完成实名认证");
                }
            } else {
                if (data.status == -1) {
                    ARouterUtils.toPathWithId(path);
                } else if (data.status == 0 || data.status == 2) {
                    ARouterUtils.toRealSuccessActivity(data.status, TextUtils.equals(popType, Interfaces.SHOW_IS_PARTY_SELF));
                } else {
                    Utils.toast("您已完成实名认证");
                }
            }
        });
    }

    public static BaseReq performWxReq(WxPayResultInfo wx) {
        if(!TextUtils.isEmpty(wx.getPay_url())){
            openBrowser(wx.getPay_url());
            return null;
        }
        if(TextUtils.equals(wx.getType(),"applet")){
            WXLaunchMiniProgram.Req req1 = new WXLaunchMiniProgram.Req();
            req1.userName = wx.getGh_id(); // 填小程序原始id
            req1.path = wx.getPath();
            req1.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
            return req1;
        }
        PayReq req = new PayReq();
        req.appId = wx.getAppid();
        req.partnerId = wx.getPartnerid();
        req.prepayId = wx.getPrepayid();
        req.nonceStr = wx.getNoncestr();
        req.timeStamp = String.valueOf(wx.getTimestamp());
        req.sign = wx.getSign();
        req.extData = wx.getOrder_no();
        req.packageValue = wx.getPackageX(); //"Sign=WXPay"
        return req;
    }


    public static void openBrowser(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(BaseApplication.getInstance().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
           Utils.toast("链接错误或无浏览器");
        }
    }


    public static String getMetaData(String key) {
        String result = "";
        try {
            ApplicationInfo appInfo = BaseApplication.getInstance().getPackageManager().getApplicationInfo(BaseApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            result = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String getUmengChannel() {
        return getMetaData("UMENG_CHANNEL");
    }

    public static String getUmengAppKey() {
        return getMetaData("UMENG_APPKEY");
    }

    public static String getBuglyAppKey() {
        return getMetaData("BUGLY_APPKEY");
    }

    public static String getUmengPushSecret() {
        return getMetaData("UMENG_PUSH_SECRET");
    }

    /**
     * 首页替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     */
    public static boolean isBtnTextHome(UserProviderService service) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_home == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()));
    }


    /**
     * 相册替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     */
    public static boolean isBtnTextPhoto(UserProviderService service) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_photo == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()));
    }

    /**
     * 一键搭讪替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     */
    public static boolean isBtnTextPop(UserProviderService service) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_muti_pop == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()));
    }

    /**
     * 替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     */
    public static boolean isBtnText1(UserProviderService service) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_new == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()));
    }


    /**
     * 替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     *
     * @param flagIsBoy 动态-看的男性用户才能显示
     */
    public static boolean isBtnTextTrend(UserProviderService service, boolean flagIsBoy) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_blog == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()) && flagIsBoy);
    }

    /**
     * 替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     *
     * @param flagIsBoy 动态-看的男性用户才能显示
     */
    public static boolean isBtnText2(UserProviderService service, boolean flagIsBoy) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_new == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()) && flagIsBoy);
    }

    /**
     * 替换搭讪文字和喜欢图片样式-
     * is_open_behavior_accost_new 开关
     *
     * @param flagIsBoy 动态-看的男性用户才能显示
     */
    public static boolean isBtnTextDetail(UserProviderService service, boolean flagIsBoy) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_open_behavior_accost_user_detail == 1
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()) && flagIsBoy);
    }


    /**
     * 修改资料页面 下部分按钮
     *
     * @param service is_detail_style_new==2和女性登录用户
     */
    public static boolean infoBtnTextUpdate3(UserProviderService service) {
        return (service.getConfigInfo() != null && service.getConfigInfo().share_config != null && service.getConfigInfo().share_config.is_detail_style_new == 2
                && (service.getUserInfo() != null && service.getUserInfo().isGirl()));
    }

    /**
     * 是否含有表情
     */
    public static boolean noContainsEmoji(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isEmojiCharacter(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    public static void performCallbackExt(CallbackExt callbackExt) { //处理服务器错误code吗
        if (callbackExt == null || callbackExt.callbackExt == null) {
            return;
        }
        switch (callbackExt.callbackExt.code) {
            case Interfaces.USER_ITEM_NOT_ENOUGH: // 我的物品数量不足
                Utils.toast(R.string.not_enough_backpacks);
                break;
            case Interfaces.USER_ILLEGAL: // 非派对女用户不可收礼
                Utils.toast("您暂时不能给该用户送礼");
                break;

        }

    }

    /**
     * 获得系统字符
     */
    public static String getOSName() {
        return "android";
    }

    /**
     * 去掉.后面的
     *
     * @param s
     * @return string
     */
    public static String replace(String s) {
        if (null != s && s.indexOf(".") > 0) {
            return s.substring(0, s.indexOf("."));
        }

        return "0";
    }
}
