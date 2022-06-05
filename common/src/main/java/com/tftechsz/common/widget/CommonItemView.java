package com.tftechsz.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tftechsz.common.R;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class CommonItemView extends RelativeLayout {

    private TextView mTvLeft;
    private ImageView mIvRight;
    private TextView mTvRight;
    private TextView mTvCenter;
    private SwitchCompat mMySwitch;
    private View mView;

    public CommonItemView(Context context) {
        super(context);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.general_item_view, this);
        mTvLeft = findViewById(R.id.tv_left);
        mIvRight = findViewById(R.id.iv_right);
        mTvRight = findViewById(R.id.tv_right);
        mMySwitch = findViewById(R.id.my_switch);
        mTvCenter = findViewById(R.id.tv_center);
        mView = findViewById(R.id.view);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonItemView);
        if (typedArray != null) {
            boolean rightTextVisible = typedArray.getBoolean(R.styleable.CommonItemView_right_text_visible, true);
            if (rightTextVisible) {
                mTvRight.setVisibility(VISIBLE);
            } else {
                mTvRight.setVisibility(GONE);
            }

            boolean centerTextVisible = typedArray.getBoolean(R.styleable.CommonItemView_center_text_visible, false);
            if (centerTextVisible) {
                mTvCenter.setVisibility(VISIBLE);
            } else {
                mTvCenter.setVisibility(GONE);
            }

            boolean rightArrowVisible = typedArray.getBoolean(R.styleable.CommonItemView_right_arrow_visible, true);
            if (rightArrowVisible) {
                mIvRight.setVisibility(VISIBLE);
            } else {
                mIvRight.setVisibility(GONE);
            }

            boolean rightSwitchVisible = typedArray.getBoolean(R.styleable.CommonItemView_right_switch_visible, false);

            if (rightSwitchVisible) {
                mMySwitch.setVisibility(VISIBLE);
            } else {
                mMySwitch.setVisibility(GONE);
            }
            boolean rightSwitchOpen = typedArray.getBoolean(R.styleable.CommonItemView_right_switch_open, true);
            if (rightSwitchOpen) {
                mMySwitch.setChecked(true);
            } else {
                mMySwitch.setChecked(false);
            }

            boolean viewVisible = typedArray.getBoolean(R.styleable.CommonItemView_view_visible, true);
            if (viewVisible) {
                mView.setVisibility(VISIBLE);
            } else {
                mView.setVisibility(GONE);
            }

            int leftTextColor = typedArray.getColor(R.styleable.CommonItemView_left_text_color, Color.BLACK);
            mTvLeft.setTextColor(leftTextColor);

            String leftContent = typedArray.getString(R.styleable.CommonItemView_left_text_content);
            mTvLeft.setText(leftContent);

            String rightContent = typedArray.getString(R.styleable.CommonItemView_right_text_content);
            mTvRight.setText(rightContent);

            String centerContent = typedArray.getString(R.styleable.CommonItemView_center_text_content);
            mTvCenter.setText(centerContent);
            typedArray.recycle();
        }
    }

    public void setTextAndValue(String text, String value) {
        mTvLeft.setText(text);
        mTvRight.setText(value);
    }

    public void setRightText(CharSequence value) {
        setRightText(value, 15);
    }

    public void setRightText(CharSequence value, int textSize) {
        mTvRight.setText(value);
        if (textSize != 0) {
            mTvRight.setTextSize(textSize);
        }
    }


    public void setRightTextColor(Context context, int color) {
        mTvRight.setTextColor(ContextCompat.getColor(context, color));
    }

    public void setLeftText(String text) {
        mTvLeft.setText(text);
    }


    public void setCenterText(String text) {
        mTvCenter.setText(text);
    }

    public TextView getTvLeft() {
        return mTvLeft;
    }

    public TextView getTvCenter() {
        return mTvCenter;
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    public SwitchCompat getMySwitch() {
        return mMySwitch;
    }

    public void setSwitchChecked(boolean checked) {
        mMySwitch.setChecked(checked);
    }
}
