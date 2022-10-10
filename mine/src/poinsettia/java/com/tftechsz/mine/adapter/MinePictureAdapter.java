package com.tftechsz.mine.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.TrendDto;

import java.util.List;

/**
 * 动态
 */
public class MinePictureAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MinePictureAdapter(@Nullable List<String> data) {
        super(R.layout.item_mine_picture, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        if (TextUtils.isEmpty(item)) {
            helper.setImageResource(R.id.iv_pic, R.mipmap.ic_add);
        } else {
            GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_pic), item);
        }
    }
}
