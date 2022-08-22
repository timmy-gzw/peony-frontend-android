package com.tftechsz.main.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.SystemAccostDto;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.main.R;
import com.tftechsz.main.adapter.AccostUserAdapter;

import razerdp.basepopup.BasePopupWindow;

/**
 * 一键搭讪
 */
public class AccostPopWindow extends BasePopupWindow implements View.OnClickListener {

    private SystemAccostDto mData;
    private AccostUserAdapter mAdapter;
    private final Context mContext;
    private final UserProviderService service;
    private LinearLayout llBtn;
    private TextView mTvAccost;
    private TextView tvTitle, tvContent;
    private RecyclerView mRvAccost;

    public AccostPopWindow(Context context, SystemAccostDto data) {
        super(context);
        this.mContext = context;
        this.mData = data;
        service = ARouter.getInstance().navigation(UserProviderService.class);
        setOutSideDismiss(false);
        initUI();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_accost);
    }


    private void initUI() {
        mRvAccost = findViewById(R.id.rv_accost);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        ((SimpleItemAnimator) mRvAccost.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvAccost.setLayoutManager(gridLayoutManager);
        findViewById(R.id.iv_close).setOnClickListener(this);   //关闭
        llBtn = findViewById(R.id.ll_btn);
        mTvAccost = findViewById(R.id.accost_btn);
        llBtn.setOnClickListener(this);  //一键搭讪
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);

    }

    public void setData(SystemAccostDto data) {
        mData = data;
        mAdapter = new AccostUserAdapter(mData.alert_accost_list);
        mRvAccost.setAdapter(mAdapter);
        tvTitle.setText(mData.title);
        tvContent.setText(mData.des);
        mTvAccost.setText(mData.button);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> ad, @NonNull View view, int position) {
                mAdapter.multipleChoose(position);
                boolean canClick = false;
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    if (mAdapter.getData().get(i).isSelected()) {
                        canClick = true;
                    }
                }
                if (canClick) {
                    llBtn.setBackgroundResource(R.drawable.bg_record_tip_radiu25);
                    mTvAccost.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    llBtn.setEnabled(true);
                } else {
                    llBtn.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
                    mTvAccost.setTextColor(ContextCompat.getColor(mContext, R.color.color_light_font));
                    llBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_btn) {   //一键搭讪
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                if (mAdapter.getData().get(i).isSelected()) {
                    ChatMsgUtil.sendAccostMessage(String.valueOf(service.getUserId()), String.valueOf(mAdapter.getData().get(i).getUser_id()), mData.gift_info.gift.id, mData.gift_info.gift.name, mData.gift_info.gift.image, mData.gift_info.gift.animation, mData.gift_info.msg, 8,mData.accost_from);
                }
            }
            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_ACCOST_SUCCESS, mData.gift_info.gift.animation));
            dismiss();
        } else if (id == R.id.iv_close) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_enter_anim);
    }
}
