package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;

/**
 *家族公告
 */
public class MsgViewHolderFamilyAnnouncement extends MsgViewHolderBase {

    private TextView mTvContent;

    public MsgViewHolderFamilyAnnouncement(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_family_announcement;
    }

    @Override
    public void inflateContentView() {
        mTvContent = findViewById(R.id.tv_content);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.ApplyMessage message = JSON.parseObject(chatMsg.content, ChatMsg.ApplyMessage.class);
        if(message!=null){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(message.des);
            builder.append(message.message);
            int start = builder.toString().indexOf(message.des);
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), start, start + message.des.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvContent.setText(builder);
        }
    }


    @Override
    protected void bindHolder(BaseViewHolder holder) {
        super.bindHolder(holder);
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
