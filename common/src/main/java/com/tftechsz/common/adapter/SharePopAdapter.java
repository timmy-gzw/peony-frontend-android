package com.tftechsz.common.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.ShareBean;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class SharePopAdapter extends BaseQuickAdapter<ShareBean, BaseViewHolder> {
    public SharePopAdapter() {
        super(R.layout.item_share_pop);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, ShareBean bean) {
        ImageView ivShare = helper.getView(R.id.iv_share);
        TextView tvShare = helper.getView(R.id.tv_share);
        GlideUtils.loadImage(getContext(), ivShare, bean.icon);
        tvShare.setText(bean.title);
    }
}
