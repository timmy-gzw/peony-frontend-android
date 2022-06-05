package com.tftechsz.mine.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.mine.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 我的
 */
public class AboutAdapter extends BaseQuickAdapter<ConfigInfo.MineInfo, BaseViewHolder> {
    public AboutAdapter(@Nullable List<ConfigInfo.MineInfo> data) {
        super(R.layout.item_about, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfigInfo.MineInfo item) {
        helper.setText(R.id.tv_name, item.title);
        View view = helper.getView(R.id.view1);
        if (helper.getLayoutPosition() == getData().size() - 1) {
            view.setVisibility(View.GONE);
        }

    }
}
