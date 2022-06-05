package com.tftechsz.party.adapter;


import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.party.BR;
import com.tftechsz.party.R;
import com.tftechsz.party.databinding.ItemPartySettingBgBinding;
import com.tftechsz.party.entity.PartyManagerBgBean;

import net.mikaelzero.mojito.Mojito;
import net.mikaelzero.mojito.impl.DefaultPercentProgress;

import org.jetbrains.annotations.NotNull;

/**
 * 派对设置-背景
 */
public class PartySettingAdapter extends BaseQuickAdapter<PartyManagerBgBean, DataBindBaseViewHolder> {
    private Context context;

    public PartySettingAdapter(Context context) {
        super(R.layout.item_party_setting_bg);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull DataBindBaseViewHolder vh, PartyManagerBgBean bean) {
       /* ItemSettingPartyBgBinding bind = (ItemSettingPartyBgBinding) vh.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();
*/
        ItemPartySettingBgBinding itemPartySettingBgBinding = (ItemPartySettingBgBinding) vh.getBind();
        itemPartySettingBgBinding.setVariable(BR.item, bean);
        itemPartySettingBgBinding.executePendingBindings();
    }

    /**
     * 当前选择图片的value
     *
     * @return
     */
    public String getSel() {
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            if (getData().get(i).isSelected) {
                return getData().get(i).value;
            }
        }
        return null;
    }

    /**
     * 设置选择图片的value
     *
     * @return
     */
    public void setSel(int position) {
        if (getData().size() > position && getData().get(position).isSelected) {
            Mojito.with(context)
                    .urls(getData().get(position).icon)
                    .autoLoadTarget(false)
                    .setProgressLoader(DefaultPercentProgress::new)
                    .start();

            return;
        }
        int size = getData().size();
        for (int i = 0; i < size; i++) {
            getData().get(i).isSelected = i == position;
        }
        notifyDataSetChanged();
    }
}
