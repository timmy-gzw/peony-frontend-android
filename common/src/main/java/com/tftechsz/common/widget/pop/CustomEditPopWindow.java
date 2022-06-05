package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tftechsz.common.R;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;

/**
 *  公共弹窗
 */
public class CustomEditPopWindow extends BaseCenterPop implements View.OnClickListener {
    private int style;
    private FrameLayout mFlTile;
    private TextView mTvTitle;
    private EditText mEtContent;
    private TextView mTvSure, mTvSure1;
    private TextView mTvCancel, mTvCancel1;
    private View mView;
    private TextView mTvSingle;
    private LinearLayout mLlBot0, mLlBot1;

    public CustomEditPopWindow(Context context) {
        this(context, 0);
    }

    public CustomEditPopWindow(Context context, int style) {
        super(context);
        mContext = context;
        this.style = style;
        initUI();
        setOutSideTouchable(false);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_edit_custom);
    }

    private void initUI() {
        mFlTile = findViewById(R.id.fl_title);
        mTvTitle = findViewById(R.id.tv_title);
        mEtContent = findViewById(R.id.et_content);
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

    public CustomEditPopWindow setSingleButtong() {
        return setSingleButtong("我知道了");
    }

    public CustomEditPopWindow setSingleButtong(String singleButtong) {
        return setSingleButtong(singleButtong, Utils.getColor(R.color.colorPrimary));
    }

    public CustomEditPopWindow setSingleButtong(String singleButtong, int color) {
        mLlBot0.setVisibility(View.GONE);
        mTvSingle.setVisibility(View.VISIBLE);
        mTvSingle.setText(singleButtong);
        mTvSingle.setTextColor(color);
        return this;
    }

    public CustomEditPopWindow setContent(String content) {
        mEtContent.setText(content);
        return this;
    }

    public String getContent() {
        return mEtContent.getText().toString();
    }

    public CustomEditPopWindow setHintContent(String content) {
        mEtContent.setHint(content);
        return this;
    }

    public void setTitle(String content) {
        mTvTitle.setText(content);
    }

    public void setEtLength(int length){
        mEtContent.setFilters(new InputFilter[]{new CustomFilter(length)});
    }


    public void setTitleGone() {
        mFlTile.setVisibility(View.GONE);
    }

    public CustomEditPopWindow setLeftButton(String leftText) {
        if (style == 0) {
            mTvCancel.setText(leftText);
        } else {
            mTvCancel1.setText(leftText);
        }
        return this;
    }

    public CustomEditPopWindow setRightButton(String rightText) {
        if (style == 0) {
            mTvSure.setText(rightText);
        } else {
            mTvSure1.setText(rightText);
        }
        return this;
    }

    public void setRightGone() {
        mTvCancel.setVisibility(View.GONE);
        mTvCancel1.setVisibility(View.GONE);
        mView.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sure || id == R.id.tv_sure1) {
            if (listener != null)
                listener.onSure();
            dismiss();
        } else if (id == R.id.tv_cancel || id == R.id.tv_cancel1) {
            if (listener != null)
                listener.onCancel();
            dismiss();
        } else if (id == R.id.tv_single) {
            dismiss();
        }
    }


    public interface OnSelectListener {

        void onCancel();

        void onSure();

    }

    public OnSelectListener listener;

    public CustomEditPopWindow addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }
}
