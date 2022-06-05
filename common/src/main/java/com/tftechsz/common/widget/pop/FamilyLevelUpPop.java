package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.GlideUtils;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : TODO
 */
public class FamilyLevelUpPop extends BaseCenterPop {

    private ImageView mLevelIcon, ivRotate;
    private TextView mLevelText;
    private TextView mLevelTips;

    public FamilyLevelUpPop(Context context) {
        super(context);
        setOutSideDismiss(false);
        initUI();
    }

    private void initUI() {
        mLevelIcon = findViewById(R.id.level_icon);
        mLevelText = findViewById(R.id.level_text);
        mLevelTips = findViewById(R.id.level_tips);
        ivRotate = findViewById(R.id.iv_rotate);
        findViewById(R.id.del).setOnClickListener(v -> dismiss());

        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(5 * 1000);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        setOnPopupWindowShowListener(() -> ivRotate.setAnimation(rotate));

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                ivRotate.clearAnimation();
            }
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_level_up);
    }

    public void setData(ChatMsg.FamilyLevelUp data) {
        if (!TextUtils.isEmpty(data.level_icon)) {
            GlideUtils.loadRouteImage(mContext, mLevelIcon, data.level_icon);
        }
        mLevelText.setText(String.valueOf(data.level));
        mLevelTips.setText(data.level_tips);
        if (!TextUtils.isEmpty(data.level_tips_color) && data.level_tips_color.startsWith("#") && (data.level_tips_color.length() == 7 || data.level_tips_color.length() == 9)) {
            mLevelTips.setTextColor(Color.parseColor(data.level_tips_color));
        }
    }
}
