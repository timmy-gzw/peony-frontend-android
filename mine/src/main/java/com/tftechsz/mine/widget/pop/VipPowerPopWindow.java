package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ConvertUtils;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.VipPowerAdapter;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.databinding.PopVipPowerBinding;
import com.tftechsz.mine.entity.VipPrivilegeBean;


import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * VIP 弹窗
 */
public class VipPowerPopWindow extends BaseCenterPop implements View.OnClickListener {
    private final CompositeDisposable mCompositeDisposable;
    private PopVipPowerBinding mBind;

    public VipPowerPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        mContext = context;

    }


    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.pop_vip_power);
        mBind = DataBindingUtil.bind(view);
        return view;
    }


    public void initData(List<VipPrivilegeBean> data, int pos) {
        if (data != null && data.size() > 0) {
            VipPowerAdapter openVipAdapter = new VipPowerAdapter(mContext, data);
            mBind.viewPager.setAdapter(openVipAdapter);
            mBind.viewPager.setOffscreenPageLimit(data.size());
            setPoint(mBind.llPoint, data.size());
            mBind.viewPager.setCurrentItem(pos, false);
            setCurrentPoint(mBind.llPoint, pos);
            mBind.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    setCurrentPoint(mBind.llPoint, position);
                }
            });
            openVipAdapter.addOnClickListener(this::dismiss);
        }
    }


    /**
     * 设置底部的点
     */
    private void setPoint(LinearLayout llPoint, int count) {
        llPoint.removeAllViews();
        if (count == 0) return;
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = ConvertUtils.dp2px(3);
            params.leftMargin = ConvertUtils.dp2px(3);
            point.setBackgroundResource(R.drawable.selector_vip_point_bg);
            point.setLayoutParams(params);
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
