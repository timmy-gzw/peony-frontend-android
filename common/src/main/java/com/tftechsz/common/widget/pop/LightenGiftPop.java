package com.tftechsz.common.widget.pop;


import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tftechsz.common.R;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 1、礼物点亮弹窗dialog
 * 2、情侣甜蜜值减少
 */
public class LightenGiftPop extends BaseCenterPop {

    private TextView giftName, giftHint;
    /**
     * 1、礼物点亮弹窗dialog
     * 2、情侣甜蜜值减少
     */
    private int mType;
    private ImageView mImgCenter;
    private FamilyIdDto.CoupleGiftListInfo coupleGiftListInfo;

    public LightenGiftPop(Context context, int type) {
        super(context);
        this.mType = type;
        setOutSideDismiss(false);
        initUI();
    }

    public LightenGiftPop(Context context, int type, FamilyIdDto.CoupleGiftListInfo coupleGiftListInfo) {
        super(context);
        this.mType = type;
        this.coupleGiftListInfo = coupleGiftListInfo;
        setOutSideDismiss(false);
        initUI();
    }

    /**
     * 礼物点亮弹窗dialog
     */
    public void updateDate(FamilyIdDto.CoupleGiftListInfo coupleGiftListInfo) {
        this.coupleGiftListInfo = coupleGiftListInfo;
        giftName.setText(coupleGiftListInfo.gift_name);
        GlideUtils.loadRoundImage(mContext, mImgCenter, coupleGiftListInfo.gift_image);

        SpannableStringBuilder builder = new SpannableStringBuilder(coupleGiftListInfo.msg);
        int stat = coupleGiftListInfo.msg.indexOf(coupleGiftListInfo.desc);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), 0, coupleGiftListInfo.msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6A9D")), stat, stat + coupleGiftListInfo.desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        giftHint.setText(builder);
    }

    private void initUI() {
        mImgCenter = findViewById(R.id.img_gift);
        ImageView imgTitle = findViewById(R.id.img_alg_top);
        imgTitle.setImageResource(mType == 1 ? R.mipmap.chat_light_gift_text_top : R.mipmap.chat_couples_light_gift_text_top);
        giftName = findViewById(R.id.tv_alg_gift_name);
        giftHint = findViewById(R.id.tv_hint_text);
        findViewById(R.id.img_btn).setOnClickListener(v -> dismiss());

    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_lighten_gift);
    }


    /**
     * 情侣亲密度减少
     */
    public void setData(FamilyIdDto.CoupleSweetInfo data) {
        giftName.setText(data.numbers);
        mImgCenter.setImageResource(R.mipmap.light_then_gift_center);

        SpannableStringBuilder span = new SpannableStringBuilder();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(data.msg);
        span.append(stringBuffer);
        int start = stringBuffer.toString().indexOf(data.nickname);
        if (start >= 0) {
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6A9D")), start, start + data.nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#7F89F3")), start + data.nickname.length(), data.msg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        giftHint.setText(span);
//        if (!TextUtils.isEmpty(data.level_icon)) {
//            GlideUtils.loadRouteImage(mContext, mLevelIcon, data.level_icon);
//        }

    }
}
