package com.tftechsz.mine.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 我的
 */
public class MineMidAdapter extends BaseQuickAdapter<ConfigInfo.MineInfo, BaseViewHolder> {
    public MineMidAdapter(@Nullable List<ConfigInfo.MineInfo> data) {
        super(R.layout.item_mine_mid, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfigInfo.MineInfo item) {
        ImageView img_icon = helper.getView(R.id.img_icon);
        TextView tv_name = helper.getView(R.id.tv_name);
        GlideUtils.loadRouteImage(BaseApplication.getInstance(), img_icon, item.icon);
        tv_name.setText(item.title);
       addChildClickViewIds(R.id.root_view);
       /* GlideUtils.loadRouteImage(mContext, helper.getView(R.id.iv_pic), item.icon);
        helper.setText(R.id.tv_name, item.title);
        View view = helper.getView(R.id.view1);
        if (helper.getLayoutPosition() == getData().size() - 1) {
            view.setVisibility(View.GONE);
        }
        helper.setGone(R.id.iv_red_package, !TextUtils.equals(item.title, "红包福利"));*/
    }
}
