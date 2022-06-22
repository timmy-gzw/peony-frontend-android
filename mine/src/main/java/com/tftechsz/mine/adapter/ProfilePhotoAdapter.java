package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

/**
 * 个人主页 - 我的相册
 */
public class ProfilePhotoAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public int selectedIndex = 0;

    public ProfilePhotoAdapter() {
        super(R.layout.item_profile_pic);
    }

    public void setIndex(int index) {
        int i = selectedIndex;
        if (index >= 0) {
            selectedIndex = index;
            notifyItemChanged(i);
            notifyItemChanged(index);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        if (selectedIndex == holder.getLayoutPosition()) {
            holder.itemView.setBackgroundResource(R.drawable.sp_s_white);
        } else {
            holder.itemView.setBackgroundResource(0);
        }
        GlideUtils.loadImage(getContext(), holder.getView(R.id.iv_pic), s);
    }
}
