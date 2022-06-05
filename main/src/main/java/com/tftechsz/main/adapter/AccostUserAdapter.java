package com.tftechsz.main.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.Constants;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.StringUtils;
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
        TextView tvSex = helper.getView(R.id.tv_sex);
        CommonUtil.setSexAndAge(getContext(), item.getSex(), String.valueOf(item.getAge()), tvSex);
        String name = StringUtils.handleText(item.getNickname(), Constants.MAX_NAME_LENGTH);
        helper.setText(R.id.tv_name, name)
                .setText(R.id.tv_city, item.getCity());
        GlideUtils.loadRoundImage(getContext(), (ImageView) helper.getView(R.id.iv_avatar), item.getIcon());
        if (getData().get(helper.getLayoutPosition()).isSelected()) {
            ivCheck.setImageResource(R.mipmap.ic_accost_check_selector);
        } else {
            ivCheck.setImageResource(R.mipmap.ic_accost_check_normal);
        }
    }


}
