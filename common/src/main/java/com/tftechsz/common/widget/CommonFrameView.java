package com.tftechsz.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tftechsz.common.R;

public class CommonFrameView extends RelativeLayout {

    private TextView mTvContent;

    public CommonFrameView(Context context) {
        super(context);
    }

    public CommonFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_frame_view, this);
        mTvContent = findViewById(R.id.tv_content);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonFrameView);
        if (typedArray != null) {
            String leftContent = typedArray.getString(R.styleable.CommonFrameView_text_content);
            mTvContent.setText(leftContent);
            typedArray.recycle();
        }
    }

    public void setContentText(String text) {
        mTvContent.setText(text);
    }

}
