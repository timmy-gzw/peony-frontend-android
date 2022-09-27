package com.tftechsz.im.mvp.presenter;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;
import static com.umeng.socialize.utils.ContextUtil.getPackageName;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.DownloadHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tencent.qgame.animplayer.AnimConfig;
import com.tencent.qgame.animplayer.AnimView;
import com.tencent.qgame.animplayer.inter.IAnimListener;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.CallStatusInfo;
import com.tftechsz.im.mvp.iview.ICallView;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.entity.IntegralDto;
import com.tftechsz.common.entity.RechargeQuickDto;
import com.tftechsz.common.event.MessageCallEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.PermissionUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.GiftPopWindow;
import com.tftechsz.common.widget.pop.RechargeBeforePop;
import com.tftechsz.mine.api.MineApiService;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CallPresenter extends BasePresenter<ICallView> {

    private final ChatApiService service;
    private final ChatApiService payService;
    private final ChatApiService imService;
    private final ChatApiService imService2;
    private RechargePopWindow rechargePopWindow;
    private GiftPopWindow giftPopWindow;
    // private List<GiftDto> mGiftList;  //礼物列表
    private RechargeQuickDto mRechargeDto;  //充值列表
    private final Queue<GiftDto> myGiftList = new ConcurrentLinkedQueue<>();
    private SVGAParser svgaParser;
    private SVGAParser.ParseCompletion mParseCompletionCallback;
    private final int EVENT_MESSAGE = 10000;
    private Handler safeHandle;
    private boolean mIsDown = false;
    private CustomPopWindow permissionPop;
    private final MineService mineService;
    private final UserProviderService userService;
    private RechargeBeforePop beforePop;
    public MineApiService mineApiService;


    public CallPresenter() {
        mineApiService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        imService = RetrofitManager.getInstance().createIMApi(ChatApiService.class);
        imService2 = RetrofitManager.getInstance().createIMApi2(ChatApiService.class);
        payService = RetrofitManager.getInstance().createConfigApi(ChatApiService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        userService = ARouter.getInstance().navigation(UserProviderService.class);
    }

    /**
     * 获取他人用户信息
     */
    public void getUserInfoById(String userId) {
        addNet(mineApiService.getUserInfoById(userId).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (getView() == null) return;
                        getView().getUserInfoSuccess(response.getData());
                    }
                }));
    }

    /**
     * 获取聊天中用户信息
     */
    public void getImUserInfo(String uid) {
        addNet(service.getImUserInfo(uid).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<UserInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<UserInfo>> response) {
                        getView().getChatUserInfo(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));


    }

    /**
     * 统计电话进入
     */
    public void buriedPoint(String scene, String event, String index, String extend) {
        mineService.trackEvent(scene, event, index, extend, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
            }
        });
    }


    /**
     * 语音匹配选项
     */
    public void voiceAction(String from, String to, String action) {
        addNet(service.voiceAction(from, to, action).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 视频匹配选项
     */
    public void videoAction(String from, String to, String action) {
        addNet(service.videoAction(from, to, action).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 发送自定义通知
     *
     * @param sessionId
     * @param isFaceOn
     */
    public void sendNotification(String sessionId, boolean isFaceOn) {
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(sessionId);
        notification.setSessionType(SessionTypeEnum.P2P);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        JSONObject json = new JSONObject();
        json.put("data", isFaceOn);
        notification.setContent(json.toString());
        // 若接收者不在线，则当其再次上线时，将收到该自定义系统通知。若设置为 true，则再次上线时，将收不到该通知。
        notification.setSendToOnlineUserOnly(true);
        // 配置 CustomNotificationConfig
        CustomNotificationConfig config = new CustomNotificationConfig();
        // 需要推送
        config.enablePush = false;
        config.enableUnreadCount = false;
        notification.setConfig(config);
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

    /**
     * 发送自定义通知
     * type 0:用户接收通话   1:拒绝通话  2:进入通话了 4:服务器返回挂断
     */
    public void sendNotification(String sessionId, int type) {
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(sessionId);
        notification.setSessionType(SessionTypeEnum.P2P);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        JSONObject json = new JSONObject();
        json.put("call", type);
        notification.setContent(json.toString());
        // 若接收者不在线，则当其再次上线时，将收到该自定义系统通知。若设置为 true，则再次上线时，将收不到该通知。
        notification.setSendToOnlineUserOnly(true);
        // 配置 CustomNotificationConfig
        CustomNotificationConfig config = new CustomNotificationConfig();
        // 需要推送
        config.enablePush = false;
        config.enableUnreadCount = false;
        notification.setConfig(config);
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }


    /**
     * 获取是否拥有权限去通话
     */
    public void getCurrentCoin(String callId) {
        addNet(imService.checkCallStatus(callId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CallStatusInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<CallStatusInfo> response) {
                        if (null != response.getData() && null != getView()) {
                            getView().getCallUserInfoSuccess(response.getData());
                        } else {
                            getCurrentCoin(callId);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null != getView()) {
                            if (code == 10000) {
                                getView().getCallUserInfoNoNet();
                            } else {
                                getView().hungUp();
                            }
                        } else {
                            getCurrentCoin(callId);
                        }

                    }
                }));
    }


    /**
     * 再次获取是否拥有权限去通话
     */
    public void getCurrentAgainCoin(String callId) {
        addNet(imService2.checkCallStatus(callId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CallStatusInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<CallStatusInfo> response) {
                        if (null != response.getData() && null != getView()) {
                            getView().getCallUserInfoSuccess(response.getData());
                        } else {
                            getCurrentAgainCoin(callId);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        if (null != getView()) {
                            if (code == 10000) {
                                getView().getCallUserInfoNoNetAgain();
                            } else {
                                getView().hungUpAgain();
                            }
                        } else {
                            getCurrentAgainCoin(callId);
                        }

                    }
                }));
    }


    /**
     * 检测男生是否又钱通话
     */
    public void checkAcceptCheck(InviteParamBuilder paramBuilder, String account,String callId) {
        addNet(service.checkAcceptCheck(callId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<CallStatusInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<CallStatusInfo> response) {
                        if (null != getView()) {
                            getView().checkAcceptCheckSuccess(paramBuilder, account, response.cmd_data);
                        }
                    }
                }));
    }


    /* *//**
     * 获取礼物数据
     *//*
    public void getGift(Activity activity, String to) {
        mCompositeDisposable.add(new RetrofitManager().createExchApi(ChatApiService.class).getGift(2).compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (getView() == null) return;
                        if (!TextUtils.isEmpty(response.getData())) {
                            try {
                                String key = response.getData().substring(0, 6);
                                byte[] data = Base64.decodeBase64(response.getData().substring(6).getBytes());
                                String iv = MD5Util.toMD532(key).substring(0, 16);
                                byte[] jsonData = AesUtil.AES_cbc_decrypt(data, MD5Util.toMD532(key).getBytes(), iv.getBytes());
                                LogUtil.e("------------", new String(jsonData));
                                mGiftList = JSON.parseObject(new String(jsonData), new TypeReference<List<GiftDto>>() {
                                });
                                showGiftPopSecond(activity, to, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }*/

    /**
     * 获取个人金币
     */
    private void getCoin() {
        userService.getField("property", "coin", new ResponseObserver<BaseResponse<IntegralDto>>() {
            @Override
            public void onSuccess(BaseResponse<IntegralDto> response) {
                if (response.getData() != null) {
                    UserInfo userInfo = userService.getUserInfo();
                    userInfo.setCoin(response.getData().coin);
                    userService.setUserInfo(userInfo);
                    if (giftPopWindow != null) {
                        giftPopWindow.setCoin(response.getData().coin);
                    }
                }
            }
        });
    }


    /**
     * 显示充值列表
     */
    public void showRechargePop(Activity activity, int from_type, int scene, String userId) {
        if (userService.getConfigInfo() != null && userService.getConfigInfo().share_config != null && userService.getConfigInfo().share_config.is_limit_from_channel == 1) {
            if (beforePop == null)
                beforePop = new RechargeBeforePop(BaseApplication.getInstance());
            beforePop.addOnClickListener(new RechargeBeforePop.OnSelectListener() {
                @Override
                public void recharge() {
                    if (null == rechargePopWindow)
                        rechargePopWindow = new RechargePopWindow(activity, from_type, scene, 0, 0, userId);
                    rechargePopWindow.getCoin();
                    rechargePopWindow.requestData();
                    rechargePopWindow.showPopupWindow();
                }
            });
            beforePop.showPopupWindow();
        } else {
            if (null == rechargePopWindow)
                rechargePopWindow = new RechargePopWindow(activity, from_type, scene, 0, 0, userId);
            rechargePopWindow.getCoin();
            rechargePopWindow.requestData();
            rechargePopWindow.showPopupWindow();
        }
    }


    /**
     * 显示礼物弹窗
     */
    public void getGiftData(Activity activity, String to, int scene) {
        showGiftPopSecond(activity, to, scene);
    }

    /**
     * 显示礼物弹窗
     */
    private void showGiftPopSecond(Activity activity, String to, int scene) {
        getCoin();
        if (null == giftPopWindow)
            giftPopWindow = new GiftPopWindow(activity, 3, scene, 0);
        giftPopWindow.setUserIdType(to, 1, "");
        giftPopWindow.setCoin(userService.getUserInfo().getCoin());
        giftPopWindow.addOnClickListener(new GiftPopWindow.OnSelectListener() {
            @Override
            public void sendGift(GiftDto data, int num, List<String> toMember, String name) {
                ChatMsgUtil.sendGiftMessage(SessionTypeEnum.P2P, String.valueOf(userService.getUserId()), to, data.is_choose_num, data.cate, data.tag_value, data.id, data.coin, data.name, data.image, data.animationType, data.animation, data.animations, data.combo, "", num, "p2p", "", null, new ChatMsgUtil.OnMessageListener() {
                    @Override
                    public void onGiftListener(int code, IMMessage message) {
                        if (code == 200) {
                            RxBus.getDefault().post(new MessageCallEvent(message));
//                            try {
//                                ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
//                                if (chatMsg != null && TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
//                                    showGift(message, chatMsg, 0);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                });
            }

            @Override
            public void getMyCoin() {
                getCoin();
            }

            @Override
            public void atUser(List<String> userId, String name) {

            }

            @Override
            public void upOrDownSeat(int userId, boolean isOnSeat) {

            }

            @Override
            public void muteVoice(int userId, int voiceStatus) {

            }
        });
        //giftPopWindow.showPopupWindow();
    }

    /**
     * 发送礼物成功
     */
    public void sendGiftSuccess(GiftDto data, IMMessage message, List<String> toMember) {
        try {
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
            if (chatMsg != null && TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
                showGift(message, chatMsg, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (giftPopWindow != null) {
            giftPopWindow.sendGiftSuccess(data);
        }
    }

    /**
     * 显示礼物动画
     *
     * @param type 发送礼物 0 : 发  1 ： 收
     */
    public void showGift(IMMessage message, ChatMsg chatMsg, int type) {
        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
        if (gift != null && gift.gift_info != null) {
            if (gift.gift_info.animations != null && !gift.gift_info.animations.isEmpty()) {
                for (String ani : gift.gift_info.animations) {
                    addOffer(message, type, gift, ani);
                }
            } else {
                addOffer(message, type, gift, gift.gift_info.animation);
            }
        }

    }

    private void addOffer(IMMessage message, int type, ChatMsg.Gift gift, String ani) {
        GiftDto bean = new GiftDto();
        bean.group = gift.gift_num;
        bean.name = Utils.getFileName(ani).replace(".svga", "");
        bean.image = gift.gift_info.image;
        bean.animation = ani;
        if (type == 0) {
            bean.headUrl = userService.getUserId() + "";
        } else {
            bean.headUrl = message.getFromAccount();
        }
        if (message != null) {
            if (type == 0) {
                bean.toUserName = UserInfoHelper.getUserDisplayName(message.getSessionId());
            } else {
                bean.toUserName = UserInfoHelper.getUserDisplayName(userService.getUserId() + "");
            }
            bean.userName = message.getFromNick();
        }
        if (gift.gift_info.animationType == 1) {    //动效类型:1.普通PNG 2.炫 3.动
            if (getView() == null) return;
            getView().showGiftAnimation(bean);
        } else {
            myGiftList.offer(bean);
            if (giftPopWindow != null && type == 0)
                giftPopWindow.dismiss();
        }
    }

    public void startThread(Activity activity, SVGAImageView svgaImageView, AnimView animView) {
        init(activity, svgaImageView, animView);
        if (null == safeHandle) {
            safeHandle = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == EVENT_MESSAGE) {
                        try {
                            playGift();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        safeHandle.sendEmptyMessageDelayed(EVENT_MESSAGE, 1000);
                    }
                }
            };
        }
        if (safeHandle.hasMessages(EVENT_MESSAGE)) return;
        safeHandle.sendEmptyMessage(EVENT_MESSAGE);
    }

    private AnimView animView;
    private SVGAImageView svgaImageView;
    private boolean mIsPlay;

    private void init(Activity activity, SVGAImageView svgaImageView, AnimView animView) {
        this.animView = animView;
        this.svgaImageView = svgaImageView;

        if (null == svgaParser)
            svgaParser = new SVGAParser(activity);
        if (mParseCompletionCallback == null) {
            mParseCompletionCallback = new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                    svgaImageView.setVisibility(View.VISIBLE);
                    svgaImageView.setVideoItem(videoItem);
                    svgaImageView.stepToFrame(0, true);
                }

                @Override
                public void onError() {
                    mIsPlay = false;
                }
            };
        }
        svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {
                mIsPlay = false;
                svgaImageView.setVisibility(View.GONE);
            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int frame, double percentage) {

            }
        });
        animView.setAnimListener(new IAnimListener() {
            @Override
            public boolean onVideoConfigReady(@NonNull AnimConfig animConfig) {
                return true;
            }

            @Override
            public void onVideoStart() {

            }

            @Override
            public void onVideoRender(int i, @Nullable AnimConfig animConfig) {

            }

            @Override
            public void onVideoComplete() {
                mIsPlay = false;
                animView.post(() -> animView.setVisibility(View.GONE));
            }

            @Override
            public void onVideoDestroy() {

            }

            @Override
            public void onFailed(int i, @Nullable String s) {
                mIsPlay = false;
            }
        });
    }


    private void playGift() {
        if (mIsDown || mIsPlay || myGiftList.isEmpty())
            return;

        GiftDto data = myGiftList.peek();
        if (data == null) {
            return;
        }
        File file = new File(DownloadHelper.FILE_PATH + File.separator + data.name);
        myGiftList.poll();
        if (file.exists()) {
            playAnimation(file, data);
        } else {
            DownloadHelper.downloadGift(data.animation, new DownloadHelper.DownloadListener() {
                @Override
                public void completed() {
                    mIsDown = false;
                    playAnimation(file, data);
                }

                @Override
                public void failed() {
                    mIsDown = false;
                }

                @Override
                public void onProgress(int progress) {
                    mIsDown = true;
                }
            });
        }
    }

    private void playAnimation(File file, GiftDto data) {
        mIsPlay = true;
        if (file.getAbsolutePath().endsWith(".mp4")) {
            animView.setVisibility(View.VISIBLE);
            animView.startPlay(file);
            return;
        }
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            svgaParser.decodeFromInputStream(bis, file.getAbsolutePath(), mParseCompletionCallback, true, null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void showPermission(Activity activity) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setTitle("权限设置");
        permissionPop.setContent(BaseApplication.getInstance().getString(com.tftechsz.common.R.string.chat_open_camera_permission));
        permissionPop.setLeftButton("知道了");
        permissionPop.setRightButton("去设置");
        permissionPop.setIsDismiss(false);
        permissionPop.setOutSideDismiss(false);
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
                if (getView() == null) return;
                getView().cancel();
            }

            @Override
            public void onSure() {
                if (getView() == null) return;
                getView().cancel();
                PermissionUtil.gotoPermission(BaseApplication.getInstance());
            }
        });
        if (!permissionPop.isShowing())
            permissionPop.showPopupWindow();
    }

    public void showAlertPermission(Activity activity) {
        if (permissionPop == null)
            permissionPop = new CustomPopWindow(activity);
        permissionPop.setTitle("权限设置");
        permissionPop.setContent("需要取得权限以使用悬浮窗");
        permissionPop.setLeftButton("知道了");
        permissionPop.setRightButton("去设置");
        permissionPop.setIsDismiss(false);
        permissionPop.setOutSideDismiss(false);
        permissionPop.addOnClickListener(new CustomPopWindow.OnSelectListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onSure() {
                permissionPop.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        if (!permissionPop.isShowing())
            permissionPop.showPopupWindow();
    }

    public void permissionPopDismiss() {
        if (permissionPop != null)
            permissionPop.dismiss();
    }


    @Override
    public void detachView() {
        super.detachView();
        permissionPopDismiss();
        myGiftList.clear();
        safeHandle.removeMessages(EVENT_MESSAGE);
    }
}
