package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.team.helper.TeamHelper;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.SpannableStringUtils;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * 群组礼物消息
 */
public class MsgViewHolderTeamGift extends MsgViewHolderBase {

    private AvatarImageView ivGift;
    private AvatarImageView ivHead;
    private TextView tvGift;
    private TextView tvName;
    private LinearLayout mLlContent;
    private TextView mTvReward;

    public MsgViewHolderTeamGift(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_team_gift;
    }

    @Override
    public void inflateContentView() {
        ivGift = findViewById(R.id.iv_gift);
        ivHead = findViewById(R.id.iv_avatar);
        tvName = findViewById(R.id.tv_name);
        tvGift = findViewById(R.id.tv_gift);
        mLlContent = findViewById(R.id.ll_content);
        mTvReward = findViewById(R.id.tv_reward);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
        try {
            refreshContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void layoutByDirection() {
        int gravity = Gravity.LEFT;
        if (isReceivedMessage()) {   //接收消息
            mLlContent.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
            gravity = Gravity.RIGHT;
        } else {
            mLlContent.setBackgroundResource(R.drawable.nim_message_right_white_bg);
            gravity = Gravity.LEFT;
        }
        mLlContent.setPadding(ScreenUtil.dip2px(15), 0, ScreenUtil.dip2px(15), 0);

        if (mTvReward != null) {
            if (message.getLocalExtension() != null && message.getLocalExtension().get("award") != null) {
                String award = (String) message.getLocalExtension().get("award");
                String color = (String) message.getLocalExtension().get("color");
                if (!TextUtils.isEmpty(award) && !TextUtils.isEmpty(color)) {
                    SpannableString spannableString = new SpannableString(award);
                    int start = 0;
                    if (!TextUtils.isEmpty(color)) {
                        start = spannableString.toString().indexOf(color);
                    }
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FC5858")), start, start + color.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (!TextUtils.isEmpty(award)) {
                        mTvReward.setText(spannableString);
                        mTvReward.setVisibility(View.VISIBLE);
                    } else {
                        mTvReward.setVisibility(View.GONE);
                    }
                }
            } else {
                mTvReward.setVisibility(View.GONE);
            }
        }

        //item 礼物面板增加中奖提示
        LinearLayout linearLayout = this.view.findViewById(R.id.message_item_body2);
        if (linearLayout != null && linearLayout.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params2.gravity = gravity;
            linearLayout.setLayoutParams(params2);
        } else if (linearLayout != null && linearLayout.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
            params2.gravity = gravity;
            linearLayout.setLayoutParams(params2);
        }
    }

    private void refreshContent() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
        if (gift != null) {
            if (isReceivedMessage()) {   //接收
                String account = "";
                if (gift.to != null && gift.to.size() > 0)
                    account = gift.to.get(0);
                ivGift.loadBuddyAvatar(account);
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate();          //缓存全尺寸
                Glide.with(context)                             //配置上下文
                        .asDrawable()
                        .apply(options)
                        .load(gift.gift_info.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .into(ivHead);

            } else {  //发送
                String account = "";
                if (gift.to != null && gift.to.size() > 0)
                    account = gift.to.get(0);
                ivHead.loadBuddyAvatar(account);
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate();          //缓存全尺寸
                Glide.with(context)                             //配置上下文
                        .asDrawable()
                        .apply(options)
                        .load(gift.gift_info.image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .into(ivGift);
            }
            tvGift.setText(gift.gift_info.name + "x" + gift.gift_num);

            // 添加盲盒中奖消息
            appendExt(message.getCallbackExtension());
            //添加家族联系礼物奖励倍数
            appendExtGift(gift, chatMsg.from);
            if (gift.to != null && gift.to.size() > 0) {
                tvName.setText(TeamHelper.getTeamMemberDisplayName(message.getSessionId(), gift.to.get(0)));
            }
        }
    }

    private void appendExt(String str) {
        ChatMsg cm = ChatMsgUtil.parseMessage(str);
        if (cm != null) {
            ChatMsg.Gift gift = JSON.parseObject(cm.content, ChatMsg.Gift.class);
            if (gift != null &&  gift.ext != null && gift.ext.get_gift != null && !TextUtils.isEmpty(gift.ext.get_gift.gift_name)) {
                mTvReward.setText(new SpannableStringUtils.Builder().append("开出了")
                        .append(gift.ext.get_gift.gift_name)
                        .setForegroundColor(Color.parseColor("#FC5858"))
                        .append("，恭喜恭喜!").create());
                mTvReward.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 家族礼物奖励倍数
     */
    private void appendExtGift(ChatMsg.Gift gift, String from) {
        if (gift != null) {
            if (!TextUtils.isEmpty(gift.msg) && !TextUtils.isEmpty(gift.from_nickname)) {
                if (!TextUtils.isEmpty(NimUIKitImpl.getAccount()) && !TextUtils.isEmpty(from)) {
                    mTvReward.setText(new SpannableStringUtils.Builder().append(NimUIKitImpl.getAccount().equals(from) ? "您" : gift.from_nickname)
                            .setForegroundColor(Color.parseColor("#FC5858"))
                            .append(gift.msg).create());
                    mTvReward.setVisibility(View.VISIBLE);
                }


            }
        }
    }

    @Override
    public void onItemClick() {
        if (NimUIKitImpl.getSessionListener() != null)
            NimUIKitImpl.getSessionListener().continueSendGift(message);
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }
}
