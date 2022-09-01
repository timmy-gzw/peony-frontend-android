package com.tftechsz.main.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.main.R;

import java.util.List;

public class AccostUserAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {


    public AccostUserAdapter(@Nullable List<UserInfo> accostList) {
        super(R.layout.item_accost_user, accostList);
    }


    public void multipleChoose(int position) {
        UserInfo contactInfo = getData().get(position);
        contactInfo.setSelected(!contactInfo.isSelected());
        notifyItemChanged(position);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserInfo item) {
        ImageView ivCheck = helper.getView(R.id.iv_check);
        TextView age = helper.getView(com.tftechsz.common.R.id.tv_age);
        ImageView sex = helper.getView(com.tftechsz.common.R.id.iv_sex);
        LinearLayout llage = helper.getView(com.tftechsz.common.R.id.ll_age);
        helper.setText(R.id.tv_name, item.getNickname());
        if (item.getSex() == 2) {
            llage.setBackgroundResource(com.tftechsz.common.R.drawable.bg_girl_age_sex);
            sex.setImageResource(com.tftechsz.common.R.drawable.ic_girl);
        } else {
            llage.setBackgroundResource(com.tftechsz.common.R.drawable.bg_boy_age_sex);
            sex.setImageResource(com.tftechsz.common.R.drawable.ic_boy);
        }
        age.setText(item.getAge() + "");
        GlideUtils.loadRoundImage(getContext(), (ImageView) helper.getView(R.id.iv_avatar), item.getIcon(), getContext().getResources().getInteger(R.integer.one_key_accost_radius));
        if (getData().get(getItemPosition(item)).isSelected()) {
            ivCheck.setImageResource(R.mipmap.ic_check_selector);
        } else {
            ivCheck.setImageResource(R.mipmap.ic_check_normal);
        }
    }


}
