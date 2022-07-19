package com.tftechsz.mine.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;

/**
 * 我的
 */
public class MineMidAdapter extends BaseQuickAdapter<ConfigInfo.MineInfo, BaseViewHolder> {
    public MineMidAdapter() {
        super(R.layout.item_mine_mid);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfigInfo.MineInfo item) {
        helper.setText(R.id.tv_name, item.title);
        ImageView img_icon = helper.getView(R.id.img_icon);
        GlideUtils.loadRouteImage(BaseApplication.getInstance(), img_icon, item.icon);
       /* GlideUtils.loadRouteImage(mContext, helper.getView(R.id.iv_pic), item.icon);
        helper.setText(R.id.tv_name, item.title);
        View view = helper.getView(R.id.view1);
        if (helper.getLayoutPosition() == getData().size() - 1) {
            view.setVisibility(View.GONE);
        }
        helper.setGone(R.id.iv_red_package, !TextUtils.equals(item.title, "红包福利"));*/

        if ("谁看过我".equals(item.title) || "访客记录".equals(item.title)) {
            helper.setGone(R.id.tv_count, item.visitor_count <= 0)
                    .setText(R.id.tv_count, String.valueOf(item.visitor_count));
        }
    }
}
