package com.tftechsz.im.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.im.R;
import com.tftechsz.im.api.MultipleItem;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class ViewPagerScrollAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    public ViewPagerScrollAdapter() {
        addItemType(Interfaces.ADAPTER_TYPE_1, R.layout.item_message_top_vp1);
        addItemType(Interfaces.ADAPTER_TYPE_2, R.layout.item_message_top_vp2);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MultipleItem multipleItem) {
        ChatMsg.JoinFamily dto = multipleItem.getPerson();
        switch (helper.getItemViewType()) {
            case Interfaces.ADAPTER_TYPE_1:
                ImageView img = helper.getView(R.id.iv_avatar_rank);
                GlideUtils.loadRoundImage(getContext(), img, dto.icon,100);
                break;

            case Interfaces.ADAPTER_TYPE_2:
                ImageView left_icon = helper.getView(R.id.left_icon);
                ImageView right_icon = helper.getView(R.id.right_icon);
                GlideUtils.loadRoundImage(getContext(), left_icon, dto.left_icon,100);
                GlideUtils.loadRoundImage(getContext(), right_icon, dto.right_icon,100);
                break;
        }
    }
}
