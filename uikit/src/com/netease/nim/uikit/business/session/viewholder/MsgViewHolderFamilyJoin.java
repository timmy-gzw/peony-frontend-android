package com.netease.nim.uikit.business.session.viewholder;

import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.imageview.AvatarImageView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * 进入家族
 */
public class MsgViewHolderFamilyJoin extends MsgViewHolderBase {

    private TextView mTvContent, mTvJoin;
    private AvatarImageView ivHead;

    public MsgViewHolderFamilyJoin(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_family_join;
    }

    @Override
    public void inflateContentView() {
        mTvContent = findViewById(R.id.tv_content);
        ivHead = findViewById(R.id.iv_head);
        mTvJoin = findViewById(R.id.tv_join);
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.JoinFamily joinFamily = JSON.parseObject(chatMsg.content, ChatMsg.JoinFamily.class);
        if (joinFamily != null) {
            mTvContent.setText(joinFamily.name);
            ivHead.loadAvatar(joinFamily.icon);
            mTvJoin.setOnClickListener(v -> {
                if (NimUIKitImpl.getSessionListener() != null)
                    NimUIKitImpl.getSessionListener().onFamilyJoin(chatMsg.from, message);
            });
        }
        mTvJoin.setVisibility(View.VISIBLE);
        if (message.getLocalExtension() != null) {
            Object obj = message.getLocalExtension().get("family_join");
            if (obj != null) {
                mTvJoin.setVisibility(View.INVISIBLE);
            } else {
                mTvJoin.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
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
