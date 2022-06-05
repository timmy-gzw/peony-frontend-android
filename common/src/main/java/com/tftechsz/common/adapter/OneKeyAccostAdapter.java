package com.tftechsz.common.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class OneKeyAccostAdapter extends BaseQuickAdapter<ChatMsg.AccostPopup, BaseViewHolder> {
    public OneKeyAccostAdapter() {
        super(R.layout.item_one_key_accost);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, ChatMsg.AccostPopup item) {
        TextView name = helper.getView(R.id.name);
        ImageView icon = helper.getView(R.id.icon);
        ImageView iv_check = helper.getView(R.id.iv_check);
        name.setText(item.nickname);
        GlideUtils.loadRoundImageRadius(getContext(), icon, item.icon);

        if (item.is_selected) {
            iv_check.setImageResource(R.mipmap.ic_accost_check_selector);
        } else {
            iv_check.setImageResource(R.mipmap.ic_accost_check_normal);
        }
    }
}
