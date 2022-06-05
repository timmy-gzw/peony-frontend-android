package com.tftechsz.party.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.party.R;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.party.adapter
 * 描 述 : TODO
 */
public class PartyScrollerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public PartyScrollerAdapter() {
        super(R.layout.item_scroller_party_view);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.msg, s);

    }
}
