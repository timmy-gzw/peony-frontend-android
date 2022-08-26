package com.tftechsz.im.adapter;

import android.graphics.Color;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.im.BR;
import com.tftechsz.im.R;
import com.tftechsz.im.databinding.ItemCallLogBinding;
import com.tftechsz.im.model.dto.CallLogDto;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.im.adapter
 * 描 述 : TODO
 */
public class CallLogAdapter extends BaseQuickAdapter<CallLogDto, DataBindBaseViewHolder> {

    public CallLogAdapter() {
        super(R.layout.item_call_log);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, CallLogDto bean) {
        ItemCallLogBinding binding = (ItemCallLogBinding) helper.getBind();
        binding.setVariable(BR.item, bean);
        binding.executePendingBindings();

        CommonUtil.setUserName(binding.name, bean.user_nickname, bean.isVip());
        binding.setBotLineVisible(getItemCount() - 1 == helper.getLayoutPosition());
//        binding.setEndIconVisible(bean.is_tag);
        GlideUtils.loadRoundImage(getContext(), binding.icon, bean.user_icon, Utils.getDimensPx(getContext(),R.dimen.px_avatar_image_view_radius));
        if (!TextUtils.isEmpty(bean.color) && bean.color.startsWith("#") && (bean.color.length() == 7 || bean.color.length() == 9)) {
            binding.desc.setTextColor(Color.parseColor(bean.color));
        }

        //20220625 hide
//        if (bean.is_tag) {
//            binding.ivCall.setImageResource(bean.isVideo() ? R.drawable.peony_xx_sp_icon01 : R.drawable.peony_xx_yy_icon01);
//        } else {
//            binding.ivCall.setImageResource(bean.isVideo() ? R.drawable.peony_xx_sp_icon02 : R.drawable.peony_xx_yy_icon02);
//        }
    }
}
