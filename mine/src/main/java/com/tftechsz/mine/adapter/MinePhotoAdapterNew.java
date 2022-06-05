package com.tftechsz.mine.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.MinePhotoDto;

import androidx.annotation.NonNull;

/**
 * 相册
 */
public class MinePhotoAdapterNew extends BaseQuickAdapter<MinePhotoDto, BaseViewHolder> {

    public MinePhotoAdapterNew() {
        super(R.layout.item_trend_new);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Interfaces.TYPE_CAMERA;
        }
        return Interfaces.TYPE_PICTURE;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MinePhotoDto item) {
        ImageView ivClose = helper.getView(R.id.img);
        TextView tips = helper.getView(R.id.tips);
        if (item.getUrl().equals("ADD")) {
            ivClose.setImageResource(R.mipmap.ic_add);
            tips.setVisibility(View.GONE);
        } else {
            tips.setVisibility(item.isShow() ? View.VISIBLE : View.GONE);
            GlideUtils.loadRoundImage(getContext(), helper.getView(R.id.img), item.getUrl());
        }
    }
}
