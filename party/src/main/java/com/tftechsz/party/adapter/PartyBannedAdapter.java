package com.tftechsz.party.adapter;


import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartySetListConfigBean;

/**
 * 房间禁言
 */
public class PartyBannedAdapter extends BaseQuickAdapter<PartySetListConfigBean.KickDTO, BaseViewHolder> {


    public PartyBannedAdapter() {
        super(R.layout.item_banner_adapter);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PartySetListConfigBean.KickDTO item) {
        CheckBox checkBox = helper.getView(R.id.iv_check);
        checkBox.setChecked(item.isCheck);
        helper.setText(R.id.tv_iba_day, item.desc);
    }

    public int getOpType() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).isCheck) {
                return getData().get(i).type;
            }
        }
        return 0;
    }

    /**
     * 选中时间
     */
    public void setSelectData(int position) {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            getData().get(i).isCheck = i == position;
        }
        notifyDataSetChanged();
    }
}
