package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ConvertUtils;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.ExpressionVpAdapter;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 派对表情
 */
public class PartyExpressionPop extends BaseBottomPop {
    private ViewPager2 mVp;
    private List<GiftDto> giftDtoList;
    private ExpressionVpAdapter vpAdapters;
    private int currentPage = 0;   // 当前选中页数
    private int partyId;
    private LinearLayout llPoint;
    private boolean mIsFirst = true;
    private final CompositeDisposable mCompositeDisposable;


    public PartyExpressionPop(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        initUI();
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_party_expression);
    }


    private void initUI() {
        mVp = findViewById(R.id.vp);
        llPoint = findViewById(R.id.ll_point);
        vpAdapters = new ExpressionVpAdapter(mContext, null, giftDto -> {
            if (listener != null)
                listener.choosePicture(giftDto);
            dismiss();
        });
        mVp.setAdapter(vpAdapters);
        mVp.setCurrentItem(0);
        mVp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!mIsFirst) {
                    setCurrentPoint(position);
                }
                mIsFirst = false;
            }
        });
        if (vpAdapters != null)
            vpAdapters.setChecks(currentPage, 0);
    }


    public void setData(int partyId, List<GiftDto> data) {
        this.partyId = partyId;
        giftDtoList = data;
        vpAdapters.setList(giftDtoList);
        vpAdapters.notifyDataSetChanged();
        setPoint();
    }

    /**
     * 设置底部的点
     */
    private void setPoint() {
        llPoint.removeAllViews();
        int count = vpAdapters.getItemCount();
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = ConvertUtils.dp2px(5);
            params.leftMargin = ConvertUtils.dp2px(5);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.selector_yellow_point_bg);
            if (currentPage == i) {
                point.setEnabled(false);
            }
            llPoint.addView(point);
        }
    }



    private void setCurrentPoint(int position) {
        if (llPoint.getChildCount() <= position) {
            return;
        }
        llPoint.getChildAt(position).setEnabled(false);
        if (llPoint.getChildCount() > currentPage) {
            llPoint.getChildAt(currentPage).setEnabled(true);
        }
        Log.e("tag", "position=" + currentPage + "");
        currentPage = position;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public interface OnSelectListener {
        void choosePicture(GiftDto giftDto);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
