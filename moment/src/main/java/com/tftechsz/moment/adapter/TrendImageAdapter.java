package com.tftechsz.moment.adapter;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.entity.UserViewInfo;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.moment.R;

import java.util.List;

/**
 * 动态图片
 */
public class TrendImageAdapter extends BaseQuickAdapter<UserViewInfo, BaseViewHolder> {
    public TrendImageAdapter(@Nullable List<UserViewInfo> data) {
        super(R.layout.item_image, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserViewInfo item) {
        ImageView iv1 = helper.getView(R.id.iv1);
        ImageView iv1_5 = helper.getView(R.id.iv1_5);
        iv1.setVisibility(View.GONE);
        iv1_5.setVisibility(View.GONE);

        if (getData().size() == 1) {
            //imageView.serHeight((float) 1.5);
            iv1_5.setVisibility(View.VISIBLE);
            GlideUtils.loadRoundImageRadius(getContext(), iv1_5, item.getUrl());
        } else {
            iv1.setVisibility(View.VISIBLE);
            int radius = Utils.getDimensPx(getContext(),R.dimen.trend_image_radius);
            GlideUtils.loadRoundImage(getContext(), iv1, item.getUrl(),radius);
        }

    }

}
