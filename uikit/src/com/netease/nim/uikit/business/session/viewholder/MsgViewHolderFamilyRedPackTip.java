package com.netease.nim.uikit.business.session.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * 红包雨tips
 */
public class MsgViewHolderFamilyRedPackTip extends MsgViewHolderBase {
    protected TextView bodyTextView;
    private ImageView ivLevel;

    public MsgViewHolderFamilyRedPackTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_item_family_red_pack_tip;
    }


    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.tv_content);
        ivLevel = findViewById(R.id.iv_level);
    }

    @Override
    public void bindContentView() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.NobilityLevelUp levelUp = JSON.parseObject(chatMsg.content, ChatMsg.NobilityLevelUp.class);
        if (levelUp != null) {
            bodyTextView.setText(levelUp.msg);
        }
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
