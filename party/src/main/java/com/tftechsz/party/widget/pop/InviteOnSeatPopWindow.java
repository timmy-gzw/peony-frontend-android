package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.view.View;

import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;

/**
 * 邀请上麦弹窗
 */
public class InviteOnSeatPopWindow extends BaseCenterPop implements View.OnClickListener {
    public InviteOnSeatPopWindow(Context context) {
        super(context);
        findViewById(R.id.tv_accept).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_invite_on_seat);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_accept) {  //同意上麦
            if (listener != null)
                listener.agree();
            dismiss();
        } else if (id == R.id.tv_cancel) {  //拒绝上麦
            if (listener != null)
                listener.refuse();
            dismiss();
        }
    }

    public interface OnSelectListener {
        void agree();

        void refuse();

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
