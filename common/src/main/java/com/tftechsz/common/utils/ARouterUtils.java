package com.tftechsz.common.utils;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.NetworkUtils;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.common.ARouterApi;
import com.tftechsz.common.Constants;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.AudioBean;
import com.tftechsz.common.entity.RealStatusInfoDto;
import com.tftechsz.common.entity.RechargeDto;
import com.tftechsz.common.iservice.AttentionService;
import com.tftechsz.common.iservice.PartyService;
import com.tftechsz.common.nim.model.CallParams;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.netease.nim.uikit.business.session.constant.Extras.EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT;

public class ARouterUtils {

    public static void inject(Object ob) {
        ARouter.getInstance().inject(ob);
    }

    public static void toPathWithId(String path) {
        ARouter.getInstance().build(path)
                .navigation();
    }

    public static void toPathWithId(String path, String param) {
        ARouter.getInstance().build(path)
                .withString("id", param)
                .navigation();
    }

    public static void toLoginActivity(String path) {
        ARouter.getInstance().build(path)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
    }


    public static void toLoginActivity(String path, ChatMsg.JumpMessage message) {
        ARouter.getInstance().build(path)
                .withSerializable("jumpMessage", message)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
    }


    /**
     * 跳转充值列表
     */
    public static void toRechargeActivity() {
        toRechargeActivity("");
    }

    /**
     * 跳转充值列表
     */
    public static void toRechargeActivity(String coin) {
        toRechargeActivity(coin, 0);
    }

    public static void toRechargeActivity(String coin, int form_type) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_CHARGE_LIST_NEW)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withString(Interfaces.EXTRA_COIN_VALUE, coin)
                .withInt(Interfaces.EXTRA_RECHARGE_TYPE, form_type)
                .navigation();
    }

    /**
     * 跳转清单列表
     */
    public static void toIntegralDetailedActivity(int type) { //0积分清单/收益记录  1:金币清单/收支记录  2.积分兑换记录   3金币消耗记录   4音符消耗记录   5音符兑换记录
        ARouter.getInstance().build(ARouterApi.ACTIVITY_INTEGRAL_DETAILED)
                .withInt(Interfaces.EXTRA_TYPE, type)
                .navigation();
    }

    /**
     * 跳转主页吗
     */
    public static void toMainActivity() {
        ARouter.getInstance().build(ARouterApi.MAIN_MAIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withInt("position", 0)
                .navigation();
    }

    /**
     * 跳转主页吗
     */
    public static void toMainActivity(int position) {
        ARouter.getInstance().build(ARouterApi.MAIN_MAIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withInt("position", position)
                .navigation();
    }

    /**
     * 跳转主页吗
     */
    public static void toMainMessageTab() {
        int position;
        boolean showParty = MMKVUtils.getInstance().decodeBoolean(Constants.SHOW_PARTY_ICON);
        position = showParty ? 3 : 2;
        ARouter.getInstance().build(ARouterApi.MAIN_MAIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withInt("position", position)
                .navigation();
    }


    /**
     * 跳转主页吗
     */
    public static void toMainActivity(IMMessage imMessage) {
        ARouter.getInstance().build(ARouterApi.MAIN_MAIN)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .withSerializable(NimIntent.EXTRA_NOTIFY_CONTENT, imMessage)
                .navigation();
    }

    /**
     * 跳转小秘书页面
     */
    public static void toNoticeActivity(String account) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_NOTICE)
                .withString("chat_user", account)
                .navigation();
    }


    /**
     * 跳转聊天
     */
    public static void toChatP2PActivity(String account, SessionCustomization customization, IMMessage anchor, String nickName, String nickAvatar) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_P2P_MESSAGE)
                .withString("account", account)
                .withSerializable("customization", customization)
                .withSerializable("anchor", anchor)
                .withString("nickName", nickName)
                .withString("nickAvatar", nickAvatar)
                .navigation();
    }

    /**
     * 跳转聊天
     */
    public static void toChatP2PActivity(String account, SessionCustomization customization, IMMessage anchor) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_P2P_MESSAGE)
                .withString("account", account)
                .withSerializable("customization", customization)
                .withSerializable("anchor", anchor)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转聊天列表
     */
    public static void toChatListActivity(String roomId, String roomName, String roomIcon) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_PARTY_MESSAGE)
                .withString("roomId", roomId)
                .withString("roomName", roomName)
                .withString("roomIcon", roomIcon)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转派对聊天
     */
    public static void toChatPartyP2PActivity(String account, SessionCustomization customization, IMMessage anchor, int height) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_PARTY_MESSAGE_DETAILS)
                .withString("account", account)
//                .withInt(EXTRA_TAG_SHOW_PARTY_MENU, 1)
                .withSerializable("customization", customization)
                .withSerializable("anchor", anchor)
                .withInt(EXTRA_TYPE_DIALOG_ACTIVITY_HEIGHT, height)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转聊天
     */
    public static void toChatTeamActivity(String account, SessionCustomization customization, IMMessage anchor) {
        if (!ClickUtil.canOperate()) return;
        AttentionService attentionService = ARouter.getInstance().navigation(AttentionService.class);
        PartyService partyService = ARouter.getInstance().navigation(PartyService.class);
        if (partyService.isRunFloatService() || partyService.isRunActivity()) {
            CustomPopWindow customPopWindow = new CustomPopWindow(BaseApplication.getInstance());
            customPopWindow.setContent("进入家族后会退出当前派对哦");
            customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                @Override
                public void onCancel() {
                    customPopWindow.dismiss();
                }

                @Override
                public void onSure() {
                    partyService.stopFloatService();
                    if (partyService.isRunActivity())
                        partyService.finishPartyActivity();
                    attentionService.finishPartyActivity();
                    ARouter.getInstance().build(ARouterApi.ACTIVITY_TEAM_MESSAGE)
                            .withString("account", account)
                            .withSerializable("customization", customization)
                            .withSerializable("anchor", anchor)
                            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .navigation();
                }
            });
            customPopWindow.showPopupWindow();
            return;
        }
        ARouter.getInstance().build(ARouterApi.ACTIVITY_TEAM_MESSAGE)
                .withString("account", account)
                .withSerializable("customization", customization)
                .withSerializable("anchor", anchor)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }


    /**
     * 跳转聊天
     */
    public static void toChatTeamActivity(String account, SessionCustomization customization, IMMessage anchor, int type) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_TEAM_MESSAGE)
                .withString("account", account)
                .withInt("teamType", type)
                .withSerializable("customization", customization)
                .withSerializable("anchor", anchor)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转语音视频
     */
    public static void toCallActivity(UserInfo userInfo, int type) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_VIDEO_CALL)
                .withInt("call_dir", 0)
                .withSerializable("call_out_user", userInfo)
                .withInt("call_type", type)
                .navigation();
    }


    /**
     * 跳转语音视频
     */
    public static void toTeamCallActivity(String teamid) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_TEAM_CALL)
                .withString("teamid", teamid)
                .withString(CallParams.TEAM_CHAT_GROUP_ID, teamid)
                .withBoolean(CallParams.INVENT_CALL_RECEIVED, false)
                .navigation();
    }

    public static void toTeamCallActivity(ArrayList<String> callUserList, String groupID, InvitedEvent invitedEvent) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_TEAM_CALL)
                .withStringArrayList(CallParams.INVENT_USER_IDS, callUserList)
                .withString(CallParams.TEAM_CHAT_GROUP_ID, groupID)
                .withString(CallParams.INVENT_REQUEST_ID, invitedEvent.getRequestId())
                .withString(CallParams.INVENT_CHANNEL_ID, invitedEvent.getChannelBaseInfo().getChannelId())
                .withString(CallParams.INVENT_FROM_ACCOUNT_ID, invitedEvent.getFromAccountId())
                .withBoolean(CallParams.INVENT_CALL_RECEIVED, true)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }


    /**
     * 跳转语音视频
     */
    public static void toCallActivity(InvitedEvent chatMsg, int type, String matchType, String callId, boolean isMatch) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_VIDEO_CALL)
                .withInt("call_dir", 1)
                .withInt("call_type", type)
                .withSerializable("call_in_event", chatMsg)
                .withString("call_match_type", matchType)
                .withBoolean("call_is_match", isMatch)
                .withString("call_id", callId)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转语音视频
     */
    public static void toSplashActivity() {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_SPLASH)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }

    /**
     * 跳转语音视频
     */
    public static void toCallActivity(String fromId, int type, String matchType, String callId, boolean isMatch) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_VIDEO_CALL)
                .withInt("call_dir", 1)
                .withInt("call_type", type)
                .withSerializable("from_id", fromId)
                .withString("call_match_type", matchType)
                .withString("call_id", callId)
                .withBoolean("call_is_match", isMatch)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();
    }


    /**
     * 跳转个人信息
     */
    public static void toMineDetailActivity(String userId) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_MINE_DETAIL)
                .withString("user_id", userId)
                .navigation();
    }

    /**
     * 跳转举报页面
     *
     * @param fromType 1:个人  2：动态 3：帮助反馈
     */
    public static void toReportActivity(int reportType, int blogId, int fromType) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REPORT)
                .withInt("report_type", reportType)
                .withInt("blogId", blogId)
                .withInt("fromType", fromType)
                .navigation();
    }

    public static void toSettingActivity() {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_SETTING)
                .navigation();
    }


    /**
     * 跳转个人动态
     */
    public static void toTrendActivity(String id, String name) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_MINE_TREND)
                .withString("id", id)
                .withString("name", name)
                .navigation();
    }

    /**
     * 跳转到家族详情
     */
    public static void toFamilyDetail(int familyId, String invite_id, int fromType) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_FAMILY_DETAIL)
                .withInt("familyId", familyId)
                .withInt("fromType", fromType)
                .withString(Interfaces.EXTRA_INVITE_ID, invite_id)
                .navigation();
    }

    /**
     * 跳转到家族详情
     */
    public static void toFamilyDetail(int familyId, int fromType) {
        toFamilyDetail(familyId, "", fromType);
    }

    /**
     * 跳转到家族详情
     */
    public static void toFamilyMember(int familyId) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_FAMILY_MEMBER)
                .withInt("familyId", familyId)
                .withInt("from", 1)
                .navigation();
    }

    /**
     * 跳转到编辑家族
     */
    public static void toEditFamily(String desc, int role) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_EDIT_FAMILY)
                .withInt("type", 5)
                .withString("desc", desc)
                .withInt("role", role)
                .navigation();
    }

    /**
     * 跳转到编辑语音公告
     */
    public static void toEditFamilyfVoice(String value, int type, String desc) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_EDIT_FAMILY)
                .withInt("type", type)
                .withString("value", value)
                .withString("desc", desc)
                .navigation();
    }

    /**
     * 跳转到家族详情
     */
    public static void toFamilyMember(Activity context, String tid) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_FAMILY_MEMBER)
                .withString("tid", tid)
                .withInt("from", 2)
                .navigation(context, 10000);
    }


    /**
     * 跳转到组情侣
     */
    public static void toGroupCouple(Activity context, String tid) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_GROUP_COUPLE)
                .withString("tid", tid)
                .navigation(context, 10001);
    }


    /**
     * 跳转到ait 页面
     */
    public static void toFamilyAitMember(Activity context, String tid) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_FAMILY_AIT_MEMBER)
                .withString("tid", tid)
                .navigation(context, 10000);
    }


    /**
     * 跳转到家族详情
     */
    public static void toRoomMember(Activity context, String rid, int from) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_ROOM_MEMBER)
                .withString("rid", rid)
                .withInt("from", from)
                .navigation(context, 10000);

    }

    /**
     * 进入语音直播间
     */
    public static void joinRoom(String roomId, int id) {
       /* Intent intent = new Intent(activity, PartyRoomActivity.class);
        //String roomId, int id, String roomThumb, String roomBg
        intent.putExtra("roomId", roomId);
        intent.putExtra("id", id);
        intent.putExtra("roomThumb", roomThumb);
        intent.putExtra("roomBg", roomBg);
        intent.putExtra("partyData", data);
        activity.startActivity(intent);*/
        ARouter.getInstance().build(ARouterApi.ACTIVITY_ROOM_STUDIO)
                .withString("roomId", roomId)
                .withInt("id", id)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();

    }

    /**
     * 进入语音直播间
     */
    public static void joinRoom(String roomId, int id, int partyId) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_ROOM_STUDIO)
                .withString("roomId", roomId)
                .withInt("id", id)
                .withInt("lastPartyId", partyId)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .navigation();

    }

    /**
     * 跳转到我的家族
     */
    public static void toMineFamily(int familyId) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_MINE_FAMILY)
                .withInt("familyId", familyId)
                .navigation();
    }


    /**
     * 跳转真人认证状态
     */
    public static void toRealAuthentication(int status, String path, RealStatusInfoDto data) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REAL_AUTHENTICATION_STATUS)
                .withInt(Constants.EXTRA_STATUS, status)
                .withString(Constants.EXTRA_PATH, path)
                .withSerializable(Constants.EXTRA_DATA, data)
                .navigation();
    }

    /**
     * 跳转实名认证状态
     */
    public static void toRealSuccessActivity(int status) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REAL_SUCCESS)
                .withInt("extra_status", status)
                .navigation();
    }

    /**
     * 跳转实名认证状态
     */
    public static void toRealSuccessActivity(int status, String errorMsg) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REAL_SUCCESS)
                .withInt(Constants.EXTRA_STATUS, status)
                .withString(Interfaces.EXTRA_MESSAGE, errorMsg)
                .navigation();
    }

    /**
     * 跳转实名认证状态
     */
    public static void toRealSuccessActivity(int status, boolean isInPartySelf) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REAL_SUCCESS)
                .withInt("extra_status", status)
                .withBoolean(Interfaces.EXTRA_TYPE, isInPartySelf)
                .navigation();
    }


    /**
     * 跳转视频裁剪
     */
    public static void toVideoTrimmerActivity(Activity mContext, String videoPath) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_VIDEOTRIMMER)
                .withString(Interfaces.VIDEO_PATH_KEY, videoPath)
                .navigation(mContext, Interfaces.VIDEO_TRIM_REQUEST_CODE);
    }

    public static void toAccostSettingActivity(String title) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_ACCOST_SETTING)
                .withString(Interfaces.EXTRA_TITLE, title)
                .navigation();
    }

    public static void toUserPicBrowserActivity(int uid, int index, String firstIcon, boolean flagIsBoy) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_USER_PIC_BROWSER)
                .withInt(Interfaces.EXTRA_UID, uid)
                .withInt(Interfaces.EXTRA_INDEX, index)
                .withString(Interfaces.EXTRA_FIRST_ICON, firstIcon)
                .withBoolean(Interfaces.EXTRA_ISBOY_ICON, flagIsBoy)
                .navigation();
    }

    /**
     * 跳转到我的家族
     */
    public static void toUserShareList(String type) {
        if (!NetworkUtils.isConnected()) {
            Utils.toast(R.string.net_error);
            return;
        }
        ARouter.getInstance().build(ARouterApi.ACTIVITY_SHARE)
                .withString(Interfaces.EXTRA_TYPE, type)
                .navigation();
    }

    public static void toRealCamera(Activity activity, int type) { //0:人脸   1:身份证正面   2:反面
        int requestCode;
        switch (type) {
            case 1:
                requestCode = Interfaces.EXTRA_REAL_CAMERA_SFZ_FONT;
                break;
            case 2:
                requestCode = Interfaces.EXTRA_REAL_CAMERA_SFZ_BACK;
                break;
            default:
                requestCode = Interfaces.EXTRA_REAL_CAMERA;
                break;
        }
        ARouter.getInstance().build(ARouterApi.ACTIVITY_REAL_CAMERA)
                .withInt(Interfaces.EXTRA_TYPE, type)
                .navigation(activity, requestCode);
    }

    public static void toVipDressUp(int page, boolean isMyDressUpTitle) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_VIP_DRESS_UP)
                .withInt(Interfaces.EXTRA_PAGE, page)
                .withBoolean(Interfaces.EXTRA_DATA, isMyDressUpTitle)
                .navigation();
    }


    public static void toNobleActivity() {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_MY_NOBLE)
                .navigation();
    }

    public static void toMineIntegralActivity(int type) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_MINE_INTEGRAL)
                .withInt(Interfaces.EXTRA_TYPE, type)
                .navigation();
    }

    public static void toScanActivity(List<AudioBean> beans) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_SCAN_MUSIC)
                .withSerializable(Interfaces.EXTRA_DATA, beans.isEmpty() ? new ArrayList<>() : ((Serializable) beans))
                .navigation();

    }

    public static void toFaceCheck(boolean isPartyMode) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_FACE_CHECK)
                .withBoolean(Interfaces.EXTRA_DATA, isPartyMode)
                .navigation();
    }

    public static void toChargePayActivity(RechargeDto coin, int form_type) {
        ARouter.getInstance().build(ARouterApi.ACTIVITY_CHARGE_PAY)
                .withSerializable(Interfaces.EXTRA_DATA, coin)
                .withInt(Interfaces.EXTRA_RECHARGE_TYPE, form_type)
                .navigation();
    }
}
