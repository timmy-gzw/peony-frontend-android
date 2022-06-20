package com.tftechsz.common.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;

public class TopicAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    int type; //0 横向 1 纵向


    public TopicAdapter(int type) {
        super(type == 0? R.layout.topic_item:R.layout.topic_item2);
        this.type = type;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, String s) {
        baseViewHolder.setText(R.id.tv_text, s);
    }
}
