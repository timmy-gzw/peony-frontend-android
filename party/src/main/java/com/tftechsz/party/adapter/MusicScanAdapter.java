package com.tftechsz.party.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.party.BR;
import com.tftechsz.party.R;
import com.tftechsz.party.databinding.ItmeMusicScanBinding;
import com.tftechsz.common.entity.AudioBean;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.party.adapter
 * 描 述 : 音乐扫描适配器
 */
public class MusicScanAdapter extends BaseQuickAdapter<AudioBean, DataBindBaseViewHolder> {
    private ItmeMusicScanBinding mBind;

    public MusicScanAdapter() {
        super(R.layout.itme_music_scan);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, AudioBean bean) {
        mBind = (ItmeMusicScanBinding) helper.getBind();
        mBind.setVariable(BR.item, bean);
        mBind.executePendingBindings();

        mBind.checkbox.setImageResource(bean.isChecked() ? R.mipmap.ic_check_selector : R.mipmap.ic_check_normal);
        mBind.time.setText(Utils.getLastTime1(bean.getDuration())/* + " -- " + FileUtils.getSize(bean.getPath())*/);

    }

}
