package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nim.uikit.common.ConfigInfo;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.Utils;

/**
 * 权限弹窗
 */
public class PermissionPopWindow extends BaseCenterPop implements View.OnClickListener {
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSure;
    private TextView mTvCancel;
    private boolean mIsDismiss = true;

    public PermissionPopWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
        setOutSideTouchable(false);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_permission);
    }

    private void initUI() {
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        mTvSure = findViewById(R.id.tv_sure);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSure.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }


    public PermissionPopWindow setContentText(CharSequence content) {
        mTvContent.setText(content);
        return this;
    }


    public PermissionPopWindow setTitle(String content) {
        mTvTitle.setText(content);
        return this;
    }


    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_sure ) {
            if (listener != null)
                listener.onSure();
            if (mIsDismiss)
                dismiss();
        } else if (id == R.id.tv_cancel) {
            if (listener != null)
                listener.onCancel();
            dismiss();
        }
    }


    public interface OnSelectListener {

        void onCancel();

        void onSure();

    }

    @Override
    public boolean onDispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return mIsDismiss;
        } else {
            return super.onDispatchKeyEvent(event);
        }
    }


    public OnSelectListener listener;

    public PermissionPopWindow addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }
}
