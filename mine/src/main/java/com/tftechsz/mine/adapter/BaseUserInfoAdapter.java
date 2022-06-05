package com.tftechsz.mine.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.mine.R;

import java.util.List;

/**
 * 用户基本信息
 */
public class BaseUserInfoAdapter extends BaseQuickAdapter<UserInfo.BaseInfo, BaseViewHolder> {
    int showIcon;

    public BaseUserInfoAdapter(@Nullable List<UserInfo.BaseInfo> data, int showIcon) {
        super(R.layout.item_base_info, data);
        this.showIcon = showIcon;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo.BaseInfo item) {

        helper.setText(R.id.tv_name, item.title)
                .setText(R.id.tv_value, item.value);

        TextView tv = helper.getView(R.id.tv_value);
        if (showIcon == 1 && item.title.contains("芍药")) {
            tv.setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawable(R.drawable.min_name_img_left), null, null, null);
        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}
