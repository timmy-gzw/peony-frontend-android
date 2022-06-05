package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;

/**
 * 离开麦位弹窗
 */
public class LeaveOnSeatPopWindow extends BaseCenterPop implements View.OnClickListener {
    private String msg;
    TextView mTvContext;

    public LeaveOnSeatPopWindow(Context context, String msg) {
        super(context);
        this.msg = msg;
        findViewById(R.id.tv_sure).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        mTvContext = findViewById(R.id.tv_leave_on_text);
        if (!TextUtils.isEmpty(msg)) {
            mTvContext.setText(msg);
        }
    }

    public void setTextMsg(String msg){
        this.msg = msg;
        mTvContext.setText(msg);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_leave_on_seat);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_sure) {  //离开麦位
            dismiss();
            Utils.runOnUiThread(() -> {
                if (listener != null)
                    listener.leaveOnSeat();
            });
        } else if (id == R.id.tv_cancel) {  //取消
            dismiss();
        }
    }


    public interface OnSelectListener {
        void leaveOnSeat();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
