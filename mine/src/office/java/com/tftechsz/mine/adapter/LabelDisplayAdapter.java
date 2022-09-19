package com.tftechsz.mine.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;

import org.jetbrains.annotations.NotNull;

public class LabelDisplayAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public LabelDisplayAdapter() {
        super(R.layout.item_label_content_2);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, String label) {
        TextView tvLabel = baseViewHolder.getView(R.id.tv_content);
        tvLabel.setText(label);
        tvLabel.setBackgroundResource(R.drawable.bg_label_select);
        tvLabel.setTextColor(Utils.getColor(R.color.colorPrimary));
    }
}
