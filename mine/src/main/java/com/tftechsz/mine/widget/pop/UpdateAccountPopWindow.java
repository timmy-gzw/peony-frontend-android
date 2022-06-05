package com.tftechsz.mine.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.mine.R;

/**
 * 更新账号
 */
public class UpdateAccountPopWindow extends BaseBottomPop implements View.OnClickListener {

    private Context mContext;

    public UpdateAccountPopWindow(Context context) {
        super(context);
        mContext = context;
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        findViewById(R.id.tv_black).setOnClickListener(this);
        findViewById(R.id.tv_report).setOnClickListener(this);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_update_account);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_black) {  //拉黑
            if (listener != null)
                listener.blackUser();
            dismiss();
        } else if (id == R.id.tv_report) {
            if (listener != null)
                listener.reportUser();
            dismiss();
        }
    }

    public interface OnSelectListener {
        void reportUser();

        void blackUser();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
