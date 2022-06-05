package com.tftechsz.party.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.entity.PartyPermission;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.party.R;

/**
 * 语音房-功能菜单
 */
public class FunctionPopAdapter extends BaseQuickAdapter<PartyPermission, BaseViewHolder> {
    /**
     * 0  管理权限
     * 1  功能
     */
    private int mTag;
    //是否静音
    private boolean mIsSilence;
    //是否爱意值    0 : 开  1：关
    private int mIsLoveValue;
    //开关特效
    private boolean mSpecialEffects;

    public FunctionPopAdapter(int tag, boolean mIsSilence, int isLoveValue, boolean specialEffects) {
        super(R.layout.adapter_function);
        this.mTag = tag;
        this.mIsSilence = mIsSilence;
        this.mIsLoveValue = isLoveValue;
        this.mSpecialEffects = specialEffects;
    }

    public void setTag(int position) {
        this.mTag = position;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, PartyPermission item) {
        if (mTag == 0) {
            GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), item.icon);
        } else {
            if (item.title.contains("静音")) {
                if (mIsSilence) {
                    GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), R.drawable.party_function_jingyin_gone);
                    helper.setText(R.id.tv_function_text, "已静音");
                    return;
                } else {
                    GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), item.icon);
                    helper.setText(R.id.tv_function_text, "静音");
                }

            } else if (item.id == 15) {
                if (mIsLoveValue == 1) {
                    GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), R.drawable.party_function_aiyizhi_gone);
                    helper.setText(R.id.tv_function_text, "爱意值(隐)");
                    return;
                } else {
                    GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), item.icon);
                }

            } else if (item.id == 25) {
//                if (mSpecialEffects) {
//                    GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), R.drawable.special_effects_icon);
//                } else {
//
//                }
                GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), item.icon);
            } else
                GlideUtils.loadImage(getContext(), helper.getView(R.id.img_gift_adapter), item.icon);
        }

        helper.setText(R.id.tv_function_text, item.title);
    }

    /**
     * 修改静音模式
     */
    public void updateSilence(boolean mFlagSilence) {
        this.mIsSilence = mFlagSilence;
        notifyDataSetChanged();
    }

    /**
     * 修改爱意值图片
     */
    public void updateLoveValue(int mIsLoveValue) {
        this.mIsLoveValue = mIsLoveValue;
        notifyDataSetChanged();
    }

    /**
     * 修改特效管理
     */
    public void updateSpecial(boolean mSpecialEffects) {
        this.mSpecialEffects = mSpecialEffects;
        notifyDataSetChanged();
    }
}
