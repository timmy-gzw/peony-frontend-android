package com.tftechsz.common.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 包 名 : com.tftechsz.common.adapter
 * 描 述 : TODO
 */
public class RealPopAdapter extends BaseQuickAdapter<ConfigInfo.RealIcon, BaseViewHolder> {
    public RealPopAdapter(List<ConfigInfo.RealIcon> data) {
        super(R.layout.item_real_pop, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, ConfigInfo.RealIcon bean) {
        ImageView icon = helper.getView(R.id.icon);
        TextView name = helper.getView(R.id.name);

        GlideUtils.loadImage(getContext(), icon, bean.icon);
        name.setText(bean.title);
    }
}
