package com.tftechsz.home.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.home.BR;
import com.tftechsz.home.R;
import com.tftechsz.home.databinding.ItemTopSignBinding;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 包 名 : com.tftechsz.home.adapter
 * 描 述 : TODO
 */
public class TopSinAdapter extends BaseQuickAdapter<SignInBean.SignTopBean, DataBindBaseViewHolder> {
    public TopSinAdapter(List<SignInBean.SignTopBean> bean) {
        super(R.layout.item_top_sign, bean);
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder helper, SignInBean.SignTopBean bean) {
        ItemTopSignBinding mBinding = (ItemTopSignBinding) helper.getBind();
        mBinding.setVariable(BR.item, bean);
        mBinding.executePendingBindings();

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mBinding.bg.getLayoutParams();
        lp.width = ScreenUtils.getScreenWidth() / 2 - ConvertUtils.dp2px(60);
        mBinding.bg.setLayoutParams(lp);
    }
}
