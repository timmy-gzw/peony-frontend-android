package com.tftechsz.common.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 数量显示
 */
public class GiftNumberAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public GiftNumberAdapter(@Nullable List<Integer> data) {
        super(R.layout.item_gift_number, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Integer item) {
        TextView tvNumber = helper.getView(R.id.tv_number);
        tvNumber.setText(item + "");
    }
}
