package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.mine.R;

public class DelPicPopWindow extends BaseBottomPop implements View.OnClickListener {

    OnClickListener l;

    public void setOnClickListener(OnClickListener l) {
        this.l = l;
    }

    public interface OnClickListener {

        void onDeleteClick();

    }


    public DelPicPopWindow(Context context) {
        super(context);
        TextView mTvCancel = findViewById(R.id.tv_cancel);
        TextView mTvDelete = findViewById(R.id.tv_delete);
        mTvDelete.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.popwindow_del_pic);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_cancel) {
            dismiss();
        } else if (id == R.id.tv_delete) {
            if (null != l) {
                l.onDeleteClick();
                dismiss();
            }
        }
    }
}
