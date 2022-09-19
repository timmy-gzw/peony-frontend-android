package com.tftechsz.mine.adapter;

import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;

import org.jetbrains.annotations.NotNull;

public class LabelDisplayAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public static int TYPE_ADD = 0x110;
    public static int TYPE_TAG = 0x111;

    public LabelDisplayAdapter() {
        super(R.layout.item_label_content_2);
    }

    @Override
    protected int getDefItemViewType(int position) {
        if ("ADD_TAG".equals(getData().get(position))) {
            return TYPE_ADD;
        } else {
            return TYPE_TAG;
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, String label) {
        if (holder.getItemViewType() == TYPE_ADD) {
            TextView tvLabel = holder.getView(R.id.tv_content);
            tvLabel.setText("填选标签");
            tvLabel.setBackgroundResource(R.drawable.shape_bg_f9f9f9_radius);
            tvLabel.setTextColor(Utils.getColor(R.color.color_light_font));
            tvLabel.setCompoundDrawablePadding(ConvertUtils.dp2px(6));
            tvLabel.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_mine_add, 0, 0, 0);
        } else {
            TextView tvLabel = holder.getView(R.id.tv_content);
            tvLabel.setText(label);
            tvLabel.setBackgroundResource(R.drawable.bg_label_select);
            tvLabel.setTextColor(Utils.getColor(R.color.colorPrimary));
            tvLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
}
