package com.tftechsz.im.adapter;


import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.MoreFunDto;

import org.jetbrains.annotations.NotNull;

public class MoreFunAdapter extends BaseQuickAdapter<MoreFunDto, BaseViewHolder> {
    public MoreFunAdapter() {
        super(R.layout.item_more_fun);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, MoreFunDto moreFunDto) {
        ConstraintLayout root = helper.getView(R.id.more_root);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) root.getLayoutParams();
        lp.gravity = moreFunDto.show_new ? Gravity.END : Gravity.CENTER;
        root.setLayoutParams(lp);
        helper.setText(R.id.tv_more, moreFunDto.content);
        helper.setImageResource(R.id.iv_pic, moreFunDto.bg);
        helper.setGone(R.id.show_new, !moreFunDto.show_new);
        helper.setVisible(R.id.iv_mode, moreFunDto.id == 5);
    }
}
