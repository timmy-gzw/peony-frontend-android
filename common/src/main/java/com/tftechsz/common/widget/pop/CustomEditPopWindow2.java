package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.KeyboardUtils;
import com.tftechsz.common.R;
import com.tftechsz.common.utils.Keyboard1Util;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.CustomFilter;

/**
 * 支持长按复制粘贴
 */
public class CustomEditPopWindow2 extends DialogFragment implements View.OnClickListener {

    private int style;
    private FrameLayout mFlTile;
    private TextView mTvTitle;
    private EditText mEtContent;
    private TextView mTvSure, mTvSure1;
    private TextView mTvCancel, mTvCancel1;
    private View mView;
    private TextView mTvSingle;
    private LinearLayout mLlBot0, mLlBot1;
    private boolean sureDismiss = true;
    private Context context;


    public CustomEditPopWindow2(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这句代码的意思是让dialogFragment弹出时沾满全屏
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.edit_theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_edit_custom2, null);
        //让DialogFragment的背景为透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = getDialog().getWindow();
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        initView(view);
//        initEvent();
        return view;
    }

    private void initView(View view) {
        mFlTile = view.findViewById(R.id.fl_title);
        mTvTitle = view.findViewById(R.id.tv_title);
        mEtContent = view.findViewById(R.id.et_content);
        mLlBot0 = view.findViewById(R.id.ll_bot_0);
        mLlBot1 = view.findViewById(R.id.ll_bot_1);
        mTvSure = view.findViewById(R.id.tv_sure);
        mTvSure1 = view.findViewById(R.id.tv_sure1);
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvCancel1 = view.findViewById(R.id.tv_cancel1);
        mTvSingle = view.findViewById(R.id.tv_single);
        mView = view.findViewById(R.id.view1);
        mTvSingle.setVisibility(View.GONE);
        mTvSure.setOnClickListener(this);
        mTvSure1.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvCancel1.setOnClickListener(this);
        mTvSingle.setOnClickListener(this);

        if (style == 1) {
            mLlBot0.setVisibility(View.GONE);
            view.findViewById(R.id.view).setVisibility(View.GONE);
            mLlBot1.setVisibility(View.VISIBLE);
        }
    }

    public CustomEditPopWindow2 setSingleButtong() {
        return setSingleButtong("我知道了");
    }

    public CustomEditPopWindow2 setSingleButtong(String singleButtong) {
        return setSingleButtong(singleButtong, Utils.getColor(R.color.colorPrimary));
    }

    public CustomEditPopWindow2 setSingleButtong(String singleButtong, int color) {
        mLlBot0.setVisibility(View.GONE);
        mTvSingle.setVisibility(View.VISIBLE);
        mTvSingle.setText(singleButtong);
        mTvSingle.setTextColor(color);
        return this;
    }

    public CustomEditPopWindow2 setContent(String content) {
        mEtContent.setText(content);
        return this;
    }

    public String getContent() {
        return mEtContent.getText().toString();
    }

    public CustomEditPopWindow2 setHintContent(String content) {
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

    public CustomEditPopWindow2 setLeftButton(String leftText) {
        if (style == 0) {
            mTvCancel.setText(leftText);
        } else {
            mTvCancel1.setText(leftText);
        }
        return this;
    }

    public CustomEditPopWindow2 setRightButton(String rightText) {
        if (style == 0) {
            mTvSure.setText(rightText);
        } else {
            mTvSure1.setText(rightText);
        }
        return this;
    }
    public CustomEditPopWindow2 setSureDismiss(boolean dismiss){
        sureDismiss = dismiss;
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
            if(sureDismiss)
                dismiss();
        } else if (id == R.id.tv_cancel || id == R.id.tv_cancel1) {
            if (listener != null)
                listener.onCancel();
            dismiss();
        } else if (id == R.id.tv_single) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        Keyboard1Util.hideSoftInput(this.context,mEtContent);
        super.dismiss();
    }

    public interface OnSelectListener {

        void onCancel();

        void onSure();

    }

    public OnSelectListener listener;

    public CustomEditPopWindow2 addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
        return this;
    }
}
