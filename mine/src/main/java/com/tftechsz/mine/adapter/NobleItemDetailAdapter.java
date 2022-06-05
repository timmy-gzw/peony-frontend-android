package com.tftechsz.mine.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class NobleItemDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public NobleItemDetailAdapter() {
        super(R.layout.item_noble_item_detail);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView img = helper.getView(R.id.img);
        GlideUtils.loadRouteImage(getContext(), img, item, R.drawable.bg_trans);
    }
}
