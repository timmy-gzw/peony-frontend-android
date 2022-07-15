package com.tftechsz.home.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.home.R;
import com.tftechsz.home.entity.SignInBean;

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
        ImageView iv_gold = helper.getView(R.id.iv_gold);
        TextView tv_gold = helper.getView(R.id.tv_gold);
        View view = helper.getView(R.id.cl_container);

        tv_status.setText(TextUtils.isEmpty(item.tips) ? item.day : item.tips);
        tv_gold.setText(item.cost);

        if (item.day_number < start_day && item.is_complete == 1) {
            iv_gold.setImageResource(R.mipmap.peony_qd_yqd02_icon);
            view.setBackgroundResource(R.drawable.shape_sign_in_item_bg);
            tv_status.setTextColor(Utils.getColor(R.color.c_ff9e00));
            tv_gold.setTextColor(Utils.getColor(R.color.c_ff9e00));
        } else {
            view.setBackgroundResource(R.drawable.sp_f7f7f7_c_5);
            iv_gold.setImageResource(R.mipmap.peony_qd_wqd_icon);
            tv_status.setTextColor(Utils.getColor(R.color.color_light_font));
            tv_gold.setTextColor(Utils.getColor(R.color.color_light_font));
        }
    }
}
