package com.tftechsz.common.nim;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.recent.RecentCustomization;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.api.model.session.SessionEventTipListener;
import com.netease.nim.uikit.api.wrapper.NimMessageRevokeObserver;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.business.extension.AVChatAttachment;
import com.netease.nim.uikit.business.extension.AccostCardAttachment;
import com.netease.nim.uikit.business.extension.AccostChatAttachment;
import com.netease.nim.uikit.business.extension.CustomAttachParser;
import com.netease.nim.uikit.business.extension.CustomAttachment;
import com.netease.nim.uikit.business.extension.FamilyInviteAttachment;
import com.netease.nim.uikit.business.extension.GiftChatAttachment;
import com.netease.nim.uikit.business.extension.GiftChatTeamAttachment;
import com.netease.nim.uikit.business.extension.MsgViewHolderDefCustom;
import com.netease.nim.uikit.business.extension.RedEnvelopeAttachment;
import com.netease.nim.uikit.business.extension.RedReceiveAttachment;
import com.netease.nim.uikit.business.extension.ReplyAccostChatAttachment;
import com.netease.nim.uikit.business.extension.TipCustomAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgTipViewHolder;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderAVChat;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderAccost;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderAirdrop;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderAirdropReceive;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderFamilyInvite;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderFamilySeat;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderGame;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderGift;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderRedEnvelope;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderRedReceive;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderReplyAccost;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderTeamGift;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderUserInfo;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
import com.netease.nim.uikit.impl.customization.DefaultRecentCustomization;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BaseWebViewActivity;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.CallCheckDto;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.event.FamilyActivityDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.iservice.FamilyService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.ToastUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.CustomPopWindow;
import com.tftechsz.common.widget.pop.RemoveCouplesPop;

import java.util.List;

/**
 * UIKit自定义消息界面用法展示类
 */
public class SessionHelper {

    private static final int ACTION_HISTORY_QUERY_PERSIST_CLEAR = 0;

    private static final int ACTION_HISTORY_QUERY_NOT_PERSIST_CLEAR = 1;

    private static final int ACTION_SEARCH_MESSAGE = 2;

    private static final int ACTION_CLEAR_MESSAGE_RECORD = 3;

    private static final int ACTION_CLEAR_MESSAGE_NOT_RECORD = 4;

    private static final int ACTION_CLEAR_MESSAGE = 5;

    private static SessionCustomization p2pCustomization;

    private static SessionCustomization normalTeamCustomization;

    private static SessionCustomization advancedTeamCustomization;

    private static SessionCustomization myP2pCustomization;

    private static SessionCustomization robotCustomization;

    private static RecentCustomization recentCustomization;

    private static NIMPopupMenu popupMenu;

    private static List<PopupMenuItem> menuItemList;

    public static final boolean USE_LOCAL_ANTISPAM = true;
    private static UserProviderService service;

    public static void init() {
        // 注册自定义消息附件解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        // 注册各种扩展消息类型的显示ViewHolder
        registerViewHolders();
        // 设置会话中点击事件响应处理
        setSessionListener();
        NimUIKit.setRecentCustomization(getRecentCustomization());
        registerIMMessageFilter();

        service = ARouter.getInstance().navigation(UserProviderService.class);


    }

    private static RecentCustomization getRecentCustomization() {
        if (recentCustomization == null) {
            recentCustomization = new DefaultRecentCustomization() {

                @Override
                public String getDefaultDigest(RecentContact recent) {
                    return super.getDefaultDigest(recent);
                }
            };
        }
        return recentCustomization;
    }


    private static void registerViewHolders() {
        NimUIKit.registerMsgItemViewHolder(CustomAttachment.class, MsgViewHolderDefCustom.class);
        NimUIKit.registerMsgItemViewHolder(TipCustomAttachment.class, MsgTipViewHolder.class);
        NimUIKit.registerMsgItemViewHolder(AVChatAttachment.class, MsgViewHolderAVChat.class);
        NimUIKit.registerMsgItemViewHolder(AccostChatAttachment.class, MsgViewHolderAccost.class);
        NimUIKit.registerMsgItemViewHolder(GiftChatAttachment.class, MsgViewHolderGift.class);
        NimUIKit.registerMsgItemViewHolder(GiftChatTeamAttachment.class, MsgViewHolderTeamGift.class);
        NimUIKit.registerMsgItemViewHolder(RedEnvelopeAttachment.class, MsgViewHolderRedEnvelope.class);
        NimUIKit.registerMsgItemViewHolder(RedReceiveAttachment.class, MsgViewHolderRedReceive.class);
        NimUIKit.registerMsgItemViewHolder(ReplyAccostChatAttachment.class, MsgViewHolderReplyAccost.class);
        NimUIKit.registerMsgItemViewHolder(FamilyInviteAttachment.class, MsgViewHolderFamilyInvite.class);
        NimUIKit.registerMsgItemViewHolder(FamilyInviteAttachment.class, MsgViewHolderAirdrop.class);
        NimUIKit.registerMsgItemViewHolder(FamilyInviteAttachment.class, MsgViewHolderAirdropReceive.class);
        NimUIKit.registerMsgItemViewHolder(FamilyInviteAttachment.class, MsgViewHolderGame.class);
        NimUIKit.registerMsgItemViewHolder(AccostCardAttachment.class, MsgViewHolderUserInfo.class);
        NimUIKit.registerMsgItemViewHolder(AccostCardAttachment.class, MsgViewHolderFamilySeat.class);
    }


    private static void setSessionListener() {
        SessionEventTipListener listenerTip = (context, type, content) -> {   // /2/打开webview   1打开原生页面
            if (type == 1) {
                if (content.contains(Constants.REAL_AUTHENTICATION_ACTIVITY)) {   //真人认证
                    getRealInfo();
                } else if (content.contains(Constants.CHAT_ACTIVITY)) {   //聊天页面
                    String userId = content.substring(Constants.CHAT_ACTIVITY.length() + 1);
                    ARouterUtils.toChatP2PActivity(userId, NimUIKit.getCommonP2PSessionCustomization(), null);
                } else if (content.contains(Constants.CHAT_TEAM_ACTIVITY)) {  //群聊页面
                    String teamId = content.substring(Constants.CHAT_TEAM_ACTIVITY.length() + 1);
                    getFamilyId(teamId);
                } else if (content.contains(Constants.USER_DETAIL_ACTIVITY)) {   //个人详情页面
                    String userId = content.substring(Constants.USER_DETAIL_ACTIVITY.length() + 1);
                    if (TextUtils.equals(String.valueOf(service.getUserId()), userId)) {
                        userId = "";
                    }
                    ARouterUtils.toMineDetailActivity(userId);
                } else if (content.contains(Constants.USER_ATTENTION)) {    //关注用户
                    String userId = content.substring(Constants.USER_ATTENTION.length() + 1);
                    attentionUser(Integer.parseInt(userId));
                } else if (content.contains(Constants.CALL_VOICE)) {    //语音
                    String userId = content.substring(Constants.CALL_VOICE.length() + 1);
                    if (!TextUtils.isEmpty(userId))
                        checkCallMsg(userId, 1);
                } else if (content.contains(Constants.CALL_VIDEO)) {    //视频
                    String userId = content.substring(Constants.CALL_VIDEO.length() + 1);
                    if (!TextUtils.isEmpty(userId))
                        checkCallMsg(userId, 2);
                } else if (content.contains(Constants.USER_ACCOST)) {      //搭讪用户
                    String userId = content.substring(Constants.USER_ACCOST.length() + 1);
                    accostUser(userId);
                } else if (content.contains(Constants.MY_BLOG_LIST)) {   //我的动态列表
                    ARouterUtils.toTrendActivity("", "");
                } else if (content.contains(Constants.MY_PHOTO_LIST)) {   //我的相册
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_PHOTO);
                } else if (content.contains(Constants.FAMILY_APPLY_JOIN) || content.contains(Constants.FAMILY_APPLY_LEAVE)) {   //家族加入人申请列表
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_FAMILY_APPLY);
                } else if (content.contains(Constants.MY_GOLD_LIST)) {   //金币清单
                    ARouterUtils.toIntegralDetailedActivity(1);
                } else if (content.contains(Constants.EDIT_INFO)) {   //个人资料编辑
                    if (!NetworkUtils.isConnected()) {
                        Utils.toast(R.string.net_error);
                        return;
                    }
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_MINE_INFO);
                } else if (content.contains(Interfaces.LINK_PEONY_ACCOST_SETTING_AUDIO)) {   //招呼语音
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_AUDIO);
                } else if (content.contains(Interfaces.LINK_PEONY_ACCOST_SETTING_PIC)) {   //相册招呼
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_PIC);
                } else if (content.contains(Interfaces.LINK_PEONY_ACCOST_SETTING_CUSTOMIZE)) {   //自定义招呼
                    ARouterUtils.toPathWithId(ARouterApi.ACTIVITY_ACCOST_SETTING_CUSTOMIZE);
                } else if (content.contains(Interfaces.LINK_PEONY_SELF)) {   //实名驳回
                    checkSelfStatus();
                } else if (content.contains(Constants.USER_COUPLE_RELIEVE)) {   //解除情侣
                    String applyId = content.substring(Constants.USER_COUPLE_RELIEVE.length() + 1);
                    if (!TextUtils.isEmpty(applyId)) {
                        showRemovePop(applyId);
                    }
                }
            } else if (type == 2) {
                if (content.contains(Interfaces.ACTIVITY_QIXI)) {
                    ARouter.getInstance().navigation(MineService.class)
                            .trackEvent("七夕活动点击_小秘书", Interfaces.POINT_EVENT_CLICK, "activity_qixi_notice_click",
                                    JSON.toJSONString(new FamilyActivityDto(null, service.getUserInfo().isBoy() ? "boy" : "girl")), null);
                }

                BaseWebViewActivity.start(context, "", content, 0, 0);
            }
        };
        NimUIKit.setSessionTipListener(listenerTip);

    }

    private static void checkSelfStatus() {
        if (!NetworkUtils.isConnected()) {
            Utils.toast(R.string.net_error);
            return;
        }
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        mineService.getSelfInfo(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
            @Override
            public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                if (null != response.getData()) {   //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
                    CommonUtil.performSelf(response.getData());
                }
            }
        });
    }


    /**
     * 关注用户
     */
    public static void attentionUser(int id) {
        AttentionService attentionService = ARouter.getInstance().navigation(AttentionService.class);
        attentionService.attentionUser(id, new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {

            }
        });

    }


    /**
     * 获取是否真人认证
     */
    public static void getRealInfo() {
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        mineService.getRealInfo(new ResponseObserver<BaseResponse<RealStatusInfoDto>>() {
            @Override
            public void onSuccess(BaseResponse<RealStatusInfoDto> response) {
                if (null != response.getData()) {   //用户审核状态 -1.未认证,0.等待审核，1.审核完成，2.审核驳回
                    CommonUtil.performReal(response.getData());
                }
            }
        });


    }

    /**
     * 搭讪用户
     */
    public static void accostUser(String userId) {
        AccostService accostService = ARouter.getInstance().navigation(AccostService.class);
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        accostService.accostUser(userId, CommonUtil.isBtnTextHome(service) ? 1 : 2, 7, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                ChatMsgUtil.sendAccostMessage(String.valueOf(service.getUserId()), userId, response.getData().gift.id, response.getData().gift.name, response.getData().gift.image, response.getData().gift.animation, response.getData().msg, 7, response.getData().accost_from);
//                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS));

            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });
    }


    private static void checkCallMsg(String userId, int type) {
        MineService mineService = ARouter.getInstance().navigation(MineService.class);
        mineService.getCallCheck(userId, 2, new ResponseObserver<BaseResponse<CallCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<CallCheckDto> response) {
                CallCheckDto data = response.getData();
                if (null == data || !com.tftechsz.common.utils.CommonUtil.hasPerformAccost(data.tips_msg, data.is_real_alert, data.is_self_alert, service.getUserInfo())) {
                    if (type == 1) {
                        if (null != data && null != data.error && null != data.error.voice) {
                            if (TextUtils.equals(data.error.voice.cmd_type, Constants.DIRECT_RECHARGE)) {
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ALIPAY));
                            } else {
                                showCustomPop(data.error.voice.msg);
                            }
                        } else {
                            service.setMatchType("");
                            ChatMsgUtil.callMessage(type, String.valueOf(service.getUserId()), userId, "", false);
                        }
                    } else {
                        if (null != data && null != data.error && null != data.error.video) {
                            if (TextUtils.equals(data.error.video.cmd_type, Constants.DIRECT_RECHARGE)) {
                                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ALIPAY));
                            } else {
                                showCustomPop(data.error.video.msg);
                            }
                        } else {
                            service.setMatchType("");
                            ChatMsgUtil.callMessage(type, String.valueOf(service.getUserId()), userId, "", false);
                        }
                    }
                }
            }
        });

    }


    private static void showRemovePop(String applyId) {
        RemoveCouplesPop removeCouplesPop = new RemoveCouplesPop(BaseApplication.getInstance(),Integer.parseInt(applyId));
        removeCouplesPop.getData(Integer.parseInt(applyId));
        if (!removeCouplesPop.isShowing())
            removeCouplesPop.showPopupWindow();
    }

    private static void showCustomPop(String content) {
        CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance());
        customPopWindow.setContent(content);
        customPopWindow.setRightGone();
        customPopWindow.showPopupWindow();
    }

    /**
     * 获取群id
     */
    private static void getFamilyId(String teamId) {
        FamilyService familyService = ARouter.getInstance().navigation(FamilyService.class);
        UserProviderService service = ARouter.getInstance().navigation(UserProviderService.class);
        familyService.searchFamily(teamId, new ResponseObserver<BaseResponse<FamilyIdDto>>() {
            @Override
            public void onSuccess(BaseResponse<FamilyIdDto> response) {
                if (null != response.getData() && service != null) {
                    if (response.getData().family_id == service.getUserInfo().family_id) {
                        ARouterUtils.toChatTeamActivity(teamId, NimUIKit.getCommonTeamSessionCustomization(), null);
                    } else {
                        ToastUtil.showToast(BaseApplication.getInstance(), "您已不在该家族");
                    }
                }
            }
        });

    }


    private static void registerIMMessageFilter() {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(message -> {
            if (message.getAttachment() != null) {
                if (message.getAttachment() instanceof MemberChangeAttachment) {
                    MemberChangeAttachment attachment = (MemberChangeAttachment) message.getAttachment();
                    if (attachment.getType() == NotificationType.KickMember || attachment.getType() == NotificationType.SUPER_TEAM_KICK
                            || attachment.getType() == NotificationType.LeaveTeam || attachment.getType() == NotificationType.SUPER_TEAM_KICK) {
                        return true;
                    }
                    if (attachment.getType() == NotificationType.SUPER_TEAM_INVITE || attachment.getType() == NotificationType.InviteMember) {
                        if (service.getConfigInfo() != null && service.getConfigInfo().sys != null &&
                                service.getConfigInfo().sys.room_id_list != null && service.getConfigInfo().sys.room_id_list.size() > 0 &&
                                service.getConfigInfo().sys.room_id_list.contains(message.getSessionId())) {
                            return true;
                        }
                    }
                } else if (message.getAttachment() instanceof AVChatAttachment) {
                    return false;// 是否过滤音视频消息
                }
            }
            return false;
        });
    }


    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new NimMessageRevokeObserver(), true);
    }


}
