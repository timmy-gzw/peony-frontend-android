package com.netease.nim.uikit.api.model.session;

import android.content.Context;

import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * 会话窗口消息列表一些点击事件的响应处理函数
 */
public interface SessionEventListener {

    // 头像点击事件处理，一般用于打开用户资料页面
    void onAvatarClicked(Context context, IMMessage message);

    //
    void onVoiceSeatClicked(Context context, IMMessage message);

    // 卡片点击事件处理，一般用于打开用户资料页面
    void onCardClicked(Context context, IMMessage message);


    void onCardPhotoClicked(Context context, IMMessage message ,int position , String url);


    void onAitClicked(String account);

    // 头像长按事件处理，一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
    void onAvatarLongClicked(Context context, IMMessage message);

    // 已读回执事件处理，用于群组的已读回执事件的响应，弹出消息已读详情
    void onAckMsgClicked(Context context, IMMessage message);

    //添加了金币后的点击事件
    void onAddAmount();

    void onCreateFamilyEvent(String event);

    void onFamilyJoin(String account, IMMessage message);

    //点击打开红包
    void openRedEnvelope(int red_packet_id, IMMessage message);

    //点击打开空投
    void openAirdrop(int airDrop, IMMessage message);

    //继续送礼
    void continueSendGift(IMMessage message);

    //查看红包详情
    void openAirdropDetail(int airDropId);

    void toFamilyDetail(int familyId, String invite_id);

    //查看红包详情
    void getRedEnvelopeDetail(int red_packet_id);

    // 头像点击事件处理，一般用于打开用户资料页面
    void onAvatarClicked(String uid);
}
