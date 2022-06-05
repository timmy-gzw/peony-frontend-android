package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.LuckyResultAdapter;
import com.tftechsz.party.adapter.LuckyStoneResultAdapter;
import com.tftechsz.party.entity.TurntableStartBeforeBean;
import com.tftechsz.party.entity.WheelResultBean;

import java.util.List;

import static com.tftechsz.common.Constants.NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN;
import static com.tftechsz.party.mvp.ui.activity.PartyRoomActivity.mNumber;

/**
 * 幸运转盘 -开奖结果
 */
public class LuckyResultPopWindow extends BaseCenterPop implements View.OnClickListener {

    private RecyclerView recyclerView;
    // 'status' 1-正常 0-异常
    private int mType;
    private ImageView imgKnow, mImgResultTitle, mImgClose;
    private List<WheelResultBean.RewardBean> mList;
    private LuckyResultAdapter adapter;
    private TextView mTvPartyHint;
    private ConstraintLayout constraintLayoutRoot;
    private boolean mFlagLuckyStone;//幸运石弹窗
    private CheckBox checkBox;
    private int positionType;//回调之后重新开始转盘

    public LuckyResultPopWindow(Context context, List<WheelResultBean.RewardBean> list, boolean mFlagLuckyStone) {
        super(context);
        mContext = context;
        this.mFlagLuckyStone = mFlagLuckyStone;
        this.mList = list;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_party_lucky_result);
    }

    public void setAdapter(List<WheelResultBean.RewardBean> list, int type) {
        this.mType = type;
        mFlagLuckyStone = false;
        initUI(list);
    }

    public void setAdapterTurnStart(List<TurntableStartBeforeBean.BuyBean> list, ILResultPopCallBack ilResultPopCallBack, int type, int positionType) {
        mFlagLuckyStone = true;
        this.ilResultPopCallBack = ilResultPopCallBack;
        this.mType = type;
        this.positionType = positionType;
        initStartUI(list);
        checkBox.setChecked(true);
    }

    private void initStartUI(List<TurntableStartBeforeBean.BuyBean> list) {
        setPublicUI();
        findViewById(R.id.tv_title_hint).setVisibility(View.INVISIBLE);
        mTvPartyHint.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        checkBox = findViewById(R.id.tv_title_lucky_stone);
        checkBox.setText(mType == 1 ? "当幸运石不足时，自动为您购买聊天气泡，并赠送初级幸运石" : "当幸运石不足时，自动为您购买头像框，并赠送高级幸运石");
        checkBox.setVisibility(View.VISIBLE);
        findViewById(R.id.img_know_click_not).setVisibility(View.GONE);
        imgKnow.setImageResource(R.drawable.party_know_btn_ok);
        imgKnow.setVisibility(View.VISIBLE);
        LuckyStoneResultAdapter adapter = new LuckyStoneResultAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, list != null && list.size() > 1 ? 2 : 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(adapter);
        if (list != null) {
            int size = list.size();

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
            if (size > 4) {
                layoutParams.height = DensityUtils.dp2px(mContext, 300);
            } else {
                layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            }
            if (size < 3) {
                layoutParams.topMargin = DensityUtils.dp2px(mContext, 20);
                layoutParams.bottomMargin = DensityUtils.dp2px(mContext, 20);
            } else {
                layoutParams.topMargin = DensityUtils.dp2px(mContext, 5);
            }
            recyclerView.setLayoutParams(layoutParams);

            adapter.setList(list);

        }
    }

    /**
     * 公共ui
     */
    private void setPublicUI() {
        setOutSideDismiss(false);
        constraintLayoutRoot = findViewById(R.id.con_root);
        mImgResultTitle = findViewById(R.id.img_align_1);
        mImgClose = findViewById(R.id.img_close);
        mImgResultTitle.setImageResource(mFlagLuckyStone ? (mType == 1 ? R.drawable.party_gxhd_resultpop_bg_lucky_stone : R.drawable.party_gxhd_resultpop_bg_lucky_stone_gj) : R.drawable.party_gxhd_resultpop_bg);
        mImgClose.setImageResource(R.drawable.party_result_closeimg);
        constraintLayoutRoot.setBackgroundResource(R.drawable.party_bg_results);
        mTvPartyHint = findViewById(R.id.tv_title_partylucky);
        imgKnow = findViewById(R.id.img_know_click);
        recyclerView = findViewById(R.id.rel_party_lucky_result_recyclerview);
        imgKnow.setOnClickListener(this);
        findViewById(R.id.img_know_click_not).setOnClickListener(this);
        findViewById(R.id.img_close).setOnClickListener(this);
        mTvPartyHint.setVisibility(View.VISIBLE);
    }

    private void initUI(List<WheelResultBean.RewardBean> list) {
        setPublicUI();

        if (mType == 0) {
            mImgResultTitle.setImageResource(R.drawable.party_result_hint_title);
            recyclerView.setVisibility(View.GONE);
            mTvPartyHint.setVisibility(View.INVISIBLE);
            findViewById(R.id.tv_title_hint).setVisibility(View.VISIBLE);
            findViewById(R.id.img_know_click_not).setVisibility(View.VISIBLE);
            ImageView img = findViewById(R.id.img_know_click_not);
            img.setImageResource(R.drawable.party_know_btn);
            imgKnow.setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_title_hint).setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.img_know_click_not).setVisibility(View.GONE);
            imgKnow.setVisibility(View.VISIBLE);
            setBtnResultWindow();
        }
        this.mList = list;
        if (mList != null) {
            int size = mList.size();
            int itemHorizontalCount = Math.min(size, 3);
            adapter = new LuckyResultAdapter(itemHorizontalCount);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, itemHorizontalCount);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(gridLayoutManager);

            if (mType != 0) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
                if (size > 9) {
                    layoutParams.height = DensityUtils.dp2px(mContext, 300);
                } else {
                    layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                }
                if (size < 4) {
                    layoutParams.topMargin = DensityUtils.dp2px(mContext, 20);
                    layoutParams.bottomMargin = DensityUtils.dp2px(mContext, 20);
                } else {
                    layoutParams.topMargin = DensityUtils.dp2px(mContext, 5);
                }
                recyclerView.setLayoutParams(layoutParams);
            }


            adapter.setList(mList);

        }
        if (adapter != null && adapter.getData() != null && adapter.getData().size() > 0) {
            int size = adapter.getData().size();
            if (size > 0) {
                int heightHint = 0;
                int heightBtn = 0;
                if (size < 4) {
                    heightHint = DensityUtils.dp2px(mContext, 17);
                    heightBtn = DensityUtils.dp2px(mContext, 13);
                } else {
                    heightHint = DensityUtils.dp2px(mContext, 13);
                    heightBtn = DensityUtils.dp2px(mContext, 10);
                }
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mTvPartyHint.getLayoutParams();
                layoutParams.topMargin = DensityUtils.dp2px(mContext, heightHint);
                mTvPartyHint.setLayoutParams(layoutParams);

                ConstraintLayout.LayoutParams layoutParamsImg = (ConstraintLayout.LayoutParams) imgKnow.getLayoutParams();
                layoutParamsImg.topMargin = DensityUtils.dp2px(mContext, heightBtn);
                imgKnow.setLayoutParams(layoutParamsImg);
            }
        }

    }

    public void setAdapterLuckyStone(List<WheelResultBean.RewardBean> list) {

        initUI(list);

    }

    private void setBtnResultWindow() {

        if (mNumber == 10) {
            imgKnow.setImageResource(R.drawable.party_result_lucky_10);
        } else if (mNumber == 100) {
            imgKnow.setImageResource(R.drawable.party_result_lucky_100);
        } else {
            imgKnow.setImageResource(R.drawable.party_result_lucky_1);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_know_click) {
            if (mFlagLuckyStone) {
                //确定购买头像框
                if (ilResultPopCallBack != null) {
                    ilResultPopCallBack.ok(checkBox.isChecked(), positionType);
                }
                dismiss();
                return;
            }
            if (mType == 0) {
                dismiss();
                return;
            }
            RxBus.getDefault().post(new CommonEvent(NOTIFY_ENTER_PARTY_WHEEL_RESULT_ALIGN, String.valueOf(mNumber)));
            dismiss();

        } else if (id == R.id.img_close) {
            dismiss();
            if (mFlagLuckyStone) {
                if (ilResultPopCallBack != null) {
                    ilResultPopCallBack.onCallBack();
                }
            }
        } else if (id == R.id.img_know_click_not) {
            dismiss();
        }

    }

    ILResultPopCallBack ilResultPopCallBack;

    interface ILResultPopCallBack {
        void ok(boolean isCheck, int type);

        void onCallBack();
    }


}
