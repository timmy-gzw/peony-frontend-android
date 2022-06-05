package com.netease.nim.uikit.business.session.viewholder;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.DensityUtils;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.CenteredImageSpan;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderFamilyCoupleTip extends MsgViewHolderBase {
    protected TextView bodyTextView;
    private TextView tvFrom, tvTo;
    private TextView tvFromAge, tvToAge;

    public MsgViewHolderFamilyCoupleTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.chat_item_couple_tip;
    }


    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.tv_content);
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        tvFromAge = findViewById(R.id.tv_from_age);
        tvToAge = findViewById(R.id.tv_to_age);
    }

    @Override
    public void bindContentView() {
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        setReSendGone();
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.CoupleLetter letter = JSON.parseObject(chatMsg.content, ChatMsg.CoupleLetter.class);
        if (letter != null) {
            tvFrom.setText(letter.from_nickname);
            tvFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NimUIKitImpl.getSessionListener() != null)
                        NimUIKitImpl.getSessionListener().onAvatarClicked(letter.from_user_id);
                }
            });
            setSexAndAge(context, letter.from_sex, letter.from_age, tvFromAge);
            setSexAndAge(context, letter.to_sex, letter.to_age, tvToAge);
            tvTo.setText(letter.to_nickname);
            tvTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NimUIKitImpl.getSessionListener() != null)
                        NimUIKitImpl.getSessionListener().onAvatarClicked(letter.to_user_id);
                }
            });
            bodyTextView.setText(letter.desc);
        }


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
