package com.netease.nim.uikit.business.session.viewholder;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.adapter.FamilyBeforeTips3Adapter;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * 红包雨tips前三奖励
 */
public class MsgViewHolderFamilyRedPackBefore3Tip extends MsgViewHolderBase {
    protected TextView title;
    protected RecyclerView recyclerView;

    public MsgViewHolderFamilyRedPackBefore3Tip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_item_family_pack_before_tip;
    }


    @Override
    public void inflateContentView() {
        title = findViewById(R.id.tv_titlet);
        recyclerView = findViewById(R.id.rv_message);
    }

    @Override
    public void bindContentView() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        ChatMsg.RainStartForget3 rain = JSON.parseObject(chatMsg.content, ChatMsg.RainStartForget3.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(title.getContext()));
        FamilyBeforeTips3Adapter mTaskAdapter = new FamilyBeforeTips3Adapter(rain.top3, rain.color);
        recyclerView.setAdapter(mTaskAdapter);
        //首先给赋值颜色
        title.setText(rain.title);

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
