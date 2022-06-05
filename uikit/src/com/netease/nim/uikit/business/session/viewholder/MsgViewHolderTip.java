package com.netease.nim.uikit.business.session.viewholder;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderTip extends MsgViewHolderBase {
    protected TextView bodyTextView;

    public MsgViewHolderTip(BaseMultiItemFetchLoadAdapter adapter) {
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
        ChatMsg.Tips tips = JSON.parseObject(chatMsg.content, ChatMsg.Tips.class);
        SpannableStringBuilder span = ChatMsgUtil.getTipContent(/*"        " + */tips.des,"", new ChatMsgUtil.OnSelectListener() {
            @Override
            public void onClick(String content) {
                String webview = "webview://";
                String peony = "peony://";
                if (content.contains(webview)) {  //打开webview
                    NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 2, content.substring(webview.length() + 1, content.length() - 1));
                } else if (content.contains(peony)) {   //打开原生页面
                    NimUIKitImpl.getSessionTipListener().onTipMessageClicked(context, 1, content.substring(peony.length() + 1, content.length() - 1));
                }
            }
        });

        bodyTextView.setText(span);

        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
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
