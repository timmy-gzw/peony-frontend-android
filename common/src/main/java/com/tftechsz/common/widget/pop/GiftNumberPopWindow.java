package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.GiftNumberAdapter;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import razerdp.basepopup.BasePopupWindow;

public class GiftNumberPopWindow extends BasePopupWindow implements View.OnClickListener {

    private RecyclerView mRvNumber;
    private LinearLayout mLlNumPop;
    private final Context mContext;
    private final boolean isPackMode;
    private final OnSelectListener listener;
    private boolean isLuckyGift, isGiftPop;
    private TextView mTvNumber10;
    private TextView mTvNumber99;
    private TextView mTvNumber520;
    private TextView mTvNumber666;
    private TextView mTvNumber1314;

    /**
     * @param context    上下文
     * @param isGiftPop  是否是礼物弹窗面板
     * @param isPackMode 是否是背包模式, 一键全送
     */
    public GiftNumberPopWindow(Context context, OnSelectListener listener, boolean isGiftPop, boolean isPackMode, boolean isLuckyGift) {
        super(context);
        this.isGiftPop = isGiftPop;
        this.isPackMode = isPackMode;
        this.isLuckyGift = isLuckyGift;
        this.listener = listener;
        mContext = context;
        setPopupGravity(Gravity.BOTTOM | Gravity.END);
        setPopupFadeEnable(true);
        initUI();
        initData();
    }


    public void setLayoutParams() {
//        setBackground(R.color.transparent);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mLlNumPop.getLayoutParams();
        layoutParams.rightMargin = 0;
        layoutParams.bottomMargin = DensityUtils.dp2px(BaseApplication.getInstance(), isLuckyGift ? 100 : 245);
        setPopupGravity(Gravity.CENTER);
        mLlNumPop.setLayoutParams(layoutParams);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_gift_number);
    }


    private void initUI() {
        mLlNumPop = findViewById(R.id.ll_num_pop);
        mRvNumber = findViewById(R.id.rv_number);
        TextView tvNumber = findViewById(R.id.tv_number);
        TextView sendAll = findViewById(R.id.send_all);

        if (isLuckyGift) {
            tvNumber.setVisibility(View.GONE);
            sendAll.setVisibility(View.GONE);
        } else {
            tvNumber.setVisibility(!isPackMode ? View.VISIBLE : View.GONE);
            sendAll.setVisibility(isPackMode ? View.VISIBLE : View.GONE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        mRvNumber.setLayoutManager(gridLayoutManager);
        mTvNumber10 = findViewById(R.id.tv_number_10);
        mTvNumber99 = findViewById(R.id.tv_number_99);
        mTvNumber520 = findViewById(R.id.tv_number_520);
        mTvNumber666 = findViewById(R.id.tv_number_666);
        mTvNumber1314 = findViewById(R.id.tv_number_1314);
        mTvNumber10.setOnClickListener(this);
        mTvNumber99.setOnClickListener(this);
        mTvNumber520.setOnClickListener(this);
        mTvNumber666.setOnClickListener(this);
        mTvNumber1314.setOnClickListener(this);
        tvNumber.setOnClickListener(this);
        sendAll.setOnClickListener(this);

    }


    /**
     * 初始化加载数据
     */
    private void initData() {
        if (!isGiftPop && isLuckyGift) {
            mTvNumber10.setText(Interfaces.SEGMENT_DATA_LUCKY[0]);
            mTvNumber99.setText(Interfaces.SEGMENT_DATA_LUCKY[1]);
            mTvNumber520.setText(Interfaces.SEGMENT_DATA_LUCKY[2]);
            mTvNumber666.setText(Interfaces.SEGMENT_DATA_LUCKY[3]);
            mTvNumber1314.setText(Interfaces.SEGMENT_DATA_LUCKY[4]);
            mRvNumber.setVisibility(View.GONE);
            return;
        }
        List<Integer> list = new ArrayList<>();
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(1);
        list.add(2);
        list.add(3);
        GiftNumberAdapter adapter = new GiftNumberAdapter(list);
        mRvNumber.setAdapter(adapter);
        adapter.setOnItemClickListener((a, view, position) -> {
            listener.onCheckNumber(adapter.getData().get(position));
            dismiss();
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_number_10) {
            if (null != listener)
                listener.onCheckNumber(Utils.numberFormat(Utils.getText(mTvNumber10)));
        } else if (id == R.id.tv_number_99) {
            if (null != listener)
                listener.onCheckNumber(Utils.numberFormat(Utils.getText(mTvNumber99)));
        } else if (id == R.id.tv_number_520) {
            if (null != listener)
                listener.onCheckNumber(Utils.numberFormat(Utils.getText(mTvNumber520)));
        } else if (id == R.id.tv_number_666) {
            if (null != listener)
                listener.onCheckNumber(Utils.numberFormat(Utils.getText(mTvNumber666)));
        } else if (id == R.id.tv_number_1314) {
            if (null != listener)
                listener.onCheckNumber(Utils.numberFormat(Utils.getText(mTvNumber1314)));
        } else if (id == R.id.tv_number) {   //自定义数量
            GiftNumberChoosePopWindow popWindow = new GiftNumberChoosePopWindow(mContext);
            popWindow.showPopupWindow();
        } else if (id == R.id.send_all) {
            if (null != listener)
                listener.onSendAll();
        }
        dismiss();
    }

    public interface OnSelectListener {
        void onCheckNumber(int number);


        void onSendAll();
    }

}
