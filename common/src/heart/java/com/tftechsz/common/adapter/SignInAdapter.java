package com.tftechsz.common.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.SignInBean;
import com.tftechsz.common.utils.Utils;

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

        tv_gold.setText("x" + item.cost);

        if (item.day_number <= start_day) {
            tv_status.setText(item.is_complete == 1 ? getContext().getString(R.string.sign_in_already) : item.day);
            iv_gold.setImageResource(R.mipmap.peony_qd_yqd02_icon);
            view.setBackgroundResource(R.drawable.shape_sign_in_item_bg);
            tv_status.setTextColor(Utils.getColor(R.color.white));
            tv_gold.setTextColor(Utils.getColor(R.color.white));
        } else {
            tv_status.setText(item.day);
            view.setBackgroundResource(R.drawable.sp_f7f7f7_c_5);
            iv_gold.setImageResource(R.mipmap.peony_qd_wqd_icon);
            tv_status.setTextColor(Utils.getColor(R.color.color_light_font));
            tv_gold.setTextColor(Utils.getColor(R.color.color_light_font));
        }
    }
}
