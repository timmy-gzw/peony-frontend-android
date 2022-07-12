package com.tftechsz.mine.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tftechsz.common.utils.GlideUtils
import com.tftechsz.mine.R
import com.tftechsz.mine.entity.dto.GiftDto

/**
 * 礼物墙
 */
class GiftWallAdapter : BaseQuickAdapter<GiftDto, BaseViewHolder>(R.layout.item_gift_wall) {
    override fun convert(holder: BaseViewHolder, item: GiftDto) {
        holder.setText(R.id.tv_gift_name, item.name)
                .setText(R.id.tv_gift_count, "X${item.number}")
        GlideUtils.loadImage(context, holder.getView(R.id.iv_gift), item.image)
        if (item.number <= 0) {
            holder.itemView.alpha = 0.3f
        } else {
            holder.itemView.alpha = 1f
        }
    }
}