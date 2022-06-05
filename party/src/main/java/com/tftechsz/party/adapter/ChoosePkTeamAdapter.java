package com.tftechsz.party.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;

import org.jetbrains.annotations.NotNull;

public class ChoosePkTeamAdapter extends BaseQuickAdapter<VoiceRoomSeat, BaseViewHolder> {
    private int mTeamType;  // 1:红队 2:蓝队

    public ChoosePkTeamAdapter() {
        super(R.layout.item_choose_pk_team);
    }

    public void setTeamType(int teamType) {
        this.mTeamType = teamType;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VoiceRoomSeat item) {
        ImageView IvMemberAvatar = baseViewHolder.getView(R.id.iv_member_avatar);
        if (!TextUtils.isEmpty(item.getIcon())) {
            if (item.getIcon().startsWith("http"))
                GlideUtils.loadCircleImage(getContext(), IvMemberAvatar, item.getIcon(), R.drawable.party_ic_member_empty);
            else {
                GlideUtils.loadCircleImage(getContext(), IvMemberAvatar, CommonUtil.getHttpUrlHead() + item.getIcon(), R.drawable.party_ic_member_empty);
            }
        }else {
            GlideUtils.loadCircleImage(getContext(), IvMemberAvatar, item.getIcon(), R.drawable.party_ic_member_empty);
        }
        ImageView ivChoose = baseViewHolder.getView(R.id.iv_choose);
        ivChoose.setImageResource(mTeamType == 1 ? R.drawable.party_ic_red_choose : R.drawable.party_ic_blue_choose);
        //红队
        if (mTeamType == 1) {
            baseViewHolder.setVisible(R.id.iv_choose, item.isSelect());
            if (item.isBlueSelect() && !TextUtils.isEmpty(item.getIcon())) {
                IvMemberAvatar.setAlpha(0.5f);
            } else {
                IvMemberAvatar.setAlpha(1.0f);
            }
        } else {
            baseViewHolder.setVisible(R.id.iv_choose, item.isBlueSelect());
            if (item.isSelect() && !TextUtils.isEmpty(item.getIcon())) {
                IvMemberAvatar.setAlpha(0.5f);
            } else {
                IvMemberAvatar.setAlpha(1.0f);
            }
        }
    }

}
