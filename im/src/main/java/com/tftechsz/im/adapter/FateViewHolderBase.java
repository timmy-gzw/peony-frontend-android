package com.tftechsz.im.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.holder.RecyclerViewHolder;
import com.netease.nim.uikit.common.util.sys.TimeUtil;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.tftechsz.im.R;
import com.tftechsz.im.model.FateInfo;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;

import java.lang.reflect.Type;

public abstract class FateViewHolderBase extends RecyclerViewHolder<BaseMultiItemFetchLoadAdapter, BaseViewHolder, FateInfo> {

    public FateViewHolderBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
        this.adapter = adapter;
    }

    // basic
    protected View view;
    protected Context context;
    protected BaseMultiItemFetchLoadAdapter adapter;
    protected int layoutPosition;

    // data
    protected FateInfo mFateInfo;

    // view
    private RelativeLayout rlView;
//    protected View alertButton;
    protected TextView timeTextView;
    protected ProgressBar progressBar;
//    protected TextView nameTextView;
    protected FrameLayout contentContainer;
    protected LinearLayout contentContainerWithReplyTip; //包含回复提示的内容部分
    protected TextView replyTipAboveMsg; //消息列表中，显示在消息体上方的回复提示
    protected LinearLayout nameContainer;
    protected TextView readReceiptTextView;
    protected TextView ackMsgTextView;
//    protected TextView replyTipTextView; //回复消息时，显示在回复框上方的回复提示
    protected ImageView pinTipImg;
    protected TextView tvSex;

//    private AvatarImageView avatarLeft;
//    private AvatarImageView avatarRight;
    private RoundedImageView avatarRight;

    /**
     * 合并转发用多选框
     */
//    private CheckBox multiCheckBox;

    public ImageView nameIconView;

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


    protected int rightBackground() {
        return 0;
    }

    // 返回该消息是不是居中显示
    protected boolean isMiddleItem() {
        return false;
    }


    public int getHeight() {
        return layoutPosition;
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
    protected final FateAdapter getFateAdapter() {
        return (FateAdapter) adapter;
    }



    // 设置FrameLayout子控件的gravity参数
    protected final void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
        view.setLayoutParams(params);
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
        return false;
    }

    /// -- 以下是基类实现代码
    @Override
    public void convert(BaseViewHolder holder, FateInfo fateInfo, int position, boolean isScrolling) {
        view = holder.getConvertView();
        context = holder.getContext();
        mFateInfo = fateInfo;
        layoutPosition = holder.getLayoutPosition();


        inflate();
        refresh();
        bindHolder(holder);
    }

    protected final void inflate() {
        rlView = findViewById(R.id.rl_view);
        timeTextView = findViewById(R.id.message_item_time);
        FrameLayout view = findViewById(R.id.message_item_body_layout);
//        avatarRight = findViewById(R.id.message_item_portrait_right);
        avatarRight  = findViewById(R.id.message_item_rounded_right);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.message_item_rounded_right);
        view.setLayoutParams(layoutParams);
//        multiCheckBox = findViewById(R.id.message_item_multi_check_box);
//        alertButton = findViewById(R.id.message_item_alert);
        progressBar = findViewById(R.id.message_item_progress);
//        nameTextView = findViewById(R.id.message_item_nickname);
        contentContainer = findViewById(R.id.message_item_content);
        contentContainerWithReplyTip = findViewById(R.id.message_item_container_with_reply_tip);
        replyTipAboveMsg = findViewById(R.id.tv_reply_tip_above_msg);
        nameIconView = findViewById(R.id.message_item_name_icon);
        nameContainer = findViewById(R.id.message_item_name_layout);
        readReceiptTextView = findViewById(R.id.textViewAlreadyRead);
        ackMsgTextView = findViewById(R.id.team_ack_msg);
        pinTipImg = findViewById(R.id.message_item_pin);
//        replyTipTextView = findViewById(R.id.message_item_reply);
        tvSex = findViewById(R.id.tv_sex);
        // 这里只要inflate出来后加入一次即可
        if (contentContainer.getChildCount() == 0) {
            View.inflate(view.getContext(), getContentResId(), contentContainer);
        }
        inflateContentView();
    }

    protected final void refresh() {
        //如果是avchat类消息，先根据附件的from字段重置消息的方向和发送者ID
//        MessageHelper.adjustAVChatMsgDirect(message);
        setHeadImageView();
        setTimeTextView();
        setOnClickListener();
        setContent();
        bindContentView();
    }

    public void refreshCurrentItem() {
        if (mFateInfo != null) {
            refresh();
        }
    }

    /**
     * 设置时间显示
     */
    private void setTimeTextView() {
        if (getFateAdapter().needShowTime(mFateInfo)) {
            timeTextView.setVisibility(View.VISIBLE);
        } else {
            timeTextView.setVisibility(View.GONE);
            return;
        }

        String text = TimeUtil.getTimeShowString(mFateInfo.getCreated_at() * 1000, false);
        timeTextView.setText(text);
        timeTextView.setBackgroundResource(R.color.transparent);
        timeTextView.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
    }

    private void setHeadImageView() {
        avatarRight.setVisibility(View.VISIBLE);
        loadBuddyAvatar(String.valueOf(mFateInfo.getUser_id()));
//
    }

    public void loadBuddyAvatar(String account) {
        if (null == context)
            return;
        try {
            final UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
            String url = "";
            if (userInfo != null && null != getConfig(context) && null != getConfig(context).api && null != getConfig(context).api.oss && null != getConfig(context).api.oss.cdn)
                url = getConfig(context).api.oss.cdn_scheme + getConfig(context).api.oss.cdn.user + url + userInfo.getAvatar();
            GlideUtils.loadRouteImage(context, avatarRight, url, R.mipmap.mine_ic_girl_default);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ConfigInfo getConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("tfpeony-pref",
                Context.MODE_PRIVATE);
        String config = null;
        if (TextUtils.isEmpty(config)) {
            config = sp.getString("configInfo", "");
        }

        Gson gson = new Gson();
        ConfigInfo configInfo = gson.fromJson(config, (Type) ConfigInfo.class);
        return configInfo;
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
        // 内容区域点击事件响应， 相当于点击了整项
        contentContainer.setOnClickListener((View v) ->{ onItemClick();});

        // 头像点击事件响应
        if (avatarRight != null) {
            avatarRight.setOnClickListener((View v) ->{
                ARouterUtils.toMineDetailActivity("");
            });
        }
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
        replyTipAboveMsg.setVisibility(View.GONE);

    }


    /**
     * 设置性别
     *
     * @param sex 性别 1：男 2：女
     * @param age 年龄
     */
    public static void setSexAndAge(Context context, int sex, int age, TextView view) {
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
    }


}
