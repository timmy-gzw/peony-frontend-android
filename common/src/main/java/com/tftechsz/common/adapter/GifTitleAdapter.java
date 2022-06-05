package com.tftechsz.common.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.BR;
import com.tftechsz.common.R;
import com.tftechsz.common.databinding.ItemGifTitleBinding;
import com.tftechsz.common.entity.GifTitleDto;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class GifTitleAdapter extends BaseQuickAdapter<GifTitleDto, DataBindBaseViewHolder> {

    public GifTitleAdapter() {
        super(R.layout.item_gif_title);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, GifTitleDto bean) {
        ItemGifTitleBinding bind = (ItemGifTitleBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();

        bind.endLine.setVisibility(helper.getLayoutPosition() == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    }

    public int getPosition() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).isIs_active()) {
                return i;
            }
        }
        return 0;
    }
}
