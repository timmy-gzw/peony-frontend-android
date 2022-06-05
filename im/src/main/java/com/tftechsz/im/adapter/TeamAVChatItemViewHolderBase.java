package com.tftechsz.im.adapter;


import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.ui.recyclerview.holder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.recyclerview.holder.RecyclerViewHolder;

/**
 * Created by huangjun on 2017/5/9.
 */

abstract class TeamAVChatItemViewHolderBase<T> extends RecyclerViewHolder<BaseMultiItemFetchLoadAdapter, BaseViewHolder, T> {

    TeamAVChatItemViewHolderBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(final BaseViewHolder holder, T data, int position, boolean isScrolling) {
        inflate(holder);
        refresh(data);
    }

    protected abstract void inflate(final BaseViewHolder holder);

    protected abstract void refresh(final T data);
}
