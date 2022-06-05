package com.tftechsz.party.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.dto.PkTimeDto;

import org.jetbrains.annotations.NotNull;

public class PartyTimeChooseAdapter extends BaseQuickAdapter<PkTimeDto.PkTimeInfo, BaseViewHolder> {
    public PartyTimeChooseAdapter() {
        super(R.layout.item_party_time_choose);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PkTimeDto.PkTimeInfo pkTimeInfo) {

        baseViewHolder.setText(R.id.tv_time, pkTimeInfo.getMinute() + pkTimeInfo.getText());
    }
}
