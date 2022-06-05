package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.ConfessionLetterAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.CoupleLetterDto;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 表白信
 */
public class ConfessionLetterPopWindow extends BaseCenterPop {
    private ViewPager2 viewPager2;
    private LinearLayout llPoint;
    private ConfessionLetterAdapter mAdapter;
    private final ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;
    private int currentPage = 0;   // 当前选中页数

    public ConfessionLetterPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        viewPager2 = findViewById(R.id.view_pager);
        llPoint = findViewById(R.id.ll_point);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_confession_letter);
    }


    public void initData(List<CoupleLetterDto> data) {
        mAdapter = new ConfessionLetterAdapter();
        mAdapter.setList(data);
        viewPager2.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.tv_think, R.id.tv_accept);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            CoupleLetterDto coupleLetterDto = mAdapter.getData().get(position);
            if (coupleLetterDto == null) return;
            if (view.getId() == R.id.tv_think) { //拒绝
                operateLetter(coupleLetterDto.id, 2);
            } else if (view.getId() == R.id.tv_accept) {  //接受
                operateLetter(coupleLetterDto.id, 1);
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        setPoint();


    }


    /**
     * 表白信操作
     * type 1-接受 2-拒绝
     */
    public void operateLetter(int id, int type) {
        mCompositeDisposable.add(chatApiService.operateLetter(id, type).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (response.getData() != null) {
                    dismiss();
                }
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                dismiss();
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    /**
     * 设置底部的点
     */
    private void setPoint() {
        int count = mAdapter.getItemCount();
        if (count <= 1) return;
        llPoint.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView point = new ImageView(BaseApplication.getInstance());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = DensityUtils.dp2px(BaseApplication.getInstance(), 5);
            params.leftMargin = DensityUtils.dp2px(BaseApplication.getInstance(), 5);
            point.setLayoutParams(params);
            point.setBackgroundResource(R.drawable.selector_gift_point_bg);
            if (0 == i) {
                point.setEnabled(false);
            }
            llPoint.addView(point);
        }
    }

    private void setCurrentPoint(int position) {
        int count = mAdapter.getItemCount();
        if (count <= 1) return;
        llPoint.getChildAt(position).setEnabled(false);
        llPoint.getChildAt(currentPage).setEnabled(true);
        Log.e("tag", "position=" + currentPage + "");
        currentPage = position;

    }

}
