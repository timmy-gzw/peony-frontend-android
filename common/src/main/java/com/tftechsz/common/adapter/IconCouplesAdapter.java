package com.tftechsz.common.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 资料卡头像图片
 */
public class IconCouplesAdapter extends BaseQuickAdapter<FamilyIdDto.CoupleGiftListInfo, BaseViewHolder> {

    public IconCouplesAdapter() {
        super(R.layout.adapter_icon_couples);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FamilyIdDto.CoupleGiftListInfo item) {

        ImageView iv = helper.getView(R.id.img_icon);
        GlideUtils.loadRouteImage(getContext(), iv, item.gift_icon);

    }

}
