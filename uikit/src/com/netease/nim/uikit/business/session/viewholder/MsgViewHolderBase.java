package com.netease.nim.uikit.business.session.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.session.SessionCustomization;
import com.netease.nim.uikit.business.session.helper.MessageHelper;
import com.netease.nim.uikit.business.session.module.list.MsgAdapter;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.CommonUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.UIUtils;
import com.netease.nim.uikit.common.UserInfo;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.holder.RecyclerViewHolder;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.log.sdk.wrapper.NimLog;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NIMSDK;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MsgThreadOption;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * 会话窗口消息列表项的ViewHolder基类，负责每个消息项的外层框架，包括头像，昵称，发送/接收进度条，重发按钮等。<br>
 * 具体的消息展示项可继承该基类，然后完成具体消息内容展示即可。
 */
public abstract class MsgViewHolderBase extends RecyclerViewHolder<BaseMultiItemFetchLoadAdapter, BaseViewHolder, IMMessage> {

    public MsgViewHolderBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
        this.adapter = adapter;
    }

    // basic
    protected View view;
    protected Context context;
    protected BaseMultiItemFetchLoadAdapter adapter;
    protected int layoutPosition;

    // data
    protected IMMessage message;

    // view
    private RelativeLayout rlView;
    protected View alertButton;
    protected TextView timeTextView;
    protected ProgressBar progressBar;
    protected TextView nameTextView, nameTextView2;
    protected FrameLayout contentContainer;
    protected LinearLayout contentContainerWithReplyTip; //包含回复提示的内容部分
    protected TextView replyTipAboveMsg; //消息列表中，显示在消息体上方的回复提示
    protected LinearLayout nameContainer, nameContainer2;
    protected TextView readReceiptTextView;
    protected TextView ackMsgTextView;
    protected TextView replyTipTextView; //回复消息时，显示在回复框上方的回复提示
    protected ImageView pinTipImg, Ivbadge, Ivbadge2;
    protected TextView tvSex, tvSex2;
    protected TextView tvRead;
    protected TextView tvRole, tvRole2, tvNewPeople, tvNewPeople2, tvCouple, tvCouple2;  //
    private AvatarVipFrameView avatarLeft;
    private AvatarVipFrameView avatarRight;
    private String userInfo;
    private UserInfo mUserInfo;
    private NimUserInfo nimUserInfo;
    /**
     * 合并转发用多选框
     */
    private CheckBox multiCheckBox;

    public ImageView nameIconView, nameIconView2;

    // contentContainerView的默认长按事件。如果子类需要不同的处理，可覆盖onItemLongClick方法
    // 但如果某些子控件会拦截触摸消息，导致contentContainer收不到长按事件，子控件也可在inflate时重新设置
    protected View.OnLongClickListener longClickListener;

    /// -- 以下接口可由子类覆盖或实现
    // 返回具体消息类型内容展示区域的layout res id
    abstract public int getContentResId();

    // 在该接口中根据layout对各控件成员变量赋值
    abstract public void inflateContentView();

    // 在该接口操作BaseViewHolder中的数据，进行事件绑定，可选
    protected void bindHolder(BaseViewHolder holder) {

    }

    // 将消息数据项与内容的view进行绑定
    abstract public void bindContentView();

    // 内容区域点击事件响应处理。
    public void onItemClick() {
    }

    // 内容区域长按事件响应处理。该接口的优先级比adapter中有长按事件的处理监听高，当该接口返回为true时，adapter的长按事件监听不会被调用到。
    protected boolean onItemLongClick() {
        return false;
    }

    // 当是接收到的消息时，内容区域背景的drawable id
    protected int leftBackground() {
        return 0;
    }

    // 当是发送出去的消息时，内容区域背景的drawable id
//    protected int rightBackground() {
//        return NimUIKitImpl.getOptions().messageRightBackground;
//    }

    protected int rightBackground() {
        return 0;
    }

    // 返回该消息是不是居中显示
    protected boolean isMiddleItem() {
        return false;
    }

    //为Thread消息的设置回复提示语
    protected void setBeRepliedTip() {
        int count = 0;
        if (message.isThread()) {
            count = NIMClient.getService(MsgService.class).queryReplyCountInThreadTalkBlock(message);
        }
        if (count <= 0) {
            replyTipTextView.setVisibility(View.GONE);
            return;
        }
        replyTipTextView.setText(String.format(context.getResources().getString(R.string.reply_with_amount), String.valueOf(count)));
        replyTipTextView.setVisibility(View.VISIBLE);
    }

    protected void setReplyTip() {
        if (message.isThread()) {
            replyTipAboveMsg.setVisibility(View.GONE);
            return;
        }
        replyTipAboveMsg.setText(getReplyTip());
        replyTipAboveMsg.setVisibility(View.VISIBLE);
    }


    public int getHeight() {
        return layoutPosition;
    }

    protected String getReplyTip() {
        //thread消息没有回复对象
        if (message.isThread()) {
            return "";
        }
        MsgThreadOption threadOption = message.getThreadOption();
        String replyFrom = threadOption.getReplyMsgFromAccount();
        if (TextUtils.isEmpty(replyFrom)) {
            NimLog.w("MsgViewHolderBase", "no reply message found, uuid=" + message.getUuid());
            return "";
        }
        String fromDisplayName = UserInfoHelper.getUserDisplayNameInSession(replyFrom, message.getSessionType(), message.getSessionId());

        String replyUuid = threadOption.getReplyMsgIdClient();
        String content = getMessageBrief(replyUuid, "...");

        return String.format(context.getString(R.string.reply_with_message), fromDisplayName, content);
    }

    protected String getMessageBrief(String uuid, String defaultValue) {
        if (TextUtils.isEmpty(uuid)) {
            return defaultValue;
        }
        List<String> uuidList = new ArrayList<>(1);
        uuidList.add(uuid);
        List<IMMessage> msgList = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuidList);
        if (msgList == null || msgList.isEmpty()) {
            return defaultValue;
        }
        IMMessage msg = msgList.get(0);
        SessionCustomization sessionCustomization = SessionTypeEnum.P2P == msg.getSessionType() ?
                NimUIKit.getCommonP2PSessionCustomization() : NimUIKit.getCommonTeamSessionCustomization();
        return sessionCustomization.getMessageDigest(msg);
    }

    // 是否显示头像，默认为显示
    protected boolean isShowHeadImage() {
        return true;
    }

    // 是否显示气泡背景，默认为显示
    protected boolean isShowBubble() {
        return true;
    }

    // 是否显示已读，默认为显示
    protected boolean shouldDisplayReceipt() {
        return true;
    }

    /// -- 以下接口可由子类调用
    protected final MsgAdapter getMsgAdapter() {
        return (MsgAdapter) adapter;
    }

    protected boolean shouldDisplayNick() {
        return message.getSessionType() == SessionTypeEnum.Team /*&& isReceivedMessage()*/ && !isMiddleItem();
    }


    /**
     * 下载附件/缩略图
     */
    protected void downloadAttachment(RequestCallback<Void> callback) {
        if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
            NIMClient.getService(MsgService.class).downloadAttachment(message, true).setCallback(callback);
    }

    // 设置FrameLayout子控件的gravity参数
    protected final void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams paramsFrame = null;
        LinearLayout.LayoutParams paramsLinear = null;
        if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            paramsFrame = (FrameLayout.LayoutParams) view.getLayoutParams();
        } else {
            paramsLinear = (LinearLayout.LayoutParams) view.getLayoutParams();
        }
        if (paramsFrame != null) {
            paramsFrame.gravity = gravity;
            view.setLayoutParams(paramsFrame);
        } else if (paramsLinear != null) {
            paramsLinear.gravity = gravity;
        }

    }


    // 设置控件的长宽
    protected void setLayoutParams(int width, int height, View... views) {
        for (View view : views) {
            ViewGroup.LayoutParams maskParams = view.getLayoutParams();
            maskParams.width = width;
            maskParams.height = height;
            view.setLayoutParams(maskParams);
        }
    }

    // 根据layout id查找对应的控件
    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    // 判断消息方向，是否是接收到的消息
    protected boolean isReceivedMessage() {
        return message.getDirect() == MsgDirectionEnum.In;
    }

    /// -- 以下是基类实现代码
    @Override
    public void convert(BaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        view = holder.getConvertView();
        context = holder.getContext();
        message = data;
        layoutPosition = holder.getLayoutPosition();


        inflate();
        refresh();
        bindHolder(holder);
    }

    public void initParameter(View itemView, Context context, IMMessage data, int position) {
        view = itemView;
        this.context = context;
        message = data;
        layoutPosition = position;


        timeTextView = new TextView(context);
        avatarLeft = new AvatarVipFrameView(context);
        avatarRight = new AvatarVipFrameView(context);
        multiCheckBox = new CheckBox(context);
        alertButton = new View(context);
        progressBar = new ProgressBar(context);
        nameTextView = new TextView(context);
        nameTextView2 = new TextView(context);
        contentContainer = new FrameLayout(context);
        contentContainerWithReplyTip = new LinearLayout(context);
        replyTipAboveMsg = new TextView(context);
        nameIconView = new ImageView(context);
        nameIconView2 = new ImageView(context);
        nameContainer = new LinearLayout(context);
        nameContainer2 = new LinearLayout(context);
        readReceiptTextView = new TextView(context);
        ackMsgTextView = new TextView(context);

    }

    protected final void inflate() {
        rlView = findViewById(R.id.rl_view);
        timeTextView = findViewById(R.id.message_item_time);
        avatarLeft = findViewById(R.id.message_item_portrait_left);
        avatarRight = findViewById(R.id.message_item_portrait_right);
        multiCheckBox = findViewById(R.id.message_item_multi_check_box);
        alertButton = findViewById(R.id.message_item_alert);
        progressBar = findViewById(R.id.message_item_progress);
        nameTextView = findViewById(R.id.message_item_nickname);
        nameTextView2 = findViewById(R.id.message_item_nickname2);
        contentContainer = findViewById(R.id.message_item_content);
        contentContainerWithReplyTip = findViewById(R.id.message_item_container_with_reply_tip);
        replyTipAboveMsg = findViewById(R.id.tv_reply_tip_above_msg);
        nameIconView = findViewById(R.id.message_item_name_icon);
        nameIconView2 = findViewById(R.id.message_item_name_icon2);
        nameContainer = findViewById(R.id.message_item_name_layout);
        nameContainer2 = findViewById(R.id.message_item_name_layout2);
        readReceiptTextView = findViewById(R.id.textViewAlreadyRead);
        ackMsgTextView = findViewById(R.id.team_ack_msg);
        pinTipImg = findViewById(R.id.message_item_pin);
        replyTipTextView = findViewById(R.id.message_item_reply);
        Ivbadge = findViewById(R.id.noble_label);
        Ivbadge2 = findViewById(R.id.noble_label2);
        tvSex = findViewById(R.id.tv_sex);
        tvSex2 = findViewById(R.id.tv_sex2);
        tvRole = findViewById(R.id.tv_role);
        tvRole2 = findViewById(R.id.tv_role2);
        tvNewPeople = findViewById(R.id.tv_new_people);
        tvNewPeople2 = findViewById(R.id.tv_new_people2);
        tvCouple = findViewById(R.id.tv_couple);
        tvCouple2 = findViewById(R.id.tv_couple2);
        // 这里只要inflate出来后加入一次即可
        if (contentContainer.getChildCount() == 0) {
            View.inflate(view.getContext(), getContentResId(), contentContainer);
        }
        inflateContentView();
        mUserInfo = getUserInfo(context);
        nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(message.getFromAccount());
    }

    protected final void refresh() {
        //如果是avchat类消息，先根据附件的from字段重置消息的方向和发送者ID
        MessageHelper.adjustAVChatMsgDirect(message);
        nameContainer2.setVisibility(isReceivedMessage() ? View.INVISIBLE : View.VISIBLE);
        nameContainer.setVisibility(isReceivedMessage() ? View.VISIBLE : View.INVISIBLE);
        setHeadImageView();
        setNameTextView();
        setTimeTextView();
        setStatus();
        setOnClickListener();
        setLongClickListener();
        setContent();
//        setExtension();
        setReadReceipt();
        setAckMsg();
        setMultiCheckBox();
        bindContentView();
//        setRead();
    }

    public void refreshCurrentItem() {
        if (message != null) {
            refresh();
        }
    }

    /**
     * 设置时间显示
     */
    private void setTimeTextView() {
        if (getMsgAdapter().needShowTime(message)) {
            timeTextView.setVisibility(View.VISIBLE);
        } else {
            timeTextView.setVisibility(View.GONE);
            return;
        }

        String text = TimeUtil.getTimeShowString(message.getTime(), false);
        timeTextView.setText(text);
//        if (message.getSessionType() == SessionTypeEnum.Team) {
//
//        }
        timeTextView.setBackgroundResource(R.color.transparent);
        timeTextView.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
    }

    /**
     * 设置消息发送状态
     */
    public void setStatus() {
        MsgStatusEnum status = message.getStatus();
        switch (status) {
            case fail:
                progressBar.setVisibility(View.GONE);
                alertButton.setVisibility(View.VISIBLE);
                break;
            case sending:
                progressBar.setVisibility(View.VISIBLE);
                alertButton.setVisibility(View.GONE);
                break;
            default:
                progressBar.setVisibility(View.GONE);
                alertButton.setVisibility(View.GONE);
                break;
        }
    }

    private void setHeadImageView() {
        AvatarVipFrameView show = isReceivedMessage() ? avatarLeft : avatarRight;
        AvatarVipFrameView hide = isReceivedMessage() ? avatarRight : avatarLeft;
        hide.setVisibility(View.GONE);
        if (!isShowHeadImage()) {
            show.setVisibility(View.GONE);
            return;
        }
        if (isMiddleItem()) {
            show.setVisibility(View.GONE);
        } else {
            show.setBgFrame(-1);
            show.setVisibility(View.VISIBLE);
            show.setAvatar(message);
            if (isReceivedMessage()) {
                if (nimUserInfo != null && nimUserInfo.getExtension() != null) {
                    try {
                        ChatMsg.Vip vip = JSON.parseObject(nimUserInfo.getExtension(), ChatMsg.Vip.class);
                        if (vip != null) {
                            show.setChatBgFrame(vip.picture_frame);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (mUserInfo != null) {
                    show.setChatBgFrame(mUserInfo.picture_frame);
                }
            }
        }
    }

    public int getLeftBg() {
        int bg = 0;
//        Map<String, Object> extension = message.getRemoteExtension();
//        if (extension != null && extension.get("self_attach") != null) {
//            ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage((String) extension.get("self_attach"));
//            if (chatMsg != null && chatMsg.vip != null) {
//                bg = VipUtils.getChatBubbleBackground(chatMsg.vip.chat_bubble, true);
//            }
//        }
        if (nimUserInfo != null && nimUserInfo.getExtension() != null) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(nimUserInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    bg = vip.chat_bubble;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bg;
    }


    public int getRightBg() {
        int bg = 0;
        if (mUserInfo != null) {
            bg = mUserInfo.chat_bubble;
        }
        return bg;
    }


    public void setRightInVisible() {
        avatarRight.setVisibility(View.INVISIBLE);
    }


    public void setRlView(boolean show) {
        rlView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            ViewGroup.LayoutParams params = rlView.getLayoutParams();
            rlView.setPadding(0, DensityUtils.dp2px(context, 10), 0, DensityUtils.dp2px(context, 10));
            rlView.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = rlView.getLayoutParams();
            params.height = 0;
            rlView.setLayoutParams(params);
        }
    }

    private void setOnClickListener() {
        //消息是否处于可被选择状态，true: 点击只能改变被选择状态; false: 点击可执行消息的点击事件
        boolean inNormalMode = message.isChecked() == null;
        multiCheckBox.setOnClickListener((v) -> getMsgAdapter().getEventListener().onCheckStateChanged(layoutPosition, multiCheckBox.isChecked()));
        if (!inNormalMode) {
            alertButton.setClickable(false);
            contentContainer.setClickable(false);
            avatarLeft.setClickable(false);
            avatarRight.setClickable(false);
            ackMsgTextView.setClickable(false);
            return;
        }
        // 重发/重收按钮响应事件
        if (getMsgAdapter().getEventListener() != null) {
            alertButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getMsgAdapter().getEventListener().onFailedBtnClick(message);
                }
            });
        }

        // 内容区域点击事件响应， 相当于点击了整项
        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });


        // 头像点击事件响应
        if (NimUIKitImpl.getSessionListener() != null) {
            View.OnClickListener portraitListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NimUIKitImpl.getSessionListener() != null && context != null && message != null)
                        NimUIKitImpl.getSessionListener().onAvatarClicked(context, message);
                }
            };
            avatarLeft.setOnClickListener(portraitListener);
            avatarRight.setOnClickListener(portraitListener);
        }
        // 已读回执响应事件
        if (NimUIKitImpl.getSessionListener() != null) {
            ackMsgTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NimUIKitImpl.getSessionListener().onAckMsgClicked(context, message);
                }
            });
        }
    }

    /**
     * item长按事件监听
     */
    private void setLongClickListener() {
        longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 优先派发给自己处理，
                if (!onItemLongClick()) {
                    if (getMsgAdapter().getEventListener() != null) {
                        getMsgAdapter().getEventListener().onViewHolderLongClick(contentContainer, view, message);
                        return true;
                    }
                }
                return false;
            }
        };
        // 消息长按事件响应处理
        contentContainer.setOnLongClickListener(longClickListener);

        // 头像长按事件响应处理
        if (NimUIKitImpl.getSessionListener() != null) {
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (NimUIKitImpl.getSessionListener() != null) {
                        NimUIKitImpl.getSessionListener().onAvatarLongClicked(context, message);
                        return true;
                    }
                    return false;
                }
            };
            avatarLeft.setOnLongClickListener(longClickListener);
            //avatarRight.setOnLongClickListener(longClickListener);
        }
    }

    private void setNameTextView() {
        if (!shouldDisplayNick()) {
            nameTextView.setVisibility(View.GONE);
            nameTextView2.setVisibility(View.GONE);
            tvRole.setVisibility(View.GONE);
            tvRole2.setVisibility(View.GONE);
            tvNewPeople.setVisibility(View.GONE);
            tvNewPeople2.setVisibility(View.GONE);
            tvCouple.setVisibility(View.GONE);
            tvCouple2.setVisibility(View.GONE);
            tvSex.setVisibility(View.GONE);
            tvSex2.setVisibility(View.GONE);
            Ivbadge.setVisibility(View.GONE);
            Ivbadge2.setVisibility(View.GONE);
            return;
        }

        Ivbadge.setVisibility(View.GONE);
        Ivbadge.setImageDrawable(null);
        Ivbadge2.setVisibility(View.GONE);
        Ivbadge2.setImageDrawable(null);
        nameTextView.setVisibility(View.VISIBLE);
        nameTextView.setText(getNameText());
        nameTextView2.setVisibility(View.VISIBLE);
        nameTextView2.setText(getNameText());
        tvSex.setVisibility(View.VISIBLE);
        tvSex2.setVisibility(View.VISIBLE);
        tvRole.setBackgroundResource(R.color.transparent);
        tvRole2.setBackgroundResource(R.color.transparent);
        tvNewPeople.setBackgroundResource(R.color.transparent);
        tvNewPeople2.setBackgroundResource(R.color.transparent);
        tvCouple.setBackgroundResource(R.color.transparent);
        tvCouple2.setBackgroundResource(R.color.transparent);
        if (nimUserInfo != null && nimUserInfo.getGenderEnum() != null) {
            setSexAndAge(context, nimUserInfo.getGenderEnum().getValue(), StringUtil.getAgeFromBirthTime(nimUserInfo.getBirthday()), tvSex);
            setSexAndAge(context, nimUserInfo.getGenderEnum().getValue(), StringUtil.getAgeFromBirthTime(nimUserInfo.getBirthday()), tvSex2);
        }

        if (nimUserInfo != null && nimUserInfo.getExtension() != null) {
            try {
                ChatMsg.Vip vip = JSON.parseObject(nimUserInfo.getExtension(), ChatMsg.Vip.class);
                if (vip != null) {
                    UIUtils.setFamilyTitle(tvRole, vip.role_id);
                    UIUtils.setFamilyTitle(tvRole2, vip.role_id);
                    LogUtil.e("====================", vip.role_id + "=======");
                    if (vip.new_tag == 1) {
                        tvNewPeople.setVisibility(View.VISIBLE);
                        tvNewPeople.setBackgroundResource(R.drawable.chat_ic_new_people);
                        tvNewPeople2.setVisibility(View.VISIBLE);
                        tvNewPeople2.setBackgroundResource(R.drawable.chat_ic_new_people);
                    } else {
                        tvNewPeople.setVisibility(View.GONE);
                        tvNewPeople2.setVisibility(View.GONE);
                    }
                    if (vip.is_couple == 1) {
                        tvCouple.setVisibility(View.VISIBLE);
                        tvCouple.setBackgroundResource(R.drawable.chat_ic_couple);
                        tvCouple2.setVisibility(View.VISIBLE);
                        tvCouple2.setBackgroundResource(R.drawable.chat_ic_couple);
                    } else {
                        tvCouple.setVisibility(View.GONE);
                        tvCouple2.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(vip.badge_small)) {
                        CommonUtil.setBadge(Ivbadge, vip.badge_small);
                        CommonUtil.setBadge(Ivbadge2, vip.badge_small);
                    } else {
                        CommonUtil.setBadge(Ivbadge, vip.badge);
                        CommonUtil.setBadge(Ivbadge2, vip.badge);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public UserInfo getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(userInfo)) {
            userInfo = sp.getString("userInfo", "");
        }
        UserInfo userInfo = JSON.parseObject(this.userInfo, UserInfo.class);
        return userInfo;
    }


    protected String getNameText() {
        if (message.getSessionType() == SessionTypeEnum.Team) {
            return TeamHelper.getDisplayNameWithoutMe(message.getSessionId(), message.getFromAccount());
        }
        return "";
    }

    private void setContent() {
        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }

        LinearLayout bodyContainer = (LinearLayout) view.findViewById(R.id.message_item_body);

        // 调整container的位置
        int index = isReceivedMessage() ? 0 : 4;
        if (bodyContainer.getChildAt(index) != contentContainerWithReplyTip) {
            bodyContainer.removeView(contentContainerWithReplyTip);
            bodyContainer.addView(contentContainerWithReplyTip, index);
        }

        if (isMiddleItem()) {
            setGravity(bodyContainer, Gravity.CENTER);
        } else {
            if (isReceivedMessage()) {
                setGravity(bodyContainer, Gravity.LEFT);
                contentContainerWithReplyTip.setBackgroundResource(leftBackground());
                replyTipAboveMsg.setTextColor(Color.BLACK);
            } else {
                setGravity(bodyContainer, Gravity.RIGHT);
                contentContainerWithReplyTip.setBackgroundResource(rightBackground());
                replyTipAboveMsg.setTextColor(Color.WHITE);
            }
        }
    }

    private void setExtension() {
        if (!isShowBubble() && !isMiddleItem()) {
            return;
        }

        LinearLayout extensionContainer = view.findViewById(R.id.message_item_extension);

        // 调整扩展功能提示的位置
        int index = isReceivedMessage() ? 0 : 1;
        if (extensionContainer.getChildAt(index) != pinTipImg) {
            extensionContainer.removeView(pinTipImg);
            extensionContainer.addView(pinTipImg, index);
        }

        if (isMiddleItem()) {
            return;
        }
        setGravity(extensionContainer, isReceivedMessage() ? Gravity.LEFT : Gravity.RIGHT);
        setBeRepliedTip();
        setReplyTip();
    }

    /**
     * 发送消息已读
     */
    private void setReadReceipt() {
        if (shouldDisplayReceipt() && !TextUtils.isEmpty(getMsgAdapter().getUuid()) && message.getUuid().equals(getMsgAdapter().getUuid())) {
            readReceiptTextView.setVisibility(View.GONE);
        } else {
            readReceiptTextView.setVisibility(View.GONE);
        }
    }

    protected void setRead() {
        if (tvRead != null && mUserInfo != null && mUserInfo.isVip() && message.getSessionType() == SessionTypeEnum.P2P) {
            tvRead.setVisibility(View.VISIBLE);
            if (message.isRemoteRead()) {
                tvRead.setTextColor(Color.parseColor("#cccccc"));
                tvRead.setText("已读");
            } else {
                tvRead.setText("未读");
                tvRead.setTextColor(Color.parseColor("#7F89F3"));
            }
        }

    }


    public void setReSendGone() {
        alertButton.setVisibility(View.GONE);
    }


    private void setAckMsg() {
        if (message.getSessionType() == SessionTypeEnum.Team && message.needMsgAck()) {
            if (isReceivedMessage()) {
                // 收到的需要已读回执的消息，需要给个反馈
                ackMsgTextView.setVisibility(View.GONE);
                NIMSDK.getTeamService().sendTeamMessageReceipt(message);
            } else {
                // 自己发的需要已读回执的消息，显示未读人数
                ackMsgTextView.setVisibility(View.VISIBLE);
                if (message.getTeamMsgAckCount() == 0 && message.getTeamMsgUnAckCount() == 0) {
                    ackMsgTextView.setText("还未查看");
                } else {
                    ackMsgTextView.setText(message.getTeamMsgUnAckCount() + "人未读");
                }
            }
        } else {
            ackMsgTextView.setVisibility(View.GONE);
        }
    }

    private void setMultiCheckBox() {
        Boolean selectState = message.isChecked();
        multiCheckBox.setVisibility(selectState == null ? View.GONE : View.VISIBLE);
        if (Boolean.TRUE.equals(selectState)) {
            multiCheckBox.setChecked(true);
        } else if (Boolean.FALSE.equals(selectState)) {
            multiCheckBox.setChecked(false);
        }
    }

    /**
     * 设置性别
     *
     * @param sex 性别 1：男 2：女
     * @param age 年龄
     */
    public void setSexAndAge(Context context, int sex, int age, TextView view) {
        Drawable boy = ContextCompat.getDrawable(context, R.drawable.ic_boy);
        Drawable girl = ContextCompat.getDrawable(context, R.drawable.ic_girl);
        view.setVisibility(View.VISIBLE);
        view.setBackgroundResource(sex == 1 ? R.drawable.bg_boy : R.drawable.bg_girl);
        if (sex == 1) {
            view.setCompoundDrawablesWithIntrinsicBounds(boy,
                    null, null, null);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(girl,
                    null, null, null);
        }
        view.setText(String.valueOf(age));
        //view.setCompoundDrawablePadding(-ConvertUtils.dp2px(3));
    }


}
