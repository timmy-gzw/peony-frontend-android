package com.tftechsz.mine.adapter;

import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;

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

    public BaseUserInfoAdapter() {
        this(false);
    }

    public void setTextViewMaxWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo.BaseInfo item) {
        TextView tvValue = helper.getView(R.id.tv_value);
        TextView textView = helper.getView(R.id.tv_name);
        textView.setText(item.title);
        if (textWidth > 0) {
            textView.setWidth(textWidth);
        }

        if (helper.getLayoutPosition() == 0) {
            helper.setGone(R.id.iv_copy, false);
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tvValue, 8, 12, 1, TypedValue.COMPLEX_UNIT_SP);
        } else {
            helper.setGone(R.id.iv_copy, true);
            TextViewCompat.setAutoSizeTextTypeWithDefaults(tvValue, TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE);
            tvValue.setTextSize(12);
        }
        tvValue.setText(item.value);
    }
}
