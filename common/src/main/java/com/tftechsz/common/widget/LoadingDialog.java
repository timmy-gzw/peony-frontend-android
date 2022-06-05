package com.tftechsz.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tftechsz.common.R;


/**
 * 加载进度提示框
 */
public class LoadingDialog extends Dialog {
    private Context mContext;
    private TextView tvPercent;

    public LoadingDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(v, params);// 设置布局 );
        tvPercent = findViewById(R.id.tv_progress);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.parseColor("#00000000"));
        getWindow().setBackgroundDrawable(colorDrawable);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0f;
        getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        if (mContext != null)
            super.show();
    }

    @Override
    public void dismiss() {
        if (mContext != null)
            super.dismiss();
    }

    public void destory() {
        mContext = null;
    }

    public void setProgressPercent(String percent) {
        if (null != tvPercent)
            tvPercent.setText(percent);
    }
}
