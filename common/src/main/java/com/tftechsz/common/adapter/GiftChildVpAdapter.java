package com.tftechsz.common.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.IGiftVpOnItemClick;
import com.tftechsz.common.entity.GiftDto;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class GiftChildVpAdapter extends BaseQuickAdapter<List<GiftDto>, BaseViewHolder> {
    private final IGiftVpOnItemClick mIGiftVpOnItemClick;
    private List<GiftDto> selGift = new ArrayList<>();
    private List<GiftVpAdapter> giftVpAdapters = new ArrayList<>();

    public GiftChildVpAdapter(IGiftVpOnItemClick iGiftVpOnItemClick) {
        super(R.layout.item_child_giftvp);
        this.mIGiftVpOnItemClick = iGiftVpOnItemClick;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, List<GiftDto> giftDtos) {
        ViewPager2 mGiftVp = holder.getView(R.id.viewpager2);
        ImageView ivEmpty = holder.getView(R.id.iv_empty);
        TextView tvEmpty = holder.getView(R.id.tv_empty);
        LinearLayout llPoint = holder.getView(R.id.ll_point);
        if (giftDtos.size() == 0) {//数据为空
            mGiftVp.setVisibility(View.GONE);
            ivEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.VISIBLE);
            setPoint(llPoint, 0);
        } else { //有数据
            setPoint(llPoint, giftDtos.size());
            selGift.set(getItemPosition(giftDtos), giftDtos.get(0));
            mGiftVp.setVisibility(View.VISIBLE);
            ivEmpty.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
            mGiftVp.requestDisallowInterceptTouchEvent(true);
            GiftVpAdapter vpAdapter = new GiftVpAdapter(getContext(), giftDtos, mIGiftVpOnItemClick);
            mGiftVp.setAdapter(vpAdapter);
            //mGiftVp.setOffscreenPageLimit(giftDtos.size());
            mGiftVp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    vpAdapter.setChecks(position, 0);
                    mIGiftVpOnItemClick.onitemClickGiftInfoActivityGet(vpAdapter.getGift(position));
                    mIGiftVpOnItemClick.onItemCurrent(position);
                    setCurrentPoint(llPoint, position);
                    selGift.set(getItemPosition(giftDtos), vpAdapter.getGift(position));
                }
            });
            giftVpAdapters.set(getItemPosition(giftDtos), vpAdapter);
        }
    }

    /**
     * 设置底部的点
     */
    private void setPoint(LinearLayout llPoint, int count) {
        llPoint.removeAllViews();
        if (count == 0) return;

        int i1 = count % 8;
        count = i1 == 0 ? count / 8 : count / 8 + 1;
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = ConvertUtils.dp2px(5);
            params.leftMargin = ConvertUtils.dp2px(5);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.selector_gift_point_bg);
            llPoint.addView(point);
        }
    }

    private void setCurrentPoint(LinearLayout llPoint, int position) {
        if (llPoint.getChildCount() <= position) {
            return;
        }
        for (int i = 0, j = llPoint.getChildCount(); i < j; i++) {
            llPoint.getChildAt(i).setEnabled(i != position);
        }
    }

    public void setSelGiftList(List<GiftVpAdapter> giftVpAdapters, List<GiftDto> selGift) {
        this.giftVpAdapters = giftVpAdapters;
        this.selGift = selGift;
    }

    public void setGiftList(int index, GiftDto gift) {
        selGift.set(index, gift);
    }

    public GiftDto getSelGiftDto(int page) {
        return selGift.get(page);
    }

    public GiftVpAdapter getGiftVpAdapter(int page) {
        return giftVpAdapters.get(page);
    }
}
