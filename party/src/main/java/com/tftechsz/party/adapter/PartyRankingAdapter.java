package com.tftechsz.party.adapter;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;
import com.tftechsz.party.entity.PartyRankBean;

import org.jetbrains.annotations.NotNull;

/**
 * 排行榜adapter
 */
public class PartyRankingAdapter extends BaseQuickAdapter<PartyRankBean.ListDTO, BaseViewHolder> {
    private GiftRankingPartyAdapter giftRankingPartyAdapter;
    private boolean mIsActivityBg;//活动皮肤

    public PartyRankingAdapter(boolean isActivityBg) {
        super(R.layout.item_rank_adapter);
        this.mIsActivityBg = isActivityBg;
    }


    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PartyRankBean.ListDTO item) {
        if (item.index.equals("1")) {
            baseViewHolder.setBackgroundResource(R.id.tv_ran_number, R.drawable.party_rank_pop1);
            baseViewHolder.setText(R.id.tv_ran_number, "");
        } else if (item.index.equals("2")) {
            baseViewHolder.setBackgroundResource(R.id.tv_ran_number, R.drawable.party_rank_pop2);
            baseViewHolder.setText(R.id.tv_ran_number, "");
        } else if (item.index.equals("3")) {
            baseViewHolder.setBackgroundResource(R.id.tv_ran_number, R.drawable.party_rank_pop3);
            baseViewHolder.setText(R.id.tv_ran_number, "");
        } else {
            baseViewHolder.setBackgroundResource(R.id.tv_ran_number, android.R.color.transparent);
            baseViewHolder.setText(R.id.tv_ran_number, String.valueOf(item.index));
        }

        ConstraintLayout constraintLayout = baseViewHolder.getView(R.id.rel_pop_bottom_alg);
        constraintLayout.setBackgroundResource(mIsActivityBg ? R.drawable.activity_party_record_bgitem : R.drawable.party_ranking_bgitem);
        GlideUtils.loadCircleImage(baseViewHolder.itemView.getContext(), baseViewHolder.getView(R.id.img_ran_header), item.icon, R.drawable.party_ic_member_empty);
        baseViewHolder.setText(R.id.tv_name_ran, item.nickname);
        baseViewHolder.setTextColor(R.id.tv_name_ran, mIsActivityBg ? Color.parseColor("#06404B") : Color.parseColor("#ff7bf1f9"));
        RecyclerView recyclerView = baseViewHolder.getView(R.id.rec_rank_adapter);
        giftRankingPartyAdapter = new GiftRankingPartyAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(baseViewHolder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        giftRankingPartyAdapter.setList(item.gift_list);
        recyclerView.setAdapter(giftRankingPartyAdapter);

        AssetManager mgr = getContext().getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/ranking.ttf");
        TextView tv = baseViewHolder.getView(R.id.tv_ran_number);
        tv.setTypeface(tf);
    }
}
