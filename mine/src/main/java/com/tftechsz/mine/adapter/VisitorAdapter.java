package com.tftechsz.mine.adapter;

import android.graphics.BlurMaskFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.MaskFilterSpan;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.BR;
import com.tftechsz.mine.R;
import com.tftechsz.mine.databinding.ItemVisitorBinding;
import com.tftechsz.mine.entity.VisitorBean;

import org.jetbrains.annotations.NotNull;

/**
 * 包 名 : com.tftechsz.mine.adapter
 * 描 述 : TODO
 */
public class VisitorAdapter extends BaseQuickAdapter<VisitorBean, DataBindBaseViewHolder> {

    private boolean isVip;

    public VisitorAdapter() {
        super(R.layout.item_visitor);
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, VisitorBean bean) {
        ItemVisitorBinding mBinding = (ItemVisitorBinding) helper.getBind();
        mBinding.setVariable(BR.item, bean);
        mBinding.executePendingBindings();
        CommonUtil.setUserName(mBinding.tvVisName, bean.nickname, false, bean.isVip());

        mBinding.visSign.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mBinding.tvVisName.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (!isVip) {
            SpannableString ss = new SpannableString(bean.nickname);
            ss.setSpan(new MaskFilterSpan(new BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)),
                    0, ss.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mBinding.tvVisName.setText(ss);

            SpannableString ss1 = new SpannableString(bean.desc);
            ss1.setSpan(new MaskFilterSpan(new BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)),
                    0, ss1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mBinding.visSign.setText(ss1);

            GlideUtils.loadImageGaussian(getContext(), mBinding.ivAvatar, bean.icon);
        } else {
            mBinding.tvVisName.setText(bean.nickname);
            mBinding.visSign.setText(bean.desc);
            GlideUtils.loadRoundImageRadius(getContext(), mBinding.ivAvatar, bean.icon);
        }
    }
}
