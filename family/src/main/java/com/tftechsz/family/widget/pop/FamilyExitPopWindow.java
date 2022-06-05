package com.tftechsz.family.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.family.R;
import com.tftechsz.common.widget.pop.BaseBottomPop;


/**
 * 退出家族弹窗
 */
public class FamilyExitPopWindow extends BaseBottomPop implements View.OnClickListener {

    public FamilyExitPopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        findViewById(R.id.tv_exit).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_family_exit);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_exit) {
            if (null != listener)
                listener.exitFamily();
            dismiss();
        } else if (id == R.id.tv_cancel) {
            dismiss();
        }
    }


    public interface OnSelectListener {
        void exitFamily();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
