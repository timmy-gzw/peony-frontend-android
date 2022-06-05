package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.Utils;

/**
 *  公共弹窗
 */
public class CustomPopWindow extends BaseCenterPop implements View.OnClickListener {
    private final int style;
    private FrameLayout mFlTile;
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSure, mTvSure1;
    private TextView mTvCancel, mTvCancel1;
    private View mView;
    private TextView mTvSingle;
    private LinearLayout mLlBot0, mLlBot1;
    private boolean mIsDismiss = true;

    public CustomPopWindow(Context context) {
        this(context, 0);
    }

    public CustomPopWindow(Context context, int style) {
        super(context);
        mContext = context;
        this.style = style;
        initUI();
        setOutSideTouchable(false);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_custom);
    }

    private void initUI() {
        mFlTile = findViewById(R.id.fl_title);
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        mLlBot0 = findViewById(R.id.ll_bot_0);
        mLlBot1 = findViewById(R.id.ll_bot_1);
        mTvSure = findViewById(R.id.tv_sure);
        mTvSure1 = findViewById(R.id.tv_sure1);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvCancel1 = findViewById(R.id.tv_cancel1);
        mTvSingle = findViewById(R.id.tv_single);
        mView = findViewById(R.id.view1);
        mTvSingle.setVisibility(View.GONE);
        mTvSure.setOnClickListener(this);
        mTvSure1.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvCancel1.setOnClickListener(this);
        mTvSingle.setOnClickListener(this);

        if (style == 1) {
            mLlBot0.setVisibility(View.GONE);
            findViewById(R.id.view).setVisibility(View.GONE);
            mLlBot1.setVisibility(View.VISIBLE);
        }
    }



    public CustomPopWindow setSingleButtong() {
        return setSingleButtong("我知道了");
    }

    public CustomPopWindow setSingleButtong(String singleButtong) {
        return setSingleButtong(singleButtong, Utils.getColor(R.color.colorPrimary));
    }

    public CustomPopWindow setSingleButtong(String singleButtong, int color) {
        mLlBot0.setVisibility(View.GONE);
        mTvSingle.setVisibility(View.VISIBLE);
        mTvSingle.setText(singleButtong);
        mTvSingle.setTextColor(color);
        return this;
    }


    public CustomPopWindow setContentText(CharSequence content) {
        mTvContent.setText(content);
        return this;
    }

    public CustomPopWindow setContent(CharSequence content) {
        setContent(content, Gravity.CENTER);
        return this;
    }

    public CustomPopWindow setContent(CharSequence content, int gravity) {
        if (!TextUtils.isEmpty(content)) {
            String trim = content.toString().trim();
            if (trim.startsWith("<") && trim.endsWith(">")) {
                mTvContent.setText(Html.fromHtml(content.toString()));
            } else {
                mTvContent.setText(content);
            }
            mTvContent.setGravity(gravity);
        }
        return this;
    }

    public CustomPopWindow setIsDismiss(boolean isDismiss) {
        mIsDismiss = isDismiss;
        return this;
    }

    public CustomPopWindow setTitle(String content) {
        mTvTitle.setText(content);
        return this;
    }

    public void setTitleGone() {
        mFlTile.setVisibility(View.GONE);
    }

    public CustomPopWindow setLeftButton(String leftText) {
        if (style == 0) {
            mTvCancel.setText(leftText);
        } else {
            mTvCancel1.setVisibility(View.VISIBLE);
            mTvCancel1.setText(leftText);
        }
        return this;
    }

    public CustomPopWindow setSingleButton() {
        mTvCancel1.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.width = DensityUtils.dp2px(BaseApplication.getInstance(), 135);
        mTvSure1.setLayoutParams(params);
        return this;
    }

    public CustomPopWindow setIsCancel(boolean isCancel) {
        this.setOutSideDismiss(isCancel);
        return this;
    }

    public CustomPopWindow setRightButton(String rightText) {
        if (style == 0) {
            mTvSure.setText(rightText);
        } else {
            mTvSure1.setText(rightText);
        }
        return this;
    }

    public CustomPopWindow setRightColor(int color) {
        if (style == 0) {
            mTvSure.setTextColor(Utils.getColor(color));
        } else {
            mTvSure1.setTextColor(Utils.getColor(color));
        }
        return this;
    }


    public CustomPopWindow setLeftColor(int color) {
        if (style == 0) {
            mTvCancel.setTextColor(Utils.getColor(color));
        } else {
            mTvCancel1.setTextColor(Utils.getColor(color));
        }
        return this;
    }

    public CustomPopWindow setRightGone() {
        mTvCancel.setVisibility(View.GONE);
        mTvCancel1.setVisibility(View.GONE);
        mView.setVisibility(View.GONE);
        return this;
    }


    @Override
    public void onClick(View v) {
        if (!ClickUtil.canOperate()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_sure || id == R.id.tv_sure1) {
            if (listener != null)
                listener.onSure();
            if (mIsDismiss)
                dismiss();
        } else if (id == R.id.tv_cancel || id == R.id.tv_cancel1) {
            if (listener != null)
                listener.onCancel();
            dismiss();
        } else if (id == R.id.tv_single) {
            if (listener != null)
                listener.onSure();
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

    public CustomPopWindow addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }
}
