package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.VipUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;

import java.util.Map;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {

    protected TextView bodyTextView;
    private TextView tvIntegral;
    private LinearLayout llContent;

    public MsgViewHolderText(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    public void inflateContentView() {
        llContent = findViewById(R.id.ll_content);
        bodyTextView = findViewById(R.id.nim_message_item_text_body);
        tvIntegral = findViewById(R.id.tv_integral);
        tvRead = findViewById(R.id.tv_read);
    }

    @Override
    public void bindContentView() {
        layoutDirection();
    }

    private void layoutDirection() {
        //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bodyTextView.getLayoutParams();
        if (getDisplayText().length() <= 2) {
            bodyTextView.setGravity(Gravity.CENTER);
        } else {
            bodyTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        }
        if (isReceivedMessage()) {
            tvRead.setVisibility(View.GONE);
            tvIntegral.setVisibility(View.GONE);
            Map<String, Object> extension = message.getRemoteExtension();
            if (extension != null && extension.get("self_attach") != null) {
                setAttach((String) extension.get("self_attach"));
            }
            String attachStr = message.getAttachStr();   //拥有积分的时候
            if (!TextUtils.isEmpty(attachStr)) {
                setAttach(attachStr);
            }
            //bodyTextView.setBackgroundResource(getLeftBg());
            int leftBg = getLeftBg();
            VipUtils.setPersonalise(bodyTextView, leftBg, false);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bodyTextView.getLayoutParams();
            params.gravity = Gravity.START;
            bodyTextView.setLayoutParams(params);
        } else {
            setStatus();
            setRead();
            tvIntegral.setVisibility(View.GONE);
            int rightBg = getRightBg();
            VipUtils.setPersonalise(bodyTextView, rightBg, true);
            bodyTextView.setTextColor(NimUIKit.getContext().getResources().getColor(R.color.white));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bodyTextView.getLayoutParams();
            params.gravity = Gravity.END;
            bodyTextView.setLayoutParams(params);
            llContent.setGravity(Gravity.END);
        }
        //bodyTextView.setLayoutParams(lp);
        tvIntegral.setOnClickListener(v -> {
            if (NimUIKitImpl.getSessionListener() != null)
                NimUIKitImpl.getSessionListener().onAddAmount();
        });

        bodyTextView.setOnClickListener(v -> onItemClick());
        if (message.getSessionType() == SessionTypeEnum.Team && isReceivedMessage()) {
            MemberPushOption option = message.getMemberPushOption();
            if (option != null && (option.getForcePushList() != null && option.getForcePushList().size() > 0)) {
                MoonUtil.identifyFace(NimUIKit.getContext(), bodyTextView, getDisplayText(), option.getForcePushList(), option.getForcePushContent(), ImageSpan.ALIGN_BOTTOM);
            } else {
                MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
            }
        } else {
            MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        }


//        MoonUtil.identifyRecentVHFaceExpressionAndTags(NimUIKit.getContext(), bodyTextView,getDisplayText(), ImageSpan.ALIGN_BOTTOM, 0.45f);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        if(isReceivedMessage()){
            bodyTextView.setLinkTextColor(Color.BLACK);
        }else {
            bodyTextView.setLinkTextColor(Color.WHITE);
        }
        bodyTextView.setOnLongClickListener(longClickListener);
    }


    private void setAttach(String attach) {
        ChatMsg chatMsg = ChatMsgUtil.parseAttachMessage(attach);
        if (chatMsg != null && !TextUtils.isEmpty(chatMsg.msg_integral)) {
            tvIntegral.setText(chatMsg.msg_integral);
            if (chatMsg.msg_icon_type == 2) {
                tvIntegral.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NimUIKit.getContext(), R.drawable.peony_ltk_icon), null, null, null);
            } else {
                tvIntegral.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(NimUIKit.getContext(), R.drawable.nim_ic_chat_money), null, null, null);
            }
            tvIntegral.setVisibility(View.VISIBLE);
        } else {
            tvIntegral.setVisibility(View.GONE);
        }
    }


    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }
}
