package com.tftechsz.mine.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.AccostSettingListBean;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class AccostPicAdapter extends BaseQuickAdapter<AccostSettingListBean, BaseViewHolder> {
    public AccostPicAdapter() {
        super(R.layout.item_accost_pic);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AccostSettingListBean item) {
        GlideUtils.loadRoundImage(getContext(), helper.getView(R.id.icon), item.url);
        helper.setVisible(R.id.del, item.is_show == 1);
        helper.setVisible(R.id.under_review, item.is_show == 0);
    }
}
