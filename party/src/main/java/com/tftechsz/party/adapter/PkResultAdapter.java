package com.tftechsz.party.adapter;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.party.R;

import org.jetbrains.annotations.NotNull;

public class PkResultAdapter extends BaseQuickAdapter<VoiceRoomSeat, BaseViewHolder> {
    public PkResultAdapter() {
        super(R.layout.item_pk_result);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VoiceRoomSeat item) {
        if (item.getUser_id() == 0) return;
        CircleImageView ivPic = baseViewHolder.findView(R.id.iv_user);
        if (!TextUtils.isEmpty(item.getIcon())) {
            if (item.getIcon().startsWith("http"))
                GlideUtils.loadImage(getContext(), ivPic, item.getIcon(), R.drawable.ic_default_avatar);
            else
                GlideUtils.loadImage(getContext(), ivPic, CommonUtil.getHttpUrlHead() + item.getIcon(), R.drawable.party_ic_seat);
        } else {
            baseViewHolder.setImageResource(R.id.iv_user, R.drawable.party_ic_seat);
        }
        baseViewHolder.setVisible(R.id.iv_mvp, item.getIs_mvp() == 1);
        baseViewHolder.setText(R.id.tv_chat_love, TextUtils.isEmpty(item.getCost()) ? "0" : item.getCost());
        baseViewHolder.setText(R.id.tv_position, item.getNickname());
    }
}
