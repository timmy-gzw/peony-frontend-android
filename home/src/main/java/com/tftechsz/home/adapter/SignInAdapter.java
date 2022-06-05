package com.tftechsz.home.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.home.R;
import com.tftechsz.home.entity.SignInBean;
import com.tftechsz.common.utils.Utils;

import static com.tftechsz.common.Constants.SIGN_CHAT_CARD;

/**
 * 包 名 : com.tftechsz.home.adapter
 * 描 述 : TODO
 */
public class SignInAdapter extends BaseQuickAdapter<SignInBean.SignInListBean, BaseViewHolder> {
    private final int start_day;

    public SignInAdapter(int start_day) {
        super(R.layout.item_sign_in);
        this.start_day = start_day;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SignInBean.SignInListBean item) {
        TextView tv_status = helper.getView(R.id.tv_status);
        View left_line = helper.getView(R.id.left_line);
        View end_line = helper.getView(R.id.end_line);
        ImageView iv_gold_bg = helper.getView(R.id.iv_gold_bg);
        ImageView iv_gold = helper.getView(R.id.iv_gold);
        TextView tv_gold = helper.getView(R.id.tv_gold);
        View view = helper.getView(R.id.view);
        TextView tips = helper.getView(R.id.tips);
        View tips_line = helper.getView(R.id.tips_line);

        tv_status.setText(item.day);
        tv_gold.setText(item.cost);

        if (item.day_number < start_day) {
            if (item.is_complete == 1) {
                iv_gold.setImageResource(item.type.equals(SIGN_CHAT_CARD) ? R.mipmap.peony_ltk03_icon : R.mipmap.peony_qd_yqd_icon);
            } else {
                iv_gold.setImageResource(item.type.equals(SIGN_CHAT_CARD) ? R.mipmap.peony_ltk03_icon : R.mipmap.peony_qd_wqd_icon);
            }
        } else if (start_day == item.day_number) {
            iv_gold.setImageResource(item.type.equals(SIGN_CHAT_CARD) ? R.mipmap.peony_ltk02_icon : R.mipmap.peony_qd_yqd02_icon);
        } else {
            iv_gold.setImageResource(item.type.equals(SIGN_CHAT_CARD) ? R.mipmap.peony_ltk02_icon : R.drawable.peony_qd_wqd_icon);
        }
        tips.setVisibility(!TextUtils.isEmpty(item.tips) && item.day_number >= start_day ? View.VISIBLE : View.GONE);
        tips.setText(!TextUtils.isEmpty(item.tips) && item.day_number >= start_day ? item.tips : "送VIP体验");
        if (start_day == item.day_number) { //签到当天
            view.setBackgroundResource(R.drawable.shape_gradient_sign_in_item_bg);
            tips.setBackgroundResource(R.drawable.shape_sign_top_tips_sel);
            tv_status.setTextColor(Utils.getColor(R.color.white));
            left_line.setBackgroundResource(R.drawable.shape_gradient_sign_in_left_line_selected);
            end_line.setBackgroundResource(R.drawable.shape_gradient_sign_in_right_line_selected);
            iv_gold_bg.setVisibility(View.VISIBLE);
            tv_gold.setTextColor(Utils.getColor(R.color.white));
            if (!TextUtils.isEmpty(item.tips)) {
                tips_line.setVisibility(View.VISIBLE);
            }
        } else {
            view.setBackgroundResource(R.drawable.shape_bg_f9f9f9_radius_6);
            tips.setBackgroundResource(R.drawable.shape_sign_top_tips_nor);
            tv_status.setTextColor(Utils.getColor(R.color.color_999999));
            left_line.setBackgroundResource(R.drawable.shape_gradient_sign_in_left_line_normal);
            end_line.setBackgroundResource(R.drawable.shape_gradient_sign_in_right_line_normal);
            iv_gold_bg.setVisibility(View.GONE);
            tv_gold.setTextColor(Utils.getColor(R.color.color_999999));
            tips_line.setVisibility(View.GONE);
        }
    }
}
