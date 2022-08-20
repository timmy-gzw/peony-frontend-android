package com.tftechsz.mine.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.BaseItemBean;

/**
 * 我的
 */
public class BaseItemAdapter extends BaseQuickAdapter<BaseItemBean, BaseViewHolder> {
    public BaseItemAdapter() {
        super(R.layout.item_mine_bot);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseItemBean item) {
        ImageView left_icon = helper.getView(R.id.iv_pic);
        ImageView right_icon = helper.getView(R.id.right_icon);
        TextView right_txt = helper.getView(R.id.right_txt);
        right_icon.setVisibility(View.GONE);
        right_txt.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(item.left_icon)) {
            if (!"访客记录".equals(item.title) || left_icon.getTag() == null) {
                GlideUtils.loadRouteImage(getContext(), left_icon, item.left_icon);
            }
            left_icon.setTag("true");
        }
        if (item.right_icon != 0) {
            if (!"访客记录".equals(item.title) || right_icon.getTag() == null) {
                Glide.with(getContext()).load(item.right_icon).into(right_icon);
            }
            right_icon.setTag("true");
            right_icon.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(item.right_txt)) {
            right_txt.setText(item.right_txt);
            right_txt.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(item.right_txt_color) && item.right_txt_color.charAt(0) == '#' &&
                (item.right_txt_color.length() == 7 || item.right_txt_color.length() == 9)) {
            right_txt.setTextColor(Color.parseColor(item.right_txt_color));
        }

        helper.setText(R.id.tv_name, item.title);
        View view = helper.getView(R.id.view1);
        if (helper.getLayoutPosition() == getData().size() - 1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
