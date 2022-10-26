package com.tftechsz.mine.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.LabelInfoDto;

import org.jetbrains.annotations.NotNull;

public class LabelContentAdapter extends BaseQuickAdapter<LabelInfoDto, BaseViewHolder> {


    public LabelContentAdapter() {
        super(R.layout.item_label_content);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LabelInfoDto item) {
        TextView tvLabel = baseViewHolder.getView(R.id.tv_content);
        tvLabel.setText(item.name);
        if (item.is_select == 1) {
            tvLabel.setBackgroundResource(R.drawable.bg_label_select);
            tvLabel.setTextColor(Utils.getColor(R.color.colorPrimary));
        } else {
            tvLabel.setBackgroundResource(R.drawable.bg_label_normal);
            tvLabel.setTextColor(Utils.getColor(R.color.color_light_font));
        }
    }

}
