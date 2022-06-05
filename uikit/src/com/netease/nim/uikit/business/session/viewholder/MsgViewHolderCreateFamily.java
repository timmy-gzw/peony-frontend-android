package com.netease.nim.uikit.business.session.viewholder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.impl.NimUIKitImpl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 创建
 */
public class MsgViewHolderCreateFamily extends MsgViewHolderBase {

    private RecyclerView rvFamily;

    public MsgViewHolderCreateFamily(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_create_family;
    }

    @Override
    public void inflateContentView() {
        rvFamily = findViewById(R.id.rv_family);
        rvFamily.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void bindContentView() {
        layoutByDirection();
    }

    private void layoutByDirection() {
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        try {
            List<ChatMsg.Alert> family = JSONObject.parseArray(chatMsg.content, ChatMsg.Alert.class);
            if (family != null) {
                FamilyAdapter adapter = new FamilyAdapter(family);
                rvFamily.setAdapter(adapter);
                adapter.addChildClickViewIds(R.id.tv_event);
                adapter.setOnItemChildClickListener((adapter1, view, position) -> {
                    if (NimUIKitImpl.getSessionListener() != null)
                        NimUIKitImpl.getSessionListener().onCreateFamilyEvent(adapter.getData().get(position).button.event);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick() {
    }

    @Override
    protected void bindHolder(BaseViewHolder holder) {
        super.bindHolder(holder);
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

    public static class FamilyAdapter extends BaseQuickAdapter<ChatMsg.Alert, com.chad.library.adapter.base.viewholder.BaseViewHolder> {

        public FamilyAdapter(List<ChatMsg.Alert> data) {
            super(R.layout.nim_message_item_family_craete, data);
        }


        @Override
        protected void convert(@NotNull com.chad.library.adapter.base.viewholder.BaseViewHolder helper, ChatMsg.Alert alert) {
            helper.setText(R.id.tv_content, alert.title);
            ChatMsg.AlertEvent alertEvent = alert.button;
            if (alertEvent != null) {
                helper.setText(R.id.tv_event, alertEvent.msg);
            }
        }
    }

}
