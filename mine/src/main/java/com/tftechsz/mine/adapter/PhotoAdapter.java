package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 相册
 */
public class PhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public PhotoAdapter(@Nullable List<String> data) {
        super(R.layout.item_trend, data);
    }


    @Override
    public int getItemCount() {
        return  Math.min(getData().size(), 4);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        if (helper.getLayoutPosition() == 0) {
            helper.setImageResource(R.id.iv_trend, R.mipmap.ic_add);
        } else {
            GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_trend), item);
        }

    }
}
