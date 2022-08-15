package com.tftechsz.mine.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GiftDto;

/**
 * 礼物
 */
public class GiftAdapter extends BaseQuickAdapter<GiftDto, BaseViewHolder> {

    private final ColorMatrixColorFilter colorFilter;

    public GiftAdapter() {
        super(R.layout.mine_item_gift);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        colorFilter = new ColorMatrixColorFilter(colorMatrix);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDto item) {
        ImageView imageView = helper.getView(R.id.iv_gift);
        GlideUtils.loadRouteImage(getContext(), imageView, item.image);

        if (item.number <= 0) {
            imageView.setColorFilter(colorFilter);
            imageView.setAlpha(0.7f);
        } else {
            imageView.setColorFilter(null);
            imageView.setAlpha(1f);
        }
    }
}
