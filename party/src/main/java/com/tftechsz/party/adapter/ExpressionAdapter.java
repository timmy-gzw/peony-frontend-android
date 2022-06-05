package com.tftechsz.party.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExpressionAdapter extends BaseQuickAdapter<GiftDto, BaseViewHolder> {

    private int checkPosition;
    private final ExpressionVpAdapter.IGiftVpOnItemClick iGiftVpOnItemClick;


    public ExpressionAdapter(@Nullable List<GiftDto> data, ExpressionVpAdapter.IGiftVpOnItemClick iGiftVpOnItemClick, int checkPosition) {
        super(R.layout.item_expression_emj, data);
        this.checkPosition = checkPosition;
        this.iGiftVpOnItemClick = iGiftVpOnItemClick;
    }

    public void setCheckPosition(int p) {
        checkPosition = p;
    }

    public void setCheckPositions(int p) {
        checkPosition = p;
        notifyDataSetChanged();

    }

    public int getCheckPosition() {
        return checkPosition;
    }

    public GiftDto getGift() {
        if (checkPosition != -1 && getItemCount() > getCheckPosition()) {
            return getItem(checkPosition);
        }
        return null;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDto item) {
        helper.setText(R.id.tv_name, item.name);
        GlideUtils.loadRouteImage(getContext(), helper.getView(R.id.iv), item.getImage());
    }

    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (checkPosition >= getItemCount()) {
            checkPosition = 0;
        }
        holder.itemView.setOnClickListener(view -> {
            checkPosition = position;
            if (iGiftVpOnItemClick != null) {
                iGiftVpOnItemClick.onitemClickGiftInfoActivityGet(getGift());
            }
            notifyDataSetChanged();
        });
    }

}
