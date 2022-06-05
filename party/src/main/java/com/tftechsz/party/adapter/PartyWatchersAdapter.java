package com.tftechsz.party.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.dto.WatcherRankDto;

import org.jetbrains.annotations.NotNull;


public class PartyWatchersAdapter extends BaseQuickAdapter<WatcherRankDto, BaseViewHolder> {
    private final int[] valueBg = {R.drawable.bg_watcher_top1, R.drawable.bg_watcher_top2, R.drawable.bg_watcher_top3};

    public PartyWatchersAdapter() {
        super(R.layout.item_party_watcher);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, WatcherRankDto item) {
        ImageView ivHeader = baseViewHolder.getView(R.id.iv_header);
        CircleImageView ivAvatar = baseViewHolder.getView(R.id.iv_avatar);
        TextView tvContent = baseViewHolder.getView(R.id.tv_value);
        if (baseViewHolder.getLayoutPosition() == getData().size() - 1) {
            ivHeader.setVisibility(View.VISIBLE);
        } else {
            ivHeader.setVisibility(View.INVISIBLE);
        }
        tvContent.setBackgroundResource(valueBg[(getData().size() - 1) - baseViewHolder.getLayoutPosition()]);
        baseViewHolder.setText(R.id.tv_value, item.getValue());

        GlideUtils.loadCircleImage(getContext(), ivAvatar, item.getIcon(), R.mipmap.ic_default_avatar);
    }
}
