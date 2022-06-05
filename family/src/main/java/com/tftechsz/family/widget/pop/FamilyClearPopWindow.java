package com.tftechsz.family.widget.pop;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.tftechsz.family.R;
import com.tftechsz.common.utils.Utils;

import razerdp.basepopup.BasePopupWindow;

/**
 * 包 名 : com.tftechsz.family.widget.pop
 * 描 述 : TODO
 */
public class FamilyClearPopWindow extends BasePopupWindow implements View.OnClickListener {

    public FamilyClearPopWindow(Context context) {
        super(context);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {

        findViewById(R.id.tv_all_read).setOnClickListener(this);
        findViewById(R.id.tv_del).setOnClickListener(this);
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.pop_family_clear);
        return v;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, true);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_enter_anim);
    }


    @Override
    protected Animation onCreateDismissAnimation() {
        return Utils.getTopScaleAnimation(0.9f, 0.0f, false);
        //return AnimationUtils.loadAnimation(mContext, R.anim.pop_center_exit_anim);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_all_read) {  //标记全部已读
            if (listener != null)
                listener.click(0);
            dismiss();
        } else if (id == R.id.tv_del) {
            if (listener != null)
                listener.click(1);
            dismiss();
        }
    }

    public interface OnSelectListener {
        void click(int position);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
