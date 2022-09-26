package com.tftechsz.mine.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.mine.R;

/**
 * 用户基本信息
 */
public class BaseUserInfoAdapter extends BaseQuickAdapter<UserInfo.BaseInfo, BaseViewHolder> {

    private int textWidth = 0;

    public BaseUserInfoAdapter(boolean isGa) {
        super(isGa ? R.layout.item_base_info_ga : R.layout.item_base_info);
    }

    public void setTextViewMaxWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo.BaseInfo item) {
        helper.setText(R.id.tv_value, item.value);

        TextView textView = helper.getView(R.id.tv_name);
        textView.setText(item.title);
        if (textWidth > 0) {
            textView.setWidth(textWidth);
        }
        helper.setGone(R.id.iv_copy, helper.getLayoutPosition() != 0);
    }
}
