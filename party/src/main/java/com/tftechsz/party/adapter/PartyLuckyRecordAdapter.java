package com.tftechsz.party.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartyTurntableBean;

import org.jetbrains.annotations.NotNull;

public class PartyLuckyRecordAdapter extends BaseQuickAdapter<PartyTurntableBean, BaseViewHolder> {
    private GiftRecordPartyAdapter giftRankingPartyAdapter;
    private boolean mIsActivityBg;//活动皮肤

    public PartyLuckyRecordAdapter(boolean isActivityBg) {
        super(R.layout.item_record_adapter);
        this.mIsActivityBg = isActivityBg;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PartyTurntableBean item) {
        if (item.number.contains("100")) {
            baseViewHolder.setImageResource(R.id.tv_title_ira, R.drawable.record_click_100);
        } else if (item.number.contains("10")) {
            baseViewHolder.setImageResource(R.id.tv_title_ira, R.drawable.record_click_10);
        } else if (item.number.contains("1")) {
            baseViewHolder.setImageResource(R.id.tv_title_ira, R.drawable.record_click_1);
        }
        ConstraintLayout constraintLayout = baseViewHolder.getView(R.id.rel_pop_bottom_alg);
        constraintLayout.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_record_bgitem : R.drawable.party_record_bgitem);
        baseViewHolder.setText(R.id.tv_session, "(" + item.type + ")");
        baseViewHolder.setText(R.id.tv_time_ira, item.created_at);
        RecyclerView recyclerView = baseViewHolder.getView(R.id.rec_ira);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(baseViewHolder.itemView.getContext(), 6);
        recyclerView.setLayoutManager(gridLayoutManager2);
        giftRankingPartyAdapter = new GiftRecordPartyAdapter();
        giftRankingPartyAdapter.setList(item.reward);
        recyclerView.setAdapter(giftRankingPartyAdapter);
        TextView tvText = baseViewHolder.getView(R.id.tv_time_ira);
        tvText.setTextColor(mIsActivityBg ? Color.parseColor("#06404B") : Color.parseColor("#7bf1f9"));
    }
}
