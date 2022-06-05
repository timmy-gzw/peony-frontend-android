package com.tftechsz.common.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.RedOpenDetails;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;

import androidx.annotation.NonNull;

public
/**
 *  包 名 : com.tftechsz.common.adapter

 *  描 述 : TODO
 */
class RedEnvelopeDetailsAdapter extends BaseQuickAdapter<RedOpenDetails.ListBean, BaseViewHolder> {
    private Context mContext;
    private int max_user_id;

    public RedEnvelopeDetailsAdapter(Context context, int max_user_id) {
        super(R.layout.item_red_envelope_details);
        mContext = context;
        this.max_user_id = max_user_id;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RedOpenDetails.ListBean item) {
        ImageView icon = helper.getView(R.id.icon);
        TextView name = helper.getView(R.id.name);
        TextView time = helper.getView(R.id.time);
        TextView gold = helper.getView(R.id.gold);
        TextView iv_sex = helper.getView(R.id.iv_sex);
        TextView feeling_lucky = helper.getView(R.id.feeling_lucky); //手气最佳

        GlideUtils.loadRoundImage(mContext, icon, item.icon, 6);
        name.setText(item.nickname);
        time.setText(item.created_at);
        gold.setText(String.format("%s金币", item.coin));
        CommonUtil.setSexAndAge(mContext, item.sex, item.age, iv_sex);
        feeling_lucky.setVisibility(max_user_id == item.user_id ? View.VISIBLE : View.GONE);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouterUtils.toMineDetailActivity(String.valueOf(item.user_id));
            }
        });
    }
}
