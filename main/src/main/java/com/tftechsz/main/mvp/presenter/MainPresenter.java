package com.tftechsz.main.mvp.presenter;

import static com.tftechsz.common.constant.Interfaces.FIY_NUMBER;
import static com.tftechsz.common.constant.Interfaces.RECHARGE_NUMBER;
import static com.tftechsz.common.constant.Interfaces.SCENE_NUMBER;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.ActivityUtils;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.AppManager;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.AlipayResultInfo;
import com.tftechsz.common.entity.IntimacyEntity;
import com.tftechsz.common.entity.MessageInfo;
import com.tftechsz.common.entity.NobilityLevelUpPopDto;
import com.tftechsz.common.entity.QingLangBean;
import com.tftechsz.common.entity.SignInBean;
import com.tftechsz.common.entity.SignInSuccessBean;
import com.tftechsz.common.entity.SystemAccostDto;
import com.tftechsz.common.event.AccostNowEvent;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.UserStatusEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.manager.DbManager;
import com.tftechsz.common.nim.model.impl.NERTCVideoCallImpl;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.MMKVUtils;
import com.tftechsz.common.utils.SPUtils;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.NobilityLevelUpPop;
import com.tftechsz.common.widget.pop.NobleNoticePop;
import com.tftechsz.common.widget.pop.QingLangPop;
import com.tftechsz.common.widget.pop.RechargeBeforePop;
import com.tftechsz.common.widget.pop.RechargePopWindow;
import com.tftechsz.common.widget.pop.RecommendValuePop;
import com.tftechsz.common.widget.pop.RedPackagePopWindow;
import com.tftechsz.im.mvp.ui.activity.VideoCallActivity;
import com.tftechsz.im.uikit.P2PMessageActivity;
import com.tftechsz.im.uikit.TeamMessageActivity;
import com.tftechsz.main.R;
import com.tftechsz.main.api.MainApiService;
import com.tftechsz.main.entity.UpdateLocationReq;
import com.tftechsz.main.mvp.IView.IMainView;
import com.tftechsz.main.widget.AccostPopWindow;
import com.tftechsz.main.widget.RetainNoMessagePopWindow;
import com.tftechsz.main.widget.RetainPopWindow;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.utils.UserManager;
import com.umeng.analytics.MobclickAgent;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<IMainView> {
    private long currentTime;
    private AccostPopWindow mAccostDialog;  //搭讪弹窗
    private RetainPopWindow mRetainPopWindow;  //挽留弹窗
    private RetainNoMessagePopWindow mRetainNoMessagePopWindow;
    MineApiService mineApiService;
    @Autowired
    UserProviderService userService;


    public MainPresenter() {

        mineApiService = RetrofitManager.getInstance().createUserApi(MineApiService.class);
        ARouter.getInstance().inject(this);
    }


    public void updateLocation(UpdateLocationReq body) {
        MainApiService service = RetrofitManager.getInstance().createUserApi(MainApiService.class);
        addNet(service.setAddress(body).compose(BasePresenter.<BaseResponse<String>>applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {

                    }

                }));
    }


    public void register(boolean isRegister) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeMsgStatus(messageStatusObserver, isRegister);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, isRegister);
    }


    @Override
    public void detachView() {
        super.detachView();
        register(false);
    }

    /**
     * 命令消息接收观察者
     */
    private final Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            final ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getContent());
            try {
                if (null == chatMsg) {
                    return;
                }
                LogUtil.e("=================", chatMsg.cmd_type);
                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.RED_PACKET_TYPE)) {   //消息回调（弹窗类型）红包
                    if (chatMsg.cmd.equals(ChatMsg.DEFAULT)) {
                        Utils.runOnUiThreadDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ChatMsg.RedPacket intimacy = JSON.parseObject(chatMsg.content, ChatMsg.RedPacket.class);
                                RedPackagePopWindow popWindow = new RedPackagePopWindow(ActivityUtils.getTopActivity(), intimacy);
                                popWindow.addOnClickListener(new RedPackagePopWindow.OnSelectListener() {
                                    @Override
                                    public void onCancel() {
                                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_RECOMMEND));
                                    }

                                    @Override
                                    public void onSure() {
                                    }
                                });
                                popWindow.showPopupWindow();
                            }
                        }, 1000);
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {   //语音视频速配
                    if (NERTCVideoCallImpl.sharedInstance().isReceive()) return;
                    ChatMsg.CallMsg callMsg = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                    boolean isMatch = TextUtils.equals(callMsg.type, ChatMsg.CALL_MATCH_FORCE);
                    //type.force = 强制匹配，type.inquiry = 询问匹配
                    userService.setMatchType(callMsg.type);
                    if (TextUtils.equals(chatMsg.cmd, "video_call_matched")) {
                        ChatMsgUtil.callMessage(2, chatMsg.to, chatMsg.from, callMsg.type, isMatch);
                    } else {
                        ChatMsgUtil.callMessage(1, chatMsg.to, chatMsg.from, callMsg.type, isMatch);
                    }
                    LogUtil.e("=================", isMatch + "=======" + callMsg.type);
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //弹窗
                    showPopWindow(chatMsg);
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_APPLY_TYPE)) {  //申请通知
                    MMKVUtils.getInstance().encode(userService.getUserId() + Constants.FAMILY_APPLY, chatMsg.content);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_USER_APPLY_JOIN));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.BLOG_NOTICE)) {   //动态通知
                    SPUtils.put(Constants.BLOG_NOTICE, chatMsg.content);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_BLOG_NOTICE));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.UPDATE_HOME_NAV)) {   //通知首页更新 家族解散，退出家族
                    ChatMsg.Notice notice = JSON.parseObject(chatMsg.content, ChatMsg.Notice.class);
                    if (TextUtils.equals("leave_family_notice", notice.from)) {  //离开家族
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_EXIT_FAMILY, 0));
                        MMKVUtils.getInstance().removeKey(userService.getUserId() + Constants.FAMILY_AIT);
                        MMKVUtils.getInstance().encode(Constants.TEAM_IS_FIRST, false);
                    } else {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REFRESH_RECOMMEND));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY)) {   //更新亲密度
                    long now = System.currentTimeMillis();
                    if ((now - currentTime) > 30000) {
                        currentTime = System.currentTimeMillis();
                        if (TextUtils.equals(chatMsg.cmd, "sync")) {
                            Utils.runOnUiThreadDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_INTIMACY));
                                }
                            }, 5000);
                        }
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.EVENT)) {   //type=(real,self,not-self,not-real)
                    ChatMsg.EventType eventType = JSON.parseObject(chatMsg.content, ChatMsg.EventType.class);
                    if (TextUtils.equals(eventType.type, "real") || TextUtils.equals(eventType.type, "not-real")
                            || TextUtils.equals(eventType.type, "self") || TextUtils.equals(eventType.type, "not-self")) {   // 真人认证实名认证审核通过
                        getUserInfo();
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.TOAST)) {   //搭讪过多的吐司
                    ChatMsg.EventType eventType = JSON.parseObject(chatMsg.content, ChatMsg.EventType.class);
                    if (!TextUtils.isEmpty(eventType.msg))
                        ToastUtil.showToast(BaseApplication.getInstance(), eventType.msg);
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_AFTER_INTIMACY_TIPS)) {   //亲密度达到可以音视频通话
                    ChatMsg.EventType intimacy = JSON.parseObject(chatMsg.content, ChatMsg.EventType.class);
                    if (intimacy != null && !TextUtils.isEmpty(intimacy.her_user_id)) {
                        IntimacyEntity entity = new IntimacyEntity();
                        entity.setUserId(intimacy.her_user_id);
                        entity.setSelfId(userService.getUserId() + "");
                        entity.setIsShow(1);
                        mCompositeDisposable.add(DbManager.getInstance().insert(entity).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) {
                            }
                        }));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.REDIRECT)) {   //跳转
                    if (TextUtils.equals(chatMsg.cmd, "login_page")) {  //跳转登录页面
                        ChatMsg.JumpMessage jumpMessage = JSON.parseObject(chatMsg.content, ChatMsg.JumpMessage.class);
                        AppManager.getAppManager().finishAllActivity();
                        ARouterUtils.toLoginActivity(ARouterApi.MINE_LOGIN, jumpMessage);
                        SPUtils.put(Constants.IS_COMPLETE_INFO, 0);
                        NIMClient.getService(AuthService.class).logout();
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.USER_STATUS)) {   //用户状态
                    ChatMsg.UserStatus userStatus = JSON.parseObject(chatMsg.content, ChatMsg.UserStatus.class);
                    if (userStatus != null && !TextUtils.isEmpty(userStatus.status))
                        RxBus.getDefault().post(new UserStatusEvent(userStatus.status));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_NOW)) {   //系统牵线消息
                    ChatMsg.AccostNow accostNow = JSON.parseObject(chatMsg.content, ChatMsg.AccostNow.class);
                    if (accostNow != null)
                        RxBus.getDefault().post(new AccostNowEvent(accostNow.pic, accostNow.title, accostNow.desc, accostNow.timestamp));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PAYMENT)) { //支付成功
                    ChatMsg.EventType content = JSON.parseObject(chatMsg.content, ChatMsg.EventType.class);
                    if (TextUtils.equals(content.type, "success")) {
                        MobclickAgent.onEvent(BaseApplication.getInstance(), "pay_success");
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.RESET_RECOMMEND)) {   //推荐值过低弹窗
                    ChatMsg.RecommendValue recommendValue = JSON.parseObject(chatMsg.content, ChatMsg.RecommendValue.class);
                    if (recommendValue != null) {
                        RecommendValuePop pop = new RecommendValuePop(BaseApplication.getInstance(), recommendValue);
                        pop.showPopupWindow();
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.USER_TASK)) { //用户在线任务
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ON_LINE_STATUS, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_RECRUIT)) { //家族招募红包
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_RECRUIT, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_FIRST_JOIN)) { //家族加入
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_FIRST_JOIN, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_POPUP)) { //搭讪pop
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_POPUP, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_AIT)) {  //家族ait人数
                    MMKVUtils.getInstance().encode(userService.getUserId() + Constants.FAMILY_AIT, chatMsg.content);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_AIT_SELF));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_RANK)) {   //家族排行
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_RANK, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_GIFT_BAG)) {   //空投消息通知
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_GIFT_BAG, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_COUPLE_LETTER_NUM)) {   //表白信
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_COUPLE_LETTER, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_COUPLE_GIFT_BAG_TO_UID)) {   //表白信礼包
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_COUPLE_PASTER, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_ACTIVITY)) {   //活动
                    if (TextUtils.equals(chatMsg.cmd, ChatMsg.FAMILY_ACTIVITY_QIXI)) {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACTIVITY_QIXI, chatMsg.content));
                    } else if (TextUtils.equals(chatMsg.cmd, ChatMsg.FAMILY_ACTIVITY_P2P)) { //活动p2p通用
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACTIVITY_P2P, chatMsg.content));
                    } else if (TextUtils.equals(chatMsg.cmd, ChatMsg.FAMILY_ACTIVITY_FAMILY)) { //活动家族通用
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACTIVITY_FAMILY, chatMsg.content));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_MARQUEE)) {   //亲密度100
                    final ChatMsg.AirdropOpen intimacy = JSON.parseObject(chatMsg.content, ChatMsg.AirdropOpen.class);
                    if (intimacy != null && !TextUtils.isEmpty(intimacy.to_user_id + "")) {
                        mCompositeDisposable.add(DbManager.getInstance().query(intimacy.to_user_id + "", userService.getUserId() + "").subscribe(new Consumer<IntimacyEntity>() {
                            @Override
                            public void accept(IntimacyEntity intimacyEntity) {
                                IntimacyEntity entity;
                                if (intimacyEntity == null) {
                                    entity = new IntimacyEntity();
                                    entity.setUserId(intimacy.to_user_id + "");
                                    entity.setSelfId(userService.getUserId() + "");
                                } else {
                                    entity = intimacyEntity;
                                }
                                entity.setEndTime(120);
                                mCompositeDisposable.add(DbManager.getInstance().insert(entity).subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) {
                                    }
                                }));
                            }
                        }));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_TASK_UPDATE)) {   //家族任务刷新
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_TASK_UPDATE));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_LEVEL_UP)) { //家族等级提升
                    com.blankj.utilcode.util.SPUtils.getInstance().put(Interfaces.SP_FAMILY_LEVEL_UP, chatMsg.content);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_LEVEL_UP, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.USER_LEVEL_UP)) { //用户等级提升
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_USER_LEVEL_UP, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_BOX_NOTICE_TO_UID) || TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_BOX_NOTICE)) { //家族宝箱
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_BOX, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_SWEET_RANK)) {//家族情侣
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_SWEET_RANK, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.RELIEVE_APPLY)) {//家族情侣解除申请
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_RELIEVE_APPLY, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CHAT_ALERT)) { //家族弹窗
                    if (TextUtils.equals(chatMsg.cmd, ChatMsg.CHAT_ALERT_REAL)) {//真人弹窗
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CHAT_ALERT_REAL, chatMsg.content));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.INTIMACY_GIFT)) {//亲密度等级礼物
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_INTIMACY_GIFT, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_INTO_BANNER)) {//家族进场动画
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_INTO_BANNER, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_INTO_BANNER_TO_UID)) {
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_INTO_BANNER_TO_UID, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.NOBILITY_NOTICE_TO_UID)) { //贵族开通推送
                    new NobleNoticePop().setData(chatMsg.content);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_NOBILITY_NOTICE_TO_UID));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.VIDEO_STYLE)) {   //音视频违规
                    if (TextUtils.equals("cost", chatMsg.cmd)) {   //花费多少钱
                        Utils.runOnUiThreadDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_CALL_COST, chatMsg.content));
                            }
                        }, 1000);
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.RED_PACKET_FAMILY)) {   //family
                    if (TextUtils.equals("contact", chatMsg.cmd)) {   //复制 家族广场联系号码
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_FAMILY_CONTENT, chatMsg.content));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.REMOVE_USER_CHAT)) {  //移除用户消息
                    ChatMsg.AirdropOpen user = JSON.parseObject(chatMsg.content, ChatMsg.AirdropOpen.class);
                    if (user != null)
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_REMOVE_USER, user.from_user_id));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.NOTICE_H5)) {  //通知H5更新
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_NOTICE_H5, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_NOTICE)) {
                    if (TextUtils.equals(chatMsg.cmd, ChatMsg.GO_INFO_PARTY)) {
                        ChatMsg.OneDay notice = JSON.parseObject(chatMsg.content, ChatMsg.OneDay.class);
                        ARouterUtils.joinRoom(String.valueOf(notice.room_id), notice.party_room_id);
                    } else {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT, chatMsg.content));
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.PARTY_GIFT_PLAY)) { //派对送礼
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PARTY_GIFT_PLAY, chatMsg.content));
                } else if (TextUtils.equals(chatMsg.cmd_type, "toast")) {
                    ChatMsg.AlertEvent msg = JSON.parseObject(chatMsg.content, ChatMsg.AlertEvent.class);
                    if (msg != null) {
                        Utils.toast(msg.msg);
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.UPDATE_CONFIG_LAUNCH)) {  //通知更新lunch
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_CONFIG_LAUNCH));
                    MMKVUtils.getInstance().encode(Constants.UPDATE_CONFIG_LAUNCH, 1);
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.QING_LANG)) {
                    QingLangBean content = JSON.parseObject(chatMsg.content, QingLangBean.class);
                    if (content != null) {
                        new QingLangPop(content.img, content.link, content.option).showPopupWindow();
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.NOBILITY_LEVEL_UP)) { // 贵族升级通知
                    if (chatMsg.cmd.equals(ChatMsg.DEFAULT)) {
                        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_MINE_USER_INFO));
                        Activity activity = AppManager.getAppManager().currentActivity();
                        if (activity != null) {
                            String simpleName = activity.getClass().getSimpleName();
                            if (TextUtils.equals(simpleName, "TeamMessageActivity")
                                    || TextUtils.equals(simpleName, "P2PMessageActivity")
                                    || TextUtils.equals(simpleName, "VideoCallActivity")
                                    || TextUtils.equals(simpleName, "NobleActivity")
                                    || TextUtils.equals(simpleName, "PartyRoomActivity")) {
                                NobilityLevelUpPopDto dto = JSON.parseObject(chatMsg.content, NobilityLevelUpPopDto.class);
                                NobilityLevelUpPop nobilityLevelUpPop = new NobilityLevelUpPop();
                                nobilityLevelUpPop.setDto(dto);
                            }
                        }
                    }
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ENTER_FAMILY)) {   //通知进入家族
                    ChatMsg.EnterFamily notice = JSON.parseObject(chatMsg.content, ChatMsg.EnterFamily.class);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ENTER_FAMILY, notice.family_id));
                } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_OWNER_CHANGE)) {   //家族改变通知
                    MMKVUtils.getInstance().removeKey(userService.getUserId() + Constants.FAMILY_APPLY);
                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 消息状态变化观察者 回调
     */
    private final Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            NimLog.i("TAG", String.format("content: %s, callbackExt: %s", message.getContent(), message.getCallbackExtension()));
            ChatMsg chatMsg = ChatMsgUtil.parseMessage(message.getCallbackExtension());
            try {
                if (null != chatMsg && TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) { //语音视频消息回调
                    ChatMsg.CallMsg callMsg1 = JSON.parseObject(chatMsg.content, ChatMsg.CallMsg.class);
                    if (callMsg1.state == 0) {
                        userService.setCallId(callMsg1.call_id);
                        userService.setRoomToken(callMsg1.room_token);
                        userService.setChannelName(callMsg1.channel_name);
                        userService.setMatchType(callMsg1.type);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUser_id(Integer.parseInt(chatMsg.to));
                        boolean isMatch = TextUtils.equals(chatMsg.cmd, ChatMsg.CALL_TYPE_VIDEO_MATCH) || TextUtils.equals(chatMsg.cmd, ChatMsg.CALL_TYPE_VOICE_MATCH);   //是否语音视频速配
                        VideoCallActivity.startCallOther(BaseApplication.getInstance(), userInfo, chatMsg.type, callMsg1.type, callMsg1.call_id, callMsg1.to_user_is_online, isMatch);
                    }
                } else if (null != chatMsg && TextUtils.equals(chatMsg.cmd_type, ChatMsg.ALERT_TYPE)) {   //消息回调（弹窗类型）
                    showPopWindow(chatMsg);
                } else if (null != chatMsg && TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_TYPE)) {   //搭讪成功
//                    RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS));
                }
                if (message.getStatus() == MsgStatusEnum.fail && UserInfoHelper.isInBlackList(message.getSessionId())) {
                    IMMessage tip = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
                    tip.setContent(Utils.getString(R.string.black_list_send_tip1));
                    tip.setStatus(MsgStatusEnum.success);
                    CustomMessageConfig config = new CustomMessageConfig();
                    config.enableUnreadCount = false;
                    tip.setConfig(config);
                    NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
                }


//                ChatMsg.CallBackMessage callBackMessage = JSON.parseObject(message.getCallbackExtension(), ChatMsg.CallBackMessage.class);
//                if (callBackMessage != null && !TextUtils.isEmpty(callBackMessage.tips_msg)) {
//                    ChatMsg chatMsg1 = JSON.parseObject(callBackMessage.new_body, ChatMsg.class);
//                    try {
//                        if (null != chatMsg1 && TextUtils.equals(chatMsg1.cmd_type, ChatMsg.CALL_TYPE)) { //语音视频消息回调
//                            ToastUtil.showToast(BaseApplication.getInstance(), callBackMessage.tips_msg);
//                            return;
//                        }
//                        message.setStatus(callBackMessage.show_fail == 1 ? MsgStatusEnum.fail : MsgStatusEnum.success);
//                        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
//                        IMMessage tip = MessageBuilder.createTipMessage(message.getSessionId(), message.getSessionType());
//                        tip.setContent(callBackMessage.tips_msg);
//                        tip.setStatus(MsgStatusEnum.success);
//                        CustomMessageConfig config = new CustomMessageConfig();
//                        config.enableUnreadCount = false;
//                        tip.setConfig(config);
//                        NIMClient.getService(MsgService.class).saveMessageToLocal(tip, true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 经纬度上传
     *
     * @param type 后台和前台显示类型
     */
    public void updateLocationReq(String type) {
        UpdateLocationReq req = new UpdateLocationReq();
        String city = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_CITY);
        String province = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_PROVINCE);
        String latitude = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_LATITUDE);
        String longitude = MMKVUtils.getInstance().decodeString(userService.getUserId() + Constants.LOCATION_LONGITUDE);
        req.latitude = TextUtils.isEmpty(latitude) ? 0 : Double.parseDouble(Objects.requireNonNull(latitude));
        req.longitude = TextUtils.isEmpty(longitude) ? 0 : Double.parseDouble(Objects.requireNonNull(longitude));
        req.province = province;
        req.city = city;
        req.fromTpe = type;
        updateLocation(req);
    }


    /**
     * 显示金币弹窗
     *
     * @param chatMsg
     */
    private void showPopWindow(ChatMsg chatMsg) {
        final ChatMsg.Alert alert = JSON.parseObject(chatMsg.content, ChatMsg.Alert.class);
        if (alert.template == 2) {  //金币充值弹窗
//            final RechargeTipPopWindow rechargeTipPopWindow = new RechargeTipPopWindow(BaseApplication.getInstance());
//            if (null != alert.left_button) {
//                rechargeTipPopWindow.setLeftButton(alert.left_button.msg);
//            }
//            if (null != alert.right_button)
//                rechargeTipPopWindow.setRightButton(alert.right_button.msg);
//            rechargeTipPopWindow.setContent(alert.des);
//            rechargeTipPopWindow.addOnClickListener(new RechargeTipPopWindow.OnSelectListener() {
//                @Override
//                public void onCancel() {
//                    if (null != alert.left_button) {
//                        String event = alert.left_button.event;
//                        if (event.contains("close"))
//                            rechargeTipPopWindow.dismiss();
//                    }
//                }
//
//                @Override
//                public void onSure() {
//                    if (null != alert.right_button) {
//                        String event = alert.right_button.event;
//                        if (!TextUtils.isEmpty(event) && event.contains("recharge"))
//                            ChargeListActivity.startActivity(BaseApplication.getInstance(), "");
//                    }
//                }
//            });
//            if (!rechargeTipPopWindow.isShowing())
//                rechargeTipPopWindow.showPopupWindow();
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_HIDE_SOFT));
            showRechargePop();
        } else {
            CommonUtil.showCustomPop(alert);
        }
    }


    /**
     * 显示搭讪弹窗
     */
    public void showAccostDialog(Context context, SystemAccostDto data) {
        if (null == mAccostDialog)
            mAccostDialog = new AccostPopWindow(context, data);
        mAccostDialog.setData(data);
        mAccostDialog.showPopupWindow();
    }

    /**
     * 显示挽留弹窗
     */
    public void showRetainNoMessagePop(Context context) {
        if (null == mRetainNoMessagePopWindow)
            mRetainNoMessagePopWindow = new RetainNoMessagePopWindow(context);
        mRetainNoMessagePopWindow.showPopupWindow();
    }

    /**
     * 显示挽留弹窗
     */
    public void showRetainPop(Context context, MessageInfo messageInfo) {
        if (null == mRetainPopWindow)
            mRetainPopWindow = new RetainPopWindow(context);
        mRetainPopWindow.initData(messageInfo);
        mRetainPopWindow.showPopupWindow();
    }


    //签到列表
    public void signList(String scene) {
        MainApiService service = RetrofitManager.getInstance().createUserApi(MainApiService.class);
        addNet(service.signListNew(scene).compose(BasePresenter
                        .<BaseResponse<SignInBean>>applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<SignInBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<SignInBean> response) {
                        if (getView() == null) return;
                        if (response.getData() != null) {
                            getView().signListSucceeded(response.getData());
                        } else {
                            getView().signListFail();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        //super.onFail(code, msg);
                        if (getView() == null) return;
                        getView().signListFail();
                    }
                }));
    }

    public void sign() {
        MainApiService service = RetrofitManager.getInstance().createUserApi(MainApiService.class);
        addNet(service.sign().compose(BasePresenter.<BaseResponse<SignInSuccessBean>>applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<SignInSuccessBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<SignInSuccessBean> response) {
                        SignInSuccessBean data = response.getData();
                        getView().onSignInResult(data);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().onSignInResult(null);
                    }
                }));
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        addNet(mineApiService.getUserInfo().compose(BasePresenter
                        .<BaseResponse<UserInfo>>applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<UserInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        if (response.getData() != null && getView() != null) {
                            UserManager.getInstance().setUserInfo(response.getData());
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO));
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 显示充值列表
     */
    public void showRechargePop() {
        if (getView() == null) return;
        if (userService.getConfigInfo() != null && userService.getConfigInfo().share_config != null && userService.getConfigInfo().share_config.is_limit_from_channel == 1) {
            RechargeBeforePop beforePop = new RechargeBeforePop(BaseApplication.getInstance());
            beforePop.addOnClickListener(new RechargeBeforePop.OnSelectListener() {
                @Override
                public void recharge() {
                    showPop();
                }
            });
            beforePop.showPopupWindow();
        } else {
            showPop();
        }
    }


    public void showPop() {
        int fromType = MMKVUtils.getInstance().decodeInt(RECHARGE_NUMBER);
        String number = MMKVUtils.getInstance().decodeString(FIY_NUMBER);
        int numberScene = MMKVUtils.getInstance().decodeInt(SCENE_NUMBER);
        RechargePopWindow rechargePopWindow = new RechargePopWindow(BaseApplication.getInstance(), fromType != -1 ? fromType : 1, 2);
        boolean isP2pRoom = TextUtils.equals(AppManager.getAppManager().currentActivity().getClass().getSimpleName(), P2PMessageActivity.class.getSimpleName());
        boolean isTeamRoom = TextUtils.equals(AppManager.getAppManager().currentActivity().getClass().getSimpleName(), TeamMessageActivity.class.getSimpleName());
        Utils.logE("  是否在单聊界面: " + isP2pRoom + "   是否在群聊: " + isTeamRoom);

        rechargePopWindow.setFormType(isTeamRoom ? 1 : 0);
        try {
            rechargePopWindow.setFamilyId(!TextUtils.isEmpty(number) && !number.equals("-1") ? Integer.parseInt(number) : isTeamRoom ? 1 : 0);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (numberScene != -1) {
            rechargePopWindow.scene = numberScene;
        }
        rechargePopWindow.getCoin();
        rechargePopWindow.requestData();
        rechargePopWindow.addOnClickListener(new RechargePopWindow.OnSelectListener() {
            @Override
            public void alipay(String orderNum) {
                wakeUpAliPay(orderNum);
            }
        });
        rechargePopWindow.showPopupWindow();

    }


    /**
     * 支付宝
     */
    public void wakeUpAliPay(final String orderInfo) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) {
                        PayTask alipay = new PayTask((Activity) MainPresenter.this.getView());
                        String result = alipay.pay(orderInfo, true);
                        emitter.onNext(result);
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        AlipayResultInfo payResult = new AlipayResultInfo(s);
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            MobclickAgent.onEvent(BaseApplication.getInstance(), "pay_success");
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_USER_INFO_SUCCESS));
                            ToastUtil.showToast(BaseApplication.getInstance(), "支付成功");
                        } else {
                            ToastUtil.showToast(BaseApplication.getInstance(), "支付失败");
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }

//
//    private boolean isMyMessage(IMMessage message) {
//        return message.getSessionType() == SessionTypeEnum.P2P
//                && message.getSessionId() != null
//                && message.getSessionId().equals(String.valueOf(userService.getUserId()));
//    }


}
