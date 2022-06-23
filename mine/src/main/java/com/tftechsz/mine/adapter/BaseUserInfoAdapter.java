package com.tftechsz.mine.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.mine.R;

/**
 * 用户基本信息
 */
public class BaseUserInfoAdapter extends BaseQuickAdapter<UserInfo.BaseInfo, BaseViewHolder> {

    public BaseUserInfoAdapter() {
        super(R.layout.item_base_info);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo.BaseInfo item) {
        helper.setText(R.id.tv_name, item.title)
                .setText(R.id.tv_value, item.value);
    }
}
