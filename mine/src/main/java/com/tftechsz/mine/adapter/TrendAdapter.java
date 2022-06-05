package com.tftechsz.mine.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.TrendDto;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 动态
 */
public class TrendAdapter extends BaseQuickAdapter<TrendDto, BaseViewHolder> {
    private final String mUserId;
    public TrendAdapter(@Nullable List<TrendDto> data,String userId) {
        super(R.layout.item_trend, data);
        this.mUserId = userId;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TrendDto item) {
        if (helper.getLayoutPosition() == 0) {
            if(TextUtils.isEmpty(mUserId)){
                helper.setImageResource(R.id.iv_trend, R.mipmap.ic_add);
            }else{
                GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_trend), item.image);
            }
        } else {
            GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv_trend), item.image);
        }
    }
}
