package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.im.R;
import com.tftechsz.im.adapter.CoupleBagGiftAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.CoupleBagDetailDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.AnimationUtil;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 情侣礼包详情购买
 */
public class CoupleGiftBagDetailPop extends BaseCenterPop implements View.OnClickListener {

    private final ImageView mIvLeft, mIvRight, mIvCountTime;
    private final TextView mTvCoupleTime;  //倒计时
    private final TextView mTvPrice, mTvCouplePrice;  //购买价格
    private final TextView mTvWelcome;
    public CoupleBagDetailDto mData;
    private final LinearLayout mLLBuy;
    private CountBackUtils countBackUtils;
    private final RecyclerView mRvLove;
    private int mFamilyId;
    private int mId;
    private ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;

    public CoupleGiftBagDetailPop(Context context) {
        super(context);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        mTvCoupleTime = findViewById(R.id.tv_couple_time);
        mTvPrice = findViewById(R.id.tv_price);
        mIvCountTime = findViewById(R.id.iv_couple_bag_time);
        mTvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
        mTvPrice.getPaint().setAntiAlias(true); //去掉锯齿
        mTvCouplePrice = findViewById(R.id.tv_couple_price);
        mTvWelcome = findViewById(R.id.tv_welcome);
        mIvLeft = findViewById(R.id.left_icon);
        mIvRight = findViewById(R.id.right_icon);
        mRvLove = findViewById(R.id.rv_love);
        findViewById(R.id.tv_not_buy).setOnClickListener(this);
        mLLBuy = findViewById(R.id.ll_couple_buy);
        mLLBuy.setOnClickListener(this);
    }

    public void setData(int familyId, int id, CoupleBagDetailDto data) {
        mFamilyId = familyId;
        mId = id;
        mData = data;
        mTvWelcome.setText(data.welcome);
        mTvPrice.setText("原价" + data.price + "金币");
        mTvCouplePrice.setText(data.discount_price + "金币");
        if (TextUtils.equals(data.price, data.discount_price)) {
            mTvPrice.setVisibility(View.GONE);
            mIvCountTime.setVisibility(View.GONE);
        } else {
            mTvPrice.setVisibility(View.VISIBLE);
            mIvCountTime.setVisibility(View.VISIBLE);
        }
        GlideUtils.loadCircleImage(mContext, mIvLeft, data.user_icon1);
        GlideUtils.loadCircleImage(mContext, mIvRight, data.user_icon2);
        if (countBackUtils == null)
            countBackUtils = new CountBackUtils();
        countBackUtils.countBack(data.start_time, new CountBackUtils.Callback() {
            @Override
            public void countBacking(long time) {
                mTvCoupleTime.setText("超值礼包将在" + Utils.getLastTime(time) + "后失效");
            }

            @Override
            public void finish() {
                mTvCoupleTime.setText("情侣礼包优惠已失效");
                mTvPrice.setText(View.GONE);
                mIvCountTime.setVisibility(View.GONE);
            }
        });
        mRvLove.setLayoutManager(new GridLayoutManager(mContext, 3));
        CoupleBagGiftAdapter adapter = new CoupleBagGiftAdapter();
        adapter.setList(data.gift);
        mRvLove.setAdapter(adapter);
        AnimationUtil.createAvatarAnimation(mLLBuy);

    }

    /**
     * 购买情侣礼包
     */
    public void buyGiftBag(int familyId, int bagId) {
        mCompositeDisposable.add(chatApiService.buyGiftBag(familyId, bagId).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (response.getData() != null && response.getData())
                    dismiss();
            }
        }));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_couple_gift_bag_detail);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_not_buy) {  //暂不购买
            dismiss();
        } else if (id == R.id.ll_couple_buy) {   //购买
            buyGiftBag(mFamilyId, mId);
        }
    }
}
