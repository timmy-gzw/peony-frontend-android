package com.tftechsz.im.adapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.list.MsgAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.im.model.FateInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FateAdapter extends BaseMultiItemFetchLoadAdapter<FateInfo, BaseViewHolder> {

    private Map<Class<? extends FateViewHolderBase>, Integer> holder2ViewType;

    private FateAdapter.ViewHolderEventListener eventListener;
    private Map<String, Float> progresses; // 有文件传输，需要显示进度条的消息ID map
    private String messageId;
    private Container container;

    private LinearLayoutManager layoutManager;
    private int viewHeight;
    private int offsetY = 0;
    private final float MIN_SCALE = 0.55f;
    private int firstView = 0;
    private int lasttView = -1;
    public FateAdapter(RecyclerView recyclerView, LinearLayoutManager linearLayoutManager, List<FateInfo> data, Container container) {
        super(recyclerView, data);

        this.layoutManager = linearLayoutManager;
        timedItems = new HashSet<>();
        progresses = new HashMap<>();
        // view type, view holder
        holder2ViewType = new HashMap<>();



        List<Class<? extends FateViewHolderBase>> holders = FateViewHolderFactory.getAllViewHolders();
        int viewType = 0;
        for (Class<? extends FateViewHolderBase> holder : holders) {
            viewType ++;
            addItemType(viewType, com.netease.nim.uikit.R.layout.nim_message_item, holder);
            holder2ViewType.put(holder, viewType);
        }

        this.container = container;
    }


    @Override
    protected String getItemKey(FateInfo item) {
        return null;
    }


    public interface OnScrollListener {
        void getScrollPosition(float sx, RecyclerView recyclerView, int rvHeight, View view, int last);
    }

    public FateAdapter.OnScrollListener scrollListener;

    public void addOnScrollListener(FateAdapter.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }


    @Override
    protected int getViewType(FateInfo fateInfo) {
        return holder2ViewType.get(FateViewHolderFactory.getViewHolderByType(fateInfo));
    }


    public void setEventListener(FateAdapter.ViewHolderEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }


    public int getViewHeight() {
        return viewHeight;
    }


    public FateAdapter.ViewHolderEventListener getEventListener() {
        return eventListener;
    }



    /**
     * *********************** 时间显示处理 ***********************
     */

    private Set<Long> timedItems; // 需要显示消息时间的消息ID
    private FateInfo lastShowTimeItem; // 用于消息时间显示,判断和上条消息间的时间间隔

    public boolean needShowTime(FateInfo info) {
        return timedItems.contains(info.getCreated_at());
    }

    /**
     * 列表加入新消息时，更新时间显示
     */
    public void updateShowTimeItem(List<FateInfo> items, boolean fromStart, boolean update) {
        FateInfo anchor = fromStart ? null : lastShowTimeItem;
        for (FateInfo message : items) {
            if (setShowTimeFlag(message, anchor)) {
                anchor = message;
            }
        }
        if (update) {
            lastShowTimeItem = anchor;
        }
    }

    /**
     * 是否显示时间item
     */
    private boolean setShowTimeFlag(FateInfo message, FateInfo anchor) {
        boolean update = false;

            if (anchor == null) {
                setShowTime(message, true);
                update = true;
            } else {
                long time = anchor.getCreated_at() * 1000;
                long now = message.getCreated_at() * 1000;

                if (now - time == 0) {
                    // 消息撤回时使用
                    setShowTime(message, true);
                    lastShowTimeItem = message;
                    update = true;
                } else if (now - time < (NimUIKitImpl.getOptions().displayMsgTimeWithInterval)) {
                    setShowTime(message, false);
                } else {
                    setShowTime(message, true);
                    update = true;
                }
            }


        return update;
    }

    private void setShowTime(FateInfo fateInfo, boolean show) {
        if (show) {
            timedItems.add(fateInfo.getCreated_at());
        } else {
            timedItems.remove(fateInfo.getCreated_at());
        }
    }


    public interface ViewHolderEventListener {
        // 长按事件响应处理
        boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item);

        // 发送失败或者多媒体文件下载失败指示按钮点击响应处理
        void onFailedBtnClick(IMMessage resendMessage);

        // viewholder footer按钮点击，如机器人继续会话
        void onFooterClick(IMMessage message);

        /**
         * 消息对应的复选框的状况变化时回调
         * 状态: true: 选中; false: 未被选中; null: 选则无效（复选框不可见，且状态重置为未被选中）
         *
         * @param index    消息在列表中的位置
         * @param newState 变化后的状态
         */
        void onCheckStateChanged(int index, Boolean newState);
    }

    /**
     * 为了在实现ViewHolderEventListener时只需要复写需要的部分
     */
    public static class BaseViewHolderEventListener implements MsgAdapter.ViewHolderEventListener {

        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item) {
            return false;
        }

        @Override
        public void onFailedBtnClick(IMMessage resendMessage) {
        }

        @Override
        public void onFooterClick(IMMessage message) {
        }

        @Override
        public void onCheckStateChanged(int index, Boolean newState) {

        }
    }

    public void setUuid(String messageId) {
        this.messageId = messageId;
    }

    public String getUuid() {
        return messageId;
    }

    public Container getContainer() {
        return container;
    }
}
