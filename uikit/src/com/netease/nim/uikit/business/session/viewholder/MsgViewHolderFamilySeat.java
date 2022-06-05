package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderFamilySeat extends MsgViewHolderBase {
    protected TextView bodyTextView;

    public MsgViewHolderFamilySeat(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.chat_item_center_custom;
    }


    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.tv_content);
    }

    @Override
    public void bindContentView() {
        bodyTextView.setOnClickListener(v -> onItemClick());
        setReSendGone();
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.RoomInfo tips = JSON.parseObject(chatMsg.content, ChatMsg.RoomInfo.class);
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(tips.name);
        stringBuffer.append(" ");
        stringBuffer.append(tips.content);
        spanString.append(stringBuffer);
        int start = stringBuffer.indexOf(tips.name);
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                if (NimUIKitImpl.getSessionListener() != null)
                    NimUIKitImpl.getSessionListener().onVoiceSeatClicked(context, message);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        }, start, start + tips.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#4287FF")), start, start + tips.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        bodyTextView.setText(spanString);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }


    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
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
