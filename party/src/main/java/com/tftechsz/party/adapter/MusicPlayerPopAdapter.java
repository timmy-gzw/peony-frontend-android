package com.tftechsz.party.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tftechsz.common.adapter.DataBindBaseViewHolder;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.party.BR;
import com.tftechsz.party.R;
import com.tftechsz.party.databinding.ItemMusicPlayerPopBinding;
import com.tftechsz.common.entity.AudioBean;

import androidx.annotation.NonNull;

/**
 * 包 名 : com.tftechsz.party.adapter
 * 描 述 : TODO
 */
public class MusicPlayerPopAdapter extends BaseQuickAdapter<AudioBean, DataBindBaseViewHolder> {

    private boolean isEdit;

    public MusicPlayerPopAdapter() {
        super(R.layout.item_music_player_pop);
    }

    @Override
    protected void convert(@NonNull DataBindBaseViewHolder helper, AudioBean bean) {
        com.tftechsz.party.databinding.ItemMusicPlayerPopBinding bind = (ItemMusicPlayerPopBinding) helper.getBind();
        bind.setVariable(BR.item, bean);
        bind.executePendingBindings();

        if (bean.isPlaying()) {
            bind.lottieBg.playAnimation();
        } else if (bean.isPlayPause()) {
            bind.lottieBg.pauseAnimation();
        } else {
            bind.lottieBg.cancelAnimation();
        }
        bind.ivDel.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        bind.ivSort.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        bind.time.setText(Utils.getLastTime1(bean.getDuration())/* + " -- " + FileUtils.getSize(bean.getPath())*/);
    }

    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        compatibilityDataSizeChanged(getItemCount());
    }
}
