package com.tftechsz.common.adapter;

import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class DataBindBaseViewHolder  extends BaseViewHolder {
    private final ViewDataBinding binding;

    public DataBindBaseViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public ViewDataBinding getBind() {
        return binding;
    }
}
