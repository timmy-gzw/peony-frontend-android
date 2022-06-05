package com.tftechsz.im.mvp.presenter;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.animation.TranslateAnimation;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.OnLineListBean;
import com.netease.nim.uikit.bean.AccostDto;
import com.netease.nim.uikit.business.recent.TeamMemberAitHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.util.Base64;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.tftechsz.im.R;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.ContactInfo;
import com.tftechsz.im.model.dto.AppInitDto;
import com.tftechsz.im.model.dto.MessageIntimacyDto;
import com.tftechsz.im.model.event.MessageRefreshListEvent;
import com.tftechsz.im.mvp.iview.IChatView;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.ChatHistoryDto;
import com.tftechsz.common.entity.MsgCheckDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.AccostService;
import com.tftechsz.common.iservice.MineService;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.MMKVUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatPresenter extends BasePresenter<IChatView> {
    private static final String SELF_ATTACH = "self_attach";
    private static final String BOY_ACCOST_DESC = "boy_accost_desc";
    public ChatApiService service, serviceParty;
    private final MineService mineService;
    private final UserProviderService userProviderService;
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private AccostService accostService;


    private long[] mHits = new long[2];

    public ChatPresenter() {
        service = RetrofitManager.getInstance().createUserApi(ChatApiService.class);
        serviceParty = RetrofitManager.getInstance().createPartyApi(ChatApiService.class);
        userProviderService = ARouter.getInstance().navigation(UserProviderService.class);
        mineService = ARouter.getInstance().navigation(MineService.class);
        accostService = ARouter.getInstance().navigation(AccostService.class);
    }

    /**
     * 获取聊天中用户信息
     */
    public void getImUserInfo(List<ContactInfo> data) {
        AtomicInteger atomicInteger = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i).contactId);
            sb.append(",");
        }
        if (data.size() <= 0) {
            getView().getChatUserInfo(data);
            return;
        }
        addNet(service.getImUserInfo(sb.substring(0, sb.toString().length() - 1)).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<UserInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<UserInfo>> response) {
                        atomicInteger.getAndIncrement();
                        for (int i = 0; i < data.size(); i++) {
                            for (int j = 0; j < response.getData().size(); j++) {
                                if (TextUtils.equals(data.get(i).contactId, String.valueOf(response.getData().get(j).getUser_id()))) {
                                    data.get(i).fromNick = response.getData().get(j).getNickname();
                                    data.get(i).userAvatar = response.getData().get(j).getIcon();
                                }
                            }
                        }
                        if (atomicInteger.get() >= data.size() && getView() != null) {
                            getView().getChatUserInfo(data);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }

    /**
     * 派对在线列表
     */
    public void getPartyList(int page, String id) {

        addNet(serviceParty.userlist(id, page).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<OnLineListBean>>() {
                    @Override
                    public void onSuccess(BaseResponse<OnLineListBean> response) {
                        getView().onSuccessUserList(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        getView().onSuccessUserList(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getView().onSuccessUserList(null);
                    }
                }));
    }

    public void getUserInfo() {
        mineService.getUserInfo(new ResponseObserver<BaseResponse<UserInfo>>() {
            @Override
            public void onSuccess(BaseResponse<UserInfo> response) {
                if (response.getData() != null && getView() != null) {
                    getView().getUserInfoSuccess(response.getData());
                    userProviderService.setUserInfo(response.getData());
                }
            }
        });

    }

    public void checkUserInfoById(String userId) {
        mineService.getUserInfoById(userId, new ResponseObserver<BaseResponse<UserInfo>>() {
            @Override
            public void onSuccess(BaseResponse<UserInfo> response) {
                if (response.getData() != null && getView() != null) {
                    getView().checkUserInfoSuccess(userId, response.getData());
                }
            }
        });
    }


    /**
     * app 进入获取密友消息
     */
    public void appInit() {
        List<ContactInfo> data = new ArrayList<>();
        MsgService msgService = NIMClient.getService(MsgService.class);
        AtomicInteger atomicInteger = new AtomicInteger();
        addNet(service.appInit().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<AppInitDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<AppInitDto> response) {
                        AppInitDto info = response.getData();
                        if (null != info && null != info.intimacy_friend) {
                            for (int i = 0; i < info.intimacy_friend.size(); i++) {
                                atomicInteger.getAndIncrement();
                                ContactInfo contactInfo = new ContactInfo();
                                contactInfo.sessionType = SessionTypeEnum.P2P;
                                contactInfo.contactId = "" + info.intimacy_friend.get(i).getUser_id();
                                contactInfo.intimacy_val = info.intimacy_friend.get(i).intimacy;
//                                contactInfo.userId = info.intimacy_friend.get(i).getUser_id();
                                data.add(contactInfo);
                                RecentContact recentContactFromDB = msgService.queryRecentContact(contactInfo.contactId, SessionTypeEnum.P2P);
                                //如果之前不存在，创建一条空的会话记录
                                final RecentContact recent = recentContactFromDB != null ? recentContactFromDB :
                                        msgService.createEmptyRecentContact(contactInfo.contactId, SessionTypeEnum.P2P, 0, System.currentTimeMillis(), true);
                                HashMap<String, Object> selfAttach = new HashMap<>();
                                selfAttach.put(SELF_ATTACH, String.valueOf(info.intimacy_friend.get(i).intimacy));
                                recent.setExtension(selfAttach);
                                NIMClient.getService(MsgService.class).updateRecent(recent);
                            }
                            if (atomicInteger.get() >= info.intimacy_friend.size()) {
                                MMKVUtils.getInstance().encode(userProviderService.getUserId() + Constants.IS_FIRST_IN_MESSAGE, true);
                                getView().getChatUserInfo(data);
                            }
                        }
                    }

                }));

    }


    /**
     * 更新亲密度
     */
    public void updateIntimacy(List<ContactInfo> data) {
        if (data == null || data.size() <= 0) return;
        int size = data.size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(data.get(i).contactId);
            sb.append(",");
        }
        String contactId = sb.substring(0, sb.length() - 1);
        AtomicInteger atomicInteger = new AtomicInteger();
        MsgService msgService = NIMClient.getService(MsgService.class);
        addNet(service.updateIntimacy(contactId).compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<List<MessageIntimacyDto>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<MessageIntimacyDto>> response) {
                        List<MessageIntimacyDto> info = response.getData();
                        if (null != info && info.size() > 0) {
                            for (int i = 0; i < info.size(); i++) {
                                for (int j = 0; j < size; j++) {
                                    if (TextUtils.equals(info.get(i).user_id, data.get(j).contactId)) {
                                        atomicInteger.getAndIncrement();
                                        data.get(j).intimacy_val = Float.parseFloat(info.get(i).value);
                                        RecentContact recent = msgService.queryRecentContact(data.get(j).contactId, SessionTypeEnum.P2P);
                                        HashMap<String, Object> selfAttach = new HashMap<>();
                                        selfAttach.put(SELF_ATTACH, info.get(i).value);
                                        recent.setExtension(selfAttach);
                                        NIMClient.getService(MsgService.class).updateRecent(recent);
                                    }
                                }
                            }
                            if (atomicInteger.get() >= info.size()) {
                                getView().getChatUserInfo(data);
                                RxBus.getDefault().post(new MessageRefreshListEvent(info));
                            }
                        }
                    }
                }));
    }


    /**
     * 设置信息
     */
    public void setContactData(List<ContactInfo> loadedRecents, List<RecentContact> recents, AtomicInteger atomicInteger, int size) {
        List<String> uuids = new ArrayList<>();
//        ChatTabFragment.mapConTactInfo = new HashMap<>();
        for (RecentContact contact : recents) {
            ContactInfo contactInfo = setContactData(contact, false);
            uuids.add(contact.getRecentMessageId());
            loadedRecents.add(contactInfo);
//            ChatTabFragment.mapConTactInfo.put(contact.getContactId(), contactInfo);
        }
        HashMap<String, Object> map = new HashMap<>();
        NIMClient.getService(MsgService.class).queryMessageListByUuid(uuids).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                if (messages == null || messages.size() <= 0) return;
                for (IMMessage message : messages) {
                    map.put(message.getUuid(), message);
                }
            }
        });
        for (ContactInfo contact : loadedRecents) {
            singleThreadExecutor.execute(() -> {
                IMMessage message = (IMMessage) map.get(contact.getRecentMessageId());
                if (contact.extension == null) {   //是否有拓展字段
                    saveExtension(message, contact);
                } else {
                    String intimacy = (String) contact.getExtension().get(SELF_ATTACH);
                    if (intimacy != null && !TextUtils.isEmpty(intimacy) && intimacy.length() < 10) {
                        contact.intimacy_val = Float.parseFloat(intimacy);
                    }
                    String accostDesc = (String) contact.getExtension().get(BOY_ACCOST_DESC);
                    if (!TextUtils.isEmpty(accostDesc)) {
                        contact.accostDesc = accostDesc;
                    } else {
                        contact.accostDesc = "";
                    }
                }
                if (TextUtils.equals("[自定义消息]", contact.getContent())) {
                    String content = getMessage(BaseApplication.getInstance(), contact, message, String.valueOf(userProviderService.getUserId()), contact.getFromAccount());
                    contact.content = content;
                }
                atomicInteger.getAndIncrement();
                if (atomicInteger.get() >= size && getView() != null) {
                    getView().getContactInfoSuccess();
                }
            });
        }

//        for (IMMessage message : messages) {
//            LogUtil.e("======================", message.getAttachStr());
//        }
//        LogUtil.e("======================", messages.size() + "大小1111");
//        long one = System.currentTimeMillis();
//        for (RecentContact contact : recents) {
//            singleThreadExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    ContactInfo contactInfo = setContactData(contact, true);
//                    loadedRecents.add(contactInfo);
//                    atomicInteger.getAndIncrement();
//                    if (atomicInteger.get() >= size && getView() != null) {
//                        LogUtil.e("===================", atomicInteger.get() + "===========" + size);
//                        long endTime = System.currentTimeMillis();
//                        LogUtil.e("====================", "耗时：" + (endTime - one));
//                        getView().getContactInfoSuccess();
//                    }
//                }
//            });
//        }

    }

    /**
     * 拉黑后解除拉黑添加会话
     *
     * @param account
     */
    public void addContact(List<String> account) {
        if (account != null && account.size() > 0) {
            IMMessage anchor = MessageBuilder.createEmptyMessage(account.get(0), SessionTypeEnum.P2P, System.currentTimeMillis());
            //拉取云端消息到本地。注意：persist为true。
            NIMClient.getService(MsgService.class).pullMessageHistory(anchor, 10, true).setCallback(new RequestCallback<List<IMMessage>>() {
                @Override
                public void onSuccess(List<IMMessage> imMessages) {
                    //拉取到后，用最新的消息传参去创建一个新会话
                    if (imMessages != null && imMessages.size() > 0)
                        NIMClient.getService(MsgService.class).updateRecentByMessage(imMessages.get(0), true);
                }

                @Override
                public void onFailed(int i) {
                }

                @Override
                public void onException(Throwable throwable) {
                }
            });
        }
    }

    /**
     * 设置信息
     *
     * @param isQuery 是否查询最近一条消息，第一次不查询
     */
    public ContactInfo setContactData(RecentContact contact, boolean isQuery) {
        ContactInfo contactInfo = new ContactInfo();
        if (contact != null) {
            contactInfo.contactId = contact.getContactId();
            contactInfo.fromAccount = contact.getFromAccount();
            contactInfo.fromNick = contact.getFromNick();
            contactInfo.sessionType = contact.getSessionType();
            contactInfo.recentMessageId = contact.getRecentMessageId();
            contactInfo.time = contact.getTime();
            contactInfo.attachment = contact.getAttachment();
            contactInfo.msgType = contact.getMsgType();
            contactInfo.unreadCount = contact.getUnreadCount();
            contactInfo.msgStatus = contact.getMsgStatus();
            contactInfo.tag = contact.getTag();
            contactInfo.content = contact.getContent();
            contactInfo.extension = contact.getExtension();
            if (isQuery) {
                IMMessage message = NIMClient.getService(MsgService.class).queryLastMessage(contact.getContactId(), SessionTypeEnum.P2P);
                //存储扩展字段
                if (message != null && (message.getRemoteExtension() != null || message.getCallbackExtension() != null)) {
                    if (message.getRemoteExtension() != null) {   //接收到的消息
                        Map<String, Object> selfAttach = contact.getExtension();
                        String content = (String) message.getRemoteExtension().get(SELF_ATTACH);
                        if (!TextUtils.isEmpty(content)) {
                            ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(content);
                            if (null != chatMsg && null != chatMsg.msg_intimacy && !TextUtils.isEmpty(chatMsg.msg_intimacy.intimacy_val)) {   //消息回调（弹窗类型）
                                if (selfAttach == null)
                                    selfAttach = new HashMap<>();
                                selfAttach.put(SELF_ATTACH, chatMsg.msg_intimacy.intimacy_val);
                                contact.setExtension(selfAttach);
                                contactInfo.intimacy_val = Float.parseFloat(chatMsg.msg_intimacy.intimacy_val);
                            }
                            if (null != chatMsg) {   //消息回调（弹窗类型）
                                if (selfAttach == null)
                                    selfAttach = new HashMap<>();
                                if (!TextUtils.isEmpty(chatMsg.boy_accost_desc)) {
                                    selfAttach.put(BOY_ACCOST_DESC, chatMsg.boy_accost_desc);
                                } else {
                                    selfAttach.put(BOY_ACCOST_DESC, "");
                                }
                                contact.setExtension(selfAttach);
                                contactInfo.accostDesc = chatMsg.boy_accost_desc;
                            }
                        }
                    } else {   //自己发送的消息取
                        ChatMsg.CallBackMessage callBackMessage = JSON.parseObject(message.getCallbackExtension(), ChatMsg.CallBackMessage.class);
                        Map<String, Object> selfAttach = contact.getExtension();
                        if (callBackMessage != null && !TextUtils.isEmpty(callBackMessage.body)) {
                            ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(callBackMessage.body);
                            if (null != chatMsg) {   //消息回调（弹窗类型）
                                if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.CALL_TYPE)) {

                                } else {
                                    LogUtil.e("============", chatMsg.content);
                                    ChatMsg.Intimacy intimacy = JSON.parseObject(chatMsg.content, ChatMsg.Intimacy.class);
                                    if (intimacy != null && !TextUtils.isEmpty(intimacy.intimacy_val)) {
                                        if (selfAttach == null)
                                            selfAttach = new HashMap<>();
                                        selfAttach.put(SELF_ATTACH, intimacy.intimacy_val);
                                        contact.setExtension(selfAttach);
                                        contactInfo.intimacy_val = Float.parseFloat(intimacy.intimacy_val);
                                    }
                                }
                            }
                        }
                        if (selfAttach == null)
                            selfAttach = new HashMap<>();
                        selfAttach.put(BOY_ACCOST_DESC, "");
                        contact.setExtension(selfAttach);
                        contactInfo.accostDesc = "";
                    }
                    NIMClient.getService(MsgService.class).updateRecent(contact);
                }
            }
            if (contact.getExtension() != null) {
                String intimacy = (String) contact.getExtension().get(SELF_ATTACH);
                if (intimacy != null && !TextUtils.isEmpty(intimacy) && intimacy.length() < 10) {
                    contactInfo.intimacy_val = Float.parseFloat(intimacy);
                }
                contactInfo.extension = contact.getExtension();
            }
            if (TextUtils.equals("[自定义消息]", contact.getContent())) {
                IMMessage message = NIMClient.getService(MsgService.class).queryLastMessage(contact.getContactId(), SessionTypeEnum.P2P);
                String content = getMessage(BaseApplication.getInstance(), contactInfo, message, String.valueOf(userProviderService.getUserId()), contact.getFromAccount());
//                if (TextUtils.equals("[提示消息]", content)) {
//                    String messageContent = loadFromLocal(contactInfo, contact.getContactId(), contact.getTime(), QueryDirectionEnum.QUERY_OLD);
//                    contactInfo.content = messageContent;
//                } else {
//
//                }
                contactInfo.content = content;
            } else {
                contactInfo.content = contact.getContent();
            }
        }
        return contactInfo;
    }


    /**
     * 存储拓展字段
     */
    private void saveExtension(IMMessage message, ContactInfo contact) {
        //存储扩展字段
        if (message != null && (message.getRemoteExtension() != null || message.getCallbackExtension() != null)) {
            if (message.getRemoteExtension() != null) {   //接收到的消息
                Map<String, Object> selfAttach = contact.getExtension();
                String content = (String) message.getRemoteExtension().get(SELF_ATTACH);
                if (!TextUtils.isEmpty(content)) {
                    ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(content);
                    if (null != chatMsg && null != chatMsg.msg_intimacy && !TextUtils.isEmpty(chatMsg.msg_intimacy.intimacy_val)) {   //消息回调（弹窗类型）
                        if (selfAttach == null)
                            selfAttach = new HashMap<>();
                        selfAttach.put(SELF_ATTACH, chatMsg.msg_intimacy.intimacy_val);
                        contact.setExtension(selfAttach);
                        contact.intimacy_val = Float.parseFloat(chatMsg.msg_intimacy.intimacy_val);
                    }
                    if (null != chatMsg && null != chatMsg.vip) {   //消息回调（弹窗类型）
                        contact.is_vip = 1;
                        contact.picture_frame = 3;
                    } else {
                        contact.is_vip = 0;
                        contact.picture_frame = 0;
                    }

                }
            } else {   //自己发送的消息取
                ChatMsg.CallBackMessage callBackMessage = JSON.parseObject(message.getCallbackExtension(), ChatMsg.CallBackMessage.class);
                Map<String, Object> selfAttach = contact.getExtension();
                if (callBackMessage != null && !TextUtils.isEmpty(callBackMessage.body)) {
                    ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(callBackMessage.body);
                    if (null != chatMsg) {   //消息回调（弹窗类型）
                        ChatMsg.Intimacy intimacy = JSON.parseObject(chatMsg.content, ChatMsg.Intimacy.class);
                        if (intimacy != null && !TextUtils.isEmpty(intimacy.intimacy_val)) {
                            if (selfAttach == null)
                                selfAttach = new HashMap<>();
                            selfAttach.put(SELF_ATTACH, intimacy.intimacy_val);
                            contact.setExtension(selfAttach);
                            contact.intimacy_val = Float.parseFloat(intimacy.intimacy_val);
                        }
                    }
                }
            }
            NIMClient.getService(MsgService.class).updateRecent(contact);
        }


    }


    /**
     * 加载本地消息
     */
    private String loadFromLocal(ContactInfo contactInfo, String account, long time, QueryDirectionEnum direction) {
        final String[] messageContent = new String[1];
        NIMClient.getService(MsgService.class)
                .queryMessageListEx(MessageBuilder.createEmptyMessage(account, SessionTypeEnum.P2P, time), direction, 5, false)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> messages, Throwable exception) {
                        if (messages != null) {
                            for (IMMessage message : messages) {
                                if (TextUtils.isEmpty(message.getContent())) {
                                    String content = getMessage(BaseApplication.getInstance(), contactInfo, message, String.valueOf(userProviderService.getUserId()), account);
                                    if (TextUtils.isEmpty(message.getContent()) || TextUtils.equals("[礼物]", message.getContent()) || TextUtils.equals("call", message.getContent())) {
                                        messageContent[0] = content;
                                        contactInfo.content = messageContent[0];
                                        break;
                                    }
                                } else {
                                    messageContent[0] = message.getContent();
                                    contactInfo.content = messageContent[0];
                                    break;
                                }
                            }
                        }
                    }
                });
        return messageContent[0];
    }


    /**
     * 获取最近消息
     */
    public String getMessage(Context context, ContactInfo contactInfo, IMMessage message, String userId, String fromAccount) {
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

                            } else {
                                ChatMsg.Accost accost = JSON.parseObject(chatMsg.content, ChatMsg.Accost.class);
                                content = "送给Ta" + "[" + accost.gift_info.name + "]";
                            }
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.TIP_TYPE)) {
                        ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
                        if (TextUtils.equals(ChatMsg.ACCOST_TYPE, tips.from_type)) {
                            content = context.getString(R.string.message_accost);
                        } else if (TextUtils.equals(ChatMsg.ACCOST_EXPIRED_TYPE, tips.from_type)) {   //过期红包
                            content = context.getString(R.string.message_accost);
                        } else {
                            content = "[提示消息]";
                        }
                        contactInfo.from_type = tips.from_type;
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.GIFT_TYPE)) {
                        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
                        if (TextUtils.equals(userId, fromAccount)) {
                            content = "送给Ta" + "[" + gift.gift_info.name + "]";
                        } else {
                            content = "对方发来" + "[" + gift.gift_info.name + "]";
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.FAMILY_SHARE_TYPE)) {
                        ChatMsg.FamilyShareMessage msg = JSON.parseObject(chatMsg.content, ChatMsg.FamilyShareMessage.class);
                        if (msg != null) {
                            content = Base64.decode(msg.share_msg);
                        }
                    } else if (TextUtils.equals(chatMsg.cmd_type, ChatMsg.ACCOST_CARD)) {   //搭讪卡片信息
                        content = "[" + "卡片信息" + "]";
                    } else {
                        content = "";
                    }
                    contactInfo.cmd_type = chatMsg.cmd_type;
                    contactInfo.cmd = chatMsg.cmd;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }


    public void updateOfflineContactAited(final RecentContact recentContact) {
        if (recentContact == null || recentContact.getSessionType() != SessionTypeEnum.Team ||
                recentContact.getUnreadCount() <= 0) {
            return;
        }
        // 锚点
        List<String> uuid = new ArrayList<>(1);
        uuid.add(recentContact.getRecentMessageId());
        List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuid);
        if (messages == null || messages.size() < 1) {
            return;
        }
        final IMMessage anchor = messages.get(0);
        // 查未读消息
        NIMClient.getService(MsgService.class).queryMessageListEx(anchor, QueryDirectionEnum.QUERY_OLD,
                recentContact.getUnreadCount() - 1, false)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS && result != null) {
                            result.add(0, anchor);
                            Set<IMMessage> messages = null;
                            // 过滤存在的@我的消息
                            for (IMMessage msg : result) {
                                if (TeamMemberAitHelper.isAitMessage(msg)) {
                                    if (messages == null) {
                                        messages = new HashSet<>();
                                    }
                                    messages.add(msg);
                                }
                            }
                            // 更新并展示
                            if (messages != null && getView() != null) {
                                TeamMemberAitHelper.setRecentContactAited(recentContact, messages);
                                getView().notifyDataSetChanged();
                            }
                        }
                    }
                });

    }


    /**
     * 获取聊天中的历史记录消息
     */
    public void getChatHistory() {
        if (userProviderService  != null && userProviderService.getConfigInfo() != null && userProviderService.getConfigInfo().sys != null && userProviderService.getConfigInfo().sys.is_fix_chat_history == 1) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
            mHits[mHits.length-1] = SystemClock.uptimeMillis();
            if (mHits[mHits.length-1] - mHits[0] < 1000) { //一秒以内禁用重复请求
                return;
            }
        }

        addNet(service.getChatHistory().compose(applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<ChatHistoryDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<ChatHistoryDto> response) {
                        if (getView() == null || response.getData() == null) return;
                        getView().getChatHistorySuccess(response.getData());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                    }
                }));
    }


    /**
     * 查找未读的位置
     *
     * @return
     */
    public int findUnReadPosition(List<ContactInfo> items, RecyclerView recyclerView) {
        int position = 0;
        int size = items.size();
        int firstPosition = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
        int lastPosition = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));
        int lastNumPosition = -1, firstNumPosition = 0;
        for (int i = size - 1; i >= 0; i--) {
            if (items.get(i).getUnreadCount() > 0) {
                lastNumPosition = i;
                break;
            }
        }
        for (int i = 0; i < size; i++) {
            if (items.get(i).getUnreadCount() > 0) {
                firstNumPosition = i;
                break;
            }
        }
        for (int i = 0; i < size; i++) {
            int num = items.get(i).getUnreadCount();
            if (i > firstPosition && num > 0 && lastPosition != size - 1) {
                position = i;
                break;
            } else if (lastNumPosition == i) {//如果是已经定位到最后一条未读position了，返回第一条未读position
                position = firstNumPosition;
            }
        }
        return position;
    }

    /**
     * 移动到未读位置
     *
     * @param recyclerView
     * @param position
     */
    public void smoothMoveToPosition(RecyclerView recyclerView, final int position) {
        int firstItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
        int lastItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1));

        if (position < firstItem) {//往上定位
            // 跳转位置在第一个可见位置之前
            recyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {//往下定位
            // 跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < recyclerView.getChildCount()) {
                int top = recyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                recyclerView.smoothScrollBy(0, top);
            }
        } else {//未读item是最后一个的时候
            recyclerView.smoothScrollToPosition(position);
        }
    }


    public void setTranX(RecyclerView mRvMessage) {
        TranslateAnimation transAnim;
        transAnim = new TranslateAnimation(0, DensityUtils.dp2px(BaseApplication.getInstance(), -30), 0, 0);
        transAnim.setDuration(500);
        transAnim.setFillAfter(true);
        transAnim.start();
        mRvMessage.setAnimation(transAnim);
    }

    public void setBackTranX(RecyclerView mRvMessage) {
        TranslateAnimation transAnim;
        transAnim = new TranslateAnimation(DensityUtils.dp2px(BaseApplication.getInstance(), -30), 0, 0, 0);
        transAnim.setDuration(500);
        transAnim.setFillAfter(true);
        transAnim.start();
        mRvMessage.setAnimation(transAnim);
    }


    /**
     * 检查私信次数
     */
    public void getMsgCheck(String userId) {
        mineService.getMsgCheck(userId, new ResponseObserver<BaseResponse<MsgCheckDto>>() {
            @Override
            public void onSuccess(BaseResponse<MsgCheckDto> response) {
                if (null == getView()) return;
                getView().getCheckMsgSuccess(userId, response.getData());
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
            }
        });

    }


    /**
     * 搭讪用户
     */
    public void accostUser(int position, String userId, int accost_from) {
        accostService.accostUser(userId, 2, accost_from, new ResponseObserver<BaseResponse<AccostDto>>() {
            @Override
            public void onSuccess(BaseResponse<AccostDto> response) {
                if (null == getView()) return;
                getView().accostUserSuccess(position, response.getData());
            }
        });

    }
}
