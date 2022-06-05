package com.netease.nim.uikit.common.adapter;


import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ChatMsg;

import java.util.List;

public class FamilyBeforeTips3Adapter extends BaseQuickAdapter<ChatMsg.RainStartForget3.RainStartForgetInner, BaseViewHolder> {
    String color;

    public FamilyBeforeTips3Adapter(@Nullable List<ChatMsg.RainStartForget3.RainStartForgetInner> data, String color) {
        super(R.layout.item_before_tips3, data);
        this.color = color;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChatMsg.RainStartForget3.RainStartForgetInner item) {

        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor(color));
        SpannableStringBuilder builder = new SpannableStringBuilder(item.tip);
        builder.setSpan(redSpan, item.tip.indexOf(item.coin_str), item.tip.indexOf(item.coin_str) + item.coin_str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_name, builder);

    }
}
