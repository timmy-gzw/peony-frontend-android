package com.tftechsz.common.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.IGiftVpOnItemClick;
import com.tftechsz.common.entity.GiftDto;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GiftVpAdapter extends RecyclerView.Adapter<GiftVpAdapter.ViewHolder> {
    private final static int DEFAULT_PAGE_SIZE = 8;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final SparseArray<GiftAdapter> adapters;//rv
    private final List<List<GiftDto>> mGifts = new ArrayList<>();
    private final IGiftVpOnItemClick mIGiftVpOnItemClick;
    private int currentPage, checkPosition;

    public GiftVpAdapter(Context context, List<GiftDto> gifts, IGiftVpOnItemClick iGiftVpOnItemClick) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(BaseApplication.getInstance());
        this.mIGiftVpOnItemClick = iGiftVpOnItemClick;
        adapters = new SparseArray<>();
        if (gifts != null && gifts.size() > 0)
            setList(gifts);
    }

    public void setList(List<GiftDto> gifts) {
        setList(gifts, 0, 0, 0);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<GiftDto> gifts, int currentPage, int checkPosition, int giftId) {
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

        if (currentPage >= mGifts.size()) {
            return;
        }

        List<GiftDto> giftDtos = mGifts.get(this.currentPage);
        if (giftDtos == null || giftId == 0) return;
        for (int i = 0; i < giftDtos.size(); i++) {
            if (giftDtos.get(i).getId() == giftId) {
                this.checkPosition = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GiftVpAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_giftvp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftVpAdapter.ViewHolder holder, int position) {
        if (mGifts.get(position).size() == 0) {//数据为空
            holder.rvGift.setVisibility(View.GONE);
            holder.ivEmpty.setVisibility(View.VISIBLE);
            holder.tvEmpty.setVisibility(View.VISIBLE);
        } else { //有数据
            holder.rvGift.setVisibility(View.VISIBLE);
            holder.ivEmpty.setVisibility(View.GONE);
            holder.tvEmpty.setVisibility(View.GONE);
            holder.rvGift.setLayoutManager(new GridLayoutManager(mContext, 4));
            if (null == adapters.get(position, null)) {
                GiftAdapter adapter = new GiftAdapter(mGifts.get(position), mIGiftVpOnItemClick, currentPage == position ? checkPosition : 0);
                adapters.put(position, adapter);
            }
            holder.rvGift.setAdapter(adapters.get(position));
        }
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
}
