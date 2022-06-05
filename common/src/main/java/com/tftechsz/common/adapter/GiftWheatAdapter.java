package com.tftechsz.common.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.BR;
import com.tftechsz.common.R;
import com.tftechsz.common.databinding.ItemGiftWheatBinding;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class GiftWheatAdapter extends BaseQuickAdapter<VoiceRoomSeat, DataBindBaseViewHolder> {
    public GiftWheatAdapter() {
        super(R.layout.item_gift_wheat);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, VoiceRoomSeat item) {
        helper.setIsRecyclable(false);
        ItemGiftWheatBinding mBinding = (ItemGiftWheatBinding) helper.getBind();
        mBinding.setVariable(BR.item, item);
        if (!TextUtils.isEmpty(item.getIcon())) {
            if (item.getIcon().startsWith("http"))
                GlideUtils.loadCircleImage(getContext(), mBinding.icon, item.getIcon());
            else {
                GlideUtils.loadCircleImage(getContext(), mBinding.icon, CommonUtil.getHttpUrlHead() + item.getIcon());
            }
        }
        mBinding.root.setPadding(ConvertUtils.dp2px(helper.getLayoutPosition() == 0 ? 10 : 3), 0, ConvertUtils.dp2px(helper.getLayoutPosition() == getItemCount() - 1 ? 10 : 3), 0);
    }
}
