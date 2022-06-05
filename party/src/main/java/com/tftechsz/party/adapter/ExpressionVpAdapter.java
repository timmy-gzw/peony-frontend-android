package com.tftechsz.party.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.party.R;

import java.util.ArrayList;
import java.util.List;

public class ExpressionVpAdapter extends RecyclerView.Adapter<ExpressionVpAdapter.ViewHolder> {
    private final static int DEFAULT_PAGE_SIZE = 8;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final SparseArray<ExpressionAdapter> adapters;//rv
    private final List<List<GiftDto>> mGifts = new ArrayList<>();
    private final IGiftVpOnItemClick mIGiftVpOnItemClick;
    private int currentPage, checkPosition;

    public ExpressionVpAdapter(Context context, List<GiftDto> gifts, IGiftVpOnItemClick iGiftVpOnItemClick) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(BaseApplication.getInstance());
        this.mIGiftVpOnItemClick = iGiftVpOnItemClick;
        adapters = new SparseArray<>();
        if (gifts != null && gifts.size() > 0)
            setList(gifts);
    }

    public void setList(List<GiftDto> gifts) {
        setList(gifts, 0, 0);
    }

    public void setList(List<GiftDto> gifts, int currentPage, int checkPosition) {
        this.currentPage = currentPage;
        this.checkPosition = checkPosition;
        if (adapters != null) {
            adapters.clear();
        }
        mGifts.clear();
        if (gifts.size() <= DEFAULT_PAGE_SIZE) {
            mGifts.add(gifts);
        } else {
            List<GiftDto> temp = new ArrayList<>();
            for (int i = 0; i < gifts.size(); i++) {
                temp.add(gifts.get(i));
                if (i != 0 && (i + 1) % 8 == 0 || i == gifts.size() - 1) {
                    mGifts.add(temp);
                    temp = new ArrayList<>();
                }
            }
        }
    }

    @NonNull
    @Override
    public ExpressionVpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_expression, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressionVpAdapter.ViewHolder holder, int position) {
        holder.rvGift.setLayoutManager(new GridLayoutManager(mContext, 4));
        if (null == adapters.get(position, null)) {
            ExpressionAdapter adapter = new ExpressionAdapter(mGifts.get(position), mIGiftVpOnItemClick, currentPage == position ? checkPosition : 0);
            adapters.put(position, adapter);
        }
        holder.rvGift.setAdapter(adapters.get(position));
    }

    @Override
    public int getItemCount() {
        return mGifts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvGift;
        ImageView ivEmpty;
        TextView tvEmpty;

        ViewHolder(View itemView) {
            super(itemView);
            rvGift = itemView.findViewById(R.id.rv);
            ivEmpty = itemView.findViewById(R.id.iv_empty);
            tvEmpty = itemView.findViewById(R.id.tv_empty);
        }
    }

    public GiftDto getGift(int position) {
        Log.e("tag", adapters.size() + "");
        return adapters.get(position) == null ? null : adapters.get(position).getGift();
    }

    public int getCheckPosition(int page) {
        if (null == adapters || null == adapters.get(page))
            return 0;
        return adapters.get(page).getCheckPosition();
    }


    public void setChecks(int page, int position) {
        if (null == adapters || null == adapters.get(page))
            return;
        adapters.get(page).setCheckPositions(position);

    }

    public interface IGiftVpOnItemClick {
        void onitemClickGiftInfoActivityGet(GiftDto giftDto);
    }
}
