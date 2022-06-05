package com.tftechsz.party.adapter;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;

import org.jetbrains.annotations.NotNull;

public class PartyTeamAdapter extends BaseQuickAdapter<VoiceRoomSeat, BaseViewHolder> {
    public PartyTeamAdapter() {
        super(R.layout.item_party_team);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VoiceRoomSeat item) {
        if (!TextUtils.isEmpty(item.getIcon())) {
            if (item.getIcon().startsWith("http"))
                GlideUtils.loadCircleImage(getContext(), baseViewHolder.getView(R.id.iv_member_avatar), item.getIcon(), R.drawable.party_ic_create_pk_add);
            else {
                GlideUtils.loadCircleImage(getContext(), baseViewHolder.getView(R.id.iv_member_avatar), CommonUtil.getHttpUrlHead() + item.getIcon(), R.drawable.party_ic_create_pk_add);
            }
        } else {
            GlideUtils.loadCircleImage(getContext(), baseViewHolder.getView(R.id.iv_member_avatar), "", R.drawable.party_ic_create_pk_add);
        }
        baseViewHolder.setText(R.id.tv_name, item.getNickname());
        baseViewHolder.setVisible(R.id.iv_del, item.getUser_id() != 0);
    }
}
